package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * Marshals double-precision floating-point values which are managed as
 * {@link Double} objects and thus might or might not be {@code null}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableDoubleLens
extends NullableNumericLens<Double>
implements Refractor<Double>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_REAL ; }

	@Override
	protected void addNonNullToContentValues(
			ContentValues vals, String sKey, @NonNull Double val )
	{ vals.put( sKey, val ) ; }

	@Override
	protected void addNonNullToBundle(
			Bundle bndl, String sKey, @NonNull Double val )
	{ bndl.putDouble( sKey, val ) ; }

	@Override
	protected Double getNonNullFromCursor( Cursor crs, int nColumn )
	{ return crs.getDouble(nColumn) ; }
}
