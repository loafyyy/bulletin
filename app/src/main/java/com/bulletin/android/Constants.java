package com.bulletin.android;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by maggie on 2018-02-03.
 */

public class Constants {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1000;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();

    static {
        // San Francisco International Airport.
        LANDMARKS.put("Moscone South", new LatLng(37.783888,-122.4009012));

        // Googleplex.
        LANDMARKS.put("Japantown", new LatLng(37.785281,-122.4296384));

        // Test
        LANDMARKS.put("Pennovation", new LatLng(39.941665, -75.200400));
        LANDMARKS.put("Penn", new LatLng(39.9518512, -75.2032924));
    }

}