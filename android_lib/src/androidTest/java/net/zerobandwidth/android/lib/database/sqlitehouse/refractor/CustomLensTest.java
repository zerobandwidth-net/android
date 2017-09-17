package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Sparkle;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Exercises features added to {@link SQLiteHouse} and {@link SQLiteColumn} to
 * support definition of custom field refractors.
 * @since zerobandwidth-net/android 0.1.5 (#41)
 */
@RunWith( AndroidJUnit4.class )
public class CustomLensTest
{
	/**
	 * Database specification for the database which tests custom refractors.
	 * @since zerobandwidth-net/android 0.1.5 (#41)
	 */
	@SQLiteDatabaseSpec(
			database_name = "custom_lens_test_db",
			classes = { Sparkle.class }
	)
	protected static class SparkleDB
	extends SQLiteHouse<SparkleDB>
	{
		public SparkleDB( SQLiteHouse.Factory factory )
		{ super(factory) ; }
	}

	/**
	 * Exercises {@link SQLiteHouse}'s ability to discover, and make use of, a
	 * custom refractor.
	 * @throws Exception if anything goes wrong (fails the test)
	 */
	@Test
	public void testCustomRefractor()
	throws Exception // Any uncaught exception is a failure.
	{
		Context ctx = SQLiteHouseTest.getTestContext() ;
		SQLiteHouseTest.delete( SparkleDB.class ) ;
		SparkleDB dbh = SQLiteHouse.Factory.init().getInstance(
				SparkleDB.class, ctx, null ) ;
		try
		{
			SQLiteHouseTest.connectTo( dbh ) ;
			assertEquals( CustomStringLens.class,
					dbh.getRefractorForField(
							Sparkle.class.getField("m_sValue") ).getClass() ) ;
			Sparkle oOrig = new Sparkle(null) ;
			long id = dbh.insert(oOrig) ;
			Sparkle oSelect = dbh.select( Sparkle.class, id ) ;
			assertEquals( CustomStringLens.DEFAULT_STRING,
					oSelect.m_sValue ) ;
		}
		finally
		{ dbh.close() ; }
	}
}
