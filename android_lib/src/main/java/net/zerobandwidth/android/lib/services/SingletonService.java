package net.zerobandwidth.android.lib.services;

import android.app.Service ;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.zerobandwidth.android.lib.content.IntentUtils;

import java.util.HashMap;

/**
 * This service provides "singleton" instances of any specified class. This is
 * done as an Android {@link Service}, instead of the normal Java singleton
 * model, because Android tends to assign multiple class loaders during the life
 * cycle of a single app, and thus static values (like singletons) set in one
 * {@link android.app.Activity Activity} might not be visible in another. By
 * registering singletons with a {@code Service}, multiple {@code Activity}s
 * can be more confident about getting their singletons from the same source.
 * @since zerobandwidth-net/android 0.0.1 (#1)
 */
public class SingletonService
extends Service
{
    /** A logging tag. */
    protected static final String LOG_TAG =
            SingletonService.class.getSimpleName() ;

	protected static final String ACTION_KICKOFF =
		"net.zerobandwidth.android.actions.services.SingletonService.KICKOFF" ;

	public static void kickoff( Context ctx )
	{
		ctx.startService(
			IntentUtils.getBoundIntent( ctx, SingletonService.class )
				.setAction( ACTION_KICKOFF ) ) ;
	}

    /**
     * An implementation of the {@link android.os.Binder} class which simply
     * returns this instance of the service.
     * @since zerobandwidth-net/android 0.0.1 (#1)
     */
    public class Binder extends android.os.Binder
    implements SimpleServiceConnection.InstanceBinder<SingletonService>
    {
        @Override
        public SingletonService getServiceInstance()
        { return SingletonService.this ; }
    }

    /** The mapping of classes to singleton instances of those classes. */
    protected HashMap<Class<?>,Object> m_mapSingletons ;

    /** A constant binder instance. */
    protected final SingletonService.Binder m_bind =
		    new SingletonService.Binder() ;

    @Override
    public void onCreate()
    {
        super.onCreate() ;
        m_mapSingletons = new HashMap<>() ;
    }

    @Override
    public SingletonService.Binder onBind( Intent sig )
    { return m_bind ; }

	@Override
	public int onStartCommand( Intent sig, int zFlags, int nStartID )
	{
		super.onStartCommand( sig, zFlags, nStartID ) ;
		Log.i( LOG_TAG, "Started service." ) ;
		return Service.START_STICKY ;
	}

	/**
	 * Returns the singleton instance of the specified class. If no singleton
	 * has been created yet, then {@code null} is returned. If a casting error
	 * occurs internally, then the method returns {@code null} but also logs a
	 * warning.
	 * @param cls the class for which a singleton should be returned
	 * @param <T> the class for which a singleton should be returned
	 * @return the singleton instance of the class, or {@code null} if none
	 *  can be obtained
	 */
    @SuppressWarnings("unchecked") // We explicitly catch ClassCastException.
    public synchronized <T> T get( Class<T> cls )
    {
        T oInstance = null ;
        try { oInstance = (T)(this.m_mapSingletons.get(cls)) ; }
        catch( ClassCastException xCast )
        { Log.w( LOG_TAG, "Cannot cast singleton fetched from map." ) ; }
        return oInstance ;
    }

	/**
	 * Specifies the singleton instance of a given class. If no previous
	 * singleton has been set, then {@code null} is returned. If a casting
	 * error occurs internally while trying to pass back the previous instance,
	 * then {@code null} is still returned, but an error is also written to the
	 * logs.
	 * @param cls the class for which a singleton should be set
	 * @param oInstance the instance to be proclaimed as the singleton instance
	 * @param <T> the class for which a singleton should be set
	 * @return any previously-set singleton instance for that class, or
	 *  {@code null} if none can be obtained
	 */
    @SuppressWarnings("unchecked") // We explicitly catch ClassCastException.
    public synchronized <T> T put( Class<T> cls, T oInstance )
    {
        T oPrevious = null ;
        try { oPrevious = (T)(this.m_mapSingletons.put(cls, oInstance)) ; }
        catch( ClassCastException xCast )
        { Log.w( LOG_TAG, "Caught exception when casting previous instance." ) ; }
        return oPrevious ;
    }

	/**
	 * Gets the current singleton instance for the specified class, or stores
	 * the supplied alternative as the singleton if none was previously set.
	 * @param cls the class for which a singleton should be returned
	 * @param oAlternative an instance to set if no previous instance is found
	 * @param <T> the class for which a singleton should be returned
	 * @return the singleton instance of the class, or the supplied alternative
	 *  if none can be obtained
	 */
    public synchronized <T> T getOrPut( Class<T> cls, T oAlternative )
    {
        if( m_mapSingletons.containsKey(cls) )
            return this.get(cls) ;
        else
        {
            this.put( cls, oAlternative ) ;
            return oAlternative ;
        }
    }

	/**
	 * Indicates whether a singleton instance has been set for the specified
	 * class.
	 * @param cls the class for which a singleton might be set
	 * @param <T> the class for which a singleton might be set
	 * @return {@code true} if an instance has been set for the class
	 */
    public synchronized <T> boolean hasInstanceOf( Class<T> cls )
    { return m_mapSingletons.containsKey(cls) ; }
}
