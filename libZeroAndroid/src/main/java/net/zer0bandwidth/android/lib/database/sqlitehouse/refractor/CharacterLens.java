package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;
import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Marshals a character.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
public class CharacterLens
extends Lens<Character>
implements Refractor<Character>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_TEXT ; }

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
		return ( o == null ? SQLITE_NULL :
				String.format( "'%s'", o.toString() ) ) ;
	}

	@Override
	public Character getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getChar(o) ; }

	@Override
	public CharacterLens addToContentValues(
			ContentValues vals, String sKey, Character val )
	{
		vals.put( sKey, val.toString() ) ;
		return this ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public CharacterLens addToBundle( Bundle bndl, String sKey, Character val )
	{
		bndl.putChar( sKey, val ) ;
		return this ;
	}

	@Override
	public Character fromCursor( Cursor crs, String sKey )
	{
		String sVal = crs.getString( crs.getColumnIndex( sKey ) ) ;
		if( sVal == null || sVal.isEmpty() ) return null ;
		return sVal.charAt(0) ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public Character fromBundle( Bundle bndl, String sKey )
	{ return bndl.getChar( sKey ) ; }
}
