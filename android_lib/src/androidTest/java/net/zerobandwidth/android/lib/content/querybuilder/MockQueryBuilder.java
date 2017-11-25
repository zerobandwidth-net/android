package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Used exclusively by {@link QueryBuilderExceptionsTest}.
 * Not intended for use in real code.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
public class MockQueryBuilder
extends QueryBuilder<MockQueryBuilder,Boolean>
{
	public MockQueryBuilder()
	{ super() ; }

	@Override
	public Boolean executeQuery( ContentResolver rslv, Uri uri )
	throws Exception
	{ return true ; }

	/**
	 * An intentionally-broken version of {@link MockQueryBuilder}.
	 * Used exclusively by {@link QueryBuilderExceptionsTest}.
	 * Not intended for use in real code.
	 * @since zerobandwidth-net/android 0.1.7 (#39)
	 */
	public static class Broken
	extends MockQueryBuilder
	{
		public Broken()
		{ super() ; }

		@Override
		public Boolean executeQuery( ContentResolver rslv, Uri uri )
		throws Exception
		{ throw new RuntimeException( "I'm totally broken." ) ; }
	}
}
