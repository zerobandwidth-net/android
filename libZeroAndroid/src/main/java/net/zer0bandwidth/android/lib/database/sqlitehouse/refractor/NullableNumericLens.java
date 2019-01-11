package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

/**
 * Serves as the basis for lenses that refract the numeric object types (i.e.
 * the descendants of {@link Number}.
 * @param <NC> the numeric wrapper class
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
public abstract class NullableNumericLens<NC extends Number>
extends Lens<NC>
implements Refractor<NC>
{
	/**
	 * Forces the default value of the nullable field to be {@code null} instead
	 * of whatever "zero" is for this data type.
	 * @return {@code null}
	 */
	@Override
	public NC getSQLiteDefaultValue()
	{ return null ; }

	@Override
	@SuppressWarnings( "unchecked" ) // wrapped in try/catch
	public NC getValueFrom( SQLightable o, Field fld )
	throws IllegalAccessException, ClassCastException
	{
		try { return ((NC)( fld.get(o) )) ; }
		catch( ClassCastException xCast )
		{
			Log.e( this.getClass().getSimpleName(),
					"Could not cast the value to the appropriate type.",
					xCast
				);
			throw xCast ;
		}
	}

	@Override
	public NullableNumericLens<NC> addToContentValues( ContentValues vals, String sKey, NC val )
	{
		if( val == null ) vals.putNull(sKey) ;
		else this.addNonNullToContentValues( vals, sKey, val ) ;
		return this ;
	}

	/**
	 * Invokes the specific method in {@link ContentValues} that would add a
	 * value of the appropriate type. There's no way for us to provide a
	 * concrete, general-case implementation here, because the method signatures
	 * in {@code ContentValues} are all distinct.
	 * @param vals the set of content values
	 * @param sKey the content value key
	 * @param val the non-null value
	 */
	protected abstract void addNonNullToContentValues( ContentValues vals, String sKey, @NonNull NC val ) ;

	/**
	 * Writes either the numeric value (if {@code val} is non-null), or a null
	 * <i>string</i> (if {@code val} is null), into the bundle. This is a
	 * strange conceit, but it exploits the ability to write null <i>strings</i>
	 * into a bundle in order to create placeholders for null numeric values
	 * instead. This means that the consumer of the lens <i>must</i> use the
	 * {@link #fromBundle} method to fetch the value, to ensure that the null
	 * values won't cause further issues
	 */
	@Override
	public NullableNumericLens<NC> addToBundle( Bundle bndl, String sKey, NC val )
	{
		if( val == null ) bndl.putString( sKey, null ) ;
		else this.addNonNullToBundle( bndl, sKey, val ) ;
		return this ;
	}

	/**
	 * Invokes the specific method in {@link Bundle} that would add a value of
	 * the appropriate type. There's no way for us to provide a concrete,
	 * general-case implementation here, because the methods in {@code Bundle}
	 * all have distinct names.
	 * @param bndl the bundle
	 * @param sKey the field's key
 	 * @param val the value to be stored
	 */
	protected abstract void addNonNullToBundle( Bundle bndl, String sKey, @NonNull NC val ) ;

	@Override
	public NC fromCursor( Cursor crs, String sKey )
	{
		int nColumn = crs.getColumnIndex(sKey) ;
		return( crs.isNull(nColumn) ? null :
				this.getNonNullFromCursor( crs, nColumn ) ) ;
	}

	/**
	 * Invokes the specific getter method in {@link Cursor} that would fetch the
	 * value of the appropriate type.
	 * @param crs the cursor from which data should be fetched
	 * @param nColumn the index of the column (found in {@link #fromCursor}
	 * @return the value from the cursor
	 */
	protected abstract NC getNonNullFromCursor( Cursor crs, int nColumn ) ;

	/**
	 * Gets the numeric value from the bundle and casts it to the appropriate
	 * type. Unlike other methods in the base {@link NullableNumericLens} class,
	 * this implementation <i>can</i> be provided concretely at the top level,
	 * because the object fetched from the bundle will retain its class and
	 * will thus be castable.
	 */
	@Override
	@SuppressWarnings("unchecked") // wrapped in try/catch
	public NC fromBundle( Bundle bndl, String sKey )
	throws ClassCastException
	{
		if( !bndl.containsKey(sKey) || bndl.get(sKey) == null ) return null ;
		else try { return ((NC)( bndl.get(sKey) )) ; }
		catch( ClassCastException xCast )
		{
			Log.e( this.getClass().getSimpleName(),
					"Could not cast bundle element to appropriate class.",
					xCast
				);
			throw xCast ;
		}
	}
}
