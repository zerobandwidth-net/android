package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLitePortal;

import java.sql.Date;

/**
 * Marshals {@link java.sql.Date} objects.
 *
 * The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Date} objects upon retrieval. This is because
 * integer timestamps are more reliably stored and are absolute (no fretting
 * about timezones).
 *
 * @see DateLens
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class SQLDateLens
extends Lens<Date>
implements Refractor<Date>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

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
	{
		return ( o == null ? SQLitePortal.SQLITE_NULL :
				Long.toString( o.getTime() ) ) ;
	}

	@Override
	public Refractor<Date> addToContentValues( ContentValues vals, String sKey, Date val )
	{
		vals.put( sKey, val.getTime() ) ;
		return this ;
	}

	@Override
	public Date fromCursor( Cursor crs, String sKey )
	{
		long ts = crs.getLong( crs.getColumnIndex( sKey ) ) ;
		return new Date(ts) ;
	}
}
