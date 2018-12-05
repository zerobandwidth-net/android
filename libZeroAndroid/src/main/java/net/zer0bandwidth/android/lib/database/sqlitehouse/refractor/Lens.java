package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Provides canonical implementations of various methods of {@link Refractor}.
 * Custom implementations of {@code Refractor} may choose to extend this class
 * or not; all internal code within the library refers to the interface, not the
 * abstract class.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
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
	{ return ( o == null ? SQLITE_NULL : o.toString() ) ; }

	/**
	 * This is the simplest and canonical implementation of the method specified
	 * by {@link Refractor}.
	 */
	@Override
	public String getSQLiteDefaultString()
	{ return this.toSQLiteString( this.getSQLiteDefaultValue() ) ; }

	/**
	 * This implementation simply uses {@link Field#get} and tries to cast it to
	 * the lens's template parameter type. Actual implementation classes should
	 * override this method if the data type is a primitive.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return ((T)(fld.get(o))) ; }
}
