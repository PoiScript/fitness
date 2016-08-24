package com.poipoipo.fitness.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationGenerator {
    private static final String TAG = "LocationGenerator";
    private static final float STANDARD_LAT = 22.7591476f;
    private static final float STANDARD_LNG = 113.8708115f;
    private static final float STANDARD_DEV = 0.0004504f;
    private static final int TIME_MIN = 1471276800; // 8-16-2016
    private static final int TIME_MAX = TIME_MIN + 24 * 60 * 60; // 8-17-2016

    private final List<Location> locations = new ArrayList<>();
    private final Random random = new Random();
    private Location prevLocation;

    public List<Location> generate() {
        locations.clear();
        for (int i = 0; i < 10; i++) {
            Location location = new Location();
            if (prevLocation == null) {
                prevLocation = new Location();
                prevLocation.setLongitude(STANDARD_LNG);
                prevLocation.setLatitude(STANDARD_LAT);
            }
            switch (random.nextInt(4)){
                case 0:
                    location.setLongitude(prevLocation.getLongitude() - STANDARD_DEV);
                    location.setLatitude(prevLocation.getLatitude());
                    break;
                case 1:
                    location.setLongitude(prevLocation.getLongitude() + STANDARD_DEV);
                    location.setLatitude(prevLocation.getLatitude());
                    break;
                case 2:
                    location.setLatitude(prevLocation.getLatitude() + STANDARD_DEV);
                    location.setLongitude(prevLocation.getLongitude());
                    break;
                case 3:
                    location.setLatitude(prevLocation.getLatitude() - STANDARD_DEV);
                    location.setLongitude(prevLocation.getLongitude());
                    break;
            }
            location.setTime(random.nextInt(TIME_MAX - TIME_MIN + 1) + TIME_MIN);
            locations.add(location);
            prevLocation = location;
        }
        return locations;
    }
}
