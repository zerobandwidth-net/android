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
 * Exercises {@link NullableLongLens}.
 * @since zer0bandwidth-net/android [NEXT] (#63)
 */
@RunWith( JUnit4.class )
public class NullableLongLensTest
{
	/**
	 * A schematic class that contains a nullable long integer field.
	 * @since zer0bandwidth-net/android [NEXT] (#63)
	 * @see NullableLongLensTest
	 */
	@SQLiteTable("nullong_test")
	private static class NullableLongContainer
	implements SQLightable
	{
		@SQLiteColumn( name="test_id", is_nullable=false )
		@SQLitePrimaryKey
		public String m_sID ;

		@SQLiteColumn( name="test_int", is_nullable=true, refractor=NullableLongLens.class )
		public Long m_nLongish = null ;

		public NullableLongContainer() {}

		public NullableLongContainer( Long n )
		{
			m_sID = UUID.randomUUID().toString() ;
			m_nLongish = n ;
		}
	}

	protected NullableLongLens m_lens = new NullableLongLens() ;

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
		assertEquals( "0", m_lens.toSQLiteString(0L) ) ;
		assertEquals( "63", m_lens.toSQLiteString( 63L ) ) ;
	}

	@Test
	public void testGetValueFrom()
	throws Exception // any exception means failure
	{
		Field fld = NullableLongContainer.Reflection
				.reflect( NullableLongContainer.class )
				.getField( "test_int" )
				;

		NullableLongContainer o = new NullableLongContainer( null ) ;
		assertNull( m_lens.getValueFrom( o, fld ) ) ;
		o.m_nLongish = 63L ;
		assertNotNull( m_lens.getValueFrom( o, fld ) ) ;
		assertEquals( 63L, m_lens.getValueFrom( o, fld ).longValue() ) ;
	}

	@Test
	public void testAddToContentValues()
	{
		ContentValues vals = new ContentValues() ;
		m_lens.addToContentValues( vals, "foo", null ) ;
		assertNull( vals.get("foo") ) ;
		m_lens.addToContentValues( vals, "foo", 63L ) ;
		assertEquals( 63L, vals.getAsLong("foo").longValue() ) ;
	}

	@Test
	public void testAddToBundle()
	{
		Bundle bndl = new Bundle() ;
		m_lens.addToBundle( bndl, "foo", null ) ;
		assertNull( bndl.get("foo") ) ;
		m_lens.addToBundle( bndl, "foo", 63L ) ;
		assertEquals( 63L, bndl.getLong("foo") ) ;
	}

	@Test
	public void testFromCursor()
	{
		ContentValues vals = new ContentValues() ;
		vals.put( "foo", ((String)(null)) ) ;
		MockCursor crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertNull( m_lens.fromCursor( crs, "foo" ) ) ;
		vals.put( "foo", 63L ) ;
		crs = new MockCursor(vals) ;
		crs.moveToFirst() ;
		assertEquals( 63L, m_lens.fromCursor( crs, "foo" ).longValue() ) ;
	}

	@Test
	public void testFromBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putString( "foo", null ) ;
		assertNull( m_lens.fromBundle( bndl, "foo" ) ) ;
		bndl.putLong( "foo", 63L ) ;
		assertEquals( 63L, m_lens.fromBundle( bndl, "foo" ).longValue() ) ;
	}
}
