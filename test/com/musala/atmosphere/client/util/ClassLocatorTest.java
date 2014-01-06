package com.musala.atmosphere.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.MissingServerAnnotationException;

/**
 * 
 * @author yordan.petrov
 * 
 */
public class ClassLocatorTest
{
	@Server(ip = "localhost", port = 69, connectionRetryLimit = 42)
	class AnnotatedClass
	{
		private ClassLocator annotationLocator;

		public AnnotatedClass()
		{
			annotationLocator = new ClassLocator(Server.class);
		}

		public Class<?> getFirstAnnotatedClass()
		{
			return annotationLocator.getFirstAnnotatedClass();
		}
	}

	class UnannotatedClass
	{
		private ClassLocator annotationLocator;

		public UnannotatedClass()
		{
			annotationLocator = new ClassLocator(Server.class);
		}

		public Class<?> getFirstAnnotatedClass()
		{
			return annotationLocator.getFirstAnnotatedClass();
		}
	}

	interface TestInterface
	{
	}

	class ImplementingInterfaceClass implements TestInterface
	{
		private ClassLocator annotationLocator;

		public ImplementingInterfaceClass()
		{
			annotationLocator = new ClassLocator(TestInterface.class);
		}

		public Class<?> getFirstImplementingClass()
		{
			return annotationLocator.getFirstImplementingClass();
		}
	}

	class NotImplementingInterfaceClass
	{
		private ClassLocator annotationLocator;

		public NotImplementingInterfaceClass()
		{
			annotationLocator = new ClassLocator(TestInterface.class);
		}

		public Class<?> getFirstImplementingClass()
		{
			return annotationLocator.getFirstImplementingClass();
		}
	}

	@Test
	public void testLocateAnnotatedClass()
	{
		AnnotatedClass annotatedClass = new AnnotatedClass();
		Class<?> foundClass = annotatedClass.getFirstAnnotatedClass();
		assertEquals("The located class does not match the annotated class.", foundClass, annotatedClass.getClass());
	}

	@Test
	public void testReturnNullWhenNoAnnotationPresent()
	{
		UnannotatedClass unannotatedClass = new UnannotatedClass();
		Class<?> foundClass = unannotatedClass.getFirstAnnotatedClass();
		assertNull("Found annotated class, but such is not present.", foundClass);
	}

	@Test
	public void testLocateImplementingClass()
	{
		ImplementingInterfaceClass implementingInterfaceClass = new ImplementingInterfaceClass();
		Class<?> foundClass = implementingInterfaceClass.getFirstImplementingClass();
		assertEquals(	"The located class does not match the implementing class.",
						foundClass,
						implementingInterfaceClass.getClass());
	}

	@Test
	public void testReturnNullWhenNoImplementingClassPresent()
	{
		NotImplementingInterfaceClass notImplementingInterfaceClass = new NotImplementingInterfaceClass();
		Class<?> foundClass = notImplementingInterfaceClass.getFirstImplementingClass();
		assertNull("Found implementing class, but such is not present.", foundClass);
	}
}
