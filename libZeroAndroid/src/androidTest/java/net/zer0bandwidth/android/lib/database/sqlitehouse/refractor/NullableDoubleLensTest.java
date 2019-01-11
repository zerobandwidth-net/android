package net.zer0bandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.os.Bundle;

import net.zer0bandwidth.android.lib.database.MockCursor;
import net.zer0bandwidth.android.lib.database.SQLiteSyntax;
import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.UUID;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Exercises {@link NullableDoubleLens}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
@RunWith( JUnit4.class )
public class NullableDoubleLensTest
{
	private static final double EPSILON = 0.000001 ;

	/**
	 * A schematic class that contains a nullable double-precision
	 * floating-point field.
	 * @since zer0bandwidth-net/android [NEXT] (#63)
	 * @see NullableDoubleLensTest
	 */
	@SQLiteTable("nulldouble_test")
	private static class Model implements SQLightable
	{
		@SQLiteColumn( name="test_id", is_nullable=false )
		@SQLitePrimaryKey
		public String m_sID ;

		@SQLiteColumn( name="test_val" )
		public Double m_rValue = null ;

		public Model() {}

		public Model( Double r )
		{
			m_sID = UUID.randomUUID().toString() ;
			m_rValue = r ;
		}
	}

	protected NullableDoubleLens m_lens = new NullableDoubleLens() ;

	@Test
	public void testGetSQLiteDataType()
	{
		assertEquals( SQLiteSyntax.SQLITE_TYPE_REAL,
				m_lens.getSQLiteDataType() ) ;
	}

	@Test
	public void testGetSQLiteDefaultValue()
	{ assertNull( m_lens.getSQLiteDefaultValue() ) ; }

	@Test
	public void testToSQLiteString()
	{
		assertEquals( SQLITE_NULL, m_lens.toSQLiteString(null) ) ;
		assertEquals( "0.0", m_lens.toSQLiteString( 0.0 ) ) ;
		assertEquals( "6.3", m_lens.toSQLiteString( 6.3 ) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws Exception // any exception means failure
	{
		Field fld = Model.Reflection.reflect( Model.class )
                .getField( "test_val" ) ;

		Model o = new Model( null ) ;
		assertNull( m_lens.getValueFrom( o, fld ) ) ;
		o.m_rValue = 6.33 ;
		assertNotNull( m_lens.getValueFrom( o, fld ) ) ;
		assertEquals( 6.33, m_lens.getValueFrom( o, fld ), EPSILON ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", null ) ;
		assertNull( vals.get("foo") ) ;
		m_lens.addToContentValues( vals, "foo", 6.333 ) ;
		assertEquals( 6.333, vals.getAsDouble("foo"), EPSILON ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", null ) ;
		assertNull( bndl.get("foo") ) ;
		m_lens.addToBundle( bndl, "foo", 6.3333 ) ;
		assertEquals( 6.3333, bndl.getDouble("foo"), EPSILON ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", ((String)(null)) ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertNull( m_lens.fromCursor( crs, "foo" ) ) ;
		vals.put( "foo", 6.33333 ) ;
		crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertEquals( 6.33333, m_lens.fromCursor( crs, "foo" ), EPSILON ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putString( "foo", null ) ;
		assertNull( m_lens.fromBundle( bndl, "foo" ) ) ;
		bndl.putDouble( "foo", 6.36363 ) ;
		assertEquals( 6.36363, m_lens.fromBundle( bndl, "foo" ), EPSILON ) ;
	}

}
