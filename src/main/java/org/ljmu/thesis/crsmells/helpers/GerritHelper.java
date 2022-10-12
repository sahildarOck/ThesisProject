package org.ljmu.thesis.crsmells.helpers;

import java.io.IOException;

public class GerritHelper {

    public static String getChangeDetail(String changeId) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%s/detail", changeId);
        return RestHelper.get(uri);
    }

    public static String getChangeRevisionCommit(String changeId) throws IOException {
        return getChangeRevisionCommit(changeId, 1);
    }

    public static String getChangeRevisionCommit(String changeId, int commitNumber) throws IOException {
        String uri = String.format("https://git.eclipse.org/r/changes/%s/revisions/%d/commit", changeId, commitNumber);
        return RestHelper.get(uri);
    }
}
