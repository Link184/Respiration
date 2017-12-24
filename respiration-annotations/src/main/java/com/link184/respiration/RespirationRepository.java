package com.link184.respiration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RespirationRepository {
    Class<?> dataSnapshotType();

    boolean persistence() default false;

    String[] children() default "";

    boolean isAccessPrivate() default false;
}
