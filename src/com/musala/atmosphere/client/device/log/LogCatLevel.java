package com.musala.atmosphere.client.device.log;

/**
 * Enumerates all the available Android LogCat levels. Supports the option for filtering both by tag and level, given as
 * priority/tag pair in the device logs. After setting the log level, only logs with higher priority will be displayed.
 *
 * @author filareta.yordanova
 *
 */
public enum LogCatLevel {
    /**
     * Sets the priority to verbose (lowest).
     **/
    VERBOSE("V"),
    /**
     * Sets the priority to debug.
     */
    DEBUG("D"),
    /**
     * Sets the priority to info.
     */
    INFO("I"),
    /**
     * Sets the priority to warning.
     */
    WARNING("W"),
    /**
     * Sets the priority to error.
     */
    ERROR("E"),
    /**
     * Sets the priority to failure.
     */
    FATAL("F"),
    /**
     * Sets the priority to silent (highest).
     */
    SILENT("S");

    private String level;

    private String tag;

    LogCatLevel(String level) {
        this.level = level;
        this.tag = "*";
    }

    /**
     * Sets the tag, which will be used in a combination with this log level. By default the tag is '*'.
     *
     * @param tag
     *        - tag to be set for filtering used with the current level
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Gets the value applied as a filter to the LogCat command.
     *
     * @return the value applied as a filter
     */
    public String getFilterValue() {
        return String.format(" %s:%s", tag, level);
    }
}
