package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;

/**
 * An entirely silly implementation of a string lens, which substitutes the
 * word "Sparkles" for any value. Used solely for testing, including
 * {@link CustomLensTest}.
 * @since zerobandwidth-net/android 0.1.5 (#41)
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
	public Refractor<String> addToContentValues( ContentValues vals, String sKey, String val )
	{
		String sVal = ( val == null ? DEFAULT_STRING : val ) ;
		vals.put( sKey, sVal ) ;
		return this ;
	}

	@Override
	public String fromCursor( Cursor crs, String sKey )
	{ return crs.getString( crs.getColumnIndex( sKey ) ) ; }
}
