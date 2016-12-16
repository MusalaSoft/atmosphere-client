package com.musala.atmosphere.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.musala.atmosphere.client.device.log.LogCatLevel;

/**
 * TODO: Add a documentation
 *
 * @author dimcho.nedev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Logcat {
    public static final String DEFAULT_LOGCAT_PATH = "./device.log";

    public static final String DEFAULT_TAG = "";
    /*
     * TODO: add a documentation
     */
    String localOuputPath() default DEFAULT_LOGCAT_PATH;

    /*
     * TODO: Add a documentation
     */
    LogCatLevel[] filter() default LogCatLevel.VERBOSE;

    /*
     * TODO: Add a documentation
     */
    String tag() default DEFAULT_TAG;
}
