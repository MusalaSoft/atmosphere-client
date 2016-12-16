package com.musala.atmosphere.client.util;

import com.musala.atmosphere.client.device.log.LogCatLevel;

/**
 * TODO: Add a documentation.
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

    // private static final String DEFAULT_PATH = System.getProperty("user.dir") + System.getProperty("file.separator") + ClientConstants.DEAFAULT_LOG_FILENAME;

    // System.getProperty("file.separator")

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
     * TODO: Add a documentation
     *
     * @return
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * TODO: Add a documentation
     *
     * @return
     */
    public String getLocalOuputPath() {
        return this.localOuputPath;
    }

    /**
     * TODO: Add a documentation.
     *
     * @return
     */
    public LogCatLevel[] getFilter() {
        return this.filter;
    }

    public String getTag() {
        return this.tag;
    }
}
