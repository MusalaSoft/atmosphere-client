package com.musala.atmosphere.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.geometry.Point;
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
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.MobileDataState;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;
import com.musala.atmosphere.commons.gesture.Gesture;
import com.musala.atmosphere.commons.util.IntentBuilder;
import com.musala.atmosphere.commons.util.IntentBuilder.IntentAction;

/**
 * Android device representing class.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class Device {
    private static final int MAX_BUFFER_SIZE = 8092; // 8K

    private static final Logger LOGGER = Logger.getLogger(Device.class.getCanonicalName());

    private static final String AWAKE_STATUS_DUMP_COMMAND = "dumpsys power";

    private static final String LOCKED_STATUS_DUMP_COMMAND = "dumpsys activity";

    private static final String AWAKE_CHECK_STRING_API_17 = " mWakefulness=Awake";

    private static final String AWAKE_CHECK_EXPRESSION_API_16 = "mPowerState=[1-9]\\d*";

    private static final String LOCKED_CHECK_STRING = "mLockScreenShown true";

    private static final String SAMSUNG_MANUFACTURER_LOWERCASE = "samsung";

    private DeviceSettingsManager deviceSettings;

    private ServerConnectionHandler serverConnectionHandler;

    private DeviceCommunicator communicator;

    /**
     * Constructor that creates a usable Device object by a given IClientDevice, it's invocation passkey.
     * 
     * @param iClientDevice
     * @param devicePasskey
     * @param serverConnectionHandler
     */
    Device(IClientDevice clientDevice, long devicePasskey, ServerConnectionHandler serverConnectionHandler) {
        this.serverConnectionHandler = serverConnectionHandler;
        communicator = new DeviceCommunicator(clientDevice, devicePasskey);
        deviceSettings = new DeviceSettingsManager(communicator);
    }

    void release() {
        communicator.release();
    }

    DeviceCommunicator getCommunicator() {
        return communicator;
    }

    /**
     * Gets the device information about this device.
     * 
     * @return {@link DeviceInformation DeviceInformation} structure with information for the testing device,<br>
     *         <code>null</code> if getting device information fails.
     */
    public DeviceInformation getInformation() {
        DeviceInformation wrappedDeviceInformation = (DeviceInformation) communicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);
        return wrappedDeviceInformation;
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
     * Changes the screen auto rotation of this device.<br>
     * Controls whether the accelerometer will be used to change screen orientation.
     * 
     * @param autoRotation
     *        - <code>false</code> - disables screen auto rotation; <code>true</code> - enables screen auto rotation.
     * @return <code>true</code> if the auto rotation setting is successful, <code>false</code> if it fails.
     */
    public boolean setAutoRotation(boolean autoRotation) {
        int autoRotationInt = autoRotation ? 1 : 0;
        boolean success = deviceSettings.putInt(AndroidSystemSettings.ACCELEROMETER_ROTATION, autoRotationInt);

        return success;
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

        if (!setAutoRotation(false)) {
            LOGGER.error("Screen orientation was not set due to setting auto rotation failure.");
            return false;
        }
        boolean success = deviceSettings.putInt(AndroidSystemSettings.USER_ROTATION,
                                                screenOrientation.getOrientationNumber());

        return success;
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
     * Gets current acceleration of this device.
     * 
     * @return {@link DeviceAcceleration DeviceAcceleration} of the device,<br>
     *         <code>null</code> if getting acceleration fails.
     */
    public DeviceAcceleration getDeviceAcceleration() {
        DeviceAcceleration deviceAcceleration = (DeviceAcceleration) communicator.sendAction(RoutingAction.GET_DEVICE_ACCELERATION);
        return deviceAcceleration;
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
            LOGGER.error("Enabling airplane mode on emulator disconnects it from ATMOSPHERE Agent and this emulator can be connected back only after Agent restart. Setting airplane mode for emulators is prohibited.");
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
            return false;
        }

        String intentCommandResponse = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND,
                                                                        intentCommand);
        Pattern intentCommandResponsePattern = Pattern.compile(INTENT_COMMAND_RESPONSE);
        Matcher intentCommandResponseMatcher = intentCommandResponsePattern.matcher(intentCommandResponse);
        if (!intentCommandResponseMatcher.find()) {
            LOGGER.error("Broadcasting notification intent failed.");
            return false;
        }

        return true;
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
            return null;
        }
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
            LOGGER.error("Saving screenshot file failed.", e);
            return false;
        }

        return true;
    }

    /**
     * Gets the currently active {@link Screen Screen} of this device.
     * 
     * @return {@link Screen Screen} instance.<br>
     *         <code>null</code> if getting active screen fails.
     */
    public Screen getActiveScreen() {
        String uiHierarchy = (String) communicator.sendAction(RoutingAction.GET_UI_XML_DUMP);

        if (uiHierarchy == null) {
            return null;
        }

        Screen activeScreen = new Screen(this, uiHierarchy);
        return activeScreen;
    }

    /**
     * Installs a specified Android application file on this device.<br>
     * 
     * @param path
     *        - location of the file to be installed.
     * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
     */
    public boolean installAPK(String path) {
        return doApkInstallation(path);
    }

    /**
     * Installs a specified Android application file on this device.<br>
     * 
     * @param path
     *        - location of the file to be installed.
     * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
     */
    private boolean doApkInstallation(String path) {
        // A string that will be used to tell which step of installation was reached
        String currentInstallationStepDescription = null;
        FileInputStream fileReaderFromApk = null;
        try {
            currentInstallationStepDescription = "Create file for storing the apk";
            Object response = communicator.sendAction(RoutingAction.APK_INIT_INSTALL);
            if (response != DeviceCommunicator.VOID_SUCCESS) {
                throw communicator.getLastException();
            }

            currentInstallationStepDescription = "Locating the file to store the apk in";
            // Transfer the installation file from the current machine to the device
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
            response = communicator.sendAction(RoutingAction.APK_BUILD_AND_INSTALL);
            if (response != DeviceCommunicator.VOID_SUCCESS) {
                throw communicator.getLastException();
            }

            LOGGER.info("File installation successfull.");
        } catch (IOException | CommandFailedException e) {
            String message = String.format("Exception occurred while '%s'.", currentInstallationStepDescription);
            LOGGER.fatal(message, e);
            // This method should work even if the apk file was not created at all.
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
     * Redirects specific IP address to another IP address.
     * 
     * @param toIp
     * @param toNewIp
     * @return <code>true</code> if the connection redirection is successful, <code>false</code> if it fails.
     */
    public boolean redirectConnection(String toIp, String toNewIp) {
        // TODO implement device.redirectConnection
        return false;
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
     * Executes user-described gesture on this device.
     * 
     * @param gesture
     *        - the gesture to be executed.
     */
    public void executeGesture(Gesture gesture) {
        communicator.sendAction(RoutingAction.PLAY_GESTURE, gesture);
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

        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);
        return communicator.getLastException() == null;
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
     */
    public boolean startActivity(String packageName, String activityName, boolean unlockDevice)
        throws ActivityStartingException {
        if (unlockDevice) {
            setLocked(false);
        }

        IntentBuilder intentBuilder = new IntentBuilder(IntentAction.START_COMPONENT);
        intentBuilder.putComponent(packageName + "/" + activityName);
        String query = intentBuilder.buildIntentCommand();
        String response = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, query);

        if (response == null || response.contains("Error: Activity class")) {
            // FIXME TBD should this method return false or should it throw an exception?
            throw new ActivityStartingException("The passed package or Activity was not found.");
        }
        return true;
    }

    /**
     * Changes the lock state of this device.
     * 
     * 
     * 
     * @param state
     *        - desired lock state of the device; <code>true</code> - lock the device, <code>false</code> - unlock the
     *        device.
     * @return <code>true</code> if the lock state setting is successful, <code>false</code> if it fails.
     */
    public boolean setLocked(boolean state) {
        DeviceInformation deviceInformation = getInformation();
        String deviceManufacturer = deviceInformation.getManufacturer();
        // There is a different logic for Samsung devices because of they specific locking and unlocking.
        if (deviceManufacturer.toLowerCase().equals(SAMSUNG_MANUFACTURER_LOWERCASE)) {
            boolean lockTerms = state && isAwake();
            boolean unlockTerms = !state && !isAwake();
            if (lockTerms || unlockTerms) {
                return pressButton(HardwareButton.POWER);
            }
        } else if (state) {
            return isLocked() || pressButton(HardwareButton.POWER);
        } else {
            if (!isLocked()) {
                return true;
            }
            boolean isAwake = isAwake() || pressButton(HardwareButton.POWER);
            return isAwake && pressButton(HardwareButton.MENU);
        }
        return false;
    }

    /**
     * Checks if this device is in a WAKE state.<br>
     * 
     * @return <code>true</code> if the device is awake.<br>
     *         <code>false</code> if the device is asleep.<br>
     *         <code>null</code> if the check failed.
     */
    public Boolean isAwake() {
        String dump = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, AWAKE_STATUS_DUMP_COMMAND);
        if (communicator.getLastException() != null) {
            return null;
        }

        Pattern awakeAPI16Pattern = Pattern.compile(AWAKE_CHECK_EXPRESSION_API_16);
        Matcher awakeAPI16Matcher = awakeAPI16Pattern.matcher(dump);
        boolean awakeAPI16 = awakeAPI16Matcher.find();
        boolean awakeAPI17 = dump.contains(AWAKE_CHECK_STRING_API_17);

        return awakeAPI16 || awakeAPI17;
    }

    /**
     * Checks if this device is locked.
     * 
     * @return <code>true</code> if the device is locked.<br>
     *         <code>false</code> if the device is unlocked. <br>
     *         <code>null</code> if the check fails.
     */
    public Boolean isLocked() {
        String dump = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, LOCKED_STATUS_DUMP_COMMAND);
        if (communicator.getLastException() != null) {
            return null;
        }
        boolean locked = dump.contains(LOCKED_CHECK_STRING);
        return locked;
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
     * Inputs text on this device through the AtmosphereIME.<br>
     * Element on device's screen, that accepts text input should be preselected.
     * 
     * @param text
     *        - text to be input.
     * @param interval
     *        - time interval in milliseconds between typing each symbol.
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
     */
    public boolean inputText(String text, int interval) {
        if (text.isEmpty()) {
            LOGGER.info("Text input requested, but an empty String is given.");
            return true;
        }

        IntentBuilder intentBuilder = new IntentBuilder(IntentAction.ATMOSPHERE_TEXT_INPUT);

        char[] textCharArray = text.toCharArray();
        List<Integer> charsList = new LinkedList<Integer>();
        for (int i = 0; i < textCharArray.length; i++) {
            int numericalCharValue = textCharArray[i];
            charsList.add(numericalCharValue);
        }
        intentBuilder.putExtraIntegerList("text", charsList);

        if (interval > 0) {
            intentBuilder.putExtraInteger("interval", interval);
        }

        String builtCommand = intentBuilder.buildIntentCommand();
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, builtCommand);

        return communicator.getLastException() == null;
    }

    /**
     * Inputs text on this device through the AtmosphereIME.<br>
     * Element on device's screen, that accepts text input should be preselected.
     * 
     * @param text
     *        - text to be input.
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
     */
    public boolean inputText(String text) {
        return inputText(text, 0);
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
     * Gets the current network connection type of this device.
     * 
     * @return {@link ConnectionType},<br>
     *         <code>null</code> if getting connection type fails.
     */
    public ConnectionType getConnectionType() {
        ConnectionType type = (ConnectionType) communicator.sendAction(RoutingAction.GET_CONNECTION_TYPE);
        return type;
    }

    /**
     * Gets the current mobile data state of this device.<br>
     * Can only be applied on <b>emulators</b>.
     * 
     * @return {@link MobileDataState},<br>
     *         <code>null</code> if getting mobile data state fails.
     */
    public MobileDataState getMobileDataState() {
        MobileDataState state = (MobileDataState) communicator.sendAction(RoutingAction.GET_MOBILE_DATA_STATE);
        return state;
    }

    /**
     * Sets the WiFi state of this device.
     * 
     * @param state
     *        - <code>true</code> enables WiFi; <code>false</code> disables WiFi.
     * @return <code>true</code> if the WiFi state setting is successful, <code>false</code> if it fails.
     */
    public boolean setWiFi(boolean state) {
        Object result = communicator.sendAction(RoutingAction.SET_WIFI_STATE, state);
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
     * Accepts call to this device.<br>
     * 
     * @return <code>true</code> if the accepting call is successful, <code>false</code> if it fails.
     */
    public boolean acceptCall() {
        return pressButton(HardwareButton.ANSWER);
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
     * Obtains information about the telephony services on the device.
     * 
     * @return {@link TelephonyInformation} instance.
     */
    public TelephonyInformation getTelephonyInformation() {
        TelephonyInformation telephonyInformation = (TelephonyInformation) communicator.sendAction(RoutingAction.GET_TELEPHONY_INFO);
        return telephonyInformation;
    }
}
