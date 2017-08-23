package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.SQLiteColumnInfo;
import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.querybuilder.DeletionBuilder;
import net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Flargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Quargle;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_TYPE_INT;

/**
 * Exercises {@link SQLiteHouse}.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseTest
{
	/**
	 * Used by {@link #testFactoryFailureFromNoAnnotation()} to verify that the
	 * {@link SQLiteHouse.Factory} will refuse to process a class that is not
	 * properly annotated.
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	protected static class NoIntrospectionClass
	extends SQLiteHouse<NoIntrospectionClass>
	{
		protected NoIntrospectionClass( SQLiteHouse.Factory factory )
		{ super(factory) ; }
	}

	/**
	 * Used as the canonical "valid" {@link SQLiteHouse} implementation for
	 * various unit tests.
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	@SuppressWarnings( "DefaultAnnotationParam" )
	@SQLiteDatabaseSpec(
			database_name = "valid_spec_class_db",
			schema_version = 1,
			classes = { Fargle.class, Dargle.class, Blargh.class }
	)
	protected static class ValidSpecClass
	extends SQLiteHouse<ValidSpecClass>
	{
		protected ValidSpecClass( SQLiteHouse.Factory factory )
		{ super(factory) ; }

		protected SQLiteDatabase getDB()
		{ return m_db ; }
	}


	@SQLiteDatabaseSpec(
			database_name = "valid_spec_class_db",
			schema_version = 2,
			classes =
				{ Flargle.class, Dargle.class, Quargle.class, Blargh.class }
	)
	protected static class UpgradeSpecClass
	extends SQLiteHouse<UpgradeSpecClass>
	{
		protected UpgradeSpecClass( SQLiteHouse.Factory factory )
		{ super(factory) ; }
	}

	/**
	 * Shorthand to provide a valid context for the unit test.
	 * @return a usable context
	 */
	public static Context getTestContext()
	{ return InstrumentationRegistry.getTargetContext() ; }

	/**
	 * Milliseconds until we give up on a connection to a database.
	 */
	public static final int CONNECTION_TIMEOUT = 1000 ;

	/**
	 * Shorthand for obtaining a connection to a test database.
	 * @param dbh an instance of the test class
	 * @param <DBH> the test class
	 * @return an instance of the test class
	 * @throws Exception if anything goes wrong while connecting
	 */
	public static <DBH extends SQLiteHouse> DBH connectTo( DBH dbh )
	throws Exception
	{
		dbh.openDB() ;
		long tsGiveUp = (new Date()).getTime() + CONNECTION_TIMEOUT ;
		//noinspection StatementWithEmptyBody
		while( ! dbh.isConnected() && (new Date()).getTime() < tsGiveUp ) ;
		if( ! dbh.isConnected() )
			fail( "Couldn't connect to database!" ) ;
		return dbh ;
	}

	/**
	 * Shorthand to delete the database specified by the given class.
	 * @param cls the test database class
	 * @param <DBH> the test database class
	 */
	public static <DBH extends SQLiteHouse> void delete( Class<DBH> cls )
	{
		getTestContext().deleteDatabase(
			cls.getAnnotation( SQLiteDatabaseSpec.class ).database_name() ) ;
	}

	/**
	 * Ensures that the class throws an exception if no annotation is provided.
	 */
	@Test
	public void testFactoryFailureFromNoAnnotation()
	{
		IntrospectionException xCaught = null ;

		try
		{
			SQLiteHouse.Factory.init().getInstance( NoIntrospectionClass.class,
					getTestContext(), null ) ;
		}
		catch( IntrospectionException xIntro )
		{ xCaught = xIntro ; }
		catch( Exception xFail )
		{ fail( xFail.getMessage() ) ; }   // Any other exception is unexpected.

		assertNotNull( xCaught ) ;
		assertNull( xCaught.getCause() ) ;  // Ensure it's the reason we expect.
	}

	/**
	 * Ensures that the factory successfully processes the annotation.
	 * @throws Exception if anything goes wrong (implies test failure)
	 */
	@Test
	public void testFactorySuccess()
	throws Exception // Any uncaught exception is a failure.
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		assertEquals( "valid_spec_class_db", dbh.getDatabaseName() ) ;
		assertEquals( 1, dbh.getLatestSchemaVersion() ) ;
		assertEquals( 3, dbh.m_aclsSchema.size() ) ;
		assertTrue( dbh.m_aclsSchema.contains( Fargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Dargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Blargh.class ) ) ;
	}

	/**
	 * Ensures that {@link SQLiteHouse} properly discovers annotated fields and
	 * ignores non-annotated fields. Also ensures that each table's primary key
	 * is discovered.
	 */
	@Test
	public void testFieldDiscovery()
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
//		assertEquals( 3, dbh.m_mapFields.size() ) ;
		assertEquals( 3, dbh.m_mapReflections.size() ) ;
/* Moved to separate SQLightableTest class in 0.1.7 (#50).
		// Test discovery in Fargle class.
		List<Field> afldFargle = dbh.m_mapFields.get( Fargle.class ) ;
		assertEquals( 3, afldFargle.size() ) ;
		for( Field fld : afldFargle )
		{ // Verify that only annotated fields were discovered.
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			assertNotNull(antCol) ;
		}
		// Assuming that the sorting worked, we can call out fields explicitly.
		assertEquals( "m_nFargleID", afldFargle.get(0).getName() ) ;
		assertEquals( "m_sString", afldFargle.get(1).getName() ) ;
		assertEquals( "m_zInteger", afldFargle.get(2).getName() ) ;

		// Test discovery in Dargle class.
		List<Field> afldDargle = dbh.m_mapFields.get( Dargle.class ) ;
		assertEquals( 3, afldDargle.size() ) ;
		for( Field fld : afldDargle )
		{ // Verify that only annotated fields were discovered.
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			assertNotNull(antCol) ;
		}
		assertEquals( "m_nRowID", afldDargle.get(0).getName() ) ;       // (#43)
		assertEquals( "m_sString", afldDargle.get(1).getName() ) ;
		assertEquals( "m_bBoolean", afldDargle.get(2).getName() ) ;
		assertEquals( "m_sString", dbh.m_mapKeys.get(Dargle.class).getName() ) ;

		// Test discovery in Blargh class.
		List<Field> afldBlargh = dbh.m_mapFields.get( Blargh.class ) ;
		assertEquals( 1, afldBlargh.size() ) ;
		assertEquals( "m_sString", afldBlargh.get(0).getName() ) ;
*/
	}

	/*
	 * Exercises {@link SQLiteHouse#getTableCreationSQL}.
	 * Moved to {@link SQLightableTest}.
	 */
/*	@Test
	public void testTableCreationSQL()
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;

		SQLiteHouse.QueryContext<ValidSpecClass> qctx = dbh.getQueryContext() ;

		qctx.loadTableDef( Fargle.class ) ;
		String sFargleSQL = dbh.getTableCreationSQL(qctx) ;
		String sFargleExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "fargles" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", fargle_id INTEGER UNIQUE NOT NULL" )
				.append( ", fargle_string TEXT NULL DEFAULT NULL" )
				.append( ", fargle_num INTEGER NULL DEFAULT 42" )
				.append( " )" )
				.toString()
				;
		assertEquals( sFargleExpected, sFargleSQL ) ;

		qctx.loadTableDef( Dargle.class ) ;
		String sDargleSQL = dbh.getTableCreationSQL(qctx) ;
		String sDargleExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "dargles" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", dargle_string TEXT UNIQUE NOT NULL" )
				.append( ", is_dargly INTEGER NULL DEFAULT 1" )
				.append( " )" )
				.toString()
				;
		assertEquals( sDargleExpected, sDargleSQL ) ;

		qctx.loadTableDef( Blargh.class ) ;
		String sBlarghSQL = dbh.getTableCreationSQL(qctx) ;
		String sBlarghExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "blargh" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", blargh_string TEXT NULL DEFAULT NULL" )
				.append( " )" )
				.toString()
				;
		assertEquals( sBlarghExpected, sBlarghSQL ) ;
	}
*/
	/**
	 * Ensures that, having opened a database connection and created the
	 * database for the first time, the file creates what we expected.
	 * @see SQLiteHouse#onCreate
	 * @see <a href="http://www.sqlite.org/lang_analyze.html">SQLite Documentation: ANALYZE</a>
	 */
	@SuppressWarnings( "deprecation" ) // verify deprecated stuff also works
	@Test
	public void testDatabaseCreation()
	throws Exception // Any uncaught exception is a failure.
	{
		Context ctx = getTestContext() ;

		delete( ValidSpecClass.class ) ;

		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, ctx, null ) ;

		try
		{
			connectTo(dbh) ;
			Map<String,SQLiteColumnInfo> mapInfo =
					dbh.getColumnMapForTable( "fargles" ) ;
			assertEquals( 4, mapInfo.size() ) ; // 3 defined plus auto-ID
			SQLiteColumnInfo infoFargleID = mapInfo.get("fargle_id") ;
			assertEquals( 1, infoFargleID.nColumnID ) ;
			// Continue testing legacy constant until it's removed.
			assertEquals( Refractor.SQLITE_TYPE_INT, infoFargleID.sColumnType );
			assertEquals( SQLITE_TYPE_INT, infoFargleID.sColumnType ) ;
			assertTrue( infoFargleID.bNotNull ) ;
			assertEquals( null, infoFargleID.sDefault ) ;
			assertFalse( infoFargleID.bPrimaryKey ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Ensures that the upgrade algorithm works, by swapping an upgraded table
	 * into the definition.
	 * @see SQLiteHouse#onUpgrade
	 */
	@Test
	public void testDatabaseUpgrade()
	throws Exception // Any uncaught exception is a failure.
	{
		Context ctx = getTestContext() ;

		delete( ValidSpecClass.class ) ;
		delete( UpgradeSpecClass.class ) ;

		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, ctx, null ) ;
		try { connectTo(dbh) ; }
		finally { dbh.close() ; }

		UpgradeSpecClass dbhUpgrade = SQLiteHouse.Factory.init().getInstance(
				UpgradeSpecClass.class, ctx, null ) ;
		try
		{
			connectTo(dbhUpgrade) ;

			// Assert that the new database name is the same as the old one, but
			// the version number has gone up.
			String sAntDatabaseName = ValidSpecClass.class
					.getAnnotation( SQLiteDatabaseSpec.class ).database_name() ;
			assertEquals( sAntDatabaseName, dbhUpgrade.getDatabaseName() ) ;
			assertEquals( 2, dbhUpgrade.getLatestSchemaVersion() ) ;

			// Show that the "fargles" table got upgraded.
			Map<String,SQLiteColumnInfo> mapFlargle =
					dbhUpgrade.getColumnMapForTable( "fargles" ) ;
			SQLiteColumnInfo infoAddition = mapFlargle.get("flargle_addition") ;
			assertNotNull( infoAddition ) ;
			assertEquals( "'NEW!'", infoAddition.sDefault ) ;

			// Show that the "quargles" table got created.
			List<SQLiteColumnInfo> infoQuargle =
					dbhUpgrade.getColumnListForTable("quargles") ;
			assertNotNull( infoQuargle ) ;
			assertEquals( 2, infoQuargle.size() ) ;
			assertEquals( "quargle", infoQuargle.get(1).sColumnName ) ;
		}
		finally
		{ dbhUpgrade.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#getQueryContext(Class)}, which in turn
	 * exercises {@link SQLiteHouse#getQueryContext()}.
	 */
	@Test
	public void testGetQueryContext()
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		SQLiteHouse.QueryContext<ValidSpecClass> qctx =
				dbh.getQueryContext( Fargle.class ) ;
		assertEquals( Fargle.class, qctx.clsTable ) ;
		assertEquals( "fargles", qctx.sTableName ) ;
		assertEquals( Fargle.class.getAnnotation( SQLiteTable.class ),
				qctx.antTable ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#insert}.
	 */
	@Test
	public void testInsertion()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		Cursor crs = null ;
		try
		{
			connectTo(dbh) ;
			Fargle fargle = new Fargle( 47, "Foo!", 99 ) ;
			dbh.insert( fargle ) ;
			crs = QueryBuilder.selectFrom( dbh.getDB(), "fargles" )
					.where( "fargle_id=?", "47" )
					.execute()
					;
			assertTrue( crs.moveToFirst() ) ;
			assertEquals( 47, crs.getInt( crs.getColumnIndex( "fargle_id" ) ) );
			assertEquals( "Foo!", crs.getString(
					crs.getColumnIndex( "fargle_string" ) ) ) ;
			assertEquals( 99, crs.getInt( crs.getColumnIndex("fargle_num") ) ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#insert}, specifically examining whether the
	 * automatic rewrite of the auto-incremented row ID works properly.
	 * @since zerobandwidth-net/android 0.1.5 (#43)
	 */
	@Test
	public void testInsertionWithIDRewrite()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Dargle dargleOne = new Dargle( "one", false, 1 ) ;
			long nOne = dbh.insert(dargleOne) ;
			assertEquals( nOne, dargleOne.getRowID() ) ;
			Dargle dargleTwo = new Dargle( "two", true, 2 ) ;
			long nTwo = dbh.insert(dargleTwo) ;
			assertEquals( nTwo, dargleTwo.getRowID() ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#update(SQLightable)}.
	 */
	@Test
	public void testUpdate()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		Cursor crs = null ;
		try
		{
			connectTo(dbh) ;

			// Insert two entries into a table.
			String sOne = UUID.randomUUID().toString() ;
			Dargle dargleOne = new Dargle( sOne, true, 1 ) ;
			dbh.insert(dargleOne) ;
			String sTwo = UUID.randomUUID().toString() ;
			Dargle dargleTwo = new Dargle( sTwo, true, 2 ) ;
			dbh.insert(dargleTwo) ;

			// Update one.
			dbh.update( dargleOne.toggle() ) ;

			// Verify that only dargleOne's row was updated.
			crs = QueryBuilder.selectFrom( dbh.getDB(), "dargles" )
					.where( "dargle_string=?", sOne )
					.execute()
					;
			assertTrue( crs.moveToFirst() ) ;
			assertEquals( sOne,
					crs.getString( crs.getColumnIndex("dargle_string") ) ) ;
			assertFalse( SQLitePortal.getBooleanColumn( crs, "is_dargly") ) ;
			crs.close() ;

			// Verify that dargleTwo's row was NOT updated.
			crs = QueryBuilder.selectFrom( dbh.getDB(), "dargles" )
					.where( "dargle_string=?", sTwo )
					.execute()
					;
			crs.moveToFirst() ;
			assertTrue( SQLitePortal.getBooleanColumn( crs, "is_dargly") ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#update(Class)}.
	 */
	@Test
	public void testUpdateTable()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		Cursor crs = null ;
		try
		{
			connectTo(dbh) ;
			Dargle dargleOne =
					new Dargle( UUID.randomUUID().toString(), true, 1 ) ;
			Dargle dargleTwo =
					new Dargle( UUID.randomUUID().toString(), true, 2 ) ;
			dbh.insert(dargleOne) ;
			dbh.insert(dargleTwo) ;

			// Perform the update, using the builder from the method under test.
			ContentValues vals = new ContentValues() ;
			vals.put( "is_dargly", SQLitePortal.SQLITE_FALSE_INT ) ;
			dbh.update( Dargle.class )
					.setValues(vals)
					.where( "dargle_string=?", dargleOne.getString() )
					.execute()
					;

			// Verify that the intended row was updated.
			crs = QueryBuilder.selectFrom( dbh.getDB(), "dargles" )
					.where( "dargle_string=?", dargleOne.getString() )
					.execute()
					;
			crs.moveToFirst() ;
			assertFalse( SQLitePortal.getBooleanColumn( crs, "is_dargly" ) ) ;
			crs.close() ;

			// Verify that the unintended row was NOT updated.
			crs = QueryBuilder.selectFrom( dbh.getDB(), "dargles" )
					.where( "dargle_string=?", dargleTwo.getString() )
					.execute()
					;
			crs.moveToFirst() ;
			assertTrue( SQLitePortal.getBooleanColumn( crs, "is_dargly" ) ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#search(SQLightable)}.
	 */
	@Test
	public void testSearch()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Fargle fargleOne = new Fargle( 10, "Sought.", 1 ) ;
			dbh.insert(fargleOne) ;
			Fargle fargleTwo = new Fargle( 20, "Unsought.", 2 ) ;
			dbh.insert(fargleTwo) ;
			Fargle fargleResult = dbh.search(fargleOne) ;
			assertTrue( fargleOne.equals(fargleResult) ) ;
			assertFalse( fargleTwo.equals(fargleResult) ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#search(Class,String)}.
	 * @since zerobandwidth-net/android 0.1.5 (#43)
	 */
	@Test
	public void testSearchByStringID()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Dargle dargleOne = new Dargle( "dargle_one", true, 1 ) ;
			dbh.insert(dargleOne) ;
			Dargle dargleTwo = new Dargle( "dargle_two", false, 2 ) ;
			dbh.insert(dargleTwo) ;
			Dargle dargleResult = dbh.search( Dargle.class, "dargle_one" ) ;
			assertTrue( dargleResult.isDargly() ) ;         // matches dargleOne
			dargleResult = dbh.search( Dargle.class, "dargle_two" ) ;
			assertFalse( dargleResult.isDargly() ) ;        // matches dargleTwo
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#select(Class,long)}.
	 */
	@Test
	public void testSelect()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Fargle fargleOne = new Fargle( 30, "Get this one first.", 1 ) ;
			long idOne = dbh.insert(fargleOne) ;
			Fargle fargleTwo = new Fargle( 60, "Then get this one second.", 2 );
			long idTwo = dbh.insert(fargleTwo) ;
			assertTrue( fargleOne.equals( dbh.select(Fargle.class,idOne) ) ) ;
			assertTrue( fargleTwo.equals( dbh.select(Fargle.class,idTwo) ) ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#selectFrom(Class)}.
	 */
	@SuppressLint("DefaultLocale")
	@Test
	public void testSelectFrom()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		SQLiteHouse.QueryContext<ValidSpecClass> qctx =
				dbh.getQueryContext(Blargh.class) ;
		Cursor crs = null ;
		try
		{
			connectTo(dbh) ;
			Blargh blarghOne = new Blargh("one") ;
			dbh.insert(blarghOne) ;
			Blargh blarghTwo = new Blargh("two") ;
			long idTwo = dbh.insert(blarghTwo) ;

			crs = dbh.selectFrom(Blargh.class)
					.where( "blargh_string=?", "one" )
					.execute()
					;
			assertTrue( crs.moveToFirst() ) ;
			assertEquals( 1, crs.getCount() ) ;
			SQLitePortal.closeCursor(crs) ;

			crs = dbh.selectFrom( Blargh.class )
					.where( "blargh_string=?", "two" )
					.execute()
					;
			assertTrue( crs.moveToFirst() ) ;
			assertEquals( 1, crs.getCount() ) ;
			Blargh blarghTwoFetched = dbh.fromCursor( crs, Blargh.class ) ;
			SQLitePortal.closeCursor(crs) ;
			assertTrue( blarghTwoFetched.equals(blarghTwo) ) ;

			crs = dbh.selectFrom( Blargh.class )
					.where( String.format( "%s=%d",
							SQLiteHouse.MAGIC_ID_COLUMN_NAME, idTwo ) )
					.execute()
					;
			assertTrue( crs.moveToFirst() ) ;
			Blargh blarghTwoIDFetched = dbh.fromCursor( crs, Blargh.class ) ;
			SQLitePortal.closeCursor(crs) ;
			assertTrue( blarghTwoIDFetched.equals(blarghTwo) ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#processResultSet(Class, Cursor)}, which in
	 * turn exercises
	 * {@link SQLiteHouse#processResultSet(SQLiteHouse.QueryContext, Cursor, Class)}.
	 * @since zerobandwidth-net/android 0.1.5 (#43)
	 */
	@Test
	public void testProcessResultSet()
	throws Exception // Any uncaught exception is a failure.
	{
		final int ITERATIONS = 10 ;                            // Tune to taste.
		final Random RNG = new Random() ;
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		Cursor crs = null ;
		List<Fargle> aInputs = new ArrayList<>() ;
		List<Fargle> aResults = null ;
		try
		{
			connectTo(dbh) ;
			for( int i = 0 ; i < ITERATIONS ; i++ )
			{ // Seed the DB with a bunch of randomized data.
				Fargle fargle = new Fargle(
						RNG.nextInt(Integer.MAX_VALUE),
						UUID.randomUUID().toString(),
						RNG.nextInt(Integer.MAX_VALUE)
					);
				aInputs.add(fargle) ;
				dbh.insert(fargle) ;
			}
			crs = dbh.selectFrom( Fargle.class )
					.orderBy( SQLiteHouse.MAGIC_ID_COLUMN_NAME )
					.execute()
					;
			aResults = dbh.processResultSet( Fargle.class, crs ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
		assertNotNull(aResults) ;
		assertEquals( aInputs.size(), aResults.size() ) ;
		for( int i = 0 ; i < aInputs.size() ; i++ )
			assertTrue( aInputs.get(i).equals( aResults.get(i) ) ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#delete(SQLightable)}.
	 */
	@Test
	public void testDelete()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Dargle dargleOne =
					new Dargle( UUID.randomUUID().toString(), true, 1 ) ;
			Dargle dargleTwo =
					new Dargle( UUID.randomUUID().toString(), true, 2 ) ;
			dbh.insert(dargleOne) ;
			dbh.insert(dargleTwo) ;

			assertEquals( 1, dbh.delete(dargleOne) ) ;
			assertEquals( 0, dbh.delete(dargleOne) ) ;
			assertEquals( 1, dbh.delete(dargleTwo) ) ;
			assertEquals( 0, dbh.delete(dargleTwo) ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#deleteFrom(Class)}.
	 */
	@Test
	public void testDeleteFrom()
	throws Exception // Any uncaught exception is a failure.
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		try
		{
			connectTo(dbh) ;
			Blargh blarghAlice = new Blargh( "alice" ) ;
			dbh.insert( blarghAlice ) ;
			Blargh blarghBob = new Blargh( "bob" ) ;
			dbh.insert( blarghBob ) ;

			DeletionBuilder deleteAlice = dbh.deleteFrom( Blargh.class )
					.where( "blargh_string=?", "alice" ) ;
			int nDeletedAlice = deleteAlice.execute() ;
			assertEquals( 1, nDeletedAlice ) ;
			nDeletedAlice = deleteAlice.execute() ;
			assertEquals( 0, nDeletedAlice ) ;

			DeletionBuilder deleteBob = dbh.deleteFrom( Blargh.class )
					.where( "blargh_string=?", "bob" ) ;
			int nDeletedBob = deleteBob.execute() ;
			assertEquals( 1, nDeletedBob ) ;
			nDeletedBob = deleteBob.execute() ;
			assertEquals( 0, nDeletedBob ) ;
		}
		finally
		{ dbh.close() ; }
	}
}
