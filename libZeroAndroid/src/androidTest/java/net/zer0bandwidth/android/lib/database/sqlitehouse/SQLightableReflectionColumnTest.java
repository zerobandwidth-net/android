package net.zer0bandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zer0bandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.CustomStringLens;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.IntegerLens;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.StringLens;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.BorkBorkBork;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Flargle;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Quargle;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Sparkle;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Exercises {@link SQLightable.Reflection.Column}.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLightableReflectionColumnTest
{
	/**
	 * Provides additional ways to mess around with the inner workings of a
	 * {@link SQLightable.Reflection} in support of the unit tests in
	 * {@link SQLightableReflectionColumnTest}.
	 * @param <SC> a schematic class
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	private class MagicMirror<SC extends SQLightable>
	extends SQLightable.Reflection<SC>
	{
		public MagicMirror( Class<SC> cls )
		{ super(cls) ; }

		/**
		 * Allows us to try to create a {@link SQLightable.Reflection.Column}
		 * based on an arbitrary field declared in the class.
		 * @param sFieldName the name of the field
		 * @return a column reflection for that field
		 * @throws NoSuchFieldException if the field name is bad
		 * @throws SchematicException if the field isn't annotated
		 * @throws IntrospectionException if refractor discovery fails
		 * @see Column#Column(Field)
		 * @see Column#discoverRefractor()
		 */
		public Column getArbitraryColumn( String sFieldName )
		throws NoSuchFieldException, SchematicException, IntrospectionException
		{
			Field fld = m_clsTable.getField(sFieldName) ;
			fld.setAccessible(true) ;
			return new Column(fld) ;
		}
	}

	/** Exercises the constructor. */
	@Test
	public void testDiscovery()
	{
		SQLightable.Reflection<Quargle>.Column col =
			SQLightable.Reflection.reflect( Quargle.class )
			                      .getColumn( "quargle" ) ;
		assertEquals( "m_sQuargle", col.m_fldColumn.getName() ) ;
		assertEquals( "quargle", col.m_antColumn.name() ) ;
		assertFalse( col.m_bKey ) ;
		assertEquals( StringLens.class, col.m_lens.getClass() ) ;
	}

	/** Exercises the constructor with a bad field input. */
	@Test
	public void testDiscoveryWithUndecoratedField()
	{
		SchematicException xSchema = null ;
		try
		{
			MagicMirror<Blargh> tbl = new MagicMirror<>( Blargh.class ) ;
			tbl.getArbitraryColumn( "m_sRedHerring1" ) ;
		}
		catch( NoSuchFieldException x )
		{ fail( "Couldn't grab the red herring field from Blargh." ) ; }
		catch( SchematicException x )
		{ xSchema = x ; }
		assertNotNull( xSchema ) ;
	}

	/**
	 * Exercises {@link SQLightable.Reflection.Column#discoverRefractor()}
	 * when the column has a custom refractor specification.
	 */
	@Test
	public void testDiscoverCustomRefractor()
	{
		SQLightable.Reflection<Sparkle> tbl =
				SQLightable.Reflection.reflect( Sparkle.class ) ;
		assertEquals( CustomStringLens.class,
				tbl.getColumn( "sparkle" ).getRefractor().getClass() ) ;
		Sparkle o = new Sparkle( "Shiny!" ) ;
		assertEquals( "'Shiny!'",
				tbl.getColumn( "sparkle" ).getSQLColumnValueFrom(o) ) ;
	}

	/**
	 * Exercises {@link SQLightable.Reflection.Column#discoverRefractor()}
	 * when the column and/or refractor is overtly broken.
	 * @see BorkBorkBork
	 */
	@Test
	public void testDiscoverBrokenRefractor()
	{
		SchematicException xSchema = null ;
		try { SQLightable.Reflection.reflect( BorkBorkBork.class ) ; }
		catch( SchematicException x ) { xSchema = x ; }
		assertNotNull( xSchema ) ;
	}

	/**
	 * Exercises the various trivial accessors in
	 * {@link SQLightable.Reflection.Column}.
	 */
	@Test
	public void testAccessors()
	{
		SQLightable.Reflection<Flargle> tbl =
				SQLightable.Reflection.reflect( Flargle.class ) ;
		SQLightable.Reflection<Flargle>.Column col =
				tbl.getColumn( "fargle_id" ) ;
		assertEquals( "m_nFargleID", col.getField().getName() ) ;
		SQLiteColumn antCol = col.getColAttrs() ;
		assertNotNull( antCol ) ;
		assertEquals( "fargle_id", antCol.name() ) ;
		assertTrue( col.isKey() ) ;
		assertEquals( "fargle_id", col.getName() ) ;
		assertEquals( IntegerLens.class, col.getRefractor().getClass() ) ;
	}
}
