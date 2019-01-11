package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Marshals long integer values which might or might not be {@code null}, such
 * as epoch timestamps.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableLongLens
extends LongLens
implements Refractor<Long>
{
	/**
	 * Forces the default value for a nullable field to be {@code null} instead
	 * of zero.
	 * @return {@code null}
	 */
	@Override
	public Long getSQLiteDefaultValue()
	{ return null ; }

	@Override
	public Long getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException
	{ return ((Long)( fld.get(o) )) ; }

	@Override
	public NullableLongLens addToContentValues( ContentValues vals, String sKey, Long val )
	{
		if( val == null ) vals.putNull( sKey ) ;
		else vals.put( sKey, val ) ;
		return this ;
	}

	/**
	 * Writes either the long integer value (if {@code val} is non-null) or a
	 * null string (if {@code val} is null) into the bundle. This is a strange
	 * conceit but it exploits the ability to write null strings in order to
	 * allow us to write nullable integers instead. This means that the consumer
	 * of the class <i>must</i> always use {@link #fromBundle} to fetch the
	 * value, to ensure that null values don't cause issues.
	 */
	@Override
	public NullableLongLens addToBundle( Bundle bndl, String sKey, Long val )
	{
		if( val == null ) bndl.putString( sKey, null ) ;
		else bndl.putLong( sKey, val ) ;
		return this ;
	}

	@Override
	public Long fromCursor( Cursor crs, String sKey )
	{
		int nColumn = crs.getColumnIndex(sKey) ;
		return( crs.isNull(nColumn) ? null : crs.getLong(nColumn) ) ;
	}

	@Override
	public Long fromBundle( Bundle bndl, String sKey )
	{
		if( !bndl.containsKey(sKey) || bndl.get(sKey) == null )
			return null ;
		else return bndl.getLong(sKey) ;
	}
}
