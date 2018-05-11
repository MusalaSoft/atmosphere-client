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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <i>@Server</i> annotation. It is used to annotate the user's test class with the IP and port for connection with the
 * server, and retry limit to connect.
 *
 * @author vladimir.vladimirov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Server {
    /**
     * IP address of the server.
     *
     * @return String
     */
    String ip();

    /**
     * Port of the server.
     *
     * @return int
     */
    int port();

    /**
     * Maximum attempts to connect to server. Used on initial connect to server and when link to the server was lost. If
     * zero or negative, then only one attempt is made to connect.
     *
     * @return int
     */
    int connectionRetryLimit();
}
