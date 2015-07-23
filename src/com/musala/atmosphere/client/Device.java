package com.musala.atmosphere.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.GettingScreenshotFailedException;
import com.musala.atmosphere.client.uiutils.GestureCreator;
import com.musala.atmosphere.client.util.settings.AndroidGlobalSettings;
import com.musala.atmosphere.client.util.settings.AndroidSystemSettings;
import com.musala.atmosphere.client.util.settings.DeviceSettingsManager;
import com.musala.atmosphere.client.util.settings.IAndroidSettings;
import com.musala.atmosphere.client.util.settings.SettingsParsingException;
import com.musala.atmosphere.commons.ConnectionType;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.TelephonyInformation;
import com.musala.atmosphere.commons.beans.DeviceAcceleration;
import com.musala.atmosphere.commons.beans.DeviceMagneticField;
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.DeviceProximity;
import com.musala.atmosphere.commons.beans.MobileDataState;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.beans.SwipeDirection;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.gesture.Gesture;
import com.musala.atmosphere.commons.ime.KeyboardAction;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;
import com.musala.atmosphere.commons.util.AccessibilityXmlSerializer;
import com.musala.atmosphere.commons.util.AtmosphereIntent;
import com.musala.atmosphere.commons.util.GeoLocation;
import com.musala.atmosphere.commons.util.IntentBuilder;
import com.musala.atmosphere.commons.util.IntentBuilder.IntentAction;
import com.musala.atmosphere.commons.util.Pair;
import com.musala.atmosphere.commons.util.structure.tree.Tree;

/**
 * Android device representing class.
 *
 * @author vladimir.vladimirov
 *
 */
public class Device {
    private static final int MAX_BUFFER_SIZE = 8092; // 8K

    private static final Logger LOGGER = Logger.getLogger(Device.class.getCanonicalName());

    private static final String ATMOSPHERE_SERVICE_PACKAGE = "com.musala.atmosphere.service";

    private static final String ANDROID_WIDGET_SWITCH_CLASS_NAME = "android.widget.Switch";

    private static final String ANDROID_WIDGET_CHECKBOX_CLASS_NAME = "android.widget.CheckBox";

    private static final String AGREE_BUTTON_RESOURCE_ID = "android:id/button1";

    private static final String ATMOSPHERE_UNLOCK_DEVICE_ACTIVITY = ".UnlockDeviceActivity";

    private static final int WAIT_FOR_AWAKE_STATE_INTERVAL = 100;

    /**
     * Default timeout for the hold phase from long click gesture. It needs to be more than the system long click
     * timeout which varies from device to device, but is usually around 1 second.
     */
    public static final int LONG_PRESS_DEFAULT_TIMEOUT = 1500; // ms

    private final DeviceSettingsManager deviceSettings;

    private final ServerConnectionHandler serverConnectionHandler;

    private final DeviceCommunicator communicator;

    private final UiElementValidator validator;

    /**
     * Constructor that creates a usable Device object by a given IClientDevice, it's invocation passkey.
     *
     * @param iClientDevice
     * @param devicePasskey
     * @param serverConnectionHandler
     */
    Device(IClientDevice clientDevice, long devicePasskey, ServerConnectionHandler serverConnectionHandler) {
        validator = new UiElementValidator();
        this.serverConnectionHandler = serverConnectionHandler;
        communicator = new DeviceCommunicator(clientDevice, devicePasskey);
        deviceSettings = new DeviceSettingsManager(communicator);
    }

    /**
     * Accepts call to this device.
     *
     * @return <code>true</code> if the accepting call is successful, <code>false</code> if it fails.
     */
    public boolean acceptCall() {
        return pressButton(HardwareButton.ANSWER);
    }

    /**
     * Accepts a call to this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param phoneNumber
     *        - {@link PhoneNumber}, that calls the device.
     * @return <code>true</code> if the accepting call is successful, <code>false</code> if it fails.
     */
    public boolean acceptCall(PhoneNumber phoneNumber) {
        Object result = communicator.sendAction(RoutingAction.CALL_ACCEPT, phoneNumber);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Cancels a call to this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param phoneNumber
     *        - {@link PhoneNumber}, that calls the device.
     * @return <code>true</code> if the canceling call is successful, <code>false</code> if it fails.
     */
    public boolean cancelCall(PhoneNumber phoneNumber) {
        Object result = communicator.sendAction(RoutingAction.CALL_CANCEL, phoneNumber);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Declines a call to this device.<br>
     *
     * @return <code>true</code> if the denying call is successful, <code>false</code> if it fails.
     */
    public boolean declineCall() {
        return pressButton(HardwareButton.DECLINE);
    }

    /**
     * Executes a command in the shell of this device.
     *
     * @param shellCommand
     *        - String, representing the command for execution.
     * @return the output of this device console, after the command is executed.
     */
    public String executeShellCommand(String shellCommand) {
        String result = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, shellCommand);
        return result;
    }

    /**
     * Executes a command in the shell of this device in a new thread.
     *
     * @param shellCommand
     *        - command to be executed in background
     */
    private void executeShellCommandInBackground(String shellCommand) {
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND_IN_BACKGROUND, shellCommand);
    }

    /**
     * Interrupts a background executing shell process.
     *
     * @param processName
     *        - name of the process to be interrupted
     */
    private void interruptBackgroundShellProcess(String processName) {
        communicator.sendAction(RoutingAction.INTERRUPT_BACKGROUND_SHELL_PROCESS, processName);
    }

    /**
     * Installs a specified Android application file on this device.<br>
     *
     * @param path
     *        - location of the file to be installed.
     * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
     */
    private boolean doApkInstallation(String path, boolean shouldForceInstall) {
        // A string that will be used to tell which step of installation was
        // reached
        String currentInstallationStepDescription = null;
        FileInputStream fileReaderFromApk = null;
        try {
            currentInstallationStepDescription = "Create file for storing the apk";
            LOGGER.info(currentInstallationStepDescription);
            Object response = communicator.sendAction(RoutingAction.APK_INIT_INSTALL);
            if (response != DeviceCommunicator.VOID_SUCCESS) {
                throw communicator.getLastException();
            }

            currentInstallationStepDescription = "Locating the file to store the apk in";
            LOGGER.info(currentInstallationStepDescription);
            // Transfer the installation file from the current machine to the
            // device
            byte[] buffer = new byte[MAX_BUFFER_SIZE];
            fileReaderFromApk = new FileInputStream(path);

            currentInstallationStepDescription = "Transferring installation file";
            LOGGER.info(currentInstallationStepDescription);
            int readBytes;
            while ((readBytes = fileReaderFromApk.read(buffer)) >= 0) {
                response = communicator.sendAction(RoutingAction.APK_APPEND_DATA, buffer, readBytes);
                if (response != DeviceCommunicator.VOID_SUCCESS) {
                    throw communicator.getLastException();
                }
            }

            currentInstallationStepDescription = "Installing transferred file";
            LOGGER.info(currentInstallationStepDescription);
            response = communicator.sendAction(RoutingAction.APK_BUILD_AND_INSTALL, shouldForceInstall);
            if (response != DeviceCommunicator.VOID_SUCCESS) {
                throw communicator.getLastException();
            }
            String message = "File installation successfull.";
            LOGGER.info(message);
        } catch (IOException | CommandFailedException e) {
            String message = String.format("Exception occurred while '%s'.", currentInstallationStepDescription);
            LOGGER.fatal(message, e);
            // This method should work even if the apk file was not created at
            // all.
            communicator.sendAction(RoutingAction.APK_DISCARD);
            return false;
        } finally {
            if (fileReaderFromApk != null) {
                try {
                    fileReaderFromApk.close();
                } catch (IOException e) {
                    // Nothing can be done here anymore
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Simulates a double tap on the specified point.
     *
     * @param point
     *        - the point to be tapped
     * @return <code>true</code> if the double tap is successful, <code>false</code> if it fails.
     */
    public boolean doubleTap(Point point) {
        Gesture doubleTap = GestureCreator.createDoubleTap(point.getX(), point.getY());
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, doubleTap);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Executes user-described gesture on this device.
     *
     * @param gesture
     *        - the gesture to be executed.
     */
    public void executeGesture(Gesture gesture) {
        communicator.sendAction(RoutingAction.PLAY_GESTURE, gesture);
    }

    /**
     * Gets the currently active {@link Screen Screen} of this device.
     *
     * @return {@link Screen} instance, representing the active screen of this device or <code>null</code> if getting
     *         active screen fails.
     */
    public Screen getActiveScreen() {
        Tree<AccessibilityElement> accessibilityElementTree = (Tree<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_UI_TREE,
                                                                                                                   true);
        String uiHierarchy = AccessibilityXmlSerializer.serialize(accessibilityElementTree);

        if (uiHierarchy == null) {
            return null;
        }

        return new Screen(this, uiHierarchy);
    }

    /**
     * Gets a list with all UI elements present on the {@link Screen active screen} and matching the given selector.
     * 
     * @param selector
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return list with all UI elements present on the screen and matching the given selector
     */
    public List<AccessibilityUiElement> getAccessibilityUiElements(UiElementSelector selector, Boolean visibleOnly) {
        List<AccessibilityElement> foundElements = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_UI_ELEMENTS,
                                                                                                        selector,
                                                                                                        visibleOnly);
        List<AccessibilityUiElement> uiElements = new ArrayList<AccessibilityUiElement>();
        for (AccessibilityElement element : foundElements) {
            uiElements.add(new AccessibilityUiElement(element, this));
        }

        return uiElements;
    }

    /**
     * Gets the airplane mode state of this device.<br>
     *
     * @return <code>true</code> if the airplane mode is on, <code>false</code> if it's off and <code>null</code> if
     *         getting airplane mode fails.
     */
    public Boolean getAirplaneMode() {
        DeviceInformation deviceInformation = getInformation();
        int apiLevel = deviceInformation.getApiLevel();

        IAndroidSettings airplaneSetting = apiLevel >= 17 ? AndroidGlobalSettings.AIRPLANE_MODE_ON
                : AndroidSystemSettings.AIRPLANE_MODE_ON;

        try {
            int airplaneMode = deviceSettings.getInt(airplaneSetting);
            return airplaneMode == 1;
        } catch (SettingsParsingException e) {
            String message = "Getting the Airplane mode of the device failed.";
            LOGGER.error(message, e);
            return null;
        }
    }

    DeviceCommunicator getCommunicator() {
        return communicator;
    }

    /**
     * Gets the current network connection type of this device.
     *
     * @return the {@link ConnectionType type} of the network on this device, or <code>null</code> if getting connection
     *         type fails.
     * @see ConnectionType
     *
     */
    public ConnectionType getConnectionType() {
        ConnectionType type = (ConnectionType) communicator.sendAction(RoutingAction.GET_CONNECTION_TYPE);
        return type;
    }

    /**
     * Gets current acceleration of this device.
     *
     * @return the movement {@link DeviceAcceleration vector} of this device in the space or <code>null</code> if
     *         getting acceleration fails.
     * @see DeviceAcceleration
     */
    public DeviceAcceleration getDeviceAcceleration() {
        DeviceAcceleration deviceAcceleration = (DeviceAcceleration) communicator.sendAction(RoutingAction.GET_DEVICE_ACCELERATION);
        return deviceAcceleration;
    }

    /**
     * Gets the current proximity of the device.
     *
     * @return a float representing the proximity of the device or null if the getting of the proximity failed
     */
    public float getDeviceProximity() {
        float proximity = (float) communicator.sendAction(RoutingAction.GET_DEVICE_PROXIMITY);

        return proximity;
    }

    /**
     * Gets current orientation in space of this device.
     *
     * @return {@link DeviceOrientation DeviceOrientation} of the testing device,<br>
     *         <code>null</code> if getting device orientation fails.
     */
    public DeviceOrientation getDeviceOrientation() {
        DeviceOrientation deviceOrientation = (DeviceOrientation) communicator.sendAction(RoutingAction.GET_DEVICE_ORIENTATION);
        return deviceOrientation;
    }

    /**
     * Provides information about device physical properties, such as type (tablet or emulator), dpi, resolution,
     * android API level, manufacturer, camera presence and others.
     *
     * @return {@link DeviceInformation DeviceInformation} structure with information for the testing device,<br>
     *         <code>null</code> if getting device information fails.
     */
    public DeviceInformation getInformation() {
        DeviceInformation wrappedDeviceInformation = (DeviceInformation) communicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);
        return wrappedDeviceInformation;
    }

    /**
     * Gets the current mobile data state of this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @return the {@link MobileDataState state} of mobile data on this device or <code>null</code> if getting mobile
     *         data state fails.
     * @see MobileDataState
     */
    public MobileDataState getMobileDataState() {
        MobileDataState state = (MobileDataState) communicator.sendAction(RoutingAction.GET_MOBILE_DATA_STATE);
        return state;
    }

    /**
     * Gets a {@link PowerProperties} instance that contains information about the current device power-related
     * environment.
     *
     * @return a filled {@link PowerProperties} instance (or <code>null</code> if fetching the environment fails).
     */
    public PowerProperties getPowerProperties() {
        PowerProperties result = (PowerProperties) communicator.sendAction(RoutingAction.GET_POWER_PROPERTIES);
        return result;
    }

    /**
     * Gets screenshot of this device's active screen.
     *
     * @return byte buffer, containing captured device screen,<br>
     *         <code>null</code> if getting screenshot fails.<br>
     *         It can be subsequently dumped to a file and directly opened as a PNG image.
     */
    public byte[] getScreenshot() {
        byte[] screenshot = (byte[]) communicator.sendAction(RoutingAction.GET_SCREENSHOT);
        return screenshot;
    }

    /**
     * Gets screenshot of this device's active screen and saves it as an image file at a specified location.
     *
     * @param pathToImageFile
     *        - location at which the screenshot image file should be saved.
     * @return <code>true</code> if the getting screenshot is successful, <code>false</code> if it fails.
     */
    public boolean getScreenshot(String pathToImageFile) {
        try {
            Path pathToPngFile = Paths.get(pathToImageFile);
            byte[] screenshot = getScreenshot();
            Files.write(pathToPngFile, screenshot);
        } catch (IOException e) {
            String message = "Saving the screenshot file failed.";
            LOGGER.error(message, e);
            return false;
        }

        return true;
    }

    /**
     * Gets a {@link ScreenOrientation} instance that contains information about the orientation of the screen.
     *
     * @return {@link ScreenOrientation object} that shows how android elements are rotated on the screen.
     * @see ScreenOrientation
     */
    public ScreenOrientation getScreenOrientation() {
        ScreenOrientation screenOrientation = null;
        try {
            int obtainedScreenOrientationValue = deviceSettings.getInt(AndroidSystemSettings.USER_ROTATION);
            screenOrientation = ScreenOrientation.getValueOfInt(obtainedScreenOrientationValue);
        } catch (SettingsParsingException e) {
            String message = "Failed to get screen orientation of the device.";
            LOGGER.error(message, e);
        }
        return screenOrientation;
    }

    /**
     * Obtains information about the telephony services on the device.
     *
     * @return {@link TelephonyInformation} instance.
     */
    public TelephonyInformation getTelephonyInformation() {
        TelephonyInformation telephonyInformation = (TelephonyInformation) communicator.sendAction(RoutingAction.GET_TELEPHONY_INFO);
        return telephonyInformation;
    }

    UiElementValidator getUiValidator() {
        return validator;
    }

    /**
     * Returns device auto rotation state.
     *
     * @return <code>true</code> if the auto rotation is on , <code>false</code> if it's not,<code>null</code> if the
     *         method failed to get device auto rotation state.
     */
    public Boolean isAutoRotationOn() {
        Boolean isAutoRotationOn = null;
        try {
            int autoRotationVelue = deviceSettings.getInt(AndroidSystemSettings.ACCELEROMETER_ROTATION);
            isAutoRotationOn = autoRotationVelue == 1 ? true : false;

        } catch (SettingsParsingException e) {
            String message = "Getting autorotation status failed.";
            LOGGER.error(message, e);
        }
        return isAutoRotationOn;
    }

    /**
     * Holds a call to this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param phoneNumber
     *        - {@link PhoneNumber}, that calls the device.
     * @return <code>true</code> if the holding call is successful, <code>false</code> if it fails.
     */
    public boolean holdCall(PhoneNumber phoneNumber) {
        Object result = communicator.sendAction(RoutingAction.CALL_HOLD, phoneNumber);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Inputs text directly on the device, in the element on focus, if possible. It is user's responsibility to focus an
     * editable android widget using {@link Device#tapScreenLocation(Point) Device.tapScreenLocation()},
     * {@link UiElement#tap() UiElement.tap()} or {@link UiElement#focus() UiElement.focus()} methods.
     *
     * @param text
     *        - text to be input.
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
     */
    public boolean inputText(String text) {
        return inputText(text, 0);
    }

    /**
     * Simulates text typing in the element on focus for this device. It is user's responsibility to focus an editable
     * android widget using {@link Device#tapScreenLocation(Point) Device.tapScreenLocation()}, {@link UiElement#tap()
     * UiElement.tap()} or {@link UiElement#focus() UiElement.focus()} methods.
     *
     * @param text
     *        - text to be input.
     * @param interval
     *        - time interval in milliseconds between typing each symbol.
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
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
     * Cuts the selected text from the current focused text field.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> if it fails
     */
    public boolean cutText() {
        AtmosphereIntent intent = new AtmosphereIntent(KeyboardAction.CUT_TEXT.intentAction);
        communicator.sendAction(RoutingAction.SEND_BROADCAST, intent);

        return communicator.getLastException() == null;
    }

    /**
     * Installs a specified Android application file on this device.<br>
     *
     * @param path
     *        - location of the file to be installed.
     * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
     */
    public boolean installAPK(String path) {
        return doApkInstallation(path, false);
    }

    /**
     * Installs a specified Android application file on this device.<br>
     *
     * @param path
     *        - location of the file to be installed.
     * @param shouldForceInstall
     *        - Indicates whether a force install should be performed
     * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
     */
    public boolean installAPK(String path, boolean shouldForceInstall) {
        return doApkInstallation(path, shouldForceInstall);
    }

    /**
     * Checks if this device is in a WAKE state.<br>
     *
     * @return <code>true</code> if the device is awake.<br>
     *         <code>false</code> if the device is asleep.<br>
     */
    public boolean isAwake() {
        boolean response = (boolean) communicator.sendAction(RoutingAction.GET_AWAKE_STATUS);
        return response;
    }

    /**
     * Checks if this device is locked.
     *
     * @return <code>true</code> if the device is locked.<br>
     *         <code>false</code> if the device is unlocked.
     */
    public Boolean isLocked() {
        return (boolean) communicator.sendAction(RoutingAction.IS_LOCKED);
    }

    /**
     * Simulates a pinch in having the initial coordinates of the fingers performing it.
     *
     * @param firstFingerInitial
     *        - the initial position of the first finger
     * @param secondFingerInitial
     *        - the initial position of the second finger
     * @return <code>true</code> if the pinch in is successful, <code>false</code> if it fails.
     */
    public boolean pinchIn(Point firstFingerInitial, Point secondFingerInitial) {
        validatePointOnScreen(firstFingerInitial);
        validatePointOnScreen(secondFingerInitial);

        Gesture pinchIn = GestureCreator.createPinchIn(firstFingerInitial, secondFingerInitial);
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, pinchIn);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Simulates a pinch out having the positions of the fingers performing it in the end of the gesture.
     *
     * @param firstFingerEnd
     *        - the position of the first finger in the end of the gesture
     * @param secondFingerEnd
     *        - the position of the second finger in the end of the gesture
     * @return <code>true</code> if the pinch out is successful, <code>false</code> if it fails.
     */
    public boolean pinchOut(Point firstFingerEnd, Point secondFingerEnd) {
        validatePointOnScreen(firstFingerEnd);
        validatePointOnScreen(secondFingerEnd);

        Gesture pinchOut = GestureCreator.createPinchOut(firstFingerEnd, secondFingerEnd);
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, pinchOut);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Presses hardware button on this device.
     *
     * @param button
     *        - {@link HardwareButton HardwareButton} to be pressed.
     * @return <code>true</code> if the button press is successful, <code>false</code> if it fails.
     */
    public boolean pressButton(HardwareButton button) {
        int keycode = button.getKeycode();
        return pressButton(keycode);
    }

    /**
     * Presses hardware button on this device.
     *
     * @param keyCode
     *        - button key code as specified by the Android KeyEvent KEYCODE_ constants.
     * @return <code>true</code> if the hardware button press is successful, <code>false</code> if it fails.
     */
    public boolean pressButton(int keyCode) {
        String query = "input keyevent " + Integer.toString(keyCode);
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);

        return communicator.getLastException() == null;
    }

    /**
     * Simulates random finger actions on the screen of this device.
     *
     * @return <code>true</code> if the random multi-touch event execution is successful, <code>false</code> if it
     *         fails.
     */
    public boolean randomMultiTouchevent() {
        // TODO implement device.randomMultiTouchEvent()
        return false;
    }

    /**
     * This device receives a call.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param phoneNumber
     *        - {@link PhoneNumber}, that will be sent to the device.
     * @return <code>true</code> if the call receiving is successful, <code>false</code> if it fails.
     */
    public boolean receiveCall(PhoneNumber phoneNumber) {
        Object result = communicator.sendAction(RoutingAction.CALL_RECEIVE, phoneNumber);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sends SMS to this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param smsMessage
     *        - {@link SmsMessage}, that will be sent to the device.
     * @return <code>true</code> if the SMS receiving is successful, <code>false</code> if it fails.
     */
    public boolean receiveSms(SmsMessage smsMessage) {
        Object result = communicator.sendAction(RoutingAction.SMS_RECEIVE, smsMessage);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Redirects specific IP address to another IP address.
     *
     * @param toIp
     *        - IP which will receive requests.
     * @param toNewIp
     *        - another IP to which the received requests from the first IP should be redirected.
     * @return <code>true</code> if the connection redirection is successful, <code>false</code> if it fails.
     */
    public boolean redirectConnection(String toIp, String toNewIp) {
        // TODO implement device.redirectConnection
        return false;
    }

    void release() {
        stopScreenRecording();
        communicator.release();
    }

    /**
     * Sets new acceleration for this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param deviceAcceleration
     *        - new {@link DeviceAcceleration DeviceAcceleration} to be set.
     * @return <code>true</code> if the acceleration setting is successful, <code>false</code> if it fails.
     */
    public boolean setAcceleration(DeviceAcceleration deviceAcceleration) {
        Object result = communicator.sendAction(RoutingAction.SET_ACCELERATION, deviceAcceleration);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sets the airplane mode state for this device.<br>
     * <i><b>Warning:</b> enabling airplane mode on emulator disconnects it from ATMOSPHERE Agent and this emulator can
     * be connected back only after Agent restart. Setting airplane mode for emulators is prohibited</i>
     *
     * @param airplaneMode
     *        - <code>true</code> to enter device in airplane mode, <code>false</code> to exit device from airplane
     *        mode.
     * @return <code>true</code> if the airplane mode setting is successful, <code>false</code> if it fails.
     */
    public boolean setAirplaneMode(boolean airplaneMode) {
        DeviceInformation deviceInformation = getInformation();
        int apiLevel = deviceInformation.getApiLevel();
        boolean isEmulator = deviceInformation.isEmulator();
        if (isEmulator) {
            String message = "Enabling airplane mode on emulator disconnects it from ATMOSPHERE Agent and this emulator can be connected back only after Agent restart. Setting airplane mode for emulators is prohibited.";
            LOGGER.warn(message);
            return false;
        }

        int airplaneModeIntValue = airplaneMode ? 1 : 0;

        IntentBuilder intentBuilder = new IntentBuilder(IntentAction.AIRPLANE_MODE_NOTIFICATION);
        intentBuilder.putExtraBoolean("state", airplaneMode);
        String intentCommand = intentBuilder.buildIntentCommand();

        final String INTENT_COMMAND_RESPONSE = "Broadcast completed: result=0";

        IAndroidSettings airplaneSetting = apiLevel >= 17 ? AndroidGlobalSettings.AIRPLANE_MODE_ON
                : AndroidSystemSettings.AIRPLANE_MODE_ON;

        boolean success = deviceSettings.putInt(airplaneSetting, airplaneModeIntValue);
        if (!success) {
            String message = "Updating airplane mode status failed.";
            LOGGER.error(message);
            return false;
        }

        String intentCommandResponse = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND,
                                                                        intentCommand);
        Pattern intentCommandResponsePattern = Pattern.compile(INTENT_COMMAND_RESPONSE);
        Matcher intentCommandResponseMatcher = intentCommandResponsePattern.matcher(intentCommandResponse);
        if (!intentCommandResponseMatcher.find()) {
            String message = "Broadcasting notification intent failed.";
            LOGGER.error(message);
            return false;
        }

        return true;
    }

    /**
     * Enables the screen auto rotation on this device.
     *
     * @return <code>true</code> if the auto rotation setting is successful, and <code>false</code> if it fails
     */
    public boolean enableScreenAutoRotation() {
        return deviceSettings.putInt(AndroidSystemSettings.ACCELEROMETER_ROTATION, 1);
    }

    /**
     * Disables the screen auto rotation on this device.
     *
     * @return <code>true</code> if the auto rotation setting is successful, and <code>false</code> if it fails
     */
    public boolean disableScreenAutoRotation() {
        return deviceSettings.putInt(AndroidSystemSettings.ACCELEROMETER_ROTATION, 0);
    }

    /**
     * Sets new orientation in space of this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param deviceOrientation
     *        - new {@link DeviceOrientation DeviceOrientation} to be set.
     * @return <code>true</code> if the orientation setting is successful, <code>false</code> if it fails.
     * @deprecated
     */
    @Deprecated
    public boolean setDeviceOrientation(DeviceOrientation deviceOrientation) {
        communicator.sendAction(RoutingAction.SET_ORIENTATION, deviceOrientation);
        // TODO validation maybe?
        return true;
    }

    /**
     * Locks the device.
     *
     * @return <code>true</code> if the lock state setting is successful, <code>false</code> if it fails
     */
    public boolean lock() {
        return setLockState(true);
    }

    /**
     * Unlocks the device.
     *
     * @return <code>true</code> if the lock state setting is successful, <code>false</code> if it fails
     */
    public boolean unlock() {
        return setLockState(false);
    }

    private boolean setLockState(boolean state) {
        if (state) {
            return isLocked() || pressButton(HardwareButton.POWER);
        } else {
            if (!isLocked()) {
                return true;
            }

            try {
                startActivity(ATMOSPHERE_SERVICE_PACKAGE, ATMOSPHERE_UNLOCK_DEVICE_ACTIVITY, false);
            } catch (ActivityStartingException e) {
                return false;
            }

            waitForAwakeState(WAIT_FOR_AWAKE_STATE_INTERVAL, true);
            pressButton(HardwareButton.BACK);

            return !isLocked();
        }
    }

    /**
     * Wait for changing the awake state of the device.
     *
     * @param timeout
     *        - time for waiting to be changed the awake state
     * @param isAwake
     *        - expected awake status <code>true</code> for awake and <code>false</code> for asleep
     * @return <code>true</code> if the state is changed as expected and <code>false</code> otherwise.
     */
    private boolean waitForAwakeState(int timeout, boolean isAwake) {
        for (int i = 0; i < timeout; i += WAIT_FOR_AWAKE_STATE_INTERVAL) {
            try {
                Thread.sleep(WAIT_FOR_AWAKE_STATE_INTERVAL);
                if (isAwake == isAwake()) {
                    return true;
                }
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Sets new magnetic field for this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param deviceMagneticField
     *        - new {@link DeviceMagneticField DeviceMagneticField} to be set.
     * @return <code>true</code> if the magnetic field setting is successful, <code>false</code> if it fails.
     */
    public boolean setMagneticField(DeviceMagneticField deviceMagneticField) {
        Object result = communicator.sendAction(RoutingAction.SET_MAGNETIC_FIELD, deviceMagneticField);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sets new proximity for this device. Can only be applied on <b>emulators</b>. You can use proximity constants from
     * the {@link DeviceProximity} class.
     *
     * @param proximity
     *        - the new proximity to be set
     * @return <code>true</code> if the proximity setting was successful, <code>false</code> otherwise
     */
    public boolean setProximity(float proximity) {
        Object result = communicator.sendAction(RoutingAction.SET_PROXIMITY, proximity);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sets the mobile data state of this device.<br>
     * Can only be applied on <b>emulators</b>.
     *
     * @param state
     *        - {@link MobileDataState} to set.
     * @return <code>true</code> if the mobile data state setting is successful, <code>false</code> if it fails.
     */
    public boolean setMobileDataState(MobileDataState state) {
        Object result = communicator.sendAction(RoutingAction.SET_MOBILE_DATA_STATE, state);

        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sets the environment power-related properties of this device.<br>
     * <i>On real devices, this manipulation only lasts for limited period of time (until the Android BatteryManager
     * updates the battery information).</i>
     *
     * @param properties
     *        - the new power related environment properties to be set.
     * @return <code>true</code> if the environment manipulation is successful, <code>false</code> otherwise.
     */
    public boolean setPowerProperties(PowerProperties properties) {
        Object result = communicator.sendAction(RoutingAction.SET_POWER_PROPERTIES, properties);
        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Sets new screen orientation for this device.<br>
     * Implicitly turns off screen auto rotation.
     *
     * @param screenOrientation
     *        - new {@link ScreenOrientation ScreenOrientation} to be set.
     * @return <code>true</code> if the screen orientation setting is successful, <code>false</code> if it fails.
     */
    public boolean setScreenOrientation(ScreenOrientation screenOrientation) {

        if (!disableScreenAutoRotation()) {
            String message = "Screen orientation was not set due to setting auto rotation failure.";
            LOGGER.error(message);
            return false;
        }
        boolean success = deviceSettings.putInt(AndroidSystemSettings.USER_ROTATION,
                                                screenOrientation.getOrientationNumber());

        return success;
    }

    /**
     * Enables the WiFi of this device.
     *
     * @return <code>true</code> if the WiFi enabling is successful, <code>false</code> if it fails
     */
    public boolean enableWiFi() {
        Object result = communicator.sendAction(RoutingAction.SET_WIFI_STATE, true);

        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Disables the WiFi of this device.
     *
     * @return <code>true</code> if the WiFi disabling is successful, <code>false</code> if it fails
     */
    public boolean disableWiFi() {
        Object result = communicator.sendAction(RoutingAction.SET_WIFI_STATE, false);

        return result == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Starts an Activity from a package on this device.
     *
     * @param packageName
     *        - package name from which an activity should be started.
     * @param activityName
     *        - activity name to be started. Expects either absolute name or a name starting with dot (.), relative to
     *        the packageName.
     * @return <code>true</code> if the activity start is successful, <code>false</code> if it fails.
     *
     * @throws ActivityStartingException
     *         when the activity can't be started.
     */
    public boolean startActivity(String packageName, String activityName) throws ActivityStartingException {
        return startActivity(packageName, activityName, true);
    }

    /**
     * Starts an Activity from a package on this device.
     *
     * @param packageName
     *        - package name from which an activity should be started.
     * @param activityName
     *        - activity name to be started. Expects either absolute name or a name starting with dot (.), relative to
     *        the packageName.
     * @param unlockDevice
     *        - if <code>true</code>, unlocks the device before starting the activity.
     * @return <code>true</code> if the activity start is successful, <code>false</code> if it fails.
     * @throws ActivityStartingException
     *         when the package or activity is invalid.
     */
    public boolean startActivity(String packageName, String activityName, boolean unlockDevice)
        throws ActivityStartingException {
        if (unlockDevice) {
            setLockState(false);
        }

        IntentBuilder intentBuilder = new IntentBuilder(IntentAction.START_COMPONENT);
        intentBuilder.putComponent(packageName + "/" + activityName);
        String query = intentBuilder.buildIntentCommand();
        String response = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);

        if (response == null || response.contains("Error: Activity class")) {
            // FIXME TBD should this method return false or should it throw an
            // exception?
            String message = "The passed package or Activity was not found.";
            LOGGER.error(message);
            throw new ActivityStartingException(message);
        }
        return true;
    }

    /**
     * Unlocks the device and starts an application on it.
     *
     * @param packageName
     *        - name of the application's package
     *
     * @return <code>true</code> if the application launch is successful and <code>false</code> otherwise
     */
    public boolean startApplication(String packageName) {
        boolean result = startApplication(packageName, true);
        return result;
    }

    /**
     * Starts an application on the device.
     *
     * @param packageName
     *        - name of the application's package
     *
     * @param shouldUnlockDevice
     *        - if <code>true</code>, unlocks the device before starting the application
     *
     * @return <code>true</code> if the application launch is successful and <code>false</code> otherwise
     */
    public boolean startApplication(String packageName, boolean shouldUnlockDevice) {
        if (shouldUnlockDevice) {
            setLockState(false);
        }

        Boolean response = (Boolean) communicator.sendAction(RoutingAction.START_APP, packageName);

        return response;
    }

    /**
     * Uninstalls an application from the device.
     *
     * @param packageName
     *        - name of the application's package
     *
     * @return <code>true</code> if the application was successfully uninstalled, <code>false</code> otherwise
     */
    public boolean uninstallApplication(String packageName) {
        Object response = communicator.sendAction(RoutingAction.UNINSTALL_APP, packageName);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Reinstalls a given application by package name and path.
     *
     * @param packageName
     *        - the package name of the application
     * @param pathToApk
     *        - location of the file to be installed
     * @return true if the reinstall was successful, false otherwise
     */
    public boolean reinstallApplication(String packageName, String pathToApk) {
        boolean uninstallResponse = uninstallApplication(packageName);
        if (!uninstallResponse) {
            return false;
        }

        return installAPK(pathToApk);
    }

    /**
     * Simulates a swipe from a point to another unknown point.
     *
     * @param point
     *        - the starting point.
     * @return <code>true</code> if the swipe is successful, <code>false</code> if it fails.
     */
    public boolean swipe(Point point, SwipeDirection swipeDirection) {
        validatePointOnScreen(point);

        DeviceInformation information = getInformation();
        Pair<Integer, Integer> resolution = information.getResolution();
        Gesture swipe = GestureCreator.createSwipe(point, swipeDirection, resolution);
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, swipe);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Executes a simple tap on the screen of this device at a specified location point.
     *
     * @param tapPoint
     *        - {@link Point Point} on the screen to tap on.
     *
     * @return <code>true</code> if tapping screen is successful, <code>false</code> if it fails.
     */
    public boolean tapScreenLocation(Point tapPoint) {
        int tapPointX = tapPoint.getX();
        int tapPointY = tapPoint.getY();
        String query = "input tap " + tapPointX + " " + tapPointY;

        showTapLocation(tapPoint);

        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);
        return communicator.getLastException() == null;
    }

    /**
     * Executes long press on point on the screen with given coordinates and (default) timeout for the gesture
     * {@value #LONG_PRESS_DEFAULT_TIMEOUT} ms.
     *
     * @param pressPoint
     *        - {@link Point point} on the screen where the long press should be executed.
     * @return - true, if operation is successful, and false otherwise.
     */
    public boolean longPress(Point pressPoint) {
        return longPress(pressPoint, LONG_PRESS_DEFAULT_TIMEOUT);
    }

    /**
     * Executes long press on point on the screen with given coordinates and timeout for the gesture in ms.
     *
     * @param pressPoint
     *        - {@link Point point} on the screen where the long press should be executed.
     * @param timeout
     *        - the time in ms, showing how long should the holding part of the gesture continues.
     * @return - true, if operation is successful, and false otherwise.
     */
    public boolean longPress(Point pressPoint, int timeout) {
        Gesture longPress = GestureCreator.createLongPress(pressPoint.getX(), pressPoint.getY(), timeout);
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, longPress);

        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Drags and drops from point (Point startPoint) to point (Point endPoint).
     *
     * @param startPoint
     *        - start point of the drag and drop gesture
     * @param endPoint
     *        - end point of the drag and drop gesture
     * @return <code>true</code>, if operation is successful, <code>false</code>otherwise
     */
    public boolean drag(Point startPoint, Point endPoint) {
        validatePointOnScreen(endPoint);
        Gesture drag = GestureCreator.createDrag(startPoint, endPoint);
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, drag);
        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Checks whether the given point is inside the bounds of the screen, and throws an {@link IllegalArgumentException}
     * otherwise.
     *
     * @param point
     *        - the point to be checked
     */
    private void validatePointOnScreen(Point point) {
        DeviceInformation information = getInformation();
        Pair<Integer, Integer> resolution = information.getResolution();

        boolean hasPositiveCoordinates = point.getX() >= 0 && point.getY() >= 0;
        boolean isOnScreen = point.getX() <= resolution.getKey() && point.getY() <= resolution.getValue();

        if (!hasPositiveCoordinates || !isOnScreen) {
            String exeptionMessageFormat = "The passed point with coordinates (%d, %d) is outside the bounds of the screen. Screen dimentions (%d, %d)";
            String message = String.format(exeptionMessageFormat,
                                           point.getX(),
                                           point.getY(),
                                           resolution.getKey(),
                                           resolution.getValue());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);

        }
    }

    /**
     * Check if there are running processes on the device with the given package
     *
     * @param packageName
     *        - package of the process that we want to check
     * @return - true, if there are running process and false otherwise
     */

    public boolean isProcessRunning(String packageName) {
        return (boolean) communicator.sendAction(RoutingAction.GET_PROCESS_RUNNING, packageName);
    }

    /**
     * ForceStops all the processes containing the given package.
     *
     * @param packageName
     *        - package of the processes that we want to stop.
     * @return - true, if execution of the command is successful, and false otherwise.
     *
     * @note - doesn't work for system processes in the Android OS such as phone, sms, etc.
     */
    public boolean forceStopProcess(String packageName) {
        Object response = communicator.sendAction(RoutingAction.FORCE_STOP_PROCESS, packageName);
        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Stops a background process by given package.
     *
     * @param packageName
     *        - contains the package of the process.
     *
     * @Note Can not be used on system processes.
     * @Note This method kills only processes that are safe to kill and that will not impact the user experience.
     * @Note Usage of this method on a process that contains service will result in process restart.
     */
    public void stopBackgroundProcess(String packageName) {
        communicator.sendAction(RoutingAction.STOP_BACKGROUND_PROCESS, packageName);
    }

    /**
     * Opens the notification bar on the device.
     *
     * @return true if the opening of the notification bar was successful, false otherwise
     */
    public boolean openNotificationBar() {
        return (boolean) communicator.sendAction(RoutingAction.OPEN_NOTIFICATION_BAR);
    }

    /**
     * Opens the quick settings on the device.
     *
     * @return true if the opening of the quick settings was successful, false otherwise
     */
    public boolean openQuickSettings() {
        return (boolean) communicator.sendAction(RoutingAction.OPEN_QUICK_SETTINGS);
    }

    /**
     * Sets the timeout in the system settings, after which the screen is turned off.
     *
     * @param screenOffTimeout
     *        - timeout in milliseconds, after which the screen is turned off.
     * @return true if the given screen off timeout is successfully set.
     * @Note On emulators the screen is only dimmed.
     */
    public boolean setScreenOffTimeout(long screenOffTimeout) {
        return deviceSettings.putLong(AndroidSystemSettings.SCREEN_OFF_TIMEOUT, screenOffTimeout);
    }

    /**
     * Gets the timeout from the system settings, after which the screen is turned off.
     *
     * @return timeout in milliseconds, after which the screen is turned off.
     */
    public long getScreenOffTimeout() {
        return deviceSettings.getLong(AndroidSystemSettings.SCREEN_OFF_TIMEOUT, 0);
    }

    /**
     * Sets a default keyboard by given ID.
     *
     * @return true if setting the IME is successful and false otherwise.
     */
    public boolean setDefaultIME(String keyboardID) {
        return (boolean) communicator.sendAction(RoutingAction.SET_DEFAULT_INPUT_METHOD, keyboardID);
    }

    /**
     * Sets the Atmosphere IME keyboard as default. The Atmosphere IME is a small android application that is a simple
     * implementation of input keyboard for Android. It is needed in order to make sure we can execute the tests
     * requiring text input.
     *
     * @return true if setting the IME is successful and false otherwise.
     */
    public boolean setAtmosphereIME() {
        return (boolean) communicator.sendAction(RoutingAction.SET_ATMOSPHERE_IME_AS_DEFAULT);
    }

    /**
     * Gets the {@link DeviceSettingsManager settings manager} of the current device, that allows getting and inserting
     * device settings.
     *
     * @return {@link DeviceSettingsManager} instance for this device
     */
    public DeviceSettingsManager getDeviceSettingsManager() {
        return deviceSettings;
    }

    /**
     * Mocks the location of the device with the one specified in the passed location object.
     *
     * @param mockLocation
     *        - the location to be mocked
     * @return <code>true</code> if the location of the device was successfully mocked, <code>false</code> otherwise
     */
    public boolean mockLocation(GeoLocation mockLocation) {
        return (Boolean) communicator.sendAction(RoutingAction.MOCK_LOCATION, mockLocation);
    }

    /**
     * Disables passing mock location data for the provider with the given name.
     *
     * @param providerName
     *        - the provider whose mocking should be disabled
     */
    public void disableMockLocation(String providerName) {
        communicator.sendAction(RoutingAction.DISABLE_MOCK_LOCATION, providerName);
    }

    /**
     * Dismisses and re-enables the keyguard of the device in order to Lock and Unlock it.
     *
     * @param keyguardStatus
     *        - <code>true</code> if the keyguard should be re-enabled and <code>false</code> to dismiss it.
     *
     * @Note The keyguard should be re-enabled for the device's lock to work properly again.
     */
    public void setKeyguard(boolean keyguardStatus) {
        communicator.sendAction(RoutingAction.SET_KEYGUARD, keyguardStatus);
    }

    /**
     * Gets all task that are currently running on the device, with the most recent being first and older ones after in
     * order.
     *
     * @param maxNum
     *        - maximum number of task that are going to be get from the device
     *
     * @return array of the running tasks id.
     *         <p>
     *         Note: Useful with {@link #bringTaskToFront(int, int) bringTaskToFront} and
     *         {@link #waitForTasksUpdate(int, int, int) waitForTaskUpdate}.
     *         </p>
     *
     * @deprecated Since LOLLIPOP, this method is no longer available. It will still return a small subset of its data:
     *             at least the caller's own tasks, and possibly some other tasks such as home that are known to not be
     *             sensitive.
     */
    @Deprecated
    public int[] getRunningTaskIds(int maxNum) {
        return (int[]) communicator.sendAction(RoutingAction.GET_RUNNING_TASK_IDS, maxNum);
    }

    /**
     * Bring the given task to the foreground of the screen.
     *
     * @param taskId
     *        - the id of the task that is going to be brought to the foreground.
     * @param timeout
     *        - to wait before bringing the task to the foreground.
     * @return <code>true</code> if the task is successfully brought on the foreground and <code>false</code> otherwise.
     */
    public boolean bringTaskToFront(int taskId, int timeout) {
        return (boolean) communicator.sendAction(RoutingAction.BRING_TASK_TO_FRONT, taskId, timeout);
    }

    /**
     * Waits for the given task to be moved to given position in running tasks.
     *
     * @param taskId
     *        - the id of the task.
     * @param position
     *        - the position of the task in which it should be after the update.
     * @param timeout
     *        - to wait for updating the task.
     * @return <code>true</code> if the task is updated and <code>false</code> otherwise.
     *
     * @deprecated Since LOLLIPOP, this method is no longer avaible.
     */
    @Deprecated
    public boolean waitForTasksUpdate(int taskId, int position, int timeout) {
        return (boolean) communicator.sendAction(RoutingAction.WAIT_FOR_TASKS_UPDATE, taskId, position, timeout);
    }

    /**
     * Simulates the given gesture.
     *
     * @param gesture
     *        - the gesture to be executed.
     * @return <code>true</code> if the gesture is executed successfully, <code>false</code> otherwise.
     */
    public boolean playGesture(Gesture gesture) {
        Object response = communicator.sendAction(RoutingAction.PLAY_GESTURE, gesture);
        return response == DeviceCommunicator.VOID_SUCCESS;
    }

    /**
     * Checks if the given image is present on the screen of the device.
     *
     * @param image
     *        - image that will be sought for on the active screen
     * @return <code>true</code> if the image is present on the screen of the device and <code>false</code> otherwise
     * @throws GettingScreenshotFailedException
     *         if getting screenshot from the device failed
     */
    public boolean isImagePresentOnScreen(Image image) throws GettingScreenshotFailedException {
        Image currentScreenImage = getDeviceScreenshotImage();
        return currentScreenImage.containsImage(image);
    }

    /**
     * Gets a screenshot from the device as buffered image.
     *
     * @return BufferedImage that contains the screenshot from the device
     * @throws GettingScreenshotFailedException
     *         if getting screenshot from the device fails
     */
    public Image getDeviceScreenshotImage() throws GettingScreenshotFailedException {
        byte[] imageInByte = getScreenshot();
        InputStream inputStream = new ByteArrayInputStream(imageInByte);

        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            return new Image(bufferedImage);
        } catch (IOException e) {
            throw new GettingScreenshotFailedException("Getting screenshot from the device failed.", e);
        }
    }

    /**
     * Starts screen recording.
     * <p>
     * Note: This method works only for Android 4.4 and above.
     * </p>
     */
    public void startScreenRecording() {
        communicator.sendAction(RoutingAction.START_RECORDING);
    }

    /**
     * Stops screen recording.
     */
    public void stopScreenRecording() {
        communicator.sendAction(RoutingAction.STOP_RECORDING);
    }

    /**
     * Check if the GPS location is enabled on this device.
     *
     * @return <code>true</code> if the GPS location is enabled, <code>false</code> if it's disabled
     */
    public boolean isGpsLocationEnabled() {
        return (boolean) communicator.sendAction(RoutingAction.IS_GPS_LOCATION_ENABLED);
    }

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
     * Checks if any audio is currently playing on the device.
     * 
     * @return <code>true</code> if an audio is playing, <code>false</code> otherwise
     */
    public Boolean isAudioPlaying() {
        return (boolean) communicator.sendAction(RoutingAction.IS_AUDIO_PLAYING);
    }

    /**
     * Changes the GPS location state of this device.
     *
     * @param state
     *        - desired GPS location state: <code>true</code> - enable GPS location, <code>false</code> - disable GPS
     *        location
     * @return <code>true</code> if the GPS location state setting is successful, <code>false</code> if it fails
     */
    private boolean setGpsLocationState(boolean state) {
        if (isGpsLocationEnabled() == state) {
            return true;
        }

        boolean isActionSuccessful = false;

        openLocationSettings();

        UiElementSelector switchButtonSelector = new UiElementSelector();
        switchButtonSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, ANDROID_WIDGET_SWITCH_CLASS_NAME);

        UiElementSelector checkBoxSelector = new UiElementSelector();
        checkBoxSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, ANDROID_WIDGET_CHECKBOX_CLASS_NAME);

        if (tapLocationSettingsActivityElement(switchButtonSelector)
                || tapLocationSettingsActivityElement(checkBoxSelector)) {
            isActionSuccessful = true;
        }

        UiElementSelector agreeButtonSelector = new UiElementSelector();
        agreeButtonSelector.addSelectionAttribute(CssAttribute.RESOURCE_ID, AGREE_BUTTON_RESOURCE_ID);
        /*
         * TODO: Remove the Thread.sleep after wait for element start working with selectors created by resource ID.
         */
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        tapLocationSettingsActivityElement(agreeButtonSelector);

        pressButton(HardwareButton.BACK);

        return isActionSuccessful;
    }

    /**
     * Taps element from location settings activity.
     *
     * @param selector
     *        - the {@link UiElementSelector} instance of the desired element
     * @return <code>true</code> if tapping the element is successful, <code>false</code> if it fails
     */
    private boolean tapLocationSettingsActivityElement(UiElementSelector selector) {
        Screen screen = getActiveScreen();
        int waitForElementTimeout = 5000;

        boolean isElementPresent = screen.waitForElementExists(selector, waitForElementTimeout);

        if (isElementPresent) {
            try {
                // We use getElements to use this logic for both switch view elements and check box elements. The reason
                // is that there is only one switch view element and many check boxes, but we need only the first one.
                UiElement element = screen.getElements(selector).get(0);
                return element.tap();
            } catch (UiElementFetchingException e) {
            }
        }

        return false;
    }

    /**
     * Opens the location settings activity.
     */
    private void openLocationSettings() {
        communicator.sendAction(RoutingAction.OPEN_LOCATION_SETTINGS);
    }

    /**
     * Gets the text of the last detected toast message.
     *
     * @return the text of the last toast message or <code>null</code> if such is not detected yet
     */
    public String getLastToast() {
        Object response = communicator.sendAction(RoutingAction.GET_LAST_TOAST);

        if (!(response instanceof String)) {
            return null;
        }

        return (String) response;
    }

    /**
     * Shows the tap location on the current device screen.
     * 
     * @param point
     *        - the point where the tap will be placed
     */
    private void showTapLocation(Point point) {
        communicator.sendAction(RoutingAction.SHOW_TAP_LOCATION, point);
    }

    /**
     * Clears the data of a given application.
     * 
     * @param packageName
     *        - the package name of the application
     */
    public void clearApplicationData(String packageName) {
        communicator.sendAction(RoutingAction.CLEAR_APP_DATA, packageName);
    }
}
