package net.zer0bandwidth.android.lib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper ;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides a {@link SQLiteOpenHelper} implementation which manages a read-only
 * database that is loaded from an asset file.
 *
 * <p>See <a href="https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/">
 * Using Your Own SQLite Database in Android Applications</a> for the basis of
 * this implementation.</p>
 *
 * <h4>Example</h4>
 *
 * <pre>
 * public class MyDB extends SQLiteAssetPortal
 * {
 *     protected static final String DB_NAME = "mydb" ;
 *
 *     /**
 *      * Rather than using this to represent the version of the database
 *      * schema, use this constant to represent the version of the asset. Thus,
 *      * whenever new content is added to the asset, the app will know to
 *      * overwrite its database with the contents of the new asset. See the
 *      * example of onUpgrade() below.
 *     {@literal *}/
 *     protected static final int DB_VERSION = 2 ;
 *
 *     /** Filename of the asset containing the static database instance. {@literal *}/
 *     protected static final String DB_SOURCE_ASSET = "mydb.v2.db" ;
 *
 *     public MyDB( Context ctx )
 *     { super( ctx, DB_NAME, null, DB_VERSION ) ; }
 *
 *    {@literal @}Override
 *     public abstract String getAssetName()
 *     { return DB_SOURCE_ASSET ; }
 * }
 * </pre>
 *
 * @since zer0bandwidth-net/android 0.1.4 (#34)
 */
public abstract class SQLiteAssetPortal
extends SQLitePortal
{
	public static final String LOG_TAG =
			SQLiteAssetPortal.class.getSimpleName() ;

/// Inner Classes //////////////////////////////////////////////////////////////

	/**
	 * Allows the {@link SQLiteAssetPortal} to create a persistent connection to
	 * its underlying database on a background thread.
	 * @since zer0bandwidth-net/android 0.1.4 (#34)
	 */
	protected class ConnectionTask
	extends SQLitePortal.ConnectionTask
	implements Runnable
	{
		/**
		 * A reference back to the {@link SQLiteAssetPortal} that needs the
		 * connection.
		 */
		protected SQLiteAssetPortal m_dbh = SQLiteAssetPortal.this ;

		protected ConnectionTask( ConnectionListener l )
		{ m_listener = l ; }

		@Override
		public void run()
		{
			m_dbh.m_db = null ;
			m_dbh.m_bIsConnected = false ;
			try { m_dbh.m_db = m_dbh.getReadableDatabase() ; }
			catch( Exception x )
			{ Log.e( LOG_TAG, "Could not establish initial connection." ) ; }
			if( m_dbh.m_bNeedsCopy ) // set by onCreate() / onUpgrade()
			{ // Close the database connection, copy from asset, and reopen DB.
				m_dbh.close() ;
				m_dbh.copyFromAsset() ;
				try
				{
					m_dbh.m_db = m_dbh.getReadableDatabase() ;
					// Explicitly override the copy flag after this second call,
					// to avoid spurious re-copying of the DB. This must be done
					// because onCreate() can still be called by Android as part
					// of the SQLite DB connection life cycle, and our override
					// would blindly set the copy flag to true.
					m_dbh.m_bNeedsCopy = false ;
				}
				catch( Exception x )
				{
					Log.e( LOG_TAG,
							"Could not connect after copying from asset." ) ;
				}
			}
			m_dbh.m_bIsConnected = ( m_dbh.m_db != null ) ;
			if( m_dbh.m_bIsConnected && m_listener != null )
				m_listener.onDatabaseConnected(m_dbh) ;
		}
	}

/// Instance Members ///////////////////////////////////////////////////////////

	/**
	 * The only method in which the old and new schema versions are exposed is
	 * in {@link #onUpgrade}; thus, that method will set this indicator flag
	 * so that the consumer can then explicitly invoke {@link #copyFromAsset}.
	 */
	protected boolean m_bNeedsCopy = false ;

/// Inherited Constructors (must duplicate here for descendants) ///////////////

	public SQLiteAssetPortal( Context ctx, String sDatabaseName,
							  SQLiteDatabase.CursorFactory cf, int nVersion )
	{ super( ctx, sDatabaseName, cf, nVersion ) ; }

/// android.database.sqlite.SQLiteOpenHelper ///////////////////////////////////

	/**
	 * If the database did not previously exist, then we need to copy it from
	 * the asset.
	 *
	 * <p>Note that, since {@link ConnectionTask} executes
	 * {@link SQLiteOpenHelper#getReadableDatabase()} twice, it is possible that
	 * this method will be called spuriously during the second invocation.
	 * However, since the task immediately resets {@link #m_bNeedsCopy} to
	 * {@code false} after this, this has no negative consequence, other than
	 * the time wasted on the method call.</p>
	 */
	@Override
	public final void onCreate( SQLiteDatabase db )
	{
		Log.d( LOG_TAG, "onCreate() called; asset may be copied." ) ;
		m_bNeedsCopy = true ;
	}

	/**
	 * Copy the database asset only if an upgrade is needed.
	 */
	@Override
	public final void onUpgrade( SQLiteDatabase db, int nOld, int nNew )
	{
		m_bNeedsCopy = ( nOld < nNew ) ;
		if( m_bNeedsCopy )
		{
			Log.d( LOG_TAG, (new StringBuilder())
					.append( "onUpdate(): Asset should be copied; old version [" )
					.append( nOld ).append( "] is less than new version [" )
					.append( nNew ).append( "]." )
					.toString()
				);
		}
	}

/// net.zer0bandwidth.android.lib.database.SQLitePortal ////////////////////////

	/**
	 * Forces the connection to be read-only, and uses the descendant version of
	 * {@link ConnectionTask} to open, and then copy, the database.
	 * @param bReadOnly placebo - always overridden as {@code true} in this
	 *  version of the method
	 * @param l the listener for the "on connected" callback
	 * @return (fluid)
	 */
	@Override
	public synchronized SQLiteAssetPortal openDB( boolean bReadOnly, ConnectionListener l )
	{
		m_bReadOnly = true ;     // Asset-copied databases are always read-only.
		(new SQLiteAssetPortal.ConnectionTask(l)).runInBackground() ;
		return this ;
	}

/// Database from Assets ///////////////////////////////////////////////////////

	/**
	 * Descendant classes must implement this method to provide the name of the
	 * asset from which the database will be replicated.
	 * @return the name of the asset which will be copied as the app's database
	 */
	public abstract String getAssetName() ;

	/**
	 * If indicated by the flag set during connection, this method overwrites
	 * the portal's database with the contents of a static asset packaged in the
	 * APK.
	 * @return {@code true} if the asset was successfully copied; {@code false}
	 *  otherwise
	 */
	protected boolean copyFromAsset()
	{
		final String sAssetFileName = this.getAssetName() ;
		boolean bSuccess = true ;
		InputStream in = null ;
		OutputStream out = null ;
		String sDatabaseName = this.getDatabaseName() ;
		try
		{
			String sDatabaseFile = this.getPathToDatabaseFile() ;
			if( this.databaseExists() )
			{
				File fOld = new File( sDatabaseFile ) ;
				if( fOld.delete() )
					Log.i( LOG_TAG, "Deleted previous database file!" ) ;
			}
			in = m_ctx.getAssets().open( sAssetFileName ) ;
			out = new FileOutputStream( sDatabaseFile ) ;
			byte[] ayBuffer = new byte[1024] ;
			int nLength ;
			Log.d( LOG_TAG, (new StringBuilder())
					.append( "Copying database from asset [" )
					.append( sAssetFileName )
					.append( "] to database [" )
					.append( sDatabaseName )
					.append( "]..." )
					.toString()
				);
			while( ( nLength = in.read(ayBuffer) ) > 0 )
				out.write( ayBuffer, 0, nLength ) ;
		}
		catch( IOException iox )
		{
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Could not copy asset [" )
					.append( sAssetFileName )
					.append( "] to database [" )
					.append( sDatabaseName )
					.append( "]:" )
					.toString()
				, iox );
			bSuccess = false ;
		}
		finally
		{ // Ensure the input/output streams are closed.
			try { if( in != null ) in.close() ; }
			catch( IOException ioxCloseInput )
			{
				Log.e( LOG_TAG, "Could not close input stream!",
						ioxCloseInput ) ;
			}
			if( out != null )
			{
				try { out.flush() ;  out.close() ; }
				catch( IOException ioxCloseOutput )
				{
					Log.e( LOG_TAG,
							"Could not close output stream!",
							ioxCloseOutput
						);
				}
			}
		}
		return bSuccess ;
	}
}
