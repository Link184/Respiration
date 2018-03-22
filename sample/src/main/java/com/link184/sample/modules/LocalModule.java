package com.link184.sample.modules;

import com.link184.respiration.LocalRepository;
import com.link184.respiration.RespirationModule;
import com.link184.respiration.repository.local.GeneralLocalRepository;
import com.link184.sample.local.User;

/**
 * Created by eugeniu on 3/22/18.
 */

@RespirationModule
public class LocalModule {
    private final String TEST_ASSET_DB_NAME = "user_db.json";

    @LocalRepository(dataSnapshotType = User.class,
            children = {"userData", "user"},
            dataBaseAssetPath = TEST_ASSET_DB_NAME)
    private GeneralLocalRepository localUserRepository;
}
