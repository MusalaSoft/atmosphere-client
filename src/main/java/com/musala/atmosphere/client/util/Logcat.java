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
