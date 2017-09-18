package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
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
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;

/**
 * Exercises {@link SQLiteHouseKeeper}.
 * @since zerobandwidth-net/android (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseKeeperTest
{
	protected static Context getContext()
	{ return InstrumentationRegistry.getTargetContext() ; }

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
	} // Complete execution equals success.

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
		sig.putExtra( m_api.getExtraSchemaClassName(),
				Fargle.class.getCanonicalName() ) ;
		Fargle fargleOrig = new Fargle( -1, "Fargle:testInsert()", 1 ) ;
		sig.putExtra( m_api.getExtraSchemaDataName(),
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

		sig.putExtra( m_api.getExtraSchemaClassName(),
				BorkBorkBork.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getExtraSchemaDataName(), new Bundle() ) ;
		nRowID = m_keeper.insert(sig) ;     // kicker: getDataFromBundle() fails
		assertEquals( INSERT_FAILED, nRowID ) ;

		sig = new Intent() ;
		sig.putExtra( m_api.getExtraSchemaClassName(),
				Sparkle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getExtraSchemaDataName(),
				m_api.reflect( Sparkle.class ).toBundle(
						new Sparkle( "0o0o0o pretty!" ) ) ) ;
		nRowID = m_keeper.insert(sig) ; // kicker: Sparkle not in ValidSpecClass
		assertEquals( INSERT_FAILED, nRowID ) ;
	}
}
