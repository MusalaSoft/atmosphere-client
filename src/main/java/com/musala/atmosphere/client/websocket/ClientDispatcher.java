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
import com.musala.atmosphere.client.util.ServerConnectionProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.exception.NoDeviceMatchingTheGivenSelectorException;
import com.musala.atmosphere.commons.cs.util.ClientServerGsonUtil;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;
import com.musala.atmosphere.commons.exceptions.NoAvailableDeviceFoundException;
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

    private static final int WAIT_FOR_RESPONSE_TIME = 30_000;

    private static final int HANDLE_LOST_CONNECTION_RETRIES = 5;

    private static final String SERVER_URI = "ws://%s:%s/client_server";

    private Session session;

    private WebSocketCommunicatorManager communicationManager = WebSocketCommunicatorManager.getInstance();

    private final IJsonUtil jsonUtil = new ClientServerGsonUtil();

    private ServerConnectionProperties serverConnectionProperties;

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
    public void connectToServer(ServerConnectionProperties serverConnectionProperties) {
        LOGGER.info("Connecting to server...");

        this.serverConnectionProperties = serverConnectionProperties;
        String serverAddress = serverConnectionProperties.getIp();
        int webSocketPort = serverConnectionProperties.getPort();
        int connectionRetryLimit = serverConnectionProperties.getConnectionRetryLimit();

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
     * @return the result of the action sent from the Agent
     * @throws CommandFailedException
     *         - when failed to send the request action to the Server
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
     * TODO: Consider to migrate this wait logic on the Server.
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
        do {
            RequestMessage request = new RequestMessage(MessageAction.DEVICE_ALLOCATION_INFORMATION, deviceSelector);
            ResponseMessage response = sendRequest(request, session);
            if (response.getMessageAction() != MessageAction.ERROR) {
                return (DeviceAllocationInformation) response.getData();
            }

            if (response.getException() instanceof NoDeviceMatchingTheGivenSelectorException) {
                throw (NoDeviceMatchingTheGivenSelectorException) response.getException();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Nothing to do here.
            }
        } while (--allocateDeviceRetryCount > 0);

        throw new NoAvailableDeviceFoundException();
    }

    /**
     * Sends a release device request to the Server and receives a response.
     *
     * @param deviceInformation
     *         - describes a device that will be released
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

    /**
     * Sends a request and waits for a certain time for a response. If the connection is lost it tries to reconnect.
     */
    private ResponseMessage sendRequest(RequestMessage request, Session session) {
        // The session identifier must be unique not only for WebSocket Session but also for action in case with multiple
        // threads scenario. Otherwise the WebSocketCommunicationManager can return a response from another action.
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
                lockObject.wait(WAIT_FOR_RESPONSE_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("Waiting for response interrupted.", e);
            }
        }

        LOGGER.debug("Getting the response...");

        return communicationManager.popResponse(sessionId);
    }

}
