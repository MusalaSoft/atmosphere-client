package com.musala.atmosphere.client;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.musala.atmosphere.client.websocket.ClientServerWebSocketCommunicator;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

@RunWith(MockitoJUnitRunner.class)
public class InstallApkTest {
    private ClientServerWebSocketCommunicator communicatorMock;

    private DeviceCommunicator deviceCommunicator;

    private Device device;

    @Before
    public void setUpDevice() throws Exception {
        DeviceInformation deviceInfoMock = mock(DeviceInformation.class);
        communicatorMock = mock(ClientServerWebSocketCommunicator.class);
        when(communicatorMock.sendAction(eq(RoutingAction.GET_DEVICE_INFORMATION))).thenReturn(deviceInfoMock);
        deviceCommunicator = new DeviceCommunicator(communicatorMock);

        device = new Device(deviceCommunicator);
    }

    @Test
    public void apkFileNotFoundTest() {
        assertFalse(device.installAPK(TestResources.PATH_TO_NOT_EXISTING_APK_FILE));
    }

    @Test
    public void apkFileInitializationErrorTest() throws Exception {
        doThrow(new CommandFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.APK_INIT_INSTALL));
        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
        verify(communicatorMock, times(1)).sendAction(eq(RoutingAction.APK_INIT_INSTALL));
    }

    @Test
    public void appendingErrorTest() throws Exception {
        Answer<Object> routeAnswer = new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RoutingAction action = (RoutingAction) args[0];
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
        Mockito.doAnswer(routeAnswer).when(communicatorMock).sendAction(any(RoutingAction.class));
        Mockito.doAnswer(routeAnswer).when(communicatorMock).sendAction(any(RoutingAction.class),
                                                                        any(),
                                                                        anyInt());

        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
        verify(communicatorMock, times(1)).sendAction(eq(RoutingAction.APK_APPEND_DATA), any(), anyLong());
    }

    @Test
    public void installationFailedCommandExecutionTest() throws Exception {
        doThrow(new CommandFailedException()).when(communicatorMock)
                                             .sendAction(eq(RoutingAction.APK_BUILD_AND_INSTALL), anyBoolean());
        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
    }

    @Test
    public void installationWritingOnWrappedDeviceErrorTest() throws Exception {
        doThrow(new CommandFailedException()).when(communicatorMock)
                                             .sendAction(eq(RoutingAction.APK_BUILD_AND_INSTALL), anyBoolean());
        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
    }

}
