package com.link184.respiration.utils;


import com.link184.respiration.exceptions.NullDataSnapshot;
import com.link184.respiration.utils.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Notification;

public class RespirationUtils {
    public static <T> Notification<List<T>> mapToList(Notification<Map<String, T>> sourceMap) {
        List<T> resultList = new ArrayList<>();
        if (sourceMap.isOnNext()) {
            if (sourceMap.getValue() != null) {
                for (Map.Entry<String, T> entry : sourceMap.getValue().entrySet()) {
                    resultList.add(entry.getValue());
                }
                return Notification.createOnNext(resultList);
            }
        } else if (sourceMap.isOnError()) {
            return Notification.createOnError(sourceMap.getError());
        }
        return Notification.createOnError(new NullDataSnapshot());
    }

    /**
     * Map from {@link Notification<List<S>>} to {@link Notification<List<R>>}
     */
    public static <S, R> Notification<List<R>> mapList(Notification<List<S>> sourceList, Mapper<S, R> mapper) {
        if (sourceList.isOnNext()) {
            return Notification.createOnNext(mapList(sourceList.getValue(), mapper));
        } else if (sourceList.isOnError()){
            return Notification.createOnError(sourceList.getError());
        }
        return Notification.createOnError(new NullDataSnapshot());
    }
    /**
     * Map from {@link List<S>} to {@link List<R>}
     */
    public static <S, R> List<R> mapList(List<S> sourceList, Mapper<S, R> mapper) {
        List<R> resultList = new ArrayList<>();
        for (S source : sourceList) {
            resultList.add(mapItem(source, mapper));
        }
        return resultList;
    }

    /**
     * Map from {@link <S> to <R>}
     */
    public static <S, R> R mapItem(S sourceItem, Mapper<S, R> mapper) {
        return mapper.transform(sourceItem);
    }
}
