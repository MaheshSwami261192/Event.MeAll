package com.prometteur.myevents;

import android.app.Application;
import android.graphics.Typeface;

import androidx.core.graphics.TypefaceCompatUtil;

import com.prometteur.myevents.Utils.TypefaceUtil;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Helvetica.ttf");
    }
}
