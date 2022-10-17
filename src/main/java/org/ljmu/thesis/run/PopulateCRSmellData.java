package org.ljmu.thesis.run;

import org.ljmu.thesis.commons.DateUtils;
import org.ljmu.thesis.commons.Utils;
import org.ljmu.thesis.helpers.CsvHelper;
import org.ljmu.thesis.helpers.JsonHelper;
import org.ljmu.thesis.helpers.crsmells.GerritApiHelper;
import org.ljmu.thesis.model.ProcessedPRRecord;
import org.ljmu.thesis.model.crsmells.Developer;
import org.ljmu.thesis.model.crsmells.GetChangeDetailOutput;
import org.ljmu.thesis.model.crsmells.GetChangeRevisionCommitOutput;
import org.ljmu.thesis.model.crsmells.RawPRRecord;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PopulateCRSmellData {
    private static final Logger LOGGER = Logger.getLogger(PopulateCRSmellData.class.getName());
    private static final List<String> IGNORE_REVIEWERS_LIST = Arrays.asList("CI Bot", "Eclipse Genie", "Gerrit Code Review @ Eclipse.org");
    private static final int PING_PONG_THRESHOLD = 3;
    private static final int SLEEPING_REVIEW_THRESHOLD = 2;
    private static final int LOC_CHANGED_THRESHOLD = 500;
    private static final int REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD = 50;

    private static final int MAX_RECORDS_TO_INCLUDE = 200;

    public static void main(String[] args) {
        try {
            System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "40");
            long start = System.currentTimeMillis();
            populate();
            long end = System.currentTimeMillis();
            LOGGER.info(String.format("Time taken in minutes to finish: [%d]", TimeUnit.MILLISECONDS.toMinutes(end - start)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void populate() throws IOException {
        //  1. Get all RawPRRecords
        List<RawPRRecord> rawPRRecords = CsvHelper.getMergedRawPRRecords();
        List<ProcessedPRRecord> processedPRRecords = new ArrayList<>();
        Map<String, Map<String, Integer>> ownerReviewersReviewCountMap = new HashMap<>();
        Map<String, Integer> ownerPRCountMap = new HashMap<>();

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
                Map<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.containsKey(owner) ? ownerReviewersReviewCountMap.get(owner) : new HashMap<>();
                filteredReviewersList.forEach(r -> {
                    int reviewerForThisAuthorReviewCount = reviewersReviewCountMap.containsKey(r) ? reviewersReviewCountMap.get(r) : 0;
                    reviewersReviewCountMap.put(r, reviewerForThisAuthorReviewCount + 1);
                });
                ownerReviewersReviewCountMap.put(owner, reviewersReviewCountMap);

                //  vi.b. Init/Update authorReviewCountMap
                int ownerReviewCount = ownerPRCountMap.containsKey(owner) ? ownerPRCountMap.get(owner) : 1;
                ownerPRCountMap.put(owner, ownerReviewCount);

                //  vi.c. Populate other fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput
                prUpdated.setOwner(owner);
                prUpdated.setReviewersList(filteredReviewersList);
                prUpdated.setCreatedDate(DateUtils.getDate(changeDetailOutput.getCreated()));
                prUpdated.setMergedDate(DateUtils.getDate(changeDetailOutput.getSubmitted()));
                prUpdated.setLocChanged(changeDetailOutput.getInsertions() + changeDetailOutput.getDeletions());
                prUpdated.setSubject(changeRevisionCommitOutput.getSubject().trim());
                prUpdated.setMessage(changeRevisionCommitOutput.getMessage().trim());

                LOGGER.info(String.format("Object updated after Gerrit calls for review number: [%d]", pr.getReviewNumber()));
            } catch (Exception e) {
                if (pr != null) {
                    LOGGER.log(Level.SEVERE, String.format("Failure occurred for review number: [%d]. Details below: ", pr.getReviewNumber()));
                }
                e.printStackTrace();
            }
            return prUpdated;
        }).collect(Collectors.toList());

        deriveAndPopulateCRSmellsInProcessedPRRecords(processedPRRecords, ownerReviewersReviewCountMap, ownerPRCountMap);

        CsvHelper.writeOutputCsv(processedPRRecords);
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

    private static void deriveAndPopulateCRSmellsInProcessedPRRecords(List<ProcessedPRRecord> processedPRRecords, Map<String, Map<String, Integer>> ownerReviewersReviewCountMap, Map<String, Integer> ownerPRCountMap) {
        processedPRRecords.parallelStream().forEach(pr -> {
            //  vii. Derive the CRSmells derived fields and populate them for OutputPRRecord
            pr.setLackOfCRCRSmell(pr.getReviewersList().isEmpty());
            pr.setPingPongCRSmell(pr.getIterationCount() > PING_PONG_THRESHOLD);
            pr.setSleepingReviewsCRSmell(pr.getMergedDate().compareTo(pr.getCreatedDate()) > SLEEPING_REVIEW_THRESHOLD);
            pr.setMissingContextCRSmell(isMissingContextCRSmell(pr.getSubject(), pr.getMessage()));
            pr.setLargeChangesetsCRSmell(pr.getLocChanged() > LOC_CHANGED_THRESHOLD);

            //  Deriving review_buddies_cr_smell
            int ownerPRCount = ownerPRCountMap.get(pr.getOwner());
            if (ownerPRCount < REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD) {
                return;
            }
            Map<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.get(pr.getOwner());
            boolean reviewBuddiesCRSmell = pr.getReviewersList().stream().anyMatch(r -> reviewersReviewCountMap.get(r) > ownerPRCount / 2);
            pr.setReviewBuddiesCRSmell(reviewBuddiesCRSmell);
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
}
