package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.SQLiteColumnInfo;
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
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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
	protected static Context getTestContext()
	{ return InstrumentationRegistry.getTargetContext() ; }

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
	throws Exception // Any uncaught exception at this point is a failure.
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
	 * Ensures that {@link SQLiteHouse#processFieldsOfClasses()} properly
	 * discovers annotated fields and ignores non-annotated fields. Also ensures
	 * that each table's primary key is discovered.
	 */
	@Test
	public void testFieldDiscovery()
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;
		assertEquals( 3, dbh.m_mapFields.size() ) ;

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
		assertEquals( "m_nFargleID", dbh.m_mapKeys.get(Fargle.class).getName() ) ;

		// Test discovery in Dargle class.
		List<Field> afldDargle = dbh.m_mapFields.get( Dargle.class ) ;
		assertEquals( 2, afldDargle.size() ) ;
		for( Field fld : afldDargle )
		{ // Verify that only annotated fields were discovered.
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			assertNotNull(antCol) ;
		}
		assertEquals( "m_sString", afldDargle.get(0).getName() ) ;
		assertEquals( "m_bBoolean", afldDargle.get(1).getName() ) ;
		assertEquals( "m_sString", dbh.m_mapKeys.get(Dargle.class).getName() ) ;

		// Test discovery in Blargh class.
		List<Field> afldBlargh = dbh.m_mapFields.get( Blargh.class ) ;
		assertEquals( 1, afldBlargh.size() ) ;
		assertEquals( "m_sString", afldBlargh.get(0).getName() ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse#getTableCreationSQL}.
	 */
	@Test
	public void testTableCreationSQL()
	{
		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, getTestContext(), null ) ;

		String sFargleSQL = dbh.getTableCreationSQL( Fargle.class,
				Fargle.class.getAnnotation( SQLiteTable.class ) ) ;
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

		String sDargleSQL = dbh.getTableCreationSQL( Dargle.class, null ) ;
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

		String sBlarghSQL = dbh.getTableCreationSQL( Blargh.class, null ) ;
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

	/**
	 * Ensures that, having opened a database connection and created the
	 * database for the first time, the file creates what we expected.
	 * @see SQLiteHouse#onCreate
	 * @see SQLiteHouse#getTableCreationSQL
	 * @see <a href="http://www.sqlite.org/lang_analyze.html">SQLite Documentation: ANALYZE</a>
	 */
	@Test
	public void testDatabaseCreation()
	{
		Context ctx = getTestContext() ;
		ctx.deleteDatabase( ValidSpecClass.class
				.getAnnotation(SQLiteDatabaseSpec.class).database_name() ) ;

		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, ctx, null ) ;

		try
		{
			dbh.openDB() ;
			//noinspection StatementWithEmptyBody
			while( ! dbh.isConnected() ) ; // Wait for a connection.
			Map<String,SQLiteColumnInfo> mapInfo =
					dbh.getColumnMapForTable( "fargles" ) ;
			assertEquals( 4, mapInfo.size() ) ; // 3 defined plus auto-ID
			SQLiteColumnInfo infoFargleID = mapInfo.get("fargle_id") ;
			assertEquals( 1, infoFargleID.nColumnID ) ;
			assertEquals( Refractor.SQLITE_TYPE_INT, infoFargleID.sColumnType );
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
	 * @see SQLiteHouse#getAddColumnSQL
	 * @see SQLiteHouse#getTableCreationSQL
	 */
	@Test
	public void testDatabaseUpgrade()
	{
		Context ctx = getTestContext() ;
		String sDatabaseName = ValidSpecClass.class
				.getAnnotation( SQLiteDatabaseSpec.class ).database_name() ;

		ctx.deleteDatabase( sDatabaseName ) ;

		ValidSpecClass dbh = SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class, ctx, null ) ;
		try
		{
			dbh.openDB() ;
			//noinspection StatementWithEmptyBody
			while( ! dbh.isConnected() ) ;
		}
		finally
		{ dbh.close() ; }

		UpgradeSpecClass dbhUpgrade = SQLiteHouse.Factory.init().getInstance(
				UpgradeSpecClass.class, ctx, null ) ;
		try
		{
			dbhUpgrade.openDB() ;
			//noinspection StatementWithEmptyBody
			while( ! dbhUpgrade.isConnected() ) ;
			assertEquals( sDatabaseName, dbhUpgrade.getDatabaseName() ) ;
			assertEquals( 2, dbhUpgrade.getLatestSchemaVersion() ) ;

			// Show that the "fargles" table got upgraded.
			Map<String,SQLiteColumnInfo> mapFlargle =
					dbhUpgrade.getColumnMapForTable( "fargles" ) ;
			assertEquals( "'NEW!'", mapFlargle.get("flargle_addition").sDefault );

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
}
