package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals integers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class IntegerLens
extends Lens<Integer>
implements Refractor<Integer>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value as zero.
	 * @return {@code 0}
	 */
	@Override
	public Integer getSQLiteDefaultValue()
	{ return 0 ; }

	@Override
	public Integer getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getInt(o) ; }

	@Override
	public IntegerLens addToContentValues( ContentValues vals, String sKey, Integer val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public IntegerLens addToBundle( Bundle bndl, String sKey, Integer val )
	{
		bndl.putInt( sKey, val ) ;
		return this ;
	}

	@Override
	public Integer fromCursor( Cursor crs, String sKey )
	{ return crs.getInt( crs.getColumnIndex( sKey ) ) ; }

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public Integer fromBundle( Bundle bndl, String sKey )
	{ return bndl.getInt( sKey ) ; }
}
