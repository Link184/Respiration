package com.link184.sample.main;


import android.app.Activity;

import com.link184.sample.main.fragments.FragmentState;

interface SampleView {
    Activity getContext();

    void navigateTo(FragmentState fragmentState);
}
