package org.ljmu.thesis.run;

import org.ljmu.thesis.commons.DateUtils;
import org.ljmu.thesis.commons.Utils;
import org.ljmu.thesis.helpers.ConfigHelper;
import org.ljmu.thesis.helpers.CsvHelper;
import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.helpers.codesmells.GitHelper;
import org.ljmu.thesis.helpers.codesmells.PmdHelper;
import org.ljmu.thesis.helpers.crsmells.GerritApiHelper;
import org.ljmu.thesis.model.ProcessedPRRecord;
import org.ljmu.thesis.model.codesmells.PmdReport;
import org.ljmu.thesis.model.crsmells.Developer;
import org.ljmu.thesis.model.crsmells.GetChangeDetailOutput;
import org.ljmu.thesis.model.crsmells.GetChangeRevisionCommitOutput;
import org.ljmu.thesis.model.crsmells.RawPRRecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EndToEndRun {
    private static final Logger LOGGER = Logger.getLogger(EndToEndRun.class.getName());
    private static final List<String> IGNORE_REVIEWERS_LIST = Arrays.asList("CI Bot", "Eclipse Genie", "Gerrit Code Review @ Eclipse.org");
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
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "40");
            FileHandler fh = new FileHandler(ConfigHelper.getOutputDirectoryPath() + "output_" + ConfigHelper.getProjectToRun() + ".log");
            LOGGER.addHandler(fh);
            long start = System.currentTimeMillis();
            populate();
            long end = System.currentTimeMillis();
            LOGGER.info(String.format("Time taken to finish: [%d] seconds", TimeUnit.MILLISECONDS.toSeconds(end - start)));
        } finally {
            cleanUp();
        }
    }

    private static void populate() throws IOException {
        //  1. Get all RawPRRecords
        List<RawPRRecord> rawPRRecords = CsvHelper.getMergedRawPRRecords().subList(0, 3);
        List<ProcessedPRRecord> processedPRRecords = new ArrayList<>();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ownerReviewersReviewCountMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Integer> ownerPRCountMap = new ConcurrentHashMap<>();

        populateExistingFieldsInProcessedPRRecordsFromRawPRRecords(rawPRRecords, processedPRRecords);

        //  3. For each ProcessedPRRecord execute in parallel
        processedPRRecords = processedPRRecords.parallelStream().map(pr -> {
            ProcessedPRRecord prUpdated = null;
            try {
                // Clone the ProcessedPRRecord to enable writing
                prUpdated = (ProcessedPRRecord) pr.clone();

                //  iv. Hit Gerrit endpoints to get the updated files details
                List<String> updatedFilesList = getUpdatedFilesList(pr.getReviewNumber(), pr.getIterationCount());
                prUpdated.setUpdatedFilesList(updatedFilesList);
                prUpdated.setAtLeastOneUpdatedJavaFile(updatedFilesList.parallelStream().anyMatch(f -> f.contains(".java")));

                //  v. Hit Gerrit endpoints and fetch GetChangeDetailOutput and GetChangeRevisionCommitOutput
                GetChangeDetailOutput changeDetailOutput = JsonHelper.getObject(GerritApiHelper.getChangeDetail(pr.getReviewNumber()), GetChangeDetailOutput.class);
                GetChangeRevisionCommitOutput changeRevisionCommitOutput = JsonHelper.getObject(GerritApiHelper.getChangeRevisionCommit(pr.getReviewNumber()), GetChangeRevisionCommitOutput.class);

                //  vi. Populate the fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput

                //  vi.a. Init/Update ownerReviewersReviewCountMap
                String owner = changeDetailOutput.getOwner().getName();
                List<String> filteredReviewersList = getFilteredReviewersList(changeDetailOutput.reviewers.REVIEWER, owner); // TODO: Update to private fields
                ConcurrentHashMap<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.containsKey(owner) ? ownerReviewersReviewCountMap.get(owner) : new ConcurrentHashMap<>();
                filteredReviewersList.forEach(r -> {
                    int reviewerForThisAuthorReviewCount = reviewersReviewCountMap.containsKey(r) ? reviewersReviewCountMap.get(r) : 0;
                    reviewersReviewCountMap.put(r, reviewerForThisAuthorReviewCount + 1);
                });
                ownerReviewersReviewCountMap.put(owner, reviewersReviewCountMap);

                //  vi.b. Init/Update authorReviewCountMap
                int ownerReviewCount = ownerPRCountMap.containsKey(owner) ? ownerPRCountMap.get(owner) : 0;
                ownerPRCountMap.put(owner, ownerReviewCount + 1);

                //  vi.c. Populate other fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput
                prUpdated.setOwner(owner);
                prUpdated.setReviewersList(filteredReviewersList);
                prUpdated.setCreatedDate(DateUtils.getDate(changeDetailOutput.getCreated()));
                prUpdated.setMergedDate(DateUtils.getDate(changeDetailOutput.getSubmitted()));
                prUpdated.setLocChanged(changeDetailOutput.getInsertions() + changeDetailOutput.getDeletions());
                prUpdated.setSubject(changeRevisionCommitOutput.getSubject().trim());
                prUpdated.setMessage(changeRevisionCommitOutput.getMessage().trim());

                // Compute and populate the Code smells difference count
                Integer codeSmellsDifferenceCount = getCodeSmellsDifferenceCount(prUpdated);
                prUpdated.setCodeSmellsDifferenceCount(codeSmellsDifferenceCount);
                prUpdated.setCodeSmellsIncreased(codeSmellsDifferenceCount != null ? codeSmellsDifferenceCount > 0 : null);

                LOGGER.info(String.format("Object updated after Gerrit calls and code smells computation for review number: [%d]", pr.getReviewNumber()));

            } catch (Exception e) {
                if (pr != null) {
                    LOGGER.log(Level.SEVERE, String.format("Failure occurred for review number: [%d]. Details below: ", pr.getReviewNumber()));
                }
                e.printStackTrace();
            }
            return prUpdated;
        }).collect(Collectors.toList());

        computeAndPopulateCRSmellsInProcessedPRRecords(processedPRRecords, ownerReviewersReviewCountMap, ownerPRCountMap);

//        computeAndPopulateCodeSmellsInProcessedPRRecords(processedPRRecords);


        int totalPRsWithAtLeastOneJavaUpdatedFile = 0;
        int totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmell = 0;
        int totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmellAndIncreasedCodeSmell = 0;
        int totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmell = 0;
        int totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmellAndIncreasedCodeSmell = 0;
        for (ProcessedPRRecord pr : processedPRRecords) {
            if (pr.hasAtLeastOneUpdatedJavaFile()) {
                totalPRsWithAtLeastOneJavaUpdatedFile++;
                if (pr.hasAtLeastOneCRSmell()) {
                    totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmell++;
                    if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                        totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmellAndIncreasedCodeSmell++;
                    }
                } else {
                    totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmell++;
                    if (Boolean.TRUE.equals(pr.getCodeSmellsIncreased())) {
                        totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmellAndIncreasedCodeSmell++;
                    }
                }
            }
        }

        // Project name:
        LOGGER.info(String.format("Project name: %s", ConfigHelper.getProjectToRun()));

        // Total PRs with Java file updates:
        LOGGER.info(String.format("Total PRs: %d", totalPRsWithAtLeastOneJavaUpdatedFile));

        // Total PRs with Java file updates having no CR Smell:
        LOGGER.info(String.format("Total PRs with no CR smell: %d", totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmell));

        // Total PRs with Java file updates having no CR Smell and Code Smell increased:
        LOGGER.info(String.format("Total PRs with no CR smell and increased code smells: %d", totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmellAndIncreasedCodeSmell));

        // % PRs with Java file updates having no CR Smell and Code Smell increased:
        LOGGER.info(String.format("Percentage of PRs with no CR smell and increased code smells: %.2f", totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmell == 0 ? 0.0f : (((float) totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmellAndIncreasedCodeSmell / totalPRsWithAtLeastOneJavaUpdatedFileAndNoCRSmell) * 100)));


        // Total PRs with Java file updates having at least 1 CR Smell:
        LOGGER.info(String.format("Total PRs with at least 1 CR smell: %d", totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmell));

        // Total PRs with Java file updates having at least 1 CR Smell and Code Smell increased:
        LOGGER.info(String.format("Total PRs with at least 1 CR smell and increased code smells: %d", totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmellAndIncreasedCodeSmell));

        // % PRs with Java file updates having at least 1 CR Smell and Code Smell increased:
        LOGGER.info(String.format("Percentage of PRs with at least 1 CR smell and increased code smells: %.2f", totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmell == 0 ? 0.0f : (((float) totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmellAndIncreasedCodeSmell / totalPRsWithAtLeastOneJavaUpdatedFileAndAtLeastOneCRSmell) * 100)));

        CsvHelper.writeOutputCsv(processedPRRecords);
    }

    private static void cleanUp() throws IOException {
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

    private static void computeAndPopulateCRSmellsInProcessedPRRecords(List<ProcessedPRRecord> processedPRRecords, ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> ownerReviewersReviewCountMap, ConcurrentHashMap<String, Integer> ownerPRCountMap) {
        processedPRRecords.parallelStream().forEach(pr -> {
            //  vii. Derive the CRSmells derived fields and populate them for OutputPRRecord
            pr.setCrSmellLackOfCR(pr.getReviewersList().isEmpty());
            pr.setCrSmellPingPong(pr.getIterationCount() > PING_PONG_THRESHOLD);
            pr.setCrSmellSleepingReviews(pr.getMergedDate().compareTo(pr.getCreatedDate()) > SLEEPING_REVIEW_THRESHOLD);
            pr.setCrSmellMissingContext(isMissingContextCRSmell(pr.getSubject(), pr.getMessage()));
            pr.setCrSmellLargeChangesets(pr.getLocChanged() > LOC_CHANGED_THRESHOLD);

            //  Deriving review_buddies_cr_smell
            int ownerPRCount = ownerPRCountMap.get(pr.getOwner());
            if (ownerPRCount < REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD) {
                return;
            }
            ConcurrentHashMap<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.get(pr.getOwner());
            boolean reviewBuddiesCRSmell = pr.getReviewersList().stream().anyMatch(r -> reviewersReviewCountMap.get(r) > ownerPRCount / 2);
            pr.setCrSmellReviewBuddies(reviewBuddiesCRSmell);
        });
    }

    private static void computeAndPopulateCodeSmellsInProcessedPRRecords(List<ProcessedPRRecord> processedPRRecords) {
        processedPRRecords.parallelStream().forEach(pr -> {
            // Compute and populate the Code smells difference count
            Integer codeSmellsDifferenceCount = null;
            try {
                codeSmellsDifferenceCount = getCodeSmellsDifferenceCount(pr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pr.setCodeSmellsDifferenceCount(codeSmellsDifferenceCount);
            pr.setCodeSmellsIncreased(codeSmellsDifferenceCount != null ? codeSmellsDifferenceCount > 0 : null);

            LOGGER.info(String.format("Object updated after Gerrit calls and code smells computation for review number: [%d]", pr.getReviewNumber()));
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

    private static List<String> getFilteredReviewersList(Developer[] reviewers, String ownerName) {
        if (reviewers == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(reviewers)
                .filter(r -> !IGNORE_REVIEWERS_LIST.contains(r.getName()) && !ownerName.equals(r.getName()))
                .map(r -> r.getName()).collect(Collectors.toList());
    }

    private static boolean isMissingContextCRSmell(String subject, String message) {
        if (subject.isEmpty() || message.isEmpty()) {
            return true;
        }
        String filteredMessage = message.split("[\n]*Change-Id")[0];
        return subject.equals(filteredMessage);
    }

    private static Integer getCodeSmellsDifferenceCount(ProcessedPRRecord prRecord) throws IOException {
        if (!prRecord.hasAtLeastOneUpdatedJavaFile()) {
            return null;
        }
        String afterCommitId = prRecord.getAfterCommitId();
        String beforeCommitId = prRecord.getBeforeCommitId();
        List<String> filePaths = prRecord.getUpdatedFilesList();
        int afterReviewCodeSmellsCount = getCodeSmellsCount(afterCommitId, "afterWorkTree_" + afterCommitId, filePaths, prRecord.getReviewNumber(), prRecord.getIterationCount());
        int beforeReviewCodeSmellsCount = getCodeSmellsCount(prRecord.getBeforeCommitId(), "beforeWorkTree_" + beforeCommitId, filePaths, prRecord.getReviewNumber(), prRecord.getIterationCount());

        LOGGER.info(String.format("beforeReviewCodeSmellsCount: [%d], afterReviewCodeSmellsCount: [%d] for review number: [%d]", beforeReviewCodeSmellsCount, afterReviewCodeSmellsCount, prRecord.getReviewNumber()));

        return afterReviewCodeSmellsCount - beforeReviewCodeSmellsCount;
    }

    private static int getCodeSmellsCount(String commitId, String workTreeName, List<String> filePaths, int reviewNumber, int iterationCount) throws IOException {
        String gitReposProjectPath = ConfigHelper.getGitReposProjectPath();
        long startTime = System.currentTimeMillis(); // TODO: Remove
        String createWorktreeProcessOutput = GitHelper.createNewWorkTree(gitReposProjectPath, workTreeName, commitId);
        long endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Create Worktree took: [%d] seconds", reviewNumber, TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove


        // Logging create worktree process output, if issues
        if (!isGitWorktreeCreationSuccessful(workTreeName.contains("before") ? true : false, reviewNumber, iterationCount, createWorktreeProcessOutput)) {
            LOGGER.info(String.format("Git createWorktreeProcessOutput: %s", createWorktreeProcessOutput));
        }

        // Computing code smells
        startTime = System.currentTimeMillis(); // TODO: Remove
        String workTreeProjectPath = gitReposProjectPath + File.separator + workTreeName;
        int codeSmellsCount = filePaths.parallelStream().map(p -> {
            try {
                String outputJson = PmdHelper.startPmdCodeSmellProcessAndGetOutput(workTreeProjectPath, p);
                if (outputJson.contains("No such file")) {
                    return 0;
                }
                PmdReport report = JsonHelper.getObject(outputJson, PmdReport.class);
                return Arrays.stream(report.files).map(file -> file.violations.length).reduce(0, (len1, len2) -> len1 + len2);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, String.format("Failure occurred for commit id: [%s]. Details below: ", commitId));
                e.printStackTrace();
                return 0;
            }
        }).reduce(0, (count1, count2) -> count1 + count2);
        endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Computing code smells took: [%d] seconds", reviewNumber, TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove

        // Logging remove worktree process output, if issues
        startTime = System.currentTimeMillis(); // TODO: Remove
        List<String> removeWorkTreeProcessOutputs = GitHelper.removeWorkTree(gitReposProjectPath, workTreeName);
        endTime = System.currentTimeMillis(); // TODO: Remove
        LOGGER.info(String.format("[%d] Remove Worktree took: [%d] seconds", reviewNumber, TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))); // TODO: Remove
        removeWorkTreeProcessOutputs.forEach(o -> {
            if (shouldOutputListBeIgnored(o, IGNORE_REMOVE_WORKTREE_PROCESS_OUTPUT)) {
                return;
            }
            LOGGER.log(Level.SEVERE, o);
        });

        return codeSmellsCount;
    }

    private static boolean shouldOutputListBeIgnored(String output, List<String> ignoredOutputList) {
        for (String ignoreOutputRegex : ignoredOutputList) {
            if (shouldOutputBeIgnored(output, ignoreOutputRegex)) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldOutputBeIgnored(String output, String ignoreOutputRegex) {
        Pattern pattern = Pattern.compile(ignoreOutputRegex);
        if (output.isEmpty() || pattern.matcher(output).matches()) {
            return true;
        }
        return false;
    }

    private static boolean isGitWorktreeCreationSuccessful(boolean isBefore, int reviewNumber, int iterationCount, String gitCheckoutOutput) {
        String partialSuccessString = String.format("First commited as %s_%d_rev%d", isBefore ? "before" : "after", reviewNumber, isBefore ? 1 : iterationCount);
        return gitCheckoutOutput.contains(partialSuccessString);
    }
}
