package net.zerobandwidth.android.lib.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Exercises {@link CollectionsZ}.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class CollectionsZTest
{
	/** Exercises {@link CollectionsZ#newArray(int)}. */
	@Test
	public void testNewArray()
	{
		Integer[] az = CollectionsZ.of(Integer.class).newArray(0) ;
		assertEquals( 0, az.length ) ;
		az = CollectionsZ.of(Integer.class).newArray(2) ;
		assertEquals( 2, az.length ) ;
		az[0] = 42 ;
		az[1] = 50 ;
	}

	/** Exercises {@link CollectionsZ#arrayConcat} with positive tests. */
	@Test
	public void testArrayConcat()
	{
		String[] asFoo = { "foo", "Foo", "FOO" } ;
		String[] asBar = { "bar", "Bar", "BAR" } ;
		String[] asBaz = { "baz", "Baz", "BAZ" } ;

		String[] asAll = CollectionsZ.of(String.class)
				.arrayConcat( asFoo, asBar, asBaz ) ;

		assertEquals( 9, asAll.length ) ;
		for( int i = 0 ; i < 3 ; i++ )
		{
			assertEquals( asFoo[i], asAll[i] ) ;
			assertEquals( asBar[i], asAll[i+3] ) ;
			assertEquals( asBaz[i], asAll[i+6] ) ;
		}
	}

	/** Exercises {@link CollectionsZ#arrayConcat} with negative tests. */
	@SuppressWarnings("ConstantConditions") // testing wacky things on purpose
	@Test
	public void testArrayConcatNeg()
	{
		CollectionsZ<String> util = CollectionsZ.of(String.class) ;
		String[] asNull = null ;                             // a null reference
		assertNull( util.arrayConcat( asNull ) ) ;
		String[][] aas = new String[][] {} ;    // a zero-length array of arrays
		assertNull( util.arrayConcat( aas ) ) ;
		String[] asAlone = { "one", "is", "loneliest" } ;
		assertEquals( asAlone, util.arrayConcat(asAlone) ) ;
	}
}
