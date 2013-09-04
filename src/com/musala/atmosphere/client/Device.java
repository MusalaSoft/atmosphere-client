package com.musala.atmosphere.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.device.TouchGesture;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.ApkInstallationFailedException;
import com.musala.atmosphere.client.exceptions.DeviceInvocationRejectedException;
import com.musala.atmosphere.client.exceptions.MacroPlayingException;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.ConnectionType;
import com.musala.atmosphere.commons.DeviceAcceleration;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.MobileDataState;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.standalone.Macro;

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

	private static final String AWAKE_CHECK_STRING_4_2_2 = " mWakefulness=Awake";

	private static final String AWAKE_CHECK_EXPRESSION_4_1_2 = "mPowerState=[1-9]\\d*";

	private static final String LOCKED_CHECK_STRING = "mLockScreenShown true";

	private IClientDevice wrappedClientDevice;

	private final long invocationPasskey;

	/**
	 * Constructor that creates a usable Device object by a given IClientDevice and it's invocation passkey.
	 * 
	 * @param iClientDevice
	 * @param devicePasskey
	 *        -
	 */
	Device(IClientDevice iClientDevice, long devicePasskey)
	{
		wrappedClientDevice = iClientDevice;
		invocationPasskey = devicePasskey;
	}

	void release()
	{
		wrappedClientDevice = new ReleasedClientDevice();
	}

	/**
	 * Gets the device information about the testing device.
	 * 
	 * @return a {@link DeviceInformation DeviceInformation} structure with information for the testing device.
	 */
	public DeviceInformation getInformation()
	{
		com.musala.atmosphere.commons.DeviceInformation wrappedDeviceInformation = null;
		try
		{
			wrappedDeviceInformation = wrappedClientDevice.getDeviceInformation(invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
		DeviceInformation deviceInformation = new DeviceInformation(wrappedDeviceInformation);
		return deviceInformation;
	}

	/**
	 * Gets current orientation of the testing device
	 * 
	 * @return - orientation of the device
	 */
	public DeviceOrientation getDeviceOrientation()
	{
		DeviceOrientation deviceOrientation = null;
		try
		{
			deviceOrientation = wrappedClientDevice.getDeviceOrientation(invocationPasskey);

		}
		catch (InvalidPasskeyException e)
		{
			// TODO Add client connection failed logic.
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device orientation failed.", e);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		return deviceOrientation;
	}

	/**
	 * Sets new orientation of the testing device. Can only be applied on emulators.
	 * 
	 * @param deviceOrientation
	 *        - new device orientation to be set
	 */
	public void setDeviceOrientation(DeviceOrientation deviceOrientation)
	{
		try
		{
			wrappedClientDevice.setDeviceOrientation(deviceOrientation, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Control whether the accelerometer will be used to change screen orientation
	 * 
	 * @param autoRotation
	 *        - if false, it will not be used unless explicitly requested by the application; if true, it will be used
	 *        by default unless explicitly disabled by the application.
	 */
	public void setAutoRotation(boolean autoRotation)
	{
		try
		{
			wrappedClientDevice.setAutoRotation(autoRotation, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Sets new screen orientation for the device.
	 * 
	 * @param screenOrientation
	 *        - new screen orientation to be set
	 */
	public void setScreenOrientation(ScreenOrientation screenOrientation)
	{
		try
		{
			wrappedClientDevice.setScreenOrientation(screenOrientation, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Sets new acceleration for the testing device. Can only be applied on emulators.
	 * 
	 * @param deviceAcceleration
	 *        - new device acceleration to be set
	 */
	public void setAcceleration(DeviceAcceleration deviceAcceleration)
	{
		try
		{
			wrappedClientDevice.setAcceleration(deviceAcceleration, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets current acceleration of the testing device
	 * 
	 * @return - acceleration of the device
	 */
	public DeviceAcceleration getDeviceAcceleration()
	{
		DeviceAcceleration deviceAcceleration = null;
		try
		{
			deviceAcceleration = wrappedClientDevice.getDeviceAcceleration(invocationPasskey);

		}
		catch (InvalidPasskeyException e)
		{
			// TODO Add client connection failed logic.
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting device acceleration failed.", e);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		return deviceAcceleration;
	}

	/**
	 * Returns the current device's battery level.
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
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Sets current device's battery level.
	 * 
	 * @param batteryLevel
	 *        - new battery level in percent.
	 */
	public void setBatteryLevel(int batteryLevel)
	{
		try
		{
			wrappedClientDevice.setBatteryLevel(batteryLevel, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets the current device's battery state.
	 * 
	 * @return - a {@link BatteryState BatteryState} enumeration member.
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
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Sets the current device's battery state.
	 * 
	 * @param batteryState
	 *        - {@link BatteryState} enumeration member.
	 */
	public void setBatteryState(BatteryState batteryState)
	{
		try
		{
			wrappedClientDevice.setBatteryState(batteryState, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets the current device's power connectivity.
	 * 
	 * @return true if the device connected or false - disconnected
	 */
	public boolean getPowerState()
	{
		boolean state = false;
		try
		{
			wrappedClientDevice.getPowerState(invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Sets the current device's power connectivity state.
	 * 
	 * @param state
	 *        True for AC connected, false for disconnected.
	 */
	public void setPowerState(boolean state)
	{
		try
		{
			wrappedClientDevice.setPowerState(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Sets the current device's airplane mode.
	 * 
	 * @param airplaneMode
	 *        - True if the device should now be in airplane mode, false if not.
	 */
	public void setAirplaneMode(boolean airplaneMode)
	{
		try
		{
			wrappedClientDevice.setAirplaneMode(airplaneMode, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets the current device's screenshot.
	 * 
	 * @return a byte buffer that, when dumped to a file, can be opened as a .PNG image file.
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
			// TODO add client logic on failed connection
			e.printStackTrace();
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
	 * Gets the current device's screenshot and saves it as an image file at a specified location.
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
	 * Gets the current device's active {@link Screen Screen}.
	 * 
	 * @return a {@link Screen Screen} instance.
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
			// TODO add client logic on failed connection
			e.printStackTrace();
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
	 * Installs a specified application on the current device.
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
				LOGGER.fatal("File instalation failed: could not create temporary apk file on the remote Agent.", e);
				throw new ApkInstallationFailedException(	"File instalation failed: could not create temporary apk file on the remote Agent.",
															e);
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
				LOGGER.fatal(	"Could not locate installation file. Make sure the path is correct and the file exists.",
								e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException(	"Could not locate installation file. Make sure the path is correct and the file exists.",
															e);
			}
			catch (IOException e)
			{
				LOGGER.fatal("Reading from local file/Writing to remote file resulted in exception.", e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException(	"Reading from local file/Writing to remote file resulted in exception.",
															e);
			}

			// Install
			try
			{
				LOGGER.info("Installing transferred file...");
				wrappedClientDevice.buildAndInstallApk(invocationPasskey);
			}
			catch (IOException e)
			{
				LOGGER.fatal("Error while saving the apk file on the remote Agent.", e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException("Error while saving the apk file on the remote Agent.", e);
			}
			catch (CommandFailedException e)
			{
				LOGGER.fatal("Executing file installation command failed.", e);
				wrappedClientDevice.discardApk(invocationPasskey);
				throw new ApkInstallationFailedException("Executing file installation command failed.", e);
			}

			LOGGER.info("File instalation successfull.");
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Redirects specific address to another address.
	 * 
	 * @param toIp
	 * @param toNewIp
	 */
	public void redirectConnection(String toIp, String toNewIp)
	{
		// TODO implement device.redirectConnection
	}

	/**
	 * Simulates random finger actions on the screen of the testing device.
	 * 
	 */
	public void randomMultiTouchevent()
	{
		// TODO implement device.randomMultiTouchEvent()
	}

	/**
	 * Executing user-defined gesture on the screen.
	 * 
	 * @param gesture
	 */
	public void customGesture(TouchGesture gesture)
	{
		// TODO implement device.customGesture(gesture);
	}

	/**
	 * Simulates a simple tap on the screen at a specified location.
	 * 
	 * @param positionX
	 *        screen tap X coordinate.
	 * @param positionY
	 *        screen tap Y coordinate.
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
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Starts an Activity from a package.
	 * 
	 * @param packageName
	 *        - package name from which an activity should be started.
	 * @param activityName
	 *        - activity name to be started.
	 * 
	 * @throws ActivityStartingException
	 */
	public void startActivity(String packageName, String activityName) throws ActivityStartingException
	{
		startActivity(packageName, activityName, true);
	}

	/**
	 * Starts an Activity from a package.
	 * 
	 * @param packageName
	 *        - package name from which an activity should be started.
	 * @param activityName
	 *        - activity name to be started.
	 * @param unlockDevice
	 *        - if true unlocks the device before starting the activity.
	 * @throws ActivityStartingException
	 */
	public void startActivity(String packageName, String activityName, boolean unlockDevice)
		throws ActivityStartingException
	{
		if (unlockDevice)
		{
			unlock();
		}
		String query = "am start -n " + packageName + "/." + activityName;
		String response = null;
		try
		{
			response = wrappedClientDevice.executeShellCommand(query, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
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
	 * Locks the current device.
	 */
	public void lock()
	{
		if (!isLocked())
		{
			pressButton(HardwareButton.POWER);
		}
	}

	/**
	 * Unlocks the current device.
	 */
	public void unlock()
	{
		if (!isAwake())
		{
			pressButton(HardwareButton.POWER);
		}
		if (isLocked())
		{
			pressButton(HardwareButton.MENU);
		}
	}

	/**
	 * Checks if the current device is in a WAKE state.
	 * 
	 * @return true if the device is awake, false otherwise.
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
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Fetching device wake state failed.", e);
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}

		Pattern awakeAPI16Pattern = Pattern.compile(AWAKE_CHECK_EXPRESSION_4_1_2);
		Matcher awakeAPI16Matcher = awakeAPI16Pattern.matcher(dump);
		boolean awakeAPI16 = awakeAPI16Matcher.find();

		boolean awakeAPI17 = dump.contains(AWAKE_CHECK_STRING_4_2_2);

		return awakeAPI16 || awakeAPI17;
	}

	/**
	 * Checks if the current device is locked.
	 * 
	 * @return true if the device is locked, false otherwise.
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
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
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
	 * Presses a device hardware button on the current device.
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
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
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
	 * Presses a device hardware button.
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
	 * Inputs text on the current device through the AtmosphereIME.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param interval
	 *        - time interval in milliseconds between letters typing.
	 */
	public void inputText(String text, int intervalInMs)
	{
		if (text.isEmpty())
		{
			return;
		}
		StringBuilder intentBuilder = new StringBuilder();
		String command = "am broadcast -a atmosphere.intent.action.TEXT --eia text ";
		char[] textCharArray = text.toCharArray();

		intentBuilder.append(command);
		intentBuilder.append((int) textCharArray[0]);
		for (int i = 1; i < textCharArray.length; i++)
		{
			intentBuilder.append(",");
			int numericalCharValue = textCharArray[i];
			intentBuilder.append(numericalCharValue);
		}

		if (intervalInMs > 0)
		{
			intentBuilder.append(" --ei interval ");
			intentBuilder.append(intervalInMs);
		}

		try
		{
			String builtCommand = intentBuilder.toString();
			wrappedClientDevice.executeShellCommand(builtCommand, invocationPasskey);
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Sending text input failed.", e);
		}
		catch (RemoteException e)
		{
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
		}
		catch (InvalidPasskeyException e)
		{
			throw new DeviceInvocationRejectedException(e);
		}
	}

	/**
	 * Inputs text on the current device through the AtmosphereIME.
	 * 
	 * @param text
	 *        - text to be input.
	 */
	public void inputText(String text)
	{
		inputText(text, 0);
	}

	/**
	 * Plays a recorded macro file on the current device.
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
			// TODO implement logic behind failed client-server connection
			e.printStackTrace();
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
	 * Sets the mobile data state of an emulator.
	 * 
	 * @param state
	 *        - member of the {@link MobileDataState} enumeration.
	 */
	public void setMobileDataState(MobileDataState state)
	{
		try
		{
			wrappedClientDevice.setMobileDataState(state, invocationPasskey);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets the connection type of the device - MOBILE, WIFI, or NONE - if not connected to WiFi or Mobile network.
	 * 
	 * @return a member of the {@link ConnectionType} enum.
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
			// TODO add client connection failed logic
			e.printStackTrace();
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
	 * Gets the mobile data state of an emulator.
	 * 
	 * @return a member of the {@link MobileDataState} enum.
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
			// TODO add client connection failed logic
			e.printStackTrace();
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
}
