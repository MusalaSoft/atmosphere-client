// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;

import org.junit.BeforeClass;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.websocket.ClientDispatcher;
import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.beans.DeviceAcceleration;
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.util.AtmosphereIntent;

/**
 * TODO: The test is not very useful in this scenario. Consider to rewrite it.
 * 
 * @author yordan.petrov
 *
 */
public class ReconnectDeviceTest {
    private static final int TEST_PASSKEY = 0;

    private static ClientDispatcher dispatcherMock;

    private static DeviceCommunicator deviceCommunicator;

    private static Device testDevice;

    @BeforeClass
    public static void setUp() throws Exception {
        dispatcherMock = mock(ClientDispatcher.class);
        deviceCommunicator = new DeviceCommunicator(TEST_PASSKEY, "test_device_id");

        Class<?> deviceCommunicatorClass = deviceCommunicator.getClass();
        Field dispatcher = deviceCommunicatorClass.getDeclaredField("dispatcher");
        dispatcher.setAccessible(true);
        dispatcher.set(deviceCommunicator, dispatcherMock);

        testDevice = new Device(deviceCommunicator);

        // deviceCommunicator.release();

        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_POWER_PROPERTIES));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.APK_INIT_INSTALL));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.EXECUTE_SHELL_COMMAND),
                                                                                  anyString());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_UI_TREE),
                                                                                  anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_UI_XML_DUMP));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_CONNECTION_TYPE));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_DEVICE_ACCELERATION));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_DEVICE_ORIENTATION));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_DEVICE_INFORMATION));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_MOBILE_DATA_STATE));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_SCREENSHOT));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_ACCELERATION),
                                                                                  any(DeviceAcceleration.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_ORIENTATION),
                                                                                  any(DeviceOrientation.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_POWER_PROPERTIES),
                                                                                  any(PowerProperties.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_WIFI_STATE),
                                                                                  anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_AIRPLANE_MODE),
                                                                                  anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_SCREEN_ORIENTATION),
                                                                                  anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SET_SCREEN_AUTO_ROTATION),
                                                                                  anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SMS_RECEIVE),
                                                                                  any(SmsMessage.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.CALL_RECEIVE),
                                                                                  any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.CALL_ACCEPT),
                                                                                  any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.CALL_HOLD),
                                                                                  any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.PRESS_HARDWARE_BUTTON),
                                                                                  anyLong());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.IME_INPUT_TEXT),
                                                                                  anyString(),
                                                                                  anyLong());
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.CALL_CANCEL),
                                                                                  any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.SEND_BROADCAST),
                                                                                  any(AtmosphereIntent.class));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.GET_AWAKE_STATUS));
        doThrow(new ServerConnectionFailedException()).when(dispatcherMock).route(any(),
                                                                                  anyLong(),
                                                                                  eq(RoutingAction.IS_LOCKED));

        testDevice = new Device(deviceCommunicator);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetBatteryLevel() {
        testDevice.getPowerProperties();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnAppendToApk() {
        testDevice.installAPK("");
    }

    // @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetActiveScreen() {
        // FIXME this test case is no longer valid.
        testDevice.getActiveScreen();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetConnectionType() {
        testDevice.getConnectionType();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetDeviceAcceleration() {
        testDevice.getDeviceAcceleration();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetDeviceOrientation() {
        testDevice.getDeviceOrientation();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetInformation() {
        testDevice.getInformation();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetMobileDataState() {
        testDevice.getMobileDataState();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetScreenshot() {
        testDevice.getScreenshot();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnGetScreenshotWithPath() {
        testDevice.getScreenshot("./");
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnInputText() {
        testDevice.inputText("asd", 0);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnIsAwake() {
        testDevice.isAwake();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnIsLocked() {
        testDevice.isLocked();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnLock() {
        testDevice.lock();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnPressButton() {
        testDevice.pressButton(0);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetAcceleration() {
        testDevice.setAcceleration(new DeviceAcceleration());
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetorientation() {
        testDevice.setDeviceOrientation(new DeviceOrientation());
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetAirplaneMode() {
        testDevice.setAirplaneMode(true);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnEnableScreenAutoRotation() {
        testDevice.enableScreenAutoRotation();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnDisableSetAutoRotation() {
        testDevice.disableScreenAutoRotation();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetBatteryState() {
        testDevice.setPowerProperties(new PowerProperties());
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetScreenOrientation() {
        testDevice.setScreenOrientation(ScreenOrientation.LANDSCAPE);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnSetWiFi() {
        testDevice.enableWiFi();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnUnlock() {
        testDevice.unlock();
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnReceiveSms() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        SmsMessage smsMessage = new SmsMessage(phoneNumber, "");
        testDevice.receiveSms(smsMessage);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnReceiveCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.receiveCall(phoneNumber);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnAcceptCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.acceptCall(phoneNumber);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnHoldCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.holdCall(phoneNumber);
    }

    @Test(expected = ServerConnectionFailedException.class)
    public void testThrowsExceptionOnCancelCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.cancelCall(phoneNumber);
    }
}
