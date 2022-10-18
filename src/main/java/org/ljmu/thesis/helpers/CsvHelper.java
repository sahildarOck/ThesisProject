package org.ljmu.thesis.helpers;

import org.apache.commons.csv.CSVRecord;
import org.ljmu.thesis.commons.CsvReader;
import org.ljmu.thesis.commons.CsvWriter;
import org.ljmu.thesis.enums.Status;
import org.ljmu.thesis.model.ProcessedPRRecord;
import org.ljmu.thesis.model.crsmells.RawPRRecord;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CsvHelper {

    private static final String[] OUTPUT_HEADERS = {"revision_num", "change_id", "url", "iteration_count", "before_commit_id", "after_commit_id", "at_least_one_updated_java_file", "T(created)", "T(merged)",
            "LOC changed", "subject", "message", "cr_smell_lack_of_cr", "cr_smell_ping_pong", "cr_smell_sleeping_reviews", "cr_smell_missing_context",
            "cr_smell_large_changesets", "cr_smell_review_buddies", "code_smells_difference_count", "code_smells_increased"};

    public static List<RawPRRecord> getMergedRawPRRecords() throws IOException {
        CsvReader csvReader = new CsvReader(PathHelper.getMetaDataCsvPath());
        List<CSVRecord> records = csvReader.getCsvRecords();

        return records.parallelStream()
                .filter(r -> Status.MERGED.name().equals(r.get("status")))
                .map(r -> transformCsvRecord(r)).collect(Collectors.toList());
    }

    public static void writeOutputCsv(List<ProcessedPRRecord> processedPRRecords) throws IOException {
        List<String[]> recordsAsString = processedPRRecords.parallelStream().map(r -> r.getRecords()).collect(Collectors.toList());
        new CsvWriter().write(PathHelper.getOutputFilePath() + "testOutputFile.csv", OUTPUT_HEADERS, recordsAsString);
    }

    private static RawPRRecord transformCsvRecord(CSVRecord record) {
        RawPRRecord rawPRRecord = new RawPRRecord();

        rawPRRecord.setId(record.get("id"));
        rawPRRecord.setReviewNumber(Integer.parseInt(record.get("review_number")));
        rawPRRecord.setRevisionNumber(Integer.parseInt(record.get("revision_number")));
        rawPRRecord.setAuthor(record.get("author"));
        rawPRRecord.setStatus(Status.valueOf(record.get("status")));
        rawPRRecord.setChangeId(record.get("change_id"));
        rawPRRecord.setUrl(record.get("url"));
        rawPRRecord.setBeforeCommitId(record.get("before_commit_id"));
        rawPRRecord.setAfterCommitId(record.get("after_commit_id"));

        return rawPRRecord;
    }
}
