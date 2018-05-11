// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 * @author yordan.petrov
 * 
 */
public class ClassLocatorTest {
    @Server(ip = "localhost", port = 69, connectionRetryLimit = 42)
    class AnnotatedClass {
        private ClassLocator annotationLocator;

        public AnnotatedClass() {
            annotationLocator = new ClassLocator(Server.class);
        }

        public Class<?> getFirstAnnotatedClass() {
            return annotationLocator.getFirstAnnotatedClass();
        }
    }

    class UnannotatedClass {
        private ClassLocator annotationLocator;

        public UnannotatedClass() {
            annotationLocator = new ClassLocator(Server.class);
        }

        public Class<?> getFirstAnnotatedClass() {
            return annotationLocator.getFirstAnnotatedClass();
        }
    }

    interface TestInterface {
    }

    class ImplementingInterfaceClass implements TestInterface {
        private ClassLocator annotationLocator;

        public ImplementingInterfaceClass() {
            annotationLocator = new ClassLocator(TestInterface.class);
        }

        public Class<?> getFirstImplementingClass() {
            return annotationLocator.getFirstImplementingClass();
        }
    }

    class NotImplementingInterfaceClass {
        private ClassLocator annotationLocator;

        public NotImplementingInterfaceClass() {
            annotationLocator = new ClassLocator(TestInterface.class);
        }

        public Class<?> getFirstImplementingClass() {
            return annotationLocator.getFirstImplementingClass();
        }
    }

    @Test
    public void testLocateAnnotatedClass() {
        AnnotatedClass annotatedClass = new AnnotatedClass();
        Class<?> foundClass = annotatedClass.getFirstAnnotatedClass();
        assertEquals("The located class does not match the annotated class.", foundClass, annotatedClass.getClass());
    }

    @Test
    public void testReturnNullWhenNoAnnotationPresent() {
        UnannotatedClass unannotatedClass = new UnannotatedClass();
        Class<?> foundClass = unannotatedClass.getFirstAnnotatedClass();
        assertNull("Found annotated class, but such is not present.", foundClass);
    }

    @Test
    public void testLocateImplementingClass() {
        ImplementingInterfaceClass implementingInterfaceClass = new ImplementingInterfaceClass();
        Class<?> foundClass = implementingInterfaceClass.getFirstImplementingClass();
        assertEquals("The located class does not match the implementing class.",
                     foundClass,
                     implementingInterfaceClass.getClass());
    }

    @Test
    public void testReturnNullWhenNoImplementingClassPresent() {
        NotImplementingInterfaceClass notImplementingInterfaceClass = new NotImplementingInterfaceClass();
        Class<?> foundClass = notImplementingInterfaceClass.getFirstImplementingClass();
        assertNull("Found implementing class, but such is not present.", foundClass);
    }
}
