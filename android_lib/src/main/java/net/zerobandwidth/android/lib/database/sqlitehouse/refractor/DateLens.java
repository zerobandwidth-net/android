package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Marshals {@link java.util.Date} objects.
 *
 * The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Date} objects upon retrieval. This is because
 * integer timestamps are more reliably stored and are absolute (no fretting
 * about timezones).
 *
 * @see SQLDateLens
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class DateLens
implements Refractor<Date>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( Date o )
	{ return Long.toString( o.getTime() ) ; }

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
		return new Date( ts ) ;
	}
}
