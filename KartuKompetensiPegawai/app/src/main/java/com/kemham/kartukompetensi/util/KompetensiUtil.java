package com.kemham.kartukompetensi.util;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.kemham.kartukompetensi.R;
import com.kemham.kartukompetensi.model.Skill;
import com.kemham.kartukompetensi.model.Employee;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for competency-related operations and calculations.
 */
public class KompetensiUtil {

    private static final NumberFormat scoreFormat;
    private static final NumberFormat percentFormat;

    static {
        scoreFormat = NumberFormat.getInstance(new Locale("id"));
        scoreFormat.setMaximumFractionDigits(1);
        scoreFormat.setMinimumFractionDigits(0);

        percentFormat = NumberFormat.getPercentInstance(new Locale("id"));
        percentFormat.setMaximumFractionDigits(1);
    }

    /**
     * Formats a score value for display
     */
    public static String formatScore(double score) {
        return scoreFormat.format(score);
    }

    /**
     * Formats a percentage value for display
     */
    public static String formatPercentage(double value) {
        return percentFormat.format(value / 100.0);
    }

    /**
     * Gets the color for a score value
     */
    @ColorInt
    public static int getScoreColor(Context context, int score) {
        @ColorRes int colorRes;
        if (score >= AppSettings.EXCELLENT_THRESHOLD) {
            colorRes = R.color.score_excellent;
        } else if (score >= AppSettings.GOOD_THRESHOLD) {
            colorRes = R.color.score_good;
        } else if (score >= AppSettings.AVERAGE_THRESHOLD) {
            colorRes = R.color.score_average;
        } else if (score >= AppSettings.NEEDS_IMPROVEMENT_THRESHOLD) {
            colorRes = R.color.score_needs_improvement;
        } else {
            colorRes = R.color.score_poor;
        }
        return ContextCompat.getColor(context, colorRes);
    }

    /**
     * Gets the progress color for a score relative to target
     */
    @ColorInt
    public static int getProgressColor(Context context, int score, int target) {
        float progress = (float) score / target;
        if (progress >= 1.0f) {
            return ContextCompat.getColor(context, R.color.progress_complete);
        } else if (progress >= 0.8f) {
            return ContextCompat.getColor(context, R.color.progress_good);
        } else if (progress >= 0.6f) {
            return ContextCompat.getColor(context, R.color.progress_average);
        } else {
            return ContextCompat.getColor(context, R.color.progress_poor);
        }
    }

    /**
     * Calculates the gradient color between two colors based on progress
     */
    @ColorInt
    public static int calculateGradientColor(@ColorInt int startColor, @ColorInt int endColor, float progress) {
        float[] startHSV = new float[3];
        float[] endHSV = new float[3];
        Color.colorToHSV(startColor, startHSV);
        Color.colorToHSV(endColor, endHSV);

        float[] resultHSV = new float[3];
        for (int i = 0; i < 3; i++) {
            resultHSV[i] = startHSV[i] + (endHSV[i] - startHSV[i]) * progress;
        }

        return Color.HSVToColor(resultHSV);
    }

    /**
     * Calculates the average score for a list of skills
     */
    public static double calculateAverageScore(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Skill skill : skills) {
            sum += skill.getScore();
        }
        return sum / skills.size();
    }

    /**
     * Calculates the completion percentage for a list of skills
     */
    public static double calculateCompletionPercentage(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return 0.0;
        }

        int completed = 0;
        for (Skill skill : skills) {
            if (skill.getScore() >= skill.getTargetScore()) {
                completed++;
            }
        }
        return (double) completed / skills.size() * 100.0;
    }

    /**
     * Gets the development priority level based on score gap
     */
    public static Skill.Level calculatePriorityLevel(int currentScore, int targetScore) {
        int gap = targetScore - currentScore;
        if (gap >= 30) {
            return Skill.Level.HIGH;
        } else if (gap >= 15) {
            return Skill.Level.MEDIUM;
        } else {
            return Skill.Level.LOW;
        }
    }

    /**
     * Gets the status badge color for an employee status
     */
    @ColorInt
    public static int getStatusColor(Context context, Employee.EmployeeStatus status) {
        switch (status) {
            case ACTIVE:
                return ContextCompat.getColor(context, R.color.status_active);
            case INACTIVE:
                return ContextCompat.getColor(context, R.color.status_inactive);
            case ON_LEAVE:
                return ContextCompat.getColor(context, R.color.status_on_leave);
            case TRANSFERRED:
                return ContextCompat.getColor(context, R.color.status_transferred);
            default:
                return Color.GRAY;
        }
    }

    /**
     * Validates a NIP (Employee ID) format
     */
    public static boolean isValidNip(String nip) {
        return nip != null && nip.matches("\\d{18}");
    }

    /**
     * Gets the development status message based on progress
     */
    public static String getDevelopmentStatus(Context context, int score, int target) {
        float progress = (float) score / target;
        if (progress >= 1.0f) {
            return context.getString(R.string.development_status_complete);
        } else if (progress >= 0.8f) {
            return context.getString(R.string.development_status_on_track);
        } else if (progress >= 0.6f) {
            return context.getString(R.string.development_status_in_progress);
        } else {
            return context.getString(R.string.development_status_needs_attention);
        }
    }

    private KompetensiUtil() {
        // Private constructor to prevent instantiation
    }
}
