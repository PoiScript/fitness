package com.poipoipo.fitness.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.poipoipo.fitness.R;

public class BpmMarkerView extends MarkerView {
    private final Chart chart;
    private final TextView textView;

    public BpmMarkerView(Context context, Chart chart) {
        super(context, R.layout.marker_view_bpm);
        textView = (TextView) findViewById(R.id.marker_view_y_value);
        this.chart = chart;
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        textView.setText(new StringBuilder().append(Float.toString(entry.getVal())).append("\n").append(chart.getData().getXVals().get(entry.getXIndex())));
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
