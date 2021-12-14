package pl.wiktor.circle.domain.backup.utils;

public class BackupMessageHelper {
    public static String prepareTimeMessage(int lastBackup, int minimumMinutesToRetry) {
        String template = "Database backup was executed %d %s ago, next backup in %d %s";
        int timeAgo = lastBackup;
        String timeAgoUnit = "minute(s)";
        int nextRetry = minimumMinutesToRetry - timeAgo;
        String nextRetryUnit = "minute(s)";
        if (lastBackup > 60) {
            timeAgo = lastBackup / 60;
            timeAgoUnit = "hour(s)";
        }
        if (nextRetry > 60) {
            if (timeAgoUnit.equals("minute(s)")) {
                nextRetry = minimumMinutesToRetry / 60 - (timeAgo / 60);
            } else {
                nextRetry = minimumMinutesToRetry / 60 - timeAgo;
            }
            nextRetryUnit = "hour(s)";
        }
        return String.format(template, timeAgo, timeAgoUnit, nextRetry, nextRetryUnit);
    }
}
