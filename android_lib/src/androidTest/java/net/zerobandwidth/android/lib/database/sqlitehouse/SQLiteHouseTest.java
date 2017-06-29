package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
	protected static class NoIntrospectionClass
	extends SQLiteHouse<NoIntrospectionClass>
	{
		protected NoIntrospectionClass( SQLiteHouse.Factory factory )
		{ super(factory) ; }
	}

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

	/**
	 * Provides a valid context for the unit test.
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
		{
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			assertNotNull(antCol) ;

			switch( fld.getName() )
			{
				case "m_nFargleID":
					assertEquals( "fargle_id", antCol.value() ) ;
					break ;
				case "m_sString":
					assertEquals( "fargle_string", antCol.value() ) ;
					break ;
				case "m_zInteger":
					assertEquals( "fargle_num", antCol.value() ) ;
					break ;
				default:
					fail( (new StringBuilder())
							.append( "Found field in Fargle [" )
							.append( fld.getName() )
							.append( "] that should not have been discovered." )
							.toString()
						);
			}
		}
		assertEquals( "m_nFargleID", dbh.m_mapKeys.get(Fargle.class).getName() ) ;

		// Test discovery in Dargle class.
		List<Field> afldDargle = dbh.m_mapFields.get( Dargle.class ) ;
		assertEquals( 2, afldDargle.size() ) ;
		for( Field fld : afldDargle )
		{
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			assertNotNull(antCol) ;

			switch( fld.getName() )
			{
				case "m_sString":
					assertEquals( "dargle_string", antCol.value() ) ;
					break ;
				case "m_bBoolean":
					assertEquals( "is_dargly", antCol.value() ) ;
					break ;
				case "m_zIgnoreThisField":
				default:
					fail( (new StringBuilder())
							.append( "Found field in Dargle [" )
							.append( fld.getName() )
							.append( "] that should not have been discovered." )
							.toString()
						);
			}
		}
		assertEquals( "m_sString", dbh.m_mapKeys.get(Dargle.class).getName() ) ;

		// Test discovery in Blargh class.
		List<Field> afldBlargh = dbh.m_mapFields.get( Blargh.class ) ;
		assertEquals( 1, afldBlargh.size() ) ;
		assertEquals( "m_sString", afldBlargh.get(0).getName() ) ;
	}
}
