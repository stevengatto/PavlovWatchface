package com.example.android.wearable.watchface;

import android.text.format.Time;

/**
 * Created by steven on 1/9/15.
 */
public class FuzzyTime extends Time {

    public FuzzyTime() {
        super();

    }

    @Override
    public void setToNow() {
        set(System.currentTimeMillis() + 1000*60*5);
    }
}
