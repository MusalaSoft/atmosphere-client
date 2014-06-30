package com.musala.atmosphere.client.util;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.MissingServerAnnotationException;

/**
 * Container holding the properties of the first {@link Server} annotation found up in the stack trace.
 * 
 * @author yordan.petrov
 * 
 */
public class ServerAnnotationProperties extends ServerConnectionProperties {
    private static final Logger LOGGER = Logger.getLogger(ServerAnnotationProperties.class.getCanonicalName());

    private Server serverAnnotation;

    private Class<?> annotatedClass;

    /**
     * Finds the first {@link Server} annotation up in the stack trace and wraps it's properties.
     * 
     * @throws MissingServerAnnotationException
     *         when an annotated class can not be found.
     */
    public ServerAnnotationProperties() {
        ClassLocator annotationLocator = new ClassLocator(Server.class);
        annotatedClass = annotationLocator.getFirstAnnotatedClass();

        if (annotatedClass == null) {
            String errorMessgae = "The invoking class is missing a Server annotation.";

            LOGGER.fatal(errorMessgae);
            throw new MissingServerAnnotationException(errorMessgae);
        }

        serverAnnotation = annotatedClass.getAnnotation(Server.class);

        if (serverAnnotation == null) {
            String errorMessgae = "The invoking class is missing a @Server annotation.";

            LOGGER.fatal(errorMessgae);
            throw new MissingServerAnnotationException(errorMessgae);
        }

        this.serverIp = serverAnnotation.ip();
        this.serverPort = serverAnnotation.port();
        this.connectionRetryLimit = serverAnnotation.connectionRetryLimit();
    }

    /**
     * Returns the class the {@link Server} annotation was located on.
     * 
     * @return the class the {@link Server} annotation was located on.
     */
    public Class<?> getAnnotatedClass() {
        return annotatedClass;
    }
}
