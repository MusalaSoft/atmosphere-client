package com.musala.atmosphere.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.musala.atmosphere.client.device.log.LogCatLevel;

/**
 * <i>@Logcat</i> annotation. It is used to annotate the user's test class with LogCat enabled.
 *
 * @author dimcho.nedev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Logcat {
    public static final String DEFAULT_LOGCAT_PATH = ".";

    public static final String DEFAULT_TAG = "";
    /*
     * A path to the local output folder.
     */
    String localOuputPath() default DEFAULT_LOGCAT_PATH;

    /*
     * An array from log level filters.
     */
    LogCatLevel[] filter() default LogCatLevel.VERBOSE;

    /*
     * A tag filter.
     */
    String tag() default DEFAULT_TAG;
}
