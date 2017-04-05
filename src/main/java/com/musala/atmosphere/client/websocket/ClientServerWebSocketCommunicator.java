package com.musala.atmosphere.client.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import com.google.gson.Gson;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.exception.NoDeviceMatchingTheGivenSelectorException;
import com.musala.atmosphere.commons.exceptions.NoAvailableDeviceFoundException;
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
            LOGGER.error(e.getMessage());
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
                Gson gson = new Gson();
                String sessionId = session.getId();
                MessageType messageType = MessageType.DEVICE_ALLOCATION_INFORMATION;
                String data = gson.toJson(deviceSelector, DeviceSelector.class);
                ClientServerRequest request = new ClientServerRequest(sessionId, messageType, data);

                String requestJSON = gson.toJson(request, ClientServerRequest.class);
                session.getBasicRemote().sendText(requestJSON);
                LOGGER.info("Device Descriptor request sent to server.");

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
                ClientServerResponse response = communicationManager.getServerResponse(sessionId);
                if (response == null) {
                    LOGGER.error("Response is null.");
                    return null;
                }

                if (response.getResponseType() == MessageType.ERROR) {
                    throw new NoDeviceMatchingTheGivenSelectorException();
                }

                if (response.getResponseType() == messageType && response.getResponseData() == null) {
                    throw new NoAvailableDeviceFoundException();
                }

                DeviceAllocationInformation deviceAllocationInformation = gson.fromJson(response.getResponseData(), DeviceAllocationInformation.class);
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
                LOGGER.error("WebSockets: Could not send the device allocation request.");
            }
        }
        throw new NoAvailableDeviceFoundException();
    }
}
