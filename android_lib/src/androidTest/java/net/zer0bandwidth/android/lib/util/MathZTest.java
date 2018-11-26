package net.zer0bandwidth.android.lib.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Exercises {@link MathZ}.
 * @since zer0bandwidth-net/android 0.2.1 (#56)
 */
@RunWith( JUnit4.class )
public class MathZTest
{
	public static final double DOUBLE_EPSILON = 1.0e-16d ;

	public static final float FLOAT_EPSILON = 1.0e-16f ;

	@Test
	public void testMaxDouble()
	{
		double[] ar = new double[] { 8.0d, 6.0d, 7.0d, 5.0d, 3.0d, 0.0d, 9.0d };
		assertEquals( 9.0d, MathZ.max(ar), DOUBLE_EPSILON ) ;
	}

	@Test
	public void testMaxFloat()
	{
		float[] ar = new float[] { 8.0f, 6.0f, 7.0f, 5.0f, 3.0f, 0.0f, 9.0f } ;
		assertEquals( 9.0f, MathZ.max(ar), FLOAT_EPSILON ) ;
	}

	@Test
	public void testMaxInt()
	{ assertEquals( 9, MathZ.max( 8, 6, 7, 5, 3, 0, 9 ) ) ; }

	@Test
	public void testMaxLong()
	{ assertEquals( 9L, MathZ.max( 8L, 6L, 7L, 5L, 3L, 0L, 9L ) ) ; }

	@Test
	public void testMinDouble()
	{
		double[] ar = new double[] { 8.0d, 6.0d, 7.0d, 5.0d, 3.0d, 0.0d, 9.0d };
		assertEquals( 0.0d, MathZ.min(ar), DOUBLE_EPSILON ) ;
	}

	@Test
	public void testMinFloat()
	{
		float[] ar = new float[] { 8.0f, 6.0f, 7.0f, 5.0f, 3.0f, 0.0f, 9.0f } ;
		assertEquals( 0.0f, MathZ.min(ar), FLOAT_EPSILON ) ;
	}

	@Test
	public void testMinInt()
	{ assertEquals( 0, MathZ.min( 8, 6, 7, 5, 3, 0, 9 ) ) ; }

	@Test
	public void testMinLong()
	{ assertEquals( 0L, MathZ.min( 8L, 6L, 7L, 5L, 3L, 0L, 9L ) ) ; }
}
