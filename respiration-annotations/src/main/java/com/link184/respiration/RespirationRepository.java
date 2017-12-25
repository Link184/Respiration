package com.link184.respiration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use it to annotate classes which extend GeneralRepository or ListRepository from respiration
 * library. Also annotation can be used for fields inside a annotated with {@link RespirationModule}
 * class.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RespirationRepository {
    /**
     * Use it as database reference children. If you got to use firebase userId as database reference
     * path you must always reset this path on each firebase auth state change. Or you can just use
     * this constant to avoid boring auth listening.
     */
    String USER_ID = "user_id";

    /**
     * Give me firebase model class.
     */
    Class<?> dataSnapshotType();

    /**
     * Set firebaseDatabaseRef.setPersistenceEnabled(). Can be set only one time.
     */
    boolean persistence() default false;

    /**
     * Firebase database reference children.
     */
    String[] children() default "";

    /**
     * Set as true if the repository can be accessed only by authenticated users.
     */
    boolean isAccessPrivate() default false;
}
