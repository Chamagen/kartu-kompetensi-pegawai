package com.kemham.kartukompetensi.model;

import androidx.room.TypeConverter;

import com.kemham.kartukompetensi.model.Employee.EmployeeStatus;
import com.kemham.kartukompetensi.model.Skill.Category;
import com.kemham.kartukompetensi.model.Skill.Level;

/**
 * Type converter for handling enum values in Room database.
 */
public class CategoryConverter {

    /**
     * Converts a string to Skill Category enum.
     * @param value The string value
     * @return The Category enum value, or null if the string is null
     */
    @TypeConverter
    public static Category toCategory(String value) {
        try {
            return value == null ? null : Category.valueOf(value);
        } catch (IllegalArgumentException e) {
            return Category.BASIC; // Default value if conversion fails
        }
    }

    /**
     * Converts a Skill Category enum to string.
     * @param category The Category enum value
     * @return The string value, or null if the enum is null
     */
    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : category.name();
    }

    /**
     * Converts a string to Skill Level enum.
     * @param value The string value
     * @return The Level enum value, or null if the string is null
     */
    @TypeConverter
    public static Level toLevel(String value) {
        try {
            return value == null ? null : Level.valueOf(value);
        } catch (IllegalArgumentException e) {
            return Level.MEDIUM; // Default value if conversion fails
        }
    }

    /**
     * Converts a Skill Level enum to string.
     * @param level The Level enum value
     * @return The string value, or null if the enum is null
     */
    @TypeConverter
    public static String fromLevel(Level level) {
        return level == null ? null : level.name();
    }

    /**
     * Converts a string to EmployeeStatus enum.
     * @param value The string value
     * @return The EmployeeStatus enum value, or null if the string is null
     */
    @TypeConverter
    public static EmployeeStatus toEmployeeStatus(String value) {
        try {
            return value == null ? null : EmployeeStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return EmployeeStatus.ACTIVE; // Default value if conversion fails
        }
    }

    /**
     * Converts an EmployeeStatus enum to string.
     * @param status The EmployeeStatus enum value
     * @return The string value, or null if the enum is null
     */
    @TypeConverter
    public static String fromEmployeeStatus(EmployeeStatus status) {
        return status == null ? null : status.name();
    }

    /**
     * Gets the display name for a Category.
     * @param category The Category enum value
     * @return The display name, or "Unknown" if the category is null
     */
    public static String getCategoryDisplayName(Category category) {
        if (category == null) return "Unknown";
        switch (category) {
            case BASIC:
                return "Basic Competency";
            case TECHNICAL:
                return "Technical Competency";
            case EMERGING:
                return "Emerging Skills";
            case POTENTIAL:
                return "Potential Development";
            default:
                return "Unknown";
        }
    }

    /**
     * Gets the display name for a Level.
     * @param level The Level enum value
     * @return The display name, or "Unknown" if the level is null
     */
    public static String getLevelDisplayName(Level level) {
        if (level == null) return "Unknown";
        switch (level) {
            case LOW:
                return "Low Priority";
            case MEDIUM:
                return "Medium Priority";
            case HIGH:
                return "High Priority";
            default:
                return "Unknown";
        }
    }

    /**
     * Gets the display name for an EmployeeStatus.
     * @param status The EmployeeStatus enum value
     * @return The display name, or "Unknown" if the status is null
     */
    public static String getStatusDisplayName(EmployeeStatus status) {
        if (status == null) return "Unknown";
        switch (status) {
            case ACTIVE:
                return "Active";
            case INACTIVE:
                return "Inactive";
            case ON_LEAVE:
                return "On Leave";
            case TRANSFERRED:
                return "Transferred";
            default:
                return "Unknown";
        }
    }

    /**
     * Gets the next level in the progression.
     * @param currentLevel The current Level enum value
     * @return The next Level, or the same level if already at highest
     */
    public static Level getNextLevel(Level currentLevel) {
        if (currentLevel == null) return Level.LOW;
        switch (currentLevel) {
            case LOW:
                return Level.MEDIUM;
            case MEDIUM:
                return Level.HIGH;
            case HIGH:
            default:
                return Level.HIGH;
        }
    }

    /**
     * Gets the previous level in the progression.
     * @param currentLevel The current Level enum value
     * @return The previous Level, or the same level if already at lowest
     */
    public static Level getPreviousLevel(Level currentLevel) {
        if (currentLevel == null) return Level.LOW;
        switch (currentLevel) {
            case HIGH:
                return Level.MEDIUM;
            case MEDIUM:
                return Level.LOW;
            case LOW:
            default:
                return Level.LOW;
        }
    }

    /**
     * Checks if a status is considered active.
     * @param status The EmployeeStatus to check
     * @return true if the status is considered active, false otherwise
     */
    public static boolean isActiveStatus(EmployeeStatus status) {
        return status == EmployeeStatus.ACTIVE || status == EmployeeStatus.ON_LEAVE;
    }
}
