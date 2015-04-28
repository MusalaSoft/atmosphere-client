package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

public class UiElementGetChildrenBySelectorTest {

    private static final String TEST_XML = "testXml.xml";

    private static final String XPATH_QUERY_FOR_PARENT_ELEMENT = "//*[@bounds='[7,19][105,55]']";

    private static final String PACKAGE_NAME_FOR_CHILDREN_ELEMENTS = "com.example.coolstory";

    private static final String PACKAGE_NAME_FOR_UNEXISTING_CHILDREN_ELEMENTS = "non-existing-package";

    private static final String CSS_QUERY_FOR_FIRST_CHILD = "[bounds=[7,19][37,55]][class=android.widget.FrameLayout]";

    private static final String CSS_QUERY_FOR_SECOND_CHILD = "[bounds=[10,25][34,49]][class=android.widget.ImageView]";

    private static final String CSS_QUERY_FOR_THIRD_CHILD = "[bounds=[37,19][105,55]][class=android.widget.LinearLayout]";

    private static final String CSS_QUERY_FOR_FOURTH_CHILD = "[bounds=[37,27][99,46]][class=android.widget.LinearLayout]";

    private static final String CSS_QUERY_FOR_FIFTH_CHILD = "[bounds=[37,27][99,46]][class=android.widget.TextView]";

    private static final String CONTENT_DESCRIPTION_FOR_NON_CHILDREN_ELEMENT = "derp";

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
    }

    @Test
    public void testGetExistingChildrenBySelector() throws Exception {
        UiElement parentUiElement = screen.getElementByXPath(XPATH_QUERY_FOR_PARENT_ELEMENT);

        UiElementSelector childrenUiElementsSelector = new UiElementSelector();
        childrenUiElementsSelector.addSelectionAttribute(CssAttribute.PACKAGE_NAME, PACKAGE_NAME_FOR_CHILDREN_ELEMENTS);

        List<UiElement> childrenUiElements = parentUiElement.getChildrenBySelector(childrenUiElementsSelector);

        UiElement firstExpectedChild = screen.getElementByCSS(CSS_QUERY_FOR_FIRST_CHILD);
        UiElement secondExpectedChild = screen.getElementByCSS(CSS_QUERY_FOR_SECOND_CHILD);
        UiElement thirdExpectedChild = screen.getElementByCSS(CSS_QUERY_FOR_THIRD_CHILD);
        UiElement fourthExpectedChild = screen.getElementByCSS(CSS_QUERY_FOR_FOURTH_CHILD);
        UiElement fifthExpectedChild = screen.getElementByCSS(CSS_QUERY_FOR_FIFTH_CHILD);

        List<UiElement> expectedChildren = Arrays.asList(firstExpectedChild,
                                                         secondExpectedChild,
                                                         thirdExpectedChild,
                                                         fourthExpectedChild,
                                                         fifthExpectedChild);

        assertEquals("The returned children are not the right one", childrenUiElements, expectedChildren);
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetUnexistingChildrenBySelector() throws Exception {
        UiElement parentUiElement = screen.getElementByXPath(XPATH_QUERY_FOR_PARENT_ELEMENT);

        UiElementSelector childrenUiElementsSelector = new UiElementSelector();
        childrenUiElementsSelector.addSelectionAttribute(CssAttribute.PACKAGE_NAME,
                                                         PACKAGE_NAME_FOR_UNEXISTING_CHILDREN_ELEMENTS);

        parentUiElement.getChildrenBySelector(childrenUiElementsSelector);
    }

    @Test(expected = UiElementFetchingException.class)
    public void testGetChildrenBySelectorForNonChildrenElement() throws Exception {
        UiElement parentUiElement = screen.getElementByXPath(XPATH_QUERY_FOR_PARENT_ELEMENT);

        UiElementSelector childrenUiElementsSelector = new UiElementSelector();
        childrenUiElementsSelector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION,
                                                         CONTENT_DESCRIPTION_FOR_NON_CHILDREN_ELEMENT);

        parentUiElement.getChildrenBySelector(childrenUiElementsSelector);
    }
}
