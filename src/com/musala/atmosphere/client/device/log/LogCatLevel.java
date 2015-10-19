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
     * Sets the priority to fatal.
     */
    FATAL("F"),
    /**
     * Sets the priority to silent (highest).
     */
    SILENT("S");

    private String level;

    LogCatLevel(String level) {
        this.level = level;
    }

    /**
     * Gets the LogCat level combined with the given tag as a filter value.
     *
     * @param tag
     *        - tag to be used for filtering
     * @return tag:level filter
     */
    public String getLevelTagFilter(String tag) {
        return String.format(" %s:%s", tag, level);
    }

    /**
     * Gets the default value applied as a filter to the LogCat command.
     *
     * @return the value applied as a filter
     */
    public String getFilterValue() {
        return String.format(" *:%s", level);
    }

    @Override
    public String toString() {
        return level;
    }
}
