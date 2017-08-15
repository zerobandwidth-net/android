package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Defines how data is moved between a Java data type and its corresponding
 * SQLite data type.
 *
 * This library defines several default refractors, some of which are simple,
 * and others of which exhibit varying degrees of magic hackery. Apps may also
 * define their own custom {@code Refractor} implementations and register them
 * in their
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * descendants, after initialization but before opening a connection.
 *
 * The package name is a reference to the Fresnel lens of a lighthouse; a set of
 * implementation classes metaphorically define the rings of a Fresnel lens
 * through which the data is refracted.
 *
 * @see Lens
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public interface Refractor<T>
{
	/** @deprecated replaced by {@link SQLiteSyntax#SQLITE_TYPE_INT} in 0.1.7 (#48) */
	@SuppressWarnings( "unused" ) // Great!
	String SQLITE_TYPE_INT = SQLiteSyntax.SQLITE_TYPE_INT ;
	/** @deprecated replaced by {@link SQLiteSyntax#SQLITE_TYPE_REAL} in 0.1.7 (#48) */
	@SuppressWarnings( "unused" ) // Great!
	String SQLITE_TYPE_REAL = SQLiteSyntax.SQLITE_TYPE_REAL ;
	/** @deprecated replaced by {@link SQLiteSyntax#SQLITE_TYPE_TEXT} in 0.1.7 (#48) */
	@SuppressWarnings( "unused" ) // Great!
	String SQLITE_TYPE_TEXT = SQLiteSyntax.SQLITE_TYPE_TEXT ;

	/**
	 * Defines the SQLite data type to be used when writing a value into the
	 * database. This is used to create the column definition.
	 * @return the SQLite data type string for this Java data type
	 */
	String getSQLiteDataType() ;

	/**
	 * Returns a reasonable <b>non-null</b> default value for a column of this
	 * data type in SQLite, particularly for cases in which the value
	 * <i>cannot</i> be null. Examples would be {@code 0} for integer types,
	 * an empty string for string types, {@code false} for Boolean types, etc.
	 * @return a reasonable non-null default value for the data type
	 */
	T getSQLiteDefaultValue() ;

	/**
	 * Converts a Java thing's value into a string to be used in a SQLite query,
	 * for example, in a {@code WHERE} clause filter.
	 * @param o the Java thing to be converted
	 * @return a string serialization for use in an SQLite statement
	 */
	String toSQLiteString( T o ) ;

	/**
	 * Returns a string representation of the column type's default value, for
	 * use in an SQLite statement. The {@link Lens} abstract class provides a
	 * canonical implementation of this method:
	 *
	 * <pre>
	 *     return this.toSQLiteString( this.getSQLiteDefaultValue() ) ;
	 * </pre>
	 *
	 * @return the string representation of the column type's default value
	 */
	@SuppressWarnings("unused") // It's used in the Lens abstract descendant...
	String getSQLiteDefaultString() ;

	/**
	 * Gets the value of a field which would be appropriate for this refractor.
	 * @param o the object instance from which the value will be extracted
	 * @param fld the field from which the value will be extracted
	 * @return the value
	 */
	T getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException ;

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
