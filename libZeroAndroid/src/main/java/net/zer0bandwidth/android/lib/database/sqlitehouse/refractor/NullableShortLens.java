package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * Marshals short integer values which are stored as {@link Short} objects and
 * thus might or might not be {@code null}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableShortLens
extends NullableNumericLens<Short>
implements Refractor<Short>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	@Override
	protected void addNonNullToContentValues(
			ContentValues vals, String sKey, @NonNull Short val )
	{ vals.put( sKey, val ) ; }

	@Override
	protected void addNonNullToBundle(
			Bundle bndl, String sKey, @NonNull Short val )
	{ bndl.putShort( sKey, val ) ; }

	@Override
	protected Short getNonNullFromCursor( Cursor crs, int nColumn )
	{ return crs.getShort(nColumn) ; }
}
