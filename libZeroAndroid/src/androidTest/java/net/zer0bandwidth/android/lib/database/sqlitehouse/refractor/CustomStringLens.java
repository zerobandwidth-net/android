package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * An entirely silly implementation of a string lens, which substitutes the
 * word "Sparkles" for any value. Used solely for testing, including
 * {@link CustomLensTest}.
 * @since zer0bandwidth-net/android 0.1.5 (#41)
 */
public class CustomStringLens
extends Lens<String>
implements Refractor<String>
{
	/** A silly default value. */
	public static final String DEFAULT_STRING = "Sparkles" ;

	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_TEXT ; }

	@Override
	public String getSQLiteDefaultValue()
	{ return DEFAULT_STRING ; }

	@Override
	public String toSQLiteString( String o )
	{
		String s = ( o == null ? DEFAULT_STRING : o ) ;
		return String.format( "'%s'", s ) ;
	}

	@Override
	public CustomStringLens addToContentValues( ContentValues vals, String sKey, String val )
	{
		String sVal = ( val == null ? DEFAULT_STRING : val ) ;
		vals.put( sKey, sVal ) ;
		return this ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public CustomStringLens addToBundle( Bundle bndl, String sKey, String val )
	{
		String sVal = ( val == null ? DEFAULT_STRING : val ) ;
		bndl.putString( sKey, sVal ) ;
		return this ;
	}

	@Override
	public String fromCursor( Cursor crs, String sKey )
	{ return crs.getString( crs.getColumnIndex( sKey ) ) ; }

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public String fromBundle( Bundle bndl, String sKey )
	{ return bndl.getString( sKey ) ; }
}
