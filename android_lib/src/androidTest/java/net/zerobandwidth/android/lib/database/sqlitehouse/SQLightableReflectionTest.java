package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.StringLens;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Quargle;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Exercises {@link SQLightable.Reflection}.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLightableReflectionTest
{
	/** Exercises the constructor and accessors. */
	@Test
	public void testConstructionAndAccess()
	throws Exception // Any uncaught exception is a failure.
	{
		SQLightable.Reflection<Quargle> refl =
				SQLightable.Reflection.reflect( Quargle.class ) ;
		assertEquals( Quargle.class, refl.getTableClass() ) ;
		SQLiteTable antTable = refl.getTableAttrs() ;
		assertEquals( "quargles", antTable.value() ) ;
		assertEquals( 2, antTable.since() ) ;
		assertEquals( "quargles", refl.getTableName() ) ;
		SQLightable.Reflection.Column col = refl.getColumnDef( "quargle" ) ;
		//assertEquals( Quargle.class.getField("m_sQuargle"), col.getField() ) ;
		Field fldQuargle = col.getField() ;
		assertEquals( "m_sQuargle", fldQuargle.getName() ) ;
		SQLiteColumn antCol = col.getColAttrs() ;
		assertEquals( "quargle", antCol.name() ) ;
		assertFalse( col.isKey() ) ;
		assertTrue( col.getRefractor() instanceof StringLens ) ;
	}
}
