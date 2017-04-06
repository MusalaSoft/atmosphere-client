package com.musala.atmosphere.client.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.exception.NoDeviceMatchingTheGivenSelectorException;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;
import com.musala.atmosphere.commons.exceptions.NoAvailableDeviceFoundException;
import com.musala.atmosphere.commons.util.Pair;
import com.musala.atmosphere.commons.websocket.WebSocketCommunicatorManager;
import com.musala.atmosphere.commons.websocket.message.ClientServerRequest;
import com.musala.atmosphere.commons.websocket.message.ClientServerResponse;
import com.musala.atmosphere.commons.websocket.message.MessageType;

public class ClientServerWebSocketCommunicator {
    private static final Logger LOGGER = Logger.getLogger(ClientServerWebSocketCommunicator.class.getCanonicalName());

    private static final int ALLOCATE_DEVICE_RETRY_TIMEOUT = 1000;

    private static final int CONNECTION_TIMEOUT = 10000;

    private WebSocketCommunicatorManager communicationManager = WebSocketCommunicatorManager.getInstance();

    private Session session;

    private DeviceAllocationInformation deviceInformation;

    private Gson gson = new Gson();

    /**
     * Creates a new instance of the ClientServerWebSocketCommunicator class and establishes a new connection
     * WebSocket connection to the server.
     */
    public ClientServerWebSocketCommunicator() {
        ClientManager client = ClientManager.createClient();
        try {
            // TODO WebSockets: make the client respect the server connection annotation/properties
            String uriAddress = "ws://localhost:80/server";
            this.session = client.connectToServer(ClientWebSocketEndpoint.class, new URI(uriAddress));
            LOGGER.info("Connected to server: " + uriAddress);
        } catch (DeploymentException | IOException | URISyntaxException e) {
            LOGGER.fatal(e.getMessage());
        }
    }

    /**
     * Returns {@link DeviceAllocationInformation} from the server by the specified {@link DeviceSelector} and
     * maximum wait time (in seconds) the client should wait if such a device is present on the server, but
     * currently allocated by another client.
     *
     * @param deviceSelector
     *        - the {@link DeviceSelector} which should be used to select a device on the server
     * @param maxWaitCount
     *        - the time in milliseconds the builder should wait if a requested device is present on the server, but
     *        currently not available (allocated by another client)
     * @return DeviceAllocationInformation for the requested device, if present
     * @throws NoAvailableDeviceFoundException
     *         - if there is no device on the server, described by the provided selector
     */
    public DeviceAllocationInformation getDeviceDescriptor(DeviceSelector deviceSelector, int maxWaitCount)
            throws NoAvailableDeviceFoundException {
        int maxWaitCountInSeconds = maxWaitCount / ALLOCATE_DEVICE_RETRY_TIMEOUT;
        while (maxWaitCountInSeconds > 0) {
            try {
                String sessionId = session.getId();
                MessageType messageType = MessageType.DEVICE_ALLOCATION_INFORMATION;
                String data = gson.toJson(deviceSelector, DeviceSelector.class);
                ClientServerRequest request = new ClientServerRequest(sessionId, messageType, data);

                ClientServerResponse response = sendRequestForResponse(request);

                if (response.getResponseType() == MessageType.ERROR) {
                    throw new NoDeviceMatchingTheGivenSelectorException();
                }

                if (response.getResponseType() == messageType && response.getResponseData() == null) {
                    throw new NoAvailableDeviceFoundException();
                }

                DeviceAllocationInformation deviceAllocationInformation = gson.fromJson(response.getResponseData(), DeviceAllocationInformation.class);
                this.deviceInformation = deviceAllocationInformation;
                return deviceAllocationInformation;
            } catch (NoAvailableDeviceFoundException e) {
                LOGGER.info("Device not available. Will retry shortly...");
                try {
                    Thread.sleep(ALLOCATE_DEVICE_RETRY_TIMEOUT);
                } catch (InterruptedException e1) {
                    // Nothing to do here
                }
                maxWaitCountInSeconds--;
            } catch (NoDeviceMatchingTheGivenSelectorException e) {
                break;
            } catch (IOException e) {
                String message = "Could not send the device allocation request (connection failure).";
                LOGGER.fatal(message, e);
                throw new ServerConnectionFailedException(message, e);
            }
        }
        throw new NoAvailableDeviceFoundException();
    }

    /**
     * Returns a list with serial numbers and models of all available devices.
     *
     * @return a list with serial numbers and models of all available devices
     * @throws CommandFailedException
     *         - if the operation failed server-side
     */
    public List<Pair<String, String>> getAllAvailableDevices() throws CommandFailedException {
        String sessionId = session.getId();
        MessageType messageType = MessageType.GET_ALL_DEVICES_REQUEST;
        ClientServerRequest request = new ClientServerRequest(sessionId, messageType, null);

        try {
            ClientServerResponse response = sendRequestForResponse(request);
            if (response.getResponseType() == MessageType.ERROR) {
                String message = "Could not retrieve the list of available devices (server error)";
                throw new CommandFailedException(message);
            }

            TypeToken<List<Pair<String, String>>> listTypeToken = new TypeToken<List<Pair<String, String>>>() {};
            return gson.fromJson(response.getResponseData(), listTypeToken.getType());
        } catch (IOException e) {
            String message = "Could not get the available devices (connection failure).";
            LOGGER.fatal(message, e);
            throw new ServerConnectionFailedException(message, e);
        }
    }

    /**
     * Sends a release device request to the server.
     *
     * @param deviceDescriptor
     *        - the {@link DeviceAllocationInformation} which describes the device to be released
     */
    public void releaseDevice(DeviceAllocationInformation deviceDescriptor) {
        String sessionId = session.getId();
        MessageType messageType = MessageType.RELEASE_REQUEST;
        String data = gson.toJson(deviceDescriptor, DeviceAllocationInformation.class);
        ClientServerRequest request = new ClientServerRequest(sessionId, messageType, data);

        try {
            sendRequestForResponse(request);
        } catch (IOException e) {
            String message = "Could not release Device (connection failure).";
            LOGGER.fatal(message, e);
            throw new ServerConnectionFailedException(message, e);
        }
    }

    /**
     * Sends a {@link RoutingAction} to the server and returns the result.
     *
     * @param action
     *        - the {@link RoutingAction} which should be sent
     * @param args
     *        - the arguments of the routing action
     * @return the result of the action as an Object
     * @throws CommandFailedException
     *         - if the connection to the server failed
     */
    public Object sendAction(RoutingAction action, Object... args) throws CommandFailedException {
        String sessionId = session.getId();
        MessageType messageType = MessageType.ROUTING_ACTION;
        String data = parseRoutingActionArguments(action, args);

        ClientServerRequest request = new ClientServerRequest(sessionId, messageType, data);

        try {
            ClientServerResponse response = sendRequestForResponse(request);
            if (response.getResponseType() == MessageType.ERROR) {
                String errorMessage = response.getResponseData();
                throw new CommandFailedException(errorMessage);
            }

            // The action ran successfully, but it did not return a result
            if (response.getResponseType() == messageType && response.getResponseData() == null) {
                return null;
            }

            return parseResult(response.getResponseData());
        } catch (IOException e) {
            String message = "Could not send routing action (connection failure).";
            LOGGER.fatal(message, e);
            throw new ServerConnectionFailedException(message, e);
        }
    }

    private String parseRoutingActionArguments(RoutingAction action, Object... args) {
        JsonObject json = new JsonObject();

        json.addProperty("rmiId", deviceInformation.getProxyRmiId());
        json.addProperty("passkey", deviceInformation.getProxyPasskey());
        json.addProperty("action", gson.toJson(action, RoutingAction.class));

        // Add the arguments as json array
        JsonArray jsonArgsArray = new JsonArray();
        for (Object arg : args) {
            jsonArgsArray.add(gson.toJson(arg));
        }
        json.add("args", jsonArgsArray);
        return json.toString();
    }

    private Object parseResult(String responseData) throws CommandFailedException {
        JsonObject jsonResponse = new JsonParser().parse(responseData).getAsJsonObject();
        String className = jsonResponse.get("class").getAsString();
        try {
            return gson.fromJson(jsonResponse.get("value").getAsString(), Class.forName(className));
        } catch (JsonSyntaxException | ClassNotFoundException e) {
            throw new CommandFailedException(e.getMessage());
        }
    }

    private ClientServerResponse sendRequestForResponse(ClientServerRequest request) throws IOException {
        String sessionId = request.getSessionId();
        String requestJSON = gson.toJson(request, ClientServerRequest.class);
        session.getBasicRemote().sendText(requestJSON);
        LOGGER.info("Sending request:");
        LOGGER.info(requestJSON);

        Object lockObject = communicationManager.getSynchronizationObject(sessionId);
        LOGGER.info("Waiting for response.");
        synchronized(lockObject) {
            try {
                lockObject.wait(CONNECTION_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.info("Waiting interrupted.");
            }
        }

        LOGGER.info("Getting the response...");
        return communicationManager.getServerResponse(sessionId);
    }

    /**
     * Disconnects the communicator from the server.
     */
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            // Already disconnected, nothing to do here.
        }
    }
}
