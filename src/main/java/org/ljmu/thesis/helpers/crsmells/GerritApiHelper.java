package org.ljmu.thesis.helpers.crsmells;

import java.io.IOException;

public class GerritApiHelper {
    public static String getChangeDetail(int reviewNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%d/detail", reviewNumber);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionCommit(int reviewNumber) throws IOException {
        return getChangeRevisionCommit(reviewNumber, 1);
    }

    public static String getChangeRevisionCommit(int reviewNumber, int commitNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%d/revisions/%d/commit", reviewNumber, commitNumber);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionFiles(int reviewNumber, int commitNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%d/revisions/%d/files", reviewNumber, commitNumber);
        return ApiHelper.get(uri);
    }
}

// https://review.couchbase.org/c/spymemcached
// https://git.eclipse.org/r - eclipse / jgit / egit
// http://review.couchbase.org - couchbase jvm core / java client