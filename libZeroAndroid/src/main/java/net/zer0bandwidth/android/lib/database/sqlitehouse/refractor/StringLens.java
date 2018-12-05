package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Marshals strings.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
public class StringLens
extends Lens<String>
implements Refractor<String>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_TEXT ; }

	/**
	 * Defines the non-null default string value as an empty string.
	 * @return {@code null}
	 */
	@Override
	public String getSQLiteDefaultValue()
	{ return "" ; }

	@Override
	public String toSQLiteString( String o )
	{ return ( o == null ? SQLITE_NULL : String.format( "'%s'", o ) ) ; }

	@Override
	public StringLens addToContentValues( ContentValues vals, String sKey, String val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public StringLens addToBundle( Bundle bndl, String sKey, String val )
	{
		bndl.putString( sKey, val ) ;
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
