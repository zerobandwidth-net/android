package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;

/**
 * Defines the contract of signals between implementations of
 * {@link SQLiteHouseKeeper} and {@link SQLiteHouseRelay}.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public abstract class SQLiteHouseSignalAPI
{
	public static final String LOG_TAG =
			SQLiteHouseSignalAPI.class.getSimpleName() ;

	/** Name suffix for the extra that contains the schematic class name. */
	public static final String EXTRA_SCHEMA_CLASS_NAME = ".extra.CLASS" ;
	/** Name suffix for the extra that contains the schematic class data. */
	public static final String EXTRA_SCHEMA_CLASS_DATA = ".extra.DATA" ;
	/** Name suffix for the extra that contains the inserted row ID. */
	public static final String EXTRA_INSERT_ROW_ID = ".extra.ROW_ID" ;

	/** Action suffix for inserting an object. */
	public static final String KEEPER_INSERT = "INSERT" ;
	/** Action suffix for selecting objects. */
	public static final String KEEPER_SELECT = "SELECT" ;
	/** Action suffix for updating objects. */
	public static final String KEEPER_UPDATE = "UPDATE" ;
	/** Action suffix for deleting objects. */
	public static final String KEEPER_DELETE = "DELETE" ;

	/** The set of actions which are supported by the keeper implementation. */
	public static final String[] KEEPER_ACTIONS =
	{ KEEPER_INSERT, KEEPER_SELECT, KEEPER_UPDATE, KEEPER_DELETE } ;

	/** The default format string for constructing keeper actions. */
	public static final String DEFAULT_KEEPER_ACTION_FORMAT =
			"%s.keeper.action.%s" ;

	/** Action suffix for notifying a relay that a record was inserted. */
	public static final String RELAY_NOTIFY_INSERT = "NOTIFY_INSERT" ;
	/** Action suffix for notifying a relay that an insertion failed. */
	public static final String RELAY_NOTIFY_INSERT_FAILED =
			"NOTIFY_INSERT_FAILED" ;
	/** Action suffix for notifying a relay that a record was updated. */
	public static final String RELAY_NOTIFY_UPDATE = "NOTIFY_UPDATE" ;
	/** Action suffix for notifying a relay that an update failed. */
	public static final String RELAY_NOTIFY_UPDATE_FAILED =
			"NOTIFY_UPDATE_FAILED" ;
	/** Action suffix for notifying a relay that a record was deleted. */
	public static final String RELAY_NOTIFY_DELETE = "NOTIFY_DELETE" ;
	/** Action suffix for notifying a relay that a deletion failed. */
	public static final String RELAY_NOTIFY_DELETE_FAILED =
			"NOTIFY_DELETE_FAILED" ;

	/** The set of actions which are supported by the relay implementation. */
	public static final String[] RELAY_ACTIONS =
	{
		RELAY_NOTIFY_INSERT, RELAY_NOTIFY_INSERT_FAILED,
		RELAY_NOTIFY_UPDATE, RELAY_NOTIFY_UPDATE_FAILED,
		RELAY_NOTIFY_DELETE, RELAY_NOTIFY_DELETE_FAILED
	};

	/** The default format string for constructing relay actions. */
	public static final String DEFAULT_RELAY_ACTION_FORMAT =
			"%s.relay.action.%s" ;

	/**
	 * Cache of the tag for the extra that contains the canonical name of a
	 * schematic class.
	 * Generated and cached by {@link #getExtraSchemaClassName()}.
	 */
	protected String m_sExtraSchemaClassName = null ;

	/**
	 * Cache of the tag for the extra that contains data that could be
	 * marshalled into a schematic class instance.
	 * Generated and cached by {@link #getExtraSchemaDataName()}.
	 */
	protected String m_sExtraSchemaDataName = null ;

	/**
	 * Cache of the tag for the extra that contains an inserted row ID.
	 * Generated and cached by {@link #getExtraInsertedRowID()}.
	 */
	protected String m_sExtraRowID = null ;

	/**
	 * Cache of reflections that have been pushed through the relay.
	 * Populated by
	 */
	protected SQLightable.ReflectionMap m_mapReflections =
			new SQLightable.ReflectionMap() ;

	/**
	 * Reveals the "domain" under which the keeper and relay will operate.
	 * This string forms the root segments of the {@code Intent} action tokens.
	 * @return the domain linking a keeper and relay
	 */
	protected abstract String getIntentDomain() ;

	/**
	 * Reveals the set of actions supported by the keeper implementation.
	 *
	 * By default, this is the value of {@link #KEEPER_ACTIONS}; this may be
	 * overridden to supply additional custom actions instead.
	 *
	 * Overrides <i>should</i> include the value of {@code KEEPER_ACTIONS} in
	 * the action set; otherwise, the write functions of
	 * {@link SQLiteHouseRelay} will not function properly.
	 *
	 * @return the list of actions supported by the keeper implementation
	 */
	public String[] getKeeperActions()
	{ return KEEPER_ACTIONS ; }

	/**
	 * Specifies the format for constructing keeper actions. Override this
	 * method to use a custom format; the default implementation returns
	 * {@link #DEFAULT_KEEPER_ACTION_FORMAT}.
	 * @return the format for keeper actions
	 */
	public String getKeeperActionFormat()
	{ return DEFAULT_KEEPER_ACTION_FORMAT ; }

	/**
	 * Creates a fully-formed action name for the keeper.
	 * @param sToken the action token (suffix) to be appended
	 * @return an action token to be broadcast to a keeper
	 */
	public final String getFormattedKeeperAction( String sToken )
	{
		return String.format( this.getKeeperActionFormat(),
				this.getIntentDomain(), sToken ) ;
	}

	/**
	 * Extracts the action token from a fully-formatted keeper action string.
	 * The default implementation takes the substring after the last period.
	 * @param sAction the fully-formatted keeper action
	 * @return the action token
	 */
	public String getTokenFromKeeperAction( String sAction )
	{ return sAction.substring( sAction.lastIndexOf(".") + 1 ) ; }

	/**
	 * Constructs the {@link IntentFilter} for the {@link SQLiteHouseKeeper}
	 * implementation.
	 * @return an intent filter for the keeper
	 */
	public final IntentFilter getKeeperIntentFilter()
	{
		return IntentUtils.getActionListIntentFilter(
				this.getKeeperActionFormat(),
				this.getIntentDomain(),
				this.getKeeperActions()
			);
	}

	/**
	 * Reveals the set of actions supported by the relay implementation.
	 *
	 * By default, this is the value of {@link #RELAY_ACTIONS}; this may be
	 * overridden to supply additional custom actions if desired.
	 *
	 * Overrides <i>should</i> include the value of {@code RELAY_ACTIONS} in
	 * the action set; otherwise, the notification functions of
	 * {@link SQLiteHouseRelay} will not function properly.
	 *
	 * @return the list of actions supported by the beacon implementation
	 */
	public String[] getRelayActions()
	{ return RELAY_ACTIONS ; }

	/**
	 * Specifies the format for constructing relay actions. Override this
	 * method to use a custom format; the default implementation returns
	 * {@link #DEFAULT_RELAY_ACTION_FORMAT}.
	 * @return the format for keeper actions
	 */
	public String getRelayActionFormat()
	{ return DEFAULT_RELAY_ACTION_FORMAT ; }

	/**
	 * Creates a fully-formed action name for the relay.
	 * @param sToken the action token (suffix) to be appended
	 * @return an action token to be broadcast to a relay
	 */
	public final String getFormattedRelayAction( String sToken )
	{
		return String.format( this.getRelayActionFormat(),
				this.getIntentDomain(), sToken ) ;
	}

	/**
	 * Extracts the action token from a fully-formatted relay action string.
	 * The default implementation takes the substring after the last period.
	 * @param sAction the fully-formatted relay action
	 * @return the action token
	 */
	public String getTokenFromRelayAction( String sAction )
	{ return sAction.substring( sAction.lastIndexOf(".") + 1 ) ; }

	/**
	 * Constructs the {@link IntentFilter} for the {@link SQLiteHouseRelay}
	 * implementation.
	 * @return an intent filter for the relay
	 */
	public final IntentFilter getRelayIntentFilter()
	{
		return IntentUtils.getActionListIntentFilter(
				this.getRelayActionFormat(),
				this.getIntentDomain(),
				this.getRelayActions() ) ;
	}

	/**
	 * Constructs the full tag of an extra to be sent in a broadcast.
	 * @param sToken the significant part of the extra token which makes it
	 *               unique among the extras in this API
	 * @return the fully-formed extra tag
	 */
	protected final String constructExtraTag( String sToken )
	{
		return (new StringBuilder())
			.append( this.getIntentDomain() ).append( sToken )
			.toString()
			;
	}

	/**
	 * Accesses the full name of the intent extra that will store the canonical
	 * name of a schematic class to be marshalled. If not yet cached in this
	 * instance, then it will be constructed and cached.
	 * @return the name of the intent extra
	 */
	public final String getExtraSchemaClassName()
	{
		if( m_sExtraSchemaClassName == null )
		{
			m_sExtraSchemaClassName =
					this.constructExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		}
		return m_sExtraSchemaClassName ;
	}

	/**
	 * Accesses the full name of the intent extra that will store the bundled
	 * data from an instance of the schematic class to be marshalled. If not yet
	 * cached in this instance, then it will be constructed and cached.
	 * @return the name of the intent extra
	 */
	public final String getExtraSchemaDataName()
	{
		if( m_sExtraSchemaDataName == null )
		{
			m_sExtraSchemaDataName =
					this.constructExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		}
		return m_sExtraSchemaDataName ;
	}

	/**
	 * Accesses the full name of the intent extra that will store the numeric ID
	 * of a row which has been inserted into a database.
	 * @return the name of the intent extra
	 */
	public final String getExtraInsertedRowID()
	{
		if( m_sExtraRowID == null )
			m_sExtraRowID = this.constructExtraTag( EXTRA_INSERT_ROW_ID ) ;
		return m_sExtraRowID ;
	}

	/**
	 * Discovers and returns the schematic class definition that is named in an
	 * intent sent from a relay to a keeper.
	 * @param sig the intent
	 * @param <SC> the schematic class
	 * @return the schematic class definition
	 * @throws SQLiteContentException if the intent is malformed, in that the
	 *  class name extra cannot be found or is empty
	 * @throws IntrospectionException if something goes wrong while discovering
	 *  the class definition
	 * @throws SQLiteContentException if the extra that's supposed to have the
	 *  class name in it can't be found
	 */
	public <SC extends SQLightable> Class<SC> getClassFromExtra( Intent sig )
			throws IntrospectionException, SQLiteContentException
	{
		String sExtra = this.getExtraSchemaClassName() ;
		if( ! sig.hasExtra( sExtra ) )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		String sClass = sig.getStringExtra( sExtra ) ;
		if( sClass == null || sClass.isEmpty() )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		try
		{
			Class<?> cls = Class.forName( sClass ) ;
			if( cls.isAssignableFrom( SQLightable.class ) )
			{
				// noinspection unchecked - caught explicitly below
				return ((Class<SC>)(cls)) ;
			}
			else
			{
				Log.e( LOG_TAG, (new StringBuilder())
						.append( "Class [" ).append( sClass )
						.append( "] is not assignable from SQLightable." )
						.toString()
				);
				throw IntrospectionException.instanceFailed( sClass, null ) ;
			}
		}
		catch( ClassNotFoundException | ClassCastException x )
		{ throw IntrospectionException.instanceFailed( sClass, x ) ; }
	}

	/**
	 * Discovers and extracts an instance of a schematic class from an extra
	 * provided in an intent from a relay to a keeper.
	 * @param sig the intent
	 * @param cls the schematic class
	 * @param <SC> the schematic class
	 * @return an instance of the schematic class, with data from the intent
	 * @throws SQLiteContentException if the intent is malformed, in that the
	 *  data extra cannot be found or is empty
	 * @throws IntrospectionException if an instance of the schematic class
	 *  can't be constructed
	 * @throws SchematicException if a problem occurs while processing the
	 *  schematic class
	 */
	public <SC extends SQLightable> SC getDataFromBundle( Intent sig, Class<SC> cls )
	throws SQLiteContentException, IntrospectionException, SchematicException
	{
		String sExtra = this.getExtraSchemaDataName() ;
		if( ! sig.hasExtra( sExtra ) )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		Bundle bndl = sig.getBundleExtra( this.getExtraSchemaDataName() ) ;
		if( bndl == null )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		SQLightable.Reflection<SC> tbl = this.reflect(cls) ;
		return tbl.fromBundle(bndl) ;
	}

	/**
	 * Uses a cached {@link SQLightable.Reflection} if available; otherwise,
	 * caches a new one, then returns it.
	 * @param cls the schematic class
	 * @param <SC> the schematic class
	 * @return the class's reflection
	 */
	protected <SC extends SQLightable> SQLightable.Reflection<SC> reflect( Class<SC> cls )
	{
		if( ! m_mapReflections.containsKey(cls) )
			m_mapReflections.put( cls, SQLightable.Reflection.reflect(cls) ) ;
		return m_mapReflections.get(cls) ;
	}

	/**
	 * Uses a cached {@link SQLightable.Reflection} if available; otherwise,
	 * caches a new one, then returns it.
	 * @param o an instance of the schematic class
	 * @param <SC> the schematic class
	 * @return the class's reflection
	 */
	protected <SC extends SQLightable> SQLightable.Reflection<SC> reflect( SC o )
	{
		// noinspection unchecked - guaranteed by signature
		return this.reflect( ((Class<SC>)( o.getClass() )) ) ;
	}

}
