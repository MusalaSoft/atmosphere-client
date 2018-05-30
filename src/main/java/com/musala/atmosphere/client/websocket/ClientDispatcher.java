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

package com.musala.atmosphere.client.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.util.ConfigurationPropertiesLoader;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.exception.NoDeviceMatchingTheGivenSelectorException;
import com.musala.atmosphere.commons.cs.util.ClientServerGsonUtil;
import com.musala.atmosphere.commons.exceptions.NoAvailableDeviceFoundException;
import com.musala.atmosphere.commons.util.ConnectionProperties;
import com.musala.atmosphere.commons.util.Pair;
import com.musala.atmosphere.commons.websocket.WebSocketCommunicatorManager;
import com.musala.atmosphere.commons.websocket.message.MessageAction;
import com.musala.atmosphere.commons.websocket.message.RequestMessage;
import com.musala.atmosphere.commons.websocket.message.ResponseMessage;
import com.musala.atmosphere.commons.websocket.util.IJsonUtil;

/**
 * Dispatches the {@link RequestMessage request} and {@link ResponseMessage response} messages.
 *
 * @author dimcho.nedev
 *
 */
public class ClientDispatcher {
    private static final Logger LOGGER = Logger.getLogger(ClientDispatcher.class.getCanonicalName());

    private static final int HANDLE_LOST_CONNECTION_RETRIES = 5;

    private static final String SERVER_URI = "ws://%s:%s/client_server";

    private int waitForResponseTime = 30_000; // 30 seconds

    private int waitForDeviceTime = 300_000; // 5 minutes

    private Session session;

    private WebSocketCommunicatorManager communicationManager = WebSocketCommunicatorManager.getInstance();

    private final IJsonUtil jsonUtil = new ClientServerGsonUtil();

    private ConnectionProperties serverConnectionProperties;

    private static class DispatcherLoader {
        private static final ClientDispatcher INSTANCE = new ClientDispatcher();
    }

    public static ClientDispatcher getInstance() {
        LOGGER.setLevel(Level.INFO);
        return DispatcherLoader.INSTANCE;
    }

    /**
     * Connects the client to a server.
     *
     * @param serverConnectionProperties
     *        - properties for the server connection (IP, port, connection retry limit)
     */
    public void connectToServer(ConnectionProperties serverConnectionProperties) {
        LOGGER.info("Connecting to server...");

        this.serverConnectionProperties = serverConnectionProperties;
        String serverAddress = serverConnectionProperties.getIp();
        int webSocketPort = serverConnectionProperties.getPort();
        int connectionRetryLimit = serverConnectionProperties.getConnectionRetryLimit();

        // loads the timeouts if the config file exists
        if (ConfigurationPropertiesLoader.isConfigExists()) {
            this.waitForResponseTime = ConfigurationPropertiesLoader.getResponseWaitTimeout();
            this.waitForDeviceTime = ConfigurationPropertiesLoader.getDeviceWaitTimeout();
        }

        connectToServer(serverAddress, webSocketPort, connectionRetryLimit);
    }

    private void connectToServer(String serverAddress, int webSocketPort, int connectionRetryLimit) {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(Integer.MAX_VALUE);
        String uriAddress = String.format(SERVER_URI, serverAddress, webSocketPort);

        Exception innerException = null;

        do {
            try {
                session = container.connectToServer(ClientEndpoint.class, new URI(uriAddress));

                LOGGER.info("Connected to server address: " + uriAddress);
                return;
            } catch (DeploymentException | IOException | URISyntaxException e) {
                innerException = e;
                connectionRetryLimit--;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        } while (connectionRetryLimit > 0);

        LOGGER.error("Connecting to server failed!", innerException);
        throw new ServerConnectionFailedException("Connecting to server retry limit reached.", innerException);
    }

    /**
     * Sends a JSON message with {@link RoutingAction routing message action} to the Server's endpoint.
     *
     * @param deviceId
     *        - identifier of a device
     * @param invocationPasskey
     *        - a passkey for validating the authority for the client device
     * @param action
     *        - {@link MessageAction message action}
     * @param args
     *        - the arguments of the request
     * @return the result of the action sent from the Agent
     * @throws Exception
     *         - when an exception occurs on the Agent during the action execution
     */
    public Object route(String deviceId, long invocationPasskey, RoutingAction action, Object... args)
        throws Exception {
        RequestMessage requestMessage = buildRequest(deviceId, invocationPasskey, action, args);
        ResponseMessage response = sendRequest(requestMessage, session);

        if (response.getException() != null) {
            throw response.getException();
        }

        return response.getData();
    }

    /**
     * Sends asynchronously a JSON message with {@link RoutingAction routing message action} to the Server's endpoint.
     * The request is also expected to be executed asynchronously on the Agent. Used for the requests that doesn't
     * require an immediate response and when blocking the main thread is undesirable(i. g. printing a logcat on the
     * console during a test execution).
     *
     * @param deviceId
     *        - identifier of a device
     * @param invocationPasskey
     *        - a passkey for validating the authority for the client device
     * @param action
     *        - {@link MessageAction message action}
     * @param args
     *        - the arguments of the request
     * @throws ServerConnectionFailedException
     *         - when failed to connect to the Server
     */
    public void routeAsync(String deviceId, long invocationPasskey, RoutingAction action, Object[] args)
        throws ServerConnectionFailedException {
        RequestMessage request = buildRequest(deviceId, invocationPasskey, action, args);
        request.setAsync(true);

        try {
            Future<Void> future = session.getAsyncRemote().sendText(jsonUtil.serialize(request));
            future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new ServerConnectionFailedException();
        }
    }

    private RequestMessage buildRequest(String deviceId, long invocationPasskey, RoutingAction action, Object[] args) {
        RequestMessage requestMessage = new RequestMessage(MessageAction.ROUTING_ACTION, action, args);
        requestMessage.setDeviceId(deviceId);
        requestMessage.setPasskey(invocationPasskey);

        return requestMessage;
    }

    /**
     *
     * Gets a {@link DeviceAllocationInformation} instance with the given {@link DeviceSelector device characteristics}.
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link DeviceAllocationInformation}
     *        instance.
     * @param allocateDeviceRetryCount
     *        - the number of the attempts to get a device
     * @return a {@link DeviceAllocationInformation} instance with the given device selector.
     * @throws NoAvailableDeviceFoundException
     *         - when there is no available device
     */
    public DeviceAllocationInformation getDeviceDescriptor(DeviceSelector deviceSelector,
                                                           int allocateDeviceRetryCount) {
        RequestMessage request = new RequestMessage(MessageAction.DEVICE_ALLOCATION_INFORMATION, deviceSelector);
        ResponseMessage response = sendRequest(request, session, waitForDeviceTime);
        if (response.getMessageAction() != MessageAction.ERROR) {
            return (DeviceAllocationInformation) response.getData();
        }

        if (response.getException() instanceof NoDeviceMatchingTheGivenSelectorException) {
            throw (NoDeviceMatchingTheGivenSelectorException) response.getException();
        }

        throw new NoAvailableDeviceFoundException();
    }

    /**
     * Sends a release device request to the Server and receives a response.
     *
     * @param deviceInformation
     *        - describes a device that will be released
     * @throws Exception
     *         - when an error occurred when trying to release a device
     */
    public void releaseDevice(DeviceAllocationInformation deviceInformation) throws Exception {
        RequestMessage releaseDeviceRequest = new RequestMessage(MessageAction.RELEASE_DEVICE, deviceInformation);

        ResponseMessage response = sendRequest(releaseDeviceRequest, session);
        if (response != null && response.getException() != null) {
            LOGGER.error("An error occurred when trying to release a device.", response.getException());
            throw response.getException();
        }
    }

    /**
     * Sends a request to get a list of all available devices to the Server and receives a response.
     *
     * @return list with serial numbers and models of all available devices
     * @throws Exception
     *         - when fails to get a list of the available devices
     */
    @SuppressWarnings("unchecked")
    public List<Pair<String, String>> getAllAvailableDevices() throws Exception {
        RequestMessage getAllAvailableDevicesRequest = new RequestMessage(MessageAction.GET_ALL_AVAILABLE_DEVICES);

        ResponseMessage response = sendRequest(getAllAvailableDevicesRequest, session);
        if (response.getMessageAction() == MessageAction.ERROR) {
            throw response.getException();
        }

        return (List<Pair<String, String>>) response.getData();
    }

    private ResponseMessage sendRequest(RequestMessage request, Session session) {
        return sendRequest(request, session, waitForResponseTime);
    }

    /**
     * Sends a request and waits for a certain time for a response. If the connection is lost it tries to reconnect.
     */
    private ResponseMessage sendRequest(RequestMessage request, Session session, int wait) {
        // The session identifier must be unique not only for WebSocket Session but also for action in case with
        // multiple threads scenario. Otherwise the WebSocketCommunicationManager can return a response from another
        // action.
        final String sessionId = session.getId() + "_" + request.getMessageAction() + "_" + request.getRoutingAction();
        request.setSessionId(sessionId);

        Object lockObject = communicationManager.getSynchronizationObject(sessionId);

        String requestJSON = jsonUtil.serialize(request);

        try {
            session.getBasicRemote().sendText(requestJSON);
        } catch (IOException e1) {
            connectToServer(serverConnectionProperties.getIp(),
                            serverConnectionProperties.getPort(),
                            HANDLE_LOST_CONNECTION_RETRIES);
        }

        LOGGER.debug("Sending request:" + requestJSON);
        LOGGER.debug("Waiting for response...");

        synchronized (lockObject) {
            try {
                lockObject.wait(wait);
            } catch (InterruptedException e) {
                LOGGER.error("Waiting for response interrupted.", e);
            }
        }

        LOGGER.debug("Getting the response...");

        return communicationManager.popResponse(sessionId);
    }

}
