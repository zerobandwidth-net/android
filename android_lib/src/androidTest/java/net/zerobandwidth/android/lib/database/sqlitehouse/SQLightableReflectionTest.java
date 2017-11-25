package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.StringLens;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Quargle;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse.MAGIC_ID_COLUMN_NAME;

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

	/**
	 * Verifies reflection algorithms using test schema class {@link Fargle}.
	 */
	@Test
	public void testFieldDiscoveryOnFargle()
	{
		SQLightable.Reflection<Fargle> tblFargle =
				SQLightable.Reflection.reflect( Fargle.class ) ;
		assertEquals( 3, tblFargle.m_mapFields.size() ) ;
		assertEquals( 3, tblFargle.m_mapColNames.size() ) ;
		SQLightable.Reflection.ColumnMap<Fargle> mapFargle =
				tblFargle.getColumnMap() ;
		assertEquals( 3, mapFargle.size() ) ;
		List<SQLightable.Reflection<Fargle>.Column> acolFargle =
				mapFargle.getColumnsAsList() ;
		assertEquals( 3, acolFargle.size() ) ;
		for( SQLightable.Reflection<Fargle>.Column col : acolFargle )
		{ assertNotNull( col.getColAttrs() ) ; }
		assertColumnDefined( acolFargle, 0, "m_nFargleID", "fargle_id" ) ;
		assertColumnDefined( acolFargle, 1, "m_sString", "fargle_string" ) ;
		assertColumnDefined( acolFargle, 2, "m_zInteger", "fargle_num" ) ;
		assertTrue( acolFargle.get(0).isKey() ) ;
	}

	/**
	 * Verifies reflection algorithms using test schema class {@link Dargle}.
	 */
	@Test
	public void testFieldDiscoveryOnDargle()
	{
		SQLightable.Reflection<Dargle> tblDargle =
				SQLightable.Reflection.reflect( Dargle.class ) ;
		assertEquals( 3, tblDargle.m_mapFields.size() ) ;
		assertEquals( 3, tblDargle.m_mapColNames.size() ) ;
		SQLightable.Reflection.ColumnMap<Dargle> mapDargle =
				tblDargle.getColumnMap() ;
		assertEquals( 3, mapDargle.size() ) ;
		List<SQLightable.Reflection<Dargle>.Column> acolDargle =
				mapDargle.getColumnsAsList() ;
		assertEquals( 3, acolDargle.size() ) ;
		for( SQLightable.Reflection<Dargle>.Column col : acolDargle )
		{ assertNotNull( col.getColAttrs() ) ; }
		assertColumnDefined( acolDargle, 0, "m_nRowID", MAGIC_ID_COLUMN_NAME ) ;
		assertColumnDefined( acolDargle, 1, "m_sString", "dargle_string" ) ;
		assertColumnDefined( acolDargle, 2, "m_bBoolean", "is_dargly" ) ;
		assertTrue( acolDargle.get(1).isKey() ) ;
	}

	/**
	 * Verifies reflection algorithms using test schema class {@link Blargh}.
	 */
	@Test
	public void testFieldDiscoveryOnBargle()
	{
		SQLightable.Reflection<Blargh> tblBlargh =
				SQLightable.Reflection.reflect( Blargh.class ) ;
		assertEquals( 1, tblBlargh.m_mapFields.size() ) ;
		assertEquals( 1, tblBlargh.m_mapColNames.size() ) ;
		SQLightable.Reflection.ColumnMap<Blargh> mapBlargh =
				tblBlargh.getColumnMap() ;
		assertEquals( 1, mapBlargh.size() ) ;
		List<SQLightable.Reflection<Blargh>.Column> acolBlargh =
				mapBlargh.getColumnsAsList() ;
		assertEquals( 1, acolBlargh.size() ) ;
		assertNotNull( acolBlargh.get(0).getColAttrs() ) ;
		assertColumnDefined( acolBlargh, 0, "m_sString", "blargh_string" ) ;
	}

	/**
	 * Asserts that a list of {@link SQLightable.Reflection.Column} instances
	 * contains a reflection of the specified field and column at the specified
	 * index.
	 * @param acol a list of column reflections
	 * @param nIndex the index to assert
	 * @param sFieldName the name of the schematic class field to assert
	 * @param sColumnName the name of the table column to assert
	 * @param <T> the schematic class
	 */
	private static <T extends SQLightable> void assertColumnDefined(
			List<SQLightable.Reflection<T>.Column> acol, int nIndex,
			String sFieldName, String sColumnName )
	{
		SQLightable.Reflection<T>.Column col = acol.get(nIndex) ;
		assertEquals( sFieldName, col.getField().getName() ) ;
		assertEquals( sColumnName, col.getName() ) ;
	}

	/**
	 * Verifies table creation algorithms using test schema class
	 * {@link Fargle}.
	 */
	@Test
	public void testTableCreationOnFargle()
	{
		SQLightable.Reflection<Fargle> tblFargle =
				SQLightable.Reflection.reflect( Fargle.class ) ;
		String sFargleExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "fargles" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", fargle_id INTEGER UNIQUE NOT NULL" )
				.append( ", fargle_string TEXT NULL DEFAULT NULL" )
				.append( ", fargle_num INTEGER NULL DEFAULT 42" )
				.append( " )" )
				.toString()
				;
		assertEquals( sFargleExpected, tblFargle.getTableCreationSQL() ) ;
	}

	/**
	 * Verifies table creation algorithms using test schema class
	 * {@link Dargle}.
	 */
	@Test
	public void testTableCreationOnDargle()
	{
		SQLightable.Reflection<Dargle> tblDargle =
				SQLightable.Reflection.reflect( Dargle.class ) ;
		String sDargleExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "dargles" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", dargle_string TEXT UNIQUE NOT NULL" )
				.append( ", is_dargly INTEGER NULL DEFAULT 1" )
				.append( " )" )
				.toString()
				;
		assertEquals( sDargleExpected, tblDargle.getTableCreationSQL() ) ;
	}

	/**
	 * Verifies table creation algorithms using test schema class
	 * {@link Blargh}.
	 */
	@Test
	public void testTableCreationOnBlargh()
	{
		SQLightable.Reflection<Blargh> tblBlargh =
				SQLightable.Reflection.reflect( Blargh.class ) ;
		String sBlarghExpected = (new StringBuilder())
				.append( "CREATE TABLE IF NOT EXISTS " )
				.append( "blargh" )
				.append( " ( _id INTEGER PRIMARY KEY AUTOINCREMENT" )
				.append( ", blargh_string TEXT NULL DEFAULT NULL" )
				.append( " )" )
				.toString()
				;
		assertEquals( sBlarghExpected, tblBlargh.getTableCreationSQL() ) ;
	}

	/**
	 * Exercises the various {@link SQLightable.Reflection} accessor methods
	 * using test schema class {@link Fargle}.
	 */
	@Test
	public void testTrivialAccessorsOnFargle()
	{
		SQLightable.Reflection<Fargle> tblFargle =
				SQLightable.Reflection.reflect( Fargle.class ) ;

		// Reflection#getTableClass()
		assertEquals( Fargle.class, tblFargle.getTableClass() ) ;

		// Reflection#getTableAttrs()
		SQLiteTable antFargle = tblFargle.getTableAttrs() ;
		assertEquals( "fargles", antFargle.value() ) ;
		assertEquals( 1, antFargle.since() ) ;

		// Reflection#getTableName()
		assertEquals( "fargles", tblFargle.getTableName() ) ;

		// Reflection#getField(String)
		Field fldFargleID = tblFargle.getField( "fargle_id" ) ;
		assertEquals( "m_nFargleID", fldFargleID.getName() ) ;

		// Reflection#getColumnDef(Field)
		SQLightable.Reflection<Fargle>.Column colFargleID =
				tblFargle.getColumnDef( fldFargleID ) ;
		assertEquals( "fargle_id", colFargleID.getName() ) ;

		// Reflection#getColumnDef(String)
		SQLightable.Reflection<Fargle>.Column colFargleString =
				tblFargle.getColumnDef( "fargle_string" ) ;
		assertEquals( "m_sString", colFargleString.getField().getName() ) ;

		// Reflection#getKeyField()
		Field fldFargleKey = tblFargle.getKeyField() ;
		assertNotNull( fldFargleKey ) ;
		assertEquals( "m_nFargleID", fldFargleKey.getName() ) ;

		// Reflection#getKeyColumn()
		assertEquals( "fargle_id", tblFargle.getKeyColumn().getName() ) ;

		// Reflection#getMagicIDField()
		assertNull( tblFargle.getMagicIDField() ) ;

		// Reflection#getMagicIDColumn()
		assertNull( tblFargle.getMagicIDColumn() ) ;

		// Reflection#getKeyOrMagicIDColumn()
		assertEquals( "fargle_id",
				tblFargle.getKeyOrMagicIDColumn().getName() ) ;

		// Reflection#getFirstSchemaVersion()
		assertEquals( 1, tblFargle.getFirstSchemaVersion() ) ;
	}

	/**
	 * Exercises the various {@link SQLightable.Reflection} accessor methods
	 * using test schema class {@link Dargle}.
	 */
	@Test
	public void testTrivialAccessorsOnDargle()
	{
		SQLightable.Reflection<Dargle> tblDargle =
				SQLightable.Reflection.reflect( Dargle.class ) ;

		// Reflection#getTableClass()
		assertEquals( Dargle.class, tblDargle.getTableClass() ) ;

		// Reflection#getTableAttrs()
		SQLiteTable antDargle = tblDargle.getTableAttrs() ;
		assertEquals( "dargles", antDargle.value() ) ;
		assertEquals( 1, antDargle.since() ) ;

		// Reflection#getTableName()
		assertEquals( "dargles", tblDargle.getTableName() ) ;

		// Reflection#getField(String)
		Field fldDargleString = tblDargle.getField( "dargle_string" ) ;
		assertEquals( "m_sString", fldDargleString.getName() ) ;

		// Reflection#getColumnDef(Field)
		SQLightable.Reflection<Dargle>.Column colDargleString =
				tblDargle.getColumnDef( fldDargleString ) ;
		assertEquals( "dargle_string", colDargleString.getName() ) ;

		// Reflection#getColumnDef(String)
		SQLightable.Reflection<Dargle>.Column colIsDargly =
				tblDargle.getColumnDef( "is_dargly" ) ;
		assertEquals( "m_bBoolean", colIsDargly.getField().getName() ) ;

		// Reflection#getKeyField()
		Field fldDargleKey = tblDargle.getKeyField() ;
		assertNotNull( fldDargleKey ) ;
		assertEquals( "m_sString", fldDargleKey.getName() ) ;

		// Reflection#getKeyColumn()
		assertEquals( "dargle_string", tblDargle.getKeyColumn().getName() ) ;

		// Reflection#getMagicIDField()
		assertEquals( "m_nRowID",  tblDargle.getMagicIDField().getName() ) ;

		// Reflection#getMagicIDColumn()
		assertEquals( MAGIC_ID_COLUMN_NAME,
				tblDargle.getMagicIDColumn().getName() ) ;
		assertEquals( "m_nRowID",
				tblDargle.getMagicIDColumn().getField().getName() ) ;

		// Reflection#getKeyOrMagicIDColumn()
		assertEquals( "dargle_string",
				tblDargle.getKeyOrMagicIDColumn().getName() ) ;

		// Reflection#getFirstSchemaVersion()
		assertEquals( 1, tblDargle.getFirstSchemaVersion() ) ;
	}

	/**
	 * Exercises the various {@link SQLightable.Reflection} accessor methods
	 * using test schema class {@link Blargh}.
	 */
	@Test
	public void testTrivialAccessorsOnBlargh()
	{
		SQLightable.Reflection<Blargh> tblBlargh =
				SQLightable.Reflection.reflect( Blargh.class ) ;

		// Reflection#getTableClass()
		assertEquals( Blargh.class, tblBlargh.getTableClass() ) ;

		// Reflection#getTableAttrs()
		SQLiteTable antBlargh = tblBlargh.getTableAttrs() ;
		assertNull( antBlargh ) ;

		// Reflection#getTableName()
		assertEquals( "blargh", tblBlargh.getTableName() ) ;

		// Reflection#getField(String)
		Field fldBlarghString = tblBlargh.getField( "blargh_string" ) ;
		assertEquals( "m_sString", fldBlarghString.getName() ) ;

		// Reflection#getColumnDef(Field)
		SQLightable.Reflection<Blargh>.Column colBlarghString =
				tblBlargh.getColumnDef( fldBlarghString ) ;
		assertEquals( "blargh_string", colBlarghString.getName() ) ;

		// Reflection#getColumnDef(String)
		SQLightable.Reflection<Blargh>.Column colBlarghStillString =
				tblBlargh.getColumnDef( "blargh_string" ) ;
		assertEquals( "m_sString", colBlarghStillString.getField().getName() ) ;

		// Reflection#getKeyField()
		Field fldBlarghKey = tblBlargh.getKeyField() ;
		assertNull( fldBlarghKey ) ;

		// Reflection#getKeyColumn()
		assertNull( tblBlargh.getKeyColumn() ) ;

		// Reflection#getMagicIDField()
		assertNull( tblBlargh.getMagicIDField() ) ;

		// Reflection#getMagicIDColumn()
		assertNull( tblBlargh.getMagicIDColumn() ) ;

		// Reflection#getKeyOrMagicIDColumn()
		assertNull( tblBlargh.getKeyOrMagicIDColumn() ) ;

		// Reflection#getFirstSchemaVersion()
		assertEquals( 1, tblBlargh.getFirstSchemaVersion() ) ;
	}

	/**
	 * Exercises schema version resolution using test schema class
	 * {@link Quargle}.
	 */
	@Test
	public void testSchemaVersionOnQuargle()
	{
		SQLightable.Reflection<Quargle> tblQuargle =
				SQLightable.Reflection.reflect( Quargle.class ) ;
		assertEquals( 2, tblQuargle.getFirstSchemaVersion() ) ;
	}

	/**
	 * Exercises {@link SQLightable.Reflection#getKeyOrMagicIDColumn()} using
	 * the {@link Quargle} class, which defines only the magic column, but not
	 * a practical key.
	 */
	@Test
	public void testGetKeyOrMagicIDOnQuargle()
	{
		SQLightable.Reflection<Quargle> tblQuargle =
				SQLightable.Reflection.reflect( Quargle.class ) ;
		assertEquals( MAGIC_ID_COLUMN_NAME,
				tblQuargle.getKeyOrMagicIDColumn().getName() ) ;
	}

	/**
	 * Exercises methods that create
	 * {@link net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder}
	 * instances bound to the table name that is discovered by a reflection.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testQueryBuilderGetters()
	{
		SQLightable.Reflection<Quargle> tblQuargle =
				SQLightable.Reflection.reflect( Quargle.class ) ;
		assertTrue( tblQuargle.buildInsert().toString().startsWith(
				"INSERT INTO quargles" ) ) ;
		assertTrue( tblQuargle.buildUpdate().toString().startsWith(
				"UPDATE quargles" ) ) ;
		assertTrue( tblQuargle.buildSelect().toString().startsWith(
				"SELECT * FROM quargles" ) ) ;
		assertTrue( tblQuargle.buildDelete().toString().startsWith(
				"DELETE FROM quargles" ) ) ;
	}
}
