package com.kemham.kartukompetensi.util;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Application-wide constants and settings.
 */
public class AppSettings {

    // Score ranges
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 100;
    public static final int DEFAULT_TARGET_SCORE = 80;

    // Threshold values
    public static final int EXCELLENT_THRESHOLD = 90;
    public static final int GOOD_THRESHOLD = 75;
    public static final int AVERAGE_THRESHOLD = 60;
    public static final int NEEDS_IMPROVEMENT_THRESHOLD = 40;

    @IntDef({
        EXCELLENT_THRESHOLD,
        GOOD_THRESHOLD,
        AVERAGE_THRESHOLD,
        NEEDS_IMPROVEMENT_THRESHOLD
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScoreThreshold {}

    // Performance levels
    public static final String LEVEL_EXCELLENT = "Sangat Baik";
    public static final String LEVEL_GOOD = "Baik";
    public static final String LEVEL_AVERAGE = "Cukup";
    public static final String LEVEL_NEEDS_IMPROVEMENT = "Perlu Peningkatan";
    public static final String LEVEL_POOR = "Kurang";

    @StringDef({
        LEVEL_EXCELLENT,
        LEVEL_GOOD,
        LEVEL_AVERAGE,
        LEVEL_NEEDS_IMPROVEMENT,
        LEVEL_POOR
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PerformanceLevel {}

    // Sort orders
    public static final String SORT_NAME_ASC = "name_asc";
    public static final String SORT_NAME_DESC = "name_desc";
    public static final String SORT_SCORE_ASC = "score_asc";
    public static final String SORT_SCORE_DESC = "score_desc";
    public static final String SORT_DATE_ASC = "date_asc";
    public static final String SORT_DATE_DESC = "date_desc";

    @StringDef({
        SORT_NAME_ASC,
        SORT_NAME_DESC,
        SORT_SCORE_ASC,
        SORT_SCORE_DESC,
        SORT_DATE_ASC,
        SORT_DATE_DESC
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortOrder {}

    // Time intervals (in minutes)
    public static final int REFRESH_INTERVAL_SHORT = 15;
    public static final int REFRESH_INTERVAL_MEDIUM = 30;
    public static final int REFRESH_INTERVAL_LONG = 60;

    @IntDef({
        REFRESH_INTERVAL_SHORT,
        REFRESH_INTERVAL_MEDIUM,
        REFRESH_INTERVAL_LONG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RefreshInterval {}

    // Page sizes
    public static final int PAGE_SIZE_SMALL = 10;
    public static final int PAGE_SIZE_MEDIUM = 20;
    public static final int PAGE_SIZE_LARGE = 50;

    @IntDef({
        PAGE_SIZE_SMALL,
        PAGE_SIZE_MEDIUM,
        PAGE_SIZE_LARGE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageSize {}

    // Animation durations (in milliseconds)
    public static final int ANIMATION_DURATION_SHORT = 150;
    public static final int ANIMATION_DURATION_MEDIUM = 300;
    public static final int ANIMATION_DURATION_LONG = 500;

    @IntDef({
        ANIMATION_DURATION_SHORT,
        ANIMATION_DURATION_MEDIUM,
        ANIMATION_DURATION_LONG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationDuration {}

    // Cache settings
    public static final int CACHE_SIZE_MB = 10;
    public static final int CACHE_EXPIRY_DAYS = 7;

    // Network timeouts (in seconds)
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;

    // Date formats
    public static final String DATE_FORMAT_DISPLAY = "dd MMMM yyyy";
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";

    @StringDef({
        DATE_FORMAT_DISPLAY,
        DATE_FORMAT_ISO,
        DATE_FORMAT_SHORT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DateFormat {}

    // File size limits (in bytes)
    public static final long MAX_PHOTO_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_ATTACHMENT_SIZE = 10 * 1024 * 1024; // 10MB

    // Development flags
    public static final boolean DEBUG = true;
    public static final boolean ENABLE_CRASH_REPORTING = true;
    public static final boolean ENABLE_ANALYTICS = true;

    private AppSettings() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the performance level based on a score
     */
    @PerformanceLevel
    public static String getPerformanceLevel(int score) {
        if (score >= EXCELLENT_THRESHOLD) {
            return LEVEL_EXCELLENT;
        } else if (score >= GOOD_THRESHOLD) {
            return LEVEL_GOOD;
        } else if (score >= AVERAGE_THRESHOLD) {
            return LEVEL_AVERAGE;
        } else if (score >= NEEDS_IMPROVEMENT_THRESHOLD) {
            return LEVEL_NEEDS_IMPROVEMENT;
        } else {
            return LEVEL_POOR;
        }
    }

    /**
     * Validates a score value
     */
    public static boolean isValidScore(int score) {
        return score >= MIN_SCORE && score <= MAX_SCORE;
    }

    /**
     * Clamps a score value to the valid range
     */
    public static int clampScore(int score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
