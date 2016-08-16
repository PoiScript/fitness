package com.poipoipo.fitness.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.poipoipo.fitness.R;
import com.poipoipo.fitness.data.Location;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

public class MapUtil
        implements GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener {
    private static final BitmapDescriptor point = BitmapDescriptorFactory.fromResource(R.mipmap.pointg);
    private static final int color = Color.parseColor("#607D8B");
    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
    List<Location> locations;
    private LayoutInflater inflater;
    private Context context;
    private GoogleMap map;

    public MapUtil(GoogleMap map, Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.map = map;
        map.setOnMarkerClickListener(this);
        map.setInfoWindowAdapter(this);
    }

    public void updateMap(List<Location> locations) {
        this.locations = locations;
        map.clear();
        if (locations.size() > 0) {
            PolylineOptions options = new PolylineOptions();
            for (Location location : locations) {
                map.addMarker(new MarkerOptions()
                        .position(location.getLatLng())
                        .title("Latitude: " + location.getLatitude()
                                + "\n" + "Longitude: "
                                + location.getLongitude()
                                + "\n " + format.format(location.getTime()))
                        .icon(point).anchor(0.5f, 0.5f));
                options.add(location.getLatLng());
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(locations.get(0).getLatLng()));
            options.color(color).width(4);
            map.addPolyline(options);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = inflater.inflate(R.layout.info_content, null);
        CardView cardView = (CardView) view.findViewById(R.id.info_windows_card);
        cardView.setBackgroundColor(color);
        TextView title = (TextView) view.findViewById(R.id.info_windows_title);
        title.setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        return false;
    }
}
