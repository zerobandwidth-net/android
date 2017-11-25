package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Lens;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;

/**
 * Represents intentionally-bad data to be fed to {@link SQLiteHouse},
 * {@link SQLightable.Reflection}, etc.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 * @see SQLiteHouseTest#testGetRefractor()
 */
public class BorkBorkBork
implements SQLightable
{
	/**
	 * Represents intentionally-bad field data to be fed to {@link SQLiteHouse},
	 * {@link SQLightable.Reflection}, etc.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static class BorkedField
	{
		public BorkedField()
		{ throw new RuntimeException( "Broken field! Bork bork bork!" ) ; }
	}

	/**
	 * Represents an intentionally-broken {@link Refractor} implementation.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static class BorkedLens
	extends Lens<BorkedField>
	implements Refractor<BorkedField>
	{
		public BorkedLens()
		{ throw new RuntimeException( "Broken lens! Bork bork bork!" ) ; }

		@Override
		public String getSQLiteDataType()
		{ return null ; }

		@Override
		public BorkedLens addToContentValues( ContentValues vals, String sKey,
		                                      BorkedField val )
		{ return this ; }

		@Override
		public BorkedLens addToBundle( Bundle bndl, String sKey, BorkedField val )
		{ return this ; }

		@Override
		public BorkedField fromCursor( Cursor crs, String sKey )
		{ return null ; }

		@Override
		public BorkedField fromBundle( Bundle bndl, String sKey )
		{ return null ; }
	}

	/** Broken field with a custom refractor. */
	@SQLiteColumn( name = "borked", refractor = BorkBorkBork.BorkedLens.class )
	@SuppressWarnings( "unused" ) // referenced reflexively in tests
	public BorkedField m_oBorked ;

	/** Broken field, doesn't even define its refractor. */
	@SQLiteColumn( name = "also_borked" )
	@SuppressWarnings( "unused" ) // referenced reflexively in tests
	public BorkedField m_oAlsoBorked ;

	public BorkBorkBork()
	{ throw new RuntimeException( "BORK BORK BORK" ) ; }
}
