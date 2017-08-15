package net.zerobandwidth.android.lib.database;

/**
 * Provides string constants for various SQLite syntactic elements, magic
 * values, etc., to any class that needs them. The constants in this class may
 * be individually obtained via static imports, or the entire class may be
 * imported to make all constants available to the consuming class.
 *
 * <h3>Magic Values for Android Database Methods</h3>
 *
 * <dl>
 * <dt>{@link #COLUMN_NOT_FOUND}</dt>
 *     <dd>
 *         Compare this value to the return value of
 *         {@link android.database.Cursor#getColumnIndex} to determine whether a
 *         column exists.
 *     </dd>

 * </dl>
 *
 * <h3>SQLite Syntactic Tokens</h3>
 *
 * <h3>SQLite Type Tokens</h3>
 *
 * <h2>Items NOT Provided by This Class</h2>
 *
 * <p>The static constants and methods that support marshalling of Boolean
 * values as integers in SQLite are not defined here; they are defined in
 * {@link SQLitePortal} to keep the constants and methods in one place.</p>
 *
 * @since zerobandwidth-net/android 0.1.7 (#48)
 */
public final class SQLiteSyntax
{
/// Magic values for Android SQLite functions //////////////////////////////////

	/**
	 * Magic value returned by {@link android.database.Cursor#getColumnIndex}
	 * when a column doesn't exist. (A column index of {@code -1} indicates an
	 * invalid state.)
	 */
	public static final int COLUMN_NOT_FOUND = -1 ;

	/**
	 * Magic value to be passed to
	 * {@link android.database.sqlite.SQLiteDatabase#delete} when we want the
	 * method to return a count of the number of rows deleted. (A literal value
	 * of {@code 1} always matches as {@code true} in a {@code WHERE} clause.)
	 * The Android documentation implies that passing {@code null} as the
	 * {@code WHERE} clause will not return a count.
	 */
	public static final String DELETE_ALL = "1" ;

	/**
	 * Similar to the standard magic number returned when an insertion query
	 * fails (see {@link #INSERT_FAILED}, this return value from the execution
	 * methods of
	 * {@link net.zerobandwidth.android.lib.database.querybuilder.DeletionBuilder}
	 * indicates that the delete operation could not be carried out because of
	 * an exception.
	 */
	public static final int DELETE_FAILED = -1 ;

	/**
	 * Magic value returned by
	 * {@link android.database.sqlite.SQLiteDatabase#insert} and related methods
	 * when a row insertion fails. (A value of {@code -1} as the row ID
	 * indicates an error state.)
	 */
	public static final long INSERT_FAILED = -1 ;

	/**
	 * Magic value returned by
	 * {@link android.database.sqlite.SQLiteDatabase#replace} and related
	 * methods when a row replacement fails. (A value of {@code -1} as the row
	 * ID indicates an error state.)
	 */
	public static final long REPLACE_FAILED = -1 ;

	/**
	 * Magic value to be passed to
	 * {@link android.database.sqlite.SQLiteDatabase#query} and related
	 * methods when we want to select all rows, <i>not</i> the actual SQL syntax
	 * to specify "all columns" in a {@code SELECT} statement.
	 * @see #SQL_SELECT_ALL_COLUMNS
	 */
	public static final String SELECT_ALL = null ;

	/**
	 * Magic value to be passed to
	 * {@link android.database.sqlite.SQLiteDatabase#update} and related
	 * methods when we want to indiscriminately update all rows, and get a count
	 * of the number of rows that were updated. (A literal value of {@code 1}
	 * always matches as {@code true} in a {@code WHERE} clause.)
	 * @since zerobandwidth-net/android 0.1.1 (#23)
	 */
	public static final String UPDATE_ALL = "1" ;

	/**
	 * Similar to the standard magic number returned when an insertion query
	 * fails (see {@link #INSERT_FAILED}, this return value from the execution
	 * methods of
	 * {@link net.zerobandwidth.android.lib.database.querybuilder.UpdateBuilder}
	 * indicates that the delete operation could not be carried out because of
	 * an exception.
	 */
	public static final int UPDATE_FAILED = -1 ;

/// Static Constants: SQLite Syntax ////////////////////////////////////////////

	/** Begins a SQL {@code DELETE FROM} statement. */
	public static final String SQL_DELETE_FROM = "DELETE FROM " ;

	/** Begins a {@code FROM} clause in a SQL {@code SELECT} statement. */
	public static final String SQL_FROM = " FROM " ;

	/** Begins a {@code GROUP BY} clause in a SQL {@code SELECT} statement. */
	public static final String SQL_GROUP_BY = " GROUP BY " ;

	/** Begins a {@code HAVING} clause in a SQL {@code SELECT} statement. */
	public static final String SQL_HAVING = " HAVING " ;

	/** Begins a SQL {@code INSERT} statement. */
	public static final String SQL_INSERT_INTO = "INSERT INTO " ;

	/**
	 * Begins a {@code LIMIT} clause in a SQL {@code SELECT} statement.
	 * @see net.zerobandwidth.android.lib.database.querybuilder.SelectionBuilder#NO_LIMIT
	 */
	public static final String SQL_LIMIT = " LIMIT " ;

	/**
	 * The string to be used in an SQLite statement to represent a null value.
	 */
	public static final String SQLITE_NULL = "NULL" ;

	/** Begins an {@code ORDER BY} clause in a SQL {@code SELECT} statement. */
	public static final String SQL_ORDER_BY = " ORDER BY " ;

	/** Specifies that a column should be ordered ascending. */
	public static final String SQL_ORDER_ASC = "ASC" ;

	/** Specifies that a column should be ordered descending. */
	public static final String SQL_ORDER_DESC = "DESC" ;

	/** Begins a SQL {@code SELECT} operation. */
	public static final String SQL_SELECT = "SELECT " ;

	/**
	 * SQL syntax to specify that all columns should be returned by a
	 * {@code SELECT} statement. This is the usual SQL asterisk wildcard
	 * ({@code *}), <i>not</i> the magic Android/SQLite parameter that would be
	 * passed to a {@link android.database.sqlite.SQLiteDatabase#query}
	 * invocation.
	 * @see #SELECT_ALL
	 */
	public static final String SQL_SELECT_ALL_COLUMNS = "*" ;

	/**
	 * SQLite keyword for {@code INSERT} and {@code UPDATE} queries that set
	 * explicit column sets.
	 */
	public static final String SQL_SET = " SET " ;

	/** Begins a SQL {@code UPDATE} operation. */
	public static final String SQL_UPDATE = "UPDATE " ;

	/**
	 * The character that stands in for a variable value in the Android format
	 * string that is passed to {@link android.database.sqlite.SQLiteDatabase}
	 * query methods.
	 */
	public static final String SQLITE_VAR = "?" ;

	/** Begins a {@code WHERE} clause in an SQL statement. */
	public static final String SQL_WHERE = " WHERE " ;

/// Static Constants: SQLite Data Types ////////////////////////////////////////

	/** The data type token representing integer columns in SQLite. */
	public static final String SQLITE_TYPE_INT = "INTEGER" ;

	/** The data type token representing text columns in SQLite. */
	public static final String SQLITE_TYPE_TEXT = "TEXT" ;

	/** The data type token representing decimal-numeric columns in SQLite. */
	public static final String SQLITE_TYPE_REAL = "REAL" ;

/// Other //////////////////////////////////////////////////////////////////////

	/** Forbid instantiation. */
	private SQLiteSyntax() {}
}
