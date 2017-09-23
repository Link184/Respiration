package com.link184.sample.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.subscribers.SubscriberFirebase;
import com.link184.sample.R;
import com.link184.sample.SampleApplication;
import com.link184.sample.adapters.SamplePageAdapter;
import com.link184.sample.firebase.SamplePrivateModel;
import com.link184.sample.firebase.SamplePublicModel;
import com.link184.sample.fragments.AuthenticationFragment;
import com.link184.sample.fragments.ProfileFragment;
import com.link184.sample.fragments.RegistrationFragment;

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
    private SubscriberFirebase<SamplePrivateModel> privateRepositorySubscriber;
    private Fragment[] fragments = {new AuthenticationFragment(), new ProfileFragment(),
            new RegistrationFragment()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        ButterKnife.bind(this);

        ((SampleApplication) getApplication()).getAppComponent().inject(this);

        SamplePageAdapter pagerAdapter = new SamplePageAdapter(getSupportFragmentManager(), fragments);

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(fragments[position].getClass().getSimpleName());

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

        privateRepositorySubscriber = new SubscriberFirebase<SamplePrivateModel>() {
            @Override
            public void onSuccess(SamplePrivateModel samplePrivateModel) {

            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "onFailure: ", error);

            }
        };

        privateRepository.subscribe(privateRepositorySubscriber);
        publicRepository.subscribe(publicRepositorySubscriber);
    }

    @Override
    protected void onPause() {
        privateRepositorySubscriber.dispose();
        publicRepositorySubscriber.dispose();
        super.onPause();
    }

//    @OnClick(R.id.btnSave)
//    void saveClicked(View v) {
//        SamplePrivateModel samplePrivateModel = new SamplePrivateModel(name.getText().toString(),
//                surname.getText().toString(), Integer.parseInt(age.getText().toString()));
//        privateRepository.setValue(samplePrivateModel);
//    }
//
//    @OnClick(R.id.btnLogin)
//    void loginClicked(View v) {
//        privateRepository.getFirebaseAuth()
//                .signInWithEmailAndPassword("sample@sample.sample", "123123")
//        .addOnCompleteListener(this, task -> {
//            if (task.isSuccessful()) {
//                privateRepository.resetRepository(FirebaseModule.SAMPLE_PRIVATE_CHILD,
//                        privateRepository.getFirebaseAuth().getCurrentUser().getUid());
//            }
//        });
//    }
//
//    @OnClick(R.id.btnLogout)
//    void logoutClicked(View v) {
//        privateRepository.getFirebaseAuth().signOut();
//    }
}
