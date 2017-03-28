package unc.cs.kewang.geomusicplayer;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


public final class Constants {
    public static final String PACKAGE_NAME = "unc.cs.kewang.geomusicplayer";

    public static final int GEOCODER_SUCCESS_RESULT = 0;
    public static final int GEOCODER_FAILURE_RESULT = 1;
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final String GEOCODER_BROADCAST_ACTION = PACKAGE_NAME + ".GEOCODER_BROADCAST";
    public static final String GEOCODER_ADDRESS_KEY = PACKAGE_NAME + ".GEOCODER_ADDRESS_KEY";
    public static final String GEOCODER_RESULT_STATUS_KEY = PACKAGE_NAME + ".GEOCODER_RESULT_STATUS_KEY";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 3600 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100.0f;
    public static final String GEOFENCE_BROADCAST_ACTION = PACKAGE_NAME + ".GENFENCE_BROADCAST";
    public static final String GEOFENCE_TRANSITION_TYPE_KEY = PACKAGE_NAME + ".GENFENCE_TRANSITION_TYPE";
    public static final String GEOFENCE_PLACE_NAME_KEY = PACKAGE_NAME + ".GENFENCE_PLACE_NAME";
    public static final String GEOFENCE_TRANSITION_ENTER = "GENFENCE_ENTER";
    public static final String GEOFENCE_TRANSITION_EXIT = "GENFENCE_EXIT";

    public static final HashMap<String, LatLng> UNC_LANDMARKS = new HashMap<String, LatLng>();
    public static final HashMap<String, Integer> GEOFENCE_MUSIC_ID = new HashMap<String, Integer>();
    public static final HashMap<String, String> GEOFENCE_MUSIC_NAME = new HashMap<String, String>();

    public static final String SITTERSON_NAME = "Sitterson";
    public static final String OLD_WELL_NAME = "Old Well";
    public static final String POLK_PLACE_NAME = "Polk Place";

    static {
        UNC_LANDMARKS.put(SITTERSON_NAME, new LatLng(35.909974, -79.053086));
        UNC_LANDMARKS.put(OLD_WELL_NAME, new LatLng(35.911991, -79.051182));
        UNC_LANDMARKS.put(POLK_PLACE_NAME, new LatLng(35.910889, -79.050642));
        GEOFENCE_MUSIC_ID.put(SITTERSON_NAME, R.raw.beauty_beast);
        GEOFENCE_MUSIC_ID.put(OLD_WELL_NAME, R.raw.how_far_i_will_go);
        GEOFENCE_MUSIC_ID.put(POLK_PLACE_NAME, R.raw.let_it_go);
        GEOFENCE_MUSIC_NAME.put(SITTERSON_NAME, "Overture from Beauty and Beast");
        GEOFENCE_MUSIC_NAME.put(OLD_WELL_NAME, "How Far I'll Go from Moana");
        GEOFENCE_MUSIC_NAME.put(POLK_PLACE_NAME, "Let It Go from Frozen");
    }

}
