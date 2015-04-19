package com.stevengatto.android.wearable.pavlov;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Dharam on 12/15/2014.
 */
public class PromptActivity extends Activity implements View.OnClickListener {

    public static final long[] VIBRATE_INTENSE = {1000, 200, 1000, 200, 500,
            250, 500, 250, 500,
            250, 500, 250, 250, 250, 250, 250, 250, 250, 250, 250, 250, 100,
            100, 100, 100, 100,
            100, 100, 1000, 2000};

    private static final String TAG = "PromptActivity";

    private PowerManager.WakeLock mWakeLock;

    private Vibrator mVibrator;

    private Boolean isQuestionAnswered = false;
    private Boolean isQuestionDismissed = false;

    private TextView question;
    private Button answer1;
    private Button answer2;
    private CountDownTimer timer;

    private Activity activity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PowerManager pm = (PowerManager) getSystemService(
                Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        this.mWakeLock.acquire();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        setupAndDisplayQuestion();

        sharedPreferences = getSharedPreferences("FuzzyTime", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        final int repromptTimes = 1;
        final int displayTime = 60;

        activity = this;
        timer = new CountDownTimer(displayTime * (repromptTimes + 1) * 1000,
                1000) {
            int repromptCount = 0;
            int count = 0;

            //TODO ADD FUNCTIONALITY FOR REPROMPT WAIT TIME
            @Override
            public void onTick(long millisUntilFinished) {
                if (!(isQuestionAnswered || isQuestionDismissed)) {
                    count++;
                    if (count > displayTime) {
                        repromptCount++;
                        mVibrator.vibrate(VIBRATE_INTENSE, -1);
                        count = 0;
                    }
                }
            }

            @Override
            public void onFinish() {
                activity.finish();
            }
        };
        timer.start();
        mVibrator.vibrate(VIBRATE_INTENSE, -1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isQuestionDismissed = true;
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        mVibrator.cancel();
        releaseWakeLock();
    }

    @Override
    public void onClick(View v) {
        isQuestionAnswered = true;
        int maxMean = sharedPreferences.getInt("KEY_MAX_MEAN", 10);
        int userValue = sharedPreferences.getInt("key_mean", (maxMean / 2));

        if (v.getId() == answer1.getId()) {
            if (userValue > 0) {
                editor.putInt("key_mean", (userValue - 1));
                editor.commit();
            }
        }
        if (v.getId() == answer2.getId()) {
            if (userValue < maxMean) {
                editor.putInt("key_mean", (userValue + 1));
                editor.commit();
            }
        }
        System.out.println("Mean value after answer = " + sharedPreferences.getInt("key_mean", (maxMean / 2)));
        this.finish();
    }

    private void releaseWakeLock() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    private void setupAndDisplayQuestion() {

        setContentView(R.layout.question_style1);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(
                new WatchViewStub.OnLayoutInflatedListener() {
                    @Override
                    public void onLayoutInflated(WatchViewStub stub) {
                        question = (TextView) stub
                                .findViewById(R.id.question);
                        question.setText("Did you arrive on time?");
                        answer1 = (Button) stub.findViewById(R.id.answer1);
                        answer1.setText("Yes");
                        answer2 = (Button) stub.findViewById(R.id.answer2);
                        answer2.setText("No");
                        answer1.setOnClickListener(PromptActivity.this);
                        answer2.setOnClickListener(PromptActivity.this);
                    }
                });
    }

}
