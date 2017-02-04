package net.zerobandwidth.android.lib;

import android.content.Intent;
import android.util.Log;

/**
 * Provides utilites for dealing with intents.
 * @since zerobandwidth-net/android 0.0.1 (#5)
 */

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
}
