package com.musala.atmosphere.client;

import java.rmi.RemoteException;

import com.musala.atmosphere.client.device.DeviceOrientation;
import com.musala.atmosphere.client.device.Screen;
import com.musala.atmosphere.client.device.TouchGesture;
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
	 *        - location of the .apk
	 * @throws RemoteException
	 */
	public void installAPK(String path) throws RemoteException
	{
		// TODO implement device.installAPK
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
