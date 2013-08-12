package com.musala.atmosphere.client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.device.TouchGesture;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.ApkInstallationFailedException;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

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

	private static final String AWAKE_EXTRACTION_REGEX = ".*mWakefulness=(\\w+).*";

	private static final String LOCKED_CHECK_STRING = "mLockScreenShown true";

	private static final String AWAKE_IDENTIFIER = "awake";

	private IClientDevice wrappedClientDevice;

	/**
	 * Constructor that converts given IClientDevice to fully functioning and usable Device object.
	 * 
	 * @param iClientDevice
	 *        -
	 */
	Device(IClientDevice iClientDevice)
	{
		wrappedClientDevice = iClientDevice;
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
			wrappedDeviceInformation = wrappedClientDevice.getDeviceInformation();
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		DeviceInformation deviceInformation = new DeviceInformation(wrappedDeviceInformation);
		return deviceInformation;
	}

	void release()
	{
		wrappedClientDevice = new ReleasedClientDevice();
	}

	/**
	 * Gets current orientation of the testing device
	 * 
	 * @return - orientation of the device
	 */
	public DeviceOrientation getOrientation()
	{
		// TODO implement device.getOrientation
		return null;
	}

	/**
	 * Sets new orientation of the testing device. Can only be applied on emulators.
	 * 
	 * @param deviceOrientation
	 *        - new device orientation to be set
	 * @throws RemoteException
	 * @throws CommandFailedException
	 */
	public void setOrientation(DeviceOrientation deviceOrientation) throws RemoteException, CommandFailedException
	{
		wrappedClientDevice.setOrientation(deviceOrientation);
	}

	/**
	 * Returns battery level
	 * 
	 * @return Battery level of the given device in %
	 * @throws RemoteException
	 * @throws CommandFailedException
	 */
	public int getBatteryLevel()
	{
		int result = 0;
		try
		{
			result = wrappedClientDevice.getBatteryLevel();
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Failed to get battery level.", e);
		}
		return result;
	}

	/**
	 * Sets battery level.
	 * 
	 * @param batteryLevel
	 *        - level of battery in percent
	 */
	public void setBatteryLevel(int batteryLevel)
	{
		try
		{
			wrappedClientDevice.setBatteryLevel(batteryLevel);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Setting battery level failed.", e);
		}

	}

	/**
	 * Get current battery state of the testing device
	 * 
	 * @return - "unknown", "charging", "discharging", "not charging" or "full"
	 * @return - a {@link BatteryState BatteryState} enumeration member.
	 * @throws RemoteException
	 */
	public BatteryState getBatteryState()
	{
		BatteryState state = null;
		try
		{
			state = wrappedClientDevice.getBatteryState();
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
		return state;
	}

	/**
	 * Sets battery state of testing device.
	 * 
	 * @param batteryState
	 *        - element of type {@link BatteryState}
	 * @throws RemoteException
	 */
	public void setBatteryState(BatteryState batteryState) throws RemoteException
	{
		try
		{
			wrappedClientDevice.setBatteryState(batteryState);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			e.printStackTrace();
			LOGGER.error("Device set battery level command failed.", e);
		}
	}

	/**
	 * Gets current power state of the testing device
	 * 
	 * @return - true - connected or false - disconnected
	 * @throws CommandFailedException
	 * @throws RemoteException
	 */
	public boolean getPowerState() throws RemoteException, CommandFailedException
	{
		boolean state = wrappedClientDevice.getPowerState();
		return state;
	}

	/**
	 * Sets the power state of the testing device.
	 * 
	 * @param state
	 *        True for connected or false for disconnected.
	 * @throws CommandFailedException
	 * @throws RemoteException
	 */
	public void setPowerState(boolean state) throws CommandFailedException, RemoteException
	{
		wrappedClientDevice.setPowerState(state);
	}

	/**
	 * Gets network speeds of testing device
	 * 
	 * @return - triple of ints meaning < uploadSpeed, downloadSpeed, latency >
	 * @throws RemoteException
	 */
	// Triple<Integer> getNetworkSpeed() throws RemoteException
	// {
	// // TODO implement device.getNetworkSpeed
	// return null;
	// }

	/**
	 * Sets network speed
	 * 
	 * @param upload
	 * @param download
	 * @param latency
	 * @throws RemoteException
	 */
	public void setNetworkSpeed(int upload, int download, int latency) throws RemoteException
	{
		// TODO implement device.setNetworkSpeed
	}

	/**
	 * Sets the airplane mode of the testing device.
	 * 
	 * @param airplaneMode
	 *        True if in airplane mode, false if not.
	 * @throws CommandFailedException
	 * @throws RemoteException
	 */
	public void setAirplaneMode(boolean airplaneMode) throws CommandFailedException, RemoteException
	{
		wrappedClientDevice.setAirplaneMode(airplaneMode);
	}

	/**
	 * Allows user to get a device screenshot.
	 * 
	 * @return
	 */
	public byte[] getScreenshot()
	{
		byte[] screenshot = null;
		try
		{
			screenshot = wrappedClientDevice.getScreenshot();
		}
		catch (RemoteException e)
		{
			// TODO add client logic on failed connection
			LOGGER.error("Error in connection between client and device.", e);
		}
		catch (CommandFailedException e)
		{
			// TODO add logic on failed screenshot fetching
			LOGGER.error("Fetching screenshot had failed for some reason.", e);
		}
		return screenshot;
	}

	/**
	 * Allows user to get a device screenshot and save it as an image file.
	 * 
	 * @return
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
			// TODO add logic on failed screenshot dumping
			LOGGER.error("Error while writing to file.", e);
		}
	}

	/**
	 * Gets the active {@link Screen Screen} of the testing device.
	 * 
	 * @return
	 */
	public Screen getActiveScreen()
	{
		// TODO RemoteException and CommandFailedException should be changed to something else
		String uiHierarchy = "";
		try
		{
			uiHierarchy = wrappedClientDevice.getUiXml();
		}
		catch (RemoteException e)
		{
			// TODO add client logic on failed connection
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Getting active device screen failed.", e);
			// TODO throw something here
		}
		Screen activeScreen = new Screen(this, uiHierarchy);
		return activeScreen;
	}

	/**
	 * Installs the application to be tested on the device.
	 * 
	 * @param path
	 *        - location of the installation file on the client machine.
	 */
	public void installAPK(String path)
	{
		LOGGER.info("Preparing for apk installation...");

		try
		{
			wrappedClientDevice.initApkInstall();
		}
		catch (IOException e)
		{
			LOGGER.fatal("Apk instalation failed: could not create temporary apk file on the Device.", e);
			throw new ApkInstallationFailedException("Internal error occured: could not install apk.", e);
		}

		// Transfer the apk from Client to his device
		byte[] buffer = new byte[MAX_BUFFER_SIZE];

		try
		{
			FileInputStream fileReaderFromApk = new FileInputStream(path);
			LOGGER.info("Transferring apk...");
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
				wrappedClientDevice.appendToApk(buffer);
				numberOfCharactersLeft = fileReaderFromApk.available();
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.fatal("Could not locate APK file. Make sure the path is correct and the file exists.", e);
			throw new ApkInstallationFailedException("Missing instalation file for the tested application.", e);
		}
		catch (IOException e)
		{
			LOGGER.fatal("Error while reading from APK file.", e);
			throw new ApkInstallationFailedException("Could not install APK file. Error while reading from file.", e);
		}

		// Install
		try
		{
			LOGGER.info("Installing apk...");
			wrappedClientDevice.buildAndInstallApk();
		}
		catch (IOException e)
		{
			LOGGER.fatal("Error while saving the apk file on the wrapped device", e);
			throw new ApkInstallationFailedException(	"Internal error occured: Could not install application on device.",
														e);
		}
		catch (CommandFailedException e)
		{
			LOGGER.fatal("Error while executing command for installing application.", e);
			throw new ApkInstallationFailedException(	"Internal error occured: Could not install application on device.",
														e);
		}

		LOGGER.info("Apk instalation successfull.");
	}

	/*
	 * public State dumpCurrentState() { return IClientDevice.dumpCurrentState(); }
	 * 
	 * public void restoreState(State state) { IClientDevice.restoreState(state); }
	 */

	/**
	 * Redirects specific adress to another adress.
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
	public void tapScreenLocation(int positionX, int positionY)
	{
		String query = "input tap " + positionX + " " + positionY;
		try
		{
			wrappedClientDevice.executeShellCommand(query);
		}
		catch (RemoteException e)
		{
			// TODO add client connection failed logic
			e.printStackTrace();
		}
		catch (CommandFailedException e)
		{
			LOGGER.error("Device screen tap command failed.", e);
		}
	}

	/**
	 * Starts an Activity from a package.
	 * 
	 * @param packageName
	 *        package name from which an activity should be started.
	 * @param activityName
	 *        activity name to be started
	 */
	public void startActivity(String packageName, String activityName) throws ActivityStartingException
	{
		String query = "am start -n " + packageName + "/." + activityName;
		String response = null;
		try
		{
			response = wrappedClientDevice.executeShellCommand(query);
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
	 * Checks if the device is in a WAKE state.
	 * 
	 * @return true if the device is awake, false otherwise.
	 */
	public boolean isAwake()
	{
		String dump = "";
		try
		{
			dump = wrappedClientDevice.executeShellCommand(AWAKE_STATUS_DUMP_COMMAND);
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

		Pattern pattern = Pattern.compile(AWAKE_EXTRACTION_REGEX, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(dump);
		matcher.find();

		boolean awake = matcher.group(1).equalsIgnoreCase(AWAKE_IDENTIFIER);
		return awake;
	}

	/**
	 * Checks if the device is locked.
	 * 
	 * @return true if the device is locked, false otherwise.
	 */
	public boolean isLocked()
	{
		String dump = "";
		try
		{
			dump = wrappedClientDevice.executeShellCommand(LOCKED_STATUS_DUMP_COMMAND);
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
		boolean locked = dump.contains(LOCKED_CHECK_STRING);
		return locked;
	}

	/**
	 * Presses a device hardware button.
	 * 
	 * @param keyCode
	 *        - button key code as specified by the Android KeyEvent KEYCODE_ constants.
	 */
	public void pressButton(int keyCode)
	{
		String query = "input keyevent " + Integer.toString(keyCode);
		try
		{
			wrappedClientDevice.executeShellCommand(query);
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
	 * Inputs text on the testing device through the AtmosphereIME.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param interval
	 *        - interval in milliseconds between letters.
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
			int numericalCharValue = (int) textCharArray[i];
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
			wrappedClientDevice.executeShellCommand(builtCommand);
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
	}

	/**
	 * Inputs text on the testing device through the AtmosphereIME.
	 * 
	 * @param text
	 *        - text to be input.
	 */
	public void inputText(String text)
	{
		inputText(text, 0);
	}
}
