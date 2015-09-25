package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Before;
import org.mockito.Mockito;

public class UiElementEqualsTest {
    // TODO It's no longer possible to test this via unit test. All logic was moved to UiAutomator.

    private static final String TEST_XML = "testXml.xml";

    private static final String FIRST_CSS_QUERY = "[index=1][class=android.widget.FrameLayout][bounds=[0,55][240,320]]";

    private static final String FIRST_XPATH_QUERY = "//*[@class='android.widget.FrameLayout'][@index='1'][@bounds='[0,55][240,320]']";

    private static final String SECOND_CSS_QUERY = "[index=0][content-desc=derp]";

    private Device device;

    private Screen screen;

    @Before
    public void setUp() {
        device = mock(Device.class);
        UiElementValidator validator = new UiElementValidator();
        Mockito.when(device.getUiValidator()).thenReturn(validator);

        InputStream testXmlInput = this.getClass().getResourceAsStream(TEST_XML);
        Scanner scanXml = new Scanner(testXmlInput);
        scanXml.useDelimiter("\\A"); // read all text regex pattern
        String xmlFileContents = scanXml.next();
        scanXml.close();
        screen = new Screen(device, xmlFileContents);
        Mockito.when(device.getActiveScreen()).thenReturn(screen);
    }

    // @Test
    public void testTwoEqualUiElements() throws Exception {
        UiElement firstUiElement = screen.getElementByCSS(FIRST_CSS_QUERY);
        UiElement secondUiElement = screen.getElementByXPath(FIRST_XPATH_QUERY);

        assertEquals("UiElements are not equal.", firstUiElement, secondUiElement);
    }

    // @Test
    public void testTwoDifferentUiElements() throws Exception {
        UiElement firstUiElement = screen.getElementByCSS(FIRST_CSS_QUERY);
        UiElement secondUiElement = screen.getElementByCSS(SECOND_CSS_QUERY);

        assertFalse("UiElements are equal.", firstUiElement.equals(secondUiElement));
    }
}
