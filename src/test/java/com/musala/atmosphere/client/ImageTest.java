// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public static final String STRING_TO_COMPARE = "Test_text";

    private static Image image;

    @Before
    public void setUp() throws IOException {
        File imageFile = new File(TestResources.IMAGE_PATH);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        image = new Image(bufferedImage);
    }

    @Test
    public void testSaveImage() throws IOException {

        image = image.load(TestResources.IMAGE_PATH);
        image.save(TestResources.SAVED_IMAGE_NAME);
        File savedImageFile = new File(TestResources.SAVED_IMAGE_NAME);

        assertTrue("The image was not successfully saved.", savedImageFile.exists());
    }

    @Test(expected = IOException.class)
    public void testIOExceptionThrow() throws IOException {

        image.load(TestResources.WRONG_PATH);
    }

    @Test
    public void testLoadImage() throws IOException {
        image = image.load(TestResources.IMAGE_PATH);

        File imageFile = new File(TestResources.IMAGE_PATH);
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

    @Test
    public void testEqualsSameImage() throws Exception {
        File imageFile = new File(TestResources.IMAGE_PATH);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        Image loadedImage = new Image(bufferedImage);
        Image imageToEquals = new Image(bufferedImage);
        assertTrue("Equals returned false while comparing identical images.", loadedImage.equals(imageToEquals));
    }

    @Test
    public void testEqualsDifferentImage() throws Exception {
        File imageFile = new File(TestResources.SCREENSHOT_IMAGE_PATH);
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        Image imageToEquals = new Image(bufferedImage);
        assertFalse("Equals returned true while comparing different images.", image.equals(imageToEquals));
    }

    @Test
    public void testEqualsWrongObject() throws Exception {
        assertFalse("Equals returned true while comparing image with string.", image.equals(STRING_TO_COMPARE));
    }

    @Test
    public void testEqualsNullImage() throws Exception {
        image = image.load(TestResources.IMAGE_PATH);
        Image imageToEquals = null;
        assertFalse("Equals returned true while comparing with null image.", image.equals(imageToEquals));
    }

    @After
    public void TearDown() {
        File savedImageFile = new File(TestResources.SAVED_IMAGE_NAME);
        savedImageFile.delete();
    }
}
