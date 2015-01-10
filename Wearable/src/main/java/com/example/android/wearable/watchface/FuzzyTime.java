package com.example.android.wearable.watchface;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Created by steven on 1/9/15.
 */
public class FuzzyTime extends Time {
    public static final String TAG = "FuzzyTime";
    static final int MSG_NEW_LOC = 1912391;

    private int lastUpdateTime;
    private int currentOffset;
    private static Context context;

    public FuzzyTime(Context context) {
        super();
        this.context = context;
    }

    public FuzzyTime() {
        super();
    }

    @Override
    public void setToNow() {
        int mean = context.getSharedPreferences("FuzzyTime", Context.MODE_PRIVATE)
                .getInt("key_mean", 8);
        int stdev = context.getSharedPreferences("FuzzyTime", Context.MODE_PRIVATE)
                .getInt("key_stdev", 2);
        RandomOffsetUtil.setNormalDistribution(mean, stdev);
        set(System.currentTimeMillis() + getOffset());
    }

    /**
     * offset getter
     * @return current offset in ms
     */
    public int getOffset() {
        return 1000 * 60 * currentOffset;
    }

    public int getOffsetInMinutes() {
        return getOffset()/(1000 * 60);
    }

    /**
     * updates the offset with a positive value sampled from the distribution
     *
     * @param atMinute minute that we are updating offset, so we don't update the offset
     *                 multiple calls in calls from the same minute
     */
    public void updateOffset(int atMinute) {
        if (atMinute != lastUpdateTime)
        {
            lastUpdateTime = atMinute;
            currentOffset = (int) RandomOffsetUtil.sampleNormalDistribution();
        }
    }


    private static class RandomOffsetUtil {

        private static NormalDistribution distribution;

        public static void setNormalDistribution(double mean, double stddev) {

            Log.d(TAG, "Mean is now " + mean + " and stdev is now " + stddev);
            distribution = new NormalDistribution(mean, stddev);

            SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (sharedPreferences.contains("key_mean") && sharedPreferences.contains("key_stdev")) {
                        Log.d(TAG, "Shared preferences have been updated");
                        setNormalDistribution(sharedPreferences.getInt("key_mean", 5),
                                sharedPreferences.getInt("key_stdev", 2));
                    }
                }
            };
            SharedPreferences sharedPreferences = context.getSharedPreferences("FuzzyTime",Context.MODE_PRIVATE);
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        }

        public static double sampleNormalDistribution() {
            double sample = distribution.sample();
            sample = Math.max(0d, sample);
            sample = Math.min(sample, 2 * distribution.getMean());
            return sample;
        }

    }
}
