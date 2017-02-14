package com.musala.atmosphere.client.entity;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.Device;
import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ime.KeyboardAction;
import com.musala.atmosphere.commons.util.AtmosphereIntent;

/**
 * Entity responsible for operations related with the input method engine.
 *
 * @author yavor.stankov
 *
 */
public class ImeEntity {
    private static final Logger LOGGER = Logger.getLogger(ImeEntity.class.getCanonicalName());

    private DeviceCommunicator communicator;

    ImeEntity(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Simulates text typing in the element on focus for this device. It is user's responsibility to focus an editable
     * android widget using {@link Device#tapScreenLocation(Point) Device.tapScreenLocation()}, {@link UiElement#tap()
     * UiElement.tap()} or {@link UiElement#focus() UiElement.focus()} methods.
     *
     * @param text
     *        - text to be input
     * @param interval
     *        - time interval in milliseconds between typing each symbol
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     */
    public boolean inputText(String text, long interval) {
        if (text.isEmpty()) {
            String message = "Text input requested, but an empty String is given.";
            LOGGER.warn(message);
            return true;
        }

        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.INPUT_TEXT.intentAction);
        intent.putExtra(KeyboardAction.INTENT_EXTRA_TEXT, text);
        intent.putExtra(KeyboardAction.INTENT_EXTRA_INPUT_SPEED, interval);

        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        waitForTaskCompletion(text.length() * interval);

        return communicator.getLastException() == null;
    }

    /**
     * Clears the content of the focused text field.
     *
     * @return <code>true</code> if clear text is successful, <code>false</code> if it fails
     */
    public boolean clearText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.DELETE_ALL.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    /**
     * Selects the content of the focused text field.
     *
     * @return <code>true</code> if the text selecting is successful, <code>false</code> if it fails
     */
    public boolean selectAllText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.SELECT_ALL.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    /**
     * Copies the selected content of the focused text field.
     *
     * @return <code>true</code> if copy operation is successful, <code>false</code> if it fails
     */
    public boolean copyText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.COPY_TEXT.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    /**
     * Paste a copied text in the current focused text field.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> if it fails
     */
    public boolean pasteText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.PASTE_TEXT.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    /**
     * Cuts the selected text from the current focused text field.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> if it fails
     */
    public boolean cutText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.CUT_TEXT.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    private void waitForTaskCompletion(long timeoutInMs) {
        try {
            Thread.sleep(timeoutInMs);
        } catch (InterruptedException e) {
            // Nothing to do here
        }
    }
}
