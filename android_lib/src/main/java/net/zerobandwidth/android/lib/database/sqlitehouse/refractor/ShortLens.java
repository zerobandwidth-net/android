package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals shorts.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class ShortLens
extends Lens<Short>
implements Refractor<Short>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value as zero.
	 * @return {@code 0}
	 */
	@Override
	public Short getSQLiteDefaultValue()
	{ return 0 ; }

	@Override
	public Short getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getShort(o) ; }

	@Override
	public ShortLens addToContentValues( ContentValues vals, String sKey, Short val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public ShortLens addToBundle( Bundle bndl, String sKey, Short val )
	{
		bndl.putShort( sKey, val ) ;
		return this ;
	}

	@Override
	public Short fromCursor( Cursor crs, String sKey )
	{ return crs.getShort( crs.getColumnIndex( sKey ) ) ; }

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public Short fromBundle( Bundle bndl, String sKey )
	{ return bndl.getShort( sKey ) ; }
}
