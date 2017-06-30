package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals floating-point numbers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class FloatLens
implements Refractor<Float>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_REAL ; }

	@Override
	public String toSQLiteString( Float o )
	{ return o.toString() ; }

	@Override
	public Refractor<Float> addToContentValues( ContentValues vals, String sKey, Float val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Float fromCursor( Cursor crs, String sKey )
	{ return crs.getFloat( crs.getColumnIndex( sKey ) ) ; }
}
