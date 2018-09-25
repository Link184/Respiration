package com.link184.sample.main;


import com.link184.sample.main.fragments.FragmentState;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

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
