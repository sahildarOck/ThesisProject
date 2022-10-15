package org.ljmu.thesis.helpers.crsmells;

import java.io.IOException;

public class GerritApiHelper {

    public static String getChangeDetail(String changeId) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%s/detail", changeId);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionCommit(String changeId) throws IOException {
        return getChangeRevisionCommit(changeId, 1);
    }

    public static String getChangeRevisionCommit(String changeId, int commitNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%s/revisions/%d/commit", changeId, commitNumber);
        return ApiHelper.get(uri);
    }

    public static String getChangeRevisionFiles(String changeId, int commitNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%s/revisions/%d/files", changeId, commitNumber);
        return ApiHelper.get(uri);
    }
}
