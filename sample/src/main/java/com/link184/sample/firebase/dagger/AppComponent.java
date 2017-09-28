package com.link184.sample.firebase.dagger;

import com.link184.sample.main.SampleActivity;
import com.link184.sample.main.fragments.AuthenticationFragment;
import com.link184.sample.main.fragments.profile.ProfilePresenter;
import com.link184.sample.main.fragments.registration.RegistrationPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FirebaseModule.class})
public interface AppComponent {
    void inject(SampleActivity activity);
    void inject(ProfilePresenter profilePresenter);
    void inject(AuthenticationFragment authenticationFragment);
    void inject(RegistrationPresenter registrationPresenter);
}
