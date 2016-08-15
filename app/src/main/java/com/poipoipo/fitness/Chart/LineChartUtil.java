package com.poipoipo.fitness.chart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import com.poipoipo.fitness.data.Para;
import com.poipoipo.fitness.ui.MainActivity;
import com.poipoipo.fitness.R;

public class LineChartUtil {
    private static final String TAG = "LineChartUtil";
    private List<LineChart> lineCharts = new ArrayList<>();
    private List<Entry> entryList = new ArrayList<>();
    private List<String> xVals = new ArrayList<>();
    private LineChart lineChart;
    private LineDataSet lineDataSet;
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
    }

    public List<LineChart> getInstances() {
        return lineCharts;
    }

    public void refresh(int type, List<Para> paraList) {
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (Para para : paraList) {
            entries.add(new Entry(para.getData(), index++));
            xVals.add(Integer.toString(para.getTime()));
        }
        switch (type) {
            case Para.TYPE_BPM:
                lineChart = lineCharts.get(Para.TYPE_BPM);
                lineDataSet = new LineDataSet(entries, "BPM");
                break;
            case Para.TYPE_TEMP:
                lineChart = lineCharts.get(Para.TYPE_TEMP);
                lineDataSet = new LineDataSet(entries, "Temperature");
                break;
            case Para.TYPE_SPO2:
                lineChart = lineCharts.get(Para.TYPE_SPO2);
                lineDataSet = new LineDataSet(entries, "SpO2");
        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetSettings(lineDataSet);
        lineDataSetList.add(lineDataSet);
        LineData lineData = new LineData(xVals, lineDataSetList);
        lineChart.setData(lineData);
    }


    private void chartsSettings(List<LineChart> lineCharts) {
        lineCharts.get(Para.TYPE_BPM).setMarkerView(new MyMarkerView(activity, R.layout.marker_view_bpm));
        lineCharts.get(Para.TYPE_TEMP).setMarkerView(new MyMarkerView(activity, R.layout.marker_view_temp));
        lineCharts.get(Para.TYPE_SPO2).setMarkerView(new MyMarkerView(activity, R.layout.marker_view_spo2));
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
            xAxis.setValueFormatter(new MyXAxisValueFormatter());
            xAxis.setTextColor(Color.WHITE);
            YAxis yAxis1 = lineChart.getAxisLeft();
            YAxis yAxis2 = lineChart.getAxisRight();
            yAxis2.setEnabled(false);
            yAxis1.setTextColor(Color.WHITE);
            yAxis1.setGridColor(Color.parseColor("#ECEFF1"));
        }
    }

    private void lineDataSetSettings(LineDataSet lineDataSet) {
//        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(Color.WHITE);
    }
}
