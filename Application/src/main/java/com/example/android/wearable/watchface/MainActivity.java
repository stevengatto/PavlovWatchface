package com.example.android.wearable.watchface;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Dharam on 1/10/2015.
 */
public class MainActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        addPreferencesFromResource(R.xml.preferences);
        setAlarm();

        SharedPreferences prefs = getSharedPreferences("AheadOfTime", MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                getResources().getString(R.string.key_preference_mean); // Key for mean
                getResources().getString(R.string.key_preference_stdev); // Key for stdev
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
}
