package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.rmi.RemoteException;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.commons.BatteryState;
import com.musala.atmosphere.commons.DeviceAcceleration;
import com.musala.atmosphere.commons.DeviceOrientation;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

/**
 * 
 * @author yordan.petrov
 * 
 */
public class ReconnectDeviceTest
{
	private static IClientDevice mockedClientDevice;

	private static ServerConnectionHandler mockedServerConnectionHandler;

	private static Device testDevice;

	@BeforeClass
	public static void setUp() throws Exception
	{
		mockedClientDevice = mock(IClientDevice.class);
		doThrow(new RemoteException()).when(mockedClientDevice).getBatteryLevel(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getBatteryState(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getPowerState(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).initApkInstall(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).executeShellCommand(anyString(), anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getUiXml(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getConnectionType(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getDeviceAcceleration(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getDeviceOrientation(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getDeviceInformation(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getMobileDataState(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).getScreenshot(anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setAcceleration(any(DeviceAcceleration.class),
																				anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setDeviceOrientation(	any(DeviceOrientation.class),
																						anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setBatteryState(any(BatteryState.class), anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setBatteryLevel(anyInt(), anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setPowerState(anyBoolean(), anyLong());
		doThrow(new RemoteException()).when(mockedClientDevice).setWiFi(anyBoolean(), anyLong());

		mockedServerConnectionHandler = mock(ServerConnectionHandler.class);

		testDevice = new Device(mockedClientDevice, 0, mockedServerConnectionHandler);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetBatteryLevel()
	{
		testDevice.getBatteryLevel();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetBatteryState()
	{
		testDevice.getBatteryState();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetPowerState()
	{
		testDevice.getPowerState();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnAppendToApk()
	{
		testDevice.installAPK("");
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetActiveScreen()
	{
		testDevice.getActiveScreen();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetConnectionType()
	{
		testDevice.getConnectionType();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetDeviceAcceleration()
	{
		testDevice.getDeviceAcceleration();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetDeviceOrientation()
	{
		testDevice.getDeviceOrientation();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetInformation()
	{
		testDevice.getInformation();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetMobileDataState()
	{
		testDevice.getMobileDataState();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetScreenshot()
	{
		testDevice.getScreenshot();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnGetScreenshotWithPath()
	{
		testDevice.getScreenshot("./");
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnInputText()
	{
		testDevice.inputText("asd", 0);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnIsAwake()
	{
		testDevice.isAwake();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnIsLocked()
	{
		testDevice.isLocked();
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnLock()
	{
		testDevice.setLocked(true);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnPressButton()
	{
		testDevice.pressButton(0);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetAcceleration()
	{
		testDevice.setAcceleration(new DeviceAcceleration());
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetorientation()
	{
		testDevice.setDeviceOrientation(new DeviceOrientation());
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetAirplaneMode()
	{
		testDevice.setAirplaneMode(true);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetAutoRotation()
	{
		testDevice.setAutoRotation(true);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetBatteryState()
	{
		testDevice.setBatteryState(BatteryState.CHARGING);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetBatteryLevel()
	{
		testDevice.setBatteryLevel(11);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetPowerState()
	{
		testDevice.setPowerState(true);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetScreenOrientation()
	{
		testDevice.setScreenOrientation(ScreenOrientation.LANDSCAPE);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnSetWiFi()
	{
		testDevice.setWiFi(true);
	}

	@Test(expected = DeviceReleasedException.class)
	public void testThrowsExceptionOnUnlock()
	{
		testDevice.setLocked(false);
	}
}
