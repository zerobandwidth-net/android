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
 * Exercises {@link NullableIntegerLens}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
@RunWith( JUnit4.class )
public class NullableIntegerLensTest
{
	/**
	 * A schematic class that contains a nullable integer field.
	 * @since zer0bandwidth-net/android [NEXT] (#63)
	 * @see NullableIntegerLensTest
	 */
	@SQLiteTable("nullint_test")
	private static class Model
			implements SQLightable
	{
		@SQLiteColumn( name="test_id", is_nullable=false )
		@SQLitePrimaryKey
		public String m_sID ;

		@SQLiteColumn( name="test_int" )
		public Integer m_nInty = null ;

		public Model() {}

		public Model( Integer n )
		{
			m_sID = UUID.randomUUID().toString() ;
			m_nInty = n ;
		}
	}

	protected NullableIntegerLens m_lens = new NullableIntegerLens() ;

	@Test
	public void testGetSQLiteDataType()
	{
		assertEquals( SQLiteSyntax.SQLITE_TYPE_INT,
				m_lens.getSQLiteDataType() ) ;
	}

	@Test
	public void testGetSQLiteDefaultValue()
	{ assertNull( m_lens.getSQLiteDefaultValue() ) ; }

	@Test
	public void testToSQLiteString()
	{
		assertEquals( SQLITE_NULL, m_lens.toSQLiteString(null) ) ;
		assertEquals( "0", m_lens.toSQLiteString(0) ) ;
		assertEquals( "63", m_lens.toSQLiteString( 63 ) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws Exception // any exception means failure
	{
		Field fld = Model.Reflection.reflect( Model.class )
		                            .getField( "test_int" ) ;

		Model o = new Model( null ) ;
		assertNull( m_lens.getValueFrom( o, fld ) ) ;
		o.m_nInty = 63 ;
		assertNotNull( m_lens.getValueFrom( o, fld ) ) ;
		assertEquals( 63, m_lens.getValueFrom( o, fld ).intValue() ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", null ) ;
		assertNull( vals.get("foo") ) ;
		m_lens.addToContentValues( vals, "foo", 63 ) ;
		assertEquals( 63, vals.getAsInteger("foo").intValue() ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", null ) ;
		assertNull( bndl.get("foo") ) ;
		m_lens.addToBundle( bndl, "foo", 63 ) ;
		assertEquals( 63, bndl.getInt("foo") ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", ((String)(null)) ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertNull( m_lens.fromCursor( crs, "foo" ) ) ;
		vals.put( "foo", 63 ) ;
		crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertEquals( 63, m_lens.fromCursor( crs, "foo" ).intValue() ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putString( "foo", null ) ;
		assertNull( m_lens.fromBundle( bndl, "foo" ) ) ;
		bndl.putInt( "foo", 63 ) ;
		assertEquals( 63, m_lens.fromBundle( bndl, "foo" ).intValue() ) ;
	}
}
