package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteInheritColumns;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.InheritanceDBSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.ValidSpecClass;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest.connectTo ;
import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest.delete ;
import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest.getTestableInstanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Exercises the inheritance of schematic specifications via a class's ancestry,
 * as supported by the {@link SQLiteInheritColumns} annotation.
 * @since zerobandwidth-net/android 0.2.1 (#56)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseInheritanceTest
{
	@Test
	public void testInheritanceResolution()
	{
		delete( ValidSpecClass.class ) ;
		delete( InheritanceDBSpec.class ) ;

		// Create the original database first.
		ValidSpecClass dbhOriginal = getTestableInstanceOf( ValidSpecClass.class ) ;
		try { connectTo(dbhOriginal) ; }
		finally { dbhOriginal.close() ; }

		// Now upgrade and see what shakes out.
		InheritanceDBSpec dbhUpgraded = getTestableInstanceOf( InheritanceDBSpec.class ) ;
		try
		{
			connectTo(dbhUpgraded) ;
			this.verifyUpgradedFargles( dbhUpgraded ) ;
			this.verifyExtendedDargles( dbhUpgraded ) ;
			this.verifyFargleXMarshalling( dbhUpgraded ) ;
			this.verifyDargleXMarshalling( dbhUpgraded ) ;
		}
		finally
		{ dbhUpgraded.close() ; }
	}

	protected void verifyUpgradedFargles( InheritanceDBSpec dbh )
	{
		SQLightable.Reflection<InheritanceDBSpec.FargleX> tbl =
				dbh.getReflection( InheritanceDBSpec.FargleX.class ) ;
		assertEquals( "fargles", tbl.getTableName() ) ;
		assertEquals( 1, tbl.getTableAttrs().since() ) ;
		assertEquals( 1, tbl.getFirstSchemaVersion() ) ;
		List<SQLightable.Reflection<InheritanceDBSpec.FargleX>.Column> acol =
				tbl.getColumns() ;
		// Expect 4 columns:
		// - (orig) fargle_id
		// - (orig) fargle_string
		// - (orig) fargle_num
		// - (new)  extension_data
		assertEquals( 4, acol.size() ) ;
		assertNotNull( tbl.getColumn( "fargle_id" ) ) ;
		assertNotNull( tbl.getColumn( "fargle_string" ) ) ;
		assertNotNull( tbl.getColumn( "fargle_num" ) ) ;
		assertNotNull( tbl.getColumn( "extension_data" ) ) ;
	}

	protected void verifyExtendedDargles( InheritanceDBSpec dbh )
	{
		SQLightable.Reflection<InheritanceDBSpec.DargleX> tbl =
				dbh.getReflection( InheritanceDBSpec.DargleX.class ) ;
		assertEquals( "extended_dargles", tbl.getTableName() ) ;
		assertEquals( 2, tbl.getTableAttrs().since() ) ;
		assertEquals( 2, tbl.getFirstSchemaVersion() ) ;
		List<SQLightable.Reflection<InheritanceDBSpec.DargleX>.Column> acol =
				tbl.getColumns() ;
		// Expect 4 columns again:
		// - (orig)    _id
		// - (orig)    dargle_string
		// - (orig)    is_dargly
		// - (new)     extension_data
		// - (ignored) Dargle.m_zIgnoreThisField
		// - (ignored) DargleX.m_zIgnoreThisToo
		assertEquals( 4, acol.size() ) ;
		assertNotNull( tbl.getColumn( "dargle_string" ) ) ;
		assertNotNull( tbl.getColumn( "is_dargly" ) ) ;
		assertNotNull( tbl.getColumn( "extension_data" ) ) ;
		for( SQLightable.Reflection.Column col : acol )
			assertEquals( 2, col.getSince() ) ; // even if inherited actual is 1
	}

	protected void verifyFargleXMarshalling( InheritanceDBSpec dbh )
	{
		InheritanceDBSpec.FargleX o1 = new InheritanceDBSpec.FargleX(
				1, "fargle the first", 1001, "this fargle is longer" ) ;
		dbh.insert( o1 ) ;
		InheritanceDBSpec.FargleX o2 =
				dbh.search( InheritanceDBSpec.FargleX.class, "1" ) ;
		assertNotNull( o2 ) ;
		assertTrue( o1.equals(o2) ) ; // verifies non-extension fields only
		assertEquals( o1.getExtension(), o2.getExtension() ) ;
	}

	protected void verifyDargleXMarshalling( InheritanceDBSpec dbh )
	{
		InheritanceDBSpec.DargleX o1 = new InheritanceDBSpec.DargleX(
				"dargle the first", true, 111, "more darglier", 111111 ) ;
		dbh.insert( o1 ) ;
		InheritanceDBSpec.DargleX o2 =
				dbh.search( InheritanceDBSpec.DargleX.class, "dargle the first" ) ;
		assertNotNull( o2 ) ;
		// Verify that all the schematic members were retrieved properly.
		assertEquals( o1.getString(), o2.getString() ) ;
		assertEquals( o1.isDargly(), o2.isDargly() ) ;
		assertEquals( o1.getExtension(), o2.getExtension() ) ;
		// Verify that all the ignored fields were ignored properly.
		assertEquals( 111, o1.getIgnored() ) ;
		assertEquals( -1, o2.getIgnored() ) ;
		assertEquals( 111111, o1.getIgnored2() ) ;
		assertEquals( -2, o2.getIgnored2() ) ;
	}
}
