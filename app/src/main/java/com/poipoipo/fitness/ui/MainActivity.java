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
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.poipoipo.fitness.DatePickerFragment;
import com.poipoipo.fitness.R;
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

public class MainActivity extends AppCompatActivity
        implements OnClickListener, OnMapReadyCallback, SwipeRefreshLayout.OnRefreshListener {
    public static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int REFRESH_SPO2 = 7;
    public static final int REFRESH_BPM = 8;
    public static final int REFRESH_MAP = 9;
    public static final String DEVICE_NAME = "device_name", TOAST = "toast";
    private static final String TAG = "MainActivity";
    private static final int START_REFRESH = 5;
    //    private static final int REFRESH_DONE = 6;
//    private static final int REQUEST_CONNECT_DEVICE = 2, REQUEST_ENABLE_BT = 3;
    public Calendar calendar;
    //    private Intent serverIntent;
//    private FloatingActionButton fab;
    private Button editDate;
    //    private TextView connectState;
//    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    // private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    // Local Bluetooth adapter
//    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
//    private BluetoothService mChatService = null;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeView;
    private HttpUtil httpUtil;
    private List<LineChart> lineCharts = new ArrayList<>();
    private LineChartUtil lineChartUtil;
    private MapUtil mapUtil;
    // The Handler that gets information back from the BluetoothService, the HttpCallback and Database operation
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case MESSAGE_BLUETOOTH_STATE_CHANGE:
//                    bluetoothStateChange(msg.arg1);
//                    break;
//                case MESSAGE_READ:
//                    fab.setImageResource(R.drawable.ic_sync);
////                    byte[] readBuf = (byte[]) msg.obj;
////                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    break;
//                case MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
//                    break;
                case START_REFRESH:
                    lineChartUtil.refresh(Para.TYPE_BPM, databaseHelper.queryPara(Para.TYPE_BPM, Timestamp.getDayTimestampCalendar(calendar)));
                    lineChartUtil.refresh(Para.TYPE_SPO2, databaseHelper.queryPara(Para.TYPE_SPO2, Timestamp.getDayTimestampCalendar(calendar)));
                    mapUtil.updateMap(databaseHelper.queryLocation(Timestamp.getDayTimestampCalendar(calendar)));
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
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//        }
        super.onCreate(savedInstanceState);

        /*Stetho Debug*/
        Stetho.initializeWithDefaults(this);
        Log.d(TAG, "onCreate: Stetho Running");

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this);
        editDate = (Button) findViewById(R.id.edit_date);
        editDate.setOnClickListener(this);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        ImageButton prevDate = (ImageButton) findViewById(R.id.prev_date);
        prevDate.setOnClickListener(this);
        ImageButton nextDate = (ImageButton) findViewById(R.id.next_date);
        nextDate.setOnClickListener(this);

        lineChartUtil = new LineChartUtil(this);
        lineCharts = lineChartUtil.getInstances();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this);
        updateDate();
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(this);

        httpUtil = new HttpUtil(mHandler);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.fab:
//                serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                break;
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

    public void updateDate() {
        editDate.setText(new StringBuilder().append(calendar.get(Calendar.YEAR)).append("/")
                .append(calendar.get(Calendar.MONTH) + 1).append("/").append(calendar.get(Calendar.DAY_OF_MONTH)));
        mHandler.obtainMessage(START_REFRESH).sendToTarget();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapUtil = new MapUtil(googleMap, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random_location:
                databaseHelper.insertLocation(new LocationGenerator().generate(10));
                Toast.makeText(getApplicationContext(), "Random Location Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_clear_location:
                databaseHelper.delete(DatabaseHelper.TABLE_LOCATION);
                Toast.makeText(getApplicationContext(), "Database Location Cleared", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_random_data:
                databaseHelper.insertPara(new ParaGenerator().generate(10));
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        httpUtil.requestGps(calendar);
        httpUtil.requestSpo2(calendar);
        httpUtil.requestBpm(calendar);
    }

//    private void bluetoothStateChange(int state) {
//        switch (state) {
//            case BluetoothService.STATE_CONNECTED:
//                connectState.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
//                break;
//            case BluetoothService.STATE_CONNECTING:
//                connectState.setText(R.string.title_connecting);
//                break;
//            case BluetoothService.STATE_LISTEN:
//            case BluetoothService.STATE_NONE:
//                fab.setImageResource(R.drawable.ic_disconnect);
//                break;
//        }
//    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CONNECT_DEVICE:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == Activity.RESULT_OK) {
//                    connectDevice(data);
//                }
//                break;
//            case REQUEST_ENABLE_BT:
//                if (resultCode != Activity.RESULT_OK) {
//                    Log.d(TAG, "BT not enabled");
//                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//        }
//    }

//    private void connectDevice(Intent data) {
//        // Get the device MAC address
//        String address = data.getExtras().getString(
//                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//        // Get the BluetoothDevice object
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        // Attempt to connect to the device
//        mChatService.connect(device);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        }
//    }

//    @Override
//    public synchronized void onResume() {
//        super.onResume();
//        // Performing this check in onResume() covers the case in which BT was
//        // not enabled during onStart(), so we were paused to enable it...
//        // onResume() will be called when ACTION_REQUEST_ENABLE activity
//        // returns.
//        if (mChatService != null) {
//            // Only if the state is STATE_NONE, do we know that we haven't
//            // started already
//            if (mChatService.getState() == BluetoothService.STATE_NONE) {
//                // Start the Bluetooth chat services
//                mChatService.start();
//            }
//        }
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Stop the Bluetooth chat services
//        if (mChatService != null)
//            mChatService.stop();
//    }
}