package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.commons.RoutingAction;

/**
 * Entity responsible for operations related with hardware buttons.
 *
 * @author filareta.yordanova
 *
 */
public class HardwareButtonEntity {
    private DeviceCommunicator communicator;

    HardwareButtonEntity(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Presses hardware button on this device.
     *
     * @param keyCode
     *        - button key code as specified by the Android KeyEvent KEYCODE_ constants
     * @return <code>true</code> if the hardware button press is successful, <code>false</code> if it fails
     */
    public boolean pressButton(int keyCode) {
        String query = "input keyevent " + Integer.toString(keyCode);
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);

        return communicator.getLastException() == null;
    }
}
