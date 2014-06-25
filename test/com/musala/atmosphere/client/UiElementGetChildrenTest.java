package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

public class UiElementGetChildrenTest {

    private static final String TEST_XML = "testXml3.xml";

    private static final String XPATH_SELECTOR_LINEAR_LAYOUT_NOTE = "//node[@class='android.widget.LinearLayout' and @package='com.example.coolstory' and @content-desc='derp']";

    private Screen screen;

    private Device device;

    @Before
    public void setUp() throws Exception {
        device = mock(Device.class);
        UiElementValidator validator = new UiElementValidator();
        Mockito.when(device.getUiValidator()).thenReturn(validator);
        InputStream testXmlInput = this.getClass().getResourceAsStream(TEST_XML);
        Scanner scanXml = new Scanner(testXmlInput);
        scanXml.useDelimiter("\\A"); // read all text regex pattern
        String xmlFileContents = scanXml.next();
        scanXml.close();
        screen = new Screen(device, xmlFileContents);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetChildrenByxPathSelector() throws Exception {
        UiElement linearLayout = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_NOTE);
        final int EXPECTED_CHILDREN = 1;
        String xPathSelectorChildren = "//node[@class='android.widget.ImageView']";
        List<UiElement> children = linearLayout.getChildren(xPathSelectorChildren);
        assertEquals("Incorrect number of found children for xPath.", EXPECTED_CHILDREN, children.size());
        final int EXPECTED_CHILDREN_FRAME = 2;
        String xPathelectorChildrenFrame = "//node[@text='text' and @class='android.widget.FrameLayout']";
        List<UiElement> childrenFrame = linearLayout.getChildren(xPathelectorChildrenFrame);
        assertEquals("Incorrect number of found children for xPath", EXPECTED_CHILDREN_FRAME, childrenFrame.size());
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetChildrenGetParentNote() throws Exception {
        UiElement linearLayout = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_NOTE);
        final int EXPECTED_CHILDREN = 0;
        List<UiElement> children = linearLayout.getChildren(XPATH_SELECTOR_LINEAR_LAYOUT_NOTE);
        assertEquals("Incorrect number of found children for xPath.", EXPECTED_CHILDREN, children.size());
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetChildrenNone() throws Exception {
        UiElement linearLayout = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_NOTE);

        String xPathSelectorChildren = "//node[@class='android.widget.ImageView' and @content-desc='nonexistent']";
        linearLayout.getChildren(xPathSelectorChildren);
    }

    @Test
    public void testGetChildrenDifferentLevels() throws Exception {
        UiElement linearLayout = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_NOTE);
        final int EXPECTED_CHILDREN = 8;
        String xPathSelectorChildren = "//node[@class='android.widget.FrameLayout' and @package='com.example.coolstory']";
        List<UiElement> children = linearLayout.getChildren(xPathSelectorChildren);
        assertEquals("Incorrect number of found children for xPath.", EXPECTED_CHILDREN, children.size());

    }
}
