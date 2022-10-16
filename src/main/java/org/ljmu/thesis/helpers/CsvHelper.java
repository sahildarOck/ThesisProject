package org.ljmu.thesis.helpers;

import org.apache.commons.csv.CSVRecord;
import org.ljmu.thesis.commons.CsvReader;
import org.ljmu.thesis.enums.Status;
import org.ljmu.thesis.model.codesmells.RawPRRecord;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CsvHelper {
    public static List<RawPRRecord> getMergedRawPRRecords() throws IOException {
        CsvReader csvReader = new CsvReader(PathHelper.getMetaDataCsvPath());
        List<CSVRecord> records = csvReader.getCsvRecords();

        return records.parallelStream().map(r -> transformCsvRecord(r)).filter(r -> Status.MERGED == r.getStatus()).collect(Collectors.toList());
    }

    public static RawPRRecord transformCsvRecord(CSVRecord record) {
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

    public static void main(String[] args) throws IOException {
        List<RawPRRecord> rawPRRecords = CsvHelper.getMergedRawPRRecords();
        System.out.println("Heya!");
    }
}
