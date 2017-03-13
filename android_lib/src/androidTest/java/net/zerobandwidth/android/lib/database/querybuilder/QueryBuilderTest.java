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

import static junit.framework.Assert.* ;

import static net.zerobandwidth.android.lib.database.MinimalUnitTestDBPortal.TEST_TABLE_NAME ;

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

	/** Persistent handle on the databse under the portal. */
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
		assertFalse( nID == InsertionBuilder.INSERT_FAILED ) ;
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
		assertFalse( nID == InsertionBuilder.INSERT_FAILED ) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Full insert result: " ).append( nID ).toString() ) ;
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
		SelectionBuilder bldr = QueryBuilder.selectFrom( TEST_TABLE_NAME )
				.distinct()
				.columns( "id", "a_string_field", "a_int_field" )
				.where( "a_boolint_field=1" )
				.groupBy( "id" )
				.orderBy( "id", SelectionBuilder.ORDER_ASC )
				;
		assertEquals( "SELECT id, a_string_field, a_int_field FROM unittestdata WHERE a_boolint_field=1 GROUP BY id ORDER BY id ASC ;",
				bldr.toString() ) ;
		assertTrue( bldr.m_bDistinct ) ;
		assertEquals( "a_boolint_field=1", bldr.getWhereFormat() ) ;
		assertEquals( null, bldr.getWhereParams() ) ;
		assertEquals( "id", bldr.m_sGroupBy ) ;
		assertEquals( SelectionBuilder.ORDER_ASC, bldr.m_mapOrderBy.get("id") ) ;
		Cursor crs = bldr.executeOn( m_db ) ;
		assertNotNull( crs ) ;
		if( crs.moveToFirst() )
		{
			do
			{
				assertFalse( crs.getColumnIndex( "id" )
						== SQLitePortal.COLUMN_NOT_FOUND ) ;
				assertFalse( crs.getColumnIndex( "a_string_field" )
						== SQLitePortal.COLUMN_NOT_FOUND ) ;
				assertFalse( crs.getColumnIndex( "a_int_field" )
						== SQLitePortal.COLUMN_NOT_FOUND ) ;
				assertEquals( SQLitePortal.COLUMN_NOT_FOUND,
						crs.getColumnIndex( "a_boolint_field" ) ) ; // excluded
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
}
