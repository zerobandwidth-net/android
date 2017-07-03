package net.zerobandwidth.android.lib.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Extends {@link SQLiteOpenHelper} with a few additional features.
 *
 * <h3>Database Connection Management</h3>
 *
 * <p>The portal maintains its own persistent reference to the underlying SQLite
 * database. This connection is established with a background thread kicked off
 * by the {@link #openDB(boolean, ConnectionListener) openDB()} method. The
 * class provides variants of this method allowing the caller to specify whether
 * the connection should be opened read-only, and provide a
 * {@link ConnectionListener ConnectionListener} instance which can handle the
 * {@link ConnectionListener#onDatabaseConnected onDatabaseConnected()} callback
 * method.</p>
 *
 * <p>The portal's {@link #close()} method also overrides the parent's, so that
 * it can close the connection to the database before closing out the portal
 * itself.</p>
 *
 * <h3>Static Constants and Utility Methods</h3>
 *
 * <p>The class provides several static constants and methods that are generally
 * useful when dealing with SQLite databases. In particular:</p>
 *
 * <dl>
 *     <dt>{@link #COLUMN_NOT_FOUND}</dt>
 *     <dd>
 *         Compare this value to the return value of
 *         {@link Cursor#getColumnIndex} to determine whether a column exists.
 *     </dd>
 *     <dt>{@link #CURSOR_NOT_STARTED}</dt>
 *     <dd>
 *         Compare this value to the return value of
 *         {@link Cursor#getPosition} to determine whether the cursor has
 *         started traversing its contents.
 *     </dd>
 *     <dt>{@link #closeCursor(Cursor)}</dt>
 *     <dd>
 *         Safely closes a cursor, checking first whether the object is null or
 *         has already been closed previously.
 *     </dd>
 *     <dt>{@link #boolToInt(boolean)} and {@link #intToBool(int)}</dt>
 *     <dd>
 *         Converts between Boolean values and the integer values typically used
 *         to represent them in SQLite.
 *     </dd>
 *     <dt>{@link #now()}</dt>
 *     <dd>
 *         Returns a UTC epoch timestamp as a long integer. Implementation
 *         classes are encouraged to store dates as long integer timestamps
 *         for easier comparison and storage, and convert them to displayable
 *         dates and times only when they need to be displayed.
 *     </dd>
 * </dl>
 *
 * <p>Since 0.1.1 (#20), some of these items also form the basis of
 * {@link net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder QueryBuilder}
 * and its descendants.</p>
 *
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
	 * The string to be used in an SQLite statement to represent a null value.
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static final String SQLITE_NULL = "NULL" ;

	/**
	 * Integer representation of "true".
	 * @see #boolToInt(boolean)
	 * @see #intToBool(int)
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static final int SQLITE_TRUE_INT = 1 ;

	/**
	 * Stringified-integer representation of "true".
	 * @see #boolToIntString(boolean)
	 * @see #SQLITE_TRUE_INT
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static final String SQLITE_TRUE_INTSTRING = "1" ;

	/**
	 * Integer representation of "false".
	 * @see #boolToInt(boolean)
	 * @see #intToBool(int)
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static final int SQLITE_FALSE_INT = 0 ;

	/**
	 * Stringified-integer representation of "false".
	 * @see #boolToIntString(boolean)
	 * @see #SQLITE_FALSE_INT
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static final String SQLITE_FALSE_INTSTRING = "0" ;

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
    { return( b ? SQLITE_TRUE_INT : SQLITE_FALSE_INT ) ; }

	/**
	 * Transforms a Boolean value into the string representation of a
	 * corresponding integer, for use in SQLite statements.
	 * @param b the Boolean value to be converted
	 * @return {@code "1"} for true or {@code "0"} for false
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static String boolToIntString( boolean b )
	{ return Integer.toString( boolToInt(b) ) ; }

    /**
     * Simplistic transformation of an integer to a Boolean value, for retrieval
     * of a value from an SQLite database.
     * @param z the integer to be converted
     * @return {@code true} iff the integer is non-zero
     */
    public static boolean intToBool( int z )
    { return( z != SQLITE_FALSE_INT ) ; }

	/**
	 * Shorthand to fetch the Boolean value from a column that stores Boolean
	 * values as integers.
	 * @param crs the cursor that contains a row with one of these columns
	 * @param sColName the name of the column
	 * @return the Boolean value
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static boolean getBooleanColumn( Cursor crs, String sColName )
	{ return intToBool( crs.getInt( crs.getColumnIndex( sColName ) ) ) ; }

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
         * connection.
         */
        protected SQLitePortal m_dbh = SQLitePortal.this ;

	    /**
	     * A listener to handle the connection callback, if any.
	     * @since zerobandwidth-net/android 0.1.2 (#24)
	     */
	    protected ConnectionListener m_listener = null ;

	    /**
		 * The default constructor.
		 */
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
            try
			{
				m_dbh.m_db = ( m_dbh.m_bReadOnly ?
					m_dbh.getReadableDatabase() : m_dbh.getWritableDatabase() );
			}
            catch( Exception x )
            { Log.e( LOG_TAG, "Could not connect to database.", x ) ; }
            m_dbh.m_bIsConnected = ( m_dbh.m_db != null ) ;
	        if( m_dbh.m_bIsConnected && m_listener != null )
		        m_listener.onDatabaseConnected( m_dbh ) ;
        }
    }

/// Instance Members ///////////////////////////////////////////////////////////

	/**
	 * The context in which the portal is created.
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	protected Context m_ctx = null ;

	/** A persistent reference to the underlying database. */
	protected SQLiteDatabase m_db = null ;

	/**
	 * Remembers the version number with which the instance was constructed.
	 * This is kept private in {@link SQLiteOpenHelper}, so in order to use this
	 * in {@code SQLitePortal} and its descendants, we have to copy it here.
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	protected int m_nLatestVersion = -1 ;

	/** Indicates whether a connection to the database has been established. */
	protected boolean m_bIsConnected = false ;

	/**
	 * Indicates whether connections should be read-only.
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	protected boolean m_bReadOnly = false ;

/// Inherited Constructors (must duplicate here for descendants) ///////////////

    /** @see SQLiteOpenHelper#SQLiteOpenHelper(Context, String, SQLiteDatabase.CursorFactory, int)  */
    public SQLitePortal( Context ctx, String sDatabaseName,
                         SQLiteDatabase.CursorFactory cf, int nVersion )
    {
		super( ctx, sDatabaseName, cf, nVersion ) ;
		m_ctx = ctx ;
	    m_nLatestVersion = nVersion ;
    }

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
    { return this.openDB( false, null ) ; }

	/**
	 * Kicks off a {@link ConnectionTask} which will inform the specified
	 * {@link ConnectionListener} when the connection to the SQLite database is
	 * established.
	 * @param l the listener for the "on connected" callback
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.2 (#24)
	 */
	public synchronized SQLitePortal openDB( ConnectionListener l )
	{ return this.openDB( false, l ) ; }

	/**
	 * Kicks off a {@link ConnectionTask} which will optionally connect in
	 * read-only mode.
	 * @param bReadOnly specifies whether to open the database in read-only mode
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	public synchronized SQLitePortal openDB( boolean bReadOnly )
	{ return this.openDB( bReadOnly, null ) ; }

	/**
	 * Kicks off a {@link ConnectionTask} which will optionally connect in
	 * read-only mode, and will inform the specified {@link ConnectionListener}
	 * when the connection to the SQLite database is established
	 * @param bReadOnly specifies whether to open the database in read-only mode
	 * @param l the listener for the "on connected" callback
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	public synchronized SQLitePortal openDB( boolean bReadOnly, ConnectionListener l )
	{
		m_bReadOnly = bReadOnly ;
		(new ConnectionTask(l)).runInBackground() ;
		return this ;
	}

	/**
	 * Accessor for the underlying {@link SQLiteDatabase}. This should never be
	 * used in practice, as it is always safer to invoke methods of the
	 * {@code SQLitePortal} descendant to perform database functions. This
	 * method should be used only in certain unit-testing scenarios in which
	 * properties of the database itself must be directly examined.
	 * @return the underlying database instance
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
//	public synchronized SQLiteDatabase getDB()
//	{ return m_db ; }

    /**
     * Closes the database connection and releases all references to it.
	 *
	 * <p><b>Note:</b> Since 0.1.4 (#34), there is no need to invoke this method
	 * before {@link #close()}; this class now overrides the parent's
	 * {@code close()} method to call {@code closeDB()} first.</p>
	 *
     * @return (fluid)
     */
    public synchronized SQLitePortal closeDB()
    {
        if( m_db != null ) m_db.close() ;
        m_db = null ;
        m_bIsConnected = false ;
        return this ;
    }

	/**
	 * Closes the database connection and releases all references to it, before
	 * closing the portal itself.
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	@Override
	public void close()
	{ this.closeDB() ; super.close() ; }

/// Other Database Utility Methods /////////////////////////////////////////////

	/**
	 * Discovers the full path to the database file for this portal in the app's
	 * data folder on the Android device.
	 * @return the full path and name to the database on the device
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	protected String getPathToDatabaseFile()
	{
		return (new StringBuilder())
			.append( m_ctx.getApplicationInfo().dataDir )
			.append( File.separatorChar )
			.append( "databases" )
			.append( File.separatorChar )
			.append( this.getDatabaseName() )
			.toString()
			;
	}

	/**
	 * Checks whether the portal's database exists in the app's data folder on
	 * the Android device.
	 * @return {@code true} if the database has already been created
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	protected boolean databaseExists()
	{
		final String sPath = this.getPathToDatabaseFile() ;
		boolean bExists = false ;
		try { bExists = (new File(sPath)).exists() ; }
		catch( SecurityException x )
		{
			Log.w( LOG_TAG, (new StringBuilder())
					.append( "Denied read access when checking for file [" )
					.append( sPath ).append( "]." )
					.toString()
				);
		}
		return bExists ;
	}

	/**
	 * Uses SQLite pragmas to discover the structure of an existing table, and
	 * return a list of its column definitions.
	 * @param sTableName the name of the table to be described
	 * @return a list of column information structures
	 * @see SQLiteColumnInfo#gatherColumnList
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public List<SQLiteColumnInfo> getColumnListForTable( String sTableName )
	{ return SQLiteColumnInfo.gatherColumnList( m_db, sTableName ) ; }

	/**
	 * Uses SQLite pragmas to discover the structure of an existing table, and
	 * return a map of column names to column definitions.
	 * @param sTableName the name of the table to be described
	 * @return a map of column names to their definitions
	 * @see SQLiteColumnInfo#gatherColumnMap
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public Map<String,SQLiteColumnInfo> getColumnMapForTable( String sTableName )
	{ return SQLiteColumnInfo.gatherColumnMap( m_db, sTableName ) ; }

	/**
	 * Discovers the size of the database file in storage.
	 * @return the size of the file, or -1 if an exception is thrown
	 * @since zerobandwidth-net/android 0.1.4 (#34)
	 */
	protected long getDatabaseFileSize()
	{
		final String sPath = this.getPathToDatabaseFile() ;
		try { return (new File(sPath)).length() ; }
		catch( SecurityException x )
		{
			Log.w( LOG_TAG, (new StringBuilder())
					.append( "Access denied for file [" )
					.append( sPath ).append( "]; returning size -1." )
					.toString()
				);
			return -1 ;
		}
	}

	/**
	 * Accessor for the schema version with which the instance was constructed.
	 * @return the "latest" schema version number
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public int getLatestSchemaVersion()
	{ return m_nLatestVersion ; }
}
