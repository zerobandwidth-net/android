package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

import java.sql.Date;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Marshals {@link java.sql.Date} objects.
 *
 * The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Date} objects upon retrieval. This is because
 * integer timestamps are more reliably stored and are absolute (no fretting
 * about timezones).
 *
 * @see DateLens
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
public class SQLDateLens
extends Lens<Date>
implements Refractor<Date>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value of a date column as null. This is different
	 * from most Java datetime-related objects, which initialize by default to
	 * the current time. In a database, this should instead be left as null, to
	 * be overwritten with an explicit time later.
	 * @return {@code null}
	 */
	@Override
	public Date getSQLiteDefaultValue()
	{ return null ; }

	@Override
	public String toSQLiteString( Date o )
	{ return ( o == null ? SQLITE_NULL : Long.toString( o.getTime() ) ) ; }

	@Override
	public SQLDateLens addToContentValues( ContentValues vals, String sKey, Date val )
	{
		vals.put( sKey, val.getTime() ) ;
		return this ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public SQLDateLens addToBundle( Bundle bndl, String sKey, Date val )
	{
		bndl.putLong( sKey, val.getTime() ) ;
		return this ;
	}

	@Override
	public Date fromCursor( Cursor crs, String sKey )
	{
		long ts = crs.getLong( crs.getColumnIndex( sKey ) ) ;
		return new Date(ts) ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public Date fromBundle( Bundle bndl, String sKey )
	{ return new Date( bndl.getLong( sKey ) ) ; }
}
