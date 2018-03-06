package com.link184.respiration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jora on 11/25/17.
 */

/**
 * Use to annotate classes which store {@link FirebaseRepository} fields.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RespirationModule {
}
