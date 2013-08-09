package com.musala.atmosphere.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.Pair;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

/**
 * An instance of this class is used when a deivce allocated to a Client is released. The methods in this class throw
 * {@link DeviceReleasedException DeviceReleasedException} whenever some of them are called and thus notify the user
 * that he or she is trying to use a device that has been released (and can not be used anymore).
 * 
 * @author valyo.yolovski
 * 
 */
class ReleasedClientDevice implements IClientDevice
{
	@Override
	public long getFreeRam() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public String executeShellCommand(String shellCommand) throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public List<String> executeSequenceOfShellCommands(List<String> commands)
		throws RemoteException,
			CommandFailedException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void initApkInstall() throws RemoteException, IOException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void appendToApk(byte[] bytes) throws RemoteException, IOException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void buildAndInstallApk() throws RemoteException, IOException, CommandFailedException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void discardApk() throws RemoteException, IOException
	{
		throwDeviceReleasedException();

	}

	@Override
	public String getUiXml() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public byte[] getScreenshot() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setNetworkSpeed(Pair<Integer, Integer> speeds) throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();

	}

	@Override
	public Pair<Integer, Integer> getNetworkSpeed() throws RemoteException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setNetworkLatency(int latency) throws RemoteException
	{
		throwDeviceReleasedException();

	}

	@Override
	public int getNetworkLatency() throws RemoteException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public void setBatteryLevel(int level) throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();

	}

	@Override
	public int getBatteryLevel() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public void setBatteryState(BatteryState state) throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
	}

	@Override
	public BatteryState getBatteryState() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public boolean getPowerState() throws RemoteException, CommandFailedException
	{
		throwDeviceReleasedException();
		return false;
	}

	@Override
	public void setPowerState(boolean state) throws CommandFailedException, RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void setAirplaneMode(boolean airplaneMode) throws CommandFailedException, RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public DeviceInformation getDeviceInformation() throws RemoteException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setOrientation(DeviceOrientation deviceOrientation) throws CommandFailedException, RemoteException
	{
		throwDeviceReleasedException();
	}

	private void throwDeviceReleasedException()
	{
		throw new DeviceReleasedException("Device has been released.");
	}
}
