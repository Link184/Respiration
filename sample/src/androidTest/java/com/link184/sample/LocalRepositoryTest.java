package com.link184.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.link184.respiration.repository.local.GeneralLocalRepository;
import com.link184.respiration.repository.local.LocalConfiguration;
import com.link184.sample.main.SampleActivity;
import com.link184.sample.model.local.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.observers.TestObserver;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Ryzen on 3/2/2018.
 */

@RunWith(AndroidJUnit4.class)
public class LocalRepositoryTest {
    private final String TAG = getClass().getSimpleName();
    private final String TEST_ASSET_DB_NAME = "test_db.json";

    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);

    @Test
    public void localRepositoryTest() {
        activityTestRule.getActivity().getResources().getAssets();
        LocalConfiguration localConfiguration = new LocalConfiguration<>(User.class);
        localConfiguration.setAssetDbFilePath(TEST_ASSET_DB_NAME);
        localConfiguration.setDatabaseChildren("userData", "user");
        GeneralLocalRepository<User> generalLocalRepository =
                new GeneralLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
        TestObserver<User> userTestObserver = new TestObserver<User>() {
            @Override
            public void onNext(User user) {
                Log.e(TAG, "onNext: ");
                assertTrue(user != null);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "onError: ", t);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                Log.e(TAG, "onComplete: ");
            }
        };
        generalLocalRepository.subscribe(userTestObserver::onNext, userTestObserver::onError, userTestObserver::onComplete);

        User value = generalLocalRepository.getValue();
        assertNotNull(value);
    }
}
