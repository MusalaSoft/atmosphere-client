package com.musala.atmosphere.client.util;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

/**
 * Locates the first class up in the stack trace that matches a given criteria.
 * 
 * @author yordan.petrov
 * 
 */
public class ClassLocator {
    private static final Logger LOGGER = Logger.getLogger(ClassLocator.class.getCanonicalName());

    private Class<?> desiredClass;

    /**
     * {@link ClassLocator} object that can locate a class matching a given relation to a given class.
     * 
     * @param desiredClass
     *        the class that will be used for relationship matching.
     */
    public ClassLocator(Class<?> desiredClass) {
        this.desiredClass = desiredClass;
    }

    /**
     * Locates the first class that is annotated by the desired class.
     * 
     * @return the first class that is annotated by the desired class; <code>null</code> when such can not be found or
     *         the desired class does not implement {@link Annotation}.
     */
    public Class<?> getFirstAnnotatedClass() {
        Class<? extends Annotation> annotationClass = null;
        try {
            annotationClass = (Class<? extends Annotation>) desiredClass;
        } catch (ClassCastException e) {
            String message = "Trying to get class annotated by a non-annotation class.";
            LOGGER.error(message, e);
            return null;
        }

        Exception exception = new Exception();
        StackTraceElement[] callerMethods = exception.getStackTrace();

        Class<?> annotatedClass = null;
        for (StackTraceElement callerMethod : callerMethods) {
            // Going up in the stack trace to see which class has the desired annotation.
            Class<?> callerClass = null;
            try {
                callerClass = Class.forName(callerMethod.getClassName());

                if (callerClass.isAnnotationPresent(annotationClass)) {
                    annotatedClass = callerClass;
                    break;
                }
            } catch (ClassNotFoundException e) {
                String message = String.format("Could not find class with name: %s", callerMethod.getClassName());
                LOGGER.error(message, e);
            }
        }

        return annotatedClass;
    }

    /**
     * Locates the first class that implements the desired class.
     * 
     * @return the first class that implements the desired class; <code>null</code> when such can not be found.
     */
    public Class<?> getFirstImplementingClass() {
        Exception exception = new Exception();
        StackTraceElement[] callerMethods = exception.getStackTrace();

        Class<?> implementingClass = null;
        for (StackTraceElement callerMethod : callerMethods) {
            // Going up in the stack trace to see which class has the desired annotation.
            try {
                Class<?> callerClass = Class.forName(callerMethod.getClassName());

                if (desiredClass.isAssignableFrom(callerClass)) {
                    implementingClass = callerClass;
                    break;
                }
            } catch (ClassNotFoundException e) {
                String message = String.format("Could not find class with name: %s", callerMethod.getClassName());
                LOGGER.error(message, e);
            }
        }

        return implementingClass;
    }

}
