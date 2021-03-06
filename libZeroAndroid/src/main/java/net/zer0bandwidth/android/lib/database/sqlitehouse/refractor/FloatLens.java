package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;
import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals floating-point numbers.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
public class FloatLens
extends Lens<Float>
implements Refractor<Float>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_REAL ; }

	/**
	 * Defines the default value as zero.
	 * @return {@code 0.0f}
	 */
	@Override
	public Float getSQLiteDefaultValue()
	{ return 0.0f ; }

	@Override
	public Float getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getFloat(o) ; }

	@Override
	public FloatLens addToContentValues( ContentValues vals, String sKey, Float val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public FloatLens addToBundle( Bundle bndl, String sKey, Float val )
	{
		bndl.putFloat( sKey, val ) ;
		return this ;
	}

	@Override
	public Float fromCursor( Cursor crs, String sKey )
	{ return crs.getFloat( crs.getColumnIndex( sKey ) ) ; }

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	@Override
	public Float fromBundle( Bundle bndl, String sKey )
	{ return bndl.getFloat( sKey ) ; }
}
