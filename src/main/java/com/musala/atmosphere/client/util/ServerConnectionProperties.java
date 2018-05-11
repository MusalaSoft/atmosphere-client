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

import com.musala.atmosphere.commons.util.ConnectionProperties;

/**
 * Class containing information about a connection to server. Extends the {@link ConnectionProperties} where a
 * description of the override functions can be found.
 * 
 * @author yordan.petrov
 * 
 */
public class ServerConnectionProperties implements ConnectionProperties {
    protected String serverIp;

    protected int serverPort;

    protected int connectionRetryLimit;

    protected ServerConnectionProperties() {
    }

    public ServerConnectionProperties(String serverIp, int serverPort, int connectionRetryLimit) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.connectionRetryLimit = connectionRetryLimit;
    }

    @Override
    public String getIp() {
        return serverIp;
    }

    @Override
    public void setIp(String serverIp) {
        this.serverIp = serverIp;
    }

    @Override
    public int getPort() {
        return serverPort;
    }

    @Override
    public void setPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public int getConnectionRetryLimit() {
        return connectionRetryLimit;
    }

    @Override
    public void setConnectionRetryLimit(int connectionRetryLimit) {
        this.connectionRetryLimit = connectionRetryLimit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (!(other instanceof ServerConnectionProperties)) {
            return false;
        }
        ServerConnectionProperties otherServerConnectionProperties = (ServerConnectionProperties) other;
        boolean isIpEqual = serverIp.equals(otherServerConnectionProperties.getIp());
        boolean isPortEqual = serverPort == otherServerConnectionProperties.getPort();

        return isIpEqual && isPortEqual;
    }

    @Override
    public int hashCode() {
        return serverIp.hashCode() + serverPort;
    }
}
