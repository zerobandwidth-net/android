package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Provides canonical implementations of various methods of {@link Refractor}.
 * Custom implementations of {@code Refractor} may choose to extend this class
 * or not; all internal code within the library refers to the interface, not the
 * abstract class.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public abstract class Lens<T>
implements Refractor<T>
{
	/** @return {@code null} */
	@Override
	public T getSQLiteDefaultValue()
	{ return null ; }

	@Override
	public String toSQLiteString( T o )
	{ return ( o == null ? SQLitePortal.SQLITE_NULL : o.toString() ) ; }

	/**
	 * This is the simplest and canonical implementation of the method specified
	 * by {@link Refractor}.
	 */
	@Override
	public String getSQLiteDefaultString()
	{ return this.toSQLiteString( this.getSQLiteDefaultValue() ) ; }
}
