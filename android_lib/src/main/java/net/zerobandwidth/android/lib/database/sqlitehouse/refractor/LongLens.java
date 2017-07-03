package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals long integers.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class LongLens
extends Lens<Long>
implements Refractor<Long>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value as zero.
	 * @return {@code 0L}
	 */
	@Override
	public Long getSQLiteDefaultValue()
	{ return 0L ; }

	@Override
	public Long getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return fld.getLong(o) ; }

	@Override
	public Refractor<Long> addToContentValues( ContentValues vals, String sKey, Long val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Long fromCursor( Cursor crs, String sKey )
	{ return crs.getLong( crs.getColumnIndex( sKey ) ) ; }
}
