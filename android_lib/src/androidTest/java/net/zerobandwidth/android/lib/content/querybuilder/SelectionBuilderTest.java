package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import net.zerobandwidth.android.lib.content.ContentUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.zerobandwidth.android.lib.content.ContentUtils.QUERY_ORDER_DESCENDING;

/**
 * Exercises {@link SelectionBuilder}.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class SelectionBuilderTest
extends ProviderTestCase2<MockContentProvider>
{
	@SuppressWarnings( "unused" ) // sAuthority is intentionally ignored
	public SelectionBuilderTest()
	{
		super( MockContentProvider.class,
				QueryBuilderTest.MockContext.AUTHORITY ) ;
	}

	@Override
	@Before
	public void setUp()
	throws Exception
	{ super.setUp() ; }

	/**
	 * Exercises {@link SelectionBuilder#initColumns()} and
	 * {@link SelectionBuilder#initSortSpec()}.
	 */
	@Test
	public void testInitMethods()
	{
		SelectionBuilder qb = new SelectionBuilder() ;
		assertNotNull( qb.m_vColumns ) ;
		assertTrue( qb.m_vColumns.isEmpty() ) ;
		assertNotNull( qb.m_mapSortSpec ) ;
		assertTrue( qb.m_mapSortSpec.isEmpty() ) ;
		qb.m_vColumns.add( "foo" ) ;
		qb.m_mapSortSpec.put( "foo", "ASC" ) ;
		qb.initColumns().initSortSpec() ;
		assertNotNull( qb.m_vColumns ) ;
		assertTrue( qb.m_vColumns.isEmpty() ) ;
		assertNotNull( qb.m_mapSortSpec ) ;
		assertTrue( qb.m_mapSortSpec.isEmpty() ) ;
	}

	/**
	 * Exercises {@link SelectionBuilder#allColumns()},
	 * {@link SelectionBuilder#columns(String...)},
	 * {@link SelectionBuilder#columns(Collection)}, and
	 * {@link SelectionBuilder#getColumns()}.
	 */
	@Test
	public void testColumnSpecifiers()
	{
		SelectionBuilder qb = new SelectionBuilder() ;

		qb.columns( "foo" ) ;
		assertEquals( 1, qb.getColumns().length ) ;
		assertEquals( "foo", qb.getColumns()[0] ) ;

		qb.columns( "foo", "foo", "foo" ) ;
		assertEquals( 1, qb.getColumns().length ) ;
		assertEquals( "foo", qb.getColumns()[0] ) ;

		List<String> asColumns = new ArrayList<>() ;
		asColumns.add( "foo" ) ;
		asColumns.add( "bar" ) ;
		asColumns.add( "baz" ) ;
		qb.columns( asColumns ) ;
		assertEquals( 3, qb.getColumns().length ) ;
		assertEquals( "baz", qb.getColumns()[2] ) ;

		qb.allColumns() ;
		assertNull( qb.getColumns() ) ;

		qb.columns( "sacrificial 1" ).columns( (String[])(null) ) ;
		assertNull( qb.getColumns() ) ;

		qb.columns( "sacrificial 2" ).columns( (Collection<String>)(null) ) ;
		assertNull( qb.getColumns() ) ;

		asColumns.clear() ;
		qb.columns( "sacrificial 3" ).columns( asColumns ) ;
		assertNull( qb.getColumns() ) ;
	}

	/**
	 * Exercises {@link SelectionBuilder#orderBy(String, String)},
	 * {@link SelectionBuilder#orderBy(String)}, and
	 * {@link SelectionBuilder#getSortSpecString()}.
	 */
	@Test
	public void testSortOrderSpecifiers()
	{
		SelectionBuilder qb = new SelectionBuilder() ;
		qb.orderBy( "foo" ) ;
		assertEquals( "foo ASC", qb.getSortSpecString() ) ;
		qb.orderBy( "foo", QUERY_ORDER_DESCENDING ) ;
		assertEquals( "foo DESC", qb.getSortSpecString() ) ;
		qb.orderBy( "bar" ) ;
		assertEquals( "foo DESC, bar ASC", qb.getSortSpecString() ) ;
	}

	/** Exercises {@link SelectionBuilder#executeQuery}. */
	@Test
	public void testExecuteQuery()
	throws Exception // Any uncaught exception is a failure.
	{
		ContentResolver rslv = this.getMockContentResolver() ;
		Uri uri = QueryBuilderTest.MockContext.getMockURI() ;
		SelectionBuilder qb = new SelectionBuilder( rslv, uri ) ;
		Cursor crs = qb.execute() ;
		assertEquals( MockContentProvider.EXPECTED_SELECT_CURSOR_SIZE,
				crs.getCount() ) ;
	}

	/**
	 * Exercises {@link SelectionBuilder#executeOrCancel(CancellationSignal)}
	 * and, by extension,
	 * {@link SelectionBuilder#executeOrCancel(ContentResolver, Uri, CancellationSignal)}.
	 */
	@Test
	@RequiresApi(16)
	public void testExecuteOrCancel()
	{
		ContentResolver rslv = this.getMockContentResolver() ;
		Uri uri = QueryBuilderTest.MockContext.getMockURI() ;
		SelectionBuilder qb = new SelectionBuilder( rslv, uri ) ;
		Cursor crs = qb.executeOrCancel( new CancellationSignal() ) ;
		assertEquals( MockContentProvider.EXPECTED_SELECT_CURSOR_SIZE,
				crs.getCount() ) ;
		CancellationSignal sig = new CancellationSignal() ;
		sig.cancel() ;
		assertNull( qb.executeOrCancel(sig) ) ;
		/* TODO Why doesn't the following test work?
		this.getProvider().setBrokenSelect( true ) ;
		QueryBuilder.ExecutionException xBroken = null ;
		try { qb.executeOrCancel( new CancellationSignal() ) ; }
		catch( QueryBuilder.ExecutionException x ) { xBroken = x ; }
		assertNotNull( xBroken ) ;
		this.getProvider().setBrokenSelect( false ) ;
		*/
	}

	/**
	 * Exercises {@link SelectionBuilder#toBundle}.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testToBundle()
	{
		ContentResolver rslv = this.getMockContentResolver() ;
		Uri uri = QueryBuilderTest.MockContext.getMockURI() ;
		SelectionBuilder qb = new SelectionBuilder( rslv, uri )
				.columns( "foo", "bar", "baz" )
				.where( "fargle=? AND bargle=?", "FARGLE", "BARGLE" )
				.orderBy( "foo", QUERY_ORDER_DESCENDING )
				;
		Bundle bndl = qb.toBundle() ;
		assertEquals( uri.toString(), bndl.getString("uri") ) ;
		String[] asColumns = bndl.getStringArray("columns") ;
		assertEquals( 3, asColumns.length ) ;
		assertEquals( "foo", asColumns[0] ) ;
		assertEquals( "bar", asColumns[1] ) ;
		assertEquals( "baz", asColumns[2] ) ;
		assertEquals( "fargle=? AND bargle=?", bndl.getString("where_format") );
		String[] asWhereParams = bndl.getStringArray("where_columns") ;
		assertEquals( 2, asWhereParams.length ) ;
		assertEquals( "FARGLE", asWhereParams[0] ) ;
		assertEquals( "BARGLE", asWhereParams[1] ) ;
		assertEquals( "foo DESC", bndl.getString("order_by") ) ;
	}
}
