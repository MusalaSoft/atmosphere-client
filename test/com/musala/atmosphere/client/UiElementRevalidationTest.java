package com.musala.atmosphere.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.Scanner;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class UiElementRevalidationTest {
    // TODO: this class is no longer valid and should be removed.
    private static final String POPULATED_TEST_XML = "testXml.xml";

    private static final String UNPOPULATED_TEST_XML = "testXml2.xml";

    private Device usedDevice;

    private UiElementValidator validator;

    @Before
    public void setUp() {
        usedDevice = mock(Device.class);
        validator = new UiElementValidator();

        Mockito.when(usedDevice.getUiValidator()).thenReturn(validator);

        Screen screen = createScreen(POPULATED_TEST_XML);
        Mockito.when(usedDevice.getActiveScreen()).thenReturn(screen);
    }

    // @Test
    public void testRevalidationWithValidElement() throws Throwable {
        Mockito.when(usedDevice.getActiveScreen()).then(new Answer<Screen>() {
            @Override
            public Screen answer(InvocationOnMock invocation) throws Throwable {
                return createPopulatedScreen();
            }
        });

        Screen populated = createPopulatedScreen();

        UiElement element = populated.getElementByCSS("[text=CoolStory]");
        UiElement element2 = populated.getElementByCSS("[bounds=[0,55][240,320]][class=android.view.View]");

        assertTrue("Element revalidation should have resulted in element still present.", element.revalidate());
        assertTrue("Element revalidation should have resulted in element still present.", element2.revalidate());
    }

    // @Test
    public void testRevalidationWithInvalidElement() throws Throwable {
        Screen populated = createPopulatedScreen();

        UiElement element = populated.getElementByCSS("[text=CoolStory]");
        UiElement element2 = populated.getElementByCSS("[bounds=[0,55][240,320]][class=android.view.View]");

        Mockito.when(usedDevice.getActiveScreen()).then(new Answer<Screen>() {
            @Override
            public Screen answer(InvocationOnMock invocation) throws Throwable {
                return createUnpopulatedScreen();
            }
        });

        assertTrue("Element revalidation should have resulted in element still present.", element.revalidate());
        assertFalse("Element revalidation should have resulted in element not present anymore.", element2.revalidate());
    }

    // @Test(expected = StaleElementReferenceException.class)
    public void testCrossRevalidation() throws Throwable {
        Screen populated = createPopulatedScreen();
        UiElement element = populated.getElementByCSS("[text=CoolStory]");
        UiElement element2 = populated.getElementByCSS("[bounds=[0,55][240,320]][class=android.view.View]");

        Mockito.when(usedDevice.getActiveScreen()).then(new Answer<Screen>() {
            @Override
            public Screen answer(InvocationOnMock invocation) throws Throwable {
                return createUnpopulatedScreen();
            }
        });

        assertTrue("Element revalidation should have resulted in element still present.", element.revalidate());
        element2.tap(); // should be cross-revalidated and result in an exception.
    }

    private Screen createPopulatedScreen() {
        return createScreen(POPULATED_TEST_XML);
    }

    private Screen createUnpopulatedScreen() {
        return createScreen(UNPOPULATED_TEST_XML);
    }

    private Screen createScreen(String xmlName) {
        InputStream testXmlInput = this.getClass().getResourceAsStream(xmlName);
        Scanner scanXml = new Scanner(testXmlInput);
        scanXml.useDelimiter("\\A"); // read all text regex pattern
        String xmlFileContents = scanXml.next();
        scanXml.close();

        Screen screen = new Screen(usedDevice, xmlFileContents);
        return screen;
    }

}
