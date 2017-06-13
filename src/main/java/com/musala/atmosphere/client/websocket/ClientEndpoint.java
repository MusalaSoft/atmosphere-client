package com.musala.atmosphere.client.websocket;

import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.musala.atmosphere.commons.cs.util.ClientServerGsonUtil;
import com.musala.atmosphere.commons.websocket.WebSocketCommunicatorManager;
import com.musala.atmosphere.commons.websocket.message.MessageAction;
import com.musala.atmosphere.commons.websocket.message.ResponseMessage;
import com.musala.atmosphere.commons.websocket.util.IJsonUtil;
import com.musala.atmosphere.commons.websocket.util.JsonConst;

/**
 * Represents the client endpoint for all incoming server messages.
 *
 * @author dimcho.nedev
 *
 */
@javax.websocket.ClientEndpoint
public class ClientEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ClientEndpoint.class.getCanonicalName());

    private static WebSocketCommunicatorManager communicationManager = WebSocketCommunicatorManager.getInstance();

    private static final IJsonUtil jsonUtil = new ClientServerGsonUtil();

    @OnMessage
    public void onJsonMessage(String message, Session session) {
        MessageAction messageAction = jsonUtil.getProperty(message, JsonConst.MESSAGE_ACTION, MessageAction.class);

        switch (messageAction) {
            case ROUTING_ACTION:
            case DEVICE_ALLOCATION_INFORMATION:
            case GET_ALL_AVAILABLE_DEVICES:
            case ERROR:
            case RELEASE_DEVICE:
                ResponseMessage response = jsonUtil.deserializeResponse(message);
                communicationManager.addResponse(response);
                break;
            default:
                LOGGER.error("Unknown message action on the ClientEndpoint: " + messageAction);
                break;
        }
    }

}
