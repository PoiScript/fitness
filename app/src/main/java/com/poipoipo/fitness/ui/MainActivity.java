package com.poipoipo.fitness.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.poipoipo.fitness.DatePickerFragment;
import com.poipoipo.fitness.R;
import com.poipoipo.fitness.chart.ErrorSetterDialog;
import com.poipoipo.fitness.chart.LineChartUtil;
import com.poipoipo.fitness.data.LocationGenerator;
import com.poipoipo.fitness.data.Para;
import com.poipoipo.fitness.data.ParaGenerator;
import com.poipoipo.fitness.data.Timestamp;
import com.poipoipo.fitness.database.DatabaseHelper;
import com.poipoipo.fitness.httpConnect.HttpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements OnClickListener, OnMapReadyCallback, SwipeRefreshLayout.OnRefreshListener, ErrorSetterDialog.OnPositiveClickListener {
    public static final int REFRESH_SPO2 = 7;
    public static final int REFRESH_BPM = 8;
    public static final int REFRESH_MAP = 9;
    public static final int REFRESH_REAL_TIME_PARA = 10;
    public static final int REFRESH_REAL_TIME_LATLNG = 11;
    public static final int REFRESH_REAL_TIME_LOCATION = 13;
    private static final String TAG = "MainActivity";
    private static final int REFRESH_INIT = 5;
    public Calendar calendar;
    private Button editDate;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeView;
    private HttpUtil httpUtil;
    private List<LineChart> lineCharts = new ArrayList<>();
    private LineChartUtil lineChartUtil;
    private MapUtil mapUtil;
    private TextView realTimeBpm;
    private TextView realTimeSpo2;
    private TextView realTimeLngLat;
    private TextView realTimeLocation;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_INIT:
                    lineChartUtil.refresh(Para.TYPE_BPM, databaseHelper.queryPara(Para.TYPE_BPM, Timestamp.getDayTimestampCalendar(calendar)));
                    lineChartUtil.refresh(Para.TYPE_SPO2, databaseHelper.queryPara(Para.TYPE_SPO2, Timestamp.getDayTimestampCalendar(calendar)));
                    mapUtil.updateMap(databaseHelper.queryLocation(Timestamp.getDayTimestampCalendar(calendar)));
//                    mHandler.obtainMessage(REFRESH_INIT).sendToTarget();
                    swipeView.setRefreshing(false);
                    lineCharts.get(Para.TYPE_BPM).invalidate();
                    lineCharts.get(Para.TYPE_SPO2).invalidate();
                    break;
                case REFRESH_SPO2:
                    swipeView.setRefreshing(false);
                    lineChartUtil.refresh(Para.TYPE_SPO2, httpUtil.getSpo2s());
                    lineCharts.get(Para.TYPE_SPO2).invalidate();
                    databaseHelper.insertPara(httpUtil.getSpo2s());
                    break;
                case REFRESH_BPM:
                    swipeView.setRefreshing(false);
                    lineChartUtil.refresh(Para.TYPE_BPM, httpUtil.getBpm());
                    lineCharts.get(Para.TYPE_BPM).invalidate();
                    databaseHelper.insertPara(httpUtil.getBpm());
                    break;
                case REFRESH_MAP:
                    swipeView.setRefreshing(false);
                    mapUtil.updateMap(httpUtil.getLocations());
                    databaseHelper.insertLocation(httpUtil.getLocations());
                    break;
                case REFRESH_REAL_TIME_PARA:
                    updateRealTimePara(msg.arg1, msg.arg1);
                    break;
                case REFRESH_REAL_TIME_LATLNG:
                    updateRealTimeLatLng((LatLng) msg.obj);
                    break;
                case REFRESH_REAL_TIME_LOCATION:
                    updateRealTimeLocation((String) msg.obj);
            }
        }
    };

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        /*Stetho Debug*/
        Stetho.initializeWithDefaults(this);
        Log.d(TAG, "onCreate: Stetho Running");

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editDate = (Button) findViewById(R.id.edit_date);
        editDate.setOnClickListener(this);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        ImageButton prevDate = (ImageButton) findViewById(R.id.prev_date);
        prevDate.setOnClickListener(this);
        ImageButton nextDate = (ImageButton) findViewById(R.id.next_date);
        nextDate.setOnClickListener(this);

        lineChartUtil = new LineChartUtil(this);
        lineCharts = lineChartUtil.getInstances();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this);
        updateDate();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(this);
        httpUtil = new HttpUtil(mHandler);

        realTimeBpm = (TextView) findViewById(R.id.real_time_bpm);
        realTimeLngLat = (TextView) findViewById(R.id.real_time_lng_lat);
        realTimeSpo2 = (TextView) findViewById(R.id.real_time_spo2);
        realTimeLocation = (TextView) findViewById(R.id.real_time_location);

        new Timer().schedule(new AutoUpdate(), 0, 10000);
    }

    private void updateRealTimePara(int bpm, int spo2) {
        realTimeBpm.setText(new StringBuilder().append("心率：").append(bpm));
        realTimeSpo2.setText(new StringBuilder().append("血氧饱和度：").append(spo2));
    }

    private void updateRealTimeLatLng(LatLng latLng) {
        realTimeLngLat.setText(new StringBuilder().append("经度：").append(latLng.latitude + databaseHelper.queryLatLngError().latitude).append("维度：").append(latLng.longitude + databaseHelper.queryLatLngError().latitude));
        httpUtil.parseLatLng(new LatLng(latLng.latitude + databaseHelper.queryLatLngError().latitude, latLng.longitude + databaseHelper.queryLatLngError().longitude));
    }

    private void updateRealTimeLocation(String location) {
        realTimeLocation.setText(location);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_date:
                DialogFragment fragment = DatePickerFragment.newInstance(calendar);
                fragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.prev_date:
                calendar.add(Calendar.DATE, -1);
                updateDate();
                break;
            case R.id.next_date:
                calendar.add(Calendar.DATE, 1);
                updateDate();
                break;
        }
    }

    @Override
    public void onPositiveClick(double lat_error, double lng_error) {
        Log.d(TAG, "onPositiveClick: lat_error = " + lat_error + " lng_error = " + lng_error);
        databaseHelper.updateLatLngError(new LatLng(lat_error, lng_error));
    }

    public void updateDate() {
        editDate.setText(new StringBuilder().append(calendar.get(Calendar.YEAR)).append("/")
                .append(calendar.get(Calendar.MONTH) + 1).append("/").append(calendar.get(Calendar.DAY_OF_MONTH)));
        mHandler.obtainMessage(REFRESH_INIT).sendToTarget();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapUtil = new MapUtil(googleMap, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random_location:
                databaseHelper.insertLocation(new LocationGenerator().generate());
                Toast.makeText(getApplicationContext(), "Random Location Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_clear_location:
                databaseHelper.delete(DatabaseHelper.TABLE_LOCATION);
                Toast.makeText(getApplicationContext(), "Database Location Cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_random_data:
                databaseHelper.insertPara(new ParaGenerator().generate());
                Toast.makeText(getApplicationContext(), "Random Data Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_clear_para:
                databaseHelper.delete(DatabaseHelper.TABLE_BPM);
                databaseHelper.delete(DatabaseHelper.TABLE_SPO2);
                databaseHelper.delete(DatabaseHelper.TABLE_TEMP);
                Toast.makeText(getApplicationContext(), "Database BPM, SPO2, TEMP cleared", Toast.LENGTH_SHORT).show();
            case R.id.action_clear:
                databaseHelper.deleteAll();
                Toast.makeText(getApplicationContext(), "Database Cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_error:
                ErrorSetterDialog fragment = ErrorSetterDialog.newInstance(databaseHelper.queryLatLngError());
                fragment.show(getFragmentManager(), "errorSetter");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        httpUtil.requestGps(calendar);
        httpUtil.requestSpo2(calendar);
        httpUtil.requestBpm(calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    class AutoUpdate extends TimerTask {
        @Override
        public void run() {
            httpUtil.updateRequest();
        }
    }
}