package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * Marshals integer values which are stored as {@link Integer} objects and thus
 * might or might not be {@code null}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableIntegerLens
extends NullableNumericLens<Integer>
implements Refractor<Integer>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	@Override
	protected void addNonNullToContentValues(
			ContentValues vals, String sKey, @NonNull Integer val )
	{ vals.put( sKey, val ) ; }

	@Override
	protected void addNonNullToBundle(
			Bundle bndl, String sKey, @NonNull Integer val )
	{ bndl.putInt( sKey, val ) ; }

	@Override
	protected Integer getNonNullFromCursor( Cursor crs, int nColumn )
	{ return crs.getInt(nColumn) ; }
}
