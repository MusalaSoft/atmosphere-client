package com.musala.atmosphere.client.util.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.commons.RoutingAction;

/**
 * Provides better interface for getting and inserting all kinds of Android device settings.
 * 
 * @author nikola.taushanov
 * 
 */
public class DeviceSettingsManager {
    private DeviceCommunicator communicator;

    public DeviceSettingsManager(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Retrieves a single setting value as floating point number or returns default value if it is not found.
     * 
     * @param setting
     * @param defaultValue
     * @return
     */

    public float getFloat(IAndroidSettings setting, float defaultValue) {
        try {
            float result = getFloat(setting);
            return result;
        } catch (SettingsParsingException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a single setting value as floating point number.
     * 
     * @param setting
     * @return
     * @throws SettingsParsingException
     */
    public float getFloat(IAndroidSettings setting) throws SettingsParsingException {
        String settingStringValue = getSetting(setting);
        try {
            float settingValue = Float.parseFloat(settingStringValue);
            return settingValue;
        } catch (NumberFormatException e) {
            throw new SettingsParsingException(e.getMessage());
        }
    }

    /**
     * Retrieves a single setting value as integer or returns default value if it is not found.
     * 
     * @param setting
     * @param defaultValue
     * @return the value of the setting or the default value.
     */
    public int getInt(IAndroidSettings setting, int defaultValue) {
        try {
            int result = getInt(setting);
            return result;
        } catch (SettingsParsingException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a single setting value as integer.
     * 
     * @param setting
     * @return
     * @throws SettingsParsingException
     */
    public int getInt(IAndroidSettings setting) throws SettingsParsingException {
        String settingStringValue = getSetting(setting);
        try {
            int settingValue = Integer.parseInt(settingStringValue);
            return settingValue;
        } catch (NumberFormatException e) {
            throw new SettingsParsingException(e.getMessage());
        }
    }

    /**
     * Retrieves a single setting value as long or returns default value if it is not found.
     * 
     * @param setting
     * @param defaultValue
     * @return the value of the setting or the default value.
     */
    public long getLong(IAndroidSettings setting, long defaultValue) {
        try {
            long result = getLong(setting);
            return result;
        } catch (SettingsParsingException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves a single setting value as long.
     * 
     * @param setting
     * @return the value of the setting.
     * @throws SettingsParsingExceptionn
     */
    public long getLong(IAndroidSettings setting) throws SettingsParsingException {
        String settingStringValue = getSetting(setting);
        try {
            long settingValue = Long.parseLong(settingStringValue);
            return settingValue;
        } catch (NumberFormatException e) {
            throw new SettingsParsingException(e.getMessage());
        }
    }

    /**
     * Retrieves a single setting value as String or returns default value if it is not found.
     * 
     * @param setting
     * @return the string value of the setting or <code>null</code> if the fetching was not successful.
     */
    public String getString(IAndroidSettings setting, String defaultValue) {
        String settingValue = getSetting(setting);

        if (settingValue != null) {
            return settingValue;
        } else {
            return defaultValue;
        }
    }

    /**
     * Retrieves a single setting value as String.
     * 
     * @param setting
     * @return the string value of the setting or <code>null</code> if the fetching was not successful.
     */
    public String getString(IAndroidSettings setting) {
        String settingValue = getSetting(setting);
        return settingValue;
    }

    /**
     * Updates a single settings value as a floating point number.
     * 
     * @param setting
     * @param value
     * @result boolean indicating whether the updating was successful.
     */
    public boolean putFloat(IAndroidSettings setting, float value) {
        return putSetting(setting, "f", Float.toString(value));
    }

    /**
     * Updates a single settings value as integer.
     * 
     * @param setting
     * @param value
     * @result boolean indicating whether the updating was successful.
     */
    public boolean putInt(IAndroidSettings setting, int value) {
        return putSetting(setting, "i", Integer.toString(value));
    }

    /**
     * Updates a single settings value as long.
     * 
     * @param setting
     * @param value
     * @result boolean indicating whether the updating was successful.
     */
    public boolean putLong(IAndroidSettings setting, long value) {
        return putSetting(setting, "l", Long.toString(value));
    }

    /**
     * Updates a single settings value as String.
     * 
     * @param setting
     * @param value
     * @result boolean indicating whether the updating was successful.
     */
    public boolean putString(IAndroidSettings setting, String value) {
        return putSetting(setting, "s", value);
    }

    private String getSetting(IAndroidSettings setting) {
        StringBuilder contentShellCommand = new StringBuilder();
        contentShellCommand.append("content query --uri " + setting.getContentUri());
        contentShellCommand.append(" --projection value");
        contentShellCommand.append(" --where \"name=\'" + setting + "\'\"");

        String shellCommandResult = "";
        shellCommandResult = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND,
                                                              contentShellCommand.toString());
        if (communicator.getLastException() != null) {
            return null;
        }

        Pattern returnValuePattern = Pattern.compile("value=(.*)$");
        Matcher returnValueMatcher = returnValuePattern.matcher(shellCommandResult);

        if (returnValueMatcher.find()) {
            return returnValueMatcher.group(1);
        } else {
            return null;
        }
    }

    private boolean putSetting(IAndroidSettings setting, String valueType, String value) {
        StringBuilder contentShellCommand = new StringBuilder();
        contentShellCommand.append("content insert --uri " + setting.getContentUri());
        contentShellCommand.append(" --bind name:s:" + setting);
        contentShellCommand.append(" --bind value:" + valueType + ":" + value);

        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, contentShellCommand.toString());
        return communicator.getLastException() == null;
    }
}
