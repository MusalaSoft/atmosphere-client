package com.musala.atmosphere.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.GettingScreenshotFailedException;

/**
 * 
 * @author denis.bialev
 * 
 */
public class ImagePresentOnScreenTest {
    private static Device mockedDevice;

    private static String screenshotPath = ".%1$stest-resources%1$sscreenshot.png";

    private static String imagePath = ".%1$stest-resources%1$stestImageElement.png";

    private static String notPresentImagePath = ".%1$stest-resources%1$stestImage.png";

    private static Image currentScreenImage;

    @BeforeClass
    public static void setUp() throws IOException {
        screenshotPath = String.format(screenshotPath, File.separator);
        imagePath = String.format(imagePath, File.separator);
        notPresentImagePath = String.format(notPresentImagePath, File.separator);

        File screenshotFile = new File(screenshotPath);
        BufferedImage bufferedElementImage = ImageIO.read(screenshotFile);
        currentScreenImage = new Image(bufferedElementImage);
    }

    @Before
    public void setUpBefore() throws GettingScreenshotFailedException {
        mockedDevice = mock(Device.class);
        when(mockedDevice.getDeviceScreenshotImage()).thenReturn(currentScreenImage);
        when(mockedDevice.isImagePresentOnScreen(any(Image.class))).thenCallRealMethod();
    }

    @Test
    public void testIsPresentOnScreenWhenPresent() throws Exception {
        File imageFile = new File(imagePath);
        BufferedImage bufferedElementImage = ImageIO.read(imageFile);
        Image soughtImage = new Image(bufferedElementImage);

        assertTrue("The expected image was not found when present on the screen.",
                   mockedDevice.isImagePresentOnScreen(soughtImage));
    }

    @Test
    public void testIsPresentOnScreenWhenNotPresent() throws Exception {
        File notPresentImageFile = new File(notPresentImagePath);
        BufferedImage notPresentBufferedImage = ImageIO.read(notPresentImageFile);
        Image notPresentImage = new Image(notPresentBufferedImage);

        assertFalse("The image was found on the screen when not present.",
                    mockedDevice.isImagePresentOnScreen(notPresentImage));
    }
}
