package com.link184.sample.main;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.link184.sample.main.fragments.FragmentState;

public class SamplePageAdapter extends FragmentPagerAdapter {
    public SamplePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentState.values()[position].getFragment();
    }

    @Override
    public int getCount() {
        return FragmentState.values().length;
    }
}
