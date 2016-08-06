package com.poipoipo.fitness;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.poipoipo.fitness.Bluetooth.BluetoothService;
import com.poipoipo.fitness.Bluetooth.DeviceListActivity;
import com.poipoipo.fitness.Chart.LineChartUtil;
import com.poipoipo.fitness.Data.Para;
import com.poipoipo.fitness.Data.ParaGenerator;
import com.poipoipo.fitness.Database.DatabasePara;
import com.poipoipo.fitness.HttpConnect.HttpCallbackListener;
import com.poipoipo.fitness.HttpConnect.HttpUtil;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "MainActivity";

    public static final int MESSAGE_BLUETOOTH_STATE_CHANGE = 1, MESSAGE_READ = 2, MESSAGE_DEVICE_NAME = 3, MESSAGE_TOAST = 4, REFRESH_DONE = 5, MESSAGE_SNACKBAR = 6;
    private static final int REQUEST_CONNECT_DEVICE = 2, REQUEST_ENABLE_BT = 3;
    public static final String DEVICE_NAME = "device_name", TOAST = "toast", SNACKBAR = "snackbar";

    Intent serverIntent;
    private FloatingActionButton fab;
    ImageButton editDate;
    TextView connectState;
    Button requestButton;
    private int year, month, day;
    private String mConnectedDeviceName = null;

    private Calendar calendar;

    // Array adapter for the conversation thread
    // private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;
    private DatabasePara databasePara;
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
        editDate = (ImageButton) findViewById(R.id.edit_date);
        editDate.setOnClickListener(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        lineChartUtil = new LineChartUtil(this, mHandler);
        lineCharts = lineChartUtil.getInstances();

        databasePara = new DatabasePara(this);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                lineChartUtil.refresh(Para.TYPE_BPM, databasePara.query(Para.TYPE_BPM));
//                lineChartUtil.refresh(Para.TYPE_SPO2, databasePara.query(Para.TYPE_SPO2));
                lineChartUtil.refresh(Para.TYPE_TEMP, databasePara.query(Para.TYPE_TEMP));
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
                case REFRESH_DONE:
                    swipeView.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refresh Done", Toast.LENGTH_SHORT).show();
//                    switch (msg.arg1) {
//                        case Para.TYPE_BPM:
                            lineCharts.get(Para.TYPE_BPM).invalidate();
//                            break;
//                        case Para.TYPE_TEMP:
                            lineCharts.get(Para.TYPE_TEMP).invalidate();
//                            break;
//                        case Para.TYPE_SPO2:
//                            lineCharts.get(Para.TYPE_SPO2).invalidate();
//                            break;
//                    }
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
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int y, int m, int d) {
                        calendar.set(Calendar.YEAR, y);
                        calendar.set(Calendar.MONTH, m);
                        calendar.set(Calendar.DAY_OF_MONTH, d);
                        year = y;
                        month = m;
                        day = d;
                    }
                }, year, month, day);
                dialog.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_random:
                ParaGenerator generator = new ParaGenerator(10);
                list = generator.create();
                databasePara.insert(list);
                Toast.makeText(getApplicationContext(), "Random Data Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_clear:
            databasePara.deleteAll();
            Toast.makeText(getApplicationContext(), "Database Cleared", Toast.LENGTH_SHORT).show();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showParaDetails (List<Para> list){
        for (Para para : list) {
            Log.d(TAG, "showParaDetails: date = " + para.getTime());
            Log.d(TAG, "showParaDetails: data = " + para.getData());
        }
        list.clear();
    }

    private void connectServer() {
        HttpUtil.sendHttpRequest("http://poipoipo.com/data/dummy1.json", new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                List<Para> list = new Gson().fromJson(response, new TypeToken<List<Para>>() {
                }.getType());
                databasePara.insert(list);
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