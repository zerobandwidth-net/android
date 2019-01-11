package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * Marshals long integer values which are stored as {@link Long} objects and
 * thus might or might not be {@code null}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableLongLens
extends NullableNumericLens<Long>
implements Refractor<Long>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	@Override
	protected void addNonNullToContentValues(
			ContentValues vals, String sKey, @NonNull Long val )
	{ vals.put( sKey, val ) ; }

	@Override
	protected void addNonNullToBundle(
			Bundle bndl, String sKey, @NonNull Long val )
	{ bndl.putLong( sKey, val ) ; }

	@Override
	protected Long getNonNullFromCursor( Cursor crs, int nColumn )
	{ return crs.getLong(nColumn) ; }
}
