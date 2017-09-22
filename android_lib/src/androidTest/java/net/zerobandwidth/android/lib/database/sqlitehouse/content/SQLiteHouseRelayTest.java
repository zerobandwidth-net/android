package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_INSERT_ROW_ID;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_MODIFY_ROW_COUNT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED;

/**
 * Exercises {@link SQLiteHouseRelay}.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseRelayTest
{
	protected Context m_ctx = null ;
	protected SQLiteHouseRelay m_relay = null ;
	protected ValidTestSchemaAPI m_api = new ValidTestSchemaAPI() ;

	@Before
	public void setup()
	{
		m_ctx = InstrumentationRegistry.getTargetContext() ;
		m_relay = new SQLiteHouseRelay( m_ctx ) ;
	}

	@After
	public void teardown()
	{ m_relay.unregister() ; }

	/**
	 * Exercises {@link SQLiteHouseRelay#register}.
	 * There is no corresponding test for {@link SQLiteHouseRelay#unregister},
	 * since that method is hit by {@link #teardown} after every test.
	 */
	@Test
	public void testRegister()
	{
		m_relay.register(m_api) ;
		assertEquals( m_api, m_relay.m_api ) ;
		assertEquals( "org.totallyfake.unittest",
				m_relay.m_api.getIntentDomain() ) ;
		m_relay.register(null) ;
		assertNull( m_relay.m_api ) ;
	}

	/**
	 * Exercises the negative conditions that would cause
	 * {@link SQLiteHouseRelay#onReceive} to return trivially.
	 */
	@Test
	public void testOnReceiveNeg()
	{
		Intent sig = new Intent() ;
		m_relay.onReceive( m_ctx, sig ) ;           // kicker: no action defined
		sig.setAction("") ;
		m_relay.onReceive( m_ctx, sig ) ;       // kicker: action token is empty
		sig.setAction("foo") ;
		m_relay.onReceive( m_ctx, sig ) ;               // kicker: m_api == null
		m_relay.register(m_api) ;
		m_relay.onReceive( m_ctx, sig ) ;      // kicker: falls through switch()
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onRowInserted(Intent)} via
	 * {@link SQLiteHouseRelay#onReceive(Context, Intent)}.
	 */
	@Test
	public void testOnRowInserted()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedRelayAction( RELAY_NOTIFY_INSERT ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;            // Flows through trivially.

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_INSERT_ROW_ID ), 42L ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
					Fargle.class.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect(Fargle.class).toBundle(
						new Fargle( 42, "Fargle!", 90 ) ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onInsertFailed} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnInsertFailed()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction(
				m_api.getFormattedRelayAction( RELAY_NOTIFY_INSERT_FAILED ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;            // Flows through trivially.

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				"flugel" ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onRowsUpdated} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnRowsUpdated()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedRelayAction( RELAY_NOTIFY_UPDATE ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;            // Flows through trivially.

		sig.putExtra(
				m_api.getFormattedExtraTag( EXTRA_MODIFY_ROW_COUNT ), 21 ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Dargle.class.getCanonicalName() ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onUpdateFailed} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnUpdateFailed()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction(
				m_api.getFormattedRelayAction( RELAY_NOTIFY_UPDATE_FAILED ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				"contrabassoon" ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onRowsDeleted(Intent)} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnRowsDeleted()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction( m_api.getFormattedRelayAction( RELAY_NOTIFY_DELETE ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;            // Flows through trivially.

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_MODIFY_ROW_COUNT ), 4 );
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Blargh.class.getCanonicalName() ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.

	/**
	 * Exercises {@link SQLiteHouseRelay#onDeleteFailed} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnDeleteFailed()
	{
		m_relay.register(m_api) ;
		Intent sig = new Intent() ;
		sig.setAction(
				m_api.getFormattedRelayAction( RELAY_NOTIFY_DELETE_FAILED ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;

		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				"ultramaroon" ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.
}
