package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Marshals a character.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class CharacterLens
implements Refractor<Character>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_TEXT ; }

	@Override
	public String toSQLiteString( Character o )
	{ return o.toString() ; }

	@Override
	public Refractor<Character> addToContentValues( ContentValues vals, String sKey, Character val )
	{
		vals.put( sKey, val.toString() ) ;
		return null;
	}

	@Override
	public Character fromCursor( Cursor crs, String sKey )
	{
		String sVal = crs.getString( crs.getColumnIndex( sKey ) ) ;
		if( sVal == null || sVal.isEmpty() ) return null ;
		return sVal.charAt(0) ;
	}
}
