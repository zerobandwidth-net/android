package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

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
	{ return SQLITE_TYPE_INT ; }

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
	public Refractor<Integer> addToContentValues( ContentValues vals, String sKey, Integer val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Integer fromCursor( Cursor crs, String sKey )
	{ return crs.getInt( crs.getColumnIndex( sKey ) ) ; }
}
