package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import net.zerobandwidth.android.lib.content.querybuilder.SelectionBuilder;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Blargh;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Dargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_ORDER_DESC;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_INSERT_ROW_ID;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_RESULT_ROW_COUNT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SELECTION_QUERY_SPEC;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_SELECT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_RECEIVE_SELECTION;

/**
 * Exercises {@link SQLiteHouseRelay}.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseRelayTest
{
	public static final String LOG_TAG =
			SQLiteHouseRelayTest.class.getSimpleName() ;

	protected Context m_ctx = null ;
	protected SQLiteHouseRelay m_relay = null ;
	protected ValidTestSchemaAPI m_api = new ValidTestSchemaAPI() ;

	/**
	 * Used to exercise {@link SQLiteHouseRelay}'s listener management methods.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	private class MockRelayListener
	implements SQLiteHouseRelay.Listener
	{
		@Override
		public void onRowInserted( long nRowID ) {}

		@Override
		public void onInsertFailed()
		{ throw new RuntimeException( "An insertion failed." ) ; }

		@Override
		public void onRowsUpdated( int nCount ) {}

		@Override
		public void onUpdateFailed()
		{ throw new RuntimeException( "An update failed." ) ; }

		@Override
		public void onRowsDeleted( int nCount ) {}

		@Override
		public void onDeleteFailed()
		{ throw new RuntimeException( "A deletion failed." ) ; }

		@Override
		public <SC extends SQLightable> void onRowsSelected(
				Class<SC> cls, int nTotalCount, List<SC> aoRows )
		{
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Successfully processed signal for [" )
					.append( nTotalCount )
					.append(( nTotalCount == 1 ? "] row " : "] rows " ))
					.append( "of type [" )
					.append( cls.getSimpleName() )
					.append( "].")
					.toString()
				);
		}

		@Override
		public void onSelectFailed()
		{ throw new RuntimeException( "A selection failed." ) ; }
	}

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
	 * Exercises the listener management methods of {@link SQLiteHouseRelay}.
	 */
	@Test
	public void testListenerManagement()
	{
		assertNotNull( m_relay.m_vListeners ) ;
		SQLiteHouseRelay.Listener l = new MockRelayListener() ;
		m_relay.addListener(l) ;
		assertEquals( 1, m_relay.m_vListeners.size() ) ;
		assertTrue( m_relay.m_vListeners.contains(l) ) ;
		m_relay.addListener(l) ; // Prove idempotence of method.
		assertEquals( 1, m_relay.m_vListeners.size() ) ;

		m_relay.removeListener(l) ;
		assertNotNull( m_relay.m_vListeners ) ;
		assertEquals( 0, m_relay.m_vListeners.size() ) ;
		m_relay.removeListener(l) ; // Prove idempotence of method.
		assertNotNull( m_relay.m_vListeners ) ;
		assertEquals( 0, m_relay.m_vListeners.size() ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseRelay#buildInsertSignal}, to verify that we
	 * are constructing the {@link Intent} correctly.
	 */
	@Test
	public void testInsertSignal()
	{
		m_relay.register(m_api) ;
		Fargle fargle = new Fargle( 8, "Insert this!", 24 ) ;
		Intent sig = m_relay.buildInsertSignal(fargle) ;
		assertEquals( m_api.getFormattedKeeperAction(KEEPER_INSERT),
				sig.getAction() ) ;
		assertEquals( Fargle.class.getCanonicalName(),
				sig.getStringExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME )
			));
		Fargle fargleFromSig = m_api.reflect(Fargle.class).fromBundle(
				sig.getBundleExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA )
			));
		assertTrue( fargle.equals(fargleFromSig) ) ;
	}

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
	 * Exercises {@link SQLiteHouseRelay#buildUpdateSignal}, to verify that we
	 * are constructing the {@link Intent} correctly.
	 */
	@Test
	public void testUpdateSignal()
	{
		m_relay.register(m_api) ;
		Fargle fargle = new Fargle( 21, "Update this!", 24 ) ;
		Intent sig = m_relay.buildUpdateSignal(fargle) ;
		assertEquals( m_api.getFormattedKeeperAction(KEEPER_UPDATE),
				sig.getAction() ) ;
		assertEquals( Fargle.class.getCanonicalName(),
				sig.getStringExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME )
			));
		Fargle fargleFromSig = m_api.reflect(Fargle.class).fromBundle(
				sig.getBundleExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA )
			));
		assertTrue( fargle.equals(fargleFromSig) ) ;
	}

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
				m_api.getFormattedExtraTag(EXTRA_RESULT_ROW_COUNT), 21 ) ;
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
	 * Exercises {@link SQLiteHouseRelay#buildDeleteSignal}, to verify that we
	 * are constructing the {@link Intent} correctly.
	 */
	@Test
	public void testDeleteSignal()
	{
		m_relay.register(m_api) ;
		Fargle fargle = new Fargle( 4, "Delete this!", 24 ) ;
		Intent sig = m_relay.buildDeleteSignal(fargle) ;
		assertEquals( m_api.getFormattedKeeperAction(KEEPER_DELETE),
				sig.getAction() ) ;
		assertEquals( Fargle.class.getCanonicalName(),
				sig.getStringExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME )
			));
		Fargle fargleFromSig = m_api.reflect(Fargle.class).fromBundle(
				sig.getBundleExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA )
			));
		assertTrue( fargle.equals(fargleFromSig) ) ;
	}

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

		sig.putExtra( m_api.getFormattedExtraTag(EXTRA_RESULT_ROW_COUNT), 4 ) ;
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

	/**
	 * Exercises {@link SQLiteHouseRelay#buildSelectionSignal}, to verify that
	 * we are constructing the {@link Intent} correctly.
	 */
	@Test
	public void testSelectionSignal()
	{
		m_relay.register(m_api) ;
		SelectionBuilder q = new SelectionBuilder()
				.columns( "foo", "bar", "baz" )
				.where( "flargle=?", "1" )
				.orderBy( "blargle", SQL_ORDER_DESC )
				;
		Intent sig = m_relay.buildSelectionSignal( Fargle.class, q ) ;
		assertEquals( m_api.getFormattedKeeperAction( KEEPER_SELECT ),
				sig.getAction() ) ;
		assertEquals( Fargle.class.getCanonicalName(),
				sig.getStringExtra(
						m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME )
			));
		Bundle bndl = sig.getBundleExtra(
				m_api.getFormattedExtraTag( EXTRA_SELECTION_QUERY_SPEC ) ) ;
		String[] asColumns = bndl.getStringArray("columns") ;
		assertNotNull(asColumns) ;
		assertEquals( "foo", asColumns[0] ) ;
		assertEquals( "bar", asColumns[1] ) ;
		assertEquals( "baz", asColumns[2] ) ;
		assertEquals( "flargle=?", bndl.getString("where_format") ) ;
		String[] asWhereArgs = bndl.getStringArray("where_params") ;
		assertNotNull(asWhereArgs) ;
		assertEquals( "1", asWhereArgs[0] ) ;
		String[] asOrderByCols = bndl.getStringArray("order_by_cols") ;
		assertNotNull(asOrderByCols) ;
		assertEquals( "blargle", asOrderByCols[0] ) ;
		String[] asOrderByDirs = bndl.getStringArray("order_by_dirs") ;
		assertNotNull(asOrderByDirs) ;
		assertEquals( SQL_ORDER_DESC, asOrderByDirs[0] ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseRelay#onRowsSelected(Intent)} via
	 * {@link SQLiteHouseRelay#onReceive}.
	 */
	@Test
	public void testOnRowsSelected()
	{
		RuntimeException xFailed = null ;
		m_relay.register(m_api) ;
		m_relay.addListener( new MockRelayListener() ) ;
		Intent sig = new Intent() ;

		// Test a signal that has none of the extra data in it. (expect fail)
		Log.i( LOG_TAG, "Bogus signal test 1/3 (expect an exception)" ) ;
		sig.setAction( m_api.getFormattedRelayAction(RELAY_RECEIVE_SELECTION) );
		try { m_relay.onReceive( m_ctx, sig ) ; }
		catch( RuntimeException x ) { xFailed = x ; }
		assertNotNull(xFailed) ;
		xFailed = null ;

		// Test a signal that has a row count but no data. (expect fail)
		Log.i( LOG_TAG, "Bogus signal test 2/3 (expect an exception)" ) ;
		sig.putExtra( m_api.getFormattedExtraTag(EXTRA_RESULT_ROW_COUNT), 3 ) ;
		try { m_relay.onReceive( m_ctx, sig ) ; }
		catch( RuntimeException x ) { xFailed = x ; }
		assertNotNull(xFailed) ;
		xFailed = null ;

		// Test a signal with a class name but still no data. (expect fail)
		Log.i( LOG_TAG, "Bogus signal test 3/3 (expect an exception)" ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Fargle.class.getCanonicalName() ) ;
		try { m_relay.onReceive( m_ctx, sig ) ; }
		catch( RuntimeException x ) { xFailed = x ; }
		assertNotNull(xFailed) ;
		xFailed = null ;

		// Build out some rows, add them to the signal, and test. (expect pass)
		Log.i( LOG_TAG, "Real signal test (expect successful processing)" ) ;
		ArrayList<Bundle> bndlRows = new ArrayList<>(3) ;
		SQLightable.Reflection<Fargle> tbl = m_api.reflect(Fargle.class) ;
		bndlRows.add( tbl.toBundle( new Fargle( 1, "foo", 19 ) ) ) ;
		bndlRows.add( tbl.toBundle( new Fargle( 2, "bar", 19 ) ) ) ;
		bndlRows.add( tbl.toBundle( new Fargle( 3, "baz", 19 ) ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				bndlRows.toArray( new Parcelable[3] ) ) ;
		m_relay.onReceive( m_ctx, sig ) ;          // Gets processed and logged.
	} // Complete execution implies success.


}
