package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

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
	{ return SQLITE_TYPE_REAL ; }

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
	public Refractor<Double> addToContentValues( ContentValues vals, String sKey, Double val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Double fromCursor( Cursor crs, String sKey )
	{ return crs.getDouble( crs.getColumnIndex( sKey ) ) ; }
}
