package org.ljmu.thesis.run;

import org.apache.commons.io.FileUtils;
import org.ljmu.thesis.commons.DateUtils;
import org.ljmu.thesis.commons.Utils;
import org.ljmu.thesis.helpers.*;
import org.ljmu.thesis.model.Developer;
import org.ljmu.thesis.model.PmdReport;
import org.ljmu.thesis.model.ProcessedPRRecord;
import org.ljmu.thesis.model.RawPRRecord;
import org.ljmu.thesis.model.gerrit.GetChangeDetailOutput;
import org.ljmu.thesis.model.gerrit.GetChangeRevisionCommitOutput;
import org.ljmu.thesis.model.results.Results;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.ljmu.thesis.helpers.GitHelper.*;

public class EndToEndRun {
    private static final Logger LOGGER = Logger.getLogger(EndToEndRun.class.getName());
    private static final List<String> EXCLUDE_REVIEWERS_LIST = Arrays.asList("CI Bot", "Eclipse Genie", "Gerrit Code Review @ Eclipse.org");
    private static final int PING_PONG_THRESHOLD = 3;
    private static final int SLEEPING_REVIEW_THRESHOLD = 2;
    private static final int LOC_CHANGED_THRESHOLD = 500;
    private static final int REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD = 50;
    private static final List<String> IGNORE_REMOVE_WORKTREE_PROCESS_OUTPUT = Arrays.asList(
            "error: failed to delete '[/|a-z|A-Z|.|_|0-9]+': Directory not empty",
            "rm: \\|{2}: No such file or directory\\nrm: true: No such file or directory(\\nrm: [after|before]+WorkTree_[0-9|a-z]+: No such file or directory)?"
    );
    private static final String IGNORE_CREATE_WORKTREE_PROCESS_OUTPUT = "Preparing worktree \\(detached HEAD [0-9|a-z]+\\)\\nUpdating files: 100% \\([0-9]+\\/[0-9]+\\), done.\\nHEAD is now at [0-9|a-z]+ First commited as [after|before]+[0-9|a-z|_]+";

    public static void main(String[] args) throws IOException {
        try {
            cleanUp();
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "15");
            FileHandler fh = new FileHandler(ConfigHelper.getOutputDirectoryPath() + "output_" + ConfigHelper.getProjectToRun() + ".log");
            LOGGER.addHandler(fh);
            long start = System.currentTimeMillis();
            populate();
            long end = System.currentTimeMillis();
            LOGGER.info(String.format("Time taken to finish: [%d] seconds", TimeUnit.MILLISECONDS.toSeconds(end - start)));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Parent catch block. Exception message below!!!\n" + e.getMessage());
        } finally {
            cleanUp();
        }
    }

    private static void populate() throws IOException {
        //  1. Get all RawPRRecords
        List<RawPRRecord> rawPRRecords = CsvHelper.getMergedRawPRRecords();

//        List<Integer> reviewNumbersToInclude = Arrays.asList(20675, 22990, 23989, 26345, 26565, 27919, 28037, 28051, 28500, 28502, 60414); // TODO: TEMP: Remove after debugging
//        rawPRRecords.removeIf(r -> !reviewNumbersToInclude.contains(r.getReviewNumber())); // TODO: TEMP: Remove after debugging

        List<ProcessedPRRecord> processedPRRecords = new ArrayList<>();
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, Integer>> ownerReviewersReviewCountMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, Integer> ownerPRCountMap = new ConcurrentHashMap<>();

        populateExistingFieldsInProcessedPRRecordsFromRawPRRecords(rawPRRecords, processedPRRecords);

        //  3. For each ProcessedPRRecord execute in parallel
        processedPRRecords = processedPRRecords.parallelStream().map(pr -> {
            ProcessedPRRecord prUpdated = null;
            try {
                // Clone the ProcessedPRRecord to enable writing
                prUpdated = (ProcessedPRRecord) pr.clone();

                //  iv. Hit Gerrit endpoints to get the updated files details
                List<String> updatedFilesList = getUpdatedFilesList(pr.getReviewNumber(), pr.getIterationCount()).parallelStream().filter(f -> f.contains(".java")).collect(Collectors.toList());
                prUpdated.setUpdatedFilesList(updatedFilesList);
                prUpdated.setAtLeastOneUpdatedJavaFile(!updatedFilesList.isEmpty());

                //  v. Hit Gerrit endpoints and fetch GetChangeDetailOutput and GetChangeRevisionCommitOutput
                GetChangeDetailOutput changeDetailOutput = JsonHelper.getObject(GerritApiHelper.getChangeDetail(pr.getReviewNumber()), GetChangeDetailOutput.class);
                GetChangeRevisionCommitOutput changeRevisionCommitOutput = JsonHelper.getObject(GerritApiHelper.getChangeRevisionCommit(pr.getReviewNumber()), GetChangeRevisionCommitOutput.class);

                //  vi. Populate the fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput

                //  vi.a. Init/Update ownerReviewersReviewCountMap
                long ownerAccountId = changeDetailOutput.getOwner().get_account_id();
                List<Long> filteredReviewersList = getFilteredReviewersList(changeDetailOutput.reviewers.REVIEWER, ownerAccountId);
                ConcurrentHashMap<Long, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.containsKey(ownerAccountId) ? ownerReviewersReviewCountMap.get(ownerAccountId) : new ConcurrentHashMap<>();
                filteredReviewersList.forEach(r -> {
                    int reviewerForThisAuthorReviewCount = reviewersReviewCountMap.containsKey(r) ? reviewersReviewCountMap.get(r) : 0;
                    reviewersReviewCountMap.put(r, reviewerForThisAuthorReviewCount + 1);
                });
                ownerReviewersReviewCountMap.put(ownerAccountId, reviewersReviewCountMap);

                //  vi.b. Init/Update authorReviewCountMap
                int ownerReviewCount = ownerPRCountMap.containsKey(ownerAccountId) ? ownerPRCountMap.get(ownerAccountId) : 0;
                ownerPRCountMap.put(ownerAccountId, ownerReviewCount + 1);

                //  vi.c. Populate other fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput
                prUpdated.setOwnerAccountId(ownerAccountId);
                prUpdated.setReviewersList(filteredReviewersList);
                prUpdated.setCreatedDate(DateUtils.getDate(changeDetailOutput.getCreated()));
                prUpdated.setMergedDate(DateUtils.getDate(changeDetailOutput.getSubmitted()));
                prUpdated.setLocChanged(changeDetailOutput.getInsertions() + changeDetailOutput.getDeletions());
                prUpdated.setSubject(changeRevisionCommitOutput.getSubject().trim());
                prUpdated.setMessage(changeRevisionCommitOutput.getMessage().trim());

                // Compute and populate the Code smells difference count
                // Integer codeSmellsDifferenceCount = getNumberOfNewCodeSmells(prUpdated);
                // prUpdated.setCodeSmellsDifferenceCount(codeSmellsDifferenceCount);
                // prUpdated.setCodeSmellsIncreased(codeSmellsDifferenceCount != null ? codeSmellsDifferenceCount > 0 : null);

                LOGGER.info(String.format("Gerrit calls done for review number: [%d]", pr.getReviewNumber()));

            } catch (Exception e) {
                if (pr != null) {
                    LOGGER.log(Level.SEVERE, String.format("Gerrit data fetch failed for review number: [%d]. Details: %s", pr.getReviewNumber(), e.getMessage()));
                }
                e.printStackTrace();
            }
            return prUpdated;
        }).collect(Collectors.toList());

        computeAndPopulateCRSmellsInProcessedPRRecords(processedPRRecords, ownerReviewersReviewCountMap, ownerPRCountMap);

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");

//        computeAndPopulateCodeSmellsInProcessedPRRecords(processedPRRecords);

        String results = JsonHelper.getJsonPrettyString(Results.computeResults(processedPRRecords));
        LOGGER.info(results);

        CsvHelper.writeOutputCsv(processedPRRecords);
    }

    private static void cleanUp() throws IOException {
        // Delete all worktree directories if they exist
        Files.walk(Paths.get(ConfigHelper.getGitReposProjectPath()), 1).filter(p -> Files.isDirectory(p) && p.toFile().exists() && p.getFileName().toString().startsWith("worktree_")).forEach(p -> FileUtils.deleteQuietly(p.toFile()));

        // git worktree prune
        GitHelper.pruneWorktree(ConfigHelper.getGitReposProjectPath());
    }

    private static void populateExistingFieldsInProcessedPRRecordsFromRawPRRecords(List<RawPRRecord> rawPRRecords, List<ProcessedPRRecord> processedPRRecords) {
        //  2. For each RawPRRecord execute sequentially
        for (int i = 0; i < rawPRRecords.size(); i++) {
            ProcessedPRRecord processedPRRecord = null;
            RawPRRecord firstRawPRRecordForPR = rawPRRecords.get(i);

            //  i. Create 1 OutputPRRecord
            processedPRRecord = new ProcessedPRRecord();

            //  ii. Populate the existing fields for ProcessedPRRecord from RawPRRecord
            processedPRRecord.setReviewNumber(firstRawPRRecordForPR.getReviewNumber());
            processedPRRecord.setChangeId(firstRawPRRecordForPR.getChangeId());
            processedPRRecord.setUrl(Utils.getPRUrlFromRevisionUrl(firstRawPRRecordForPR.getUrl()));
            processedPRRecord.setBeforeCommitId(firstRawPRRecordForPR.getBeforeCommitId());

            //  iii. Retrieve the last RawPRRecord based on the last revision for the current PR
            i = getLastRevisionIndex(i, processedPRRecord.getReviewNumber(), rawPRRecords);
            RawPRRecord lastRawPRRecordForPR = rawPRRecords.get(i);
            processedPRRecord.setIterationCount(lastRawPRRecordForPR.getRevisionNumber());
            processedPRRecord.setAfterCommitId(lastRawPRRecordForPR.getAfterCommitId());

            processedPRRecords.add(processedPRRecord);
        }
    }

    private static void computeAndPopulateCRSmellsInProcessedPRRecords(List<ProcessedPRRecord> processedPRRecords, ConcurrentHashMap<Long, ConcurrentHashMap<Long, Integer>> ownerReviewersReviewCountMap, ConcurrentHashMap<Long, Integer> ownerPRCountMap) {
        processedPRRecords.parallelStream().forEach(pr -> {
            //  vii. Derive the CRSmells derived fields and populate them for OutputPRRecord
            pr.setCrSmellLackOfCR(pr.getReviewersList().isEmpty());
            pr.setCrSmellPingPong(pr.getIterationCount() > PING_PONG_THRESHOLD);
            pr.setCrSmellSleepingReviews(pr.getMergedDate().compareTo(pr.getCreatedDate()) > SLEEPING_REVIEW_THRESHOLD);
            pr.setCrSmellMissingContext(isMissingContextCRSmell(pr.getSubject(), pr.getMessage()));
            pr.setCrSmellLargeChangesets(pr.getLocChanged() > LOC_CHANGED_THRESHOLD);

            //  Determining review_buddies_cr_smell
            int ownerPRCount = ownerPRCountMap.get(pr.getOwnerAccountId());
            if (ownerPRCount < REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD) {
                return;
            }
            ConcurrentHashMap<Long, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.get(pr.getOwnerAccountId());
            boolean reviewBuddiesCRSmell = pr.getReviewersList().stream().anyMatch(r -> reviewersReviewCountMap.get(r) > ownerPRCount / 2);
            pr.setCrSmellReviewBuddies(reviewBuddiesCRSmell);
        });
    }

    private static void computeAndPopulateCodeSmellsInProcessedPRRecords(List<ProcessedPRRecord> processedPRRecords) {
        processedPRRecords.parallelStream().forEach(pr -> {
            // Compute and populate the Code smells difference count
            Integer codeSmellsDifferenceCount = null;
            try {
                codeSmellsDifferenceCount = getNumberOfNewCodeSmells(pr);
            } catch (Exception e) {
                codeSmellsDifferenceCount = null;
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            pr.setCodeSmellsDifferenceCount(codeSmellsDifferenceCount);
            pr.setIsIncreased(codeSmellsDifferenceCount != null ? codeSmellsDifferenceCount > 0 : null);

            LOGGER.info(String.format("Update done for review number: [%d]", pr.getReviewNumber()));
        });
    }

    private static int getLastRevisionIndex(int index, int reviewNumber, List<RawPRRecord> rawPRRecords) {
        while (index < rawPRRecords.size() && reviewNumber == rawPRRecords.get(index).getReviewNumber()) {
            index++;
        }
        return index - 1;
    }

    private static List<String> getUpdatedFilesList(int reviewNumber, int commitNumber) throws IOException {
        String changeRevisionFilesApiOutput = GerritApiHelper.getChangeRevisionFiles(reviewNumber, commitNumber);
        List<String> updatedFiles = JsonHelper.getFieldNames(changeRevisionFilesApiOutput);
        updatedFiles.remove(0); // Remove the 'COMMIT_MSG' field
        return updatedFiles;
    }

    private static List<Long> getFilteredReviewersList(Developer[] reviewers, long ownerAccountId) {
        if (reviewers == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(reviewers)
                .filter(r -> !EXCLUDE_REVIEWERS_LIST.contains(r.getName()) && ownerAccountId != r.get_account_id())
                .map(r -> r.get_account_id()).collect(Collectors.toList());
    }

    private static boolean isMissingContextCRSmell(String subject, String message) {
        if (subject.trim().isEmpty() || message.trim().isEmpty()) {
            return true;
        }
        String filteredMessage = (message.split("[\n]*Change-Id")[0]).replaceAll("\\s*", "");
        return subject.replaceAll("\\s*", "").equals(filteredMessage);
    }

    private static Integer getNumberOfNewCodeSmells(ProcessedPRRecord prRecord) throws IOException {
        // return null if no java updated file
        if (!prRecord.hasAtLeastOneUpdatedJavaFile()) {
            return null;
        }

        // Create new worktree with afterCommitId
        String gitReposProjectPath = ConfigHelper.getGitReposProjectPath();
        String workTreeName = "worktree_" + prRecord.getReviewNumber();
        long startTime = System.currentTimeMillis(); // TODO: Remove
        String gitCreateWorktreeProcessOutput = GitHelper.createNewWorkTree(gitReposProjectPath, workTreeName, prRecord.getAfterCommitId());
        long endTime = System.currentTimeMillis(); // TODO: Remove
        // Log if issues
        if (!isGitWorktreeCreationSuccessful(false, prRecord.getReviewNumber(), prRecord.getIterationCount(), gitCreateWorktreeProcessOutput)) {
            GitHelper.removeWorkTree(gitReposProjectPath, workTreeName);
            throw new RuntimeException(String.format("Git worktree creation process failed for commitId: [%s]...!!!\ngitCreateWorktreeProcessOutput: %s", prRecord.getBeforeCommitId(), gitCreateWorktreeProcessOutput));
        }
        LOGGER.info(String.format("[%d] Create Worktree took: [%d] seconds", prRecord.getReviewNumber(), TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove

        // Computing code smells for afterCommitId
        startTime = System.currentTimeMillis(); // TODO: Remove
        String workTreeProjectPath = gitReposProjectPath + File.separator + workTreeName;
        int afterCommitCodeSmellsCount = getCodeSmellsCount(workTreeProjectPath, prRecord.getUpdatedFilesList());
        endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Computing code smells took: [%d] seconds", prRecord.getReviewNumber(), TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove

        // Git checkout beforeCommitId
        startTime = System.currentTimeMillis(); // TODO: Remove
        String gitCheckoutProcessOutput = GitHelper.checkout(workTreeProjectPath, prRecord.getBeforeCommitId());
        endTime = System.currentTimeMillis(); // TODO: Remove
        // Log if issues
        if (!isGitCheckoutSuccessful(gitCheckoutProcessOutput)) {
            GitHelper.removeWorkTree(gitReposProjectPath, workTreeName);
            throw new RuntimeException(String.format("Git checkout process failed for commitId: [%s]\ncheckoutProcessOutput...!!!: %s", prRecord.getBeforeCommitId(), gitCheckoutProcessOutput));
        }
        LOGGER.info(String.format("[%d] Git checkout took: [%d] seconds", prRecord.getReviewNumber(), TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove

        // Compute code smells for beforeCommitId
        startTime = System.currentTimeMillis(); // TODO: Remove
        int beforeCommitCodeSmellsCount = getCodeSmellsCount(workTreeProjectPath, prRecord.getUpdatedFilesList());
        endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Computing code smells took: [%d] seconds", prRecord.getReviewNumber(), TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove

        LOGGER.info(String.format("beforeReviewCodeSmellsCount: [%d], afterReviewCodeSmellsCount: [%d] for review number: [%d]", beforeCommitCodeSmellsCount, afterCommitCodeSmellsCount, prRecord.getReviewNumber()));

        // Logging remove worktree process output, if issues
        startTime = System.currentTimeMillis(); // TODO: Remove
        String removeWorkTreeProcessOutputs = GitHelper.removeWorkTree(gitReposProjectPath, workTreeName);
        endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Remove Worktree took: [%d] seconds", prRecord.getReviewNumber(), TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove
        if (!isGitRemoveWorktreeSuccessful(removeWorkTreeProcessOutputs, IGNORE_REMOVE_WORKTREE_PROCESS_OUTPUT)) {
            LOGGER.log(Level.SEVERE, String.format("Git remove worktree failed for commitId: [%s]\nremoveWorkTreeProcessOutputs...!!!: %s", prRecord.getBeforeCommitId(), removeWorkTreeProcessOutputs));
        }

        return afterCommitCodeSmellsCount - beforeCommitCodeSmellsCount;
    }

    private static int getCodeSmellsCount(String projectPath, List<String> updatedFileList) {
        return updatedFileList.parallelStream().map(fp -> {
            try {
                if (!fp.endsWith(".java")) {
                    return 0;
                }
                String outputJson = PmdHelper.startPmdCodeSmellProcessAndGetOutput(projectPath, fp);
                if (outputJson.contains("No such file")) {
                    return 0;
                }
                PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
                return Arrays.stream(report.files).map(file -> file.violations.length).reduce(0, (len1, len2) -> len1 + len2);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, String.format("Failure while running PMD for project path: [%s]. Details: %s", projectPath, e.getMessage()));
                e.printStackTrace();
                return 0;
            }
        }).reduce(0, (count1, count2) -> count1 + count2);
    }
}
