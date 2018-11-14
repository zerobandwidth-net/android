package net.zerobandwidth.android.lib.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static net.zerobandwidth.android.lib.util.StringComparator.Base.EVALUATION_INDETERMINATE ;

/**
 * Exercises {@link StringComparator.Base}.
 * @since zerobandwidth-net/android [NEXT] (#56)
 */
@RunWith( JUnit4.class )
public class StringComparatorBaseTest
{
	/**
	 * Allows this class to almost-directly the algorithm that is defined in
	 * {@link StringComparator.Base}.
	 * @since zerobandwidth-net/android [NEXT] (#56)
	 */
	private class DumbStringComparator
	extends StringComparator.Base
	{
		@Override
		protected int executeComparison( String s1, String s2 )
		{ return EVALUATION_INDETERMINATE ; }
	}

	@Test
	public void testComparator()
	{
		DumbStringComparator cmp = new DumbStringComparator() ;
		assertEquals( 0, cmp.compare( null, null ) ) ;
		assertEquals( 0, cmp.compare( null, "" ) ) ;
		assertEquals( 0, cmp.compare( "", null ) ) ;
		assertEquals( 0, cmp.compare( "", "" ) ) ;
		assertEquals( 0, cmp.compare( "foo", "foo" ) ) ;
		assertEquals( EVALUATION_INDETERMINATE, cmp.compare( "foo", "bar" ) ) ;
		assertEquals( EVALUATION_INDETERMINATE, cmp.compare( "foo", "zoo" ) ) ;
		assertEquals( EVALUATION_INDETERMINATE, cmp.compare( "foo", "fo" ) ) ;
		assertEquals( EVALUATION_INDETERMINATE, cmp.compare( "foo", "foobar" ) ) ;
	}
}
