package com.musala.atmosphere.client.device.query;


public class ScreenTapQueryCreator
{
	public static String createQuery(int tapX, int tapY)
	{
		String query = "input tap " + tapX + " " + tapY;
		return query;
	}
}
