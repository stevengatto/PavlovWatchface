package com.example.android.wearable.watchface;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tagmanager.PreviewActivity;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;


/**
 * Created by Dharam Maniar.
 * Activity is used to listen for questions and set alarms for each question based on the TimeToPrompt.
 */
public class ListenerService extends WearableListenerService {

    private static final String TAG = "ListenerService";

    private static final String START_ACTIVITY_PATH = "/fuzzytime";

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {

            String message = new String(messageEvent.getData());
            if (message.equals("NewLocation")) {
                Log.d(TAG, "Got message for New Location");
            }
        }
    }



    @Override
    public void onPeerConnected(Node peer) {

    }

    @Override
    public void onPeerDisconnected(Node peer) {

    }

}
