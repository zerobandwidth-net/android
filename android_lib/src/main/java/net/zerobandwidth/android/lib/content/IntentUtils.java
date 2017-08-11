package net.zerobandwidth.android.lib.content;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Provides utilities for dealing with intents.
 * @since zerobandwidth-net/android 0.0.1 (#5), moved in 0.0.4 (#13)
 */
@SuppressWarnings("unused")                                // This is a library.
public class IntentUtils
{
    public static final String TAG = IntentUtils.class.getSimpleName() ;

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
     * @since zerobandwidth-net/android 0.0.2 (#8)
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
     * @since zerobandwidth-net/android 0.0.2 (#8)
     */
    public static Intent getBoundIntent( Context ctx, Class<?> cls, String sAction )
    { return getBoundIntent(ctx,cls).setAction(sAction) ; }
}
