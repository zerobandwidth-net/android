package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;

import java.util.Date;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Marshals {@link java.util.Date} objects.
 *
 * <p>The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Date} objects upon retrieval. This is because
 * integer timestamps are more reliably stored and are absolute (no fretting
 * about timezones).</p>
 *
 * @see SQLDateLens
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class DateLens
extends Lens<Date>
implements Refractor<Date>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	/**
	 * When a date cannot be null, this class provides a default value at the
	 * start of the epoch.
	 * @return a {@code Date} initialized at epoch time zero
	 */
	@Override
	public Date getSQLiteDefaultValue()
	{ return new Date(0) ; }

	@Override
	public String toSQLiteString( Date o )
	{
		return ( o == null ? SQLITE_NULL :
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
		return new Date( ts ) ;
	}
}
