package com.musala.atmosphere.client.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.musala.atmosphere.commons.websocket.WebSocketCommunicatorManager;
import com.musala.atmosphere.commons.websocket.message.ClientServerResponse;

@ClientEndpoint
public class ClientWebSocketEndpoint {
    private static final Logger LOGGER = Logger.getLogger(ClientWebSocketEndpoint.class.getCanonicalName());

    private WebSocketCommunicatorManager communicationManager = WebSocketCommunicatorManager.getInstance();

    @OnMessage
    public void onMessage(String message, Session session) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        ClientServerResponse response = gson.fromJson(message, ClientServerResponse.class);
        LOGGER.info("Received server response with id: " + response.getSessionId());
        communicationManager.setServerResponse(response);
    }
}
