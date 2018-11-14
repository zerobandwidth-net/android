package net.zerobandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import net.zerobandwidth.android.lib.database.MinimalUnitTestDBPortal;
import net.zerobandwidth.android.lib.database.SQLitePortal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.* ;

import static net.zerobandwidth.android.lib.database.MinimalUnitTestDBPortal.TEST_TABLE_NAME ;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.COLUMN_NOT_FOUND;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_ORDER_ASC;

/**
 * Exercises query builders.
 * @see QueryBuilder
 * @see SelectionBuilder
 * @see InsertionBuilder
 * @see UpdateBuilder
 * @see DeletionBuilder
 * @since zerobandwidth-net/android 0.1.1 (#20)
 */
@RunWith( AndroidJUnit4.class )
public class QueryBuilderTest
{
	private static final String LOG_TAG =
			QueryBuilderTest.class.getSimpleName() ;

	/** Persistent handle on the test DB portal. */
	private MinimalUnitTestDBPortal m_dbh ;

	/** Persistent handle on the database under the portal. */
	private SQLiteDatabase m_db ;

	/** Creates the database portal for each test. */
	@Before
	public void setUp()
	{
		m_dbh = MinimalUnitTestDBPortal.getInstrumentedInstance(
					QueryBuilderTest.class.getSimpleName() ) ;
		m_dbh.openDB() ;
		//noinspection StatementWithEmptyBody
		while( ! m_dbh.isConnected() ) ; // Wait for a connection.
		m_db = m_dbh.getDB() ;
	}

	/** Closes the database after each test. */
	@After
	public void finish()
	{ m_dbh.closeDB().close() ; }

	/**
	 * Creates a consistent set of row values for testing.
	 * @return a set of row values
	 */
	private static ContentValues getTestableValues()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "a_string_field", "foo" ) ;
		vals.put( "a_int_field", 5 ) ;
		vals.put( "a_boolint_field", SQLitePortal.boolToInt(true) ) ;
		return vals ;
	}

	/**
	 * Explicitly exercises the basic functions of {@link InsertionBuilder}.
	 */
	@Test
	public void testInsertionBuilderBasic()
	{
		ContentValues vals = getTestableValues() ;
		InsertionBuilder bldr =
			QueryBuilder.insertInto( TEST_TABLE_NAME )
				.setValues( vals )
				;
		assertEquals( "INSERT INTO unittestdata SET a_boolint_field=1, a_int_field=5, a_string_field='foo' ;",
			bldr.toString() ) ;
		assertEquals( null, bldr.m_sNullableColumn ) ;
		assertEquals( SQLiteDatabase.CONFLICT_NONE,
				bldr.m_zConflictAlgorithmID ) ;
		final long nID = bldr.executeOn( m_db ) ;
		assertFalse( nID == INSERT_FAILED ) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Basic insert result: " ).append( nID ).toString() ) ;
	}

	/**
	 * Explicitly exercises all functions of {@link InsertionBuilder}.
	 */
	@Test
	public void testInsertionBuilderFull()
	{
		ContentValues vals = getTestableValues() ;
		InsertionBuilder bldr = QueryBuilder.insertInto( TEST_TABLE_NAME )
				.setValues( vals )
				.withNullable( "a_string_field" )
				.onConflict( SQLiteDatabase.CONFLICT_ROLLBACK )
				;
		assertEquals( "INSERT INTO unittestdata SET a_boolint_field=1, a_int_field=5, a_string_field='foo' ;",
			bldr.toString() ) ;
		assertEquals( "a_string_field", bldr.m_sNullableColumn ) ;
		assertEquals( SQLiteDatabase.CONFLICT_ROLLBACK,
				bldr.m_zConflictAlgorithmID ) ;
		final long nID = bldr.executeOn( m_db ) ;
		assertFalse( nID == INSERT_FAILED ) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Full insert result: " ).append( nID ).toString() ) ;
	}

	/**
	 * Exercises the newer grammar for binding and executing an
	 * {@link InsertionBuilder}.
	 * Intentionally doesn't test any of the other inner workings of the
	 * builder, since those are covered elsewhere.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@Test
	public void testInsertionBuilderBound()
	{
		ContentValues vals = getTestableValues() ;
		long nID = QueryBuilder.insertInto( m_db, TEST_TABLE_NAME )
				.setValues(vals).execute() ;
		assertFalse( nID == INSERT_FAILED ) ;
	}

	/**
	 * Explicitly exercises basic functions of {@link UpdateBuilder}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set
	 * for updates.
	 */
	@Test
	public void testUpdateBuilderBasic()
	{
		final ContentValues vals = getTestableValues() ;
		final long nID = QueryBuilder.insertInto( TEST_TABLE_NAME )
				.setValues( vals ).executeOn( m_db ) ;
		QueryBuilder.insertInto( TEST_TABLE_NAME ).setValues( vals )
	            .executeOn( m_db ) ; // Do it again so that there are two rows.
		ContentValues valsNew = new ContentValues() ;
		valsNew.put( "a_string_field", "bar" ) ;
		final String sID = Long.toString(nID) ;
		UpdateBuilder bldr = QueryBuilder.update( TEST_TABLE_NAME )
				.setValues( valsNew )
				.where( "id=?", sID )
				;
		assertEquals( "UPDATE unittestdata SET a_string_field='bar' WHERE id=" + sID + " ;",
				bldr.toString() ) ;
		assertEquals( 1, bldr.executeOn( m_db ).intValue() ) ;
	}

	/**
	 * Explicitly exercises {@link UpdateBuilder#updateAll()}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set
	 * for updates.
	 * Implicitly exercises {@link DeletionBuilder#deleteAll()} to clear the
	 * data set prior to the run.
	 */
	@Test
	public void testUpdateBuilderUpdateAll()
	{
		QueryBuilder.deleteFrom( TEST_TABLE_NAME ).deleteAll().executeOn( m_db ) ;
		final int ITERATIONS = 5 ; // Tune this to taste.
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			ContentValues valsInsert = new ContentValues() ;
			valsInsert.put( "a_int_field", i ) ;
			QueryBuilder.insertInto( TEST_TABLE_NAME ).setValues( valsInsert )
					.executeOn( m_db ) ;
		}
		ContentValues valsUpdate = new ContentValues() ;
		valsUpdate.put( "a_string_field", "foo" ) ;
		UpdateBuilder bldr = QueryBuilder.update( TEST_TABLE_NAME )
				.setValues( valsUpdate )
				.updateAll()
				;
		assertEquals( "UPDATE unittestdata SET a_string_field='foo' WHERE 1 ;",
				bldr.toString() ) ;
		assertEquals( ITERATIONS, bldr.executeOn( m_db ).intValue() ) ;
	}

	/**
	 * Explicitly exercises all the functions of {@link UpdateBuilder}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set
	 * for updates.
	 */
	@Test
	public void testUpdateBuilderFull()
	{
		final long nID = QueryBuilder.insertInto( TEST_TABLE_NAME )
				.setValues( getTestableValues() ).executeOn( m_db ) ;
		final String sID = Long.toString(nID) ;
		ContentValues vals = new ContentValues() ;
		vals.put( "a_string_field", "single_update" ) ;
		UpdateBuilder bldr = QueryBuilder.update( TEST_TABLE_NAME )
				.setValues( vals )
				.where( "id=?", sID )
				.onConflict( SQLiteDatabase.CONFLICT_ROLLBACK )
				;
		assertEquals( "UPDATE unittestdata SET a_string_field='single_update' WHERE id=" + sID + " ;",
				bldr.toString() ) ;
		assertEquals( SQLiteDatabase.CONFLICT_ROLLBACK,
				bldr.m_zConflictAlgorithmID ) ;
		final int nCount = bldr.executeOn( m_db ) ;
		assertEquals( 1, nCount ) ;
	}

	/**
	 * Exercises the newer grammar for binding and executing an
	 * {@link UpdateBuilder}.
	 * Intentionally doesn't test any of the other inner workings of the
	 * builder, since those are covered elsewhere.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@Test
	public void testUpdateBuilderBound()
	{
		final long nID = QueryBuilder.insertInto( m_db, TEST_TABLE_NAME )
				.setValues( getTestableValues() ).execute() ;
		final String sID = Long.toString(nID) ;
		ContentValues valsUpdate = new ContentValues() ;
		valsUpdate.put( "a_string_field", "bound_update" ) ;
		long nCount = QueryBuilder.update( m_db, TEST_TABLE_NAME )
				.setValues( valsUpdate )
				.where( "id=?", sID )
				.execute()
				;
		assertEquals( 1, nCount ) ;
	}

	/**
	 * Explicitly exercises basic functions of {@link DeletionBuilder}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set to
	 * be deleted.
	 */
	@Test
	public void testDeletionBuilderBasic()
	{
		final int ITERATIONS = 5 ; // Tune this to taste.
		long nLastID = 0 ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			nLastID = QueryBuilder.insertInto( TEST_TABLE_NAME )
					.setValues( getTestableValues() ).executeOn( m_db ) ;
		}
		final String sLastID = Long.toString( nLastID ) ;
		DeletionBuilder bldr = QueryBuilder.deleteFrom( TEST_TABLE_NAME )
				.where( "id=?", sLastID ) ;
		assertEquals( "DELETE FROM unittestdata WHERE id=" + sLastID + " ;",
				bldr.toString() ) ;
		assertEquals( 1, bldr.executeOn( m_db ).intValue() ) ;
	}

	/**
	 * Explicitly exercises {@link DeletionBuilder#deleteAll()}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set to
	 * be deleted.
	 */
	@Test
	public void testDeletionBuilderAll()
	{
		final int ITERATIONS = 5 ; // Tune this to taste.
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			QueryBuilder.insertInto( TEST_TABLE_NAME )
		            .setValues( getTestableValues() ).executeOn( m_db ) ;
		}
		DeletionBuilder bldr = QueryBuilder.deleteFrom( TEST_TABLE_NAME )
				.deleteAll() ;
		assertEquals( "DELETE FROM unittestdata WHERE 1 ;", bldr.toString() ) ;
		final int nCount = bldr.executeOn( m_db ) ;
		assertTrue( nCount >= ITERATIONS ) ;
		Log.d( LOG_TAG, (new StringBuilder())
			.append( "Deleted all rows: " ).append( nCount ).toString() ) ;
	}

	/**
	 * Exercises the newer grammar for binding and executing an
	 * {@link DeletionBuilder}.
	 * Intentionally doesn't test any of the other inner workings of the
	 * builder, since those are covered elsewhere.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@Test
	public void testDeletionBuilderBound()
	{
		final int ITERATIONS = 5 ; // Tune this to taste.
		long nLastID = 0 ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			nLastID = QueryBuilder.insertInto( m_db, TEST_TABLE_NAME )
					.setValues( getTestableValues() ).execute() ;
		}
		final String sLastID = Long.toString( nLastID ) ;
		Integer nCount = QueryBuilder.deleteFrom( m_db, TEST_TABLE_NAME )
				.where( "id=?", sLastID )
				.execute()
				;
		assertEquals( 1, nCount.intValue() ) ;
	}

	/**
	 * Explicitly exercises basic functions of {@link SelectionBuilder}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set to
	 * be selected.
	 * Implicitly exercises {@link DeletionBuilder#deleteAll()} to clear the
	 * data set prior to the run.
	 */
	@Test
	public void testSelectionBuilderBasic()
	{
		QueryBuilder.deleteFrom( TEST_TABLE_NAME ).deleteAll().executeOn( m_db ) ;
		final int ITERATIONS = 5 ; // Tune this to taste.
		long nLastID = 0 ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			nLastID = QueryBuilder.insertInto( TEST_TABLE_NAME )
				.setValues( getTestableValues() ).executeOn( m_db ) ;
		}
		final String sLastID = Long.toString(nLastID) ;
		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.where( "id=?", sLastID ) ;
		assertEquals( "SELECT * FROM unittestdata WHERE id=" + sLastID + " ;",
				bldr.toString() ) ;
		Cursor crs = bldr.executeOn( m_db ) ;
		assertNotNull( crs ) ;
		if( crs.moveToFirst() )
		{
			assertEquals( nLastID, crs.getLong( 0 ) ) ;
			SQLitePortal.closeCursor( crs ) ;
		}
		else
		{
			SQLitePortal.closeCursor( crs ) ;
			fail( "Got no results!" ) ;
		}
	}

	/**
	 * Explicitly exercises all functions of {@link SelectionBuilder}.
	 * Implicitly exercises {@link InsertionBuilder} to build up the data set to
	 * be selected.
	 * Implicitly exercises {@link DeletionBuilder#deleteAll()} to clear the
	 * data set prior to the run.
	 */
	@Test
	public void testSelectionBuilderFull()
	{
		final String sFoo = "abcdefghijklmnopqrstuvwxyz" ;
		QueryBuilder.deleteFrom( TEST_TABLE_NAME ).deleteAll().executeOn( m_db ) ;
		final int ITERATIONS = 5 ; // Tune this to taste; never more than 24.
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			ContentValues vals = new ContentValues() ;
			vals.put( "a_string_field", sFoo.substring( i, i+2 ) ) ;
			vals.put( "a_int_field", i ) ;
			vals.put( "a_boolint_field", i % 2 ) ; // ends up "true" if i is odd
			QueryBuilder.insertInto( TEST_TABLE_NAME )
		            .setValues( vals ).executeOn( m_db ) ;
		}

		// Normally, all these calls to methods of "bldr" would be fluidly
		// chained together, but here we want to poke each one in turn.

		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME ) ;

		bldr.distinct() ;
		assertTrue( bldr.m_bDistinct ) ;

		bldr.columns( "id", "a_string_field", "a_int_field" ) ;
		assertEquals( 3, bldr.m_vColumns.size() ) ;
		assertTrue( bldr.m_vColumns.contains( "id" ) ) ;
		assertTrue( bldr.m_vColumns.contains( "a_string_field" ) ) ;
		assertTrue( bldr.m_vColumns.contains( "a_int_field" ) ) ;

		ArrayList<String> asCols = new ArrayList<>() ;
		asCols.add( "id" ) ;
		asCols.add( "a_string_field" ) ;
		asCols.add( "a_int_field" ) ;
		bldr.columns( asCols ) ;
		assertEquals( 3, bldr.m_vColumns.size() ) ;
		assertTrue( bldr.m_vColumns.contains( "id" ) ) ;
		assertTrue( bldr.m_vColumns.contains( "a_string_field" ) ) ;
		assertTrue( bldr.m_vColumns.contains( "a_int_field" ) ) ;

		bldr.where( "a_boolint_field=1" ) ;
		assertEquals( "a_boolint_field=1", bldr.getWhereFormat() ) ;
		assertEquals( null, bldr.getWhereParams() ) ;

		bldr.groupBy( "id" ) ;
		assertEquals( "id", bldr.m_sGroupBy ) ;
		bldr.orderBy( "id", SQL_ORDER_ASC ) ;
		assertEquals( SQL_ORDER_ASC, bldr.m_mapOrderBy.get("id") ) ;

		// Final check of the whole SQL statement:
		assertEquals( "SELECT id, a_string_field, a_int_field FROM unittestdata WHERE a_boolint_field=1 GROUP BY id ORDER BY id ASC ;",
				bldr.toString() ) ;

		Cursor crs = bldr.executeOn( m_db ) ;
		assertNotNull( crs ) ;
		if( crs.moveToFirst() )
		{
			do
			{
				assertFalse( crs.getColumnIndex( "id" ) == COLUMN_NOT_FOUND ) ;
				assertFalse( crs.getColumnIndex( "a_string_field" )
						== COLUMN_NOT_FOUND ) ;
				assertFalse( crs.getColumnIndex( "a_int_field" )
						== COLUMN_NOT_FOUND ) ;
				assertEquals( COLUMN_NOT_FOUND,
						crs.getColumnIndex( "a_boolint_field" ) ) ;  // excluded
				Log.d( LOG_TAG, (new StringBuilder())
						.append( "Selection test: " )
						.append( " id [" )
						.append( crs.getLong( crs.getColumnIndex( "id" ) ) )
						.append( "] string [" )
						.append( crs.getString(
								crs.getColumnIndex( "a_string_field" ) ) )
						.append( "] int [" )
						.append( crs.getInt(
								crs.getColumnIndex( "a_int_field" ) ) )
						.append( "]" )
						.toString()
					);
			}
			while( crs.moveToNext() ) ;
			SQLitePortal.closeCursor( crs ) ;
		}
		else
		{
			SQLitePortal.closeCursor( crs ) ;
			fail( "Got no results in first selection." ) ;
		}
		bldr.allColumns().groupBy(null).orderBy(null).limit(1) ;
		assertEquals( "SELECT * FROM unittestdata WHERE a_boolint_field=1 LIMIT 1 ;",
				bldr.toString() ) ;
		assertNull( bldr.m_sGroupBy ) ;
		assertTrue( bldr.m_mapOrderBy.isEmpty() ) ;
		crs = bldr.executeOn( m_db ) ;
		assertNotNull( crs ) ;
		assertEquals( 1, crs.getCount() ) ;
		SQLitePortal.closeCursor( crs ) ;
	}

	/**
	 * Exercises the newer grammar for binding and executing an
	 * {@link SelectionBuilder}.
	 * Intentionally doesn't test any of the other inner workings of the
	 * builder, since those are covered elsewhere.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@Test
	public void testSelectionBuilderBound()
	{
		long nID = QueryBuilder.insertInto( m_db, TEST_TABLE_NAME )
				.setValues( getTestableValues() ).execute() ;
		String sID = Long.toString(nID) ;
		Cursor crs = QueryBuilder.selectFrom( m_db, TEST_TABLE_NAME )
				.where( "id=?", sID )
				.execute()
				;
		assertNotNull(crs) ;
		if( crs.moveToFirst() )
		{
			assertEquals( nID, crs.getLong(0) ) ;
			SQLitePortal.closeCursor( crs ) ;
		}
		else
		{
			SQLitePortal.closeCursor( crs ) ;
			fail( "Didn't get the expected row!" ) ;
		}
	}

	/**
	 * Uses some unlikely selection grammar in order to stress-test various
	 * functions of {@link SelectionBuilder}.
	 * @since zerobandwidth-net/android 0.2.1 (#53)
	 */
	@Test
	public void testWeirdSelectionGrammars()
	{
		// Ensure that a null string array is interpreted as "select all".
		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.columns( (String[])null ) ;
		assertEquals( "SELECT * FROM unittestdata ;", bldr.toString() ) ;

		// Ensure that a null collection is interpreted as "select all".
		bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.columns( (Collection<String>)null ) ;
		assertEquals( "SELECT * FROM unittestdata ;", bldr.toString() ) ;

		// Ensure that redundant columns are added only once.
		bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.columns( "bork", "bork", "bork" ) ;
		assertEquals( 1, bldr.getColumnList().length ) ;
		Collection<String> asCols = new ArrayList<>() ;
		asCols.add( "bork" ) ;
		asCols.add( "bork" ) ;
		asCols.add( "bork" ) ;
		bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME ).columns( asCols ) ;
		assertEquals( 1, bldr.getColumnList().length ) ;
	}

	/**
	 * Exercises the failure mechanism built into the base class's
	 * {@link QueryBuilder#execute} method.
	 * The syntax/grammar used in each {@code try} block is intentionally wrong,
	 * in order to trigger the exception.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@Test
	public void testFailedBuilderBindings()
	{
		QueryBuilder.UnboundException xCaught = null ;

		try
		{
			QueryBuilder.insertInto( TEST_TABLE_NAME )
					.setValues( getTestableValues() ).execute() ;
		}
		catch( QueryBuilder.UnboundException x )
		{ xCaught = x ; }
		assertNotNull( xCaught ) ;

		xCaught = null ;

		try
		{
			QueryBuilder.update( TEST_TABLE_NAME )
					.setValues( getTestableValues() ).updateAll().execute() ;
		}
		catch( QueryBuilder.UnboundException x )
		{ xCaught = x ; }
		assertNotNull( xCaught ) ;

		xCaught = null ;

		try
		{
			QueryBuilder.selectFrom( TEST_TABLE_NAME )
					.allColumns().execute() ;
		}
		catch( QueryBuilder.UnboundException x )
		{ xCaught = x ; }
		assertNotNull( xCaught ) ;

		xCaught = null ;

		try
		{
			QueryBuilder.deleteFrom( TEST_TABLE_NAME ).deleteAll().execute() ;
		}
		catch( QueryBuilder.UnboundException x )
		{ xCaught = x ; }
		assertNotNull( xCaught ) ;
	}

	/**
	 * Exercises the {@link SelectionBuilder#having(String)} mutator for the
	 * sake of code coverage.
	 * @since zerobandwidth-net/android 0.2.1 (#53)
	 */
	@Test
	public void testHaving()
	{
		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.having( "foo" ) ; // Obviously bogus SQL, but who cares?
		assertEquals( "foo", bldr.m_sHaving ) ;
		assertEquals( "SELECT * FROM unittestdata HAVING foo ;",
				bldr.toString() ) ;
	}

	/**
	 * More fully exercises {@link SelectionBuilder#orderBy(String)}.
	 * @since zerobandwidth-net/android 0.2.1 (#53)
	 */
	@Test
	public void testOrderBy()
	{
		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.orderBy( "foo" ).orderBy( "foo" ).orderBy( "bar" ) ;
		assertEquals( "foo ASC, bar ASC", bldr.getOrderByClause() ) ;
		bldr.orderBy(null) ;
		assertNotNull( bldr.m_mapOrderBy ) ;
		assertNull( bldr.getOrderByClause() ) ;
		bldr.m_mapOrderBy = null ; // might break the class under normal ops?
		assertNull( bldr.getOrderByClause() ) ;
	}
}
