package com.poipoipo.fitness.chart;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyXAxisValueFormatter implements XAxisValueFormatter {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    @Override
    public String getXValue(String s, int i, ViewPortHandler viewPortHandler) {
        format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        return format.format(new Date(Integer.parseInt(s)*1000L));
    }
}
