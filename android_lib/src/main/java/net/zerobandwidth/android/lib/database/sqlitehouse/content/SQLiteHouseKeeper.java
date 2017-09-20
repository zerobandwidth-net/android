package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.zerobandwidth.android.lib.content.ContentUtils;
import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.UPDATE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_INSERT_ROW_ID;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_MODIFY_ROW_COUNT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_SELECT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED;

/**
 * A class which is bound to a {@link SQLiteHouse} implementation and receives
 * intents that request queries from the underlying database.
 *
 * This class fills the role of a {@link ContentProvider} without implementing
 * that class's API, since the prototypes of the {@code ContentProvider}'s
 * methods don't fit with the workflow of a {@code SQLiteHouse}.
 *
 * @param <H> the {@link SQLiteHouse} implementation to which this provider is
 *           bound
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public class SQLiteHouseKeeper<H extends SQLiteHouse>
extends BroadcastReceiver
{
/// Static constants ///////////////////////////////////////////////////////////

	public static final String LOG_TAG =
			SQLiteHouseKeeper.class.getSimpleName() ;

/// Static methods /////////////////////////////////////////////////////////////

/// Inner instance classes /////////////////////////////////////////////////////

	/**
	 * A default implementation of {@link SQLiteHouseSignalAPI} for this keeper
	 * class. The "domain" string is defaulted to the canonical name of the
	 * {@link SQLiteHouse} implementation to which the keeper is bound.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public class DefaultSignals
	extends SQLiteHouseSignalAPI
	{
		protected String getIntentDomain()
		{ return SQLiteHouseKeeper.this.m_dbh.getClass().getCanonicalName() ; }
	}

/// Member fields //////////////////////////////////////////////////////////////

	/** The context in which the keeper will operate. */
	protected Context m_ctx = null ;

	/** A persistent handle on the {@link SQLiteHouse} implementation class. */
	protected Class<H> m_cls ;

	/** A persistent reference to the database helper instance. */
	protected H m_dbh = null ;

	/** A reference for the contract under which the keeper was registered. */
	protected SQLiteHouseSignalAPI m_api = null ;

/// Constructors and initializers //////////////////////////////////////////////

	/**
	 * Constructs an instance, but does not register it.
	 * @param ctx the context in which the keeper will operate
	 * @param cls the {@link SQLiteHouse} implementation class
	 * @param dbh the {@link SQLiteHouse} implementation instance to which the
	 *            keeper is bound
	 */
	public SQLiteHouseKeeper( Context ctx, Class<H> cls, H dbh )
	{
		m_ctx = ctx ;
		m_cls = cls ;
		m_dbh = dbh ;
		m_api = null ;
	}

/// Receiver registration //////////////////////////////////////////////////////

	/**
	 * Registers the keeper instance as a {@link BroadcastReceiver} in its
	 * context, using the canonical name of the underlying {@link SQLiteHouse}
	 * implementation class as the contractual "authority".
	 * @return (fluid)
	 * @see DefaultSignals
	 * @see SQLiteHouseSignalAPI
	 */
	public SQLiteHouseKeeper<H> register()
	{ return this.register( new DefaultSignals() ) ; }

	/**
	 * Registers the keeper instance as a {@link BroadcastReceiver} in its
	 * context.
	 * @param api the signal contract between the keeper and its relays; if
	 *             {@code null}, then the keeper will be unregistered instead!
	 * @return (fluid)
	 */
	public SQLiteHouseKeeper<H> register( SQLiteHouseSignalAPI api )
	{
		m_api = api ;
		if( api == null )
			this.unregister() ;
		else
			m_ctx.registerReceiver( this, api.getKeeperIntentFilter() ) ;
		return this ;
	}

	/**
	 * Unregisters the keeper in its context.
	 * @return (fluid)
	 */
	public SQLiteHouseKeeper<H> unregister()
	{
		ContentUtils.unregister( m_ctx, this ) ;
		m_api = null ;
		return this ;
	}

/// android.content.BroadcastReceiver //////////////////////////////////////////

	@Override
	public final void onReceive( Context ctx, Intent sig )
	{
		String sAction = IntentUtils.discoverAction(sig) ;

		if( sAction == null || sAction.isEmpty() )
		{
			Log.i( LOG_TAG, "Ignoring request with empty action token." ) ;
			return ;
		}

		if( m_api == null )
		{
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "No signals are registered! Ignoring action [" )
					.append( sAction )
					.append( "]." )
					.toString()
				);
			return ;
		}

		String sActionToken = m_api.getTokenFromKeeperAction(sAction) ;

		switch( sActionToken )
		{
			case KEEPER_INSERT:
				this.insert(sig) ;
				break ;
			case KEEPER_SELECT:
				// TODO Select the records requested in the action.
				break ;
			case KEEPER_UPDATE:
				this.update(sig) ;
				break ;
			case KEEPER_DELETE:
				// TODO Delete the records specified in the action.
				break ;
			default:
				this.handleCustomAction( ctx, sig, sActionToken ) ;
		}
	}

/// Action handlers ////////////////////////////////////////////////////////////

	/**
	 * Override this method to handle custom actions not covered by the standard
	 * set defined in {@link SQLiteHouseSignalAPI}. The default implementation
	 * writes an information log stating that the action is unrecognized.
	 * @param ctx the context from which the signal originated
	 * @param sig the received signal
	 * @param sToken the action token parsed from the signal
	 */
	@SuppressWarnings("UnusedParameters") // default intentionally ignores
	protected void handleCustomAction( Context ctx, Intent sig, String sToken )
	{
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Ignoring unrecognized action [" )
				.append( sToken )
				.append( "]." )
				.toString()
			);
	}

	/**
	 * Handles a request to insert data into the underlying database.
	 * @param sig the received signal
	 * @param <SC> the schematic class that is discovered along the way
	 * @return the inserted row ID
	 */
	protected synchronized <SC extends SQLightable> long insert( Intent sig )
	{
		long nRowID ;
		Class<SC> cls = null ;
		SC o = null ;
		try
		{
			cls = m_api.getClassFromExtra(sig) ;
			o = m_api.getDataFromBundle( sig, cls ) ;
			nRowID = m_dbh.insert(o) ;
		}
		catch( SQLiteContentException xContent )
		{
			Log.e( LOG_TAG, "Malformed intent received by insert().",
					xContent ) ;
			nRowID = INSERT_FAILED ;
		}
		catch( IntrospectionException | SchematicException xSchema )
		{
			Log.e( LOG_TAG, "Failed to insert an object.", xSchema ) ;
			nRowID = INSERT_FAILED ;
		}

		if( nRowID != INSERT_FAILED )
			this.notifyInsert( nRowID, cls, o ) ;
		else if( cls != null )
			this.notifyInsertFailed( cls.getCanonicalName() ) ;
		else
			this.notifyInsertFailed( null ) ;

		return nRowID ;
	}

	/**
	 * Handles a request to update a record in the underlying database.
	 * @param sig the received signal
	 * @param <SC> the schematic class of the row to be updated
	 * @return the number of updated rows (probably 0, 1, or -1)
	 */
	protected synchronized <SC extends SQLightable> int update( Intent sig )
	{
		int nRowsUpdated ;
		Class<SC> cls = null ;
		try
		{
			cls = m_api.getClassFromExtra(sig) ;
			SC o = m_api.getDataFromBundle( sig, cls ) ;
			nRowsUpdated = m_dbh.update(o) ;
		}
		catch( SQLiteContentException xContent )
		{
			Log.e( LOG_TAG, "Malformed intent received by update().",
					xContent ) ;
			nRowsUpdated = UPDATE_FAILED ;
		}
		catch( IntrospectionException | SchematicException xSchema )
		{
			Log.e( LOG_TAG, "Failed to update an object.", xSchema ) ;
			nRowsUpdated = UPDATE_FAILED ;
		}

		if( nRowsUpdated != UPDATE_FAILED )
			this.notifyUpdate( cls, nRowsUpdated ) ;
		else if( cls != null )
			this.notifyUpdateFailed( cls.getCanonicalName() ) ;
		else
			this.notifyUpdateFailed( null ) ;

		return nRowsUpdated ;
	}

/// Broadcasts to SQLiteHouseRelay /////////////////////////////////////////////

	/**
	 * Notifies the relay that an insertion succeeded.
	 * @param cls the schematic class of the inserted data
	 * @param nRowID the ID of the inserted row
	 * @param <SC> the schematic class of the inserted data
	 */
	protected synchronized <SC extends SQLightable> void notifyInsert(
			long nRowID, Class<SC> cls, SC row )
	{
		Intent sig = new Intent( m_api.getFormattedRelayAction(
				RELAY_NOTIFY_INSERT ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_INSERT_ROW_ID ),
				nRowID ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				cls.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				m_api.reflect(cls).toBundle(row) ) ;
		m_ctx.sendBroadcast( sig ) ;
	}

	/**
	 * Notifies the relay that an insertion failed.
	 * @param sClass the name of the class that would have been inserted
	 */
	protected synchronized void notifyInsertFailed( String sClass )
	{
		Intent sig = new Intent( m_api.getFormattedRelayAction(
				RELAY_NOTIFY_INSERT_FAILED ) ) ;
		if( sClass != null )
		{
			sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
					sClass ) ;
		}
		m_ctx.sendBroadcast( sig ) ;
	}

	/**
	 * Notifies the relay that an update operation succeeded.
	 * @param cls the schematic class of the updated data
	 * @param nRows the number of rows that were updated
	 * @param <SC> the schematic class of the updated data
	 */
	protected synchronized <SC extends SQLightable> void notifyUpdate(
			Class<SC> cls, int nRows )
	{
		Intent sig = new Intent( m_api.getFormattedRelayAction(
				RELAY_NOTIFY_UPDATE ) ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				cls.getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_MODIFY_ROW_COUNT ),
				nRows ) ;
		m_ctx.sendBroadcast( sig ) ;
	}

	/**
	 * Notifies the relay that an update failed.
	 * @param sClass the name of the class that would have been updated
	 */
	protected synchronized void notifyUpdateFailed( String sClass )
	{
		Intent sig = new Intent( m_api.getFormattedRelayAction(
				RELAY_NOTIFY_UPDATE_FAILED ) ) ;
		if( sClass != null )
		{
			sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
					sClass ) ;
		}
		m_ctx.sendBroadcast( sig ) ;
	}

/// Other accessors and mutators ///////////////////////////////////////////////

}
