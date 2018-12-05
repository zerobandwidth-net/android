package net.zer0bandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNull;

/**
 * Exercises {@link DeletionBuilder}.
 * @since zer0bandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class DeletionBuilderTest
extends ProviderTestCase2<MockContentProvider>
{
	protected QueryBuilderTest.MockContext m_mockery =
			new QueryBuilderTest.MockContext() ;

	@SuppressWarnings( "unused" ) // sAuthority is intentionally ignored
	public DeletionBuilderTest()
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
	public void testDeleteAll()
	{
		DeletionBuilder qb =
				new DeletionBuilder( m_mockery.ctx, m_mockery.uri ) ;
		qb.m_sExplicitWhereFormat = "qarnflarglebarg" ;
		qb.m_asExplicitWhereParams = new String[] { "foo", "bar", "baz" } ;
		qb.deleteAll() ;
		assertNull( qb.m_sExplicitWhereFormat ) ;
		assertNull( qb.m_asExplicitWhereParams ) ;
	}

	/** Exercises {@link DeletionBuilder#executeQuery}. */
	@Test
	public void testExecuteQuery()
	throws Exception // Any uncaught exception is a failure.
	{
		ContentResolver rslv = this.getMockContentResolver() ;

		int nDeleted = QueryBuilder.deleteFrom( rslv, m_mockery.uri ).execute();
		assertEquals( MockContentProvider.EXPECTED_DELETE_COUNT, nDeleted ) ;
	}
}
