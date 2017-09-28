package com.link184.sample.main.fragments.profile;

import android.support.v4.app.Fragment;

import com.link184.sample.firebase.SamplePrivateModel;

/**
 * Created by erza on 9/24/17.
 */

interface ProfileView {
    void newDataReceived(SamplePrivateModel privateModel);

    Fragment getFragment();
}
