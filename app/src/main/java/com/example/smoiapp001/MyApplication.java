package com.example.smoiapp001;

import android.app.Application;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*Log.i("App",BuildConfig.DEBUG+"");*/
        if(BuildConfig.DEBUG){
            System.out.println("DEBUG!");
            Timber.plant(new Timber.DebugTree());
        }
    }
}
