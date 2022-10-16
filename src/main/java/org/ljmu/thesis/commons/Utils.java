package org.ljmu.thesis.commons;

public class Utils {
    public static String getPRUrlFromRevisionUrl(String revisionUrl) {
        return revisionUrl.substring(0, revisionUrl.lastIndexOf("/"));
    }
}
