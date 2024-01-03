package com.example.eggdetector.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        return dateFormat.format(date);
    }

    public static String formatDateWeekly(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM - ");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // Start of the week

        Date startTime = calendar.getTime();

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endTime = calendar.getTime();

        SimpleDateFormat endDateFormat = new SimpleDateFormat("dd MMMM, yyyy");
        return dateFormat.format(startTime) + endDateFormat.format(endTime);


    }

    public static String formatDateByMonth(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
        return dateFormat.format(date);
    }

    public static String formatDateWithoutYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        return dateFormat.format(date);
    }

    public static String formatDateWithWeekday(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy");
        return dateFormat.format(date);
    }
}
