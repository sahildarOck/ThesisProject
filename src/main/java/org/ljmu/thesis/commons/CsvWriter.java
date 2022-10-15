package org.ljmu.thesis.commons;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.ljmu.thesis.helpers.PathHelper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CsvWriter {
    public <T> void write(String filePath, String[] headers, List<T> object) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers).setTrim(true).setIgnoreHeaderCase(true).build());
        ) {
            csvPrinter.printRecord(object);
        }
    }

    public static void main(String[] args) throws IOException {
        String[] headers = new String[]{"HEADER_1", "HEADER_2"};
        new CsvWriter().write(PathHelper.getOutputFilePath() + "testOutputFile.csv", headers, Arrays.asList("Heya", "Sheya"));
    }
}
