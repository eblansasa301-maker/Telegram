package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Local communication streak tracker for sLgram.
 *
 * A streak counts consecutive days on which two people exchanged at least one message.
 * It becomes visible after 3 consecutive days and burns out when a whole day is skipped.
 * Since streaks are not provided by the Telegram API, they are stored locally per account.
 */
public class StreakManager {

    private static final String PREFS_PREFIX = "slgram_streaks_";
    private static final int MIN_VISIBLE_STREAK = 3;

    public static class StreakInfo {
        public long dialogId;
        public int streak;
        public int totalDays;
        public int bestStreak;
        public long lastDay;
        public int visibleStreak;

        public boolean isVisible() {
            return visibleStreak >= MIN_VISIBLE_STREAK;
        }
    }

    private static SharedPreferences prefs(int account) {
        return ApplicationLoader.applicationContext.getSharedPreferences(PREFS_PREFIX + account, Context.MODE_PRIVATE);
    }

    private static String key(long dialogId, String suffix) {
        return "d" + dialogId + "_" + suffix;
    }

    private static long toEpochDay(long unixSeconds) {
        return Instant.ofEpochSecond(unixSeconds).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay();
    }

    private static long today() {
        return LocalDate.now().toEpochDay();
    }

    /**
     * Records a message exchanged in a private dialog. Safe to call repeatedly:
     * only one increment happens per calendar day per dialog.
     */
    public static synchronized void record(int account, long dialogId, long unixSeconds) {
        if (dialogId <= 0 || unixSeconds <= 0) {
            return;
        }
        final long day = toEpochDay(unixSeconds);
        final SharedPreferences p = prefs(account);
        final String lastKey = key(dialogId, "last");
        final String streakKey = key(dialogId, "streak");
        final String totalKey = key(dialogId, "total");
        final String bestKey = key(dialogId, "best");

        final long last = p.getLong(lastKey, 0);
        int streak = p.getInt(streakKey, 0);
        int total = p.getInt(totalKey, 0);
        int best = p.getInt(bestKey, 0);

        if (day <= last) {
            return;
        }

        if (last != 0 && day == last + 1) {
            streak++;
        } else {
            streak = 1;
        }
        total++;
        if (streak > best) {
            best = streak;
        }

        p.edit()
                .putLong(lastKey, day)
                .putInt(streakKey, streak)
                .putInt(totalKey, total)
                .putInt(bestKey, best)
                .apply();
    }

    /**
     * Returns the locally stored streak. A streak is only considered alive if the last
     * communication happened today or yesterday; otherwise it has already burned out.
     */
    public static synchronized StreakInfo getInfo(int account, long dialogId) {
        final SharedPreferences p = prefs(account);
        final StreakInfo info = new StreakInfo();
        info.dialogId = dialogId;
        info.lastDay = p.getLong(key(dialogId, "last"), 0);
        info.streak = p.getInt(key(dialogId, "streak"), 0);
        info.totalDays = p.getInt(key(dialogId, "total"), 0);
        info.bestStreak = p.getInt(key(dialogId, "best"), 0);

        final long today = today();
        final boolean alive = info.lastDay == today || info.lastDay == today - 1;
        info.visibleStreak = alive ? info.streak : 0;
        return info;
    }

    public static int getVisibleStreak(int account, long dialogId) {
        return getInfo(account, dialogId).visibleStreak;
    }
}
