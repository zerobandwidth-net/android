package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.MockCursor;
import net.zer0bandwidth.android.lib.database.SQLiteSyntax;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Dargle;

import org.junit.Test;

import java.lang.reflect.Field;

import static net.zer0bandwidth.android.lib.database.SQLitePortal.SQLITE_TRUE_INT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Exercises {@link BooleanLens}.
 * @since zer0bandwidth-net/android 0.2.1 (#53)
 */
public class BooleanLensTest
{
	protected BooleanLens m_lens = new BooleanLens() ;

	@Test
	public void testGetSQLiteDataType()
	{
		assertEquals( SQLiteSyntax.SQLITE_TYPE_INT,
				m_lens.getSQLiteDataType() ) ;
	}

	@Test
	public void testGetSQLiteDefaultValue()
	{ assertFalse( m_lens.getSQLiteDefaultValue() ) ; }

	@Test
	public void testToSQLiteString()
	{
		assertEquals( "0", m_lens.toSQLiteString( false ) ) ;
		assertEquals( "1", m_lens.toSQLiteString( true ) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws IllegalAccessException
	{
		Dargle dargle = new Dargle( "foo", true, 0 ) ;
		Field fldBoolean = Dargle.Reflection.reflect( Dargle.class )
							.getField("is_dargly") ;
		assertTrue( m_lens.getValueFrom( dargle, fldBoolean ) ) ;
		dargle.toggle() ;
		assertFalse( m_lens.getValueFrom( dargle, fldBoolean ) ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", true ) ;
		assertEquals( SQLITE_TRUE_INT, vals.get("foo") ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", true ) ;
		assertTrue( bndl.getBoolean("foo") ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", 1 ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertTrue( m_lens.fromCursor( crs, "foo" ) ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putBoolean( "foo", true ) ;
		assertTrue( m_lens.fromBundle( bndl, "foo" ) ) ;
	}

}
