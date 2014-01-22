package com.musala.atmosphere.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import com.musala.atmosphere.client.device.TouchGesture;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.ApkInstallationFailedException;
import com.musala.atmosphere.client.exceptions.DeviceInvocationRejectedException;
import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.client.exceptions.MacroPlayingException;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.util.settings.AndroidGlobalSettings;
import com.musala.atmosphere.client.util.settings.AndroidSystemSettings;
import com.musala.atmosphere.client.util.settings.DeviceSettingsManager;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.ConnectionType;
import com.musala.atmosphere.commons.DeviceAcceleration;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.MobileDataState;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.standalone.Macro;
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
	 * @return {@link DeviceInformation DeviceInformation} structure with information for the testing device.
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
			throw new DeviceInvocationRejectedException(e);
		}
		return wrappedDeviceInformation;
	}

	/**
	 * Gets current orientation in space of this device.
	 * 
	 * @return {@link DeviceOrientation DeviceOrientation} of the testing device.
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
	 * @deprecated
	 */
	public void setDeviceOrientation(DeviceOrientation deviceOrientation)
	{
		try
		{
			wrappedClientDevice.setDeviceOrientation(deviceOrientation, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device orientation failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Changes the screen auto rotation of this device.<br>
	 * Control whether the accelerometer will be used to change screen orientation.
	 * 
	 * @param autoRotation
	 *        - <code>false</code> - disables screen auto rotation; <code>true</code> - enables screen auto rotation.
	 */
	public void setAutoRotation(boolean autoRotation)
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
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device auto rotation failed.", e);
		}
	}

	/**
	 * Sets new screen orientation for this device.<br>
	 * Implicitly turns off screen auto rotation.
	 * 
	 * @param screenOrientation
	 *        - new {@link ScreenOrientation ScreenOrientation} to be set.
	 */
	public void setScreenOrientation(ScreenOrientation screenOrientation)
	{
		try
		{
			setAutoRotation(false);
			deviceSettings.putInt(AndroidSystemSettings.USER_ROTATION, screenOrientation.getOrientationNumber());
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting screen orientation failed.", e);
		}
	}

	/**
	 * Sets new acceleration for this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param deviceAcceleration
	 *        - new {@link DeviceAcceleration DeviceAcceleration} to be set.
	 */
	public void setAcceleration(DeviceAcceleration deviceAcceleration)
	{
		try
		{
			wrappedClientDevice.setAcceleration(deviceAcceleration, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device acceleration failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Gets current acceleration of this device.
	 * 
	 * @return {@link DeviceAcceleration DeviceAcceleration} of the device.
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
			handleLostConnection();
		}
		return deviceAcceleration;
	}

	/**
	 * Gets the current battery charge level of this device.
	 * 
	 * @return Battery level of the device in percents.
	 */
	public int getBatteryLevel()
	{
		int result = 0;
		try
		{
			result = wrappedClientDevice.getBatteryLevel(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device battery level failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		return result;
	}

	/**
	 * Sets the battery charge level of this device.<br>
	 * <i>On real device, sets the battery level only for limited time, until Android BatteryManager updates the battery
	 * information.</i>
	 * 
	 * @param batteryLevel
	 *        - new battery level in percent to be set.
	 */
	public void setBatteryLevel(int batteryLevel)
	{
		try
		{
			wrappedClientDevice.setBatteryLevel(batteryLevel, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device battery level failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Gets the current battery state of this device.
	 * 
	 * @return {@link BatteryState BatteryState} of the device.
	 */
	public BatteryState getBatteryState()
	{
		BatteryState state = null;
		try
		{
			state = wrappedClientDevice.getBatteryState(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device battery state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		return state;
	}

	/**
	 * Sets the battery state of this device.<br>
	 * <i>On real device, sets the battery state only for limited time, until Android BatteryManager updates the battery
	 * information.</i>
	 * 
	 * @param batteryState
	 *        - new {@link BatteryState} to be set.
	 */
	public void setBatteryState(BatteryState batteryState)
	{
		try
		{
			wrappedClientDevice.setBatteryState(batteryState, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device battery level failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Gets the current connection state of this device to external power source.
	 * 
	 * @return <code>true</code> if the device is connected to a power source.<br>
	 *         <code>false</code> if device is not connected to a power source.
	 */
	public boolean getPowerState()
	{
		boolean state = false;
		try
		{
			state = wrappedClientDevice.getPowerState(invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device power connectivity state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		return state;
	}

	/**
	 * Sets the connection state of this device to external power source.<br>
	 * <i>On real device, sets the power state only for limited time, until Android BatteryManager updates the power
	 * connection state information.</i>
	 * 
	 * @param state
	 *        - <code>true</code> for connected to AC power adapter, <code>false</code> for disconnected power state.
	 */
	public void setPowerState(boolean state)
	{
		try
		{
			wrappedClientDevice.setPowerState(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device power connectivity state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
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
	 */
	public void setAirplaneMode(boolean airplaneMode)
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
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device airplane mode failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Gets screenshot of this device's active screen.
	 * 
	 * @return byte buffer, containing captured device screen.<br>
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
			throw new DeviceInvocationRejectedException(e);
		}
		return screenshot;
	}

	/**
	 * Gets screenshot of this device's active screen and saves it as an image file at a specified location.
	 * 
	 * @param pathToImageFile
	 *        - location at which the screenshot image file should be saved.
	 */
	public void getScreenshot(String pathToImageFile)
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
		}
	}

	/**
	 * Gets the currently active {@link Screen Screen} of this device.
	 * 
	 * @return {@link Screen Screen} instance.
	 */
	public Screen getActiveScreen()
	{
		String uiHierarchy = "";
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
			throw new DeviceInvocationRejectedException(e);
		}
		Screen activeScreen = new Screen(this, uiHierarchy);
		return activeScreen;
	}

	/**
	 * Installs a specified Android application file on this device.<br>
	 * 
	 * @param path
	 *        - location of the file to be installed.
	 */
	public void installAPK(String path)
	{
		try
		{
			try
			{
				wrappedClientDevice.initApkInstall(invocationPasskey);
			}
			catch (IOException e)
			{
				if (!(e instanceof RemoteException))
				{
					LOGGER.fatal("File instalation failed: could not create temporary apk file on the remote Agent.", e);
					throw new ApkInstallationFailedException(	"File instalation failed: could not create temporary apk file on the remote Agent.",
																e);
				}

				throw e;
			}

			// Transfer the installation file from the current machine to the device
			byte[] buffer = new byte[MAX_BUFFER_SIZE];
			try
			{
				FileInputStream fileReaderFromApk = new FileInputStream(path);
				LOGGER.info("Transferring installation file...");
				// number of characters until the end of file
				int numberOfCharactersLeft = fileReaderFromApk.available();

				while (numberOfCharactersLeft > 0)
				{
					if (numberOfCharactersLeft >= MAX_BUFFER_SIZE)
					{
						fileReaderFromApk.read(buffer, 0, MAX_BUFFER_SIZE);
					}
					else
					{
						buffer = new byte[numberOfCharactersLeft];
						fileReaderFromApk.read(buffer, 0, numberOfCharactersLeft);
					}
					wrappedClientDevice.appendToApk(buffer, invocationPasskey);
					numberOfCharactersLeft = fileReaderFromApk.available();
				}
			}
			catch (FileNotFoundException e)
			{
				String message = "Could not locate installation file. Make sure the path is correct and the file exists.";

				LOGGER.fatal(message, e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException(message, e);
			}
			catch (IOException e)
			{
				String message = "Reading from local file/Writing to remote file resulted in exception.";

				if (!(e instanceof RemoteException))
				{
					LOGGER.fatal(message, e);
					wrappedClientDevice.discardApk(invocationPasskey);
					throw new ApkInstallationFailedException(message, e);
				}

				throw e;
			}

			// Install
			try
			{
				LOGGER.info("Installing transferred file...");
				wrappedClientDevice.buildAndInstallApk(invocationPasskey);
			}
			catch (IOException e)
			{
				String message = "Error while saving the apk file on the remote Agent.";

				if (!(e instanceof RemoteException))
				{
					LOGGER.fatal(message, e);
					wrappedClientDevice.discardApk(invocationPasskey);
					throw new ApkInstallationFailedException(message, e);
				}

				throw e;
			}
			catch (CommandFailedException e)
			{
				String message = "Executing file installation command failed.";

				LOGGER.fatal(message, e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException(message, e);
			}

			LOGGER.info("File instalation successfull.");
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (IOException e)
		{
			// Should never get here since IO exceptions are handled above.
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
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
	 */
	public void redirectConnection(String toIp, String toNewIp)
	{
		// TODO implement device.redirectConnection
	}

	/**
	 * Simulates random finger actions on the screen of this device.
	 * 
	 */
	public void randomMultiTouchevent()
	{
		// TODO implement device.randomMultiTouchEvent()
	}

	/**
	 * Executes user-defined gesture on this device's screen.
	 * 
	 * @param gesture
	 */
	public void customGesture(TouchGesture gesture)
	{
		// TODO implement device.customGesture(gesture);
	}

	/**
	 * Executes a simple tap on the screen of this device at a specified location point.
	 * 
	 * @param tapPoint
	 *        - {@link Point Point} on the screen to tap on.
	 */
	public void tapScreenLocation(Point tapPoint)
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
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Device screen tap failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Starts an Activity from a package on this device.
	 * 
	 * @param packageName
	 *        - package name from which an activity should be started.
	 * @param activityName
	 *        - activity name to be started. Expects either absolute name or a name starting with dot (.), relative to
	 *        the packageName.
	 * 
	 * @throws ActivityStartingException
	 */
	public void startActivity(String packageName, String activityName) throws ActivityStartingException
	{
		startActivity(packageName, activityName, true);
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
	 * @throws ActivityStartingException
	 */
	public void startActivity(String packageName, String activityName, boolean unlockDevice)
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
		}
		catch (CommandFailedException e)
		{
			throw new ActivityStartingException("The activity starting query was rejected.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}

		if (response.contains("Error: Activity class"))
		{
			throw new ActivityStartingException("The passed package or Activity was not found.");
		}
	}

	/**
	 * Changes the lock state of this device.
	 * 
	 * @param state
	 *        - desired lock state of the device; <code>true</code> - lock the device, <code>false</code> - unlock the
	 *        device.
	 */
	public void setLocked(boolean state)
	{
		boolean toLock = state && !isLocked();
		boolean toWakeForUnlock = !state && !isAwake();

		if (toLock || toWakeForUnlock)
		{
			pressButton(HardwareButton.POWER);
		}

		if (!state && isLocked())
		{
			pressButton(HardwareButton.MENU);
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
	 */
	public void pressButton(int keyCode)
	{
		String query = "input keyevent " + Integer.toString(keyCode);
		try
		{
			wrappedClientDevice.executeShellCommand(query, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending key input failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Presses hardware button on this device.
	 * 
	 * @param button
	 *        - {@link HardwareButton HardwareButton} to be pressed.
	 */
	public void pressButton(HardwareButton button)
	{
		int keycode = button.getKeycode();
		pressButton(keycode);
	}

	/**
	 * Inputs text on this device through the AtmosphereIME.<br>
	 * Element on device's screen, that accepts text input should be preselected.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param interval
	 *        - time interval in milliseconds between typing each symbol.
	 */
	public void inputText(String text, int interval)
	{
		if (text.isEmpty())
		{
			return;
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
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Inputs text on this device through the AtmosphereIME.<br>
	 * Element on device's screen, that accepts text input should be preselected.
	 * 
	 * @param text
	 *        - text to be input.
	 */
	public void inputText(String text)
	{
		inputText(text, 0);
	}

	/**
	 * Plays a recorded macro file on this device.
	 * 
	 * @param filePath
	 *        - path to the recorded macro file.
	 * @throws MacroPlayingException
	 */
	public void playMacro(String filePath) throws MacroPlayingException
	{
		try
		{
			FileInputStream macroInputStream = new FileInputStream(filePath);
			ObjectInputStream deserializationInput = new ObjectInputStream(macroInputStream);
			Macro macro = (Macro) deserializationInput.readObject();

			String macroDeviceModel = macro.getDeviceIdentifier().trim();
			String deviceModel = wrappedClientDevice.executeShellCommand("getprop ro.product.model", invocationPasskey);
			deviceModel = deviceModel.trim();

			if (!deviceModel.equals(macroDeviceModel))
			{
				throw new MacroPlayingException("Macro was recorded on a different device model. (macro device model = "
						+ macroDeviceModel + ", current device model = " + deviceModel + ")");
			}
			if (macro.getEventCount() == 0)
			{
				return;
			}

			List<String> events = macro.getParsedEvents();
			wrappedClientDevice.executeSequenceOfShellCommands(events, invocationPasskey);
		}
		catch (FileNotFoundException e)
		{
			throw new MacroPlayingException("Specified macro file could not be found.", e);
		}

		catch (ClassNotFoundException e)
		{
			throw new MacroPlayingException("Macro file deserialization failed. Make sure the specified file is a macro file.",
											e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Playing macro failed.", e);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		catch (IOException e)
		{
			throw new MacroPlayingException("Macro file deserialization failed.", e);
		}
	}

	/**
	 * Sets the mobile data state of this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param state
	 *        - {@link MobileDataState} to set.
	 */
	public void setMobileDataState(MobileDataState state)
	{
		try
		{
			wrappedClientDevice.setMobileDataState(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting mobile data state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Gets the current network connection type of this device.
	 * 
	 * @return {@link ConnectionType}.
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
			throw new DeviceInvocationRejectedException(e);
		}
		return type;
	}

	/**
	 * Gets the current mobile data state of this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @return {@link MobileDataState}.
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
			throw new DeviceInvocationRejectedException(e);
		}
		return state;
	}

	/**
	 * Sets the WiFi state of this device.
	 * 
	 * @param state
	 *        - <code>true</code> enables WiFi; <code>false</code> disables WiFi.
	 */
	public void setWiFi(boolean state)
	{
		try
		{
			wrappedClientDevice.setWiFi(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting device WiFi state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Sends SMS to this device.<br>
	 * Can only be applied on <b>emulators</b>.
	 * 
	 * @param smsMessage
	 *        - {@link SmsMessage}, that will be sent to the device.
	 */
	public void receiveSms(SmsMessage smsMessage)
	{
		try
		{
			wrappedClientDevice.receiveSms(smsMessage, invocationPasskey);
		}
		catch (RemoteException e)
		{
			handleLostConnection();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending SMS to the testing device failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Attempts to reconnect to the ATMOSPHERE server.
	 * 
	 * @throws ServerConnectionFailedException
	 * @throws DeeviceReleasedExceprion
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
