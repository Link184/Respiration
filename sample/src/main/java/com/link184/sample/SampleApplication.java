package com.link184.sample;

import android.app.Application;

import com.link184.sample.firebase.dagger.AppComponent;
import com.link184.sample.firebase.dagger.DaggerAppComponent;

public class SampleApplication extends Application {
    private AppComponent appComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.create();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
