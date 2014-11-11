package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author denis.bialev
 * 
 */
public class ImageTest {

    private static String IMAGE_PATH_FORMAT = ".%stest-resources%stestImage.png";

    private static String IMAGE_PATH;

    private static String WRONG_PATH_FORMAT = ".%sUnexistentImage.png";

    private static String WRONG_PATH;

    private static final String SAVED_IMAGE_NAME = "saveTestImage.png";

    private static Image image;

    @Before
    public void setUp() throws IOException {
        IMAGE_PATH = String.format(IMAGE_PATH_FORMAT, File.separator, File.separator);
        WRONG_PATH = String.format(WRONG_PATH_FORMAT, File.separator);

        File imageFile = new File(IMAGE_PATH);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        image = new Image(bufferedImage);
    }

    @Test
    public void testSaveImage() throws IOException {

        image = image.load(IMAGE_PATH);
        image.save(SAVED_IMAGE_NAME);
        File savedImageFile = new File(SAVED_IMAGE_NAME);

        assertTrue("The image was not successfully saved.", savedImageFile.exists());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionThrow() throws IOException {

        image.load(WRONG_PATH);
    }

    @Test
    public void testLoadImage() throws IOException {
        image = image.load(IMAGE_PATH);

        File imageFile = new File(IMAGE_PATH);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        Image loadedImage = new Image(bufferedImage);

        Integer loadedImageHeight = loadedImage.getHeight();
        Integer loadedImageWidth = loadedImage.getWidth();
        Integer bufferedImageHeight = bufferedImage.getHeight();
        Integer bufferedImageWidth = bufferedImage.getWidth();

        assertEquals("The loaded image have different height from the buffered image.",
                     bufferedImageHeight,
                     loadedImageHeight);
        assertEquals("The loaded image have different width from the buffered image.",
                     bufferedImageWidth,
                     loadedImageWidth);
        assertTrue("The image comparator returned false when the loaded and buffered image should be equals.",
                   image.containsImage(loadedImage));
    }

    @After
    public void TearDown() {
        File savedImageFile = new File(SAVED_IMAGE_NAME);
        savedImageFile.delete();
    }
}
