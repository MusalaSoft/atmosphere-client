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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    private static Image currentScreenImage;

    @BeforeClass
    public static void setUp() throws IOException {

        File screenshotFile = new File(TestResources.SCREENSHOT_IMAGE_PATH);
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
        File imageFile = new File(TestResources.IMAGE_ELEMENT_PATH);
        BufferedImage bufferedElementImage = ImageIO.read(imageFile);
        Image soughtImage = new Image(bufferedElementImage);

        assertTrue("The expected image was not found when present on the screen.",
                   mockedDevice.isImagePresentOnScreen(soughtImage));
    }

    @Test
    public void testIsPresentOnScreenWhenNotPresent() throws Exception {
        File notPresentImageFile = new File(TestResources.IMAGE_PATH);
        BufferedImage notPresentBufferedImage = ImageIO.read(notPresentImageFile);
        Image notPresentImage = new Image(notPresentBufferedImage);

        assertFalse("The image was found on the screen when not present.",
                    mockedDevice.isImagePresentOnScreen(notPresentImage));
    }
}
