package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals a double-precision floating-point number.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class DoubleLens
implements Refractor<Double>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_REAL ; }

	@Override
	public String toSQLiteString( Double o )
	{ return o.toString() ; }

	@Override
	public Refractor<Double> addToContentValues( ContentValues vals, String sKey, Double val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Double fromCursor( Cursor crs, String sKey )
	{ return crs.getDouble( crs.getColumnIndex( sKey ) ) ; }
}
