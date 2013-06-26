package com.musala.atmosphere.client.device;

/**
 * This class simulates most basic gesture a user can do on the screen - drag his finger from point with coordinates <b>
 * (startX,startY) </b> to <b> (endX,endY) </b> for <b> executionTime </b> milliseconds. In particular, dragging from
 * (startX,startY) to (startX,startY) will be treated as tap or hold, depending on the value of <b>executionTime</b>.
 * 
 * 
 * @author vladimir.vladimirov
 * 
 */

public class SimpleGesture
{
	/**
	 * X-coordinate of the initial touch of the screen.
	 */
	private int startX;

	/**
	 * Y-coordinate of the initial touch of the screen.
	 */
	private int startY;

	/**
	 * X-coordinate of the position where the user's finger releases the screen.
	 */
	private int endX;

	/**
	 * Y-coordinate of the position where the user's finger releases the screen.
	 */
	private int endY;

	/**
	 * The time for which the user drags its finger from (startX,startY) to (endX,endY).
	 */
	private int executionTime;

	public int getStartX()
	{
		return startX;
	}

	public void setStartX(int startX)
	{
		this.startX = startX;
	}

	public int getStartY()
	{
		return startY;
	}

	public void setStartY(int startY)
	{
		this.startY = startY;
	}

	public int getEndX()
	{
		return endX;
	}

	public void setEndX(int endX)
	{
		this.endX = endX;
	}

	public int getEndY()
	{
		return endY;
	}

	public void setEndY(int endY)
	{
		this.endY = endY;
	}

	public int getExecutionTime()
	{
		return executionTime;
	}

	public void setExecutionTime(int executionTime)
	{
		this.executionTime = executionTime;
	}

}
