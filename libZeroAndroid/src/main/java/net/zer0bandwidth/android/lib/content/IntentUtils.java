package net.zer0bandwidth.android.lib.content;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Collection;

/**
 * Provides utilities for dealing with intents.
 * @since zer0bandwidth-net/android 0.0.1 (#5), moved in 0.0.4 (#13)
 */
public class IntentUtils
{
    public static final String TAG = IntentUtils.class.getSimpleName() ;

	/**
	 * Exception thrown when a call to one of the intent filter creation methods
	 * encounters a problem with one or more arguments.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static class FilterFormatException
	extends IllegalArgumentException
	{
		/** Message when a domain specification is left empty. */
		protected static final String EMPTY_DOMAIN =
				"Intent action names must be linked to a domain." ;

		/** Message when an action list is left empty. */
		protected static final String EMPTY_ACTION_LIST =
				"Intent action list cannot be empty." ;

		public static FilterFormatException forEmptyDomain()
		{ return new FilterFormatException(EMPTY_DOMAIN) ; }

		public static FilterFormatException forEmptyActionList()
		{ return new FilterFormatException(EMPTY_ACTION_LIST) ; }

		protected FilterFormatException( String sMessage )
		{ super(sMessage) ; }
	}

    /**
     * Discovers the action inscribed into the intent, if any, and logs the
     * result before returning it to the caller.
     * @param sig the intent to be examined
     * @return the action discovered if any
     */
    public static String discoverAction( Intent sig )
    {
        String sAction = null ;

        if( sig == null )
            Log.d( TAG, "Can't discover action in a null intent." ) ;
        else
        {
            sAction = sig.getAction() ;
            Log.d( TAG, (new StringBuilder())
                    .append( "Discovered action [" )
                    .append(( sAction == null ? "(null)" : sAction ))
                    .append( "].")
                    .toString()
                );
        }

        return sAction ;
    }

    /**
     * Tries to get an {@link Intent} that is bound to the specified class,
     * which is expected to process that intent. If the context is null, then
     * the method will still return an intent, but it will not be successfully
     * bound.
     * @param ctx the context in which to create the intent
     * @param cls the class that is expected to process the intent
     * @return the intent
     * @since zer0bandwidth-net/android 0.0.2 (#8)
     */
    public static Intent getBoundIntent( Context ctx, Class<?> cls )
    {
        if( ctx != null ) return new Intent( ctx, cls ) ;
        else return new Intent() ;
    }

    /**
     * Tries to get an {@link Intent} that is bound to the specified class,
     * which is expected to process that intent. If the context is null, then
     * the method will still return an intent, but it will not be bound to the
     * specified class. That intent is then inscribed with a specific action.
     * @param ctx the context in which to create the intent
     * @param cls the class that is expected to process the intent
     * @param sAction the action to be inscribed into the intent
     * @return the intent
     * @since zer0bandwidth-net/android 0.0.2 (#8)
     */
    public static Intent getBoundIntent( Context ctx, Class<?> cls, String sAction )
    { return getBoundIntent(ctx,cls).setAction(sAction) ; }

    /**
     * The default action name format to be used with
     * {@link #getActionListIntentFilter}. It has two placeholders; one for the
	 * intent filter's "domain" and the other for each action token.
     * @since zer0bandwidth-net/android 0.1.7 (#50)
     */
    public static final String DEFAULT_ACTION_FORMAT = "%s.action.%s" ;

    /**
     * Constructs an {@link IntentFilter} for all actions that would be
     * assembled from the specified format string, domain, and action list.
     * @param sCustomFormat a format string to be applied to the intent
     *                      domain and action names
     * @param sDomain the domain under which the intents will be
     *                recognized; this is typically a package or class name
     * @param asActions the list of actions to be written to the filter
     * @return an intent filter for the specified actions
     * @throws FilterFormatException if the domain string or action list
	 *  is not usable
     * @since zer0bandwidth-net/android 0.1.7 (#50)
     */
    public static IntentFilter getActionListIntentFilter(
            String sCustomFormat, String sDomain, String... asActions )
    throws FilterFormatException
    {
        IntentFilter filter = new IntentFilter() ;

        String sFormat = sCustomFormat ;
        if( sFormat == null || sFormat.length() == 0 )
            sFormat = DEFAULT_ACTION_FORMAT ;

        if( sDomain == null || sDomain.length() == 0 )
        	throw FilterFormatException.forEmptyDomain() ;

        if( asActions == null || asActions.length == 0 )
        	throw FilterFormatException.forEmptyActionList() ;

        for( String sAction : asActions )
            filter.addAction( String.format( sFormat, sDomain, sAction ) ) ;

        return filter ;
    }

    /**
     * Constructs an {@link IntentFilter} for all actions that would be
     * assembled from the specified format string, domain, and action list.
     * @param sFormat a format string to be applied to the intent domain
     *                and action names
     * @param sDomain the domain under which the intents will be
     *                recognized; this is typically a package or class name
     * @param asActions the list of actions to be written to the filter
     * @return an intent filter for the specified actions
     * @throws FilterFormatException if the domain string or action list
     *  is not usable
     * @since zer0bandwidth-net/android 0.1.7 (#50)
     */
    public static IntentFilter getActionListIntentFilter(
            String sFormat, String sDomain, Collection<String> asActions )
    throws FilterFormatException
    {
        if( asActions == null || asActions.size() == 0 )
        	throw FilterFormatException.forEmptyActionList() ;
        return getActionListIntentFilter( sFormat, sDomain,
                asActions.toArray( new String[ asActions.size() ] ) ) ;
    }

    /**
     * Constructs an {@link IntentFilter} for all actions that would be
     * assembled from the specified domain and action list.
     *
     * Each action token is inserted along with the domain string into the
     * {@link #DEFAULT_ACTION_FORMAT} specification.
     *
     * @param sDomain the domain under which the intents will be recognized;
     *                recognized; this is typically a package or class name
     * @param asActions the list of actions to be written to the filter
     * @return an intent filter for the specified actions
     * @throws FilterFormatException if the domain string or action list
     *  is unusable
     * @since zer0bandwidth-net/android 0.1.7 (#50)
     */
    public static IntentFilter getActionListIntentFilter(
            String sDomain, String... asActions )
    throws FilterFormatException
    {
        return getActionListIntentFilter( DEFAULT_ACTION_FORMAT,
                sDomain, asActions ) ;
    }

    /**
     * Constructs an {@link IntentFilter} for all actions that would be
     * assembled from the specified domain and action list.
     *
     * Each action token is inserted along with the domain string into the
     * {@link #DEFAULT_ACTION_FORMAT} specification.
     *
     * @param sDomain the domain under which the intents will be
     *                recognized; this is typically a package or class name
     * @param asActions the list of actions to be written to the filter
     * @return an intent filter for the specified actions
     * @throws FilterFormatException if the domain string or action list
     *  is unusable
     * @since zer0bandwidth-net/android 0.1.7 (#50)
     */
    public static IntentFilter getActionListIntentFilter(
            String sDomain, Collection<String> asActions )
    throws FilterFormatException
    {
        return getActionListIntentFilter( DEFAULT_ACTION_FORMAT,
                sDomain, asActions ) ;
    }
}
