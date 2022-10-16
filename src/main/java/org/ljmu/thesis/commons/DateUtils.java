package org.ljmu.thesis.commons;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static String date = "2015-12-28 09:35:26.000000000"; // TODO: remove
    private static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.n";

    public static LocalDate getDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDate getDate(String date) {
        return getDate(date, DEFAULT_DATE_FORMAT);
    }
}
