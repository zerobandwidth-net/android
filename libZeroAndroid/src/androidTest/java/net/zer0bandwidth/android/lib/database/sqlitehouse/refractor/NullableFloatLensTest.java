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
 * Exercises {@link NullableFloatLens}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
@RunWith( JUnit4.class )
public class NullableFloatLensTest
{
	private static final double EPSILON = 0.000001 ;

	/**
	 * A schematic class that contains a nullable single-precision
	 * floating-point field.
	 * @since zer0bandwidth-net/android [NEXT] (#63)
	 * @see NullableFloatLensTest
	 */
	@SQLiteTable("nullfloat_test")
	private static class Model implements SQLightable
	{
		@SQLiteColumn( name="test_id", is_nullable=false )
		@SQLitePrimaryKey
		public String m_sID ;

		@SQLiteColumn( name="test_val" )
		public Float m_rValue = null ;

		public Model() {}

		public Model( Float r )
		{
			m_sID = UUID.randomUUID().toString() ;
			m_rValue = r ;
		}
	}

	protected NullableFloatLens m_lens = new NullableFloatLens() ;

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
		assertEquals( "0.0", m_lens.toSQLiteString( 0.0f ) ) ;
		assertEquals( "6.3", m_lens.toSQLiteString( 6.3f ) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws Exception // any exception means failure
	{
		Field fld = Model.Reflection.reflect( Model.class )
                .getField( "test_val" ) ;

		Model o = new Model( null ) ;
		assertNull( m_lens.getValueFrom( o, fld ) ) ;
		o.m_rValue = 6.33f ;
		assertNotNull( m_lens.getValueFrom( o, fld ) ) ;
		assertEquals( 6.33f, m_lens.getValueFrom( o, fld ), EPSILON ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", null ) ;
		assertNull( vals.get("foo") ) ;
		m_lens.addToContentValues( vals, "foo", 6.333f ) ;
		assertEquals( 6.333, vals.getAsFloat("foo"), EPSILON ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", null ) ;
		assertNull( bndl.get("foo") ) ;
		m_lens.addToBundle( bndl, "foo", 6.3333f ) ;
		assertEquals( 6.3333f, bndl.getFloat("foo"), EPSILON ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", ((String)(null)) ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertNull( m_lens.fromCursor( crs, "foo" ) ) ;
		vals.put( "foo", 6.33333f ) ;
		crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertEquals( 6.33333f, m_lens.fromCursor( crs, "foo" ), EPSILON ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putString( "foo", null ) ;
		assertNull( m_lens.fromBundle( bndl, "foo" ) ) ;
		bndl.putFloat( "foo", 6.36363f ) ;
		assertEquals( 6.36363f, m_lens.fromBundle( bndl, "foo" ), EPSILON ) ;
	}

}
