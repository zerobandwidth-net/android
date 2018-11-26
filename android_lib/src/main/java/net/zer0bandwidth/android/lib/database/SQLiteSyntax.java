package net.zer0bandwidth.android.lib.database;

/**
 * Provides named constants for various SQLite syntactic elements, magic values,
 * etc., to any class that needs them. The constants in this class may be
 * individually obtained via static imports, or the entire class may be imported
 * to make all constants available to the consuming class.
 *
 * <h3>Magic Values for Android Database Methods</h3>
 *
 * <ul>
 * <li>{@link #COLUMN_NOT_FOUND} = -1</li>
 * <li>{@link #DELETE_ALL} = {@code "1"}</li>
 * <li>{@link #DELETE_FAILED} = -1</li>
 * <li>{@link #INSERT_FAILED} = -1L</li>
 * <li>{@link #REPLACE_FAILED} = -1L</li>
 * <li>{@link #SELECT_ALL} = {@code null}</li>
 * <li>{@link #UPDATE_ALL} = {@code "1"}</li>
 * <li>{@link #UPDATE_FAILED} = -1</li>
 * </ul>
 *
 * <h3>SQLite Syntactic Tokens</h3>
 *
 * <ul>
 * <li>{@link #SQL_ADD_COLUMN} = {@code " ADD COLUMN "}</li>
 * <li>{@link #SQL_ALTER_TABLE} = {@code "ALTER TABLE "}</li>
 * <li>{@link #SQL_COLUMN_DEFAULT} = {@code " DEFAULT "}</li>
 * <li>{@link #SQL_COLUMN_DEFAULT_NULL} = {@code " DEFAULT NULL"}</li>
 * <li>{@link #SQL_COLUMN_IS_KEYLIKE} = {@code " UNIQUE NOT NULL"}</li>
 * <li>{@link #SQL_COLUMN_NULLABLE} = {@code " NULL"}</li>
 * <li>{@link #SQL_COLUMN_NOT_NULLABLE} = {@code " NOT NULL"}</li>
 * <li>{@link #SQL_DELETE_FROM} = {@code "DELETE FROM "}</li>
 * <li>{@link #SQL_FROM} = {@code " FROM "}</li>
 * <li>{@link #SQL_GROUP_BY} = {@code " GROUP BY "}</li>
 * <li>{@link #SQL_HAVING} = {@code " HAVING "}</li>
 * <li>{@link #SQL_INSERT_INTO} = {@code "INSERT INTO "}</li>
 * <li>{@link #SQL_LIMIT} = {@code " LIMIT "}</li>
 * <li>{@link #SQLITE_NULL} = {@code "NULL"}</li>
 * <li>{@link #SQL_ORDER_BY} = {@code " ORDER BY "}</li>
 * <li>{@link #SQL_ORDER_ASC} = {@code "ASC"}</li>
 * <li>{@link #SQL_ORDER_DESC} = {@code "DESC"}</li>
 * <li>{@link #SQL_SELECT} = {@code "SELECT "}</li>
 * <li>{@link #SQL_SELECT_ALL_COLUMNS} = {@code "*"}</li>
 * <li>{@link #SQL_SET} = {@code " SET "}</li>
 * <li>{@link #SQL_UPDATE} = {@code "UPDATE "}</li>
 * <li>{@link #SQLITE_VAR} = {@code "?"}</li>
 * <li>{@link #SQL_WHERE} = {@code " WHERE "}</li>
 * </ul>
 *
 * <p><b>Note:</b> {@code SQL_COLUMN_DEFAULT} should not be used in conjunction
 * with {@code SQL_COLUMN_DEFAULT_NULL}, which also includes the SQL
 * {@code DEFAULT} keyword. Similarly, {@code SQL_COLUMN_NOT_NULLABLE} should
 * not be used with {@code SQL_COLUMN_IS_KEYLIKE}, which includes the same SQL
 * {@code NOT NULL} clause.</p>
 *
 * <h3>SQLite Type Tokens</h3>
 *
 * <ul>
 * <li>{@link #SQLITE_TYPE_INT} = {@code "INTEGER"}</li>
 * <li>{@link #SQLITE_TYPE_TEXT} = {@code "TEXT"}</li>
 * <li>{@link #SQLITE_TYPE_REAL} = {@code "REAL"}</li>
 * </ul>
 *
 * <h2>Items NOT Provided by This Class</h2>
 *
 * <p>The static constants and methods that support marshalling of Boolean
 * values as integers in SQLite are not defined here; they are defined in
 * {@link SQLitePortal} to keep the constants and methods in one place.</p>
 *
 * @since zer0bandwidth-net/android 0.1.7 (#48)
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
	 * {@link net.zer0bandwidth.android.lib.database.querybuilder.DeletionBuilder}
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
	public static final long INSERT_FAILED = -1L ;

	/**
	 * Magic value returned by
	 * {@link android.database.sqlite.SQLiteDatabase#replace} and related
	 * methods when a row replacement fails. (A value of {@code -1} as the row
	 * ID indicates an error state.)
	 */
	@SuppressWarnings("unused")
	public static final long REPLACE_FAILED = -1L ;

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
	 * @since zer0bandwidth-net/android 0.1.1 (#23)
	 */
	public static final String UPDATE_ALL = "1" ;

	/**
	 * Similar to the standard magic number returned when an insertion query
	 * fails (see {@link #INSERT_FAILED}, this return value from the execution
	 * methods of
	 * {@link net.zer0bandwidth.android.lib.database.querybuilder.UpdateBuilder}
	 * indicates that the delete operation could not be carried out because of
	 * an exception.
	 */
	public static final int UPDATE_FAILED = -1 ;

/// Static Constants: SQLite Syntax ////////////////////////////////////////////

	/**
	 * Begins a SQL {@code ADD COLUMN} clause in an {@code ALTER TABLE}
	 * statement.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_ADD_COLUMN = " ADD COLUMN " ;

	/**
	 * Begins a SQL {@code ALTER TABLE} statement.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_ALTER_TABLE = "ALTER TABLE " ;

	/**
	 * Sets up the part of a column definition clause in which the default value
	 * is specified
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_COLUMN_DEFAULT = " DEFAULT " ;

	/**
	 * Defines a column with a null default value.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_COLUMN_DEFAULT_NULL = " DEFAULT NULL" ;

	/**
	 * Defines a column as "key-like" &mdash; unique and not nullable.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_COLUMN_IS_KEYLIKE = " UNIQUE NOT NULL" ;

	/**
	 * Defines a column as nullable.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_COLUMN_NULLABLE = " NULL" ;

	/**
	 * Defines a column as not nullable.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static final String SQL_COLUMN_NOT_NULLABLE = " NOT NULL" ;

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
	 * @see net.zer0bandwidth.android.lib.database.querybuilder.SelectionBuilder#NO_LIMIT
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
