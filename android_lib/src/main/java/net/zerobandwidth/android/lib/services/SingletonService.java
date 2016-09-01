package net.zerobandwidth.android.lib.services;

import android.app.Service ;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;

/**
 * This service provides "singleton" instances of any specified class. This is
 * done as an Android {@link Service}, instead of the normal Java singleton
 * model, because Android tends to assign multiple class loaders during the life
 * cycle of a single app, and thus static values (like singletons) set in one
 * {@link android.app.Activity Activity} might not be visible in another. By
 * registering singletons with a {@code Service}, multiple {@code Activity}s
 * can be more confident about getting their singletons from the same source.
 * @since issue 1
 */
public class SingletonService
extends Service
implements SingletonServiceInterface
{
    /** A logging tag. */
    protected static final String TAG =
            SingletonService.class.getCanonicalName() ;

    /**
     * An implementation of the {@link android.os.Binder} class which simply
     * returns this instance of the service.
     * @since issue 1
     */
    @SuppressWarnings("unchecked") // SingletonService will satisfy return type.
    public class Binder extends android.os.Binder
    implements SimpleServiceConnection.InstanceBinder
    {
        @Override
        public SingletonService getServiceInstance()
        { return SingletonService.this ; }
    }

    /**
     * A canonical implementation of the {@link ServiceConnection} interface,
     * which simply acts as a pass-through for the public methods of the service
     * itself. A consumer of the service does not have to implement its own
     * {@code ServiceConnection}; instead, it may store this one and interact
     * with it directly as if it were the service instance itself.
     * @since issue 1
     */
    public static class Connection
    extends SimpleServiceConnection<SingletonService>
    implements ServiceConnection, SingletonServiceInterface
    {
        public Connection()
        { super( SingletonService.class ) ; }

        @Override
        public synchronized <T> T get( Class<T> cls )
        { return m_srvInstance.get(cls) ; }

        @Override
        public synchronized <T> T put( Class<T> cls, T oInstance )
        { return m_srvInstance.put(cls, oInstance) ; }

        @Override
        public synchronized <T> T getOrPut( Class<T> cls, T oFallback )
        { return m_srvInstance.getOrPut( cls, oFallback ) ; }

        @Override
        public synchronized <T> boolean hasInstanceFor( Class<T> cls )
        { return m_srvInstance.hasInstanceFor(cls) ; }
    }

    /** The mapping of classes to singleton instances of those classes. */
    protected HashMap<Class<?>,Object> m_mapSingletons ;

    /** A constant binder instance. */
    protected final IBinder m_bind = new SingletonService.Binder() ;

    @Override
    public void onCreate()
    {
        super.onCreate() ;
        m_mapSingletons = new HashMap<>() ;
    }

    @Override
    public IBinder onBind( Intent in )
    { return m_bind ; }

    @Override
    @SuppressWarnings("unchecked") // We explicitly catch ClassCastException.
    public synchronized <T> T get( Class<T> cls )
    {
        T oInstance = null ;
        try { oInstance = (T)(this.m_mapSingletons.get(cls)) ; }
        catch( ClassCastException xCast )
        { Log.w( TAG, "Cannot cast singleton fetched from map." ) ; }
        return oInstance ;
    }

    @Override
    @SuppressWarnings("unchecked") // We explicitly catch ClassCastException.
    public synchronized <T> T put( Class<T> cls, T oInstance )
    {
        T oPrevious = null ;
        try { oPrevious = (T)(this.m_mapSingletons.put(cls, oInstance)) ; }
        catch( ClassCastException xCast )
        { Log.w( TAG, "Caught exception when casting previous instance." ) ; }
        return oPrevious ;
    }

    @Override
    public synchronized <T> T getOrPut( Class<T> cls, T oFallback )
    {
        if( m_mapSingletons.containsKey(cls) )
            return this.get(cls) ;
        else
        {
            this.put( cls, oFallback ) ;
            return oFallback ;
        }
    }

    @Override
    public synchronized <T> boolean hasInstanceFor( Class<T> cls )
    { return m_mapSingletons.containsKey(cls) ; }
}
