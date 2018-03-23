package com.link184.sample.modules;

import com.link184.respiration.LocalRepository;
import com.link184.respiration.RespirationModule;
import com.link184.respiration.repository.local.LocalGeneralRepository;
import com.link184.respiration.repository.local.LocalListRepository;
import com.link184.sample.local.User;
import com.link184.sample.local.workout.UserWorkout;

/**
 * Created by eugeniu on 3/22/18.
 */

@RespirationModule
public class LocalModule {
    private final String TEST_ASSET_DB_NAME = "user_db.json";
    private final String TEST_ASSET_WORKOUT_DB_NAME = "workout_db.json";

    @LocalRepository(dataSnapshotType = User.class,
            children = {"userData", "user"},
            dataBaseAssetPath = TEST_ASSET_DB_NAME)
    private LocalGeneralRepository localUserRepository;

    @LocalRepository(dataSnapshotType = User.class,
            children = {"userData", "user"},
            dataBaseAssetPath = TEST_ASSET_DB_NAME)
    private LocalListRepository localUserListRepository;

    @LocalRepository(dataSnapshotType = UserWorkout.class,
            children = {"userData", "user"},
            dataBaseAssetPath = TEST_ASSET_WORKOUT_DB_NAME)
    private LocalListRepository localWorkoutListRepository;
}
