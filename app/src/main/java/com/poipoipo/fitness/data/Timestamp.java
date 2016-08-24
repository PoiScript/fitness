package com.poipoipo.fitness.data;

import java.util.Calendar;

public class Timestamp {

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
}
