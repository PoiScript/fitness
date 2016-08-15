package com.poipoipo.fitness.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.poipoipo.fitness.R;
import com.poipoipo.fitness.bluetooth.BluetoothService;
import com.poipoipo.fitness.chart.LineChartUtil;
import com.poipoipo.fitness.data.Para;
import com.poipoipo.fitness.data.ParaGenerator;
import com.poipoipo.fitness.data.Timestamp;
import com.poipoipo.fitness.database.DatabaseHelper;
import com.poipoipo.fitness.httpConnect.HttpCallbackListener;
import com.poipoipo.fitness.httpConnect.HttpUtil;

public class MainActivity extends AppCompatActivity
        implements OnClickListener, OnMapReadyCallback {
    private static final String TAG = "MainActivity";

    public static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 1, MESSAGE_READ = 2, MESSAGE_DEVICE_NAME = 3, MESSAGE_TOAST = 4, START_REFRESH= 5, REFRESH_DONE = 6;
    private static final int REQUEST_CONNECT_DEVICE = 2, REQUEST_ENABLE_BT = 3;
    private static final int DATE_PICKER = 1;
    public static final String DEVICE_NAME = "device_name", TOAST = "toast", SNACKBAR = "snackbar";

    Intent serverIntent;
    private FloatingActionButton fab;
    Button editDate;
    TextView connectState;
    GoogleMap map;
    private String mConnectedDeviceName = null;
    private Timestamp timestamp;

    private Calendar calendar;

    // Array adapter for the conversation thread
    // private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;
    private DatabaseHelper databaseHelper;
    private SwipeRefreshLayout swipeView;
    List<Para> list = new ArrayList<>();
    List<LineChart> lineCharts = new ArrayList<>();
    LineChartUtil lineChartUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        super.onCreate(savedInstanceState);

        /*Stetho Debug*/
        Stetho.initializeWithDefaults(this);
        Log.d(TAG, "onCreate: Stetho Running");

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        editDate = (Button) findViewById(R.id.edit_date);
        editDate.setOnClickListener(this);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        timestamp = new Timestamp(calendar);
        updateDate();
        ImageButton prevDate = (ImageButton) findViewById(R.id.prev_date);
        prevDate.setOnClickListener(this);
        ImageButton nextDate = (ImageButton) findViewById(R.id.next_date);
        nextDate.setOnClickListener(this);

        lineChartUtil = new LineChartUtil(this, mHandler);
        lineCharts = lineChartUtil.getInstances();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                mHandler.obtainMessage(START_REFRESH).sendToTarget();
            }
        });
    }

    // The Handler that gets information back from the BluetoothService, the HttpCallback and Database operation
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_BLUETOOTH_STATE_CHANGE:
                    bluetoothStateChange(msg.arg1);
                    break;
                case MESSAGE_READ:
                    fab.setImageResource(R.drawable.ic_sync);
                    byte[] readBuf = (byte[]) msg.obj;
//				 TODO construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case START_REFRESH:
                    lineChartUtil.refresh(Para.TYPE_BPM, databaseHelper.queryPara(Para.TYPE_BPM, timestamp.getTodayTimestamp()));
                    lineChartUtil.refresh(Para.TYPE_SPO2, databaseHelper.queryPara(Para.TYPE_SPO2, timestamp.getTodayTimestamp()));
                    lineChartUtil.refresh(Para.TYPE_TEMP, databaseHelper.queryPara(Para.TYPE_TEMP, timestamp.getTodayTimestamp()));
                    mHandler.obtainMessage(MainActivity.REFRESH_DONE).sendToTarget();
                    break;
                case REFRESH_DONE:
                    swipeView.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refresh Done", Toast.LENGTH_SHORT).show();
                    lineCharts.get(Para.TYPE_BPM).invalidate();
                    lineCharts.get(Para.TYPE_TEMP).invalidate();
                    lineCharts.get(Para.TYPE_SPO2).invalidate();
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                break;
            case R.id.edit_date:
                showDialog(DATE_PICKER);
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

    private void updateDate() {
        editDate.setText(new StringBuilder().append(calendar.get(Calendar.YEAR)).append("/")
                .append(calendar.get(Calendar.MONTH) + 1).append("/").append(calendar.get(Calendar.DAY_OF_MONTH)));
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    calendar.set(i, i1, i2);
                    updateDate();
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER:
                return new DatePickerDialog(this, onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sydney = new LatLng(37.45, -122.0);
        PolylineOptions options = new PolylineOptions()
                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
                .add(new LatLng(37.35, -122.0)); // Closes the polyline.
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.addPolyline(options);
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random:
                databaseHelper.insertPara(new ParaGenerator().generate(10));
                Toast.makeText(getApplicationContext(), "Random Data Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_clear:
                databaseHelper.deleteAll();
                Toast.makeText(getApplicationContext(), "Database Cleared", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectServer() {
        HttpUtil.sendHttpRequest("http://poipoipo.com/data/dummy1.json", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<Para> list = new Gson().fromJson(response, new TypeToken<List<Para>>() {
                }.getType());
                databaseHelper.insertPara(list);
                mHandler.obtainMessage(MainActivity.REFRESH_DONE).sendToTarget();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void bluetoothStateChange(int state) {
        switch (state) {
            case BluetoothService.STATE_CONNECTED:
                connectState.setText(getString(R.string.title_connected_to, mConnectedDeviceName));
                break;
            case BluetoothService.STATE_CONNECTING:
                connectState.setText(R.string.title_connecting);
                break;
            case BluetoothService.STATE_LISTEN:
            case BluetoothService.STATE_NONE:
                fab.setImageResource(R.drawable.ic_disconnect);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode != Activity.RESULT_OK) {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();
    }
}