package unc.cs.kewang.geomusicplayer;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, ResultCallback<Status>, OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 2000;
    private static final long FASTEST_UPDATE_INTERVAL = 1000;

    // UI
    private Circle mGeofenceCircle;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private Marker mCurrentLocationMarker;
    private TextView mGeofenceTextView;
    private TextView mLatLngTextView;
    private TextView mTextView;


    private Location mLastLocation;
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencingPendingIntent;
    private LocalBroadcastManager mLocalBroadcastManager;
    private GeofenceBroadcastReceiver mGeofenceBroadcastReceiver;
    private GeocoderBroadcastReceiver mGeocoderBroadcastReceiver;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 233;
    private static final int MAP_ZOOM_LEVEL = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissions()) {
            requestPermissions();
        }

        // setup UI
        mTextView = (TextView) findViewById(R.id.address_tv);
        mLatLngTextView = (TextView) findViewById(R.id.latlng_tv);
        mGeofenceTextView = (TextView) findViewById(R.id.geofence_tv);

        // setup Map
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linear_layout_container, mMapFragment);
        fragmentTransaction.commit();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // setup geofence
        mGeofenceList = new ArrayList<Geofence>();
        for (Map.Entry<String, LatLng> entry : Constants.UNC_LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude, Constants.GEOFENCE_RADIUS_IN_METERS)
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT).build());
        }

        // setup broadcast receiver
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mGeofenceBroadcastReceiver = new GeofenceBroadcastReceiver();
        IntentFilter geofenceBroadcastIntentFilter = new IntentFilter();
        geofenceBroadcastIntentFilter.addAction(Constants.GEOFENCE_BROADCAST_ACTION);
        mLocalBroadcastManager.registerReceiver(mGeofenceBroadcastReceiver, geofenceBroadcastIntentFilter);

        mGeocoderBroadcastReceiver = new GeocoderBroadcastReceiver();
        IntentFilter geocoderBroadcastIntentFilter = new IntentFilter();
        geocoderBroadcastIntentFilter.addAction(Constants.GEOCODER_BROADCAST_ACTION);
        mLocalBroadcastManager.registerReceiver(mGeocoderBroadcastReceiver, geocoderBroadcastIntentFilter);

        // Start music background service
        Intent startBackgroundMusicServiceIntent = new Intent(this, BackgroundMusicService.class);
        startService(startBackgroundMusicServiceIntent);

        buildGoogleApiClient();
        createLocationRequest();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mGeocoderBroadcastReceiver);
        mLocalBroadcastManager.unregisterReceiver(mGeofenceBroadcastReceiver);
    }

    private synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencingPendingIntent() {
        if (mGeofencingPendingIntent != null) {
            return mGeofencingPendingIntent;
        }

        Intent intent = new Intent(this, GeofenceIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, getGeofencingRequest(), getGeofencingPendingIntent()).setResultCallback(this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            startGeocoderIntentService();
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLatLngTextView.setText("GPS Location: " + String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
        startGeocoderIntentService();

        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL));
    }

    private void startGeocoderIntentService() {
        Intent intent = new Intent(this, GeocoderIntentService.class);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Geofences successfully added.");
        } else {
            String errorMessage = GeofenceIntentService.getGeofenceErrorMessage(this, status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Map is ready!");
        mGoogleMap = googleMap;
    }

    class GeofenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update UI
            String transitionType = intent.getStringExtra(Constants.GEOFENCE_TRANSITION_TYPE_KEY);
            String placeName = intent.getStringExtra(Constants.GEOFENCE_PLACE_NAME_KEY);
            if (transitionType.equals(Constants.GEOFENCE_TRANSITION_EXIT)) {
                Log.i(TAG, "Exited: " + placeName);
                mGeofenceTextView.setText("Exited: " + placeName);
                if (mGeofenceCircle != null) {
                    mGeofenceCircle.remove();
                }
            } else {
                Log.i(TAG, "Entered" + placeName);
                mGeofenceTextView.setText("Entered: " + placeName + ". Playing: " + Constants.GEOFENCE_MUSIC_NAME.get(placeName));

                // show circle for geofence
                if (mGeofenceCircle != null) {
                    mGeofenceCircle.remove();
                }
                LatLng center = Constants.UNC_LANDMARKS.get(placeName);
                mGeofenceCircle = mGoogleMap.addCircle(new CircleOptions().center(center).radius(Constants.GEOFENCE_RADIUS_IN_METERS).strokeWidth(5));
            }
        }
    }

    class GeocoderBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update UI
            String address = intent.getStringExtra(Constants.GEOCODER_ADDRESS_KEY);
            int status = intent.getIntExtra(Constants.GEOCODER_RESULT_STATUS_KEY, Constants.GEOCODER_FAILURE_RESULT);

            if (status == Constants.GEOCODER_SUCCESS_RESULT) {
                mTextView.setText(address);
            } else {
                String errorMessage = "No address available for current location.";
                mTextView.setText(errorMessage);
            }
        }
    }

    private boolean checkPermissions() {
        int locationPermissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int externalStoragePermissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return externalStoragePermissionState == PackageManager.PERMISSION_GRANTED && locationPermissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Log.wtf(TAG, getString(R.string.permission_denied));
                    Snackbar.make(findViewById(R.id.linear_layout_container), getString(R.string.permission_denied), Snackbar.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }
}
