package com.link184.respiration.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Ryzen on 3/2/2018.
 */

public class Preconditions {
    @NonNull
    public static <T> T checkNotNull(@Nullable T arg) {
        return checkNotNull(arg, "Argument must not be null");
    }

    @NonNull
    public static <T> T checkNotNull(@Nullable T arg, @NonNull String message) {
        if (arg == null) {
            throw new NullPointerException(message);
        }
        return arg;
    }
}
