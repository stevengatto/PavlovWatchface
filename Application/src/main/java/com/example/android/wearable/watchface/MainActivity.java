package com.example.android.wearable.watchface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Dharam on 1/10/2015.
 */
public class MainActivity extends PreferenceActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    private static final String TAG = "MainActivity";

    private static final String START_ACTIVITY_PATH = "/fuzzytime";
    private boolean mResolvingError = false;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        addPreferencesFromResource(R.xml.preferences);
        setAlarm();

        buildGoogleApiClient();

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String keyMean = getResources().getString(R.string.key_preference_mean);
                String keyStdev= getResources().getString(R.string.key_preference_stdev);
                new StartWearableActivityTask().execute("MEAN:" + prefs.getString(keyMean, "5")
                        + ",STDEV:" + prefs.getString(keyStdev, "2"));
            }
        });

        Preference button = findPreference(getResources().getString(R.string.key_preference_button));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                new StartWearableActivityTask().execute("NewLocation");
                return true;
            }
        });
    }

    private void setAlarm() {
        Intent minuteServiceIntent = new Intent(getApplicationContext(),
                MinuteService.class);

        PendingIntent mAlarmSender = PendingIntent
                .getService(getApplicationContext(), 16486, minuteServiceIntent,
                        0);

        // We want the alarm to go off at the start of the next minute
        long scheduleTime = System.currentTimeMillis();

        AlarmManager am = (AlarmManager) getSystemService(
                Context.ALARM_SERVICE);
        am.cancel(mAlarmSender);
        am.setRepeating(AlarmManager.RTC_WAKEUP, scheduleTime, 60 * 1000,
                mAlarmSender);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }

    @Override
    public void onPeerConnected(Node node) {

    }

    @Override
    public void onPeerDisconnected(Node node) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mResolvingError = false;
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi
                .getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    private void sendStartActivityMessage(String node, String mQuestion) {

        Wearable.MessageApi.sendMessage(mGoogleApiClient, node,
                START_ACTIVITY_PATH, mQuestion.getBytes())
                .setResultCallback(
                        new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(
                                    MessageApi.SendMessageResult sendMessageResult) {
                                if (!sendMessageResult.getStatus().isSuccess()) {
                                    Log.e(TAG,
                                            "Failed to send message with status code: "
                                                    + sendMessageResult.getStatus()
                                                    .getStatusCode());
                                }
                            }
                        });
    }

    private class StartWearableActivityTask extends
            AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                System.out.println("sending message = " + params[0]);
                sendStartActivityMessage(node, params[0]);
            }
            return null;
        }
    }
}
