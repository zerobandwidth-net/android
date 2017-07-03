package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals a character.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class CharacterLens
extends Lens<Character>
implements Refractor<Character>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_TEXT ; }

	/**
	 * When a character cannot be null, we generate a null character.
	 * @return a null character
	 */
	@Override
	public Character getSQLiteDefaultValue()
	{ return '\0' ; }

	@Override
	public String toSQLiteString( Character o )
	{
		return ( o == null ? SQLitePortal.SQLITE_NULL :
				String.format( "'%s'", o.toString() ) ) ;
	}

	@Override
	public Character getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getChar(o) ; }

	@Override
	public Refractor<Character> addToContentValues(
			ContentValues vals, String sKey, Character val )
	{
		vals.put( sKey, val.toString() ) ;
		return this ;
	}

	@Override
	public Character fromCursor( Cursor crs, String sKey )
	{
		String sVal = crs.getString( crs.getColumnIndex( sKey ) ) ;
		if( sVal == null || sVal.isEmpty() ) return null ;
		return sVal.charAt(0) ;
	}
}
