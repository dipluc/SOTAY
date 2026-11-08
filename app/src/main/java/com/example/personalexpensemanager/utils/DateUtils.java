package com.example.personalexpensemanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String MONTH_YEAR_FORMAT = "yyyy-MM";

    public static String getTodayDbDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getCurrentMonthYear() {
        SimpleDateFormat sdf = new SimpleDateFormat(MONTH_YEAR_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String formatToDisplayDate(String dbDate) {
        if (dbDate == null || dbDate.trim().isEmpty()) return "";
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat outFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date date = inFormat.parse(dbDate);
            return date != null ? outFormat.format(date) : dbDate;
        } catch (Exception e) {
            return dbDate;
        }
    }

    public static String formatToDbDate(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        SimpleDateFormat sdf = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
        return sdf.format(cal.getTime());
    }
}