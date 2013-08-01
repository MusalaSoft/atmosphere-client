package com.musala.atmosphere.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.DeviceOrientation;
import com.musala.atmosphere.client.device.TouchGesture;
import com.musala.atmosphere.client.exceptions.ActivityStartingException;
import com.musala.atmosphere.client.exceptions.ApkInstallationFailedException;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

/**
 * Contains the methods a user can call directly in his test.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class Device
{
	// TODO extract constant to config file
	private static final int MAX_BUFFER_SIZE = 8092; // 8Kb

	private static final Logger LOGGER = Logger.getLogger(Device.class.getCanonicalName());

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
	 * Checkouts resolution height of the testing device.
	 * 
	 * @return - height of the screen resolution
	 */
	public int getResolutionHeight()
	{
		// TODO implement device.getResolutionHeight
		return 0;
	}

	/**
	 * Sets screen resolution's height.
	 * 
	 * @param height
	 */
	public void setResolutionHeight(int height)
	{
		// TODO implement device.setResolutionheight
	}

	/**
	 * Gets testing device's resolution width.
	 * 
	 * @return - width of testing device's resolution
	 */
	public int getResolutionWidth()
	{
		// TODO implement device.getResolutionWidth
		return 0;
	}

	/**
	 * Sets testing device's resolution width to <b> width </b>
	 * 
	 * @param width
	 *        - the width to set
	 */
	public void setResolutionWidth(int width)
	{
		// TODO implement device.setResolutionWidth
	}

	/**
	 * Gets DPI of the testing device's screen.
	 * 
	 * @return DPI of the tester's device
	 */
	public int getDpi()
	{
		// TODO implement device.getDpi
		return 0;
	}

	/**
	 * Sets dpi property on testing device.
	 * 
	 * @param dpi
	 *        - dpi to be set
	 */
	public void setDpi(int dpi)
	{
		// TODO implement device.set Dpi
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
	 * Sets new orientation of the testing device.
	 * 
	 * @param deviceOrientation
	 *        - new device orientation to be set
	 */
	public void setOrientation(DeviceOrientation deviceOrientation)
	{
		// TODO implement device.setOrientation
	}

	/**
	 * Returns battery level
	 * 
	 * @return Battery level of the given device in %
	 * @throws RemoteException
	 * @throws CommandFailedException
	 */
	public int getBatteryLevel() throws RemoteException, CommandFailedException
	{
		return wrappedClientDevice.getBatteryLevel();
	}

	/**
	 * Sets battery level
	 * 
	 * @param batteryLevel
	 *        - level of battery in percent
	 * @throws RemoteException
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
			e.printStackTrace();
			LOGGER.error("Device set battery level command failed.", e);
		}

	}

	/**
	 * Get current battery state of the testing device
	 * 
	 * @return - "unknown", "charging", "discharging", "not charging" or "full"
	 * @throws RemoteException
	 */
	public BatteryState getBatteryState() throws RemoteException
	{
		// TODO implement device.getBatteryState
		return null;
	}

	/**
	 * Sets battery state of testing device.
	 * 
	 * @param batteryState
	 *        - element of type {@link DeviceBatteryState}
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
	public void setAirplaneMode(boolean airplaneMode)
			throws CommandFailedException,
			RemoteException
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
		// TODO implement device.getScreenShot
		return null;
	}

	/**
	 * Gets the active {@link Screen Screen} of the testing device.
	 * 
	 * @return
	 * @throws CommandFailedException
	 * @throws RemoteException
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

		File apkFile = new File(path);

		// Transfer the apk from Client to his device
		byte[] buffer = new byte[MAX_BUFFER_SIZE];

		try
		{
			LOGGER.info("Transferring apk...");
			FileInputStream fileReaderFromApk = new FileInputStream(apkFile);
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
}
