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
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PopulateCRSmellData {
    private static final Logger LOGGER = Logger.getLogger(PopulateCRSmellData.class.getName());
    private static final List<String> IGNORE_REVIEWERS_LIST = Arrays.asList("hudsonvoter", "egenie");
    private static final int PING_PONG_THRESHOLD = 3;
    private static final int SLEEPING_REVIEW_THRESHOLD = 2;
    private static final int LOC_CHANGED_THRESHOLD = 500;
    private static final int REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD = 50;

    public static void main(String[] args) throws IOException {
        //  1. Get all RawPRRecords
        List<RawPRRecord> rawPRRecords = CsvHelper.getMergedRawPRRecords().subList(0, 10);
        List<ProcessedPRRecord> processedPRRecords = new ArrayList<>();
        Map<String, Map<String, Integer>> ownerReviewersReviewCountMap = new HashMap<>();
        Map<String, Integer> ownerPRCountMap = new HashMap<>();
        int firstProcessedRecordCount = 0;

        //  2. For each RawPRRecord
        for (int i = 0; i < rawPRRecords.size(); i++) {
            RawPRRecord firstRawPRRecordForPR = rawPRRecords.get(i);

            //  i. Create 1 OutputPRRecord
            ProcessedPRRecord processedPRRecord = new ProcessedPRRecord();

            //  ii. Populate the existing fields for OutputPRRecord from RawPRRecord
            processedPRRecord.setReviewNumber(firstRawPRRecordForPR.getReviewNumber());
            processedPRRecord.setChangeId(firstRawPRRecordForPR.getChangeId());
            processedPRRecord.setUrl(Utils.getPRUrlFromRevisionUrl(firstRawPRRecordForPR.getUrl()));
            processedPRRecord.setBeforeCommitId(firstRawPRRecordForPR.getBeforeCommitId());

            //  iii. Retrieve the last RawPRRecord based on the last revision for the current PR
            i = getLastRevisionIndex(i, processedPRRecord.getReviewNumber(), rawPRRecords);
            RawPRRecord lastRawPRRecordForPR = rawPRRecords.get(i);
            processedPRRecord.setIterationCount(lastRawPRRecordForPR.getRevisionNumber());
            processedPRRecord.setAfterCommitId(lastRawPRRecordForPR.getAfterCommitId());

            //  iv. Hit Gerrit endpoints to get the updated files details
            String colonDelimitedUpdatedFilesList = getColonDelimitedUpdatedFilesList(processedPRRecord.getReviewNumber(), processedPRRecord.getIterationCount());
            processedPRRecord.setUpdatedFilesList(colonDelimitedUpdatedFilesList);
            processedPRRecord.setAtLeastOneUpdatedJavaFile(colonDelimitedUpdatedFilesList.contains(".java"));

            //  v. Hit Gerrit endpoints and fetch GetChangeDetailOutput and GetChangeRevisionCommitOutput
            GetChangeDetailOutput changeDetailOutput = JsonHelper.getObject(GerritApiHelper.getChangeDetail(processedPRRecord.getReviewNumber()), GetChangeDetailOutput.class);
            GetChangeRevisionCommitOutput changeRevisionCommitOutput = JsonHelper.getObject(GerritApiHelper.getChangeRevisionCommit(processedPRRecord.getReviewNumber()), GetChangeRevisionCommitOutput.class);

            //  vi. Populate the fetched fields for OutputPRRecord from GetChangeDetailOutput and GetChangeRevisionCommitOutput
            processedPRRecord.setOwner(changeDetailOutput.owner.username); // TODO: Update to private fields
            processedPRRecord.setReviewersList(getFilteredReviewersList(changeDetailOutput.reviewers.REVIEWER, changeDetailOutput.owner.username)); // TODO: Update to private fields

            //  vi.a. Init/Update ownerReviewersReviewCountMap
            Map<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.containsKey(processedPRRecord.getOwner()) ? ownerReviewersReviewCountMap.get(processedPRRecord.getOwner()) : new HashMap<>();
            processedPRRecord.getReviewersList().forEach(r -> {
                int reviewerForThisAuthorReviewCount = reviewersReviewCountMap.containsKey(r) ? reviewersReviewCountMap.get(r) : 0;
                reviewersReviewCountMap.put(r, reviewerForThisAuthorReviewCount + 1);
            });
            ownerReviewersReviewCountMap.put(processedPRRecord.getOwner(), reviewersReviewCountMap);

            //  vi.b. Init/Update authorReviewCountMap
            int ownerReviewCount = ownerPRCountMap.containsKey(processedPRRecord.getOwner()) ? ownerPRCountMap.get(processedPRRecord.getOwner()) : 1;
            ownerPRCountMap.put(processedPRRecord.getOwner(), ownerReviewCount);

            processedPRRecord.setCreatedDate(DateUtils.getDate(changeDetailOutput.created)); // TODO: Update to private fields
            processedPRRecord.setMergedDate(DateUtils.getDate(changeDetailOutput.submitted)); // TODO: Update to private fields
            processedPRRecord.setLocChanged(changeDetailOutput.insertions + changeDetailOutput.deletions); // TODO: Update to private fields
            processedPRRecord.setSubject(changeRevisionCommitOutput.subject.trim()); // TODO: Update to private fields
            processedPRRecord.setMessage(changeRevisionCommitOutput.message.trim()); // TODO: Update to private fields

            //  vii. Derive the CRSmells (except review_buddies_cr_smell) derived fields and populate them for OutputPRRecord
            processedPRRecord.setLackOfCRCRSmell(processedPRRecord.getReviewersList().isEmpty());
            processedPRRecord.setPingPongCRSmell(processedPRRecord.getIterationCount() > PING_PONG_THRESHOLD);
            processedPRRecord.setSleepingReviewsCRSmell(processedPRRecord.getMergedDate().compareTo(processedPRRecord.getCreatedDate()) > SLEEPING_REVIEW_THRESHOLD);
            processedPRRecord.setMissingContextCRSmell(isMissingContextCRSmell(processedPRRecord.getSubject(), processedPRRecord.getMessage()));
            processedPRRecord.setLargeChangesetsCRSmell(processedPRRecord.getLocChanged() > LOC_CHANGED_THRESHOLD);

            processedPRRecords.add(processedPRRecord);
            firstProcessedRecordCount++;
            if (firstProcessedRecordCount % 500 == 0) {
                LOGGER.info(String.format("First loop processing completed for: %d records", firstProcessedRecordCount));
            }
        }

        //  viii. Derive review_buddies_cr_smell and populate it for OutputPRRecord
        processedPRRecords.stream().forEach(pr -> {
            int ownerPRCount = ownerPRCountMap.get(pr.getOwner());
            if (ownerPRCount < REVIEW_BUDDIES_TOTAL_PRS_BY_AUTHOR_THRESHOLD) {
                return;
            }
            Map<String, Integer> reviewersReviewCountMap = ownerReviewersReviewCountMap.get(pr.getOwner());
            boolean reviewBuddiesCRSmell = pr.getReviewersList().stream().anyMatch(r -> reviewersReviewCountMap.get(r) > ownerPRCount / 2);
            pr.setReviewBuddiesCRSmell(reviewBuddiesCRSmell);
        });

        CsvHelper.writeOutputCsv(processedPRRecords);
    }

    private static int getLastRevisionIndex(int index, int reviewNumber, List<RawPRRecord> rawPRRecords) {
        while (index < rawPRRecords.size() && reviewNumber == rawPRRecords.get(index).getReviewNumber()) {
            index++;
        }
        return index - 1;
    }

    private static String getColonDelimitedUpdatedFilesList(int reviewNumber, int commitNumber) throws IOException {
        String changeRevisionFilesApiOutput = GerritApiHelper.getChangeRevisionFiles(reviewNumber, commitNumber);
        List<String> updatedFiles = JsonHelper.getFieldNames(changeRevisionFilesApiOutput);
        updatedFiles.remove(0); // Remove the 'COMMIT_MSG' field
        return updatedFiles.stream().reduce("", (str1, str2) -> str1 + ":" + str2);
    }

    private static List<String> getFilteredReviewersList(Developer[] reviewers, String ownerUsername) {
        return Arrays.stream(reviewers)
                .filter(r -> !IGNORE_REVIEWERS_LIST.contains(r.username) && !ownerUsername.equals(r.username))
                .map(r -> r.username).collect(Collectors.toList());
    }

    private static boolean isMissingContextCRSmell(String subject, String message) {
        if (subject.isEmpty() || message.isEmpty()) {
            return true;
        }
        String filteredMessage = message.split("[\n]*Change-Id")[0];
        return subject.equals(filteredMessage);
    }
}
