package net.zer0bandwidth.android.lib.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Exercises {@link CollectionsZ}.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
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

	/** Exercises {@link CollectionsZ#toArray} with a positive test. */
	@Test
	public void testToArray()
	{
		ArrayList<String> asInput = new ArrayList<>() ;
		asInput.add( "foo" ) ;
		asInput.add( "bar" ) ;
		asInput.add( "baz" ) ;
		String[] asOutput = CollectionsZ.of(String.class).toArray(asInput) ;
		assertEquals( 3, asOutput.length ) ;
		for( int i = 0 ; i < asInput.size() ; i++ )
			assertEquals( asInput.get(i), asOutput[i] ) ;
	}

	/** Exercises {@link CollectionsZ#toArray} with negative tests. */
	@Test
	public void testToArrayNeg()
	{
		String[] as = CollectionsZ.of(String.class).toArray(null) ;
		assertNull(as) ;
		as = CollectionsZ.of(String.class).toArray( new ArrayList<String>() ) ;
		assertNotNull(as) ;
		assertEquals( 0, as.length ) ;
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
