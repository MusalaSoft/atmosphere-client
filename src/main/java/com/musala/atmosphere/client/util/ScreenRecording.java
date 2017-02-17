package com.musala.atmosphere.client.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <i>@ScreenRecording</i> annotation. It is used to annotate the user's test class with screen recording enabled. If
 * the duration is not specified, a recording will start when the {@link com.musala.atmosphere.client.Device device} is
 * created and stop when the device is released.
 *
 * @author dimcho.nedev
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScreenRecording {
    /**
     * The duration of the screen record
     *
     * @return int
     */
    int duration() default ClientConstants.DEFAULT_SCREEN_RECORD_TIME_LIMIT;
}
