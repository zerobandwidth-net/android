package net.zer0bandwidth.android.lib.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.util.Log;

import java.util.concurrent.TimeoutException;

/**
 * Extends {@link SimpleServiceConnection} to provide connectivity to services
 * within an Android test case. This is not a test suite in itself; rather, it
 * provides a feature which may be used in tests.
 * @since zer0bandwidth-net/android 0.1.2 (#3)
 */
@SuppressWarnings("unused")
public class SimpleTestServiceConnection<S extends Service>
extends SimpleServiceConnection<S>
{
    public static final String LOG_TAG =
            SimpleTestServiceConnection.class.getSimpleName() ;

    /**
     * Limits the number of times that the connection will retry before giving
     * up.
     */
    public static final int MAX_CONNECTION_RETRIES = 10 ;

    public SimpleTestServiceConnection( Class<S> cls )
    { super(cls) ; }

    /**
     * As {@link SimpleServiceConnection#connect(Context,int)}, but with a
     * {@link ServiceTestRule} instead of a {@link Context} as the context in
     * which the connection occurs.
     * @param rule a service test rule to facilitate the binding
     * @param bmFlags a mask of optional binding flags; see {@code Context}
     * @return the connection, for fluid invocations
     * @throws IllegalArgumentException if the rule is null
     * @throws TimeoutException if the binding times out
     */
    public synchronized SimpleTestServiceConnection<S> connectTest( ServiceTestRule rule, int bmFlags )
    throws IllegalArgumentException, TimeoutException
    {
        if( rule == null )
        {
            throw new IllegalArgumentException(
                    "Cannot bind to service without a testing rule." ) ;
        }
        int i = 0 ;
        IBinder binder = null ;
        while( binder == null && i++ < MAX_CONNECTION_RETRIES )
        {
            Intent sig = new Intent(
                    InstrumentationRegistry.getTargetContext(), m_clsService ) ;
            try { binder = rule.bindService( sig, this, bmFlags ) ; }
            catch( TimeoutException x )
            { Log.w( LOG_TAG, "Timed out waiting for a connection." ) ; }
        }

        if( binder == null )
            Log.w( LOG_TAG, "Exhausted retry attempts, still failed." ) ;
        else
        {
            Log.i( LOG_TAG, (new StringBuilder())
                    .append( "Connected after [" )
                    .append( i )
                    .append( "] attempts." )
                    .toString()
                );
        }

        return this ;
    }

    /**
     * As {@link SimpleTestServiceConnection#connect(Context)}, but with a
     * {@link ServiceTestRule} instead of a {@link Context} as the context in
     * which the connection occurs.
     * @param rule a service test rule to facilitate the binding
     * @return the connection, for fluid invocations
     * @throws IllegalArgumentException if the rule is null
     * @throws TimeoutException if the binding times out
     */
    public synchronized SimpleTestServiceConnection<S> connectTest( ServiceTestRule rule )
    throws IllegalArgumentException, TimeoutException
    { return this.connectTest( rule, DEFAULT_BINDING_FLAGS ) ; }

    // /// Below this line are overrides with new return types.

    @Override
    public SimpleTestServiceConnection<S> addListener( Listener<S> l )
    { super.addListener(l) ; return this ; }

    @Override
    public SimpleTestServiceConnection<S> removeListener( Listener<S> l )
    { super.removeListener(l) ; return this ; }

    @Override
    public SimpleTestServiceConnection<S> connect( Context ctx, int bmFlags )
    { super.connect(ctx,bmFlags) ; return this ; }

    @Override
    public SimpleTestServiceConnection<S> connect( Context ctx )
    { super.connect(ctx) ; return this ; }

    @Override
    public SimpleTestServiceConnection<S> disconnect( Context ctx )
    { super.disconnect(ctx) ; return this ; }
}
