package com.automatedpsychtest;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by gyra on 10/30/2017.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                         .setDefaultFontPath("fonts/HelveticaNeueLight.ttf")
                         .setFontAttrId(R.attr.fontPath)
                         .build());
    }
}
