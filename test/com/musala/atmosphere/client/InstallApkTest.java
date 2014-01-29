package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.rmi.RemoteException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertFalse;

import com.musala.atmosphere.client.util.ServerAnnotationProperties;
import com.musala.atmosphere.commons.CommandFailedException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

public class InstallApkTest
{
	private final String PATH_TO_APK_FILE = "./object-browser.apk";

	private final String PATH_TO_NOT_EXISTING_APK_FILE = "E:\\NoExistingFolder\\NotExistingFile.apk";

	private IClientDevice innerClientDeviceMock;

	private Device device;

	@Before
	public void setUpDevice()
	{
		long testPasskey = 0;
		innerClientDeviceMock = mock(IClientDevice.class);
		ServerAnnotationProperties serverAnnotationProperties = mock(ServerAnnotationProperties.class);
		ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(serverAnnotationProperties);
		device = new Device(innerClientDeviceMock, testPasskey, serverConnectionHandler);
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void apkFileNotFoundTest()
	{
		assertFalse(device.installAPK(PATH_TO_NOT_EXISTING_APK_FILE));
	}

	@Test
	public void apkFileInitializationErrorTest() throws Exception
	{
		doThrow(new IOException()).when(innerClientDeviceMock).initApkInstall(anyLong());
		assertFalse(device.installAPK(PATH_TO_APK_FILE));
		verify(innerClientDeviceMock, times(1)).initApkInstall(anyLong());
	}

	@Test
	public void appendingErrorTest() throws Exception
	{
		doThrow(new RemoteException()).when(innerClientDeviceMock).appendToApk((byte[]) any(), anyLong());
		assertFalse(device.installAPK(PATH_TO_APK_FILE));
		// TODO: This should be revised!
		// verify(innerClientDeviceMock, times(1)).appendToApk((byte[]) any(), anyLong());
	}

	@Test
	public void installationFailedCommandExecutionTest() throws Exception
	{
		doThrow(new CommandFailedException()).when(innerClientDeviceMock).buildAndInstallApk(anyLong());
		assertFalse(device.installAPK(PATH_TO_APK_FILE));
	}

	@Test
	public void installationWritingOnWrappedDeviceErrorTest() throws Exception
	{
		doThrow(new CommandFailedException()).when(innerClientDeviceMock).buildAndInstallApk(anyLong());
		assertFalse(device.installAPK(PATH_TO_APK_FILE));
	}

}
