package com.example.smoiapp001.utilities;

import android.arch.persistence.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    // Constant for normal date format
    public static final String DATE_FORMAT_NORMAL = "dd/MM/yyyy";
    // Constant for obvious date format
    public static final String DATE_FORMAT_OBVIOUS = "EEE, d MMM yyyy HH:mm:ss";

    // Constant for a day in millisecond
    public static final long DAY_IN_MILLISECOND = 86400000;
    public static final long WEEK_IN_MILLISECOND = 7 * DAY_IN_MILLISECOND;
    // Assume that one month is 30 days
    public static final long MONTH_IN_MILLISECOND = 30 * DAY_IN_MILLISECOND;


    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    public static int getDayByNormalFormat(String date) {
        String day;
        if (date.substring(0,1).equals("0")) {
            day = date.substring(1,2);
        }
        else {
            day = date.substring(0,2);
        }
        return Integer.parseInt(day);
    }

    public static int getMonthByNormalFormat(String date) {
        String monthString;
        if (date.substring(3,4).equals("0")) {
            monthString = date.substring(4,5);
        }
        else {
            monthString = date.substring(3,5);
        }
        int month = Integer.parseInt(monthString);
        month--;
        return month;
    }

    public static int getYearByNormalFormat(String date) {
        String year;
        year = date.substring(6,10);
        return Integer.parseInt(year);
    }

    public static SimpleDateFormat getNormalDateFormat(){

        return new SimpleDateFormat(DateConverter.DATE_FORMAT_NORMAL, Locale.getDefault());
    }

    public static SimpleDateFormat getObviousDateFormat(){

        return new SimpleDateFormat(DateConverter.DATE_FORMAT_OBVIOUS, Locale.getDefault());
    }

    public static String buildNormalDateString(int year, int month, int day) {
        month++;
        String dayString = Integer.toString(day);
        String monthString = Integer.toString(month);

        if (day < 10) {
            dayString = "0" + dayString;
        }

        if (month < 10) {
            monthString = "0" + monthString;
        }
        String formedDate = dayString + "/" + monthString + "/" + year;

        return formedDate;
    }

}
