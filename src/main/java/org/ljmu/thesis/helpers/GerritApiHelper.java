package org.ljmu.thesis.helpers;

import java.io.IOException;

public class GerritApiHelper {
    public static String getChangeDetail(int reviewNumber) throws IOException {
        String uri = String.format("%s/changes/%d/detail", ConfigHelper.getBaseUrlForProjectToRun(), reviewNumber);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionCommit(int reviewNumber) throws IOException {
        return getChangeRevisionCommit(reviewNumber, 1);
    }

    public static String getChangeRevisionCommit(int reviewNumber, int commitNumber) throws IOException {
        String uri = String.format("%s/changes/%d/revisions/%d/commit", ConfigHelper.getBaseUrlForProjectToRun(), reviewNumber, commitNumber);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionFiles(int reviewNumber, int commitNumber) throws IOException {
        String uri = String.format("%s/changes/%d/revisions/%d/files", ConfigHelper.getBaseUrlForProjectToRun(), reviewNumber, commitNumber);
        return ApiHelper.get(uri);
    }
}