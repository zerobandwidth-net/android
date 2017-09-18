package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals a double-precision floating-point number.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class DoubleLens
extends Lens<Double>
implements Refractor<Double>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_REAL ; }

	/**
	 * Defines the non-null default value as zero.
	 * @return {@code 0.0}
	 */
	@Override
	public Double getSQLiteDefaultValue()
	{ return 0.0 ; }

	@Override
	public Double getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getDouble(o) ; }

	@Override
	public DoubleLens addToContentValues( ContentValues vals, String sKey, Double val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public DoubleLens addToBundle( Bundle bndl, String sKey, Double val )
	{
		bndl.putDouble( sKey, val ) ;
		return this ;
	}

	@Override
	public Double fromCursor( Cursor crs, String sKey )
	{ return crs.getDouble( crs.getColumnIndex( sKey ) ) ; }

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public Double fromBundle( Bundle bndl, String sKey )
	{ return bndl.getDouble( sKey ) ; }
}
