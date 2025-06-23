package com.kemham.kartukompetensi.model;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Type converter for handling Date objects in Room database.
 */
public class DateConverter {

    /**
     * Converts a timestamp to Date object.
     * @param value The timestamp in milliseconds
     * @return The Date object, or null if the timestamp is null
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Converts a Date object to timestamp.
     * @param date The Date object to convert
     * @return The timestamp in milliseconds, or null if the date is null
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * Checks if a date is older than the specified number of days.
     * @param date The date to check
     * @param days The number of days
     * @return true if the date is older than the specified days, false otherwise
     */
    public static boolean isOlderThanDays(Date date, int days) {
        if (date == null) return true;
        long daysInMillis = (long) days * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - date.getTime() > daysInMillis;
    }

    /**
     * Checks if a date is older than the specified number of months.
     * @param date The date to check
     * @param months The number of months
     * @return true if the date is older than the specified months, false otherwise
     */
    public static boolean isOlderThanMonths(Date date, int months) {
        if (date == null) return true;
        // Approximate months in milliseconds (30 days per month)
        long monthsInMillis = (long) months * 30 * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - date.getTime() > monthsInMillis;
    }

    /**
     * Gets the number of days between two dates.
     * @param date1 The first date
     * @param date2 The second date
     * @return The number of days between the dates, or -1 if either date is null
     */
    public static int getDaysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) return -1;
        long diff = Math.abs(date2.getTime() - date1.getTime());
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Gets the number of months between two dates.
     * @param date1 The first date
     * @param date2 The second date
     * @return The approximate number of months between the dates, or -1 if either date is null
     */
    public static int getMonthsBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) return -1;
        long diff = Math.abs(date2.getTime() - date1.getTime());
        // Approximate months (30 days per month)
        return (int) (diff / (30L * 24 * 60 * 60 * 1000));
    }

    /**
     * Adds the specified number of days to a date.
     * @param date The date to add to
     * @param days The number of days to add
     * @return The new date, or null if the input date is null
     */
    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        return new Date(date.getTime() + ((long) days * 24 * 60 * 60 * 1000));
    }

    /**
     * Adds the specified number of months to a date.
     * @param date The date to add to
     * @param months The number of months to add
     * @return The new date, or null if the input date is null
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        // Approximate months (30 days per month)
        return new Date(date.getTime() + ((long) months * 30 * 24 * 60 * 60 * 1000));
    }

    /**
     * Gets the start of the day for a given date.
     * @param date The date to get the start of day for
     * @return The start of the day, or null if the input date is null
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) return null;
        long time = date.getTime();
        time = time - (time % (24 * 60 * 60 * 1000));
        return new Date(time);
    }

    /**
     * Gets the end of the day for a given date.
     * @param date The date to get the end of day for
     * @return The end of the day, or null if the input date is null
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) return null;
        long time = date.getTime();
        time = time - (time % (24 * 60 * 60 * 1000)) + (24 * 60 * 60 * 1000) - 1;
        return new Date(time);
    }

    /**
     * Checks if a date is today.
     * @param date The date to check
     * @return true if the date is today, false otherwise
     */
    public static boolean isToday(Date date) {
        if (date == null) return false;
        return getDaysBetween(date, new Date()) == 0;
    }

    /**
     * Checks if a date is in the future.
     * @param date The date to check
     * @return true if the date is in the future, false otherwise
     */
    public static boolean isFuture(Date date) {
        if (date == null) return false;
        return date.after(new Date());
    }

    /**
     * Checks if a date is in the past.
     * @param date The date to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isPast(Date date) {
        if (date == null) return false;
        return date.before(new Date());
    }
}
