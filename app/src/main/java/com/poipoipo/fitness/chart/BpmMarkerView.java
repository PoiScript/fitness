package com.poipoipo.fitness.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import com.poipoipo.fitness.R;

public class BpmMarkerView extends MarkerView {
    private TextView textView;

    public BpmMarkerView(Context context, int layoutRes){
        super(context, layoutRes);
        textView = (TextView) findViewById(R.id.marker_view_y_value);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        textView.setText(Float.toString(entry.getVal()));
    }

    @Override
    public int getXOffset(float v) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float v) {
        return -getHeight();
    }
}
