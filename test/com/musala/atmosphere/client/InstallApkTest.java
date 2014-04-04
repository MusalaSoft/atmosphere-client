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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.musala.atmosphere.client.util.ServerAnnotationProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

public class InstallApkTest {
    private final String PATH_TO_APK_FILE = "./object-browser.apk";

    private final String PATH_TO_NOT_EXISTING_APK_FILE = "E:\\NoExistingFolder\\NotExistingFile.apk";

    private IClientDevice innerClientDeviceMock;

    private Device device;

    @Before
    public void setUpDevice() {
        long testPasskey = 0;
        innerClientDeviceMock = mock(IClientDevice.class);
        ServerAnnotationProperties serverAnnotationProperties = mock(ServerAnnotationProperties.class);
        ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(serverAnnotationProperties);
        device = new Device(innerClientDeviceMock, testPasskey, serverConnectionHandler);
    }

    @After
    public void tearDown() {

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
        Mockito.doAnswer(routeAnswer)
               .when(innerClientDeviceMock)
               .route(anyLong(), any(RoutingAction.class), any(), anyInt());

        // FIXME: This should be revised!
        assertFalse(device.installAPK(PATH_TO_APK_FILE));
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
