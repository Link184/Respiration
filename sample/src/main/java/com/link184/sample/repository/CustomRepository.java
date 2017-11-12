package com.link184.sample.repository;

import com.link184.respiration.repository.Configuration;
import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration_annotation.RespirationRepository;
import com.link184.sample.firebase.SamplePrivateModel;

/**
 * Created by jora on 11/12/17.
 */

@RespirationRepository
public class CustomRepository extends GeneralRepository<SamplePrivateModel>{
    public CustomRepository(Configuration<SamplePrivateModel> repositoryConfig) {
        super(repositoryConfig);
    }
}
