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
