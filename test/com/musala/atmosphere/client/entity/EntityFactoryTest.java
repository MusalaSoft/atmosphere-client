package com.musala.atmosphere.client.entity;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.BeforeClass;
import org.junit.Test;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.Screen;
import com.musala.atmosphere.commons.DeviceInformation;

/**
 * Tests {@link EntityFactory}.
 *
 * @author filareta.yordanova
 *
 */
// TODO: Add more cases when complex criteria are available, e.g more fields are added to @Restriction.
public class EntityFactoryTest {
    private static final String ERROR_MESSAGE = "Returned entity instance is not from expected type.";

    private static Screen mockedScreen;

    private static DeviceCommunicator mockedCommunicator;

    private EntityFactory entityFactory;

    @BeforeClass
    public static void setUp() {
        mockedCommunicator = mock(DeviceCommunicator.class);
        mockedScreen = mock(Screen.class);
    }

    @Test
    public void testGetGpsLocationEntityForManufacturer() {
        DeviceInformation requiredInformation = new DeviceInformation();
        requiredInformation.setManufacturer("samsung");
        entityFactory = new EntityFactory(mockedScreen, requiredInformation, mockedCommunicator);

        GpsLocationEntity entity = entityFactory.getGpsLocationEntity();
        assertTrue(ERROR_MESSAGE, entity instanceof GpsLocationCheckBoxEntity);
    }

    @Test
    public void testGetGpsLocationEntityWhenNoMatchFound() {
        DeviceInformation requiredInformation = new DeviceInformation();
        requiredInformation.setManufacturer("LG");
        entityFactory = new EntityFactory(mockedScreen, requiredInformation, mockedCommunicator);

        GpsLocationEntity entity = entityFactory.getGpsLocationEntity();
        assertTrue(ERROR_MESSAGE, entity instanceof GpsLocationSwitchViewEntity);
    }
}
