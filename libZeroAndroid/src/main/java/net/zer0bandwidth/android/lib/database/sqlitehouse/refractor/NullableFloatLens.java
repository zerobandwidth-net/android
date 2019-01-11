package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

/**
 * Marshals single-precision floating-point values which are managed as
 * {@link Float} objects and thus might or might not be {@code null}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public class NullableFloatLens
extends NullableNumericLens<Float>
implements Refractor<Float>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_REAL ; }

	@Override
	protected void addNonNullToContentValues(
			ContentValues vals, String sKey, @NonNull Float val )
	{ vals.put( sKey, val ) ; }

	@Override
	protected void addNonNullToBundle(
			Bundle bndl, String sKey, @NonNull Float val )
	{ bndl.putFloat( sKey,val ) ; }

	@Override
	protected Float getNonNullFromCursor( Cursor crs, int nColumn )
	{ return crs.getFloat(nColumn) ; }
}
