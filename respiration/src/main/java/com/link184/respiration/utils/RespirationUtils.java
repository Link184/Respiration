package com.link184.respiration.utils;


import com.link184.respiration.repository.NullFirebaseDataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Notification;

public class RespirationUtils {
    public static <T> Notification<List<T>> mapToList(Notification<Map<String, T>> sourceMap) {
        List<T> resultList = new ArrayList<>();
        if (sourceMap.getValue() != null) {
            for (Map.Entry<String, T> entry : sourceMap.getValue().entrySet()) {
                resultList.add(entry.getValue());
            }
            return Notification.createOnNext(resultList);
        }
        return Notification.createOnError(new NullFirebaseDataSnapshot());
    }

    public static <T> T unwrapNotification(Notification<T> source) {
        return source.getValue();
    }
}
