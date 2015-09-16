package com.musala.atmosphere.client;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URLDecoder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

@RunWith(MockitoJUnitRunner.class)
public class InstallApkTest {
    private static final int TEST_PASSKEY = 0;

    private final String PATH_TO_APK_FILE = "object-browser.apk";

    private final String PATH_TO_NOT_EXISTING_APK_FILE = "E:\\NoExistingFolder\\NotExistingFile.apk";

    private IClientDevice innerClientDeviceMock;

    private DeviceCommunicator deviceCommunicator;

    private Device device;

    @Before
    public void setUpDevice() throws Exception {
        DeviceInformation deviceInfoMock = mock(DeviceInformation.class);
        innerClientDeviceMock = mock(IClientDevice.class);
        when(innerClientDeviceMock.route(eq(TEST_PASSKEY),
                                         eq(RoutingAction.GET_DEVICE_INFORMATION))).thenReturn(deviceInfoMock);
        deviceCommunicator = new DeviceCommunicator(innerClientDeviceMock, TEST_PASSKEY);

        device = new Device(deviceCommunicator);
    }

    @Test
    public void apkFileNotFoundTest() {
        assertFalse(device.installAPK(PATH_TO_NOT_EXISTING_APK_FILE));
    }

    @Test
    public void apkFileInitializationErrorTest() throws Exception {
        doThrow(new CommandFailedException()).when(innerClientDeviceMock).route(anyLong(),
                                                                                eq(RoutingAction.APK_INIT_INSTALL));
        assertFalse(device.installAPK(PATH_TO_APK_FILE));
        verify(innerClientDeviceMock, times(1)).route(anyLong(), eq(RoutingAction.APK_INIT_INSTALL));
    }

    @Test
    public void appendingErrorTest() throws Exception {
        Answer<Object> routeAnswer = new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RoutingAction action = (RoutingAction) args[1];
                switch (action) {
                    case APK_INIT_INSTALL:
                        return null;
                    case APK_APPEND_DATA:
                        throw new CommandFailedException();
                    default:
                        break;
                }
                return null;
            }
        };
        Mockito.doAnswer(routeAnswer).when(innerClientDeviceMock).route(anyLong(), any(RoutingAction.class));
        Mockito.doAnswer(routeAnswer).when(innerClientDeviceMock).route(anyLong(),
                                                                        any(RoutingAction.class),
                                                                        any(),
                                                                        anyInt());

        // FIXME: This should be revised!
        String file = getClass().getResource(PATH_TO_APK_FILE).getFile();
        // Decoding the url encoded values
        file = URLDecoder.decode(file, "UTF-8");
        assertFalse(device.installAPK(file));
        verify(innerClientDeviceMock, times(1)).route(anyLong(), eq(RoutingAction.APK_APPEND_DATA), any(), anyLong());
    }

    @Test
    public void installationFailedCommandExecutionTest() throws Exception {
        doThrow(new CommandFailedException()).when(innerClientDeviceMock)
                                             .route(anyLong(), eq(RoutingAction.APK_BUILD_AND_INSTALL));
        assertFalse(device.installAPK(PATH_TO_APK_FILE));
    }

    @Test
    public void installationWritingOnWrappedDeviceErrorTest() throws Exception {
        doThrow(new CommandFailedException()).when(innerClientDeviceMock)
                                             .route(anyLong(), eq(RoutingAction.APK_BUILD_AND_INSTALL));
        assertFalse(device.installAPK(PATH_TO_APK_FILE));
    }

}
