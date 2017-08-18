package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Exercises {@link QueryBuilder}.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class QueryBuilderTest
{
	/**
	 * Open structure providing an operational context for the classes that test
	 * {@link QueryBuilder} and its implementation classes.
	 * @since zerobandwidth-net/android 0.1.7 (#39)
	 */
	protected static class MockContext
	{
		public static final String AUTHORITY = "net.foo" ;

		public static final String MOCK_URI = "content://net.foo/bar" ;

		public static Uri getMockURI()
		{ return Uri.parse(MOCK_URI) ; }

		/** An operational context. */
		public Context ctx ;
		/** A {@link ContentResolver} obtained from the operational context. */
		public ContentResolver rslv ;
		/** A canned test URI. */
		public Uri uri = getMockURI() ;

		/** Initializes the test context. */
		public MockContext()
		{
			this.ctx = InstrumentationRegistry.getContext() ;
			this.rslv = ctx.getContentResolver() ;
		}
	}

	/** The context in which the tests will be conducted. */
	protected MockContext m_mockery = new MockContext() ;

	/**
	 * Exercises {@link QueryBuilder#insert()},
	 * {@link QueryBuilder#insertInto(ContentResolver, Uri)}, and
	 * {@link QueryBuilder#insertInto(Context, Uri)}.
	 */
	@Test
	public void testKickoffInsert()
	{
		assertEquals( null, QueryBuilder.insert().m_rslv ) ;
		assertEquals( "net.foo",
			QueryBuilder.insertInto( m_mockery.rslv, m_mockery.uri )
						.m_uri.getAuthority() );
		assertTrue( m_mockery.rslv ==
			QueryBuilder.insertInto( m_mockery.ctx, m_mockery.uri ).m_rslv ) ;
	}

	/**
	 * Exercises {@link QueryBuilder#update()},
	 * {@link QueryBuilder#update(ContentResolver, Uri)}, and
	 * {@link QueryBuilder#update(Context, Uri)}.
	 */
	@Test
	public void testKickoffUpdate()
	{
		assertEquals( null, QueryBuilder.update().m_rslv ) ;
		assertEquals( "net.foo",
			QueryBuilder.update( m_mockery.rslv, m_mockery.uri )
						.m_uri.getAuthority() ) ;
		assertTrue( m_mockery.rslv ==
			QueryBuilder.update( m_mockery.ctx, m_mockery.uri ).m_rslv ) ;
	}

	/**
	 * Exercises {@link QueryBuilder#select()},
	 * {@link QueryBuilder#selectFrom(ContentResolver, Uri)}, and
	 * {@link QueryBuilder#selectFrom(Context, Uri)}.
	 */
	@Test
	public void testKickoffSelect()
	{
		assertEquals( null, QueryBuilder.select().m_rslv ) ;
		assertEquals( "net.foo",
			QueryBuilder.selectFrom( m_mockery.rslv, m_mockery.uri )
						.m_uri.getAuthority() );
		assertTrue( m_mockery.rslv ==
			QueryBuilder.selectFrom( m_mockery.ctx, m_mockery.uri ).m_rslv ) ;
	}

	/**
	 * Exercises {@link QueryBuilder#delete()},
	 * {@link QueryBuilder#deleteFrom(ContentResolver, Uri)}, and
	 * {@link QueryBuilder#deleteFrom(Context, Uri)}.
	 */
	@Test
	public void testKickoffDelete()
	{
		assertEquals( null, QueryBuilder.delete().m_rslv ) ;
		assertEquals( "net.foo",
			QueryBuilder.deleteFrom( m_mockery.rslv, m_mockery.uri )
						.m_uri.getAuthority() );
		assertTrue( m_mockery.rslv ==
			QueryBuilder.deleteFrom( m_mockery.ctx, m_mockery.uri ).m_rslv ) ;
	}

	/** Exercises {@link QueryBuilder#getContentResolver}. */
	@Test
	public void testGetContentResolver()
	{
		assertTrue( m_mockery.rslv ==
				QueryBuilder.getContentResolver( m_mockery.ctx ) ) ;
		QueryBuilder.UnboundException xUnbound = null ;
		try { QueryBuilder.getContentResolver(null) ; }
		catch( QueryBuilder.UnboundException x ) { xUnbound = x ; }
		assertNotNull( xUnbound ) ;
	}

	/** Exercises {@link QueryBuilder#validateDataContextBinding}. */
	@Test
	public void testValidateDataContextBinding()
	{
		QueryBuilder.validateDataContextBinding( m_mockery.rslv, m_mockery.uri ) ;
		QueryBuilder.UnboundException xUnbound = null ;
		try { QueryBuilder.validateDataContextBinding( null, m_mockery.uri ) ; }
		catch( QueryBuilder.UnboundException x ) { xUnbound = x ; }
		assertNotNull( xUnbound ) ;
		assertEquals( "A content resolver is required.",
				xUnbound.getMessage() ) ;
		xUnbound = null ;
		try
		{ QueryBuilder.validateDataContextBinding( m_mockery.rslv, null ) ; }
		catch( QueryBuilder.UnboundException x ) { xUnbound = x ; }
		assertNotNull( xUnbound ) ;
		assertEquals( "A valid URI is required.", xUnbound.getMessage() ) ;
	}

	/**
	 * Exercises {@link QueryBuilder#onDataSource(ContentResolver,Uri)}
	 * and {@link QueryBuilder#onDataSource(Context, Uri)}.
	 */
	@Test
	public void testOnDataSource()
	{
		QueryBuilder qb = new MockQueryBuilder() ;
		assertNull( qb.m_rslv ) ;
		assertNull( qb.m_uri ) ;
		qb.onDataSource( m_mockery.rslv, m_mockery.uri ) ;
		assertTrue( m_mockery.rslv == qb.m_rslv ) ;
		assertTrue( m_mockery.uri == qb.m_uri ) ;
		qb.m_rslv = null ;
		qb.m_uri = null ;
		qb.onDataSource( m_mockery.ctx, m_mockery.uri ) ;
		assertTrue( m_mockery.rslv == qb.m_rslv ) ;
		assertTrue( m_mockery.uri == qb.m_uri ) ;
	}

	/** Exercises {@link QueryBuilder#setValues}. */
	@Test
	public void testSetValues()
	{
		ContentValues vals = new ContentValues() ;
		QueryBuilder qb = (new MockQueryBuilder()).setValues(vals) ;
		assertTrue( vals == qb.m_valsToWrite ) ;
	}

	/**
	 * Exercises methods of {@link QueryBuilder} related to construction of
	 * "where" clauses.
	 */
	@Test
	public void testWhere()
	{
		QueryBuilder qb = new MockQueryBuilder() ;
		qb.where( "This isn't even a valid clause." ) ;
		assertEquals( "This isn't even a valid clause.", qb.getWhereFormat() ) ;
		assertNull( qb.getWhereParams() ) ;
		qb.where( "Some format.", "foo", "bar", "baz" ) ;
		assertEquals( "Some format.", qb.getWhereFormat() ) ;
		assertEquals( 3, qb.getWhereParams().length ) ;
		assertEquals( "baz", qb.getWhereParams()[2] ) ;
		List<String> asParams = new ArrayList<>() ;
		asParams.add( "flargle" ) ;
		asParams.add( "dargle" ) ;
		qb.where( "Another format.", asParams ) ;
		assertEquals( "Another format.", qb.getWhereFormat() ) ;
		assertEquals( 2, qb.getWhereParams().length ) ;
		assertEquals( "dargle", qb.getWhereParams()[1] ) ;
		qb.where( "Tricky format.", ((Collection<String>)(null)) ) ;
		assertEquals( "Tricky format.", qb.getWhereFormat() ) ;
		assertNull( qb.getWhereParams() ) ;
	}

	/**
	 * Exercises {@link QueryBuilder#execute},
	 * {@link QueryBuilder#executeOn(ContentResolver, Uri)}, and
	 * {@link QueryBuilder#executeOn(Context, Uri)}.
	 */
	@Test
	public void testExecute()
	{
		MockQueryBuilder qbGood = (new MockQueryBuilder())
				.onDataSource( m_mockery.rslv, m_mockery.uri ) ;
		assertTrue( qbGood.execute() ) ;
		assertTrue( qbGood.executeOn( m_mockery.ctx, m_mockery.uri ) ) ;

		MockQueryBuilder qbBad = (new MockQueryBuilder.Broken())
				.onDataSource( m_mockery.rslv, m_mockery.uri ) ;
		QueryBuilder.ExecutionException xBroken = null ;
		try { qbBad.execute() ; }
		catch( QueryBuilder.ExecutionException x ) { xBroken = x ; }
		assertNotNull( xBroken ) ;
		assertEquals( "I'm totally broken.", xBroken.getCause().getMessage() ) ;
		xBroken = null ;
		try { qbBad.executeOn( m_mockery.ctx, m_mockery.uri ) ; }
		catch( QueryBuilder.ExecutionException x ) { xBroken = x ; }
		assertNotNull( xBroken ) ;
		assertEquals( "I'm totally broken.", xBroken.getCause().getMessage() ) ;
	}
}
