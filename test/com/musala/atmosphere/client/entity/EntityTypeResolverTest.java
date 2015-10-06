package com.musala.atmosphere.client.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.musala.atmosphere.commons.DeviceInformation;

/**
 * Tests {@link EntityTypeResolver}.
 *
 * @author filareta.yordanova
 *
 */
// TODO: Add more cases when complex criteria are available, e.g more fields are added to @Restriction.
public class EntityTypeResolverTest {
    private static final String ERROR_MESSAGE = "Returned entity instance is not from expected type.";

    private EntityTypeResolver entityFactory;

    @Test
    public void testGetGpsLocationEntityForManufacturer() {
        DeviceInformation requiredInformation = new DeviceInformation();
        requiredInformation.setManufacturer("samsung");
        entityFactory = new EntityTypeResolver(requiredInformation);

        Class<?> entityClass = entityFactory.getEntityClass(GpsLocationEntity.class);
        assertEquals(ERROR_MESSAGE, entityClass, GpsLocationCheckBoxEntity.class);
    }

    @Test
    public void testGetGpsLocationEntityWhenNoMatchFound() {
        DeviceInformation requiredInformation = new DeviceInformation();
        requiredInformation.setManufacturer("LG");
        entityFactory = new EntityTypeResolver(requiredInformation);

        Class<?> entityClass = entityFactory.getEntityClass(GpsLocationEntity.class);
        assertEquals(ERROR_MESSAGE, entityClass, GpsLocationSwitchViewEntity.class);
    }
}
