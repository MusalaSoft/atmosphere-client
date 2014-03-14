package com.musala.atmosphere.client;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Test;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.UiElementSelectionOption;
import com.musala.atmosphere.client.uiutils.UiElementSelector;

public class UiElementRevalidationTest {
    private static final String TEST_XML = "testXml.xml";

    private UiElement spyUiElement;

    private Screen uiScreen;

    @Test(expected = RuntimeException.class)
    public void testTapInvalidElement() throws UiElementFetchingException {
        initializeNonfocusableElement();
        doThrow(new RuntimeException()).when(spyUiElement).innerRevalidation();

        final int numberOfTaps = 10;

        for (int taps = 0; taps < numberOfTaps; taps++) {
            spyUiElement.tap();
        }

        verify(spyUiElement, times(1)).innerRevalidation();
    }

    @Test
    public void testTap() throws UiElementFetchingException {
        initializeNonfocusableElement();
        spyUiElement.tap();
        verify(spyUiElement, times(1)).innerRevalidation();
    }

    @Test
    public void testTapPoint() throws UiElementFetchingException {
        initializeNonfocusableElement();
        Point tappedPoint = new Point(12, 23);
        final int numberOfTaps = 10;

        for (int taps = 0; taps < numberOfTaps; taps++) {
            spyUiElement.tap(tappedPoint);
        }

        verify(spyUiElement, times(numberOfTaps)).innerRevalidation();
    }

    @Test(expected = RuntimeException.class)
    public void testClearTextInvalidElement() throws UiElementFetchingException {
        initializeNonfocusableElement();
        doThrow(new RuntimeException()).when(spyUiElement).innerRevalidation();
        spyUiElement.clearText();
    }

    @Test(expected = RuntimeException.class)
    public void testInputTextStringIntInvalidElement() throws UiElementFetchingException {
        initializeNonfocusableElement();
        doThrow(new RuntimeException()).when(spyUiElement).innerRevalidation();
        spyUiElement.inputText("sample message", 10);
    }

    private void initializeNonfocusableElement() throws UiElementFetchingException {

        uiScreen = readSampleScreen();

        final String desiredElementClass = "android.widget.FrameLayout";

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CLASS_NAME, UiElementSelectionOption.EQUALS, desiredElementClass);
        Point upperLeftCorner = new Point(0, 0);
        Point lowerRightCorner = new Point(240, 320);
        selector.addSelectionAttribute(CssAttribute.BOUNDS,
                                       UiElementSelectionOption.EQUALS,
                                       new Bounds(upperLeftCorner, lowerRightCorner));

        spyUiElement = spy(uiScreen.getElement(selector));
    }

    private Screen readSampleScreen() {
        InputStream testXmlInput = this.getClass().getResourceAsStream(TEST_XML);
        Scanner scanXml = new Scanner(testXmlInput);
        scanXml.useDelimiter("\\A"); // read all text regex pattern
        String xmlFileContents = scanXml.next();
        scanXml.close();
        Device mockDevice = mock(Device.class);

        Screen screen = new Screen(mockDevice, xmlFileContents);
        return screen;
    }

}
