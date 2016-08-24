package com.poipoipo.fitness.chart;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.poipoipo.fitness.R;

public class TempMarkerView extends MarkerView {
    private Chart chart;
    private TextView textView;

    public TempMarkerView(Context context, Chart chart) {
        super(context, R.layout.marker_view_temp);
        textView = (TextView) findViewById(R.id.marker_view_y_value);
        this.chart = chart;
    }
    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        textView.setText(Float.toString(entry.getVal()) + "°C\n" + chart.getData().getXVals().get(entry.getXIndex()) +  "");
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
