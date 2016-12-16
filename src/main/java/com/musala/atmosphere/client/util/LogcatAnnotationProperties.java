package com.musala.atmosphere.client.util;

import com.musala.atmosphere.client.device.log.LogCatLevel;

/**
 * Container holding the properties of the first {@link Logcat} annotation found up in the stack trace.
 *
 * @author dimcho.nedev
 *
 */
public class LogcatAnnotationProperties {
    private Logcat logcatAnnotation;

    private Class<?> annotatedClass;

    private boolean enabled = false;

    private String localOuputPath;

    private LogCatLevel[] filter;

    private String tag;

    public LogcatAnnotationProperties() {
        ClassLocator annotationLocator = new ClassLocator(Logcat.class);
        annotatedClass = annotationLocator.getFirstAnnotatedClass();
        if (annotatedClass != null) {
            enabled = true;
            logcatAnnotation = annotatedClass.getAnnotation(Logcat.class);
            this.localOuputPath = logcatAnnotation.localOuputPath();
            this.filter = logcatAnnotation.filter();
            this.localOuputPath = logcatAnnotation.localOuputPath();
            this.tag = logcatAnnotation.tag();
        }
    }

    /**
     * If LogCat is enabled
     *
     * @return <code>true</code> if a LogCat annotation is found, otherwise returns <code>false</code>.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Gets the path to the output folder.
     *
     * @return String path
     */
    public String getLocalOuputPath() {
        return this.localOuputPath;
    }

    /**
     * Gets the log level filters.
     *
     * @return an array of log level filters
     */
    public LogCatLevel[] getLogCatLevel() {
        return this.filter;
    }

    /**
     * Gets the tag filter.
     *
     * @return String, tag filter
     */
    public String getTag() {
        return this.tag;
    }
}
