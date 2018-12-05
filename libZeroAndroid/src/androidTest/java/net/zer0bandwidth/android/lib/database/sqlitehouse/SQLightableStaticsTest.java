package net.zer0bandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.BorkBorkBork;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Sparkle;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Exercises the static utility methods of {@link SQLightable.Reflection}.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLightableStaticsTest
{
	/**
	 * Exercises the static {@link SQLightable.Reflection#getTableName(Class)},
	 * which, in turn, exercises the protected
	 * {@link SQLightable.Reflection#getTableName(Class, SQLiteTable)}.
	 */
	@Test
	public void testStaticGetTableName()
	{
		assertEquals( "fargles",
				SQLightable.Reflection.getTableName( Fargle.class ) ) ;
		assertEquals( "dargles",
				SQLightable.Reflection.getTableName( Dargle.class ) ) ;
		assertEquals( "sparkles",
				SQLightable.Reflection.getTableName( Sparkle.class ) ) ;
		// Also works on classes that don't have an annotation.
		assertEquals( "blargh",
				SQLightable.Reflection.getTableName( Blargh.class ) ) ;
		// Also works on classes whose reflections are otherwise broken.
		assertEquals( "borkborkbork",
				SQLightable.Reflection.getTableName( BorkBorkBork.class ) ) ;
	}

	/** Exercises {@link SQLightable.Reflection#buildInsert(Class)}. */
	@Test
	public void testBuildInsert()
	{
		assertTrue( SQLightable.Reflection.buildInsert( Fargle.class )
				.toString().startsWith( "INSERT INTO fargles" ) ) ;
	}

	/** Exercises {@link SQLightable.Reflection#buildUpdate(Class)}. */
	@Test
	public void testBuildUpdate()
	{
		assertTrue( SQLightable.Reflection.buildUpdate( Dargle.class )
				.toString().startsWith( "UPDATE dargles" ) ) ;
	}

	/** Exercises {@link SQLightable.Reflection#buildSelect(Class)}. */
	@Test
	public void testBuildSelect()
	{
		assertTrue( SQLightable.Reflection.buildSelect( Blargh.class )
				.toString().startsWith( "SELECT * FROM blargh" ) ) ;
	}

	/** Exercises {@link SQLightable.Reflection#buildDelete(Class)}. */
	@Test
	public void testBuildDelete()
	{
		assertTrue( SQLightable.Reflection.buildDelete( Sparkle.class )
				.toString().startsWith( "DELETE FROM sparkles" ) ) ;
	}
}
