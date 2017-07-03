package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals floating-point numbers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class FloatLens
extends Lens<Float>
implements Refractor<Float>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_REAL ; }

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
	public Refractor<Float> addToContentValues( ContentValues vals, String sKey, Float val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Float fromCursor( Cursor crs, String sKey )
	{ return crs.getFloat( crs.getColumnIndex( sKey ) ) ; }
}
