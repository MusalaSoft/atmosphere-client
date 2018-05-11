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

package com.musala.atmosphere.client;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.DeviceInvocationRejectedException;
import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.websocket.ClientDispatcher;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.exception.DeviceNotFoundException;
import com.musala.atmosphere.commons.cs.exception.InvalidPasskeyException;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;
import com.musala.atmosphere.commons.webelement.exception.WebElementNotPresentException;

/**
 * Class used for handling all requests to the remote devices and the possible exceptions.
 *
 * @author georgi.gaydarov
 *
 */
public class DeviceCommunicator {
    private long invocationPasskey;

    private static final Logger LOGGER = Logger.getLogger(DeviceCommunicator.class.getCanonicalName());

    public static final Object VOID_SUCCESS = new Object();

    private CommandFailedException lastSentActionException;

    private ClientDispatcher dispatcher = ClientDispatcher.getInstance();

    private String deviceId;

    private boolean releasedDevice;

    /**
     * Creates an instance for specified client device.
     *
     * @param wrappedDevice
     *        - the {@link IClientDevice} instance which this instance will communicate with.
     * @param passkey
     *        - the invocation passkey for the client device instance.
     */
    DeviceCommunicator(long passkey, String deviceId) {
        invocationPasskey = passkey;
        this.deviceId = deviceId;
    }

    /**
     * Release the underlying client device so no further invocation can be possible.
     */
    public void release() {
        // TODO: could be more optimal
        releasedDevice = true;
    }

    /**
     * Shows the exception that was thrown during the last action if such exception occurred.
     *
     * @return the {@link CommandFailedException} instance that was thrown during the last action if such exception
     *         occurred, <code>null</code> otherwise.
     */
    public CommandFailedException getLastException() {
        return lastSentActionException;
    }

    /**
     * Requests an action invocation on the device wrapper.
     *
     * @param isAsync
     *        - whether the required action is asynchronous
     * @param action
     *        - a {@link RoutingAction} instance that specifies the action to be invoked
     * @param args
     *        - the action parameters (if required)
     * @return the result from the {@link RoutingAction action} invocation
     */
    public Object sendAction(boolean isAsync, RoutingAction action, Object... args) {
        if (releasedDevice) {
            throw new DeviceReleasedException("The device you are trying to use is released.");
        }

        lastSentActionException = null;
        Object response = null;

        try {
            if (!isAsync) {
                response = dispatcher.route(deviceId, invocationPasskey, action, args);
            } else {
                dispatcher.routeAsync(deviceId, invocationPasskey, action, args);
            }

            if (response == null) {
                response = VOID_SUCCESS;
            }
        } catch (Exception e) {
            if (e instanceof CommandFailedException) {
                LOGGER.error("Executing action failed.", e);
                lastSentActionException = (CommandFailedException) e;
            } else if (e instanceof IllegalArgumentException) {
                throw new IllegalArgumentException(e.getMessage());
            } if (e instanceof InvalidPasskeyException || e instanceof DeviceNotFoundException) {
                LOGGER.error("Executing action was rejected by the server.", e);
                throw new DeviceInvocationRejectedException(e);
            } else if (e instanceof ServerConnectionFailedException) {
                throw new ServerConnectionFailedException("Could not send the routing action (connection failure).");
            } else if (e instanceof WebElementNotPresentException) {
                throw new WebElementNotPresentException(e.getMessage());
            }
        }

        return response;
    }

    /**
     * Requests an routing action to a device and expects a result.
     * 
     * @param action
     *        - {@link RoutingAction routing action}
     * @param args
     *        - arguments of the action
     * @return the result from the {@link RoutingAction action} invocation
     */
    public Object sendAction(RoutingAction action, Object... args) {
        return this.sendAction(false, action, args);
    }

    /**
     * Attempts to reconnect to the ATMOSPHERE server.
     *
     * @throws ServerConnectionFailedException
     * @throws DeviceReleasedException
     */
    private void handleLostConnection() {
        // try {
        // serverConnectionHandler.connect();
        // } catch (ServerConnectionFailedException e) {
        // throw e;
        // }
        // FIXME handle lost connection

        // this message is disinforming and points to an error, which is not occuring!
        // String message = "Reconnecting to server succeeded, but the device was already released.";

        LOGGER.fatal("Lost connection handler reached. This means either a device timed out or a RemoteException was thrown.");
        // throw new DeviceReleasedException(message);

        String message = "Reconnecting to server succeeded, but the device was already released.";

        LOGGER.fatal(message);
        throw new DeviceReleasedException(message);
    }
}
