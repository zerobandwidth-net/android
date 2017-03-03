package net.zerobandwidth.android.lib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

/**
 * Provides a minimal implementation of a {@link SQLitePortal}, for use in unit
 * testing of other database components in the library.
 * @see <a href="http://thedeveloperworldisyours.com/android/android-sqlite-test/#sthash.eHLSypcw.dpbs">The Developer World is Yours: Android SQLite Test</a>
 * @see <a href="http://stackoverflow.com/a/39696959">StackOverflow answer #39696959</a>
 * @since zerobandwidth-net/android 0.1.1 (#20)
 */
public class MinimalUnitTestDBPortal
extends SQLitePortal
{
	public static final String TEST_DATABASE_NAME =
			MinimalUnitTestDBPortal.class.getSimpleName() ;

	public static final String TEST_TABLE_NAME = "unittestdata" ;

	public static final String TEST_TABLE_SQL =
			  "CREATE TABLE " + TEST_TABLE_NAME + " ("
			+ " id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ " a_string_field TEXT,"
			+ " a_int_field INTEGER,"
			+ " a_boolint_field INTEGER"
			+ " );"
			;

	/**
	 * Constructs an instance using the context provided by
	 * {@link InstrumentationRegistry#getTargetContext()}.
	 * @return an instance of the minimal database portal
	 * @see #MinimalUnitTestDBPortal(Context)
	 */
	public static MinimalUnitTestDBPortal getInstrumentedInstance()
	{
		return new MinimalUnitTestDBPortal(
				InstrumentationRegistry.getTargetContext() ) ;
	}

	/**
	 * Constructs an instance using the context provided by
	 * {@link InstrumentationRegistry#getTargetContext()}.
	 * @param sName the name of the database
	 * @return an instance of the minimal database portal
	 * @see #MinimalUnitTestDBPortal(Context, String)
	 */
	public static MinimalUnitTestDBPortal getInstrumentedInstance( String sName )
	{
		return new MinimalUnitTestDBPortal(
				InstrumentationRegistry.getTargetContext(), sName ) ;
	}

	/**
	 * Basic constructor for a test database.
	 * @param ctx the context in which the database is created
	 * @see #TEST_DATABASE_NAME
	 */
	public MinimalUnitTestDBPortal( Context ctx )
	{ super( ctx, TEST_DATABASE_NAME, null, 1 ) ; }

	/**
	 * Constructor allowing a custom database name.
	 * @param ctx the context in which the database is created
	 * @param sName the name of the database
	 */
	public MinimalUnitTestDBPortal( Context ctx, String sName )
	{ super( ctx, sName, null, 1 ) ; }

	@Override
	public void onCreate( SQLiteDatabase db )
	{ db.execSQL( TEST_TABLE_SQL ) ; }

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
	{ /* Meh. */ }

	/**
	 * Allows the unit test class to directly access the database.
	 * @return the database connection under the portal
	 */
	public SQLiteDatabase getDB()
	{ return m_db ; }
}
