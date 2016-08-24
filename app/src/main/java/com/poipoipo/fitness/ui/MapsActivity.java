package com.poipoipo.fitness.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.poipoipo.fitness.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setPadding(0, 0, 0, 20);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(37.45, -122.0);
//        PolylineOptions options = new PolylineOptions()
//                .add(new LatLng(37.45, -122.0))  // North of the previous point, but at the same longitude
//                .add(new LatLng(37.45, -122.2))  // Same latitude, and 30km to the west
//                .add(new LatLng(37.35, -122.2))  // Same longitude, and 16km to the south
//                .add(new LatLng(37.35, -122.0)); // Closes the polyline.
        MarkerOptions marker = new MarkerOptions().position(new LatLng(37.45, -122.0)).title("Hello Maps");

// Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.point));

// adding marker
        googleMap.addMarker(marker);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.addPolyline(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
