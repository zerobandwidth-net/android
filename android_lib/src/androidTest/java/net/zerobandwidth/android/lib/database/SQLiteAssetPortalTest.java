package net.zerobandwidth.android.lib.database ;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4 ;
import android.util.Log;

import net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder;

import org.junit.Test ;
import org.junit.runner.RunWith ;

import static junit.framework.Assert.* ;

/**
 * Exercises {@link SQLiteAssetPortal}.
 * @since zerobandwidth-net/android 0.1.4 (#34)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteAssetPortalTest
{
	public static final String LOG_TAG =
			SQLiteAssetPortalTest.class.getSimpleName() ;

	/**
	 * Time to wait for a database connection to open, in milliseconds.
	 */
	public static final int OPEN_DB_WAIT_MS = 2000 ;

	/** The execution context. */
	protected Context m_ctx ;

	/**
	 * Verifies that the logic that manages the asset-to-database conversions
	 * will operate properly.
	 *
	 * <p>The test codebase contains two database assets, named
	 * {@code db_asset_test.v1.db} and {@code db_asset_test.v2.db}. Each has a
	 * single table {@code foo} with columns {@code _id} and {@code stuff}. In
	 * version 1, the table contains two rows; in version 2, it contains three
	 * rows. This test method attempts to create version 1, verify that it has
	 * two rows in its table, then immediately create version 2, and verify that
	 * it has three rows in its table.
	 */
	@Test
	public void testDatabaseManagement()
	throws InterruptedException // from Thread.sleep()
	{
		m_ctx = InstrumentationRegistry.getContext() ;
		Log.d( LOG_TAG, "Calling test DB constructor..." ) ;
		SQLiteAssetPortal dbh = new SQLiteAssetTestDB( m_ctx, 1 ) ;
		Cursor crs = null ;
		try
		{
			Log.i( LOG_TAG, "Phase 1: Open version 1..." ) ;
			this.verifyDBContents( dbh, 2 ) ;

			Log.i( LOG_TAG, "Phase 2: Upgrade to version 2..." ) ;
			dbh = new SQLiteAssetTestDB( m_ctx, 2 ) ;
			this.verifyDBContents( dbh, 3 ) ; // Opening DB will trigger upgrade

			Log.i( LOG_TAG, "Phase 3: Open version 2 again with new object..." ) ;
			dbh = new SQLiteAssetTestDB( m_ctx, 2 ) ;
			this.verifyDBContents( dbh, 3 ) ;

			Log.i( LOG_TAG, "Phase 4: Open version 2 with same object..." ) ;
			this.verifyDBContents( dbh, 3 ) ;
		}
		finally
		{
			Log.i( LOG_TAG, "Deleting database..." ) ;
			m_ctx.deleteDatabase( dbh.getDatabaseName() ) ;
		}
	}

	/**
	 * Repetitive code to verify that the test DB exists and contains the
	 * expected number of rows.
	 * @param dbh the test database portal
	 * @param nExpected the number of rows expected
	 * @throws InterruptedException if the thread sleeper fails
	 */
	private void verifyDBContents( SQLiteAssetPortal dbh, int nExpected )
	throws InterruptedException
	{
		Cursor crs = null ;
		try
		{
			dbh.openDB() ;
			Thread.sleep(OPEN_DB_WAIT_MS) ;
			assertTrue( dbh.databaseExists() ) ;
			crs = QueryBuilder.selectFrom( "foo" ).executeOn( dbh.m_db ) ;
			assertEquals( nExpected, crs.getCount() ) ;
		}
		finally
		{
			SQLitePortal.closeCursor(crs) ;
			dbh.close() ;
		}
	}
}
