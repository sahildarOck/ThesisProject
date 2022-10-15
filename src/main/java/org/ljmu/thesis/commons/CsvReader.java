package org.ljmu.thesis.commons;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvReader {
    private static final String INPUT_CSV_PATH = File.separator + "Users" + File.separator + "srivastavas" + File.separator + "Downloads" + File.separator + "M.Sc."
            + File.separator + "datasets" + File.separator + "CROP_dataset" + File.separator + "metadata" + File.separator + "eclipse.platform.ui.csv";

    private List<CSVRecord> records;

    public CsvReader(String filePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {
            records = csvParser.getRecords();
        }
    }

    public String getValue(int recordNumber, String header) throws IOException {
        return records.get(recordNumber).get(header);
    }
}
