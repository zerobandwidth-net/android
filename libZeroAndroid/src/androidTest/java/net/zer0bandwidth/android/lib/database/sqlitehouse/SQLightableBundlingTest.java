package net.zer0bandwidth.android.lib.database.sqlitehouse;

import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.SQLiteHouse.MAGIC_ID_COLUMN_NAME;

/**
 * Exercises the methods within {@link SQLightable.Reflection} which can marshal
 * an instance to/from a {@link Bundle}.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLightableBundlingTest
{
	/**
	 * Verifies that a {@link Fargle} can be marshalled to/from a
	 * {@link Bundle}.
	 */
	@Test
	public void testFargleBundling()
	{
		Fargle fargle = new Fargle( 47, "Foo!", 96 ) ;
		SQLightable.Reflection<Fargle> tblFargle =
				SQLightable.Reflection.reflect( Fargle.class ) ;

		Bundle bndl = tblFargle.toBundle(fargle) ;
		assertEquals( 47, bndl.getInt( "fargle_id" ) ) ;
		assertEquals( "Foo!", bndl.getString( "fargle_string" ) ) ;
		assertEquals( 96, bndl.getInt( "fargle_num" ) ) ;

		Fargle fargleCopy = tblFargle.fromBundle( bndl ) ;
		assertTrue( fargle.equals(fargleCopy) ) ;
	}

	/**
	 * Verifies that a {@link Dargle} can be marshalled to/from a
	 * {@link Bundle}.
	 */
	@Test
	public void testDargleBundling()
	{
		Dargle dargle = new Dargle( "Foo!", false, 42 ) ;
		dargle.setRowID( 99 ) ;
		SQLightable.Reflection<Dargle> tblDargle =
				SQLightable.Reflection.reflect( Dargle.class ) ;

		Bundle bndl = tblDargle.toBundle(dargle) ;
		assertEquals( 99, bndl.getLong( MAGIC_ID_COLUMN_NAME ) ) ;
		assertEquals( "Foo!", bndl.getString( "dargle_string" ) ) ;
		assertFalse( bndl.getBoolean( "is_dargly" ) ) ;

		Dargle dargleCopy = tblDargle.fromBundle( bndl ) ;
		assertEquals( 99, dargleCopy.getRowID() ) ;
		assertEquals( "Foo!", dargleCopy.getString() ) ;
		assertFalse( dargleCopy.isDargly() ) ;
		assertEquals( -1, dargleCopy.getIgnored() ) ; // was not bundled
	}
}
