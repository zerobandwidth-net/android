package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

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
	{ return SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value as zero.
	 * @return {@code 0}
	 */
	@Override
	public Short getSQLiteDefaultValue()
	{ return 0 ; }

	@Override
	public Refractor<Short> addToContentValues( ContentValues vals, String sKey, Short val )
	{
		vals.put( sKey, val ) ;
		return this ;
	}

	@Override
	public Short fromCursor( Cursor crs, String sKey )
	{ return crs.getShort( crs.getColumnIndex( sKey ) ) ; }
}
