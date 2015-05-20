package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelectionOption;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

public class UiElementFetchingTest {
    private static final String TEST_XML = "testXml.xml";

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
        Mockito.doReturn(screen).when(device).getActiveScreen();
    }

    @Test
    public void getByCSSTest() throws Exception {
        final String desiredElementClass = "android.widget.FrameLayout";
        final String desiredElementContentDescription = "derp";

        XmlNodeUiElement element = screen.getElementByCSS("hierarchy > *[class=" + desiredElementClass + "]");
        UiElementSelector selector = element.getElementSelector();

        assertEquals("Desired element was not fetched correctly.",
                     selector.getStringValue(CssAttribute.CONTENT_DESCRIPTION),
                     desiredElementContentDescription);
    }

    @Test
    public void getByXPathTest() throws Exception {
        final String desiredElementContentDescription = "derp";
        final String desiredElementClass = "android.widget.FrameLayout";

        XmlNodeUiElement element = screen.getElementByXPath("//hierarchy/*[@content-desc='"
                + desiredElementContentDescription + "']");
        UiElementSelector selector = element.getElementSelector();

        assertEquals("Desired element was not fetched correctly.",
                     selector.getStringValue(CssAttribute.CLASS_NAME),
                     desiredElementClass);
    }

    @Test
    public void getBySelectorTest() throws Exception {
        final String desiredElementContentDescription = "derp";
        final String desiredElementClass = "android.widget.FrameLayout";

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION,
                                       UiElementSelectionOption.EQUALS,
                                       desiredElementContentDescription);
        UiElement element = screen.getElement(selector);

        UiElementPropertiesContainer properties = element.getProperties();

        assertEquals("Desired element was not fetched correctly.", properties.getClassName(), desiredElementClass);
    }

    @Test(expected = MultipleElementsFoundException.class)
    public void multipleFoundBySelectorTest() throws Exception {
        final String desiredElementClass = "android.widget.FrameLayout";

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CLASS_NAME, UiElementSelectionOption.EQUALS, desiredElementClass);
        screen.getElement(selector);
    }

    @Test(expected = UiElementFetchingException.class)
    public void notFoundByXPathTest() throws Exception {
        final String desiredElementContentDescription = "derp";

        screen.getElementByXPath("//hierarchy/nonexistent[@content-desc='" + desiredElementContentDescription + "']");
    }

    @Test(expected = MultipleElementsFoundException.class)
    public void multipleFoundByCSSTest() throws Exception {
        screen.getElementByCSS("node");
    }

    @Test
    public void getElementsByCSSTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        final String desiredElementClass = "android.widget.FrameLayout";

        List<XmlNodeUiElement> elements = screen.getAllElementsByCSS("[class=" + desiredElementClass + "]");

        assertEquals("Desired elements were not fetched correctly.", elements.size(), 4);
    }

    @Test
    public void getElementsTest() throws UiElementFetchingException, XPathExpressionException, InvalidCssQueryException {
        final String desiredElementClass = "android.widget.FrameLayout";

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CLASS_NAME, UiElementSelectionOption.EQUALS, desiredElementClass);
        List<UiElement> elements = screen.getElements(selector);

        assertEquals("Desired elements were not fetched correctly.", elements.size(), 4);
    }

    @Test(expected = UiElementFetchingException.class)
    public void getElementsByCSSExceptionTest() throws Exception {
        screen.getElementByCSS("nonexistent");
    }

    @Test
    public void tapElementTest() throws Exception {
        final String desiredElementContentDescription = "derp";

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION,
                                       UiElementSelectionOption.EQUALS,
                                       desiredElementContentDescription);
        UiElement element = screen.getElement(selector);

        element.tap();

        verify(device, times(1)).tapScreenLocation(any(Point.class));
    }

    @Test
    public void keepBoundsUpperLeftCornerConstructorTest() {
        Point corner = new Point(0, 0);
        Bounds bounds = new Bounds(corner, 10, 10);
        corner.addVector(new Point(50, 50));
        Point boundsCorner = bounds.getUpperLeftCorner();
        assertFalse("Upper left corner of Bounds object was modified", corner.equals(boundsCorner));
    }

    @Test
    public void keepBoundsUpperLeftCornerTest() {
        Bounds bounds = new Bounds(new Point(0, 0), 10, 10);
        Point boundsCorner = bounds.getUpperLeftCorner();
        boundsCorner.addVector(new Point(50, 50));
        Point newBoundsCorner = bounds.getUpperLeftCorner();
        assertFalse("Upper left corner of Bounds object was modified", newBoundsCorner.equals(boundsCorner));
    }

    @Test
    public void tapElementWithTextTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        screen.tapElementWithText("CoolStory", 0);

        verify(device, times(1)).tapScreenLocation(any(Point.class));
    }

    @Test(expected = UiElementFetchingException.class)
    public void tapElementWithTextNotEnoughElementsTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        screen.tapElementWithText("CoolStory", 1);
    }

    @Test(expected = UiElementFetchingException.class)
    public void tapElementWithTextNoElementsTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        screen.tapElementWithText("CoolStory1");
    }

    @Test
    public void hasElementWithTextTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        assertTrue("Expected to find an element, but did not find one", screen.hasElementWithText("CoolStory"));
    }

    @Test
    public void hasElementWithTextNegativeTest()
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException {
        assertFalse("Expected not to find element, but found one", screen.hasElementWithText("CoolStory1"));
    }

    @Test
    public void testGetDirectChildren() throws Exception {

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CLASS_NAME, "android.widget.LinearLayout");
        Bounds boundsOfFatherElement = new Bounds(new Point(7, 19), new Point(105, 55));
        selector.addSelectionAttribute(CssAttribute.BOUNDS, boundsOfFatherElement);

        UiElement linearLayout = screen.getElement(selector);

        List<UiElement> children = linearLayout.getDirectChildren();
        assertEquals("Incorrect number of found children for empty selector.", 2, children.size());

        List<UiElement> secondLayerChildren = children.get(0).getDirectChildren();
        assertEquals("Incorrect number of found children for first direct ascendant of root node.",
                     1,
                     secondLayerChildren.size());
    }
}
