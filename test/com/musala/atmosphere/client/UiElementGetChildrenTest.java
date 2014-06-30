package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

public class UiElementGetChildrenTest {

    private static final String TEST_XML = "testXml3.xml";

    private static final String XPATH_SELECTOR_LINEAR_LAYOUT_PARENT = "//node[@class='android.widget.LinearLayout' and @package='com.example.coolstory' and @content-desc='derp']";

    private static final String XPATH_SELECTOR_IMAGE_VIEW = "//node[@class='android.widget.ImageView']";

    private static final String XPATH_SELECTOR_NONEXISTEND_NODE = "//node[@class='android.widget.ImageView' and @content-desc='nonexistent']";

    private static final String XPATH_SELECTOR_FRAME_LAYOUT = "//node[@index=1 and @class='android.widget.FrameLayout' and @package='com.example.coolstory']";

    private static final String XPATH_SELECTOR_FRAME_LAYOUT_FIRST_CHILD = "//node[@index=1 and @text='text' and @class='android.widget.FrameLayout' and @package='com.example.coolstory']";

    private static final String XPATH_SELECTOR_FRAME_LAYOUT_SECOND_CHILD = "//node[@index=1 and @text='' and @class='android.widget.FrameLayout' and @package='com.example.coolstory']";

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
        UiElement linearLayoutParent = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_PARENT);

        UiElement firstAndOnlyExpectedChild = screen.getElementByXPath(XPATH_SELECTOR_IMAGE_VIEW);
        List<UiElement> expectedChildrenList = new LinkedList<UiElement>();
        expectedChildrenList.add(firstAndOnlyExpectedChild);

        List<UiElement> returnedChildrenList = linearLayoutParent.getChildren(XPATH_SELECTOR_IMAGE_VIEW);
        assertEquals("The returned UiElements are not as expected", expectedChildrenList, returnedChildrenList);
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetChildrenGetParentNote() throws Exception {
        UiElement linearLayoutParentNode = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_PARENT);
        linearLayoutParentNode.getChildren(XPATH_SELECTOR_LINEAR_LAYOUT_PARENT);
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetChildrenNone() throws Exception {
        UiElement linearLayoutParent = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_PARENT);
        linearLayoutParent.getChildren(XPATH_SELECTOR_NONEXISTEND_NODE);
    }

    @Test
    public void testGetChildrenDifferentLevels() throws Exception {
        UiElement linearLayoutParent = screen.getElementByXPath(XPATH_SELECTOR_LINEAR_LAYOUT_PARENT);

        UiElement firstExpectedChild = screen.getElementByXPath(XPATH_SELECTOR_FRAME_LAYOUT_FIRST_CHILD);
        UiElement secondExpectedChild = screen.getElementByXPath(XPATH_SELECTOR_FRAME_LAYOUT_SECOND_CHILD);

        List<UiElement> ExpectedChildrenList = new LinkedList<UiElement>();
        ExpectedChildrenList.add(firstExpectedChild);
        ExpectedChildrenList.add(secondExpectedChild);

        List<UiElement> returnedChildrenList = linearLayoutParent.getChildren(XPATH_SELECTOR_FRAME_LAYOUT);
        assertEquals("The returned UiElements are not as expected", ExpectedChildrenList, returnedChildrenList);
    }
}
