// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client.util;

/**
 * Container holding the properties of the first {@link ScreenRecording} annotation found up in the stack trace.
 *
 * @author dimcho.nedev
 *
 */
public class ScreenRecordingAnnotationProperties {
    private ScreenRecording screenRecordingAnnotation;

    private Class<?> annotatedClass;

    private boolean enabled = false;

    private int duration = ClientConstants.DEFAULT_SCREEN_RECORD_TIME_LIMIT;

    public ScreenRecordingAnnotationProperties() {
        ClassLocator annotationLocator = new ClassLocator(ScreenRecording.class);
        annotatedClass = annotationLocator.getFirstAnnotatedClass();
        if (annotatedClass != null) {
            enabled = true;
            screenRecordingAnnotation = annotatedClass.getAnnotation(ScreenRecording.class);

            if (screenRecordingAnnotation.duration() <= ClientConstants.DEFAULT_SCREEN_RECORD_TIME_LIMIT) {
                this.duration = screenRecordingAnnotation.duration();
            }
        }
    }

    /**
     * If screen recording is enabled
     *
     * @return <code>true</code> if a screen recording annotation is found, otherwise returns <code>false</code>.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Gets a screen record duration.
     *
     * @return a duration
     */
    public int getDuration() {
        return this.duration;
    }
}
