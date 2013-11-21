package com.musala.atmosphere.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.UiElementSelector;

public class UiElementFetchingTest
{
	private static final String TEST_XML = "testXml.xml";

	private Screen screen;

	private Device device;

	@Before
	public void setUp() throws Exception
	{
		device = mock(Device.class);
		InputStream testXmlInput = this.getClass().getResourceAsStream(TEST_XML);
		Scanner scanXml = new Scanner(testXmlInput);
		scanXml.useDelimiter("\\A"); // read all text regex pattern
		String xmlFileContents = scanXml.next();
		scanXml.close();
		screen = new Screen(device, xmlFileContents);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void getByCSSTest() throws UiElementFetchingException
	{
		final String desiredElementClass = "android.widget.FrameLayout";
		final String desiredElementContentDescription = "derp";

		UiElement element = screen.getElementByCSS("hierarchy > *[class=" + desiredElementClass + "]");
		UiElementAttributes attributes = element.getElementAttributes(false);

		assertEquals(	"Desired element was not fetched correctly.",
						attributes.getContentDescription(),
						desiredElementContentDescription);
	}

	@Test
	public void getByXPathTest() throws UiElementFetchingException, XPathExpressionException
	{
		final String desiredElementContentDescription = "derp";
		final String desiredElementClass = "android.widget.FrameLayout";

		UiElement element = screen.getElementByXPath("//hierarchy/*[@content-desc='" + desiredElementContentDescription
				+ "']");
		UiElementAttributes attributes = element.getElementAttributes(false);

		assertEquals("Desired element was not fetched correctly.", attributes.getClassName(), desiredElementClass);
	}

	@Test
	public void getBySelectorTest() throws UiElementFetchingException
	{
		final String desiredElementContentDescription = "derp";
		final String desiredElementClass = "android.widget.FrameLayout";

		UiElementSelector selector = new UiElementSelector();
		selector.setContentDescription(desiredElementContentDescription);
		UiElement element = screen.getElement(selector);
		UiElementAttributes attributes = element.getElementAttributes(false);

		assertEquals("Desired element was not fetched correctly.", attributes.getClassName(), desiredElementClass);
	}

	@Test(expected = UiElementFetchingException.class)
	public void multipleFoundBySelectorTest() throws UiElementFetchingException
	{
		final String desiredElementClass = "android.widget.FrameLayout";

		UiElementSelector selector = new UiElementSelector();
		selector.setClassName(desiredElementClass);
		UiElement element = screen.getElement(selector);
	}

	@Test(expected = UiElementFetchingException.class)
	public void notFoundByXPathTest() throws UiElementFetchingException, XPathExpressionException
	{
		final String desiredElementContentDescription = "derp";

		UiElement element = screen.getElementByXPath("//hierarchy/nonexistent[@content-desc='"
				+ desiredElementContentDescription + "']");
	}

	@Test(expected = UiElementFetchingException.class)
	public void multipleFoundByCSSTest() throws UiElementFetchingException
	{
		UiElement element = screen.getElementByCSS("node");
	}

	@Test
	public void getElementsByCSSTest() throws UiElementFetchingException
	{
		final String desiredElementClass = "android.widget.FrameLayout";

		List<UiElement> elements = screen.getElementsByCSS("[class=" + desiredElementClass + "]");

		assertEquals("Desired elements were not fetched correctly.", elements.size(), 4);
	}

	@Test
	public void getElementsTest() throws UiElementFetchingException
	{
		final String desiredElementClass = "android.widget.FrameLayout";

		UiElementSelector selector = new UiElementSelector();
		selector.setClassName(desiredElementClass);
		List<UiElement> elements = screen.getElements(selector);

		assertEquals("Desired elements were not fetched correctly.", elements.size(), 4);
	}

	@Test(expected = UiElementFetchingException.class)
	public void getElementsByCSSExceptionTest() throws UiElementFetchingException
	{
		UiElement element = screen.getElementByCSS("nonexistent");
	}

	@Test
	public void tapElementTest() throws UiElementFetchingException
	{
		final String desiredElementContentDescription = "derp";

		UiElementSelector selector = new UiElementSelector();
		selector.setContentDescription(desiredElementContentDescription);
		UiElement element = screen.getElement(selector);

		element.tap(false);

		verify(device, times(1)).tapScreenLocation(any(Point.class));
	}
}
