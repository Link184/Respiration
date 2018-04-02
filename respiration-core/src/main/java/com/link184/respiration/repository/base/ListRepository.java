package com.link184.respiration.repository.base;

import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.link184.respiration.subscribers.ListSubscriberRespiration;
import com.link184.respiration.subscribers.SubscriberRespiration;

import java.util.List;
import java.util.Map;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public interface ListRepository<T> {

    void onNewDataReceived(Map<String, T> value);

    void subscribe(ListSubscriberRespiration<T> subscriber);

    /**
     * Subscription to specific item.
     *
     * @param itemId firebase object key to subscribe on.
     */
    void subscribeToItem(String itemId, SubscriberRespiration<T> subscriber);

    void subscribeToList(SubscriberRespiration<List<T>> subscriber);

    void subscribeToList(Consumer<? super List<T>> onNext);

    void subscribeToList(Consumer<? super List<T>> onNext, Consumer<? super Throwable> onError);

    void subscribeToList(Consumer<? super List<T>> onNext,
                         Consumer<? super Throwable> onError, Action onComplete);

    void subscribeToList(Consumer<? super List<T>> onNext,
                                Consumer<? super Throwable> onError, Action onComplete,
                                Consumer<? super Disposable> onSubscribe);

    /**
     * Use this method instead of {@link super.asObservable()}
     */
    Observable<Notification<Map<String, T>>> asListObservable();

    void removeValue(String itemId);

    void removeValue(String itemId, DatabaseReference.CompletionListener completionListener);

    @Nullable
    Map<String, T> getItemsAsMap();

    /**
     * Get items directly form cache without subscription. Use carefully, response may be empty.
     */
    List<T> getItems();

    /**
     * Get value directly from cache without subscription.
     *
     * @param itemId local object key.
     */
    @Nullable
    T getValue(String itemId);

    /**
     * Get key of last element directly form cache.
     */
    String getLastKey();

    void setValue(String itemId, T newValue, DatabaseReference.CompletionListener completionListener);

    void setValue(String itemId, T newValue);

    /**
     * Replace the whole list in json node.
     */
    void replaceValue(Map<String, T> newValue, DatabaseReference.CompletionListener completionListener);

    /**
     * Replace the whole list in json node.
     */
    void replaceValue(Map<String, T> newValue);

    /**
     * Add new value to the list with generated auto id.
     */
    void addValue(T newValue);

    void addValue(T newValue, DatabaseReference.CompletionListener onCompleteListener);
}
