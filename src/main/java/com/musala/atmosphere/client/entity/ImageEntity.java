package com.musala.atmosphere.client.entity;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.Image;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Entity responsible for operations related with images.
 *
 * @author yavor.stankov
 *
 */
public class ImageEntity {
    DeviceCommunicator communicator;

    DeviceSettingsEntity settingsEntity;

    /**
     * Constructs a new {@link ImageEntity} object by given {@link DeviceCommunicator device communicator}.
     *
     * @param communicator
     *        - a communicator to the remote device
     */
    ImageEntity(DeviceCommunicator communicator, DeviceSettingsEntity settingsEntity) {
        this.communicator = communicator;
        this.settingsEntity = settingsEntity;
    }

    /**
     * Gets screenshot of this device's active screen.
     *
     * @return byte buffer, containing captured device screen,<br>
     *         <code>null</code> if getting screenshot fails.<br>
     *         It can be subsequently dumped to a file and directly opened as a PNG image
     */
    public byte[] getScreenshot() {
        return (byte[]) communicator.sendAction(RoutingAction.GET_SCREENSHOT);
    }

    /**
     * Crops this {@link UiElement} as an image, using the bounds of the element.
     *
     * @param propertiesContainer
     *        - a container of properties
     * @return {@link Image} contained in the element's bounds
     * @throws IOException
     *         - if getting screenshot from the device fails
     */
    public Image getElementImage(UiElementPropertiesContainer propertiesContainer) throws IOException {
        byte[] imageInByte = getScreenshot();
        InputStream inputStream = new ByteArrayInputStream(imageInByte);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        Bounds elementBounds = propertiesContainer.getBounds();
        Pair<Integer, Integer> resolution = getDeviceResolution();
        ScreenOrientation screenOrientation = settingsEntity.getScreenOrientation();

        Image newImage = new Image(bufferedImage);
        return newImage.getSubimage(elementBounds, screenOrientation, resolution);
    }

    private Pair<Integer, Integer> getDeviceResolution() {
        // TODO: Move the device information in a entity, or think for a better way to pass it to the UiElement.
        DeviceInformation deviceInformation = (DeviceInformation) communicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);

        return deviceInformation.getResolution();
    }
}
