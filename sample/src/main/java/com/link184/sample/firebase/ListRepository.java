package com.link184.sample.firebase;

import com.link184.respiration.repository.Configuration;
import com.link184.respiration.repository.GeneralRepository;
import com.link184.respiration.subscribers.SubscriberFirebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Notification;

/**
 * Created by erza on 9/23/17.
 */

public class ListRepository<T> extends GeneralRepository<Map<String, T>> {
    protected ListRepository(boolean persistence, boolean accessPrivate, Configuration<Map<String, T>> repositoryConfig) {
        super(persistence, accessPrivate, repositoryConfig);
    }

    public void subscribeToList(SubscriberFirebase<T> subscriberFirebase) {
        publishSubject.map(this::mapToList);
    }

    private List<T> mapToList(Notification<Map<String, T>> sourceMap) {
        List<T> resultList = new ArrayList<>();
        for (Map.Entry<String, T> entry: sourceMap.getValue().entrySet()) {
            resultList.add(entry.getValue());
        }
        return resultList;
    }
}
