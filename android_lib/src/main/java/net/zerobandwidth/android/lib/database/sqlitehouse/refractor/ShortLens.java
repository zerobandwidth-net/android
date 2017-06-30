package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals shorts.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class ShortLens
implements Refractor<Short>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( Short o )
	{ return o.toString() ; }

	@Override
	public Refractor<Short> addToContentValues( ContentValues vals, String sKey, Short val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Short fromCursor( Cursor crs, String sKey )
	{ return crs.getShort( crs.getColumnIndex( sKey ) ) ; }
}
