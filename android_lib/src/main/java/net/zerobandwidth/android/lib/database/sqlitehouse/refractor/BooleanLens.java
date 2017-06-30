package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Marshals Boolean values by converting them to/from integers.
 * @see SQLitePortal#boolToInt(boolean)
 * @see SQLitePortal#intToBool(int)
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class BooleanLens
implements Refractor<Boolean>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( Boolean o )
	{ return Integer.toString( SQLitePortal.boolToInt(o) ) ; }

	@Override
	public Refractor<Boolean> addToContentValues( ContentValues vals, String sKey, Boolean val )
	{
		vals.put( sKey, SQLitePortal.boolToInt(val) ) ;
		return this ;
	}

	@Override
	public Boolean fromCursor( Cursor crs, String sKey )
	{
		return SQLitePortal.intToBool(
				crs.getInt( crs.getColumnIndex(sKey) ) ) ;
	}
}
