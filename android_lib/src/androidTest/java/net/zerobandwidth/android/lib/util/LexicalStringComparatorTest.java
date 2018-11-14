package net.zerobandwidth.android.lib.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Exercises {@link LexicalStringComparator}.
 * @since zerobandwidth-net/android 0.2.1 (#56)
 */
@RunWith( JUnit4.class )
public class LexicalStringComparatorTest
{
	@Test
	public void testComparator()
	{
		LexicalStringComparator cmp = new LexicalStringComparator() ;
		assertEquals( 0, cmp.compare( "foo", "foo" ) ) ;
		assertEquals( 1, cmp.compare( "foo", "bar" ) ) ;
		assertEquals( -1, cmp.compare( "foo", "zoo" ) ) ;
		assertEquals( 1, cmp.compare( "foo", "fo" ) ) ;
		assertEquals( -1, cmp.compare( "foo", "foobar" ) ) ;
	}
}
