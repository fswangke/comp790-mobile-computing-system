package unc.cs.kewang.geomusicplayer;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocoderIntentService extends IntentService {
    private static final String TAG = GeocoderIntentService.class.getSimpleName();
    private LocalBroadcastManager mLocalBroadcastManager;

    public GeocoderIntentService() {
        super("GeocoderIntentService");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            Log.e(TAG, "Null intent parameter.");
            return;
        }

        String errorMessage = "";

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided);
            Log.e(TAG, errorMessage);
            broadcastResultToReceiver(Constants.GEOCODER_FAILURE_RESULT, errorMessage);
            return;
        }

        Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioExecption) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioExecption);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_lng_parameter);
            Log.e(TAG, errorMessage, illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            broadcastResultToReceiver(Constants.GEOCODER_FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); ++i) {
                addressFragments.add(address.getAddressLine(i));
            }
            String correct_result = TextUtils.join(System.getProperty("line.separator"), addressFragments);
            Log.i(TAG, getString(R.string.address_found) + ":" + correct_result);
            broadcastResultToReceiver(Constants.GEOCODER_SUCCESS_RESULT, correct_result);
        }
    }

    private void broadcastResultToReceiver(int resultCode, String address) {
        Intent broadcastIntent = new Intent(Constants.GEOCODER_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.GEOCODER_ADDRESS_KEY, address);
        broadcastIntent.putExtra(Constants.GEOCODER_RESULT_STATUS_KEY, resultCode);
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
