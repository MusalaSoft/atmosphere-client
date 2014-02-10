package com.musala.atmosphere.client.util.settings;

import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

/**
 * Provides better interface for getting and inserting all kinds of Android device settings.
 * 
 * @author nikola.taushanov
 * 
 */
public class DeviceSettingsManager
{
	private IClientDevice wrappedDevice;

	private long invocationPasskey;

	public DeviceSettingsManager(IClientDevice wrappedDevice, long invocationPasskey)
	{
		this.wrappedDevice = wrappedDevice;
		this.invocationPasskey = invocationPasskey;
	}

	/**
	 * Retrieves a single setting value as floating point number or returns default value if it is not found.
	 * 
	 * @param setting
	 * @param defaultValue
	 * @return
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */

	public float getFloat(IAndroidSettings setting, float defaultValue)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		try
		{
			float result = getFloat(setting);
			return result;
		}
		catch (SettingsParsingException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves a single setting value as floating point number.
	 * 
	 * @param setting
	 * @return
	 * @throws SettingsParsingException
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public float getFloat(IAndroidSettings setting)
		throws SettingsParsingException,
			RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		String settingStringValue = getSetting(setting);
		try
		{
			float settingValue = Float.parseFloat(settingStringValue);
			return settingValue;
		}
		catch (NumberFormatException e)
		{
			throw new SettingsParsingException(e.getMessage());
		}
	}

	/**
	 * Retrieves a single setting value as integer or returns default value if it is not found.
	 * 
	 * @param setting
	 * @param defaultValue
	 * @return
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public int getInt(IAndroidSettings setting, int defaultValue)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		try
		{
			int result = getInt(setting);
			return result;
		}
		catch (SettingsParsingException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves a single setting value as integer.
	 * 
	 * @param setting
	 * @return
	 * @throws SettingsParsingException
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public int getInt(IAndroidSettings setting)
		throws SettingsParsingException,
			RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		String settingStringValue = getSetting(setting);
		try
		{
			int settingValue = Integer.parseInt(settingStringValue);
			return settingValue;
		}
		catch (NumberFormatException e)
		{
			throw new SettingsParsingException(e.getMessage());
		}
	}

	/**
	 * Retrieves a single setting value as long or returns default value if it is not found.
	 * 
	 * @param setting
	 * @param defaultValue
	 * @return
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public long getLong(IAndroidSettings setting, long defaultValue)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		try
		{
			long result = getLong(setting);
			return result;
		}
		catch (SettingsParsingException e)
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves a single setting value as long.
	 * 
	 * @param setting
	 * @return
	 * @throws SettingsParsingException
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public long getLong(IAndroidSettings setting)
		throws SettingsParsingException,
			RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		String settingStringValue = getSetting(setting);
		try
		{
			long settingValue = Long.parseLong(settingStringValue, 0);
			return settingValue;
		}
		catch (NumberFormatException e)
		{
			throw new SettingsParsingException(e.getMessage());
		}
	}

	/**
	 * Retrieves a single setting value as String or returns default value if it is not found.
	 * 
	 * @param setting
	 * @return
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public String getString(IAndroidSettings setting, String defaultValue)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		String settingValue = getSetting(setting);

		if (settingValue != null)
		{
			return settingValue;
		}
		else
		{
			return defaultValue;
		}
	}

	/**
	 * Retrieves a single setting value as String.
	 * 
	 * @param setting
	 * @return
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public String getString(IAndroidSettings setting)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		String settingValue = getSetting(setting);
		return settingValue;
	}

	/**
	 * Updates a single settings value as a floating point number.
	 * 
	 * @param setting
	 * @param value
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public void putFloat(IAndroidSettings setting, float value)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		putSetting(setting, "f", Float.toString(value));
	}

	/**
	 * Updates a single settings value as integer.
	 * 
	 * @param setting
	 * @param value
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public void putInt(IAndroidSettings setting, int value)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		putSetting(setting, "i", Integer.toString(value));
	}

	/**
	 * Updates a single settings value as long.
	 * 
	 * @param setting
	 * @param value
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public void putLong(IAndroidSettings setting, long value)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		putSetting(setting, "l", Long.toString(value));
	}

	/**
	 * Updates a single settings value as String.
	 * 
	 * @param setting
	 * @param value
	 * @throws CommandFailedException
	 * @throws InvalidPasskeyException
	 * @throws RemoteException
	 */
	public void putString(IAndroidSettings setting, String value)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		putSetting(setting, "s", value);
	}

	private String getSetting(IAndroidSettings setting)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		StringBuilder contentShellCommand = new StringBuilder();
		contentShellCommand.append("content query --uri " + setting.getContentUri());
		contentShellCommand.append(" --projection value");
		contentShellCommand.append(" --where \"name=\'" + setting + "\'\"");

		String shellCommandResult = "";
		shellCommandResult = wrappedDevice.executeShellCommand(contentShellCommand.toString(), invocationPasskey);

		Pattern returnValuePattern = Pattern.compile("value=(.*)$");
		Matcher returnValueMatcher = returnValuePattern.matcher(shellCommandResult);

		if (returnValueMatcher.find())
		{
			return returnValueMatcher.group(1);
		}
		else
		{
			return null;
		}
	}

	private void putSetting(IAndroidSettings setting, String valueType, String value)
		throws RemoteException,
			CommandFailedException,
			InvalidPasskeyException
	{
		StringBuilder contentShellCommand = new StringBuilder();
		contentShellCommand.append("content insert --uri " + setting.getContentUri());
		contentShellCommand.append(" --bind name:s:" + setting);
		contentShellCommand.append(" --bind value:" + valueType + ":" + value);

		wrappedDevice.executeShellCommand(contentShellCommand.toString(), invocationPasskey);
	}
}
