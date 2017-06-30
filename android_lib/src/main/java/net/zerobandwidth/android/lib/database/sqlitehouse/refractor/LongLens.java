package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals long integers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class LongLens
implements Refractor<Long>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( Long o )
	{ return o.toString() ; }

	@Override
	public Refractor<Long> addToContentValues( ContentValues vals, String sKey, Long val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Long fromCursor( Cursor crs, String sKey )
	{ return crs.getLong( crs.getColumnIndex( sKey ) ) ; }
}
