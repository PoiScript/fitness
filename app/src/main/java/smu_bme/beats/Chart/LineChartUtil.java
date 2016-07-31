package smu_bme.beats.Chart;

import android.app.Activity;
import android.os.Message;
import android.os.Handler;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import smu_bme.beats.Data.Para;
import smu_bme.beats.MainActivity;
import smu_bme.beats.R;

public class LineChartUtil {
    private List<LineChart> lineChartList = new ArrayList<>();
    private List<Entry> entryList = new ArrayList<>();
    private List<String> xVals = new ArrayList<>();
    private LineChart lineChart;
    private LineDataSet lineDataSet;
    final Handler handler;

    public LineChartUtil(Activity activity, Handler handler) {
        lineChartList.add((LineChart) activity.findViewById(R.id.chart));
        this.handler = handler;
    }

    public List<LineChart> getInstances() {
        return lineChartList;
    }

    public void refresh(int type, List<Para> paraList) {
        int index = 0;
        for (Para para : paraList) {
            entryList.add(new Entry(para.getData(), index++));
            xVals.add(Integer.toString(para.getDate()));
        }
        switch (type) {
            case Para.TYPE_PACE:
                lineChart = lineChartList.get(0);
                lineDataSet = new LineDataSet(entryList, "Pace");
                break;
        }
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> lineDataSetList = new ArrayList<>();
        lineDataSetList.add(lineDataSet);
        LineData lineData = new LineData(xVals, lineDataSetList);
        lineChart.setData(lineData);
        handler.obtainMessage(MainActivity.REFRESH_DONE).sendToTarget();
    }
}
