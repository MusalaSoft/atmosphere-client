package com.musala.atmosphere.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.DeviceInvocationRejectedException;
import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.util.settings.AndroidGlobalSettings;
import com.musala.atmosphere.client.util.settings.AndroidSystemSettings;
import com.musala.atmosphere.client.util.settings.DeviceSettingsManager;
import com.musala.atmosphere.commons.ConnectionType;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.TelephonyInformation;
import com.musala.atmosphere.commons.beans.DeviceAcceleration;
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.MobileDataState;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
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
public class Device
{
	private static final int MAX_BUFFER_SIZE = 8092; // 8K

	private static final Logger LOGGER = Logger.getLogger(Device.class.getCanonicalName());

	private static final String AWAKE_STATUS_DUMP_COMMAND = "dumpsys power";

	private static final String LOCKED_STATUS_DUMP_COMMAND = "dumpsys activity";

	private static final String AWAKE_CHECK_STRING_API_17 = " mWakefulness=Awake";

	private static final String AWAKE_CHECK_EXPRESSION_API_16 = "mPowerState=[1-9]\\d*";

	private static final String LOCKED_CHECK_STRING = "mLockScreenShown true";

	private IClientDevice wrappedClientDevice;

	private final long invocationPasskey;

	private DeviceSettingsManager deviceSettings;

	private ServerConnectionHandler serverConnectionHandler;

	/**
	 * Constructor that creates a usable Device object by a given IClientDevice, it's invocation passkey.
	 * 
	 * @param iClientDevice
	 * @param devicePasskey
	 * @param serverConnectionHandler
	 */
	Device(IClientDevice iClientDevice, long devicePasskey, ServerConnectionHandler serverConnectionHandler)
	{
		wrappedClientDevice = iClientDevice;
		invocationPasskey = devicePasskey;
		this.serverConnectionHandler = serverConnectionHandler;
		deviceSettings = new DeviceSettingsManager(wrappedClientDevice, invocationPasskey);
	}

	void release()
	{
		wrappedClientDevice = new ReleasedClientDevice();
	}

	/**
	 * Gets the device information about this device.
	 * 
	 * @return {@link DeviceInformation DeviceInformation} structure with information for the testing device,<br>
	 *         <code>null</code> if getting device information fails.
	 */
	public DeviceInformation getInformation()
	{
		DeviceInformation wrappedDeviceInformation = null;
		try
		{
			wrappedDeviceInformation = wrappedClientDevice.getDeviceInformation(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting device information was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return wrappedDeviceInformation;
	}

	/**
	 * Gets current orientation in space of this device.
	 * 
	 * @return {@link DeviceOrientation DeviceOrientation} of the testing device,<br>
	 *         <code>null</code> if getting device orientation fails.
	 */
	public DeviceOrientation getDeviceOrientation()
	{
		DeviceOrientation deviceOrientation = null;
		try
		{
			deviceOrientation = wrappedClientDevice.getDeviceOrientation(invocationPasskey);

		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device orientation failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting device orientation was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
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
	public boolean setDeviceOrientation(DeviceOrientation deviceOrientation)
	{
		try
		{
			wrappedClientDevice.setDeviceOrientation(deviceOrientation, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device orientation failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting device orientation was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

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
	public boolean setAutoRotation(boolean autoRotation)
	{
		try
		{
			if (autoRotation)
			{
				deviceSettings.putInt(AndroidSystemSettings.ACCELEROMETER_ROTATION, 1);
			}
			else
			{
				deviceSettings.putInt(AndroidSystemSettings.ACCELEROMETER_ROTATION, 0);
			}
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting auto rotation was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device auto rotation failed.", e);
			return false;
		}
		return true;
	}

	/**
	 * Sets new screen orientation for this device.<br>
	 * Implicitly turns off screen auto rotation.
	 * 
	 * @param screenOrientation
	 *        - new {@link ScreenOrientation ScreenOrientation} to be set.
	 * @return <code>true</code> if the screen orientation setting is successful, <code>false</code> if it fails.
	 */
	public boolean setScreenOrientation(ScreenOrientation screenOrientation)
	{
		try
		{
			if (!setAutoRotation(false))
			{
				LOGGER.error("Screen orientation was not set due to setting auto rotation failure.");
				return false;
			}
			deviceSettings.putInt(AndroidSystemSettings.USER_ROTATION, screenOrientation.getOrientationNumber());
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting screen orientation was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting screen orientation failed.", e);
			return false;
		}

		return true;
	}

	/**
	 * Sets new acceleration for this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param deviceAcceleration
	 *        - new {@link DeviceAcceleration DeviceAcceleration} to be set.
	 * @return <code>true</code> if the acceleration setting is successful, <code>false</code> if it fails.
	 */
	public boolean setAcceleration(DeviceAcceleration deviceAcceleration)
	{
		try
		{
			wrappedClientDevice.setAcceleration(deviceAcceleration, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device acceleration failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting acceleration was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return true;
	}

	/**
	 * Gets current acceleration of this device.
	 * 
	 * @return {@link DeviceAcceleration DeviceAcceleration} of the device,<br>
	 *         <code>null</code> if getting acceleration fails.
	 */
	public DeviceAcceleration getDeviceAcceleration()
	{
		DeviceAcceleration deviceAcceleration = null;
		try
		{
			deviceAcceleration = wrappedClientDevice.getDeviceAcceleration(invocationPasskey);

		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device acceleration failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting acceleration was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return deviceAcceleration;
	}

	/**
	 * Gets a {@link PowerProperties} instance that contains information about the current device power-related
	 * environment.
	 * 
	 * @return a filled {@link PowerProperties} instance (or <code>null</code> if fetching the environment fails).
	 */
	public PowerProperties getPowerProperties()
	{
		PowerProperties result = null;
		try
		{
			result = wrappedClientDevice.getPowerProperties(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device power-related environment properties failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting device power-related environment properties was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
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
	public boolean setPowerProperties(PowerProperties properties)
	{
		try
		{
			wrappedClientDevice.setPowerProperties(properties, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device power-related environment properties failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting device power-related environment properties was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	// /**
	// * Gets network speeds of testing device
	// *
	// * @return - triple of ints meaning < uploadSpeed, downloadSpeed, latency >
	// * @throws RemoteException
	// */
	// Triple<Integer> getNetworkSpeed() throws RemoteException
	// {
	// // TODO implement device.getNetworkSpeed
	// return null;
	// }

	// /**
	// * Sets network speed
	// *
	// * @param upload
	// * @param download
	// * @param latency
	// */
	// public void setNetworkSpeed(int upload, int download, int latency)
	// {
	// // TODO implement device.setNetworkSpeed
	// }

	/**
	 * Sets the airplane mode state for this device.<br>
	 * <i><b>Warning:</b> enabling airplane mode on emulator disconnects it from ATMOSPHERE Agent and this emulator can
	 * be connected back only after Agent restart.</i>
	 * 
	 * @param airplaneMode
	 *        - <code>true</code> to enter device in airplane mode, <code>false</code> to exit device from airplane
	 *        mode.
	 * @return <code>true</code> if the airplane mode setting is successful, <code>false</code> if it fails.
	 */
	public boolean setAirplaneMode(boolean airplaneMode)
	{
		DeviceInformation deviceInformation = getInformation();
		int apiLevel = deviceInformation.getApiLevel();

		int airplaneModeIntValue = airplaneMode ? 1 : 0;

		IntentBuilder intentBuilder = new IntentBuilder(IntentAction.AIRPLANE_MODE_NOTIFICATION);
		intentBuilder.putExtraBoolean("state", airplaneMode);
		String intentCommand = intentBuilder.buildIntentCommand();

		final String INTENT_COMMAND_RESPONSE = "Broadcast completed: result=0";

		try
		{
			if (apiLevel >= 17)
			{
				deviceSettings.putInt(AndroidGlobalSettings.AIRPLANE_MODE_ON, airplaneModeIntValue);
			}
			else
			{
				deviceSettings.putInt(AndroidSystemSettings.AIRPLANE_MODE_ON, airplaneModeIntValue);
			}

			String intentCommandResponse = wrappedClientDevice.executeShellCommand(intentCommand, invocationPasskey);
			Pattern intentCommandResponsePattern = Pattern.compile(INTENT_COMMAND_RESPONSE);
			Matcher intentCommandResponseMatcher = intentCommandResponsePattern.matcher(intentCommandResponse);
			if (!intentCommandResponseMatcher.find())
			{
				throw new CommandFailedException("Broadcasting notification intent failed.");
			}
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device airplane mode failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting airplane mode was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Gets screenshot of this device's active screen.
	 * 
	 * @return byte buffer, containing captured device screen,<br>
	 *         <code>null</code> if getting screenshot fails.<br>
	 *         It can be subsequently dumped to a file and directly opened as a PNG image.
	 */
	public byte[] getScreenshot()
	{
		byte[] screenshot = null;
		try
		{
			screenshot = wrappedClientDevice.getScreenshot(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device screenshot failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting screenshot was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return screenshot;
	}

	/**
	 * Gets screenshot of this device's active screen and saves it as an image file at a specified location.
	 * 
	 * @param pathToImageFile
	 *        - location at which the screenshot image file should be saved.
	 * @return <code>true</code> if the getting screenshot is successful, <code>false</code> if it fails.
	 */
	public boolean getScreenshot(String pathToImageFile)
	{
		try
		{
			Path pathToPngFile = Paths.get(pathToImageFile);
			byte[] screenshot = getScreenshot();
			Files.write(pathToPngFile, screenshot);
		}
		catch (IOException e)
		{
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
	public Screen getActiveScreen()
	{
		String uiHierarchy = null;
		try
		{
			uiHierarchy = wrappedClientDevice.getUiXml(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device active screen failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting active screen was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		if (uiHierarchy == null)
		{
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
	public boolean installAPK(String path)
	{
		try
		{
			return doApkInstallation(path);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("APK installation was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Installs a specified Android application file on this device.<br>
	 * 
	 * @param path
	 *        - location of the file to be installed.
	 * @return <code>true</code> if the APK installation is successful, <code>false</code> if it fails.
	 * @throws InvalidPasskeyException
	 *         in case of {@link #wrappedClientDevice} using invalid pass key.
	 * @throws RemoteException
	 *         In case of problem with communication to the device.
	 */
	private boolean doApkInstallation(String path) throws InvalidPasskeyException, RemoteException
	{
		// A string that will be used to tell which step of installation was reached
		String currentInstallationStepDescription = null;
		FileInputStream fileReaderFromApk = null;
		try
		{
			currentInstallationStepDescription = "Create file for storing the apk";
			wrappedClientDevice.initApkInstall(invocationPasskey);

			currentInstallationStepDescription = "Locating the file to store the apk in";
			// Transfer the installation file from the current machine to the device
			byte[] buffer = new byte[MAX_BUFFER_SIZE];
			fileReaderFromApk = new FileInputStream(path);

			currentInstallationStepDescription = "Transferring installation file";
			LOGGER.info(currentInstallationStepDescription);
			int readBytes;
			while ((readBytes = fileReaderFromApk.read(buffer)) >= 0)
			{
				wrappedClientDevice.appendToApk(buffer, invocationPasskey, readBytes);
			}

			currentInstallationStepDescription = "Installing transferred file";
			LOGGER.info(currentInstallationStepDescription);
			wrappedClientDevice.buildAndInstallApk(invocationPasskey);
			LOGGER.info("File installation successfull.");
		}
		catch (RemoteException e)
		{
			// This exception will be handled on higher level.
			throw e;
		}
		catch (IOException | CommandFailedException e)
		{
			String message = String.format("Exception occurred while '%s'", currentInstallationStepDescription);
			LOGGER.fatal(message, e);
			// This method should work even if the apk file was not created at all.
			wrappedClientDevice.discardApk(invocationPasskey);
			return false;
		}
		finally
		{
			if (fileReaderFromApk != null)
			{
				try
				{
					fileReaderFromApk.close();
				}
				catch (IOException e)
				{
					// Nothing can be done here anymore
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * public State dumpCurrentState() { return IClientDevice.dumpCurrentState(); }
	 * 
	 * public void restoreState(State state) { IClientDevice.restoreState(state); }
	 */

	/**
	 * Redirects specific IP address to another IP address.
	 * 
	 * @param toIp
	 * @param toNewIp
	 * @return <code>true</code> if the connection redirection is successful, <code>false</code> if it fails.
	 */
	public boolean redirectConnection(String toIp, String toNewIp)
	{
		// TODO implement device.redirectConnection
		return false;
	}

	/**
	 * Simulates random finger actions on the screen of this device.
	 * 
	 * @return <code>true</code> if the random multi-touch event execution is successful, <code>false</code> if it
	 *         fails.
	 */
	public boolean randomMultiTouchevent()
	{
		// TODO implement device.randomMultiTouchEvent()
		return false;
	}

	/**
	 * Executes user-described gesture on this device.
	 * 
	 * @param gesture
	 *        - the gesture to be executed.
	 */
	public void executeGesture(Gesture gesture)
	{
		try
		{
			wrappedClientDevice.executeGesture(gesture, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Gesture execution failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Tapping screen location was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Executes a simple tap on the screen of this device at a specified location point.
	 * 
	 * @param tapPoint
	 *        - {@link Point Point} on the screen to tap on.
	 * 
	 * @return <code>true</code> if tapping screen is successful, <code>false</code> if it fails.
	 */
	public boolean tapScreenLocation(Point tapPoint)
	{
		int tapPointX = tapPoint.getX();
		int tapPointY = tapPoint.getY();
		String query = "input tap " + tapPointX + " " + tapPointY;
		try
		{
			wrappedClientDevice.executeShellCommand(query, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Device screen tap failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Tapping screen location was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return true;
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
	public boolean startActivity(String packageName, String activityName) throws ActivityStartingException
	{
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
		throws ActivityStartingException
	{
		if (unlockDevice)
		{
			setLocked(false);
		}

		IntentBuilder intentBuilder = new IntentBuilder(IntentAction.START_COMPONENT);
		intentBuilder.putComponent(packageName + "/" + activityName);
		String query = intentBuilder.buildIntentCommand();
		String response = null;
		try
		{
			response = wrappedClientDevice.executeShellCommand(query, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Starting activity location was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		if (response.contains("Error: Activity class"))
		{
			throw new ActivityStartingException("The passed package or Activity was not found.");
		}
		return true;
	}

	/**
	 * Changes the lock state of this device.
	 * 
	 * @param state
	 *        - desired lock state of the device; <code>true</code> - lock the device, <code>false</code> - unlock the
	 *        device.
	 * @return <code>true</code> if the lock state setting is successful, <code>false</code> if it fails.
	 */
	public boolean setLocked(boolean state)
	{
		if (state)
		{
			return isLocked() || pressButton(HardwareButton.POWER);
		}
		else
		{
			if (!isLocked())
			{
				return true;
			}
			boolean isAwake = isAwake() || pressButton(HardwareButton.POWER);
			return isAwake && pressButton(HardwareButton.MENU);
		}
	}

	/**
	 * Checks if this device is in a WAKE state.<br>
	 * 
	 * @return <code>true</code> if the device is awake.<br>
	 *         <code>false</code> otherwise.
	 */
	public boolean isAwake()
	{
		String dump = "";
		try
		{
			dump = wrappedClientDevice.executeShellCommand(AWAKE_STATUS_DUMP_COMMAND, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device wake state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Obtaining awake state was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
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
	 *         <code>false</code> otherwise.
	 */
	public boolean isLocked()
	{
		String dump = "";
		try
		{
			dump = wrappedClientDevice.executeShellCommand(LOCKED_STATUS_DUMP_COMMAND, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device lock state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Obtaining lock state was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
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
	public boolean pressButton(int keyCode)
	{
		String query = "input keyevent " + Integer.toString(keyCode);
		try
		{
			wrappedClientDevice.executeShellCommand(query, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending key input failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Button press was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Presses hardware button on this device.
	 * 
	 * @param button
	 *        - {@link HardwareButton HardwareButton} to be pressed.
	 * @return <code>true</code> if the button press is successful, <code>false</code> if it fails.
	 */
	public boolean pressButton(HardwareButton button)
	{
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
	public boolean inputText(String text, int interval)
	{
		if (text.isEmpty())
		{
			LOGGER.info("Text input requested, but an empty String is given.");
			return true;
		}

		IntentBuilder intentBuilder = new IntentBuilder(IntentAction.ATMOSPHERE_TEXT_INPUT);

		char[] textCharArray = text.toCharArray();
		List<Integer> charsList = new LinkedList<Integer>();
		for (int i = 0; i < textCharArray.length; i++)
		{
			int numericalCharValue = textCharArray[i];
			charsList.add(numericalCharValue);
		}
		intentBuilder.putExtraIntegerList("text", charsList);

		if (interval > 0)
		{
			intentBuilder.putExtraInteger("interval", interval);
		}

		String builtCommand = intentBuilder.buildIntentCommand();
		try
		{
			wrappedClientDevice.executeShellCommand(builtCommand, invocationPasskey);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending text input failed.", e);
			return false;
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Text input was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Inputs text on this device through the AtmosphereIME.<br>
	 * Element on device's screen, that accepts text input should be preselected.
	 * 
	 * @param text
	 *        - text to be input.
	 * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
	 */
	public boolean inputText(String text)
	{
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
	public boolean setMobileDataState(MobileDataState state)
	{
		try
		{
			wrappedClientDevice.setMobileDataState(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting mobile data state failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting mobile data state was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Gets the current network connection type of this device.
	 * 
	 * @return {@link ConnectionType},<br>
	 *         <code>null</code> if getting connection type fails.
	 */
	public ConnectionType getConnectionType()
	{
		ConnectionType type = null;
		try
		{
			type = wrappedClientDevice.getConnectionType(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device connection type failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting connection type was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return type;
	}

	/**
	 * Gets the current mobile data state of this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @return {@link MobileDataState},<br>
	 *         <code>null</code> if getting mobile data state fails.
	 */
	public MobileDataState getMobileDataState()
	{
		MobileDataState state = null;
		try
		{
			state = wrappedClientDevice.getMobileDataState(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device connection type failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting mobile data state was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}
		return state;
	}

	/**
	 * Sets the WiFi state of this device.
	 * 
	 * @param state
	 *        - <code>true</code> enables WiFi; <code>false</code> disables WiFi.
	 * @return <code>true</code> if the WiFi state setting is successful, <code>false</code> if it fails.
	 */
	public boolean setWiFi(boolean state)
	{
		try
		{
			wrappedClientDevice.setWiFi(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device WiFi state failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Setting WiFi state was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Sends SMS to this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param smsMessage
	 *        - {@link SmsMessage}, that will be sent to the device.
	 * @return <code>true</code> if the SMS receiving is successful, <code>false</code> if it fails.
	 */
	public boolean receiveSms(SmsMessage smsMessage)
	{
		try
		{
			wrappedClientDevice.receiveSms(smsMessage, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending SMS to the testing device failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Receiving SMS was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * This device receives a call.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param phoneNumber
	 *        - {@link PhoneNumber}, that will be sent to the device.
	 * @return <code>true</code> if the call receiving is successful, <code>false</code> if it fails.
	 */
	public boolean receiveCall(PhoneNumber phoneNumber)
	{
		try
		{
			wrappedClientDevice.receiveCall(phoneNumber, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending call to the testing device failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Receiving call was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Accepts a call to this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param phoneNumber
	 *        - {@link PhoneNumber}, that calls the device.
	 * @return <code>true</code> if the accepting call is successful, <code>false</code> if it fails.
	 */
	public boolean acceptCall(PhoneNumber phoneNumber)
	{
		try
		{
			wrappedClientDevice.acceptCall(phoneNumber, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Accepting call to the testing device failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Accepting call was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Accepts call to this device.<br>
	 * 
	 * @return <code>true</code> if the accepting call is successful, <code>false</code> if it fails.
	 */
	public boolean acceptCall()
	{
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
	public boolean holdCall(PhoneNumber phoneNumber)
	{
		try
		{
			wrappedClientDevice.holdCall(phoneNumber, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Holding call to the testing device failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Holding call was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Cancels a call to this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param phoneNumber
	 *        - {@link PhoneNumber}, that calls the device.
	 * @return <code>true</code> if the canceling call is successful, <code>false</code> if it fails.
	 */
	public boolean cancelCall(PhoneNumber phoneNumber)
	{
		try
		{
			wrappedClientDevice.cancelCall(phoneNumber, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
			return false;
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Canceling call to the testing device failed.", e);
			return false;
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Canceling call was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return true;
	}

	/**
	 * Declines a call to this device.<br>
	 * 
	 * @return <code>true</code> if the denying call is successful, <code>false</code> if it fails.
	 */
	public boolean declineCall()
	{
		return pressButton(HardwareButton.DECLINE);
	}

	/**
	 * Obtains information about the telephony services on the device.
	 * 
	 * @return {@link TelephonyInformation} instance.
	 */
	public TelephonyInformation getTelephonyInformation()
	{
		TelephonyInformation telephonyInformation = null;

		try
		{
			telephonyInformation = wrappedClientDevice.getTelephonyInformation(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting telephony information from testing device failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			LOGGER.error("Getting telephony information was rejected by the server side.", e);
			throw new DeviceInvocationRejectedException(e);
		}

		return telephonyInformation;
	}

	/**
	 * Attempts to reconnect to the ATMOSPHERE server.
	 * 
	 * @throws ServerConnectionFailedException
	 * @throws DeviceReleasedException
	 */
	private void handleLostConnection()
	{
		try
		{
			serverConnectionHandler.connect();
		}
		catch (ServerConnectionFailedException e)
		{
			throw e;
		}

		String message = "Reconnecting to server succeeded, but the device was already released.";

		LOGGER.fatal(message);
		throw new DeviceReleasedException(message);
	}
}
