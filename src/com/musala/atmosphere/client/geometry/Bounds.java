package com.musala.atmosphere.client.geometry;

/**
 * Class representing bounds as a upper left corner point, width and height.
 * 
 * @author yordan.petrov
 * 
 */
public class Bounds
{
	private Point upperLeftCorner;

	private int width;

	private int height;

	/**
	 * Creates new Bounds instance given upper left corner, width and height.
	 * 
	 * @param upperLeftCorner
	 * @param width
	 * @param height
	 */
	public Bounds(Point upperLeftCorner, int width, int height)
	{
		this.upperLeftCorner = upperLeftCorner;
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates new Bounds instance given upper left and lower right corners.
	 * 
	 * @param upperLeftCorner
	 * @param lowerRightCorner
	 */
	public Bounds(Point upperLeftCorner, Point lowerRightCorner)
	{
		int calculatedWidth = lowerRightCorner.getX() - upperLeftCorner.getX();
		int calculatedHeight = lowerRightCorner.getY() - upperLeftCorner.getY();

		this.upperLeftCorner = upperLeftCorner;
		this.width = calculatedWidth;
		this.height = calculatedHeight;
	}

	/**
	 * Gets upper left corner.
	 * 
	 * @return
	 */
	public Point getUpperLeftCorner()
	{
		return upperLeftCorner;
	}

	/**
	 * Sets upper left corner.
	 * 
	 * @param upperLeftCorner
	 */
	public void setUpperLeftCorner(Point upperLeftCorner)
	{
		width += upperLeftCorner.getX() - this.upperLeftCorner.getX();
		height += upperLeftCorner.getY() - this.upperLeftCorner.getY();
		this.upperLeftCorner = upperLeftCorner;
	}

	/**
	 * Gets upper right corner.
	 * 
	 * @return
	 */
	public Point getUpperRightCorner()
	{
		int upperRightCornerX = upperLeftCorner.getX() + width;
		int upperRightCornerY = upperLeftCorner.getY();
		Point upperRightCorner = new Point(upperRightCornerX, upperRightCornerY);
		return upperRightCorner;
	}

	/**
	 * Gets lower left corner.
	 * 
	 * @return
	 */
	public Point getLowerLeftCorner()
	{
		int lowerLeftCornerX = upperLeftCorner.getX();
		int lowerLeftCornerY = upperLeftCorner.getY() + height;
		Point lowerLeftCorner = new Point(lowerLeftCornerX, lowerLeftCornerY);
		return lowerLeftCorner;
	}

	/**
	 * Gets lower right corner.
	 * 
	 * @return
	 */
	public Point getLowerRightCorner()
	{
		int lowerRightCornerX = upperLeftCorner.getX() + width;
		int lowerRightCornerY = upperLeftCorner.getY() + height;
		Point lowerRightCorner = new Point(lowerRightCornerX, lowerRightCornerY);
		return lowerRightCorner;
	}

	/**
	 * Gets bounds width.
	 * 
	 * @return
	 */
	public int getWidth()
	{
		return width;
	}

	/**
	 * Sets bounds width.
	 * 
	 * @param width
	 */
	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * Gets bounds height.
	 * 
	 * @return
	 */
	int getHeight()
	{
		return height;
	}

	/**
	 * Sets bounds height.
	 * 
	 * @param height
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	/**
	 * Gets bounds center.
	 * 
	 * @return
	 */
	public Point getCenter()
	{
		int centerX = upperLeftCorner.getX() + width / 2;
		int centerY = upperLeftCorner.getY() + height / 2;
		Point center = new Point(centerX, centerY);
		return center;
	}

	/**
	 * Gets bounds diagonal length.
	 * 
	 * @return
	 */
	public double getDiagonalLength()
	{
		int diagonalLengthSquare = width * width + height * height;
		double diagonalLength = Math.sqrt(diagonalLengthSquare);
		return diagonalLength;
	}

	/**
	 * Returns true if bounds contain point.
	 * 
	 * @param point
	 * @return
	 */
	public boolean contains(Point point)
	{
		Point lowerRightCorner = getLowerRightCorner();
		boolean containsPointX = point.getX() >= upperLeftCorner.getX() && point.getX() <= lowerRightCorner.getX();
		boolean containsPointY = point.getY() >= upperLeftCorner.getY() && point.getY() <= lowerRightCorner.getY();

		return containsPointX && containsPointY;
	}

	/**
	 * Returns true if bounds contain bounds.
	 * 
	 * @param bounds
	 * @return
	 */
	public boolean contains(Bounds bounds)
	{
		Point boundsUpperLeftCorner = bounds.getUpperLeftCorner();
		int boundsWidth = bounds.getWidth();
		int boundsHeight = bounds.getHeight();
		boolean containsUpperLeftCorner = contains(boundsUpperLeftCorner);
		boolean containsWidth = boundsUpperLeftCorner.getX() + boundsWidth <= upperLeftCorner.getX() + width;
		boolean containsHeight = boundsUpperLeftCorner.getY() + boundsHeight <= upperLeftCorner.getY() + height;

		return containsUpperLeftCorner && containsWidth && containsHeight;
	}

	/**
	 * Gets the given point equivalent relative to the bounds upper left corner.
	 * 
	 * @param point
	 * @return
	 */
	public Point getRelativePoint(Point point)
	{
		int relativePointX = point.getX() - upperLeftCorner.getX();
		int relativePointY = point.getY() - upperLeftCorner.getY();
		Point relativePoint = new Point(relativePointX, relativePointY);

		return relativePoint;
	}

	/**
	 * Gets the given bounds equivalent relative to the bounds upper left corner;
	 * 
	 * @param bounds
	 * @return
	 */
	public Bounds getRelativeBounds(Bounds bounds)
	{
		Point boundsUpperLeftCorner = bounds.getUpperLeftCorner();
		int boundsWidth = bounds.getWidth();
		int boundsHeight = bounds.getHeight();
		Point relativeUpperLeftCorner = getRelativePoint(boundsUpperLeftCorner);
		Bounds relativeBounds = new Bounds(relativeUpperLeftCorner, boundsWidth, boundsHeight);

		return relativeBounds;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Bounds other = (Bounds) obj;
		final Point otherUpperLeftCorner = other.getUpperLeftCorner();
		final int otherWidth = other.getWidth();
		final int otherHeight = other.getHeight();

		boolean isUpperLeftCornerEqual = upperLeftCorner.equals(otherUpperLeftCorner);
		boolean isWidthEqual = width == otherWidth;
		boolean isHeightEqual = height == otherHeight;
		boolean isEqual = isUpperLeftCornerEqual && isWidthEqual && isHeightEqual;

		return isEqual;
	}
}
