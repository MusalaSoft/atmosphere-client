package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.HardwareButtonEntity;
import com.musala.atmosphere.client.entity.ImageEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.websocket.ClientServerWebSocketCommunicator;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.beans.DeviceAcceleration;
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.util.AtmosphereIntent;

/**
 *
 * @author yordan.petrov
 *
 */
public class ReconnectDeviceTest {
    @Mock
    private static ClientServerWebSocketCommunicator communicatorMock = mock(ClientServerWebSocketCommunicator.class);

    @Spy
    private static DeviceCommunicator deviceCommunicator = new DeviceCommunicator(communicatorMock);

    @InjectMocks
    private static HardwareButtonEntity hardwareButtonEntity;

    @InjectMocks
    private static ImeEntity imeEntity;

    @InjectMocks
    private static DeviceSettingsEntity settingsEntity;

    @InjectMocks
    private static ImageEntity imageEntity;

    private static Device testDevice;

    @BeforeClass
    public static void setUp() throws Exception {
        // Constructor visibility is package
        Constructor<?> hardwareButtonEntityConstructor = HardwareButtonEntity.class.getDeclaredConstructor(DeviceCommunicator.class);
        hardwareButtonEntityConstructor.setAccessible(true);
        hardwareButtonEntity = (HardwareButtonEntity) hardwareButtonEntityConstructor.newInstance(new Object[] {
                deviceCommunicator});

        Constructor<?> imeEntityConstructor = ImeEntity.class.getDeclaredConstructor(DeviceCommunicator.class);
        imeEntityConstructor.setAccessible(true);
        imeEntity = (ImeEntity) imeEntityConstructor.newInstance(new Object[] {deviceCommunicator});

        Constructor<?> settingsEntitiyConstructor = DeviceSettingsEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                      DeviceInformation.class);
        settingsEntitiyConstructor.setAccessible(true);
        settingsEntity = (DeviceSettingsEntity) settingsEntitiyConstructor.newInstance(new Object[] {deviceCommunicator,
                mock(DeviceInformation.class)});

        Constructor<?> imageEntityConstructor = ImageEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                         DeviceSettingsEntity.class);
        imageEntityConstructor.setAccessible(true);
        imageEntity = (ImageEntity) imageEntityConstructor.newInstance(new Object[] {deviceCommunicator,
                settingsEntity});

        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_POWER_PROPERTIES));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.APK_INIT_INSTALL));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.EXECUTE_SHELL_COMMAND), anyString());
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_UI_TREE),
                                                                         anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_UI_XML_DUMP));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_CONNECTION_TYPE));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_DEVICE_ACCELERATION));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_DEVICE_ORIENTATION));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_DEVICE_INFORMATION));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_MOBILE_DATA_STATE));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_SCREENSHOT));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.SET_ACCELERATION),
                                                                         any(DeviceAcceleration.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.SET_ORIENTATION),
                                                                         any(DeviceOrientation.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.SET_POWER_PROPERTIES),
                                                                         any(PowerProperties.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.SET_WIFI_STATE),
                                                                         anyBoolean());
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.SMS_RECEIVE), any(SmsMessage.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.CALL_RECEIVE), any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.CALL_ACCEPT), any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.CALL_HOLD), any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.CALL_CANCEL), any(PhoneNumber.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock)
                                      .sendAction(eq(RoutingAction.SEND_BROADCAST), any(AtmosphereIntent.class));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.GET_AWAKE_STATUS));
        doThrow(new ServerConnectionFailedException()).when(communicatorMock).sendAction(eq(RoutingAction.IS_LOCKED));

        testDevice = new Device(deviceCommunicator);
        testDevice.setHardwareButtonEntity(hardwareButtonEntity);
        testDevice.setImeEntity(imeEntity);
        testDevice.setSettingsEntity(settingsEntity);
        testDevice.setImageEntity(imageEntity);
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
