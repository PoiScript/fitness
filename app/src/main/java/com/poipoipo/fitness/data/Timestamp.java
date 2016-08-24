package com.poipoipo.fitness.data;

import java.util.Calendar;
import java.util.TimeZone;

public class Timestamp {
    private Calendar calendar;
    private Long timestamp;

    public Timestamp(Calendar calendar) {
        this.calendar = calendar;
        calendar.setTimeZone(TimeZone.getDefault());
        moveToBeginning();
        timestamp = calendar.getTimeInMillis() / 1000;
        int todayTimestamp = timestamp.intValue();
    }

    public static int getTodayTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long timestamp = calendar.getTimeInMillis() / 1000;
        return timestamp.intValue();
    }

    public static int getTimestampByLong(Long longer) {
        Long shorter = longer / 1000;
        return shorter.intValue();
    }

    public static int getDayTimestampCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long timestamp = calendar.getTimeInMillis() / 1000;
        return timestamp.intValue();
    }

    public int getCurrentTimestamp() {
        timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        return timestamp.intValue();
    }

    private void moveToBeginning() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
