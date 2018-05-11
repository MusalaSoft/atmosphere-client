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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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

    @Mock
    private AccessibilityUiElement element;

    private Bounds boundsLandscape;

    private Bounds boundsPortrait;

    private Pair<Integer, Integer> resolutionLandscape = new Pair<Integer, Integer>(1280, 800);

    private Pair<Integer, Integer> resolutionPortrait = new Pair<Integer, Integer>(800, 1280);

    private DeviceInformation deviceInformation = new DeviceInformation();

    private static final String RECEIVED_DIFFERENT_IMAGES_MESSAGE = "Received image is different than the expected one.";

    @Before
    public void setUp() throws Exception {
        propertiesContainer = spy(new AccessibilityElement());

        boundsLandscape = new Bounds(new Point(1074, 278), new Point(1154, 322));
        boundsPortrait = new Bounds(new Point(669, 278), new Point(749, 322));

        mockedDevice = mock(Device.class);

        element = mock(AccessibilityUiElement.class);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUiElementImageLandscape() throws Exception {
        Path screenshotLandscapeImagePath = Paths.get(TestResources.SCREENSHOT_LANDSCAPE_PATH);
        byte[] screenshotLandscapeData = Files.readAllBytes(screenshotLandscapeImagePath);

        deviceInformation.setResolution(resolutionLandscape);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.LANDSCAPE);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotLandscapeData);

        when(propertiesContainer.getBounds()).thenReturn(boundsLandscape);

        File expectedImageFile = new File(TestResources.ELEMENT_LANDSCAPE_PATH);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        when(element.getElementImage()).thenReturn(expectedImage);
        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImageUpsideDownLandscape() throws Exception {
        Path screenshotUpsideDownLandscapeImagePath = Paths.get(TestResources.SCREENSHOT_UPSIDE_DOWN_LANDSCAPE_PATH);
        byte[] screenshotUpsideDownLandscapeData = Files.readAllBytes(screenshotUpsideDownLandscapeImagePath);

        deviceInformation.setResolution(resolutionLandscape);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_LANDSCAPE);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownLandscapeData);

        when(propertiesContainer.getBounds()).thenReturn(boundsLandscape);

        File expectedImageFile = new File(TestResources.ELEMENT_UPSIDE_DOWN_LANDSCAPE_PATH);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        when(element.getElementImage()).thenReturn(expectedImage);
        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImagePortrait() throws Exception {
        Path screenshotPortraitImagePath = Paths.get(TestResources.SCREENSHOT_PORTRAIT_PATH);
        byte[] screenshotPortraitData = Files.readAllBytes(screenshotPortraitImagePath);

        deviceInformation.setResolution(resolutionPortrait);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.PORTRAIT);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotPortraitData);

        when(propertiesContainer.getBounds()).thenReturn(boundsPortrait);

        File expectedImageFile = new File(TestResources.ELEMENT_PORTRAIT_PATH);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);

        when(element.getElementImage()).thenReturn(expectedImage);
        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }

    @Test
    public void testGetUiElementImageUpsideDownPortrait() throws Exception {
        Path screenshotUpsideDownPortraitImagePath = Paths.get(TestResources.SCREENSHOT_UPSIDE_DOWN_PORTRAIT_PATH);
        byte[] screenshotUpsideDownPortraitData = Files.readAllBytes(screenshotUpsideDownPortraitImagePath);

        deviceInformation.setResolution(resolutionPortrait);

        when(mockedDevice.getInformation()).thenReturn(deviceInformation);
        when(mockedDevice.getScreenOrientation()).thenReturn(ScreenOrientation.UPSIDE_DOWN_PORTRAIT);
        when(mockedDevice.getScreenshot()).thenReturn(screenshotUpsideDownPortraitData);

        when(propertiesContainer.getBounds()).thenReturn(boundsPortrait);

        File expectedImageFile = new File(TestResources.ELEMENT_UPSIDE_DOWN_PORTRAIT_PATH);
        BufferedImage expectedBufferedElementImage = ImageIO.read(expectedImageFile);
        Image expectedImage = new Image(expectedBufferedElementImage);


        when(element.getElementImage()).thenReturn(expectedImage);
        Image elementImage = element.getElementImage();

        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, expectedImage.equals(elementImage));
        assertTrue(RECEIVED_DIFFERENT_IMAGES_MESSAGE, elementImage.equals(expectedImage));
    }
}
