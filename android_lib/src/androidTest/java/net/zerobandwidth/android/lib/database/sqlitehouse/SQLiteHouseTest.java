package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.SQLiteColumnInfo;
import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.querybuilder.DeletionBuilder;
import net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.StringLens;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.BorkBorkBork;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Flargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.UpgradeSpecClass;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.ValidSpecClass;

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
	 * Shorthand to provide a valid context for the unit test.
	 * @return a usable context
	 */
	public static Context getTestContext()
	{ return InstrumentationRegistry.getTargetContext() ; }

	/**
	 * Allows the unit test classes to get a testable instance of a
	 * {@link SQLiteHouse} descendant.
	 * @param clsSpec the database specification class under test
	 * @param <DBS> the database specification class under test
	 * @return a testable instance of the class
	 * @since zerobandwidth-net/android 0.2.1 (#56)
	 */
	public static <DBS extends SQLiteHouse> DBS getTestableInstanceOf( Class<DBS> clsSpec )
	{
		return SQLiteHouse.Factory.init().getInstance(
				clsSpec, getTestContext() ) ;
	}

	/**
	 * Milliseconds until we give up on a connection to a database.
	 */
	public static final int CONNECTION_TIMEOUT = 1000 ;

	/**
	 * Shorthand for obtaining a connection to a test database.
	 * @param dbh an instance of the test class
	 * @param <DBH> the test class
	 * @return an instance of the test class
	 */
	public static <DBH extends SQLiteHouse> DBH connectTo( DBH dbh )
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

	/** Ensures that the factory successfully processes the annotation. */
	@Test
	public void testFactorySuccess()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		assertEquals( "valid_spec_class_db", dbh.getDatabaseName() ) ;
		assertEquals( 1, dbh.getLatestSchemaVersion() ) ;
		assertEquals( 3, dbh.m_aclsSchema.size() ) ;
		assertEquals( 3, dbh.getSchemaClasses().size() ) ;
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
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		assertEquals( 3, dbh.m_mapReflections.size() ) ;
		assertNotNull( dbh.describe( Fargle.class ) ) ;
		assertNotNull( dbh.describe( Dargle.class ) ) ;
		assertNotNull( dbh.describe( Blargh.class ) ) ;
	}

	/**
	 * Ensures that, having opened a database connection and created the
	 * database for the first time, the file creates what we expected.
	 * @see SQLiteHouse#onCreate
	 * @see <a href="http://www.sqlite.org/lang_analyze.html">SQLite Documentation: ANALYZE</a>
	 */
	@SuppressWarnings( "deprecation" ) // verify deprecated stuff also works
	@Test
	public void testDatabaseCreation()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;

		try
		{
			connectTo(dbh) ;
			Map<String,SQLiteColumnInfo> mapInfo =
					dbh.getColumnMapForTable( "fargles" ) ;
			assertEquals( 4, mapInfo.size() ) ; // 3 defined plus auto-ID
			SQLiteColumnInfo infoFargleID = mapInfo.get("fargle_id") ;
			assertEquals( 1, infoFargleID.nColumnID ) ;
			// Continue testing legacy constant until it's removed.
			assertEquals( SQLITE_TYPE_INT, infoFargleID.sColumnType );
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
	{
		delete( ValidSpecClass.class ) ;
		delete( UpgradeSpecClass.class ) ;

		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		try { connectTo(dbh) ; }
		finally { dbh.close() ; }

		UpgradeSpecClass dbhUpgrade = UpgradeSpecClass.getTestInstance() ;
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
	@SuppressWarnings( "deprecation" ) // TODO (deprecation) remove in next major version
	public void testGetQueryContext()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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

	/** Exercises {@link SQLiteHouse#search(SQLightable)}. */
	@Test
	public void testSearch()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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

			// (#50) Now look for something that won't be there.
			Fargle fargleThree = new Fargle( 30, "Not inserted.", 3 ) ;
			assertNull( dbh.search(fargleThree) ) ;
		}
		finally
		{ dbh.close() ; }
	}


	/**
	 * Exercises {@link SQLiteHouse#search(SQLightable)} with
	 * {@link Blargh}, which defines no key columns.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testSearchWithoutKeyColumn()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SchematicException xSchema = null ;
		try
		{
			connectTo(dbh) ;
			Blargh blargh = new Blargh( "blaaaaargh" ) ;
			dbh.search( blargh ) ;
		}
		catch( SchematicException x )
		{ xSchema = x ; }
		finally
		{ dbh.close() ; }
		assertNotNull( xSchema ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#search(Class,String)}.
	 * @since zerobandwidth-net/android 0.1.5 (#43)
	 */
	@Test
	public void testSearchByStringID()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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

			// (#50) Now look for something that won't be there.
			assertNull( dbh.search( Dargle.class, "not_a_real_dargle" ) ) ;
		}
		finally
		{ dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#search(Class,String)} with
	 * {@link Blargh}, which defines no key columns.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testSearchByStringIDWithoutKeyColumn()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SchematicException xSchema = null ;
		try
		{
			connectTo(dbh) ;
			dbh.search( Blargh.class, "blaaaaargh" ) ;
		}
		catch( SchematicException x )
		{ xSchema = x ; }
		finally
		{ dbh.close() ; }
		assertNotNull( xSchema ) ;
	}

	/** Exercises {@link SQLiteHouse#select(Class,long)}. */
	@Test
	public void testSelect()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		try
		{
			connectTo(dbh) ;
			Fargle fargleOne = new Fargle( 30, "Get this one first.", 1 ) ;
			long idOne = dbh.insert(fargleOne) ;
			Fargle fargleTwo = new Fargle( 60, "Then get this one second.", 2 );
			long idTwo = dbh.insert(fargleTwo) ;
			assertTrue( fargleOne.equals( dbh.select(Fargle.class,idOne) ) ) ;
			assertTrue( fargleTwo.equals( dbh.select(Fargle.class,idTwo) ) ) ;

			// (#50) Now look for something that won't be there.
			assertNull( dbh.select( Fargle.class, 90 ) ) ;
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
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	 * Exercises {@link SQLiteHouse#processResultSet(Class, Cursor)}.
	 * @since zerobandwidth-net/android 0.1.5 (#43)
	 */
	@Test
	public void testProcessResultSet()
	{
		final int ITERATIONS = 10 ;                            // Tune to taste.
		final Random RNG = new Random() ;
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		Cursor crs = null ;
		List<Fargle> aInputs = new ArrayList<>() ;
		List<Fargle> aResults ;
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

			assertNotNull(aResults) ;
			assertEquals( aInputs.size(), aResults.size() ) ;
			for( int i = 0 ; i < aInputs.size() ; i++ )
				assertTrue( aInputs.get(i).equals( aResults.get(i) ) ) ;

			// (#50) Also verify that empty cursors are handled properly.
			crs = dbh.selectFrom( Fargle.class )
					.where( "fargle_string=?", "boogityboogityboo" )
					.execute()
					;
			aResults = dbh.processResultSet( Fargle.class, crs ) ;
			assertEquals( 0, crs.getCount() ) ;
			assertEquals( 0, aResults.size() ) ;
			//noinspection deprecation
			aResults = dbh.processResultSet( Fargle.class, crs ) ;
			assertEquals( 0, aResults.size() ) ;
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLiteHouse#delete(SQLightable)}.
	 */
	@Test
	public void testDelete()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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
	 * Exercises {@link SQLiteHouse#delete(SQLightable)} with
	 * {@link Blargh}, which defines no key columns.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testDeleteWithoutKeyColumn()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SchematicException xSchema = null ;
		try
		{
			connectTo(dbh) ;
			dbh.delete( new Blargh( "blaaaaaaaargh" ) ) ;
		}
		catch( SchematicException x )
		{ xSchema = x ; }
		finally
		{ dbh.close() ; }
		assertNotNull( xSchema ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#deleteFrom(Class)}.
	 */
	@Test
	public void testDeleteFrom()
	{
		delete( ValidSpecClass.class ) ;
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
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

	/**
	 * Exercises {@link SQLiteHouse#getRefractorForField(Field)} with good and
	 * bad inputs.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testGetRefractor()
	throws NoSuchFieldException
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;

		SQLightable.Reflection<Fargle> tbl =
				SQLightable.Reflection.reflect( Fargle.class ) ;
		assertEquals( StringLens.class,
				dbh.getRefractorForField(
						tbl.getField("fargle_string") ).getClass()
			);

		IntrospectionException xIntro = null ;
		try
		{ // Get an instance of a broken refractor.
			dbh.getRefractorForField(
					BorkBorkBork.class.getField( "m_oBorked" ) ) ;
		}
		catch( IntrospectionException x ) { xIntro = x ; }
		assertNotNull( xIntro ) ;

		xIntro = null ;
		try
		{ // Try to get a refractor for a column that can't provide one.
			dbh.getRefractorForField(
					BorkBorkBork.class.getField( "m_oAlsoBorked" ) ) ;
		}
		catch( IntrospectionException x ) { xIntro = x ; }
		assertNotNull( xIntro ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#setSchemaClasses(List)} by forcing it to
	 * rewrite the schematic class list.
	 * This should never happen in practice, but fills a gap in test coverage.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testReprocessSchema()
	{
		SQLiteHouse.Factory dbf = SQLiteHouse.Factory.init() ;
		ValidSpecClass dbh = dbf.getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		dbh.setSchemaClasses( dbf.m_aclsSchema ) ;       // Force re-processing.
		assertEquals( 3, dbh.m_aclsSchema.size() ) ;
		assertTrue( dbh.m_aclsSchema.contains( Fargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Dargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Blargh.class ) ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#processReflections()} by forcing it to
	 * rewrite the reflection map.
	 * This should never happen in practice, but fills a gap in test coverage.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testReprocessReflections()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		dbh.processReflections() ;  // Force a re-processing of the reflections.
		assertTrue( dbh.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertTrue( dbh.m_mapReflections.containsKey( Dargle.class ) ) ;
		assertTrue( dbh.m_mapReflections.containsKey( Blargh.class ) ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#describe}.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testDescribe()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		assertNotNull( dbh.describe( Fargle.class ) ) ;
		assertNull( dbh.describe( BorkBorkBork.class ) ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#getReflection(Class)}.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testGetReflection()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		assertNotNull( dbh.getReflection( Dargle.class ) ) ;
		SchematicException xSchema = null ;
		try { dbh.getReflection( Flargle.class ) ; }
		catch( SchematicException x ) { xSchema = x ; }
		assertNotNull( xSchema ) ;
	}
}
