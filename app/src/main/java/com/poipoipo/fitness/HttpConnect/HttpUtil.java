package com.poipoipo.fitness.httpConnect;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.poipoipo.fitness.data.Data;
import com.poipoipo.fitness.data.Location;
import com.poipoipo.fitness.data.Para;
import com.poipoipo.fitness.data.RequestDomain;
import com.poipoipo.fitness.data.Timestamp;
import com.poipoipo.fitness.ui.MainActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    private static final SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat requestFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static final String TAG = "HttpUtil";
    private static final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();
    private List<Para> bpm = new ArrayList<>();
    private List<Para> spo2s = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();
    private Handler handler;

    public HttpUtil(Handler handler) {
        this.handler = handler;
    }

    public void requestBpm(Calendar calendar) {
        final Request request = new Request.Builder()
                .url(RequestDomain.BPM + requestFormat.format(calendar.getTime()))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    parseBpm(response);
                }
            }
        });
    }

    public void requestSpo2(Calendar calendar) {
        final Request request = new Request.Builder()
                .url(RequestDomain.SPO2 + requestFormat.format(calendar.getTime()))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    parseSpo2(response);
                }
            }
        });
    }

    public List<Location> requestGps(Calendar calendar) {
        final Request request = new Request.Builder()
                .url(RequestDomain.GPS + requestFormat.format(calendar.getTime()))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    parseLocation(response);
                }
//                Log.d(TAG, "onResponse: response = " + response.body().charStream().toString());
//                Data data = gson.fromJson(response.body().charStream(), Data.class);
//                Log.d(TAG, "onResponse: data status " + data.getStatus().getStatus());
            }
        });
        return locations;
    }

    private void parseSpo2(Response response) {
        spo2s.clear();
        Data data = gson.fromJson(response.body().charStream(), Data.class);
        for (Data.DataBean dataBean : data.getData()) {
            try {
                Para para = new Para(Para.TYPE_SPO2);
                Date date = parseFormat.parse(dataBean.getTimestamp());
                para.setTime(Timestamp.getTimestampByLong(date.getTime()));
                para.setData((int) dataBean.getSpo2().charAt(0));
                Log.d(TAG, "parseSpo2: timestamp = " + para.getTime() + "data = " + para.getData());
                spo2s.add(para);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        handler.obtainMessage(MainActivity.REFRESH_SPO2).sendToTarget();
    }

    private void parseBpm(Response response) {
        bpm.clear();
        Data data = gson.fromJson(response.body().charStream(), Data.class);
        for (Data.DataBean dataBean : data.getData()) {
            try {
                Para para = new Para(Para.TYPE_BPM);
                Date date = parseFormat.parse(dataBean.getTimestamp());
                para.setTime(Timestamp.getTimestampByLong(date.getTime()));
                para.setData((int) dataBean.getHeartrate().charAt(0));
                Log.d(TAG, "parseBpm: timestamp = " + para.getTime() + "data = " + para.getData());
                bpm.add(para);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        handler.obtainMessage(MainActivity.REFRESH_BPM).sendToTarget();
    }

    private void parseLocation(Response response) {
        locations.clear();
        Data data = gson.fromJson(response.body().charStream(), Data.class);
        for (Data.DataBean dataBean : data.getData()) {
            if (!dataBean.getGps().contains("$GPGGA")) {
                continue;
            }
            try {
                Location location = new Location();
                Date date = parseFormat.parse(dataBean.getTimestamp());
                location.setTime(Timestamp.getTimestampByLong(date.getTime()));
                String[] parts = dataBean.getGps().split(",");
                try {
                    location.setLatitude(Float.parseFloat(parts[2]) / 100);
                    location.setLongitude(Float.parseFloat(parts[4]) / 100);
                    locations.add(location);
                    Log.d(TAG, "parseLocation: time = " + location.getTime() + " lng = " + location.getLongitude() + " lat = " + location.getLatitude());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        handler.obtainMessage(MainActivity.REFRESH_MAP).sendToTarget();
    }

    public List<Para> getSpo2s() {
        return spo2s;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Para> getBpm() {
        return bpm;
    }
}
