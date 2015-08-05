package com.musala.atmosphere.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.ScreenOrientation;
import com.musala.atmosphere.commons.util.Pair;

/**
 *
 * @author konstantin.ivanov
 *
 */
public class GetUiElementImageTest {
    private Device mockedDevice;

    private UiElement element;

    private UiElementValidator validator;

    private static String screenshotPathLandscape = ".%stest-resources%stestLandscapeImage.png";

    private static String screenshotPathUpsideDownLandscape = ".%stest-resources%stestUpsideDownLandscape.png";

    private static String screenshotPathPortrait = ".%stest-resources%stestportraitImage.png";

    private static String screenshotPathUpsideDownPortrait = ".%stest-resources%stestUpsideDownPortrait.png";

    private static String elementPathLandscape = ".%stest-resources%stestCropImageLandscape.png";

    private static String elementPathUpsideDownLandscape = ".%stest-resources%stestCropImageUpsideDownLandscape.png";

    private static String elementPathPortrait = ".%stest-resources%stestCropImagePortrait.png";

    private static String elementPathUpsideDownPortrait = ".%stest-resources%stestCropImageUpsideDownPortrait.png";

    private Map<String, String> nodeAttributeMapLandscape;

    private Map<String, String> nodeAttributeMapPortrait;

    private Pair<Integer, Integer> resolutionLandscape = new Pair<Integer, Integer>(1280, 800);

    private Pair<Integer, Integer> resolutionPortrait = new Pair<Integer, Integer>(800, 1280);

    private DeviceInformation deviceInformation = new DeviceInformation();

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

        nodeAttributeMapLandscape = new HashMap<>();
        nodeAttributeMapLandscape.put("bounds", "[1074,278][1154,322]");
        nodeAttributeMapLandscape.put("index", "1");
        nodeAttributeMapLandscape.put("content-desc", "");
        nodeAttributeMapLandscape.put("text", "MORE");
        nodeAttributeMapLandscape.put("long-clickable", "false");
        nodeAttributeMapLandscape.put("password", "false");

        nodeAttributeMapPortrait = new HashMap<>();
        nodeAttributeMapPortrait.put("bounds", "[669,278][749,322]");
        nodeAttributeMapPortrait.put("index", "1");
        nodeAttributeMapPortrait.put("content-desc", "");
        nodeAttributeMapPortrait.put("text", "MORE");
        nodeAttributeMapPortrait.put("long-clickable", "false");
        nodeAttributeMapPortrait.put("password", "false");

        mockedDevice = mock(Device.class);
        validator = mock(UiElementValidator.class);
        when(mockedDevice.getUiValidator()).thenReturn(validator);

    }

    @Test
    public void testGetUiElementImageLandscape() throws Exception {

        Path screenshotLandscapeImagePath = Paths.get(screenshotPathLandscape);
        byte[] screenshotLandscapeData = Files.readAllBytes(screenshotLandscapeImagePath);

        deviceInformation.setResolution(resolutionLandscape);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        element = new XmlNodeUiElement(nodeAttributeMapLandscape, mockedDevice);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotLandscapeData);

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
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_LANDSCAPE);
        element = new XmlNodeUiElement(nodeAttributeMapLandscape, mockedDevice);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownLandscapeData);

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
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.PORTRAIT);
        element = new XmlNodeUiElement(nodeAttributeMapPortrait, mockedDevice);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotPortraitData);

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
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_PORTRAIT);
        element = new XmlNodeUiElement(nodeAttributeMapPortrait, mockedDevice);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownPortraitData);

        File expectedImageFile = new File(elementPathUpsideDownPortrait);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }
}
