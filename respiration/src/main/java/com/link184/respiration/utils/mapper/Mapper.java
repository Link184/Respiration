package com.link184.respiration.utils.mapper;

/**
 * Created by jora on 11/12/17.
 */

/**
 * Universal data mapper class
 * @param <S> source object
 * @param <R> result object
 */
public interface Mapper<S, R> {
    R transform(S source);
}
