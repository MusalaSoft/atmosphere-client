package com.musala.atmosphere.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.ConnectionType;
import com.musala.atmosphere.commons.DeviceAcceleration;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.MobileDataState;
import com.musala.atmosphere.commons.PhoneNumber;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.util.Pair;

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
	public long getFreeRam(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public String executeShellCommand(String shellCommand, long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public List<String> executeSequenceOfShellCommands(List<String> commands, long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void initApkInstall(long invocationPasskey) throws RemoteException, IOException, InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void appendToApk(byte[] bytes, long invocationPasskey)
		throws RemoteException,
			IOException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void buildAndInstallApk(long invocationPasskey)
		throws RemoteException,
			IOException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public void discardApk(long invocationPasskey) throws RemoteException, InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public String getUiXml(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public byte[] getScreenshot(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setNetworkSpeed(Pair<Integer, Integer> speeds, long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public Pair<Integer, Integer> getNetworkSpeed(long invocationPasskey)
		throws RemoteException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setNetworkLatency(int latency, long invocationPasskey) throws RemoteException, InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public int getNetworkLatency(long invocationPasskey) throws RemoteException, InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public void setBatteryLevel(int level, long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();

	}

	@Override
	public int getBatteryLevel(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return 0;
	}

	@Override
	public void setBatteryState(BatteryState state, long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
	}

	@Override
	public BatteryState getBatteryState(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public boolean getPowerState(long invocationPasskey)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return false;
	}

	@Override
	public void setPowerState(boolean state, long invocationPasskey)
		throws CommandFailedException,
			RemoteException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
	}

	@Override
	public DeviceInformation getDeviceInformation(long invocationPasskey)
		throws RemoteException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setDeviceOrientation(DeviceOrientation deviceOrientation, long invocationPasskey)
		throws CommandFailedException,
			RemoteException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void setAcceleration(DeviceAcceleration deviceAcceleration, long invocationPasskey)
		throws RemoteException,
			InvalidPasskeyException
	{
		throwDeviceReleasedException();
	}

	private void throwDeviceReleasedException()
	{
		throw new DeviceReleasedException("Device has been released.");
	}

	@Override
	public DeviceOrientation getDeviceOrientation(long invocationPasskey)
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public DeviceAcceleration getDeviceAcceleration(long invocationPasskey)
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setMobileDataState(MobileDataState state, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public ConnectionType getConnectionType(long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public MobileDataState getMobileDataState(long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
		return null;
	}

	@Override
	public void setWiFi(boolean state, long invocationPaskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void receiveSms(SmsMessage smsMessage, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void receiveCall(PhoneNumber phoneNumber, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void acceptCall(PhoneNumber phoneNumber, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void holdCall(PhoneNumber phoneNumber, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}

	@Override
	public void cancelCall(PhoneNumber phoneNumber, long invocationPasskey)
		throws InvalidPasskeyException,
			CommandFailedException,
			RemoteException
	{
		throwDeviceReleasedException();
	}
}
