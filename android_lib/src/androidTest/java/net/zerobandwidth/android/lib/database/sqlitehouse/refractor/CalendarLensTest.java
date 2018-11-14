package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.MockCursor;
import net.zerobandwidth.android.lib.database.SQLiteSyntax;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;

/**
 * Exercises {@link CalendarLens}
 * @since zerobandwidth-net/android [NEXT] (#53)
 */
@RunWith( AndroidJUnit4.class )
public class CalendarLensTest
{
	/**
	 * None of the other test schema classes bother to have a calendar-type
	 * field, so we have a one-off here.
	 * @since zerobandwidth-net/android [NEXT] (#53)
	 */
	@SQLiteTable( value = "got_gregged" )
	private class GotGregged
	implements SQLightable
	{
		@SQLiteColumn( name = "greggo", sql_default="24601" )
		public GregorianCalendar m_cal = new GregorianCalendar() ;

		public GotGregged()
		{ m_cal.setTimeInMillis( 123456789L ) ; }
	}


	protected CalendarLens<Calendar> m_lens = new CalendarLens<>() ;

	@Test
	public void testGetSQLiteDataType()
	{
		assertEquals( SQLiteSyntax.SQLITE_TYPE_INT,
				m_lens.getSQLiteDataType() ) ;
	}

	@Test
	public void testGetSQLiteDefaultValue()
	{
		GregorianCalendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis(0L) ;
		assertEquals( cal, m_lens.getSQLiteDefaultValue() ) ;
	}

	@Test
	public void testToSQLiteString()
	{
		GregorianCalendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis( 123456789L ) ;
		assertEquals( "123456789", m_lens.toSQLiteString(cal) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws IllegalAccessException
	{
		GotGregged oldgreg = new GotGregged() ;
		Field fld = GotGregged.Reflection.reflect( GotGregged.class )
						.getField( "greggo" ) ;
		Calendar cal = m_lens.getValueFrom( oldgreg, fld ) ;
		assertEquals( 123456789L, cal.getTimeInMillis() ) ;
		oldgreg.m_cal.setTimeInMillis( 987654321L ) ;
		cal = m_lens.getValueFrom( oldgreg, fld ) ;
		assertEquals( 987654321L, cal.getTimeInMillis() ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		Calendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis( 123456789L ) ;
		m_lens.addToContentValues( vals, "foo", cal ) ;
		assertEquals( 123456789L, vals.get("foo") ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		Calendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis( 987654321L ) ;
		m_lens.addToBundle( bndl, "foo", cal ) ;
		assertEquals( 987654321L, bndl.getLong( "foo" ) ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", 123456789L ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		Calendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis( 123456789L ) ;
		assertEquals( cal, m_lens.fromCursor( crs, "foo" ) ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putLong( "foo", 987654321L ) ;
		Calendar cal = new GregorianCalendar() ;
		cal.setTimeInMillis( 987654321L ) ;
		assertEquals( cal, m_lens.fromBundle( bndl, "foo" ) ) ;
	}
}
