package smu_bme.beats.Chart;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;

import java.util.Calendar;


//package smu_bme.beats.Chart;

/**
 * Created by bme-lab1 on 5/2/16.
 */
public class ChartThread {
    static Handler uiHandler;
    static Handler chartHandler;
    static ChartHandlerThread chartHandlerThread;// an extension of chartThread
//    static private View view;


    public void init(final Context context, BarChart weekView, LineChart dayView, LineChart instantView) {
//        Log.d("DEBUGGING", Thread.currentThread().getName() + "-Constructor- Start!");
        chartHandlerThread = new ChartHandlerThread("ChartThread");
        chartHandlerThread.chartView = new smu_bme.beats.Chart.ChartView(context,weekView,dayView,instantView);
//        view = chartHandlerThread.chartView.getLogChart(Calendar.getInstance());

        /**First run worker thread*/

//        ChartHandlerThreadBak chartHandlerThreadBak = new ChartHandlerThreadBak(inflater, container, context);
//        synchronized (chartHandlerThread) {
        ////////////////////////////////////
        chartHandlerThread.start();
        chartHandler = new Handler(chartHandlerThread.getLooper(), chartHandlerThread);
        chartHandler.post(new Runnable() {
            /**---------------------------------------------------------------
                                         Chart Thread
             ---------------------------------------------------------------*/
            @Override
            public void run() {
                uiHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        /**------------------------------------------------------------
                                    Message from Chart Thread to Main Thread
                         In Main Thread
                         ------------------------------------------------------------*/
                        super.handleMessage(msg);
//                        Log.d(Thread.currentThread().getName() , "-handleMessage-Start");
//                            synchronized (chartHandlerThread) {
                        if (msg.what == 0) {
                            /**Statistic Views */
                            BarData weekData = (BarData) msg.obj;
                            chartHandlerThread.chartView.postWeek(weekData);
//                            Log.d("DEBUGGING","");
                        } else if (msg.what == 1) {
                            /** Instant view*/
//                            Log.d("DEBUGGING", Thread.currentThread().getName() + "-handleMessage-valid message for view");
                            int instantBpm = (int) msg.obj;
//                            Log.d(Thread.currentThread().getName(),"-default mode-"+instantBpm);
                            chartHandlerThread.chartView.dynamicAdd(instantBpm, 300);
//                            Log.d("DEBUGGING", Thread.currentThread().getName() + "-handleMessage-view exists: " + String.valueOf(view != null));
                        } else if (msg.what == 2) {
                            /** Instant view*/
                            int instantBpm = (int) msg.obj;
                            chartHandlerThread.chartView.rollingAdd(instantBpm, 300);
                        }
//                                try {
//                        Log.d( Thread.currentThread().getName() , "-handleMessage- Ended ");
//                                notify();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                    }
//                        }
                };
//                Log.d( Thread.currentThread().getName() , "-chartView Exists- " + String.valueOf(chartHandlerThread.chartView != null));
            }

        });//Runnable ended
        /**------------------------------------------------------------
                             Back to Main Thread
         ------------------------------------------------------------ */
//        synchronized (chartHandlerThread) {
//            try {
//                Log.d("DEBUGGING", Thread.currentThread().getName() + "-Constructor- waiting");
//                chartHandlerThread.notify();
//                wait();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        }
//        Log.d("DEBUGGING", Thread.currentThread().getName() + "-Constructor- Finished");
    }


    public void refreshLogChart(Calendar ShowedCalendar,int mode) {
//        Log.d("DEBUGGING", Thread.currentThread().getName() + "-refreshDate-chartView exists: " + String.valueOf(chartHandlerThread.chartView != null));
//        Log.d("DEBUGGING", "UIThread refreshDate:" + ShowedCalendar.toString());

//        Log.d(Thread.currentThread().getName(),"refreshLogChart date:"+new SimpleDateFormat("yyyy-MM-dd").format(ShowedCalendar.getTime()));
        Message m = new Message();
        /** return different LineData according to value of mode */
        m.obj = chartHandlerThread.chartView.setWeek(ShowedCalendar,mode);
        m.what = 0;
        uiHandler.sendMessage(m);
//        Log.d("DEBUGGING", Thread.currentThread().getName() + "-chartHandler- exists:" + String.valueOf(chartHandler != null));
    }

//    public void addInstantChart(int bpm) {
//        Message m = new Message();
//        m.obj = bpm;
//        m.what = 1;
//        uiHandler.sendMessage(m);
////        Log.d( Thread.currentThread().getName() , "-default instant chart- ");
//
//    }
//
//    public void rollingInstantChart(int bpm) {
//        Message m = new Message();
//        m.obj = bpm;
//        m.what = 2;
//        uiHandler.sendMessage(m);
////        Log.d( Thread.currentThread().getName() , "-optional instant chart- ");
//    }


//    public View getView() {
//        return view;
//    }

    private class ChartHandlerThread extends HandlerThread implements Handler.Callback {

        smu_bme.beats.Chart.ChartView chartView;

        public ChartHandlerThread(String name) {
            super(name);
        }

        @Override
        public boolean handleMessage(Message msg) {

            /**------------------------------------------------------------
             Message from Main Thread to Chart Thread
                    In Chart Thread
             ------------------------------------------------------------*/
//            View view = chartView.getLogChart((Calendar) msg.obj);
//            Log.d("DEBUGGING", Thread.currentThread().getName() + "-handleMessage- chartView exists: " + String.valueOf(chartView != null));
//            synchronized (view) {
//                view.notifyAll();
//                view.postInvalidate();
//            }
//            synchronized (Thread.currentThread()) {
//                notify();
//            }
//            Log.d("DEBUGGING", Thread.currentThread().getName() + "-handleMessage- End");
            return true;
        }
    }

}
