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

package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when an Activity failed to start for some reason like invalid package or Activity of an application that it's
 * not installed on the device.
 * 
 * @author georgi.gaydarov
 * 
 */
public class ActivityStartingException extends Exception {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = 1928725919257296196L;

    public ActivityStartingException() {
    }

    public ActivityStartingException(String message) {
        super(message);
    }

    public ActivityStartingException(String message, Throwable inner) {
        super(message, inner);
    }
}
