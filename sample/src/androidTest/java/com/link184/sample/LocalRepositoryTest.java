package com.link184.sample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.link184.respiration.repository.local.GeneralLocalRepository;
import com.link184.respiration.repository.local.LocalConfiguration;
import com.link184.sample.main.SampleActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ryzen on 3/2/2018.
 */

@RunWith(AndroidJUnit4.class)
public class LocalRepositoryTest {
    private final String TEST_ASSET_DB_NAME = "test_db.json";

    @Rule
    public ActivityTestRule<SampleActivity> activityTestRule = new ActivityTestRule<>(SampleActivity.class);

    @Test
    public void localRepositoryTest() {
        activityTestRule.getActivity().getResources().getAssets();
        LocalConfiguration localConfiguration = new LocalConfiguration();
        localConfiguration.setAssetDbFilePath(TEST_ASSET_DB_NAME);
        GeneralLocalRepository<String> generalLocalRepository =
                new GeneralLocalRepository<>(activityTestRule.getActivity(), localConfiguration);
    }
}
