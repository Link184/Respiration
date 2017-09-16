package com.link184.sample.firebase.dagger;

import com.link184.sample.main.SampleActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Alchimy on 9/10/2017.
 */

@Singleton
@Component(modules = {FirebaseModule.class})
public interface AppComponent {
    void inject(SampleActivity activity);
}
