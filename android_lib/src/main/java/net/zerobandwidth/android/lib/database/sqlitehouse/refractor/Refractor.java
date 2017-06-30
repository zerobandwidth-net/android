package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Defines how data is moved between a Java data type and its corresponding
 * SQLite data type.
 *
 * This library defines several default Fresnelators, some of which are simple,
 * and others of which exhibit varying degrees of magic hackery. Apps may also
 * define their own custom Fresnel implementations and register them in their
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * descendants, after initialization but before opening a connection.
 *
 * The package name is a reference to the Fresnel lens of a lighthouse; a set of
 * implementation classes metaphorically define the rings of a Fresnel lens
 * through which the data is refracted.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public interface Refractor<T>
{
	String SQLITE_TYPE_INT = "INT" ;
	String SQLITE_TYPE_REAL = "REAL" ;
	String SQLITE_TYPE_TEXT = "TEXT" ;

	/**
	 * Defines the SQLite data type to be used when writing a value into the
	 * database. This is used to create the column definition.
	 * @return the SQLite data type string for this Java data type
	 */
	String getSQLiteDataType() ;

	/**
	 * Converts a Java thing's value into a string to be used in a SQLite query,
	 * for example, in a {@code WHERE} clause filter.
	 * @param o the Java thing to be converted
	 * @return a string serialization for use in an SQLite statement
	 */
	String toSQLiteString( T o ) ;

	/**
	 * Determines the correct method in {@link ContentValues} to be used to add
	 * the specified value to a set of content values.
	 * @param vals the set of content values
	 * @param sKey the content value key
	 * @param val the value to be stored
	 * @return (fluid)
	 */
	Refractor<T> addToContentValues( ContentValues vals, String sKey, T val ) ;

	/**
	 * Determines the correct method in {@link Cursor} to be used to fetch the
	 * specified column, then returns that value.
	 * @param crs the cursor from which data should be fetched
	 * @param sKey the data column key
	 * @return the value from the cursor
	 */
	T fromCursor( Cursor crs, String sKey ) ;
}
