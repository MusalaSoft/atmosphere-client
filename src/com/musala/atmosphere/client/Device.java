package com.musala.atmosphere.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.DeviceOrientation;
import com.musala.atmosphere.client.device.Screen;
import com.musala.atmosphere.client.device.TouchGesture;
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
		// TODO implement device.getResolutionheight
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
	public void setBatteryLevel(int batteryLevel) throws RemoteException
	{
		wrappedClientDevice.setBatteryLevel(batteryLevel);
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
		// TODO set battery state here
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
	 * Allows user to get screenshot whenever he wants
	 * 
	 * @return
	 */
	public byte[] getScreenShot()
	{
		// TODO implement device.getScreenShot
		return null;
	}

	/**
	 * Gets the active screen of the testing device
	 * 
	 * @return
	 */
	public Screen getActiveScreen()
	{
		// TODO implement device.getScreen() method(s)
		return null;
	}

	/**
	 * Installs the application to be tested on the device.
	 * 
	 * @param path
	 *        - location of the installation file on the client machine.
	 * @throws IOException
	 */
	public void installAPK(String path) throws IOException
	{
		LOGGER.info("Preparing for apk installation...");

		try
		{
			wrappedClientDevice.initApkInstall();
		}
		catch (IOException e)
		{
			LOGGER.fatal("Apk instalation failed: could not create temporary apk file on the Device.");
			throw new ApkInstallationFailedException("Internal error occured: could not install apk.");
		}

		File apkFile = new File(path);

		// Transfer the apk from Client to his device
		byte[] buffer = new byte[MAX_BUFFER_SIZE];

		try
		{
			LOGGER.info("Transferring apk...");
			FileInputStream fileReaderFromApk = new FileInputStream(apkFile);
			// number of characters that we had read with the last iteration of our "while" cycle
			int numberOfCharactersRead = -1;
			// number of characters until the end of file
			int numberOfCharactersLeft = fileReaderFromApk.available();

			while (numberOfCharactersLeft > 0)
			{
				if (numberOfCharactersLeft >= MAX_BUFFER_SIZE)
				{
					numberOfCharactersRead = fileReaderFromApk.read(buffer, 0, MAX_BUFFER_SIZE);
				}
				else
				{
					buffer = new byte[numberOfCharactersLeft];
					numberOfCharactersRead = fileReaderFromApk.read(buffer, 0, numberOfCharactersLeft);
				}
				wrappedClientDevice.appendToApk(buffer);
				numberOfCharactersLeft = fileReaderFromApk.available();
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.fatal("Could not locate APK file. Make sure the path is correct and the file exists.", e);
			throw new FileNotFoundException("Missing instalation file for the tested application.");
		}
		catch (IOException e)
		{
			LOGGER.fatal("Error while reading from APK file.", e);
			throw new IOException("Could not install APK file. Error while reading from file.");
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

}
