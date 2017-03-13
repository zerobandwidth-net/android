package net.zerobandwidth.android.lib.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

/**
 * Extends {@link SQLiteOpenHelper} by allowing an instance to maintain its own
 * persistent interface to its underlying database. Also statically provides
 * several useful database utility functions and semantic constants, which also
 * form the basis of
 * {@link net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder QueryBuilder}.
 * @since zerobandwidth-net/android 0.0.2 (#8)
 */
@SuppressWarnings("unused")                                // This is a library.
public abstract class SQLitePortal
extends SQLiteOpenHelper
{
/// Statics ////////////////////////////////////////////////////////////////////

    public static final String LOG_TAG = SQLitePortal.class.getSimpleName() ;

	/**
	 * Magic value returned by {@link Cursor#getColumnIndex} when a column
	 * doesn't exist. (A column index of {@code -1} indicates an invalid state.)
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
    public static final int COLUMN_NOT_FOUND = -1 ;

	/**
	 * Magic value returned by {@link Cursor#getPosition} when the cursor has
	 * not yet started traversing the result set. (A cursor position of
	 * {@code -1} indicates an uninitialized state.)
	 *
	 * Note, however, that the test
	 * {@code crs.getPosition() == SQLitePortal.CURSOR_NOT_STARTED} is logically
	 * equivalent to the result of the existing method
	 * {@link Cursor#isBeforeFirst()}.
	 *
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final int CURSOR_NOT_STARTED = -1 ;

	/**
	 * Magic value to be passed to {@link SQLiteDatabase#delete} when we want
	 * the method to return a count of the number of rows deleted. (A literal
	 * value of {@code 1} always matches as {@code true} in a {@code WHERE}
	 * clause.) The Android documentation implies that passing {@code null} as
	 * the {@code WHERE} clause will not return a count.
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final String DELETE_ALL = "1" ;

	/**
	 * Magic value returned by {@link SQLiteDatabase#insert} and related methods
	 * when a row insertion fails. (A value of {@code -1} as the row ID
	 * indicates an error state.)
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final long INSERT_FAILED = -1 ;

	/**
	 * Magic value returned by {@link SQLiteDatabase#replace} and related
	 * methods when a row replacement fails. (A value of {@code -1} as the row
	 * ID indicates an error state.)
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final long REPLACE_FAILED = -1 ;

	/**
	 * Magic value to be passed to {@link SQLiteDatabase#query} and related
	 * methods when we want to select all rows.
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final String SELECT_ALL = null ;

	/**
	 * Magic value to be passed to {@link SQLiteDatabase#update} and related
	 * methods when we want to indiscriminately update all rows, and get a count
	 * of the number of rows that were updated. (A literal value of {@code 1}
	 * always matches as {@code true} in a {@code WHERE} clause.)
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final String UPDATE_ALL = "1" ;

	/**
	 * If using integer columns to store Boolean values, where {@code 1} is true
	 * and {@code 0} is false, use this constant when supplying {@code WHERE}
	 * value substitutions for "true".
	 * @see #boolToInt(boolean)
	 * @see #intToBool(int)
	 * @since zerobandwidth-net/android 0.1.1 (#20)
	 */
	public static final String WHERE_TRUE = "1" ;

	/**
	 * If using integer columns to store Boolean values, where {@code 1} is true
	 * and {@code 0} is false, use this constant when supplying {@code WHERE}
	 * value substitutions for "false".
	 * @see #boolToInt(boolean)
	 * @see #intToBool(int)
	 * @since zerobandwidth-net/android 0.1.1 (#20)
	 */
	public static final String WHERE_FALSE = "0" ;

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

	/**
	 * Returns the number of milliseconds since epoch UTC. Use this value when
	 * comparing to timestamps stored in the database as {@code long} integers.
	 * @return milliseconds since epoch UTC
	 * @since zerobandwidth-net/android 0.1.1 (#20)
	 */
	public static long now()
	{ return (new Date()).getTime() ; }

/// Inner Classes //////////////////////////////////////////////////////////////

	/**
	 * Classes interested in immediately reacting to a database's connection
	 * status may implement this interface to catch the event. Usually,
	 * connection times are fast enough that this is not necessary, and the
	 * database under the {@link SQLitePortal} may be used immediately.
	 * @since zerobandwidth-net/android 0.1.2 (#24)
	 */
	public interface ConnectionListener
	{
		/**
		 * Handles callback event from {@link ConnectionTask} when the
		 * connection is established.
		 * @param dbh the {@link SQLitePortal} instance that was connected to
		 *            the database
		 */
		void onDatabaseConnected( SQLitePortal dbh ) ;
	}

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
	     * A listener to handle the connection callback, if any.
	     * @since zerobandwidth-net/android 0.1.2 (#24)
	     */
	    protected ConnectionListener m_listener = null ;

	    /** The default constructor. */
	    protected ConnectionTask()
	    { m_listener = null ; }

	    /**
	     * A constructor which specifies a listener to receive the "on
	     * connected" callback.
	     * @param l a listener
	     * @since zerobandwidth-net/android 0.1.2 (#24)
	     */
	    protected ConnectionTask( ConnectionListener l )
	    { m_listener = l ; }

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
	        if( m_dbh.m_bIsConnected && m_listener != null )
		        m_listener.onDatabaseConnected( m_dbh ) ;
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
	 * Kicks off a {@link ConnectionTask} which will inform the specified
	 * {@link ConnectionListener} when the connection to the SQLite database is
	 * established.
	 * @param l the listener for the "on connected" callback
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.2 (#24)
	 */
	public synchronized SQLitePortal openDB( ConnectionListener l )
	{ (new ConnectionTask(l)).runInBackground() ; return this ; }

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
