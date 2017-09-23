package com.link184.sample.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.R;
import com.link184.sample.SampleApplication;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;
import com.link184.sample.firebase.dagger.FirebaseModule;
import com.link184.sample.main.fragments.FragmentState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.pager) ViewPager viewPager;

    @Inject
    GeneralRepository<SamplePrivateModel> privateRepository;
    @Inject
    GeneralRepository<SamplePublicModel> publicRepository;

    private SubscriberFirebase<SamplePublicModel> publicRepositorySubscriber;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        ButterKnife.bind(this);

        ((SampleApplication) getApplication()).getAppComponent().inject(this);

        SamplePageAdapter pagerAdapter = new SamplePageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        if (publicRepository.isUserAuthenticated()) {
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
        publicRepositorySubscriber = new SubscriberFirebase<SamplePublicModel>() {
            @Override
            public void onSuccess(SamplePublicModel samplePublicModel) {

            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);
            }
        };
        publicRepository.subscribe(publicRepositorySubscriber);

        authStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                privateRepository.resetRepository(FirebaseModule.SAMPLE_PRIVATE_CHILD,
                        firebaseAuth.getCurrentUser().getUid());
                navigateTo(FragmentState.PROFILE);
            } else {
                navigateTo(FragmentState.AUTHENTICATION);
            }
        };
        publicRepository.getFirebaseAuth().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        publicRepositorySubscriber.dispose();
        publicRepository.getFirebaseAuth().removeAuthStateListener(authStateListener);
        super.onPause();
    }

    public void navigateTo(FragmentState fragmentState) {
        viewPager.setCurrentItem(fragmentState.ordinal());
    }
}
