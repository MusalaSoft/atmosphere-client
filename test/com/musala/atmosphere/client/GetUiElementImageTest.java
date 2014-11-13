package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

/**
 * 
 * @author denis.bialev
 * 
 */
public class GetUiElementImageTest {

    private Device mockedDevice;

    private UiElement element;

    private UiElementValidator validator;

    private static String screenshotPath = ".%stest-resources%sscreenshot.png";

    private static String elementPath = ".%stest-resources%stestImageElement.png";

    @Before
    public void setUp() throws IOException {
        screenshotPath = String.format(screenshotPath, File.separator, File.separator);
        elementPath = String.format(elementPath, File.separator, File.separator);

        Path screenshotImagePath = Paths.get(screenshotPath);
        byte[] screenshotData = Files.readAllBytes(screenshotImagePath);

        Map<String, String> nodeAttributeMap = new HashMap<>();
        nodeAttributeMap.put("bounds", "[0,87][192,279]");
        nodeAttributeMap.put("index", "5");
        nodeAttributeMap.put("content-desc", "imageViewTest");
        nodeAttributeMap.put("text", "my-text");
        nodeAttributeMap.put("long-clickable", "true");
        nodeAttributeMap.put("password", "false");

        mockedDevice = mock(Device.class);
        validator = mock(UiElementValidator.class);
        when(mockedDevice.getUiValidator()).thenReturn(validator);

        element = new UiElement(nodeAttributeMap, mockedDevice);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotData);
    }

    @Test
    public void testGetUiElementImage()
        throws XPathExpressionException,
            IOException,
            UiElementFetchingException,
            InvalidCssQueryException {

        File expectedImageFile = new File(elementPath);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        Image elementImage = element.getElementImage();

        assertTrue("Received image is different than the expected one.", expectedImage.containsImage(elementImage));
        assertTrue("Expected image is different than the received one.", elementImage.containsImage(expectedImage));
    }
}
