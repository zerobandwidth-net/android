package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Exercises {@link InsertionBuilder}.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class InsertionBuilderTest
extends ProviderTestCase2<MockContentProvider>
{
	@SuppressWarnings( "unused" ) // sAuthority is intentionally ignored
	public InsertionBuilderTest()
	{
		super( MockContentProvider.class,
				QueryBuilderTest.MockContext.AUTHORITY ) ;
	}

	@Override
	@Before
	public void setUp()
	throws Exception
	{ super.setUp() ; }

	/** Exercises {@link InsertionBuilder#executeQuery}. */
	@Test
	public void testExecuteQuery()
	throws Exception // Any uncaught exception is a failure.
	{
		ContentResolver rslv = this.getMockContentResolver() ;
		Uri uri = QueryBuilderTest.MockContext.getMockURI() ;
		InsertionBuilder qb = new InsertionBuilder( rslv, uri ) ;
		Uri uriInserted = qb.execute() ;
		assertNull( uriInserted ) ; // We didn't supply any values.
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", "bar" ) ;
		uriInserted = qb.setValues( vals ).execute() ;
		assertEquals( MockContentProvider.EXPECTED_INSERT_URI, uriInserted ) ;
	}
}
