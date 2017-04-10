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
		final Context ctx = InstrumentationRegistry.getContext() ;
		SQLiteAssetPortal dbh = new SQLiteAssetTestDB( ctx, 1 ) ;
		Cursor crs = null ;
		try
		{
			Log.i( LOG_TAG, "Initial open with version 1..." ) ;
			dbh.openDB(true) ;
			Thread.sleep(2000) ;
			assertTrue( dbh.databaseExists() ) ;
			crs = QueryBuilder.selectFrom( "foo" ).executeOn( dbh.m_db ) ;
			assertEquals( 2, crs.getCount() ) ;

			SQLitePortal.closeCursor(crs) ;
			dbh.close() ;

			Log.i( LOG_TAG, "Subsequent open with version 2..." ) ;
			dbh = new SQLiteAssetTestDB( ctx, 2 ) ;
			dbh.openDB(true) ; // Should upgrade the DB with asset version 2.
			Thread.sleep(2000) ;
			assertTrue( dbh.databaseExists() ) ;
			crs = QueryBuilder.selectFrom( "foo" ).executeOn( dbh.m_db ) ;
			assertEquals( 3, crs.getCount() ) ;
		}
		finally
		{
			SQLitePortal.closeCursor(crs) ;
			dbh.close() ;
			ctx.deleteDatabase( dbh.getDatabaseName() ) ;
		}
	}
}
