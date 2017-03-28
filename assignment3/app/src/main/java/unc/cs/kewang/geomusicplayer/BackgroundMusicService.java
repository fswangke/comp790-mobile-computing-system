package unc.cs.kewang.geomusicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;

public class BackgroundMusicService extends Service {
    private static final String TAG = BackgroundMusicService.class.getSimpleName();
    private HashMap<String, MediaPlayer> mMediaPlayerHashMap;

    // Receives local broadcast from GeofenceIntentService
    private GeofenceBroadcastReceiver mGeofenceBroadcastReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    public BackgroundMusicService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mMediaPlayerHashMap = new HashMap<String, MediaPlayer>();
        for (HashMap.Entry<String, Integer> entry : Constants.GEOFENCE_MUSIC_ID.entrySet()) {
            String placeName = entry.getKey();
            int musicId = entry.getValue();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, musicId);
            mediaPlayer.setLooping(true);
            mMediaPlayerHashMap.put(placeName, mediaPlayer);
            Log.i(TAG, "Initialized music player for place:" + placeName);
        }

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.GEOFENCE_BROADCAST_ACTION);
        mGeofenceBroadcastReceiver = new GeofenceBroadcastReceiver();
        mLocalBroadcastManager.registerReceiver(mGeofenceBroadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (HashMap.Entry<String, MediaPlayer> entry : mMediaPlayerHashMap.entrySet()) {
            String place_name = entry.getKey();
            MediaPlayer mediaPlayer = entry.getValue();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            Log.i(TAG, "Released music player for place:" + place_name);
        }
        mLocalBroadcastManager.unregisterReceiver(mGeofenceBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    class GeofenceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String transitionType = intent.getStringExtra(Constants.GEOFENCE_TRANSITION_TYPE_KEY);
            String placeName = intent.getStringExtra(Constants.GEOFENCE_PLACE_NAME_KEY);
            if (transitionType.equals(Constants.GEOFENCE_TRANSITION_EXIT)) {
                MediaPlayer mediaPlayer = mMediaPlayerHashMap.get(placeName);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Log.i(TAG, "Paused " + Constants.GEOFENCE_MUSIC_NAME.get(placeName));
                }
            } else if (transitionType.equals(Constants.GEOFENCE_TRANSITION_ENTER)) {
                mMediaPlayerHashMap.get(placeName).start();
                Log.i(TAG, "Playing " + Constants.GEOFENCE_MUSIC_NAME.get(placeName));
            }
        }
    }

}
