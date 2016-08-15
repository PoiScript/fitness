package com.poipoipo.fitness.chart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.poipoipo.fitness.data.Para;
import com.poipoipo.fitness.R;

public class LineChartUtil {
    private static final String TAG = "LineChartUtil";
    private List<LineChart> lineCharts = new ArrayList<>();
    private List<String> xVal = new ArrayList<>();
    private LineChart lineChart;
    private LineDataSet lineDataSet;
    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    final Handler handler;
    Legend legend;
    Activity activity;

    public LineChartUtil(Activity activity, Handler handler) {
        this.activity = activity;
        lineCharts.add(Para.TYPE_BPM, (LineChart) activity.findViewById(R.id.bpm_chart));
        lineCharts.add(Para.TYPE_TEMP, (LineChart) activity.findViewById(R.id.temp_chart));
        lineCharts.add(Para.TYPE_SPO2, (LineChart) activity.findViewById(R.id.spo2_chart));
        this.handler = handler;
        chartsSettings(lineCharts);
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    }

    public List<LineChart> getInstances() {
        return lineCharts;
    }

    public void refresh(int type, List<Para> paraList) {
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (Para para : paraList) {
            entries.add(new Entry(para.getData(), index++));
            xVal.add(format.format(new Date(para.getTime() * 1000L)));
        }
        switch (type) {
            case Para.TYPE_BPM:
                lineChart = lineCharts.get(Para.TYPE_BPM);
                lineDataSet = new LineDataSet(entries, "Heart Rate");
                lineDataSet.setHighLightColor(R.color.bpm);
                break;
            case Para.TYPE_TEMP:
                lineChart = lineCharts.get(Para.TYPE_TEMP);
                lineDataSet = new LineDataSet(entries, "Body Temperature");
                lineDataSet.setHighLightColor(R.color.temp);
                break;
            case Para.TYPE_SPO2:
                lineChart = lineCharts.get(Para.TYPE_SPO2);
                lineDataSet = new LineDataSet(entries, "Oxygen in the Blood");
                lineDataSet.setHighLightColor(R.color.spo2);
        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetSettings(lineDataSet);
        lineDataSetList.add(lineDataSet);
        LineData lineData = new LineData(xVal, lineDataSetList);
        lineChart.setData(lineData);
    }

    private void chartsSettings(List<LineChart> lineCharts) {
        lineCharts.get(Para.TYPE_BPM).setMarkerView(
                new MyMarkerView(activity, R.layout.marker_view_bpm, lineCharts.get(Para.TYPE_BPM)));
        lineCharts.get(Para.TYPE_TEMP).setMarkerView(
                new MyMarkerView(activity, R.layout.marker_view_temp, lineCharts.get(Para.TYPE_TEMP)));
        lineCharts.get(Para.TYPE_SPO2).setMarkerView(
                new MyMarkerView(activity, R.layout.marker_view_spo2, lineCharts.get(Para.TYPE_SPO2)));
        for (LineChart lineChart : lineCharts) {
            legend = lineChart.getLegend();
            legend.setTextSize(20f);
            legend.setTextColor(Color.WHITE);
            legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
            legend.setForm(Legend.LegendForm.CIRCLE);
            lineChart.setDescription("");
            lineChart.setNoDataText("No Data");
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGridColor(Color.parseColor("#ECEFF1"));
            xAxis.setTextColor(Color.WHITE);
            YAxis yAxis1 = lineChart.getAxisLeft();
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setEnabled(false);
            yAxis1.setTextColor(Color.WHITE);
            yAxis1.setGridColor(Color.parseColor("#ECEFF1"));
        }
    }

    private void lineDataSetSettings(LineDataSet lineDataSet) {
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.WHITE);
    }
}
