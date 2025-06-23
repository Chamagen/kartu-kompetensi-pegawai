package com.kemham.kartukompetensi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing application preferences and settings.
 */
public class PreferenceManager {
    private static final String PREF_NAME = "kartu_kompetensi_prefs";
    private static volatile PreferenceManager instance;
    private final SharedPreferences preferences;

    // Preference keys
    private static final String KEY_LAST_SELECTED_EMPLOYEE = "last_selected_employee";
    private static final String KEY_SKILL_SORT_ORDER = "skill_sort_order";
    private static final String KEY_MIN_SKILL_SCORE = "min_skill_score";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_AUTO_REFRESH = "auto_refresh";
    private static final String KEY_REFRESH_INTERVAL = "refresh_interval";
    private static final String KEY_NOTIFICATION_ENABLED = "notification_enabled";
    private static final String KEY_LANGUAGE = "language";

    private PreferenceManager(Context context) {
        preferences = context.getApplicationContext()
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PreferenceManager.class) {
                if (instance == null) {
                    instance = new PreferenceManager(context);
                }
            }
        }
        return instance;
    }

    // Last selected employee
    public void setLastSelectedEmployee(String nip) {
        preferences.edit().putString(KEY_LAST_SELECTED_EMPLOYEE, nip).apply();
    }

    public String getLastSelectedEmployee() {
        return preferences.getString(KEY_LAST_SELECTED_EMPLOYEE, null);
    }

    // Skill sort order
    public void setSkillSortOrder(String order) {
        preferences.edit().putString(KEY_SKILL_SORT_ORDER, order).apply();
    }

    public String getSkillSortOrder() {
        return preferences.getString(KEY_SKILL_SORT_ORDER, "name_asc");
    }

    // Minimum skill score filter
    public void setMinimumSkillScore(int score) {
        preferences.edit().putInt(KEY_MIN_SKILL_SCORE, score).apply();
    }

    public int getMinimumSkillScore() {
        return preferences.getInt(KEY_MIN_SKILL_SCORE, AppSettings.MIN_SCORE);
    }

    // Dark mode
    public void setDarkMode(boolean enabled) {
        preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    // Auto refresh
    public void setAutoRefresh(boolean enabled) {
        preferences.edit().putBoolean(KEY_AUTO_REFRESH, enabled).apply();
    }

    public boolean isAutoRefreshEnabled() {
        return preferences.getBoolean(KEY_AUTO_REFRESH, true);
    }

    // Refresh interval (in minutes)
    public void setRefreshInterval(int minutes) {
        preferences.edit().putInt(KEY_REFRESH_INTERVAL, minutes).apply();
    }

    public int getRefreshInterval() {
        return preferences.getInt(KEY_REFRESH_INTERVAL, 30);
    }

    // Notifications
    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }

    public boolean areNotificationsEnabled() {
        return preferences.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }

    // Language
    public void setLanguage(String language) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public String getLanguage() {
        return preferences.getString(KEY_LANGUAGE, "id");
    }

    /**
     * Resets all preferences to their default values
     */
    public void resetToDefaults() {
        preferences.edit()
            .remove(KEY_LAST_SELECTED_EMPLOYEE)
            .remove(KEY_SKILL_SORT_ORDER)
            .remove(KEY_MIN_SKILL_SCORE)
            .remove(KEY_DARK_MODE)
            .remove(KEY_AUTO_REFRESH)
            .remove(KEY_REFRESH_INTERVAL)
            .remove(KEY_NOTIFICATION_ENABLED)
            .remove(KEY_LANGUAGE)
            .apply();
    }

    /**
     * Checks if this is the first launch of the app
     */
    public boolean isFirstLaunch() {
        return preferences.getBoolean("first_launch", true);
    }

    /**
     * Marks the first launch as completed
     */
    public void setFirstLaunchComplete() {
        preferences.edit().putBoolean("first_launch", false).apply();
    }

    /**
     * Gets all preferences as a map for backup purposes
     */
    public java.util.Map<String, ?> getAll() {
        return preferences.getAll();
    }

    /**
     * Imports preferences from a map (for restore purposes)
     */
    public void importPreferences(java.util.Map<String, ?> prefs) {
        SharedPreferences.Editor editor = preferences.edit();
        for (java.util.Map.Entry<String, ?> entry : prefs.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                editor.putString(entry.getKey(), (String) value);
            } else if (value instanceof Integer) {
                editor.putInt(entry.getKey(), (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(entry.getKey(), (Boolean) value);
            } else if (value instanceof Long) {
                editor.putLong(entry.getKey(), (Long) value);
            } else if (value instanceof Float) {
                editor.putFloat(entry.getKey(), (Float) value);
            }
        }
        editor.apply();
    }
}
