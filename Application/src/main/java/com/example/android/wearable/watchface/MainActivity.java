package com.example.android.wearable.watchface;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Dharam on 1/10/2015.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAlarm();
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
