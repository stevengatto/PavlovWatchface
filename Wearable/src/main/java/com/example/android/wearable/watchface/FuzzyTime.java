package com.example.android.wearable.watchface;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.WatchFaceService;
import android.text.format.Time;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import com.google.android.gms.tagmanager.PreviewActivity;

/**
 * Created by steven on 1/9/15.
 */
public class FuzzyTime extends Time {

    public static final String TAG = "FuzzyTime";
    static final int MSG_NEW_LOC = 1912391;

    Context context;

    public FuzzyTime(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void setToNow() {
        set(System.currentTimeMillis() + 1000*60*5);
    }


    /** Handler to update the time once a second in interactive mode. */
    final Handler mUpdateTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_NEW_LOC:



                    //// need to register



                    Log.v(TAG, "new location");
                    if (shouldAsk()) {

                    }
                    break;
            }
        }
    };


    public boolean shouldAsk(){

        return true;
    }

}
