package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.ApkInstallationFailedException;
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
		innerClientDeviceMock = mock(IClientDevice.class);
		device = new Device(innerClientDeviceMock);
	}

	@After
	public void tearDown()
	{

	}

	@Test(expected = ApkInstallationFailedException.class)
	public void apkFileNotFoundTest()
	{
		device.installAPK(PATH_TO_NOT_EXISTING_APK_FILE);
	}

	@Test(expected = ApkInstallationFailedException.class)
	public void apkFileInitializationErrorTest() throws IOException, ApkInstallationFailedException
	{
		doThrow(new IOException()).when(innerClientDeviceMock).initApkInstall();
		device.installAPK(PATH_TO_APK_FILE);
		verify(innerClientDeviceMock, times(1)).initApkInstall();
	}

	@Test(expected = ApkInstallationFailedException.class)
	public void appendingErrorTest() throws IOException, ApkInstallationFailedException
	{
		doThrow(new IOException()).when(innerClientDeviceMock).appendToApk((byte[]) any());
		device.installAPK(PATH_TO_APK_FILE);
		verify(innerClientDeviceMock, times(1)).appendToApk((byte[]) any());
	}

	@Test(expected = ApkInstallationFailedException.class)
	public void installationFailedCommandExecutionTest()
		throws CommandFailedException,
			IOException,
			ApkInstallationFailedException
	{
		doThrow(new CommandFailedException()).when(innerClientDeviceMock).buildAndInstallApk();
		device.installAPK(PATH_TO_APK_FILE);
		verify(innerClientDeviceMock, times(1)).appendToApk((byte[]) any());
	}

	@Test(expected = ApkInstallationFailedException.class)
	public void installationWritingOnWrappedDeviceErrorTest()
		throws IOException,
			CommandFailedException,
			ApkInstallationFailedException
	{
		doThrow(new CommandFailedException()).when(innerClientDeviceMock).buildAndInstallApk();
		device.installAPK(PATH_TO_APK_FILE);
		verify(innerClientDeviceMock, times(1)).appendToApk((byte[]) any());
	}

}
