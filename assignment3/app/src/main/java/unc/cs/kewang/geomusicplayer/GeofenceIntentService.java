package unc.cs.kewang.geomusicplayer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceIntentService extends IntentService {
    private static final String TAG = GeofenceIntentService.class.getSimpleName();
    private LocalBroadcastManager mLocalBroadcastManager;

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    public static String getGeofenceErrorMessage(Context context, int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                errorMessage = context.getString(R.string.geofence_not_available);
                break;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                errorMessage = context.getString(R.string.geofence_too_many_geofences);
                break;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                errorMessage = context.getString(R.string.geofence_too_many_pending_intents);
                break;
            default:
                errorMessage = context.getString(R.string.unknown_geofence_errors);
                break;
        }

        return errorMessage;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = getGeofenceErrorMessage(this, geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        String placeName = triggeringGeofences.get(0).getRequestId();

        Intent broadcastIntent = new Intent(Constants.GEOFENCE_BROADCAST_ACTION);
        broadcastIntent.putExtra(Constants.GEOFENCE_PLACE_NAME_KEY, placeName);
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "Entered: " + placeName);
            broadcastIntent.putExtra(Constants.GEOFENCE_TRANSITION_TYPE_KEY, Constants.GEOFENCE_TRANSITION_ENTER);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Exited: " + placeName);
            broadcastIntent.putExtra(Constants.GEOFENCE_TRANSITION_TYPE_KEY, Constants.GEOFENCE_TRANSITION_EXIT);
        }
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);
    }
}
