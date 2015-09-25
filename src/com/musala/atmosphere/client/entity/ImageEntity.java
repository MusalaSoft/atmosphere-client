package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.commons.RoutingAction;

/**
 * Entity responsible for operations related with images.
 *
 * @author yavor.stankov
 *
 */
public class ImageEntity {
    DeviceCommunicator communicator;

    /**
     * Constructs a new {@link ImageEntity} object by given {@link DeviceCommunicator device communicator}.
     * 
     * @param communicator
     *        - a communicator to the remote device
     */
    ImageEntity(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Gets screenshot of this device's active screen.
     *
     * @return byte buffer, containing captured device screen,<br>
     *         <code>null</code> if getting screenshot fails.<br>
     *         It can be subsequently dumped to a file and directly opened as a PNG image
     */
    public byte[] getScreenshot() {
        return (byte[]) communicator.sendAction(RoutingAction.GET_SCREENSHOT);
    }
}
