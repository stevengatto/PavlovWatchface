package com.example.android.wearable.watchface;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dharam on 1/10/2015.
 */
public class MinuteService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MinuteService";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Minute Service Started");
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        sharedPreferences = getSharedPreferences("AheadOfTime", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Minute Service Stopped");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    private void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            long currentTime = System.currentTimeMillis();
            if (mLastLocation != null) {
                System.out.println("Location - " + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "," + mLastLocation.getAccuracy());
                if (mLastLocation.getAccuracy() > 30) {
                    File locationFileDirectory = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.aheadoftime/");
                    if (!locationFileDirectory.exists()) {
                        locationFileDirectory.mkdirs();
                    }
                    File locationFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.aheadoftime/locations.dat");
                    FileWriter fileWriter = new FileWriter(locationFile, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(currentTime + "," + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + "," + mLastLocation.getAccuracy());
                    bufferedWriter.newLine();
                    bufferedWriter.close();

                    FileReader fileReader = new FileReader(locationFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    List<String> allLocations = new ArrayList<>();
                    String nextLine;
                    while ((nextLine = bufferedReader.readLine()) != null) {
                        allLocations.add(nextLine);
                    }
                    List<Float> distances = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        double latitude = Double.parseDouble(allLocations.get(allLocations.size() - (i + 1)).split(",")[1]);
                        double longitude = Double.parseDouble(allLocations.get(allLocations.size() - (i + 1)).split(",")[2]);
                        float results[] = new float[1];
                        Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), latitude, longitude, results);
                        Log.d(TAG, "Distance = " + results[0]);
                        distances.add(results[0]);
                        if (results[0] > 30) {
                            editor.putBoolean("KEY_IS_TRAVELLING", true);
                            editor.commit();
                        }
                    }

                    boolean isAtNewLocation = false;
                    if (sharedPreferences.getBoolean("KEY_IS_TRAVELLING", false)) {
                        isAtNewLocation = true;
                        for (int i = 0; i < distances.size(); i++) {
                            if (distances.get(i) > 30) {
                                isAtNewLocation = false;
                            }
                        }
                    }

                    if (isAtNewLocation) {
                        Log.d(TAG, "User at new location");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
