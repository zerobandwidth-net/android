package net.zerobandwidth.android.lib.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises {@link JavaStringComparator}.
 * @since zerobandwidth-net/android [NEXT] (#56)
 */
@RunWith( JUnit4.class )
public class JavaStringComparatorTest
{
	@Test
	public void testComparator()
	{
		JavaStringComparator cmp = new JavaStringComparator() ;
		assertEquals( 0, cmp.compare( "foo", "foo" ) ) ;
		assertTrue( cmp.compare( "foo", "bar" ) > 0 ) ;
		assertTrue( cmp.compare( "foo", "zoo" ) < 0 ) ;
		assertTrue( cmp.compare( "foo", "fo" ) > 0 ) ;
		assertTrue( cmp.compare( "foo", "foobar" ) < 0 ) ;

		cmp = new JavaStringComparator(true) ;
		assertEquals( 0, cmp.compare( "foo", "foo" ) ) ;
		assertEquals( 1, cmp.compare( "foo", "bar" ) ) ;
		assertEquals( -1, cmp.compare( "foo", "zoo" ) ) ;
		assertEquals( 1, cmp.compare( "foo", "fo" ) ) ;
		assertEquals( -1, cmp.compare( "foo", "foobar" ) ) ;
	}
}
