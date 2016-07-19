package com.musala.atmosphere.client.entity;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.uiutils.AccessibilityElementUtils;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Base entity responsible for handling GPS location state changing.
 *
 * @author yavor.stankov
 *
 */
public abstract class GpsLocationEntity {
    private static final Logger LOGGER = Logger.getLogger(GpsLocationEntity.class.getCanonicalName());

    private static final String AGREE_BUTTON_RESOURCE_ID = "android:id/button1";

    private static final int AGREE_BUTTON_TIMEOUT = 3000;

    protected static final int CHANGE_STATE_WIDGET_TIMEOUT = 5000;

    protected AccessibilityElementUtils elementUtils;

    protected DeviceCommunicator communicator;

    GpsLocationEntity(DeviceCommunicator communicator,
            AccessibilityElementUtils elementUtils) {
        this.communicator = communicator;
        this.elementUtils = elementUtils;
    }

    /**
     * Gets the right widget that is responsible for setting the GPS location state.
     *
     * @return the widget that should be used for setting the GPS location state.
     * @throws UiElementFetchingException
     *         if the required widget is not present on the screen
     * @throws MultipleElementsFoundException
     *         if there are more than one widgets present on the screen
     */
    protected abstract UiElement getChangeStateWidget()
        throws MultipleElementsFoundException,
            UiElementFetchingException;

    /**
     * Enables the GPS location on this device.
     *
     * @return <code>true</code> if the GPS location enabling is successful, <code>false</code> if it fails
     */
    public boolean enableGpsLocation() {
        return setGpsLocationState(true);
    }

    /**
     * Disables the GPS location on this device.
     *
     * @return <code>true</code> if the GPS location disabling is successful, <code>false</code> if it fails
     */
    public boolean disableGpsLocation() {
        return setGpsLocationState(false);
    }

    /**
     * Check if the GPS location is enabled on this device.
     *
     * @return <code>true</code> if the GPS location is enabled, <code>false</code> if it's disabled
     */
    public boolean isGpsLocationEnabled() {
        return (boolean) communicator.sendAction(RoutingAction.IS_GPS_LOCATION_ENABLED);
    }

    private boolean setGpsLocationState(boolean state) {
        if (isGpsLocationEnabled() == state) {
            return true;
        }

        openLocationSettings();

        try {
            if (getChangeStateWidget().tap()) {
                pressAgreeButton();
            }
        } catch (MultipleElementsFoundException | UiElementFetchingException e) {
            LOGGER.error("Failed to get the wanted widget, or there are more than one widgets on the screen that are matching the given selector.",
                         e);
            return false;
        }

        pressHardwareButton(HardwareButton.BACK);

        return true;
    }

    private void openLocationSettings() {
        communicator.sendAction(RoutingAction.OPEN_LOCATION_SETTINGS);
    }

    private void pressHardwareButton(HardwareButton button) {
        int keyCode = button.getKeycode();

        communicator.sendAction(RoutingAction.PRESS_HARDWARE_BUTTON, keyCode);
    }

    private void pressAgreeButton() throws MultipleElementsFoundException, UiElementFetchingException {
        UiElementSelector agreeButtonSelector = new UiElementSelector();
        agreeButtonSelector.addSelectionAttribute(CssAttribute.RESOURCE_ID, AGREE_BUTTON_RESOURCE_ID);

        if (elementUtils.waitForElementExists(agreeButtonSelector, AGREE_BUTTON_TIMEOUT)) {
            UiElement agreeButton = elementUtils.getElement(agreeButtonSelector, true);
            agreeButton.tap();
        }
    }
}
