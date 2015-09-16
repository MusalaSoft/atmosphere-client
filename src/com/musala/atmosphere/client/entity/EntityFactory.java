package com.musala.atmosphere.client.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.reflections.Reflections;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.Screen;
import com.musala.atmosphere.client.entity.annotations.Restriction;
import com.musala.atmosphere.client.exceptions.UnresolvedEntityTypeException;
import com.musala.atmosphere.commons.DeviceInformation;

/**
 * Class responsible for creating the correct instances of the entities, defined for all device specific operations,
 * depending on the provided {@link DeviceInformation}.
 *
 * @author filareta.yordanova
 *
 */
public class EntityFactory {
    private static final String ENTITIES_PACKAGE = "com.musala.atmosphere.client.entity";

    private Screen screen;

    private DeviceCommunicator deviceCommunicator;

    private DeviceInformation deviceInformation;

    private Reflections reflections;

    public EntityFactory(Screen screen, DeviceInformation deviceInformation, DeviceCommunicator deviceCommunicator) {
        this.screen = screen;
        this.deviceInformation = deviceInformation;
        this.deviceCommunicator = deviceCommunicator;
        this.reflections = new Reflections(ENTITIES_PACKAGE);
    }

    /**
     * Returns an instance of the {@link GpsLocationEntity} depending on the {@link DeviceInformation device
     * information}. If no specific implementation matches the required device information, the default
     * {@link GpsLocationEntity} is returned.
     *
     * @return instance of the correct {@link GpsLocationEntity} depending on the {@link DeviceInfromation device
     *         information}
     */
    public GpsLocationEntity getGpsLocationEntity() {
        Class<?> locationEntityClass = findEntityImplementation(GpsLocationEntity.class);

        try {
            Constructor<?> locationEntityConstructor = locationEntityClass.getDeclaredConstructor(Screen.class,
                                                                                                  DeviceCommunicator.class);
            Object locationEntityInstance = locationEntityConstructor.newInstance(new Object[] {screen,
                    deviceCommunicator});

            return (GpsLocationEntity) locationEntityInstance;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new UnresolvedEntityTypeException("Failed to find GpsLocationEntity implmentation matching the given restrictions.",
                                                    e);

        }
    }

    /**
     * Returns an instance of the {@link HardwareButtonEntity}.
     * <p>
     * Note
     * </p>
     * For now only one implementation, independent of the {@link DeviceInformation}, is available.
     *
     * @return instance of the {@link HardwareButtonEntity}
     */
    // TODO: Treat base operations entities consistently with the device specific ones. Figure out more flexible way to
    // return the required set of entities to build the device.
    public HardwareButtonEntity getHardwareButtonEntity() {
        return new HardwareButtonEntity(deviceCommunicator);
    }

    /**
     * Finds entity implementation for a device specific operation depending on the {@link DeviceInformation device
     * information} and the hierarchy type given.
     *
     * @param baseEntityClass
     *        - base class of the entity hierarchy for a device specific operation
     * @return {@link Class} of the entity that matches the required {@link DeviceInformation device information} and is
     *         from type baseEntityClass
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Class<?> findEntityImplementation(Class baseEntityClass) {
        Set<Class<?>> subClasses = reflections.getSubTypesOf(baseEntityClass);
        Class<?> defaultImplementation = null;

        for (Class<?> subClass : subClasses) {
            Annotation annotation = subClass.getAnnotation(Restriction.class);
            if (annotation == null) {
                defaultImplementation = subClass;
            }

            if (annotation != null && isApplicable((Restriction) annotation)) {
                return subClass;
            }
        }

        return defaultImplementation;
    }

    /**
     * Checks if a certain implementation annotated with {@link @Restriction} is applicable for a device with the
     * provided {@link DeviceInformation information}.
     *
     * @param restriction
     *        - restrictions provided for a certain entity implementation
     * @return <code>true</code> if the given restrictions are compatible with the {@link DeviceInformation information}
     *         for the current device, <code>false</code> otherwise
     */
    // TODO: Check for default values in the annotation methods, if the parameter has default value and is not present
    // in the annotation it is not considered when checking for applicability.
    private boolean isApplicable(Restriction restriction) {
        String manufacturer = restriction.manufacturer();
        if (!manufacturer.equals(DeviceInformation.FALLBACK_MANUFACTURER_NAME)
                && !manufacturer.equalsIgnoreCase(deviceInformation.getManufacturer())) {
            return false;
        }

        return true;
    }
}
