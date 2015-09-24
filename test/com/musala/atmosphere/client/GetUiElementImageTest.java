package com.musala.atmosphere.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.GestureEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;
import com.musala.atmosphere.commons.util.Pair;

/**
 *
 * @author konstantin.ivanov
 *
 */
public class GetUiElementImageTest {
    @Mock(name = "onDevice")
    private Device mockedDevice;

    @Spy
    private UiElementPropertiesContainer propertiesContainer;

    @InjectMocks
    private AccessibilityUiElement element;

    private Bounds boundsLandscape;

    private Bounds boundsPortrait;

    private static String screenshotPathLandscape = ".%stest-resources%stestLandscapeImage.png";

    private static String screenshotPathUpsideDownLandscape = ".%stest-resources%stestUpsideDownLandscape.png";

    private static String screenshotPathPortrait = ".%stest-resources%stestportraitImage.png";

    private static String screenshotPathUpsideDownPortrait = ".%stest-resources%stestUpsideDownPortrait.png";

    private static String elementPathLandscape = ".%stest-resources%stestCropImageLandscape.png";

    private static String elementPathUpsideDownLandscape = ".%stest-resources%stestCropImageUpsideDownLandscape.png";

    private static String elementPathPortrait = ".%stest-resources%stestCropImagePortrait.png";

    private static String elementPathUpsideDownPortrait = ".%stest-resources%stestCropImageUpsideDownPortrait.png";

    private Pair<Integer, Integer> resolutionLandscape = new Pair<Integer, Integer>(1280, 800);

    private Pair<Integer, Integer> resolutionPortrait = new Pair<Integer, Integer>(800, 1280);

    private DeviceInformation deviceInformation = new DeviceInformation();

    private GestureEntity gestureEntity;

    private ImeEntity imeEntity;

    private DeviceSettingsEntity settingsEntity;

    private static final String RECEIVED_DIFFERENT_IMAGES_MESSAGE = "Received image is different than the expected one.";

    @Before
    public void setUp() throws Exception {
        screenshotPathLandscape = String.format(screenshotPathLandscape, File.separator, File.separator);
        elementPathLandscape = String.format(elementPathLandscape, File.separator, File.separator);

        screenshotPathUpsideDownLandscape = String.format(screenshotPathUpsideDownLandscape,
                                                          File.separator,
                                                          File.separator);
        elementPathUpsideDownLandscape = String.format(elementPathUpsideDownLandscape, File.separator, File.separator);

        screenshotPathPortrait = String.format(screenshotPathPortrait, File.separator, File.separator);
        elementPathPortrait = String.format(elementPathPortrait, File.separator, File.separator);

        screenshotPathUpsideDownPortrait = String.format(screenshotPathUpsideDownPortrait,
                                                         File.separator,
                                                         File.separator);
        elementPathUpsideDownPortrait = String.format(elementPathUpsideDownPortrait, File.separator, File.separator);

        propertiesContainer = spy(new AccessibilityElement());

        boundsLandscape = new Bounds(new Point(1074, 278), new Point(1154, 322));
        boundsPortrait = new Bounds(new Point(669, 278), new Point(749, 322));

        mockedDevice = mock(Device.class);
        gestureEntity = mock(GestureEntity.class);
        imeEntity = mock(ImeEntity.class);
        settingsEntity = mock(DeviceSettingsEntity.class);


        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUiElementImageLandscape() throws Exception {
        Path screenshotLandscapeImagePath = Paths.get(screenshotPathLandscape);
        byte[] screenshotLandscapeData = Files.readAllBytes(screenshotLandscapeImagePath);

        deviceInformation.setResolution(resolutionLandscape);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(settingsEntity.getScreenOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotLandscapeData);

        when(propertiesContainer.getBounds()).thenReturn(boundsLandscape);

        File expectedImageFile = new File(elementPathLandscape);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImageUpsideDownLandscape() throws Exception {
        Path screenshotUpsideDownLandscapeImagePath = Paths.get(screenshotPathUpsideDownLandscape);
        byte[] screenshotUpsideDownLandscapeData = Files.readAllBytes(screenshotUpsideDownLandscapeImagePath);

        deviceInformation.setResolution(resolutionLandscape);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(settingsEntity.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_LANDSCAPE);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownLandscapeData);

        when(propertiesContainer.getBounds()).thenReturn(boundsLandscape);

        File expectedImageFile = new File(elementPathUpsideDownLandscape);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImagePortrait() throws Exception {
        Path screenshotPortraitImagePath = Paths.get(screenshotPathPortrait);
        byte[] screenshotPortraitData = Files.readAllBytes(screenshotPortraitImagePath);

        deviceInformation.setResolution(resolutionPortrait);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(settingsEntity.getScreenOrientation()).thenReturn(ScreenOrientation.PORTRAIT);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotPortraitData);

        when(propertiesContainer.getBounds()).thenReturn(boundsPortrait);

        File expectedImageFile = new File(elementPathPortrait);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImageUpsideDownPortrait() throws Exception {
        Path screenshotUpsideDownPortraitImagePath = Paths.get(screenshotPathUpsideDownPortrait);
        byte[] screenshotUpsideDownPortraitData = Files.readAllBytes(screenshotUpsideDownPortraitImagePath);

        deviceInformation.setResolution(resolutionPortrait);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(settingsEntity.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_PORTRAIT);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownPortraitData);

        when(propertiesContainer.getBounds()).thenReturn(boundsPortrait);

        File expectedImageFile = new File(elementPathUpsideDownPortrait);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }
}
