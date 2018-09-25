package com.link184.sample.main.fragments.profile;

import com.link184.sample.firebase.SamplePrivateModel;

import androidx.fragment.app.Fragment;

interface ProfileView {
    void newDataReceived(SamplePrivateModel privateModel);

    Fragment getFragment();
}
