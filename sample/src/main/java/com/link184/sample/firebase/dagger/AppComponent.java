package com.link184.sample.firebase.dagger;

import com.link184.sample.main.SamplePresenter;
import com.link184.sample.main.fragments.authentication.AuthenticationPresenter;
import com.link184.sample.main.fragments.profile.ProfilePresenter;
import com.link184.sample.main.fragments.registration.RegistrationPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {FirebaseModule.class})
public interface AppComponent {
    void inject(SamplePresenter samplePresenter);
    void inject(ProfilePresenter profilePresenter);
    void inject(AuthenticationPresenter authenticationPresenter);
    void inject(RegistrationPresenter registrationPresenter);
}
