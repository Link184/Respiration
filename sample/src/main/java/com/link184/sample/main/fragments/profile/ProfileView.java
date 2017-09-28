package com.link184.sample.main.fragments.profile;

import android.support.v4.app.Fragment;

import com.link184.sample.firebase.SamplePrivateModel;

interface ProfileView {
    void newDataReceived(SamplePrivateModel privateModel);

    Fragment getFragment();
}
