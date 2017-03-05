package net.zerobandwidth.android.lib.database;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static junit.framework.Assert.* ;

/**
 * Exercises {@link SQLitePortal}.
 * @since zerobandwidth-net/android 0.1.2 (#24)
 */
@RunWith( AndroidJUnit4.class )
public class SQLitePortalTest
{
	/**
	 * Set to {@code true} by a {@link SQLitePortal.ConnectionListener} callback
	 * in {@link #testConnectionListener()}.
	 */
	private boolean m_bCalledBack = false ;

	/**
	 * Exercises {@link SQLitePortal#openDB()},
	 * {@link SQLitePortal#isConnected()}, and {@link SQLitePortal#closeDB()}.
	 */
	@Test
	public void testConnection()
	{
		SQLitePortal dbh = MinimalUnitTestDBPortal.getInstrumentedInstance() ;
		try
		{
			dbh.openDB() ;
			//noinspection StatementWithEmptyBody
			while( ! dbh.isConnected() ) ; // Wait for a connection.
			assertTrue( dbh.isConnected() ) ;
			dbh.closeDB() ;
			assertFalse( dbh.isConnected() ) ;
		}
		finally { dbh.close() ; }
	}

	/**
	 * Exercises {@link SQLitePortal#openDB(SQLitePortal.ConnectionListener)}
	 * and the {@link SQLitePortal.ConnectionListener} inner class.
	 */
	@Test
	public void testConnectionListener()
	{
		m_bCalledBack = false ;
		final SQLitePortal.ConnectionListener l =
			new SQLitePortal.ConnectionListener()
			{
				@Override
				public void onDatabaseConnected( SQLitePortal dbh )
				{ SQLitePortalTest.this.m_bCalledBack = true ; }
			};
		final int SLEEP_MS = 1000 ; // Connection timeout delay. Tune to taste.
		SQLitePortal dbh = MinimalUnitTestDBPortal.getInstrumentedInstance() ;
		try
		{
			dbh.openDB(l) ;
			Thread.sleep( SLEEP_MS ) ;
			assertTrue( dbh.isConnected() ) ;
			assertTrue( m_bCalledBack ) ;
		}
		catch( InterruptedException xInterrupt )
		{ fail( "Thread was interrupted." ) ; }
		finally { dbh.closeDB().close() ; }
	}

	/**
	 * Exercises {@link SQLitePortal#boolToInt(boolean)} and
	 * {@link SQLitePortal#intToBool(int)}.
	 */
	@Test
	public void testIntBooleanTransforms()
	{
		final int ITERATIONS = 8 ; // Tune to taste.
		final int CEILING = 10 ; // Random integer ceiling. Tune to taste.
		final Random RNG = new Random() ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			final int nValue = RNG.nextInt(CEILING) - ( CEILING / 2 ) ;
			final boolean bValue = SQLitePortal.intToBool( nValue ) ;
			if( nValue == 0 ) assertFalse( bValue ) ;
			else assertTrue( bValue ) ;
			final int nBool = SQLitePortal.boolToInt( bValue ) ;
			assertEquals( ( bValue ? 1 : 0 ), nBool ) ;
		}
	}
}
