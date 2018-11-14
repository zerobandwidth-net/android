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

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises {@link CharacterLens}.
 * @since zerobandwidth-net/android [NEXT] (#53)
 */
@RunWith( AndroidJUnit4.class )
public class CharacterLensTest
{
	/**
	 * None of the other test schema classes bother to have a character-type
	 * field, so we have a one-off here.
	 * @since zerobandwidth-net/android [NEXT] (#53)
	 */
	@SQLiteTable( value = "charcoal" )
	private class Charcoal
	implements SQLightable
	{
		@SQLiteColumn( name = "briquette", sql_default="c" )
		public char m_cBriquette = 'c' ;
	}

	protected CharacterLens m_lens = new CharacterLens() ;

	@Test
	public void testGetSQLiteDataType()
	{
		assertEquals( SQLiteSyntax.SQLITE_TYPE_TEXT,
				m_lens.getSQLiteDataType() ) ;
	}

	@Test
	public void testGetSQLiteDefaultValue()
	{ assertTrue( '\0' == m_lens.getSQLiteDefaultValue() ) ; }

	@Test
	public void testToSQLiteString()
	{
		assertEquals( "'x'", m_lens.toSQLiteString( 'x' ) ) ;
		assertEquals( SQLITE_NULL, m_lens.toSQLiteString(null) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws IllegalAccessException
	{
		Charcoal coal = new Charcoal() ;
		Field fldBriquette = Charcoal.Reflection.reflect( Charcoal.class )
								.getField( "briquette" ) ;
		Character c = m_lens.getValueFrom( coal, fldBriquette ) ;
		assertTrue( 'c' == c ) ;
		coal.m_cBriquette = 'p' ;
		c = m_lens.getValueFrom( coal, fldBriquette ) ;
		assertTrue( 'p' == c ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", 'x' ) ;
		assertEquals( "x", vals.get("foo") ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", 'x' ) ;
		assertTrue( 'x' == bndl.getChar("foo") ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", "xyz" ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertTrue( 'x' == m_lens.fromCursor( crs, "foo" ) ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putChar( "foo", 'x' ) ;
		assertTrue( 'x' == m_lens.fromBundle( bndl, "foo" ) ) ;
	}
}
