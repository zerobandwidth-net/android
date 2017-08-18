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
 * Exercises {@link UpdateBuilder}.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class UpdateBuilderTest
extends ProviderTestCase2<MockContentProvider>
{
	protected QueryBuilderTest.MockContext m_mockery =
			new QueryBuilderTest.MockContext() ;

	@SuppressWarnings( "unused" ) // sAuthority is intentionally ignored
	public UpdateBuilderTest()
	{
		super( MockContentProvider.class,
				QueryBuilderTest.MockContext.AUTHORITY ) ;
	}

	@Override
	@Before
	public void setUp()
	throws Exception
	{ super.setUp() ; }

	/** Exercises {@link DeletionBuilder#deleteAll} */
	@Test
	public void testUpdateAll()
	{
		UpdateBuilder qb =
				new UpdateBuilder( m_mockery.ctx, m_mockery.uri ) ;
		qb.m_sExplicitWhereFormat = "qarnflarglebarg" ;
		qb.m_asExplicitWhereParams = new String[] { "foo", "bar", "baz" } ;
		qb.updateAll() ;
		assertNull( qb.m_sExplicitWhereFormat ) ;
		assertNull( qb.m_asExplicitWhereParams ) ;
	}

	/** Exercises {@link UpdateBuilder#executeQuery}. */
	@Test
	public void testExecuteQuery()
	throws Exception // Any uncaught exception is a failure.
	{
		ContentResolver rslv = this.getMockContentResolver() ;
		Uri uri = QueryBuilderTest.MockContext.getMockURI() ;
		UpdateBuilder qb = new UpdateBuilder( rslv, uri ) ;
		int nUpdated = qb.execute() ;
		assertEquals( 0, nUpdated ) ; // We didn't supply any values.
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", "bar" ) ;
		nUpdated = qb.setValues( vals ).execute() ;
		assertEquals( MockContentProvider.EXPECTED_UPDATE_COUNT, nUpdated ) ;
	}
}
