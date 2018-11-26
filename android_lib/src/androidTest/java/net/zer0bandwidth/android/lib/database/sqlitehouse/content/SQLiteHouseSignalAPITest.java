package net.zer0bandwidth.android.lib.database.sqlitehouse.content;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.testschema.Fargle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.DEFAULT_EXTRA_TAG_FORMAT;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.DEFAULT_KEEPER_ACTION_FORMAT;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.DEFAULT_RELAY_ACTION_FORMAT;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_ACTIONS;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_ACTIONS;
import static net.zer0bandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT;

/**
 * Exercises {@link SQLiteHouseSignalAPI}.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
 */
@RunWith( AndroidJUnit4.class )
public class SQLiteHouseSignalAPITest
{
	protected static final String[] MORE_ACTIONS = { "foo", "bar", "baz" } ;

	protected ValidTestSchemaAPI m_api ;

	/** Recycles the instance of the test API class for each test. */
	@Before
	public void setup()
	{
		m_api = new ValidTestSchemaAPI() ;
	}

	/**
	 * Exercises {@link SQLiteHouseSignalAPI#getKeeperActions} and
	 * {@link SQLiteHouseSignalAPI#setKeeperActions}.
	 */
	@Test
	public void testGetSetKeeperActions()
	{
		assertEquals( KEEPER_ACTIONS, m_api.getKeeperActions() ) ;
		m_api.setKeeperActions( MORE_ACTIONS ) ;
		assertEquals( 3, m_api.m_asCustomKeeperActions.length ) ;
		assertEquals( KEEPER_ACTIONS.length + MORE_ACTIONS.length,
				m_api.getKeeperActions().length ) ;
		m_api.setKeeperActions( null ) ;
		assertEquals( KEEPER_ACTIONS, m_api.getKeeperActions() ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseSignalAPI#getKeeperActionFormat},
	 * {@link SQLiteHouseSignalAPI#setKeeperActionFormat},
	 * {@link SQLiteHouseSignalAPI#getFormattedKeeperAction}, and
	 * {@link SQLiteHouseSignalAPI#getTokenFromKeeperAction}.
	 */
	@Test
	public void testKeeperActionFormatting()
	{
		assertEquals( DEFAULT_KEEPER_ACTION_FORMAT,
				m_api.getKeeperActionFormat() ) ;
		assertEquals( "org.totallyfake.unittest.keeper.action.INSERT",
				m_api.getFormattedKeeperAction( KEEPER_INSERT ) ) ;
		m_api.setKeeperActionFormat( "%s.foo.%s" ) ;
		String sFormattedInsertAction =
				m_api.getFormattedKeeperAction( KEEPER_INSERT ) ;
		assertEquals( "org.totallyfake.unittest.foo.INSERT",
				sFormattedInsertAction ) ;
		assertEquals( KEEPER_INSERT,
				m_api.getTokenFromKeeperAction( sFormattedInsertAction ) ) ;
		m_api.setKeeperActionFormat( null ) ;
		assertEquals( DEFAULT_KEEPER_ACTION_FORMAT,
				m_api.getKeeperActionFormat() ) ;
		m_api.setKeeperActionFormat( "" ) ;
		assertEquals( DEFAULT_KEEPER_ACTION_FORMAT,
				m_api.getKeeperActionFormat() ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getKeeperIntentFilter}. */
	@Test
	public void testGetKeeperIntentFilter()
	{
		IntentFilter filter = m_api.getKeeperIntentFilter() ;
		assertEquals( KEEPER_ACTIONS.length, filter.countActions() ) ;
		for( String sAction : KEEPER_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedKeeperAction(sAction) ) ) ;
		}
		m_api.setKeeperActions(MORE_ACTIONS) ;
		filter = m_api.getKeeperIntentFilter() ;
		assertEquals( KEEPER_ACTIONS.length + MORE_ACTIONS.length,
				filter.countActions() ) ;
		for( String sAction : KEEPER_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedKeeperAction(sAction) ) ) ;
		}
		for( String sAction : MORE_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedKeeperAction(sAction) ) ) ;
		}
	}

	/**
	 * Exercises {@link SQLiteHouseSignalAPI#getRelayActions} and
	 * {@link SQLiteHouseSignalAPI#setRelayActions}.
	 */
	@Test
	public void testGetSetRelayActions()
	{
		assertEquals( RELAY_ACTIONS, m_api.getRelayActions() ) ;
		m_api.setRelayActions( MORE_ACTIONS ) ;
		assertEquals( 3, m_api.m_asCustomRelayActions.length ) ;
		assertEquals( RELAY_ACTIONS.length + MORE_ACTIONS.length,
				m_api.getRelayActions().length ) ;
		m_api.setRelayActions( null ) ;
		assertEquals( RELAY_ACTIONS, m_api.getRelayActions() ) ;
	}

	/**
	 * Exercises {@link SQLiteHouseSignalAPI#getRelayActionFormat},
	 * {@link SQLiteHouseSignalAPI#setRelayActionFormat},
	 * {@link SQLiteHouseSignalAPI#getFormattedRelayAction}, and
	 * {@link SQLiteHouseSignalAPI#getTokenFromRelayAction}.
	 */
	@Test
	public void testRelayActionFormatting()
	{
		assertEquals( DEFAULT_RELAY_ACTION_FORMAT,
				m_api.getRelayActionFormat() ) ;
		assertEquals( "org.totallyfake.unittest.relay.action.NOTIFY_INSERT",
				m_api.getFormattedRelayAction( RELAY_NOTIFY_INSERT ) ) ;
		m_api.setRelayActionFormat( "%s.foo.%s" ) ;
		String sFormattedNotifyAction =
				m_api.getFormattedRelayAction( RELAY_NOTIFY_INSERT ) ;
		assertEquals( "org.totallyfake.unittest.foo.NOTIFY_INSERT",
				sFormattedNotifyAction ) ;
		assertEquals( RELAY_NOTIFY_INSERT,
				m_api.getTokenFromRelayAction( sFormattedNotifyAction ) ) ;
		m_api.setRelayActionFormat( null ) ;
		assertEquals( DEFAULT_RELAY_ACTION_FORMAT,
				m_api.getRelayActionFormat() ) ;
		m_api.setRelayActionFormat( "" ) ;
		assertEquals( DEFAULT_RELAY_ACTION_FORMAT,
				m_api.getRelayActionFormat() ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getKeeperIntentFilter}. */
	@Test
	public void testGetRelayIntentFilter()
	{
		IntentFilter filter = m_api.getRelayIntentFilter() ;
		assertEquals( RELAY_ACTIONS.length, filter.countActions() ) ;
		for( String sAction : RELAY_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedRelayAction(sAction) ) ) ;
		}
		m_api.setRelayActions(MORE_ACTIONS) ;
		filter = m_api.getRelayIntentFilter() ;
		assertEquals( RELAY_ACTIONS.length + MORE_ACTIONS.length,
				filter.countActions() ) ;
		for( String sAction : RELAY_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedRelayAction(sAction) ) ) ;
		}
		for( String sAction : MORE_ACTIONS )
		{
			assertTrue( filter.hasAction(
					m_api.getFormattedRelayAction(sAction) ) ) ;
		}
	}

	/**
	 * Exercises {@link SQLiteHouseSignalAPI#getExtraTagFormat()},
	 * {@link SQLiteHouseSignalAPI#setExtraTagFormat(String)}, and
	 * {@link SQLiteHouseSignalAPI#getFormattedExtraTag(String)}.
	 */
	@Test
	public void testExtraFormatter()
	{
		assertEquals( DEFAULT_EXTRA_TAG_FORMAT, m_api.getExtraTagFormat() ) ;
		assertEquals( "org.totallyfake.unittest.extra.CLASS",
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ) ;
		m_api.setExtraTagFormat( "%s.foo.bar.baz.extra.%s" ) ;
		String sFormattedTag =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		assertEquals( "org.totallyfake.unittest.foo.bar.baz.extra.DATA",
				sFormattedTag ) ;
		m_api.setExtraTagFormat( null ) ;
		assertEquals( DEFAULT_EXTRA_TAG_FORMAT, m_api.getExtraTagFormat() ) ;
		m_api.setExtraTagFormat( "" ) ;
		assertEquals( DEFAULT_EXTRA_TAG_FORMAT, m_api.getExtraTagFormat() ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getExtraSchemaClassName}. */
	@Test
	public void testGetExtraSchemaClassName()
	{
		Intent sig = new Intent() ;
		final String sExtraName =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		sig.putExtra( sExtraName, "foo" ) ;
		String sValue = m_api.getExtraSchemaClassName(sig) ;
		assertEquals( "foo", sValue ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getClassFromExtra}. */
	@Test
	public void testGetClassFromExtra()
	{
		Intent sig = new Intent() ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				Fargle.class.getCanonicalName() ) ;
		Class<? extends SQLightable> cls = m_api.getClassFromExtra(sig) ;
		assertNotNull( cls ) ;
		assertEquals( Fargle.class.getCanonicalName(), cls.getCanonicalName() );
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getClassFromExtra} negatively. */
	@Test
	public void testGetClassFromExtraNeg()
	{
		Intent sig = new Intent() ;
		final String sExtraName =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		SQLiteContentException xContent = null ;
		IntrospectionException xIntr = null ;

		try { m_api.getClassFromExtra(sig) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;

		xContent = null ;

		sig.putExtra( sExtraName, (String)null ) ;
		try { m_api.getClassFromExtra(sig) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;

		xContent = null ;
		sig.removeExtra( sExtraName ) ;

		sig.putExtra( sExtraName, "" ) ;
		try { m_api.getClassFromExtra(sig) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;

		sig.removeExtra( sExtraName ) ;
		sig.putExtra( sExtraName, "org.totallyfake.NotARealClass" ) ;
		try { m_api.getClassFromExtra(sig) ; }
		catch( IntrospectionException x ) { xIntr = x ; }
		assertNotNull( xIntr ) ;

		xIntr = null ;
		sig.removeExtra( sExtraName ) ;
		sig.putExtra( sExtraName, "java.lang.String" ) ;
		try { m_api.getClassFromExtra(sig) ; }
		catch( IntrospectionException x ) { xIntr = x ; }
		assertNotNull( xIntr ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getDataFromBundle} positively. */
	@Test
	public void testGetDataFromBundle()
	{
		Intent sig = new Intent() ;
		final String sExtraData =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		final Fargle fargle = new Fargle( 1, "foo", 47 ) ;
		final Bundle bndl =
				SQLightable.Reflection.reflect(Fargle.class).toBundle(fargle) ;
		sig.putExtra( sExtraData, bndl ) ;
		Fargle fargleBack = m_api.getDataFromBundle( sig, Fargle.class ) ;
		assertTrue( fargle.equals(fargleBack) ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#getDataFromBundle} negatively. */
	@Test
	public void testGetDataFromBundleNeg()
	{
		final String sExtraData =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		SQLiteContentException xContent = null ;

		Intent sig = new Intent() ;
		try { m_api.getDataFromBundle( sig, null ) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;

		xContent = null ;
		try { m_api.getDataFromBundle( sig, Fargle.class ) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;

		xContent = null ;
		sig.putExtra( sExtraData, (Bundle)null ) ;
		try { m_api.getDataFromBundle( sig, Fargle.class ) ; }
		catch( SQLiteContentException x ) { xContent = x ; }
		assertNotNull( xContent ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#reflect(Class)}. */
	@Test
	public void testReflectClass()
	{
		assertFalse( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 0, m_api.m_mapReflections.size() ) ;
		assertEquals( Fargle.class,
				m_api.reflect( Fargle.class ).getTableClass() ) ;
		assertTrue( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 1, m_api.m_mapReflections.size() ) ;
		m_api.reflect( Fargle.class ) ;
		assertTrue( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 1, m_api.m_mapReflections.size() ) ;
	}

	/** Exercises {@link SQLiteHouseSignalAPI#reflect(SQLightable)} */
	@Test
	public void testReflectInstance()
	{
		assertFalse( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 0, m_api.m_mapReflections.size() ) ;
		assertEquals( Fargle.class,
				m_api.reflect( new Fargle( 1, "Huh?", -1 ) ).getTableClass() ) ;
		assertTrue( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 1, m_api.m_mapReflections.size() ) ;
		m_api.reflect( new Fargle( 2, "Wait, what?", -2 ) ) ;
		assertTrue( m_api.m_mapReflections.containsKey( Fargle.class ) ) ;
		assertEquals( 1, m_api.m_mapReflections.size() ) ;
	}
}
