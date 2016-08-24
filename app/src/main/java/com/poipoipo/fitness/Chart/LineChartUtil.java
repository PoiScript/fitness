package com.poipoipo.fitness.chart;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.poipoipo.fitness.R;
import com.poipoipo.fitness.data.Para;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LineChartUtil {
    private static final String TAG = "LineChartUtil";
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final List<LineChart> lineCharts = new ArrayList<>();
    private final Activity activity;
    private LineChart lineChart;
    private LineDataSet lineDataSet;

    public LineChartUtil(Activity activity) {
        lineCharts.add(Para.TYPE_BPM, (LineChart) activity.findViewById(R.id.bpm_chart));
        lineCharts.add(Para.TYPE_SPO2, (LineChart) activity.findViewById(R.id.spo2_chart));
        this.activity = activity;
    }

    public List<LineChart> getInstances() {
        return lineCharts;
    }

    public void refresh(int type, List<Para> paraList) {
        List<String> xVal = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        switch (type) {
            case Para.TYPE_BPM:
                for (Para para : paraList) {
                    entries.add(new Entry(para.getData(), index++));
                    xVal.add(format.format(new Date(para.getTime() * 1000L)));
                }
                lineChart = lineCharts.get(Para.TYPE_BPM);
                lineDataSet = new LineDataSet(entries, "心率");
                lineDataSet.setHighlightLineWidth(0.001f);
                chartsSettings(lineChart, Para.TYPE_BPM);
                break;
            case Para.TYPE_SPO2:
                int max = 0;
                int min = 233;
                for (Para para : paraList) {
                    if (para.getData() > max) {
                        max = para.getData();
                    } else if (para.getData() < min) {
                        min = para.getData();
                    }
                }
                float diff = (max - min) / 10;
                for (Para para : paraList) {
                    entries.add(new Entry((para.getData() - min) / diff + 85.8f, index++));
                    xVal.add(format.format(new Date(para.getTime() * 1000L)));
                }
                lineChart = lineCharts.get(Para.TYPE_SPO2);
                lineDataSet = new LineDataSet(entries, "血氧饱和度");
                lineDataSet.setHighlightLineWidth(0.001f);
                chartsSettings(lineChart, Para.TYPE_SPO2);
        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetSettings(lineDataSet);
        lineDataSetList.add(lineDataSet);
        LineData lineData = new LineData(xVal, lineDataSetList);
        lineChart.setData(lineData);
    }

    private void chartsSettings(LineChart lineChart, int type) {
        lineChart.setScaleYEnabled(false);
        lineChart.setExtraOffsets(25f, 10f, 25f, 15f);
        Legend legend = lineChart.getLegend();
        legend.setTextSize(20f);
        legend.setTextColor(Color.WHITE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setForm(Legend.LegendForm.CIRCLE);
        lineChart.setDescription("");
        lineChart.setNoDataText("No Data");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        lineChart.getAxisRight().setEnabled(false);
        switch (type) {
            case Para.TYPE_BPM:
                lineChart.setMarkerView(new BpmMarkerView(activity, lineChart));
                yAxis.setGridColor(Color.parseColor("#EF9A9A"));
                break;
            case Para.TYPE_SPO2:
                lineChart.setMarkerView(new Spo2MarkerView(activity, lineChart));
                yAxis.setGridColor(Color.parseColor("#9FA8DA"));
                break;
        }
        yAxis.setDrawLimitLinesBehindData(true);
        Log.d(TAG, "chartsSettings: ");
    }

    private void lineDataSetSettings(LineDataSet lineDataSet) {
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.WHITE);
    }

//    private void initChart(List<LineChart> charts) {
//        LineChart lineChart = lineCharts.get(Para.TYPE_BPM);
//        lineChart.setMarkerView(new BpmMarkerView(activity, lineChart));
//        LimitLine max = new LimitLine(BPM_NORMAL_MAX, "Normal: " + BPM_NORMAL_MIN + " - " + BPM_NORMAL_MAX);
//        LimitLine min = new LimitLine(BPM_NORMAL_MIN, "");
//        max.setTextColor(Color.parseColor("#FFCDD2"));
//        min.setLineColor(Color.parseColor("#FFCDD2"));
//        max.setLineColor(Color.parseColor("#FFCDD2"));
//        lineChart.getAxisLeft().addLimitLine(max);
//        lineChart.getAxisLeft().addLimitLine(min);
//        lineChart = lineCharts.get(Para.TYPE_SPO2);
//        lineChart.setMarkerView(new BpmMarkerView(activity, lineChart));
//        max = new LimitLine(SPO2_NORMAL_MAX, "Normal: " + SPO2_NORMAL_MIN + " - " + SPO2_NORMAL_MAX);
//        min = new LimitLine(SPO2_NORMAL_MIN, "");
//        max.setTextColor(Color.parseColor("#C5CAE9"));
//        min.setLineColor(Color.parseColor("#C5CAE9"));
//        max.setLineColor(Color.parseColor("#C5CAE9"));
//        lineChart.getAxisLeft().addLimitLine(max);
//        lineChart.getAxisLeft().addLimitLine(min);
//        for (LineChart chart : charts) {
//            chart.setScaleYEnabled(false);
//            chart.setExtraOffsets(25f, 10f, 25f, 15f);
//            chart.setDescription("");
//            chart.setNoDataText("No Data");
//            chart.getAxisRight().setEnabled(false);
//            Legend legend = chart.getLegend();
//            legend.setTextSize(20f);
//            legend.setTextColor(Color.WHITE);
//            legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
//            legend.setForm(Legend.LegendForm.CIRCLE);
//            XAxis xAxis = chart.getXAxis();
//            xAxis.setAxisLineColor(Color.WHITE);
//            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//            xAxis.setDrawGridLines(false);
//            xAxis.setTextColor(Color.WHITE);
//            YAxis yAxis = chart.getAxisLeft();
//            yAxis.setDrawGridLines(false);
//            yAxis.setDrawAxisLine(false);
//            yAxis.setDrawLabels(false);
//        }
//    }
//
//    private void initDataSet(List<LineDataSet> dataSets) {
//        for (LineDataSet dataSet : dataSets) {
//            dataSet.setDrawValues(false);
//            dataSet.setLineWidth(2);
//            dataSet.setColor(Color.WHITE);
//            dataSet.setCircleColor(Color.WHITE);
//        }
//    }
}
