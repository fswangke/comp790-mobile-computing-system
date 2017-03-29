package unc.cs.kewang.geomusicplayer;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;


final class Constants {
    static final String PACKAGE_NAME = "unc.cs.kewang.geomusicplayer";

    static final int GEOCODER_SUCCESS_RESULT = 0;
    static final int GEOCODER_FAILURE_RESULT = 1;
    static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    static final String GEOCODER_BROADCAST_ACTION = PACKAGE_NAME + ".GEOCODER_BROADCAST";
    static final String GEOCODER_ADDRESS_KEY = PACKAGE_NAME + ".GEOCODER_ADDRESS_KEY";
    static final String GEOCODER_RESULT_STATUS_KEY = PACKAGE_NAME + ".GEOCODER_RESULT_STATUS_KEY";

    static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 3600 * 1000;
    static final String GEOFENCE_BROADCAST_ACTION = PACKAGE_NAME + ".GENFENCE_BROADCAST";
    static final String GEOFENCE_TRANSITION_TYPE_KEY = PACKAGE_NAME + ".GENFENCE_TRANSITION_TYPE";
    static final String GEOFENCE_PLACE_NAME_KEY = PACKAGE_NAME + ".GENFENCE_PLACE_NAME";
    static final String GEOFENCE_TRANSITION_ENTER = "GENFENCE_ENTER";
    static final String GEOFENCE_TRANSITION_EXIT = "GENFENCE_EXIT";

    static final HashMap<String, LatLng> UNC_LANDMARKS_GPS = new HashMap<>();
    static final HashMap<String, Float> UNC_LANDMARKS_RADIUS = new HashMap<>();
    static final HashMap<String, Integer> GEOFENCE_MUSIC_ID = new HashMap<>();
    static final HashMap<String, String> GEOFENCE_MUSIC_NAME = new HashMap<>();

    static final String SITTERSON_NAME = "Sitterson";
    static final String OLD_WELL_NAME = "Old Well";
    static final String POLK_PLACE_NAME = "Polk Place";

    static {
        UNC_LANDMARKS_GPS.put(SITTERSON_NAME, new LatLng(35.909894, -79.053134));
        UNC_LANDMARKS_GPS.put(OLD_WELL_NAME, new LatLng(35.911991, -79.051182));
        UNC_LANDMARKS_GPS.put(POLK_PLACE_NAME, new LatLng(35.910889, -79.050642));
        UNC_LANDMARKS_RADIUS.put(SITTERSON_NAME, 60.0f);
        UNC_LANDMARKS_RADIUS.put(OLD_WELL_NAME, 50.0f);
        UNC_LANDMARKS_RADIUS.put(POLK_PLACE_NAME, 50.0f);
        GEOFENCE_MUSIC_ID.put(SITTERSON_NAME, R.raw.cake_by_the_ocean_by_dnce);
        GEOFENCE_MUSIC_ID.put(OLD_WELL_NAME, R.raw.carolina_in_my_mind_by_james_taylor);
        GEOFENCE_MUSIC_ID.put(POLK_PLACE_NAME, R.raw.waves_by_kanye_west);
        GEOFENCE_MUSIC_NAME.put(SITTERSON_NAME, "Cake by the Ocean by DNCE");
        GEOFENCE_MUSIC_NAME.put(OLD_WELL_NAME, "Carolina in My Mind by James Taylor");
        GEOFENCE_MUSIC_NAME.put(POLK_PLACE_NAME, "Waves By Kanye West");
    }

}
