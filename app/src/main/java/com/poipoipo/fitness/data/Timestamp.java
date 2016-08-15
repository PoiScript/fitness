package com.poipoipo.fitness.data;

import java.util.Calendar;
import java.util.TimeZone;

public class Timestamp {
    private Calendar calendar;
    private Long timestamp;
    private int todayTimestamp;

    public Timestamp(Calendar calendar) {
        this.calendar = calendar;
        calendar.setTimeZone(TimeZone.getDefault());
        moveToBeginning();
        timestamp = calendar.getTimeInMillis() / 1000;
        todayTimestamp = timestamp.intValue();
    }

    public int getCurrentTimestamp() {
        timestamp = Calendar.getInstance().getTimeInMillis() / 1000;
        return timestamp.intValue();
    }

    public int getTodayTimestamp() {
        return todayTimestamp;
    }

    public int getDayTimestamp(Calendar calendar) {
        this.calendar = calendar;
        moveToBeginning();
        timestamp = calendar.getTimeInMillis() / 1000;
        return timestamp.intValue();
    }

    private void moveToBeginning() {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
