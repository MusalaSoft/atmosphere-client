package com.musala.atmosphere.client.util;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.MissingServerConnectionProperiesException;

/**
 * Container holding the properties of the first {@link Server} annotation found up in the stack trace.
 *
 * @author yordan.petrov
 *
 */
public class ServerAnnotationProperties extends ServerConnectionProperties {
    private static final Logger LOGGER = Logger.getLogger(ServerAnnotationProperties.class.getCanonicalName());

    private static final String LOGGER_ERROR_MESSAGE = "The invoking class is missing a @Server annotation or config.properties in the test project working directory.";

    private Server serverAnnotation;

    private Class<?> annotatedClass;

    /**
     * Finds the first {@link Server} annotation up in the stack trace and wraps it's properties.
     *
     * @throws MissingServerConnectionProperiesException
     *         when an annotated class can not be found and a config.properties file does not exist
     */
    public ServerAnnotationProperties() {
        ClassLocator annotationLocator = new ClassLocator(Server.class);
        annotatedClass = annotationLocator.getFirstAnnotatedClass();

        if (annotatedClass == null && !ConfigurationPropertiesLoader.isConfigExists()) {
            LOGGER.fatal(LOGGER_ERROR_MESSAGE);
            throw new MissingServerConnectionProperiesException(LOGGER_ERROR_MESSAGE);
        } else if (annotatedClass != null){
            serverAnnotation = annotatedClass.getAnnotation(Server.class);

            this.serverIp = serverAnnotation.ip();
            this.serverPort = serverAnnotation.port();
            this.connectionRetryLimit = serverAnnotation.connectionRetryLimit();
        }
    }

    /**
     * Returns the class the {@link Server} annotation was located on.
     *
     * @return the class the {@link Server} annotation was located on.
     */
    public Class<?> getAnnotatedClass() {
        return annotatedClass;
    }

    public boolean isServerAnnotationExists() {
        return serverAnnotation != null;
    }

}
