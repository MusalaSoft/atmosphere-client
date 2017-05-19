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

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.musala.atmosphere.client.websocket.ClientDispatcher;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

@RunWith(MockitoJUnitRunner.class)
public class InstallApkTest {
    private static final int TEST_PASSKEY = 0;

    private static final String TEST_DEVICE_ID = "test_device_id";

    private ClientDispatcher dispatcherMock;

    private DeviceCommunicator deviceCommunicator;

    private Device device;

    @Before
    public void setUpDevice() throws Exception {
        dispatcherMock = mock(ClientDispatcher.class);
        deviceCommunicator = new DeviceCommunicator(TEST_PASSKEY, TEST_DEVICE_ID);
        
        Class<?> deviceCommunicatorClass = deviceCommunicator.getClass();
        Field dispatcher = deviceCommunicatorClass.getDeclaredField("dispatcher");
        dispatcher.setAccessible(true);
        dispatcher.set(deviceCommunicator, dispatcherMock);
       
        device = new Device(deviceCommunicator);
    }

    @Test
    public void apkFileNotFoundTest() throws Exception {
        DeviceCommunicator deviceCommunicatorMock = mock(DeviceCommunicator.class);
        when(deviceCommunicatorMock.sendAction(RoutingAction.APK_INIT_INSTALL)).thenReturn(DeviceCommunicator.VOID_SUCCESS);

        Device device = new Device(deviceCommunicatorMock);

        assertFalse(device.installAPK(TestResources.PATH_TO_NOT_EXISTING_APK_FILE));
    }

    @Test
    public void apkFileInitializationErrorTest() throws Exception {
        doThrow(new CommandFailedException()).when(dispatcherMock).route(any(),
                                                                         anyLong(),
                                                                         eq(RoutingAction.APK_INIT_INSTALL));

        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
        verify(dispatcherMock, times(1)).route(any(), anyLong(), eq(RoutingAction.APK_INIT_INSTALL));
    }

    @Test
    public void appendingErrorTest() throws Exception {
        when(dispatcherMock.route(any(), anyLong(), eq(RoutingAction.APK_INIT_INSTALL))).thenReturn(null);
        doThrow(new CommandFailedException()).when(dispatcherMock)
                                             .route(any(),
                                                    anyLong(),
                                                    eq(RoutingAction.APK_APPEND_DATA),
                                                    any(),
                                                    anyInt());

        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
        verify(dispatcherMock, times(1)).route(any(), anyLong(), eq(RoutingAction.APK_APPEND_DATA), any(), anyLong());
    }

    @Test
    public void installationFailedCommandExecutionTest() throws Exception {
        doThrow(new CommandFailedException()).when(dispatcherMock)
                                             .route(any(),
                                                    anyLong(),
                                                    eq(RoutingAction.APK_BUILD_AND_INSTALL),
                                                    anyBoolean());

        assertFalse(device.installAPK(TestResources.PATH_TO_APK_FILE));
    }

}
