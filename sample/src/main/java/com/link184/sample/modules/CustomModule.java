package com.link184.sample.modules;

import com.link184.respiration.RespirationModule;
import com.link184.respiration.RespirationRepository;
import com.link184.respiration.repository.ListRepository;

/**
 * Created by jora on 11/25/17.
 */

@RespirationModule
public class CustomModule {
    @RespirationRepository(dataSnapshotType = String.class)
    public ListRepository ololo;

    public String ignoreeee;
}
