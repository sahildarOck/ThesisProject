package org.ljmu.thesis.commons;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CsvWriter {
    public void write(String filePath, String[] headers, List<String[]> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers).setTrim(true).setIgnoreHeaderCase(true).build());
        ) {
            csvPrinter.printRecords(records);
        }
    }
}
