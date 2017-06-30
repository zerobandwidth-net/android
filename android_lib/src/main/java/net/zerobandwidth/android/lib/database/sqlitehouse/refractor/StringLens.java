package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals strings.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class StringLens
implements Refractor<String>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_TEXT ; }

	@Override
	public String toSQLiteString( String o )
	{ return o ; }

	@Override
	public Refractor<String> addToContentValues( ContentValues vals, String sKey, String val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public String fromCursor( Cursor crs, String sKey )
	{ return crs.getString( crs.getColumnIndex( sKey ) ) ; }
}
