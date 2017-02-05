package net.zerobandwidth.android.lib.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Extends {@link SQLiteOpenHelper} by allowing an instance to maintain its own
 * persistent interface to its underlying database. Also statically provides
 * several useful database utility functions.
 * @since zerobandwidth-net/android 0.0.2 (#8)
 */
@SuppressWarnings("unused")                                // This is a library.
public abstract class SQLitePortal
extends SQLiteOpenHelper
{
/// Statics ////////////////////////////////////////////////////////////////////

    public static final String LOG_TAG = SQLitePortal.class.getSimpleName() ;

    /**
     * Safely closes a database cursor. If the reference is {@code null}, or the
     * cursor is already closed, then the method returns trivially.
     * @param crs the cursor to be closed
     */
    public static void closeCursor( Cursor crs )
    {
        if( crs != null && ! crs.isClosed() )
            crs.close() ;
    }

    /**
     * Simplistic transformation of a Boolean value to an integer, for storage
     * in an SQLite database.
     * @param b the Boolean value to be converted
     * @return {@code 1} for true or {@code 0} for false
     */
    public static int boolToInt( boolean b )
    { return( b ? 1 : 0 ) ; }

    /**
     * Simplistic transformation of an integer to a Boolean value, for retrieval
     * of a value from an SQLite database.
     * @param z the integer to be converted
     * @return {@code true} iff the integer is non-zero
     */
    public static boolean intToBool( int z )
    { return( z != 0 ) ; }

/// Inner Classes //////////////////////////////////////////////////////////////

    /**
     * Allows the {@link SQLitePortal} to create a persistent connection to its
     * underlying database on a background thread.
     * @since zerobandwidth-net/android 0.0.2 (#8)
     */
    protected class ConnectionTask
    implements Runnable
    {
        /**
         * A reference back to the {@link SQLitePortal} that needs the
         * conenction.
         */
        protected SQLitePortal m_dbh = SQLitePortal.this ;

        /**
         * Executes the task in the background. {@link SQLitePortal}
         * implementations should <i>always</i> use this method instead of
         * {@link #run}.
         */
        public void runInBackground()
        { (new Thread(this)).start() ; }

        @Override
        public void run()
        {
            m_dbh.m_db = null ;
            m_dbh.m_bIsConnected = false ;
            try { m_dbh.m_db = m_dbh.getWritableDatabase() ; }
            catch( Exception x )
            { Log.e( LOG_TAG, "Could not connect to database.", x ) ; }
            m_dbh.m_bIsConnected = ( m_dbh.m_db != null ) ;
        }
    }

/// Inherited Constructors (must duplicate here for descendants) ///////////////

    /** @see SQLiteOpenHelper#SQLiteOpenHelper(Context, String, SQLiteDatabase.CursorFactory, int)  */
    public SQLitePortal( Context ctx, String sDatabaseName,
                         SQLiteDatabase.CursorFactory cf, int nVersion )
    { super( ctx, sDatabaseName, cf, nVersion ) ; }

/// Instance Members ///////////////////////////////////////////////////////////

    /** A persistent reference to the underlying database. */
    protected SQLiteDatabase m_db = null ;

    /** Indicates whether a connection to the database has been established. */
    protected boolean m_bIsConnected = false ;

/// Database Connection Management Methods /////////////////////////////////////

    /**
     * Indicates whether the portal has established a connection.
     * @return {@code true} if the instance thinks that it is connected.
     */
    public boolean isConnected()
    { return m_bIsConnected ; }

    /**
     * Kicks off a {@link ConnectionTask} to establish the connection to the
     * SQLite database.
     * @return (fluid)
     */
    public synchronized SQLitePortal openDB()
    { (new ConnectionTask()).runInBackground() ; return this ; }

    /**
     * Closes the database connection and releases all references to it.
     * @return (fluid)
     */
    public synchronized SQLitePortal closeDB()
    {
        if( m_db != null ) m_db.close() ;
        m_db = null ;
        m_bIsConnected = false ;
        return this ;
    }
}
