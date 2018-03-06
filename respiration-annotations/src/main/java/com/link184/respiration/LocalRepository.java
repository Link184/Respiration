package com.link184.respiration;

/**
 * Created by eugeniu on 3/6/18.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use it to annotate classes which extend GeneralLocalRepository or ListLocalRepository from respiration
 * library. Also annotation can be used for fields inside a annotated with {@link RespirationModule}
 * class.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface LocalRepository {

    /**
     * Give me firebase model class.
     */
    Class<?> dataSnapshotType();

    /**
     * Path to raw json file from assets which will be copied to android files dir with the same
     * name as asset file.
     */
    String dataBaseAssetPath();

    /**
     *  Database file name which will be stored to the android files dir. Leave it unset if you
     *  want the name to be as asset name.
     */
    String dataBaseName() default "";
}
