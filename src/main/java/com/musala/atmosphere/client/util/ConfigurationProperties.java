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
 * Enumeration class containing a configuration properties for a test project.
 *
 * @author dimcho.nedev
 *
 */
public enum ConfigurationProperties {
    SERVER_IP("server.ip"),
    SERVER_PORT("server.port"),
    SERVER_CONNECTION_RETRIES("server.connectionRetries"),
    FTP_SERVER("ftp.server"),
    FTP_DIR("ftp.dir"),
    IMPLICIT_WAIT_TIMEOUT("implicit.wait.timeout");

    private String value;

    private ConfigurationProperties(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
