package com.link184.sample.main;

import android.app.Activity;
import android.os.Bundle;

import com.link184.sample.R;
import com.link184.sample.main.fragments.FragmentState;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleActivity extends AppCompatActivity implements SampleView{
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.pager) ViewPager viewPager;

    private SamplePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        ButterKnife.bind(this);

        presenter = new SamplePresenter(this);

        SamplePageAdapter pagerAdapter = new SamplePageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        if (presenter.isUserAuthenticated()) {
            viewPager.setCurrentItem(FragmentState.PROFILE.ordinal());
        } else {
            viewPager.setCurrentItem(FragmentState.AUTHENTICATION.ordinal());
        }
        viewPager.setOnTouchListener((v, event) -> true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(FragmentState.values()[position].getName());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView();
    }

    @Override
    protected void onPause() {
        presenter.detachView();
        super.onPause();
    }

    @Override
    public void navigateTo(FragmentState fragmentState) {
        viewPager.setCurrentItem(fragmentState.ordinal());
    }

    @Override
    public Activity getContext() {
        return this;
    }
}
