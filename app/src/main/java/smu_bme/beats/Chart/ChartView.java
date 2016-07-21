package smu_bme.beats.Chart;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import smu_bme.beats.DbData;
import smu_bme.beats.Database.DbHelper;
import smu_bme.beats.R;

/**
 * Created by gollyrui on 4/30/16.
 */
public class ChartView extends View {
    static private Calendar ShowedDate;
    private Context context;
    private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    private DbHelper dbHelper;
    private BarChart weekChart;
    private LineChart instantChart;
    private LineChart dayChart;
    private ArrayList<String> xVals = new ArrayList<>();
    private boolean alternative = false;


    public ChartView(Context context, BarChart weekView, LineChart dayView, LineChart instantView) {
        super(context);
        {
            xVals.add(context.getString(R.string.sunday));
            xVals.add(context.getString(R.string.monday));
            xVals.add(context.getString(R.string.tuesday));
            xVals.add(context.getString(R.string.wednesday));
            xVals.add(context.getString(R.string.thursday));
            xVals.add(context.getString(R.string.friday));
            xVals.add(context.getString(R.string.saturday));
        }
        this.context = context;
        dbHelper = new DbHelper(context);
        /** Debug begins*/
        Log.d(Thread.currentThread().getName(), "manually insert");
        DbData tmp = dbHelper.queryDate("2016-05-07");
        if (tmp == null) {
            dbHelper.insertData(new DbData("2016-05-07", "9:30", 120));
            dbHelper.insertData(new DbData("2016-05-07", "9:45", 110));
            dbHelper.insertData(new DbData("2016-05-07", "10:00", 130));
            dbHelper.insertData(new DbData("2016-05-07", "10:15", 150));
            dbHelper.insertData(new DbData("2016-05-07", "10:30", 130));
            dbHelper.insertData(new DbData("2016-05-07", "10:45", 150));
            dbHelper.insertData(new DbData("2016-05-07", "11:00", 110));
            dbHelper.insertData(new DbData("2016-05-07", "11:15", 140));
            dbHelper.insertData(new DbData("2016-05-07", "11:30", 160));
            dbHelper.insertData(new DbData("2016-05-07", "11:45", 120));
            dbHelper.insertData(new DbData("2016-05-07", "12:00", 110));
            dbHelper.insertData(new DbData("2016-05-07", "12:15", 130));
            dbHelper.insertData(new DbData("2016-05-07", "12:30", 150));
            dbHelper.insertData(new DbData("2016-05-07", "12:45", 130));
            dbHelper.insertData(new DbData("2016-05-07", "13:00", 150));
            dbHelper.insertData(new DbData("2016-05-07", "13:15", 110));
            dbHelper.insertData(new DbData("2016-05-07", "13:30", 140));
            dbHelper.insertData(new DbData("2016-05-07", "13:45", 160));
            dbHelper.insertData(new DbData("2016-05-07", "14:00", 120));
            dbHelper.insertData(new DbData("2016-05-07", "14:15", 110));
            dbHelper.insertData(new DbData("2016-05-07", "14:30", 130));
            dbHelper.insertData(new DbData("2016-05-07", "14:45", 150));
            dbHelper.insertData(new DbData("2016-05-07", "15:00", 130));
            dbHelper.insertData(new DbData("2016-05-07", "15:15", 150));
            dbHelper.insertData(new DbData("2016-05-07", "15:30", 110));
            dbHelper.insertData(new DbData("2016-05-07", "15:45", 140));
            dbHelper.insertData(new DbData("2016-05-07", "16:00", 160));
            dbHelper.insertData(new DbData("2016-05-07", "16:15", 120));
            dbHelper.insertData(new DbData("2016-05-07", "16:30", 110));
            dbHelper.insertData(new DbData("2016-05-07", "16:45", 130));
            dbHelper.insertData(new DbData("2016-05-07", "17:00", 150));
            dbHelper.insertData(new DbData("2016-05-07", "17:15", 130));
            dbHelper.insertData(new DbData("2016-05-07", "17:30", 150));
            dbHelper.insertData(new DbData("2016-05-07", "17:45", 110));
            dbHelper.insertData(new DbData("2016-05-07", "18:00", 140));
            dbHelper.insertData(new DbData("2016-05-07", "18:15", 160));
        } else {
            Log.d(Thread.currentThread().getName(), "Date:" + tmp.getDate() + " avgBPM" + tmp.getAvgBPM() + " Pace" + tmp.getPace());
        }
        /** Debug ends*/


        {
            /**Initialize Chart reference*/
            this.weekChart = weekView;

            this.dayChart = dayView;
            //TODO
            this.instantChart = instantView;
//            Log.d(Thread.currentThread().getName(),"instant CHart exists:"+String.valueOf(this.instantChart!=null));
        }
        //call week chart according to selected value
        weekChart.setOnChartValueSelectedListener(
                new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry entry, int i, Highlight highlight) {
//                            Log.d("DEBUGGING","XIndex:"+String.valueOf(1+entry.getXIndex()));
                        postDay(setDay(ChartView.ShowedDate, entry.getXIndex() + 1));
//                            Calendar.MONDAY
                    }

                    @Override
                    public void onNothingSelected() {
//                        dayChart.clear();
//                        dayChart.
////                        PieData pieData = new PieData();
//                        dayChart.setData(new PieData());
//                        dayChart.notifyDataSetChanged();
//                        dayChart.postInvalidate();
                    }
                }
        );


        {
            /** Week Chart*/
            XAxis xAxis = weekChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setAxisMinValue(-0.5f);
            YAxis yAxisRight = weekChart.getAxisRight();
            yAxisRight.setEnabled(false);
            YAxis yAxisLeft = weekChart.getAxisLeft();
            yAxisLeft.setAxisMinValue(0f);
            yAxisLeft.setSpaceTop(20f);
//            yAxisLeft.setAxisMaxValue(60f);
//            yAxisLeft.setSpaceBottom(10f);
            yAxisLeft.setLabelCount(6, false);

            weekChart.setScaleEnabled(false);
            weekChart.setDrawValueAboveBar(true);
            weekChart.setScaleEnabled(false);
            weekChart.setHighlightPerTapEnabled(true);
            {
                Legend l = weekChart.getLegend();
                l.setEnabled(false);
            }
//            weekChart.setBackgroundColor(0xFFF5F5F5);
            weekChart.animateY(1500);
            weekChart.animateX(2500);
            weekChart.setAlpha(0.9f);
            weekChart.setNoDataText(context.getString(R.string.no_found_on_week));
//            weekChart.setNoDataTextDescription(context.getString(R.string.get_to_start));
            weekChart.setDescription(context.getString(R.string.record_week));  // set the description
            weekChart.setDescriptionTextSize(20f);
            weekChart.setDescriptionPosition(850, 50);
        }
//        {
//            /** Instant Chart  */
//            XAxis xAxis = instantView.getXAxis();
//            xAxis.setEnabled(false);
//            xAxis.disableGridDashedLine();
//            xAxis.setDrawGridLines(false);
//            YAxis yAxisRight = instantChart.getAxisRight();
//            yAxisRight.setEnabled(false);
//            YAxis yAxisLeft = instantChart.getAxisLeft();
//            yAxisLeft.setAxisMinValue(-200f);
//            yAxisLeft.setAxisMaxValue(800f);
//            yAxisLeft.setSpaceTop(10f);
////          yAxisLeft.setAxisMaxValue(60f);
////          yAxisLeft.setSpaceBottom(10f);
//            yAxisLeft.setLabelCount(10, false);
//            {
//                Legend l = instantChart.getLegend();
//                l.setEnabled(false);
//            }
////            instantChart.animateY(1500);
//            instantChart.animateX(2500);
//            instantChart.setAlpha(0.9f);
//            instantChart.setNoDataText(context.getString(R.string.no_data));
//            instantChart.setNoDataTextDescription(context.getString(R.string.connect_to_bluetooth));
////            instantChart.setDescription(context.getString(R.string.instant_chart));  // set the description
////            instantChart.setDescriptionTextSize(15);
////            instantChart.setDescriptionPosition(350, 200);
////            instantChart.setDescriptionColor(Color.WHITE);
//            instantChart.setScaleEnabled(false);
//
//        }


        {
            /** day Chart*/
            {
                Legend l = dayView.getLegend();
                l.setEnabled(false);
            }
            XAxis xAxis = dayChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            YAxis yAxisRight = dayChart.getAxisRight();
            yAxisRight.setEnabled(false);
            YAxis yAxisLeft = dayChart.getAxisLeft();
            yAxisLeft.setSpaceTop(30f);
//            yAxisLeft.setAxisMaxValue(60f);
//            yAxisLeft.setSpaceBottom(10f);

            dayChart.setNoDataText(context.getString(R.string.no_found_on_day) + " today");
            dayChart.setNoDataTextDescription(context.getString(R.string.get_to_start));
            dayChart.setScaleEnabled(false);
            dayChart.setDescriptionTextSize(20f);
            dayChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
            dayChart.setExtraOffsets(5, 5, 5, 5);
            dayChart.setDragDecelerationFrictionCoef(0.95f);

            dayChart.setAlpha(1f);
//            dayChart.setBackgroundColor(0xFFF5F5F5);

//
            dayChart.setHighlightPerTapEnabled(false);
            dayChart.setDescriptionPosition(900, 50);

            // add a selection listener
            // mPieChart.setOnChartValueSelectedListener(this);
        }
        setWeek(Calendar.getInstance(), 2);
        setDay(Calendar.getInstance(), 0);

    }

    public void dynamicAdd(int bpm, int threshold) {

        LineData instantBpmData = instantChart.getLineData();
        Log.d(Thread.currentThread().getName(), "dynamicAdd- bpm:" + bpm);
        ILineDataSet instantBpmDataSet;
        LineDataSet newInstantBpmDataSet;
        /**if lineData does not exist*/
        if (instantBpmData == null) {
            instantChart.setData(new LineData());
            instantBpmData = instantChart.getLineData();
            newInstantBpmDataSet = new LineDataSet(null, context.getString(R.string.day_chart));
            newInstantBpmDataSet.setDrawCubic(true);
            newInstantBpmDataSet.setDrawCircles(false);
            instantBpmData.addDataSet(newInstantBpmDataSet);
        }
//            Log.d(Thread.currentThread().getName(), "LineData is not null");
        instantBpmDataSet = instantBpmData.getDataSetByIndex(0);
        Log.d(Thread.currentThread().getName(), "Instant BPM Data Set exists:" + String.valueOf(instantBpmDataSet != null));
        int count;
        count = instantBpmDataSet.getEntryCount();
        if (count > threshold) {
//            Log.d(Thread.currentThread().getName(), "Clear chart");
            instantChart.post(new Runnable() {
                @Override
                public void run() {
                    instantChart.clear();
                }
            });
            count = 0;
            instantBpmDataSet = new LineDataSet(null, context.getString(R.string.day_chart));
            instantBpmData.addDataSet(instantBpmDataSet);
        }
        instantBpmData.addXValue(String.valueOf(count));
        instantBpmData.addEntry(new Entry(bpm, count), 0);
        instantBpmData.setDrawValues(false);
        /** set axis*/
        XAxis xAxis = instantChart.getXAxis();
        xAxis.resetAxisMaxValue();
        xAxis.setAxisMaxValue((float) threshold);
        xAxis.resetAxisMinValue();
        xAxis.setAxisMinValue(0f);

//        instantChart.setDescription(context.getString(R.string.instant_chart));
        instantChart.notifyDataSetChanged();
        instantChart.postInvalidate();

    }

    public void rollingAdd(int bpm, int threshold) {
        LineData rollingBpmData = instantChart.getLineData();
        LineDataSet newInstantBpmDataSet;
        if (rollingBpmData == null) {
            /** the first time*/
            instantChart.setData(new LineData());
            rollingBpmData = instantChart.getLineData();
            newInstantBpmDataSet = new LineDataSet(null, context.getString(R.string.day_chart));
            newInstantBpmDataSet.setDrawCubic(true);
            newInstantBpmDataSet.setDrawCircles(false);
            rollingBpmData.addDataSet(newInstantBpmDataSet);
        }
        if (rollingBpmData.getDataSetCount() == 1) {
            ILineDataSet instantBpmDataSet = rollingBpmData.getDataSetByIndex(0);
            int count = instantBpmDataSet.getEntryCount();
            if (count == threshold) {
                newInstantBpmDataSet = new LineDataSet(null, context.getString(R.string.day_chart));
                newInstantBpmDataSet.setDrawCubic(true);
                newInstantBpmDataSet.setDrawCircles(false);
                rollingBpmData.addDataSet(newInstantBpmDataSet);
            }
            rollingBpmData.addXValue(String.valueOf(count));
            rollingBpmData.addEntry(new Entry(bpm, count), 0);
        } else if (alternative) {
            /** alternative*/
            ILineDataSet oldBpmDataSet = rollingBpmData.getDataSetByIndex(1);
            ILineDataSet newBpmDataSet = rollingBpmData.getDataSetByIndex(0);
            oldBpmDataSet.removeFirst();
            int count = newBpmDataSet.getEntryCount();
            rollingBpmData.addXValue(String.valueOf(count));
            rollingBpmData.addEntry(new Entry(bpm, count), 1);
            //TODO
            if (count == threshold) {
                oldBpmDataSet.clear();
                alternative = false;
            }

        } else {
            /** */
            ILineDataSet oldBpmDataSet = rollingBpmData.getDataSetByIndex(0);
            ILineDataSet newBpmDataSet = rollingBpmData.getDataSetByIndex(1);
            oldBpmDataSet.removeFirst();
            int count = newBpmDataSet.getEntryCount();
            rollingBpmData.addXValue(String.valueOf(count));
            rollingBpmData.addEntry(new Entry(bpm, count), 0);
            if (count == threshold) {
                oldBpmDataSet.clear();
                alternative = true;
            }
        }
//        Log.d(Thread.currentThread().getName(), "-rolling- count:" + count);
//        Log.d(Thread.currentThread().getName(), "Clear chart");

//        Log.d(Thread.currentThread().getName(), "-rolling- count after add:" + instantBpmDataSet.getEntryCount());
//        Log.d(Thread.currentThread().getName(), "-rolling- entry:" + entry);

        instantChart.setDescription(context.getString(R.string.instant_chart));
        instantChart.notifyDataSetChanged();
        instantChart.postInvalidate();

    }

    public BarData setWeek(final Calendar ShowedDate, int mode) {
        this.ShowedDate = (Calendar) ShowedDate.clone();
//        this.ShowedDate = ShowedDate;
        Calendar ShowedCalendar = ShowedDate;
//        Log.d(Thread.currentThread().getName(),"setWeek date:"+new SimpleDateFormat("yyyy-MM-dd").format(this.ShowedDate.getTime()));
//        Log.d("setWeek", "mode:" + mode);
        ArrayList<BarEntry> paceEntry = new ArrayList<>();
        ArrayList<BarEntry> avgBpmEntry = new ArrayList<>();
        BarData weekData;
        BarDataSet avgBpmDataSet;
        BarDataSet paceDataSet;
        for (int i = 1; i < 8; i++) {
            ShowedCalendar.set(Calendar.DAY_OF_WEEK, i);
            String date = f.format(ShowedCalendar.getTime());
//            Log.d("DEBUGGING",date);
            //TODO pace and avgBPM
            DbData dbData = dbHelper.queryDate(date);
//            try {
//                Log.d(Thread.currentThread().getName(), "Date:" + dbData.getDate() + " " + "BPM:" + dbData.getAvgBPM() + " Pace:" + dbData.getPace());
//            } catch (Exception e) {
//                Log.d(Thread.currentThread().getName(), "Date:" + date + " No data found");
//            }
            /** avgBPM */
//            Log.d("DEBUGGING","SUM:"+String.valueOf(sum));
            if (dbData != null) {
                BarEntry avgBPM = new BarEntry(dbData.getAvgBPM(), i - 1);
//                Log.d(Thread.currentThread().getName(),"avg BPM:"+dbData.getAvgBPM());
                avgBpmEntry.add(avgBPM);

                /** pace  */
                BarEntry pace = new BarEntry(dbData.getPace(), i - 1);
                paceEntry.add(pace);
            }
//            Log.d(Thread.currentThread().getName(),"setWeek loop date:"+new SimpleDateFormat("yyyy-MM-dd").format(this.ShowedDate.getTime()));
        }
//        Log.d(Thread.currentThread().getName(),"setWeek end date:"+new SimpleDateFormat("yyyy-MM-dd").format(this.ShowedDate.getTime()));
        avgBpmDataSet = new BarDataSet(avgBpmEntry, context.getString(R.string.avg_bpm));
        avgBpmDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        avgBpmDataSet.setValueTextSize(12f);
        avgBpmDataSet.setHighlightEnabled(true);
        avgBpmDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS); //JOYFUL_COLORS)VORDIPLOM_COLORS)
//        avgBpmDataSet.setDrawCubic(true);
//        avgBpmDataSet.setDrawCircles(false);

        paceDataSet = new BarDataSet(paceEntry, context.getString(R.string.pace));
        paceDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        paceDataSet.setValueTextSize(12f);
        paceDataSet.setHighlightEnabled(true);
        paceDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
//        paceDataSet.setDrawCubic(true);
//        paceDataSet.setDrawCircles(false);

        ArrayList<IBarDataSet> weekDataSets = new ArrayList<>();
        if (mode == 0) {
            weekDataSets.add(avgBpmDataSet);
            if (weekDataSets.get(0).getEntryCount() == 0) {
                Log.d(Thread.currentThread().getName(), "dataSet null");
                weekData = null;
            } else {
                weekData = new BarData(xVals, weekDataSets);
            }
        } else if (mode == 1) {
            weekDataSets.add(paceDataSet);
            if (weekDataSets.get(0).getEntryCount() == 0) {
                Log.d(Thread.currentThread().getName(), "dataSet null");
                weekData = null;
            } else {
                weekData = new BarData(xVals, weekDataSets);
            }
        } else {
            weekDataSets.add(avgBpmDataSet);
            weekDataSets.add(paceDataSet);
            if (weekDataSets.get(0).getEntryCount() == 0 && weekDataSets.get(1).getEntryCount() == 0) {
                Log.d(Thread.currentThread().getName(), "dataSet null");
                weekData = null;
            } else {
                weekData = new BarData(xVals, weekDataSets);
            }
        }
        postDay(setDay(this.ShowedDate, 0));
        return weekData;
    }

    public void postWeek(BarData weekData) {
        if (weekData == null) {
            weekChart.post(new Runnable() {
                @Override
                public void run() {
                    weekChart.clear();
                }
            });
        } else {
            weekChart.setData(weekData);
        }
        weekChart.notifyDataSetChanged();
        weekChart.postInvalidate();
    }

    private LineData setDay(Calendar ShowedDate, final int dayOfWeek) {
        Calendar ShowedCalendar = ShowedDate;
//        Log.d(Thread.currentThread().getName(),"setDay date:"+new SimpleDateFormat("yyyy-MM-dd").format(ShowedDate.getTime()));
        if (dayOfWeek > 0 && dayOfWeek < 8) {
            ShowedCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        }
//        Calendar.getInstance().set;
        ArrayList<String> dayLabels = new ArrayList<>();
        ArrayList<Entry> dayEntry = new ArrayList<>();
        String date = f.format(ShowedCalendar.getTime());
//        Log.d(Thread.currentThread().getName(), "Set Day:" + date + " dayOfWeek: " + dayOfWeek);
//        Log.d("DEBUGGING",date);
        List<DbData> dbDatas = dbHelper.queryForVisualization(date);
        Iterator<DbData> unit = dbDatas.iterator();
        int i = 0;
        while (unit.hasNext()) {
            DbData dbData = unit.next();
            dayLabels.add(dbData.getTime());
            dayEntry.add(new Entry(dbData.getBPM(), i));
            i++;
        }

        LineDataSet dayDataSet = new LineDataSet(dayEntry, context.getString(R.string.day_chart));
        dayDataSet.setValueTextSize(15);
        dayDataSet.setHighlightEnabled(true);
        dayDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dayDataSet.setDrawCubic(true);
        dayDataSet.setDrawCircles(false);
        dayDataSet.setDrawValues(false);

        LineData dayData;
        if (dayDataSet.getEntryCount() == 0) {
            dayData = null;
            Log.d("date", date);
            Log.d("today", f.format(Calendar.getInstance().getTime()));
            Log.d("date equals today", (date.equals(f.format(Calendar.getInstance().getTime()))) + " ");
            if (date.equals(f.format(Calendar.getInstance().getTime()))) {
                Log.i(Thread.currentThread().getName(), "today");
                dayChart.setNoDataText(context.getString(R.string.no_found_on_day) + " today");
                dayChart.setNoDataTextDescription(context.getString(R.string.get_to_start));
            } else {
                Log.i(Thread.currentThread().getName(), "not today");
                dayChart.setNoDataText(context.getString(R.string.no_found_on_day) + " " + date);
                dayChart.setNoDataTextDescription(null);
            }

        } else {
            dayData = new LineData(dayLabels, dayDataSet);
        }
        dayChart.setDescription(date);
        return dayData;
    }


    public void postDay(LineData dayData) {
        if (dayData == null) {
            dayChart.clear();
        } else {
            dayChart.setData(dayData);
        }
        dayChart.notifyDataSetChanged();
        dayChart.postInvalidate();
    }

//    public void getLogChart(Calendar date) {
//        setWeek(date);
//        setDay(date,-1);
//    }
}