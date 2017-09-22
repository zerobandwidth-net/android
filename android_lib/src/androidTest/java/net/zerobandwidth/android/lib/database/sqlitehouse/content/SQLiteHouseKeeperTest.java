package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.BorkBorkBork;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Sparkle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.ValidSpecClass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.UPDATE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_UPDATE;

/**
 * Exercises {@link SQLiteHouseKeeper}.
 * @since zerobandwidth-net/android (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseKeeperTest
{
	protected Context m_ctx = null ;
	protected ValidSpecClass m_house = null ;
	protected SQLiteHouseKeeper<ValidSpecClass> m_keeper = null ;
	protected ValidTestSchemaAPI m_api = new ValidTestSchemaAPI() ;

	@Before
	public void setup()
	{
		m_house = ValidSpecClass.getTestInstance() ;
		m_ctx = m_house.getContext() ;
		m_keeper = new SQLiteHouseKeeper<>(
				m_ctx, ValidSpecClass.class, m_house ) ;
	}

	@After
	public void teardown()
	{
		m_keeper.unregister() ;
		m_house.close() ;
		SQLiteHouseTest.delete( ValidSpecClass.class ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseKeeper.DefaultSignals} and the shorthand
	 * {@link SQLiteHouseKeeper#register()} method.
	 */
	@Test
	public void testDefaultSignalAPI()
	{
		m_keeper.register() ;
		assertEquals( "net.zerobandwidth.android.lib.database.sqlitehouse.testschema.ValidSpecClass",
				m_keeper.m_api.getIntentDomain() ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseKeeper#register(SQLiteHouseSignalAPI)}
	 * without testing whether it can actually receive.
	 */
	@Test
	public void testRegister()
	{
		m_keeper.register(m_api) ;
		assertEquals( m_api, m_keeper.m_api ) ;
		assertEquals( "org.totallyfake.unittest",
				m_keeper.m_api.getIntentDomain() ) ;
		m_keeper.register(null) ;
		assertNull( m_keeper.m_api ) ;
		m_keeper.register().unregister() ;
		assertNull( m_keeper.m_api ) ;
	}

	/**
	 * Exercises the negative conditions that cause
	 * {@link SQLiteHouseKeeper#onReceive} to return trivially.
	 */
	@Test
	public void testOnReceiveNeg()
	{
		Intent sig = new Intent() ;
		m_keeper.onReceive( m_ctx, sig ) ;            // kicker: sAction == null
		sig.setAction( "" ) ;
		m_keeper.onReceive( m_ctx, sig ) ;          // kicker: sAction.isEmpty()
		sig.setAction( "foo" ) ;
		m_keeper.onReceive( m_ctx, sig ) ;              // kicker: m_api == null
		m_keeper.register(m_api) ;
		m_keeper.onReceive( m_ctx, sig ) ;     // kicker: falls through switch()
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseKeeper#insert} via
	 * {@link SQLiteHouseKeeper#onReceive}.
	 */
	@Test
	public void testInsert()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo(m_house) ;
		m_keeper.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedKeeperAction( KEEPER_INSERT ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Fargle.class.getCanonicalName() ) ;
		Fargle fargleOrig = new Fargle( -1, "Fargle:testInsert()", 9 ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect(Fargle.class).toBundle(fargleOrig) ) ;
		m_keeper.onReceive( m_ctx, sig ) ;

		Cursor crs = m_house.selectFrom(Fargle.class).allColumns().execute() ;
		assertEquals( 1, crs.getCount() ) ;
		crs.moveToFirst() ;
		Fargle fargleFound = m_api.m_mapReflections.get(Fargle.class)
							.fromCursor(crs) ;
		assertTrue( fargleFound.matches(fargleOrig) ) ;
	}

	/**
	 * Exercises conditions in {@link SQLiteHouseKeeper#insert} that would cause
	 * it to exit trivially or with failure conditions.
	 */
	@Test
	public void testInsertNeg()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo(m_house) ;
		m_keeper.m_api = m_api ;                 // but don't bother registering
		Intent sig = new Intent() ;

		long nRowID = m_keeper.insert(sig) ;       // kicker: no class in intent
		assertEquals( INSERT_FAILED, nRowID ) ;

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				BorkBorkBork.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				new Bundle() ) ;
		nRowID = m_keeper.insert(sig) ;     // kicker: getDataFromBundle() fails
		assertEquals( INSERT_FAILED, nRowID ) ;

		sig = new Intent() ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Sparkle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect( Sparkle.class ).toBundle(
						new Sparkle( "0o0o0o pretty!" ) ) ) ;
		nRowID = m_keeper.insert(sig) ; // kicker: Sparkle not in ValidSpecClass
		assertEquals( INSERT_FAILED, nRowID ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseKeeper#update} via
	 * {@link SQLiteHouseKeeper#onReceive(Context, Intent)}.
	 */
	@Test
	public void testUpdate()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo( m_house ) ;
		m_keeper.register(m_api) ;
		Fargle fargle = new Fargle( -1, "Fargle:testUpdate()", 21 ) ;
		long nRowID = m_house.insert(fargle) ;
		fargle.setString( "Fargle:testUpdate() UPDATED" ) ;
		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedKeeperAction( KEEPER_UPDATE ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Fargle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect(Fargle.class).toBundle(fargle) ) ;
		m_keeper.onReceive( m_ctx, sig ) ;

		Fargle fargleFound = m_house.select( Fargle.class, nRowID ) ;
		assertNotNull(fargleFound) ;
		assertTrue( fargle.equals(fargleFound) ) ;
		assertEquals( "Fargle:testUpdate() UPDATED", fargleFound.getString() ) ;
	}

	/**
	 * Exercises conditions in {@link SQLiteHouseKeeper#update} that would cause
	 * it to exit trivially or with failure conditions.
	 */
	@Test
	public void testUpdateNeg()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo( m_house ) ;
		m_keeper.m_api = m_api ;                 // but don't bother registering
		Intent sig = new Intent() ;

		int nCount = m_keeper.update(sig) ;        // kicker: no class in intent
		assertEquals( UPDATE_FAILED, nCount ) ;

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				BorkBorkBork.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				new Bundle() ) ;
		nCount = m_keeper.update(sig) ;     // kicker: getDataFromBundle() fails
		assertEquals( UPDATE_FAILED, nCount ) ;

		sig = new Intent() ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Sparkle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect( Sparkle.class ).toBundle(
						new Sparkle( "Destined for failure!" ) ) ) ;
		nCount = m_keeper.update(sig) ; // kicker: Sparkle not in ValidSpecClass
		assertEquals( UPDATE_FAILED, nCount ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseKeeper#delete} via
	 * {@link SQLiteHouseKeeper#onReceive}.
	 */
	@Test
	public void testDelete()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo( m_house ) ;
		m_keeper.register(m_api) ;
		Fargle oOne = new Fargle( 1, "Fargle:testDelete(1)", 41 ) ;
		long nRowID = m_house.insert(oOne) ;
		Fargle oTwo = new Fargle( 2, "Fargle:testDelete(2)", 42 ) ;
		m_house.insert(oTwo) ;

		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedKeeperAction( KEEPER_DELETE ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Fargle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect(Fargle.class).toBundle(oOne) ) ;
		m_keeper.onReceive( m_ctx, sig ) ;

		Fargle oFound = m_house.select( Fargle.class, nRowID ) ;
		assertNull( oFound ) ;
		oFound = m_house.search(oOne) ;
		assertNull( oFound ) ;
	}

	/**
	 * Exercises conditions in {@link SQLiteHouseKeeper#delete} that would cause
	 * it to exit trivially or with failure conditions.
	 */
	@Test
	public void testDeleteNeg()
	throws Exception // Uncaught exception implies failure.
	{
		SQLiteHouseTest.connectTo( m_house ) ;
		m_keeper.m_api = m_api ;                 // but don't bother registering
		Intent sig = new Intent() ;

		int nCount = m_keeper.delete(sig) ;        // kicker: no class in intent
		assertEquals( DELETE_FAILED, nCount ) ;

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				BorkBorkBork.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				new Bundle() ) ;
		nCount = m_keeper.delete(sig) ;     // kicker: getDataFromBundle() fails
		assertEquals( DELETE_FAILED, nCount ) ;

		sig = new Intent() ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Sparkle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect( Sparkle.class ).toBundle(
						new Sparkle( "Can't delete what isn't there!" ) ) ) ;
		nCount = m_keeper.delete(sig) ; // kicker: Sparkle not in ValidSpecClass
		assertEquals( DELETE_FAILED, nCount ) ;
	}
}
