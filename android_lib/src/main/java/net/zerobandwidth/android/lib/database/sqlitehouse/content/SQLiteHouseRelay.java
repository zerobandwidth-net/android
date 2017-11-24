package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.zerobandwidth.android.lib.content.ContentUtils;
import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.UPDATE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_INSERT_ROW_ID;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_MODIFY_ROW_COUNT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_DATA;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.EXTRA_SCHEMA_CLASS_NAME;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.KEEPER_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED;

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
	 * @param api the signal contract between the keeper and the relay; if
	 *             {@code null}, then the relay will be unregistered instead
	 * @return (fluid)
	 */
	public SQLiteHouseRelay register( SQLiteHouseSignalAPI api )
	{
		m_api = api ;
		if( api == null )
			this.unregister() ;
		else
			m_ctx.registerReceiver( this, api.getRelayIntentFilter() ) ;
		return this ;
	}

	/**
	 * Unregisters the relay in its context.
	 * @return (fluid)
	 */
	public SQLiteHouseRelay unregister()
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

		String sActionToken = m_api.getTokenFromRelayAction(sAction) ;

		switch( sActionToken )
		{
			case RELAY_NOTIFY_INSERT:
				this.onRowInserted( sig ) ;
				break ;
			case RELAY_NOTIFY_INSERT_FAILED:
				this.onInsertFailed( sig ) ;
				break ;
			case RELAY_NOTIFY_UPDATE:
				this.onRowsUpdated( sig ) ;
				break ;
			case RELAY_NOTIFY_UPDATE_FAILED:
				this.onUpdateFailed( sig ) ;
				break ;
			case RELAY_NOTIFY_DELETE:
				this.onRowsDeleted( sig ) ;
				break ;
			case RELAY_NOTIFY_DELETE_FAILED:
				this.onDeleteFailed( sig ) ;
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
	 * Handles a signal from the keeper that a row was inserted.
	 * @param sig the received signal
	 */
	protected synchronized void onRowInserted( Intent sig )
	{
		final String sExtraRowID =
				m_api.getFormattedExtraTag( EXTRA_INSERT_ROW_ID ) ;
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
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
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
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


	/**
	 * Handles a signal from the keeper that some rows were updated.
	 * @param sig the received signal
	 */
	protected synchronized void onRowsUpdated( Intent sig )
	{
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		final String sExtraRowCount =
				m_api.getFormattedExtraTag( EXTRA_MODIFY_ROW_COUNT ) ;
		if( sig.hasExtra( sExtraClass ) && sig.hasExtra( sExtraRowCount ) )
		{ // Notify anything that cares about the update.
			int nCount = sig.getIntExtra( sExtraRowCount, UPDATE_FAILED ) ;
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Updated [" )
					.append( nCount )
					.append(( nCount == 1 ? "] row" : "] rows" ))
					.append( " in table [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "]." )
					.toString()
				);
		}
		else
		{ Log.i( LOG_TAG, "At least one row was updated." ) ; }
	}

	/**
	 * Handles a signal from the keeper that a table update has failed.
	 * @param sig the received signal
	 */
	protected synchronized void onUpdateFailed( Intent sig )
	{
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		if( sig.hasExtra( sExtraClass ) )
		{ // Notify anything that cares that the update failed.
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Keeper failed to update rows of type [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "]." )
					.toString()
				);
		}
		else
		{ Log.e( LOG_TAG, "Keeper failed to update rows." ) ; }
	}

	/**
	 * Handles a signal from the keeper that some rows were deleted.
	 * @param sig the received signal
	 */
	protected synchronized void onRowsDeleted( Intent sig )
	{
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		final String sExtraRowCount =
				m_api.getFormattedExtraTag( EXTRA_MODIFY_ROW_COUNT ) ;
		if( sig.hasExtra(sExtraClass) && sig.hasExtra(sExtraRowCount) )
		{
			int nCount = sig.getIntExtra( sExtraRowCount, DELETE_FAILED ) ;
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Deleted [" )
					.append( nCount )
					.append(( nCount == 1 ? "] row" : "] rows" ))
					.append( " from table [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "]." )
					.toString()
				);
		}
		else
		{ Log.i( LOG_TAG, "At least one row was deleted." ) ; }
	}

	/**
	 * Handles a signal from the keeper that a row deletion failed.
	 * @param sig the received signal
	 */
	protected synchronized void onDeleteFailed( Intent sig )
	{
		final String sExtraClass =
				m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		if( sig.hasExtra( sExtraClass ) )
		{ // Notify anything that cares that the deletion failed.
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Keeper failed to delete rows of type [" )
					.append( sig.getStringExtra( sExtraClass ) )
					.append( "]." )
					.toString()
				);
		}
		else
		{ Log.e( LOG_TAG, "Keeper failed to delete rows." ) ; }
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
	{ m_ctx.sendBroadcast( this.buildInsertSignal(o) ) ; return this ; }

	/**
	 * Constructs the {@link Intent} to be sent by {@link #insert}.
	 * This is a separate method only so that it can be unit-tested.
	 * @param o an instance of the schematic class to be inserted
	 * @param <SC> the schematic class
	 * @return the intent to be sent by {@link @insert}
	 */
	protected <SC extends SQLightable> Intent buildInsertSignal( SC o )
	{
		Intent sig = new Intent(
				m_api.getFormattedKeeperAction( KEEPER_INSERT ) ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(o) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				tbl.getTableClass().getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				tbl.toBundle(o) ) ;
		return sig ;
	}

	/**
	 * Requests an update of a particular row in the keeper's database,
	 * corresponding to the schematic class instance supplied.
	 * @param o an instance of the schematic class
	 * @param <SC> the schematic class
	 * @return (fluid)
	 */
	public <SC extends SQLightable> SQLiteHouseRelay update( SC o )
	{ m_ctx.sendBroadcast( this.buildUpdateSignal(o) ) ; return this ; }

	/**
	 * Constructs the {@link Intent} to be sent by {@link #update}.
	 * This is a separate method only so that it can be unit-tested.
	 * @param o an instance of the schematic class to be used as update input
	 * @param <SC> the schematic class
	 * @return the intent to be sent by {@link #update}
	 */
	protected <SC extends SQLightable> Intent buildUpdateSignal( SC o )
	{
		Intent sig = new Intent(
				m_api.getFormattedKeeperAction( KEEPER_UPDATE ) ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(o) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				tbl.getTableClass().getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				tbl.toBundle(o) ) ;
		return sig ;
	}

	/**
	 * Requests deletion of a particular row in the keeper's database,
	 * corresponding to the schematic class instance supplied.
	 * @param o an instance of the schematic class
	 * @param <SC> the schematic class
	 * @return (fluid)
	 */
	public <SC extends SQLightable> SQLiteHouseRelay delete( SC o )
	{ m_ctx.sendBroadcast( this.buildDeleteSignal(o) ) ; return this ; }

	/**
	 * Constructs the {@link Intent} to be sent by {@link #delete}.
	 * This is a separate method only so that it can be unit-tested.
	 * @param o an instance of the schematic class to be deleted
	 * @param <SC> the schematic class
	 * @return the intent to be sent by {@link #delete}
	 */
	protected <SC extends SQLightable> Intent buildDeleteSignal( SC o )
	{
		Intent sig = new Intent(
				m_api.getFormattedKeeperAction( KEEPER_DELETE ) ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(o) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				tbl.getTableClass().getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ),
				tbl.toBundle(o) ) ;
		return sig ;
	}

/// Other instance methods /////////////////////////////////////////////////////

}
