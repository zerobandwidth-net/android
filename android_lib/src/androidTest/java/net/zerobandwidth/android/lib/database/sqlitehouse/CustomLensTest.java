package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Lens;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;

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
	 * An entirely silly implementation of a string lens, which substitutes the
	 * word "Sparkles" for any value. Used solely for testing.
	 * @since zerobandwidth-net/android 0.1.5 (#41)
	 */
	protected static class CustomStringLens
	extends Lens<String>
	implements Refractor<String>
	{
		/** A silly default value. */
		public static final String DEFAULT_STRING = "Sparkles" ;

		@Override
		public String getSQLiteDataType()
		{ return SQLITE_TYPE_TEXT ; }

		@Override
		public String getSQLiteDefaultValue()
		{ return DEFAULT_STRING ; }

		@Override
		public String toSQLiteString( String o )
		{
			String s = ( o == null ? DEFAULT_STRING : o ) ;
			return String.format( "'%s'", s ) ;
		}

		@Override
		public Refractor<String> addToContentValues( ContentValues vals, String sKey, String val )
		{
			String sVal = ( val == null ? DEFAULT_STRING : val ) ;
			vals.put( sKey, sVal ) ;
			return this ;
		}

		@Override
		public String fromCursor( Cursor crs, String sKey )
		{ return crs.getString( crs.getColumnIndex( sKey ) ) ; }
	}

	/**
	 * Data table class for the database which tests custom refractors.
	 * @since zerobandwidth-net/android 0.1.5 (#41)
	 */
	@SQLiteTable( "sparkles" )
	protected static class Sparkle
	implements SQLightable
	{
		@SQLiteColumn( name = "sparkle", refractor = CustomStringLens.class )
		public String m_sValue = null ;

		public Sparkle() {}

		public Sparkle( String s )
		{ m_sValue = s ; }
	}

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
