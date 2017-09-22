package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import net.zerobandwidth.android.lib.content.IntentUtils;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zerobandwidth.android.lib.util.CollectionsZ;

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
	public static final String EXTRA_SCHEMA_CLASS_NAME = "CLASS" ;
	/** Name suffix for the extra that contains the schematic class data. */
	public static final String EXTRA_SCHEMA_CLASS_DATA = "DATA" ;
	/** Name suffix for the extra that contains the inserted row ID. */
	public static final String EXTRA_INSERT_ROW_ID = "ROW_ID" ;
	/**
	 * Name suffix for the extra that contains the number of rows updated or
	 * deleted.
	 */
	public static final String EXTRA_MODIFY_ROW_COUNT = "ROW_COUNT" ;

	/** The default format string for constructing intent extra tags. */
	public static final String DEFAULT_EXTRA_TAG_FORMAT = "%s.extra.%s" ;

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
	 * Cache of reflections that have been pushed through the relay.
	 * Populated by
	 */
	protected SQLightable.ReflectionMap m_mapReflections =
			new SQLightable.ReflectionMap() ;

	/**
	 * A set of custom actions expected to be supported by a
	 * {@link SQLiteHouseKeeper} implementation.
	 */
	protected String[] m_asCustomKeeperActions = null ;

	/**
	 * The format string to be used when creating keeper action tags.
	 * Defaults to {@link #DEFAULT_KEEPER_ACTION_FORMAT}.
	 */
	protected String m_sKeeperActionFormat = DEFAULT_KEEPER_ACTION_FORMAT ;

	/**
	 * A set of custom actions expected to be supported by a
	 * {@link SQLiteHouseRelay} implementation.
	 */
	protected String[] m_asCustomRelayActions = null ;

	/**
	 * The format string to be used when creating relay action tags.
	 * Defaults to {@link #DEFAULT_RELAY_ACTION_FORMAT}.
	 */
	protected String m_sRelayActionFormat = DEFAULT_RELAY_ACTION_FORMAT ;

	/**
	 * The format string to be used when creating intent extra tags. This is a
	 * consistent format across both sides of the API.
	 */
	protected String m_sExtraTagFormat = DEFAULT_EXTRA_TAG_FORMAT ;

	/**
	 * Reveals the "domain" under which the keeper and relay will operate.
	 * This string forms the root segments of the {@code Intent} action tokens.
	 * @return the domain linking a keeper and relay
	 */
	protected abstract String getIntentDomain() ;

	/**
	 * Accesses the set of keeper actions which will be registered.
	 *
	 * By default, this is the value of {@link #KEEPER_ACTIONS}. If the
	 * {@link SQLiteHouseKeeper} implementation also supports custom actions,
	 * then they should be registered with {@link #setKeeperActions}, which will
	 * <i>append</i> the custom list to the default.
	 *
	 * @return the list of actions supported by the keeper implementation
	 */
	public final String[] getKeeperActions()
	{
		if( m_asCustomKeeperActions == null ) return KEEPER_ACTIONS ;
		else return CollectionsZ.of( String.class )
				.arrayConcat( KEEPER_ACTIONS, m_asCustomKeeperActions ) ;
	}

	/**
	 * Adds a list of custom actions which are expected from a
	 * {@link SQLiteHouseKeeper} implementation.
	 * @param asCustomActions the list of custom actions
	 * @return (fluid)
	 */
	public final SQLiteHouseSignalAPI setKeeperActions( String[] asCustomActions )
	{ m_asCustomKeeperActions = asCustomActions ; return this ; }

	/**
	 * Accesses the format string used to construct keeper actions.
	 * @return the format for keeper actions
	 */
	public final String getKeeperActionFormat()
	{ return m_sKeeperActionFormat ; }

	/**
	 * Sets the format string used to construct actions for {@link Intent}s that
	 * are dispatched to {@link SQLiteHouseKeeper}s. This format string
	 * <i>must</i> have two string variables.
	 *
	 * Defaults to {@link #DEFAULT_KEEPER_ACTION_FORMAT} if a null or empty
	 * value is supplied.
	 *
	 * @param sFormat the format string
	 * @return (fluid)
	 */
	public final SQLiteHouseSignalAPI setKeeperActionFormat( String sFormat )
	{
		if( sFormat == null || sFormat.isEmpty() )
			m_sKeeperActionFormat = DEFAULT_KEEPER_ACTION_FORMAT ;
		else
			m_sKeeperActionFormat = sFormat ;
		return this ;
	}

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
	 * Accesses the set of relay actions which will be registered.
	 *
	 * By default, this is the value of {@link #RELAY_ACTIONS}. If the
	 * {@link SQLiteHouseRelay} implementation also supports custom actions,
	 * then they should be registered with {@link #setRelayActions}, which will
	 * <i>append</i> the custom list to the default.
	 *
	 * @return the list of actions supported by the relay implementation
	 */
	public final String[] getRelayActions()
	{
		if( m_asCustomRelayActions == null ) return RELAY_ACTIONS ;
		else return CollectionsZ.of( String.class )
				.arrayConcat( RELAY_ACTIONS, m_asCustomRelayActions ) ;
	}

	/**
	 * Adds a list of custom actions which are expected from a
	 * {@link SQLiteHouseRelay} implementation.
	 * @param asCustomActions the list of custom actions
	 * @return (fluid)
	 */
	public final SQLiteHouseSignalAPI setRelayActions( String[] asCustomActions )
	{ m_asCustomRelayActions = asCustomActions ; return this ; }

	/**
	 * Accesses the format string used to construct relay actions.
	 * @return the format for relay actions
	 */
	public final String getRelayActionFormat()
	{ return m_sRelayActionFormat ; }

	/**
	 * Sets the format string used to construct actions for {@link Intent}s that
	 * are dispatched to {@link SQLiteHouseRelay}s. This format string
	 * <i>must</i> have two string variables.
	 *
	 * Defaults to {@link #DEFAULT_RELAY_ACTION_FORMAT} if a null or empty
	 * value is supplied.
	 *
	 * @param sFormat the format string
	 * @return (fluid)
	 */
	public final SQLiteHouseSignalAPI setRelayActionFormat( String sFormat )
	{
		if( sFormat == null || sFormat.isEmpty() )
			m_sRelayActionFormat = DEFAULT_RELAY_ACTION_FORMAT ;
		else
			m_sRelayActionFormat = sFormat ;
		return this ;
	}

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
	 * Accesses the format string used to construct name tags for {@link Intent}
	 * extras.
	 * @return the format for intent extra tags
	 */
	public final String getExtraTagFormat()
	{ return m_sExtraTagFormat ; }

	/**
	 * Sets the format string used to construct tags for {@link Intent} extras
	 * that are exchanged between the keeper and the relay.
	 *
	 * Defaults to {@link #DEFAULT_EXTRA_TAG_FORMAT} if a null or empty value is
	 * supplied.
	 *
	 * @param sFormat the format string
	 * @return (fluid)
	 */
	public final SQLiteHouseSignalAPI setExtraTagFormat( String sFormat )
	{
		if( sFormat == null || sFormat.isEmpty() )
			m_sExtraTagFormat = DEFAULT_EXTRA_TAG_FORMAT ;
		else
			m_sExtraTagFormat = sFormat ;
		return this ;
	}

	/**
	 * Constructs the full name tag for an {@link Intent} extra to be exchanged
	 * between a keeper and a relay.
	 * @param sToken the significant part of the extra token which makes it
	 *               unique among the extras in this API
	 * @return the fully-formed extra tag
	 */
	public final String getFormattedExtraTag( String sToken )
	{
		return String.format( this.getExtraTagFormat(),
				this.getIntentDomain(), sToken ) ;
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
		String sExtra = this.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_NAME ) ;
		if( ! sig.hasExtra( sExtra ) )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		String sClass = sig.getStringExtra( sExtra ) ;
		if( sClass == null || sClass.isEmpty() )
			throw SQLiteContentException.noClassSpecified(null) ;
		try
		{
			Class<?> cls = Class.forName( sClass ) ;
			if( SQLightable.class.isAssignableFrom(cls) )
			{
				// noinspection unchecked - caught explicitly below
				return ((Class<SC>)(cls)) ;
			}
			else
			{
				throw IntrospectionException
						.illegalClassSpecification( cls, null ) ;
			}
		}
		catch( ClassNotFoundException | ClassCastException x )
		{ throw IntrospectionException.illegalClassSpecification( sClass, x ); }
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
		if( cls == null )
			throw SQLiteContentException.noClassSpecified(null) ;
		String sExtra = this.getFormattedExtraTag( EXTRA_SCHEMA_CLASS_DATA ) ;
		if( ! sig.hasExtra( sExtra ) )
			throw SQLiteContentException.expectedExtraNotFound( sExtra, null ) ;
		Bundle bndl = sig.getBundleExtra( sExtra ) ;
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
			m_mapReflections.put( cls ) ;
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
