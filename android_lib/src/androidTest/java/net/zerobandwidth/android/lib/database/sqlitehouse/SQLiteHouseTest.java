package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.Test;
import org.junit.runner.RunWith;

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
	 * Ensures that the class throws an exception if no annotation is provided.
	 */
	@Test
	public void testFactoryFailureFromNoAnnotation()
	{
		IntrospectionException xCaught = null ;

		try
		{
			SQLiteHouse.Factory factory = new SQLiteHouse.Factory() ;
			factory.getInstance( NoIntrospectionClass.class,
					InstrumentationRegistry.getTargetContext(), null ) ;
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
		SQLiteHouse.Factory factory = new SQLiteHouse.Factory() ;
		ValidSpecClass dbh = factory.getInstance( ValidSpecClass.class,
				InstrumentationRegistry.getTargetContext(), null ) ;
		assertEquals( "valid_spec_class_db", dbh.getDatabaseName() ) ;
		assertEquals( 1, dbh.getLatestSchemaVersion() ) ;
		assertTrue( dbh.m_aclsSchema.contains( Fargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Dargle.class ) ) ;
		assertTrue( dbh.m_aclsSchema.contains( Blargh.class ) ) ;
	}
}
