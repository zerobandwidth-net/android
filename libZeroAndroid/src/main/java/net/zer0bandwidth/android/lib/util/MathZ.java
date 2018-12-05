package net.zer0bandwidth.android.lib.util;

/**
 * A logical extension of {@link java.lang.Math}.
 * @since zer0bandwidth-net/android 0.2.1 (#56)
 */
public final class MathZ
{
	/**
	 * Linearly searches arguments for the maximum value.
	 * @param ar a list of values
	 * @return the maximum value from the list
	 */
	public static double max( double... ar )
	{
		double rMax = Double.MIN_VALUE ;
		for( double r : ar )
		{ if( Double.compare( r, rMax ) > 0 ) rMax = r ; }
		return rMax ;
	}

	/**
	 * Linearly searches arguments for the maximum value.
	 * @param ar a list of values
	 * @return the maximum value from the list
	 */
	public static float max( float... ar )
	{
		float rMax = Float.MIN_VALUE ;
		for( float r : ar )
		{ if( Float.compare( r, rMax ) > 0 ) rMax = r ; }
		return rMax ;
	}

	/**
	 * Linearly searches arguments for the maximum value.
	 * @param az a list of values
	 * @return the maximum value from the list
	 */
	public static int max( int... az )
	{
		int zMax = Integer.MIN_VALUE ;
		for( int z : az )
		{ if( z > zMax ) zMax = z ; }
		return zMax ;
	}

	/**
	 * Linearly searches arguments for the maximum value.
	 * @param az a list of values
	 * @return the maximum value from the list
	 */
	public static long max( long... az )
	{
		long zMax = Long.MIN_VALUE ;
		for( long z : az )
		{ if( z > zMax ) zMax = z ; }
		return zMax ;
	}

	/**
	 * Linearly searches arguments for the minimum value.
	 * @param ar a list of values
	 * @return the minimum value from the list
	 */
	public static double min( double... ar )
	{
		double rMin = Double.MAX_VALUE ;
		for( double r : ar )
		{ if( Double.compare( r, rMin ) < 0 ) rMin = r ; }
		return rMin ;
	}

	/**
	 * Linearly searches arguments for the minimum value.
	 * @param ar a list of values
	 * @return the minimum value from the list
	 */
	public static float min( float... ar )
	{
		float rMin = Float.MAX_VALUE ;
		for( float r : ar )
		{ if( Float.compare( r, rMin ) < 0 ) rMin = r ; }
		return rMin ;
	}

	/**
	 * Linearly searches arguments for the minimum value.
	 * @param az a list of values
	 * @return the minimum value from the list
	 */
	public static int min( int... az )
	{
		int zMin = Integer.MAX_VALUE ;
		for( int z : az )
		{ if( z < zMin ) zMin = z ; }
		return zMin ;
	}

	/**
	 * Linearly searches arguments for the minimum value.
	 * @param az a list of values
	 * @return the minimum value from the list
	 */
	public static long min( long... az )
	{
		long zMin = Long.MAX_VALUE ;
		for( long z : az )
		{ if( z < zMin ) zMin = z ; }
		return zMin ;
	}

	/** Class should not be instantiated. */
	private MathZ() {}
}
