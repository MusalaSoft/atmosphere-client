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

import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.UiElementSelector;

public class UiCollectionFetchingTest {

    private static final String TEST_XML = "testXml3.xml";

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
    public void testGetDirectChildren() throws Exception {

        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.CLASS_NAME, "android.widget.LinearLayout");
        selector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION, "collection");
        selector.addSelectionAttribute(CssAttribute.TEXT, "MainViewGroup");

        UiCollection linearLayout = screen.getCollectionBySelector(selector);

        List<UiElement> children = linearLayout.getDirectChildren();
        assertEquals("Incorrect number of found children for empty selector.", 7, children.size());

        List<UiElement> secondLayerChildren = (new UiCollection(children.get(0))).getDirectChildren();
        assertEquals("Incorrect number of found children for first direct ascendant of root node.",
                     1,
                     secondLayerChildren.size());
    }
}
