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
        askIfLate();
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
                        askIfLate();
                    }
                    break;
            }
        }
    };


    public boolean shouldAsk(){

        return true;
    }

    public void askIfLate(){

        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(context,PreviewActivity.class);
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                        .setContentTitle("Were you late?")
                        .setContentText("Bottom")
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());



    }

}
