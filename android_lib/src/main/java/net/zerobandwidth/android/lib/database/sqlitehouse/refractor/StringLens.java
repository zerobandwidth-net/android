package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Marshals strings.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class StringLens
extends Lens<String>
implements Refractor<String>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_TEXT ; }

	/**
	 * Defines the non-null default string value as an empty string.
	 * @return {@code null}
	 */
	@Override
	public String getSQLiteDefaultValue()
	{ return "" ; }

	@Override
	public String toSQLiteString( String o )
	{
		return ( o == null ? SQLitePortal.SQLITE_NULL :
				String.format( "'%s'", o ) ) ;
	}

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
