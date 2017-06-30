package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.RefractorMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

/**
 * Exercises {@link RefractorMap}
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@RunWith( AndroidJUnit4.class )
public class RefractorMapTest
{
	/**
	 * Exercises {@link RefractorMap#getSQLiteColumnTypeFor(Class)}, explicitly
	 * verifying some of the hackery that is implied therein.
	 */
	@Test
	public void testGetSQLiteColumnType()
	{
		RefractorMap map = (new RefractorMap()).init() ;
		assertEquals( "TEXT",
				map.getSQLiteColumnTypeFor( String.class ) ) ;
		assertEquals( "TEXT",
				map.getSQLiteColumnTypeFor( Character.class ) ) ;
		assertEquals( "TEXT",
				map.getSQLiteColumnTypeFor( char.class ) ) ;

		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( Integer.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( int.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( Short.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( short.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( Long.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( long.class ) ) ;

		assertEquals( "REAL",
				map.getSQLiteColumnTypeFor( Float.class ) ) ;
		assertEquals( "REAL",
				map.getSQLiteColumnTypeFor( float.class ) ) ;
		assertEquals( "REAL",
				map.getSQLiteColumnTypeFor( Double.class ) ) ;
		assertEquals( "REAL",
				map.getSQLiteColumnTypeFor( double.class ) ) ;

		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( Boolean.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( boolean.class ) ) ;

		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( java.util.Date.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( java.sql.Date.class ) ) ;
		assertEquals( "INT",
				map.getSQLiteColumnTypeFor( GregorianCalendar.class ) ) ;
	}
}
