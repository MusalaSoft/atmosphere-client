package com.musala.atmosphere.client;

import java.util.Calendar;

/**
 * A class representing the Android picker widgets.
 * 
 * @author delyan.dimitrov
 * 
 */
public abstract class PickerView {
    protected DeviceCommunicator communicator;

    /**
     * @param communicator
     *        - the device communicator used to send actions to the device
     */
    public PickerView(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Sets the value in the passed {@link Calendar} in the picker it represents.
     * 
     * @param value
     *        - the value used to set the picker
     * 
     * @return true if the value was set successfully, false otherwise
     */
    public abstract boolean setValue(Calendar value);

    /**
     * Gets the value from the picker into a calendar object.
     * 
     * @note The calendar object returned will have only its time or date set, depending on the picker.
     * 
     * @return - a {@link Calendar} object with the value of the picker
     */
    public abstract Calendar getValue();
}
