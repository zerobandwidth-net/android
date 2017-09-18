package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;

/**
 * A class which can send requests to, and receive notifications from, a
 * {@link SQLiteHouseKeeper}.
 *
 * This class fills the role of a {@link ContentResolver} without implementing
 * that class's API, since the prototypes of the {@code ContentResolver}'s
 * methods don't fit with the workflow of a {@code SQLiteHouse}.
 *
 * If an app uses an implementation of {@link SQLiteHouse} to marshal data
 * to/from a SQLite database, then it should provide a library which includes
 * the schematic classes and an implementation of this class. Since the
 * implementation class is expected to be provided in a library that is separate
 * from the {@code SQLiteHouse} implementation, its declaration <i>must not</i>
 * depend on the ability to import that implementation (<i>e.g.</i> as part of
 * a generic template parameter on the class).
 *
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public class SQLiteHouseRelay
extends BroadcastReceiver
{
/// Static constants ///////////////////////////////////////////////////////////

	public static final String LOG_TAG = SQLiteHouseRelay.class.getSimpleName();

/// Member fields //////////////////////////////////////////////////////////////

	/** The context in which the relay will operate. */
	protected Context m_ctx = null ;

	/** A reference for the contract under which the relay is registered. */
	protected SQLiteHouseSignalAPI m_api = null ;

/// Constructors and initializers //////////////////////////////////////////////

	/**
	 * Constructs an instance, but does not register it.
	 * @param ctx the context in which the relay will operate
	 */
	public SQLiteHouseRelay( Context ctx )
	{
		m_ctx = ctx ;
	}

	/**
	 * Registers the relay instance as a {@link BroadcastReceiver} in its
	 * context.
	 * @param sigs the signal contract between the keeper and the relay; if
	 *             {@code null}, then the relay will be unregistered instead
	 * @return (fluid)
	 */
	public SQLiteHouseRelay register( SQLiteHouseSignalAPI sigs )
	{
		m_api = sigs ;
		if( sigs == null )
			m_ctx.unregisterReceiver(this) ;
		else
			m_ctx.registerReceiver( this, sigs.getRelayIntentFilter() ) ;
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

		String sActionToken = m_api.getTokenFromRelayAction(sAction) ;

		switch( sActionToken )
		{
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT:
				this.onRowInserted( sig ) ;
				break ;
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT_FAILED:
				this.onInsertFailed( sig ) ;
				break ;
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE:
				// TODO notify of record updates
				break ;
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED:
				// TODO notify of update failure
				break ;
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE:
				// TODO notify of record deletions
				break ;
			case SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE_FAILED:
				// TODO notify of deletion failure
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
	 * Handles a signal from the keeper that a row was inserted.
	 * @param sig the received signal
	 */
	protected synchronized void onRowInserted( Intent sig )
	{
		final String sExtraClass = m_api.getExtraSchemaClassName() ;
		final String sExtraRowID = m_api.getExtraInsertedRowID() ;
		if( sig.hasExtra( sExtraClass ) && sig.hasExtra( sExtraRowID ) )
		{ // Notify anything that cares about the insertion.
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Row ID [" )
					.append( sig.getLongExtra( sExtraRowID, INSERT_FAILED ) )
					.append( "] of type [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "] inserted into the database." )
					.toString()
				);
		}
		else
		{ Log.i( LOG_TAG, "A row has been inserted into the database." ) ; }
	}

	/**
	 * Handles a signal from the keeper that a row insertion has failed.
	 * @param sig the received signal
	 */
	protected synchronized void onInsertFailed( Intent sig )
	{
		final String sExtraClass = m_api.getExtraSchemaClassName() ;
		if( sig.hasExtra( sExtraClass ) )
		{ // Notify anything that cares that the insertion failed.
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Keeper failed to insert a row of type [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "]." )
					.toString()
				);
		}
		else
			Log.e( LOG_TAG, "Keeper failed to insert a row." ) ;
	}

/// Broadcasts to SQLiteHouseKeeper ////////////////////////////////////////////

	/**
	 * Requests insertion of a schematic class instance into the keeper's
	 * database.
	 * @param o an instance of the schematic class
	 * @param <SC> the schematic class
	 * @return (fluid)
	 */
	public <SC extends SQLightable> SQLiteHouseRelay insert( SC o )
	{
		Intent sig = new Intent( m_api.getFormattedKeeperAction(
				SQLiteHouseSignalAPI.KEEPER_INSERT ) ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(o) ;
		sig.putExtra( m_api.getExtraSchemaClassName(),
				tbl.getTableClass().getCanonicalName() ) ;
		sig.putExtra( m_api.getExtraSchemaDataName(), tbl.toBundle(o) ) ;
		m_ctx.sendBroadcast( sig ) ;
		return this ;
	}

/// Other instance methods /////////////////////////////////////////////////////

}
