package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals integers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class IntegerLens
implements Refractor<Integer>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( Integer o )
	{ return o.toString() ; }

	@Override
	public Refractor<Integer> addToContentValues( ContentValues vals, String sKey, Integer val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Integer fromCursor( Cursor crs, String sKey )
	{ return crs.getInt( crs.getColumnIndex( sKey ) ) ; }
}
