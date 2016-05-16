package com.musala.atmosphere.client;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Constructor;
import java.rmi.RemoteException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.HardwareButtonEntity;
import com.musala.atmosphere.client.entity.ImageEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.PowerProperties;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.SmsMessage;
import com.musala.atmosphere.commons.beans.DeviceAcceleration;
import com.musala.atmosphere.commons.beans.DeviceOrientation;
import com.musala.atmosphere.commons.beans.PhoneNumber;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.util.AtmosphereIntent;

/**
 *
 * @author yordan.petrov
 *
 */
public class ReconnectDeviceTest {
    private static final int TEST_PASSKEY = 0;

    @Mock
    private static IClientDevice mockedClientDevice = mock(IClientDevice.class);

    @Spy
    private static DeviceCommunicator deviceCommunicator = new DeviceCommunicator(mockedClientDevice, TEST_PASSKEY);

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

        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_POWER_PROPERTIES));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.APK_INIT_INSTALL));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.EXECUTE_SHELL_COMMAND), anyString());
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_UI_TREE),
                                                                      anyBoolean());
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.GET_UI_XML_DUMP));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.GET_CONNECTION_TYPE));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_DEVICE_ACCELERATION));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_DEVICE_ORIENTATION));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_DEVICE_INFORMATION));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.GET_MOBILE_DATA_STATE));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.GET_SCREENSHOT));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.SET_ACCELERATION),
                                                                      any(DeviceAcceleration.class));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.SET_ORIENTATION),
                                                                      any(DeviceOrientation.class));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.SET_POWER_PROPERTIES),
                                                                      any(PowerProperties.class));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(),
                                                                      eq(RoutingAction.SET_WIFI_STATE),
                                                                      anyBoolean());
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.SMS_RECEIVE), any(SmsMessage.class));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.CALL_RECEIVE), any(PhoneNumber.class));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.CALL_ACCEPT), any(PhoneNumber.class));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.CALL_HOLD), any(PhoneNumber.class));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.CALL_CANCEL), any(PhoneNumber.class));
        doThrow(new RemoteException()).when(mockedClientDevice)
                                      .route(anyLong(), eq(RoutingAction.SEND_BROADCAST), any(AtmosphereIntent.class));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.GET_AWAKE_STATUS));
        doThrow(new RemoteException()).when(mockedClientDevice).route(anyLong(), eq(RoutingAction.IS_LOCKED));

        testDevice = new Device(deviceCommunicator);
        testDevice.setHardwareButtonEntity(hardwareButtonEntity);
        testDevice.setImeEntity(imeEntity);
        testDevice.setSettingsEntity(settingsEntity);
        testDevice.setImageEntity(imageEntity);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetBatteryLevel() {
        testDevice.getPowerProperties();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnAppendToApk() {
        testDevice.installAPK("");
    }

    // @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetActiveScreen() {
        // FIXME this test case is no longer valid.
        testDevice.getActiveScreen();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetConnectionType() {
        testDevice.getConnectionType();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetDeviceAcceleration() {
        testDevice.getDeviceAcceleration();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetDeviceOrientation() {
        testDevice.getDeviceOrientation();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetInformation() {
        testDevice.getInformation();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetMobileDataState() {
        testDevice.getMobileDataState();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetScreenshot() {
        testDevice.getScreenshot();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnGetScreenshotWithPath() {
        testDevice.getScreenshot("./");
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnInputText() {
        testDevice.inputText("asd", 0);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnIsAwake() {
        testDevice.isAwake();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnIsLocked() {
        testDevice.isLocked();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnLock() {
        testDevice.lock();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnPressButton() {
        testDevice.pressButton(0);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetAcceleration() {
        testDevice.setAcceleration(new DeviceAcceleration());
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetorientation() {
        testDevice.setDeviceOrientation(new DeviceOrientation());
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetAirplaneMode() {
        testDevice.setAirplaneMode(true);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnEnableScreenAutoRotation() {
        testDevice.enableScreenAutoRotation();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnDisableSetAutoRotation() {
        testDevice.disableScreenAutoRotation();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetBatteryState() {
        testDevice.setPowerProperties(new PowerProperties());
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetScreenOrientation() {
        testDevice.setScreenOrientation(ScreenOrientation.LANDSCAPE);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnSetWiFi() {
        testDevice.enableWiFi();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnUnlock() {
        testDevice.unlock();
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnReceiveSms() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        SmsMessage smsMessage = new SmsMessage(phoneNumber, "");
        testDevice.receiveSms(smsMessage);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnReceiveCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.receiveCall(phoneNumber);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnAcceptCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.acceptCall(phoneNumber);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnHoldCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.holdCall(phoneNumber);
    }

    @Test(expected = DeviceReleasedException.class)
    public void testThrowsExceptionOnCancelCall() {
        PhoneNumber phoneNumber = new PhoneNumber("123");
        testDevice.cancelCall(phoneNumber);
    }
}
