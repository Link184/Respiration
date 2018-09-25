package com.link184.sample.main.fragments;


import com.link184.sample.main.fragments.authentication.AuthenticationFragment;
import com.link184.sample.main.fragments.profile.ProfileFragment;
import com.link184.sample.main.fragments.registration.RegistrationFragment;

import androidx.fragment.app.Fragment;

public enum FragmentState {
    PROFILE("Profile", new ProfileFragment()),
    AUTHENTICATION("Authentication", new AuthenticationFragment()),
    REGISTRATION("Registration", new RegistrationFragment());

    private final String name;
    private final Fragment fragment;
    FragmentState(String name, Fragment fragment) {
        this.name = name;
        this.fragment = fragment;
    }

    public String getName() {
        return name;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
