package com.link184.sample.firebase.dagger;

import com.link184.sample.main.SampleActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FirebaseModule.class})
public interface AppComponent {
    void inject(SampleActivity activity);
}
