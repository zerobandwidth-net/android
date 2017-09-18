package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Marshals Boolean values by converting them to/from integers.
 * @see SQLitePortal#boolToInt(boolean)
 * @see SQLitePortal#intToBool(int)
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class BooleanLens
extends Lens<Boolean>
implements Refractor<Boolean>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_INT ; }

	/**
	 * Defines the default value for Boolean types as {@code false}.
	 * @return {@code false}
	 */
	@Override
	public Boolean getSQLiteDefaultValue()
	{ return false ; }

	@Override
	public String toSQLiteString( Boolean o )
	{
		return ( o == null ? SQLITE_NULL :
				Integer.toString( SQLitePortal.boolToInt(o) ) ) ;
	}

	@Override
	public Boolean getValueFrom( SQLightable o, Field fld )
			throws IllegalAccessException
	{ return fld.getBoolean(o) ; }

	@Override
	public BooleanLens addToContentValues(
			ContentValues vals, String sKey, Boolean val )
	{
		vals.put( sKey, SQLitePortal.boolToInt(val) ) ;
		return this ;
	}

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public BooleanLens addToBundle( Bundle bndl, String sKey, Boolean val )
	{
		bndl.putBoolean( sKey, val ) ;
		return this ;
	}

	@Override
	public Boolean fromCursor( Cursor crs, String sKey )
	{
		return SQLitePortal.intToBool(
				crs.getInt( crs.getColumnIndex(sKey) ) ) ;
	}

	/** @since zerobandwidth-net/android 0.1.7 (#50) */
	@Override
	public Boolean fromBundle( Bundle bndl, String sKey )
	{ return bndl.getBoolean(sKey) ; }
}
