package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import net.zerobandwidth.android.lib.content.ContentUtils;
import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.content.querybuilder.SelectionBuilder;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.INSERT_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.UPDATE_FAILED;
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
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_SELECT_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_NOTIFY_UPDATE_FAILED;
import static net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseSignalAPI.RELAY_RECEIVE_SELECTION;

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
/// Inner Classes //////////////////////////////////////////////////////////////

	/**
	 * Methods that must be implemented by any class that wants to process the
	 * information received in signals from a {@link SQLiteHouseKeeper}.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public interface Listener
	{
		/**
		 * Called by {@link SQLiteHouseRelay#onRowInserted} to inform the
		 * listener of a successful insertion.
		 * @param nRowID the integer ID of the new database row
		 */
		void onRowInserted( long nRowID ) ;

		/**
		 * Called by {@link SQLiteHouseRelay#onInsertFailed} to inform the
		 * listener of a failed insertion.
		 */
		void onInsertFailed() ;

		/**
		 * Called by {@link SQLiteHouseRelay#onRowsUpdated} to inform the
		 * listener of a successful update.
		 * @param nCount the number of rows that were updated
		 */
		void onRowsUpdated( int nCount ) ;
		/**
		 * Called by {@link SQLiteHouseRelay#onUpdateFailed} to inform the
		 * listener of a failed update.
		 */
		void onUpdateFailed() ;

		/**
		 * Called by {@link SQLiteHouseRelay#onRowsDeleted} to inform the
		 * listener of a successful deletion.
		 * @param nCount the number of rows that were deleted
		 */
		void onRowsDeleted( int nCount ) ;

		/**
		 * Called by {@link SQLiteHouseRelay#onDeleteFailed} to inform the
		 * listener of a failed deletion.
		 */
		void onDeleteFailed() ;

		/**
		 * Called by {@link SQLiteHouseRelay#onRowsSelected} to pass the results
		 * of a successful selection to the listener.
		 * @param cls the schematic class that will marshal the data
		 * @param nTotalCount the total number of rows
		 * @param aoRows the rows themselves, already marshalled
		 * @param <SC> the schematic class
		 */
		<SC extends SQLightable> void onRowsSelected( Class<SC> cls, int nTotalCount, List<SC> aoRows ) ;

		/**
		 * Called by {@link SQLiteHouseRelay#onSelectFailed} to inform the
		 * listener of a failed selection.
		 */
		void onSelectFailed() ;
	}

/// Static constants ///////////////////////////////////////////////////////////

	public static final String LOG_TAG = SQLiteHouseRelay.class.getSimpleName();

/// Member fields //////////////////////////////////////////////////////////////

	/** The context in which the relay will operate. */
	protected Context m_ctx = null ;

	/** A reference for the contract under which the relay is registered. */
	protected SQLiteHouseSignalAPI m_api = null ;

	/** The set of active listeners. */
	protected Vector<Listener> m_vListeners = null ;

/// Constructors and initializers //////////////////////////////////////////////

	/**
	 * Constructs an instance, but does not register it.
	 * @param ctx the context in which the relay will operate
	 */
	public SQLiteHouseRelay( Context ctx )
	{
		m_ctx = ctx ;
		m_vListeners = new Vector<>() ;
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
			case RELAY_RECEIVE_SELECTION:
				this.onRowsSelected( sig ) ;
				break ;
			case RELAY_NOTIFY_SELECT_FAILED:
				this.onSelectFailed( sig ) ;
				break ;
			default:
				this.handleCustomAction( ctx, sig, sActionToken ) ;
		}
	}

/// Listener management ////////////////////////////////////////////////////////

	/**
	 * Registers a listener.
	 * The method is idempotent; if the same listener is passed multiple times,
	 * then it will be added only if it is not already present.
	 * @param l the listener to be registered
	 * @return (fluid)
	 */
	public SQLiteHouseRelay addListener( Listener l )
	{
		if( ! m_vListeners.contains(l) )
			m_vListeners.add(l) ;
		return this ;
	}

	/**
	 * Unregisters a listener.
	 * The method is idempotent; if the same listener is passed multiple times,
	 * then it will be removed only if it is still present.
	 * @param l the listener to be removed
	 * @return (fluid)
	 */
	public SQLiteHouseRelay removeListener( Listener l )
	{
		if( m_vListeners.contains(l) )
			m_vListeners.remove(l) ;
		return this ;
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
		long nRowID = sig.getLongExtra(
				m_api.getFormattedExtraTag( EXTRA_INSERT_ROW_ID ),
				INSERT_FAILED
			);
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Row ID [" )
				.append(( nRowID == INSERT_FAILED ? "(unknown)" : nRowID ))
				.append( "] of class [" )
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "] inserted into the database." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onRowInserted(nRowID) ;
	}

	/**
	 * Handles a signal from the keeper that a row insertion has failed.
	 * @param sig the received signal
	 */
	protected synchronized void onInsertFailed( Intent sig )
	{
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.e( LOG_TAG, (new StringBuilder())
				.append( "Keeper failed to insert a row of type [" )
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onInsertFailed() ;
	}


	/**
	 * Handles a signal from the keeper that some rows were updated.
	 * @param sig the received signal
	 */
	protected synchronized void onRowsUpdated( Intent sig )
	{
		int nCount = sig.getIntExtra(
				m_api.getFormattedExtraTag( EXTRA_RESULT_ROW_COUNT ),
				UPDATE_FAILED
			);
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Updated [" )
				.append(( nCount == UPDATE_FAILED ? "(unknown)" : nCount ))
				.append(( nCount == 1 ? "] row of type [" : "] rows of type [" ))
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onRowsUpdated(nCount) ;
	}

	/**
	 * Handles a signal from the keeper that a table update has failed.
	 * @param sig the received signal
	 */
	protected synchronized void onUpdateFailed( Intent sig )
	{
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.e( LOG_TAG, (new StringBuilder())
				.append( "Keeper failed to update rows of type [" )
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onUpdateFailed() ;
	}

	/**
	 * Handles a signal from the keeper that some rows were deleted.
	 * @param sig the received signal
	 */
	protected synchronized void onRowsDeleted( Intent sig )
	{
		int nCount = sig.getIntExtra(
				m_api.getFormattedExtraTag( EXTRA_RESULT_ROW_COUNT ),
				DELETE_FAILED
			);
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Deleted [" )
				.append(( nCount == DELETE_FAILED ? "(unknown)" : nCount ))
				.append(( nCount == 1 ? "] row of type [" : "] rows of type [" ))
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onRowsDeleted(nCount) ;
	}

	/**
	 * Handles a signal from the keeper that a row deletion failed.
	 * @param sig the received signal
	 */
	protected synchronized void onDeleteFailed( Intent sig )
	{
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.e( LOG_TAG, (new StringBuilder())
				.append( "Keeper failed to delete rows of type [" )
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onDeleteFailed() ;
	}

	/**
	 * Handles a signal payload from the keeper, containing a set of selected
	 * rows from the database.
	 * @param sig the received signal
	 * @param <SC> the schematic class
	 */
	protected synchronized <SC extends SQLightable> void onRowsSelected( Intent sig )
	{
		int nCount = sig.getIntExtra(
				m_api.getFormattedExtraTag( EXTRA_RESULT_ROW_COUNT ), -1 ) ;
		if( nCount == -1 )
		{ // Short-circuit; signal doesn't tell us how many results there are.
			Log.w( LOG_TAG, "No row count included in selection results." ) ;
			this.onSelectFailed(sig) ;
		}

		Class<SC> cls = null ;
		try { cls = m_api.getClassFromExtra(sig) ; }
		catch( SQLiteContentException x )
		{ // Short-circuit; can't figure out how to marshal results.
			Log.w( LOG_TAG, (new StringBuilder())
					.append( "Can't discover class to marshal [" )
					.append( nCount )
					.append(( nCount == 1 ? "] result" : "] results" ))
					.append( "from the keeper's signal." )
					.toString()
				, x );
			this.onSelectFailed(sig) ;
		}
		String sExtra = m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		if( ! sig.hasExtra(sExtra) )
		{ // Short-circuit; can't find the data extra (should at least be empty)
			Log.w( LOG_TAG, "Selection result signal had no data." ) ;
			this.onSelectFailed(sig) ;
		}
		Parcelable[] apclRows = sig.getParcelableArrayExtra(sExtra) ;
		if( apclRows == null ) return ;
		ArrayList<SC> aoRows = new ArrayList<>( apclRows.length ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(cls) ;
		for( Parcelable pclRow : apclRows )
			aoRows.add( tbl.fromBundle( ((Bundle)(pclRow)) ) ) ;

		for( Listener l : m_vListeners )
			l.onRowsSelected( cls, nCount, aoRows ) ;
	}

	/**
	 * Handles a signal from the keeper that a row selection failed.
	 * @param sig the received signal
	 */
	protected synchronized void onSelectFailed( Intent sig )
	{
		String sClass = m_api.getExtraSchemaClassName(sig) ;
		Log.e( LOG_TAG, (new StringBuilder())
				.append( "Keeper failed to select rows of type [" )
				.append(( sClass == null ? "(unknown)" : sClass ))
				.append( "]." )
				.toString()
			);

		for( Listener l : m_vListeners )
			l.onSelectFailed() ;
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

	/**
	 * Requests the selection of a set of rows from the keeper's database.
	 * @param cls the schematic class that would contain the rows
	 * @param q a query against that data set
	 * @param <SC> the schematic class
	 * @return (fluid)
	 */
	public <SC extends SQLightable> SQLiteHouseRelay select(
			Class<SC> cls, SelectionBuilder q )
	{ m_ctx.sendBroadcast( this.buildSelectionSignal(cls,q) ) ; return this ; }

	/**
	 * Constructs the {@link Intent} to be sent by {@link #select}.
	 * This is a separate method only so that it can be unit-tested.
	 * @param cls the schematic class that would contain the rows
	 * @param q a query against that data set
	 * @param <SC> the schematic class
	 * @return the intent to be sent by {@link #select}
	 */
	protected <SC extends SQLightable> Intent buildSelectionSignal(
			Class<SC> cls, SelectionBuilder q )
	{
		Intent sig = new Intent(
				m_api.getFormattedKeeperAction( KEEPER_SELECT ) ) ;
		SQLightable.Reflection<SC> tbl = m_api.reflect(cls) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ),
				tbl.getTableClass().getCanonicalName() ) ;
		sig.putExtra( m_api.getFormattedExtraTag( EXTRA_SELECTION_QUERY_SPEC ),
				q.toBundle() ) ;
		return sig ;
	}

/// Other instance methods /////////////////////////////////////////////////////

}
