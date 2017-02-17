package net.zerobandwidth.android.lib.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.Vector;

/**
 * This class provides a simple, consistent implementation of a service binding,
 * so that neither the service nor the consumer of that service is required to
 * have its own implementation. The class provides basic methods to bind to, and
 * unbind from, the service; get the bound instance of the service; bind as a
 * listener to the connection itself.
 *
 * Note: In version 0.1.0 (#19), the class's members and inner classes were
 * fundamentally modified to provide tighter access to the connected service
 * class. While this makes the class more convenient to use, it is a flag day
 * beyond which consumers of this class will need to be modified to catch up.
 *
 * @since zerobandwidth-net/android 0.0.1 (#1)
 */
@SuppressWarnings("unused") // because it's a library
public class SimpleServiceConnection<S extends Service>
implements ServiceConnection
{
    /**
     * By default, the connection will bind at a level above the consumer, and
     * auto-create the service if it has not yet been started.
     * Used by the class's {@link #connect} methods in absence of any flag set
     * specified by the caller.
     */
    public static final int DEFAULT_BINDING_FLAGS =
            ( Context.BIND_ABOVE_CLIENT | Context.BIND_AUTO_CREATE ) ;

    /**
     * Because connections can take a while, the object that is waiting for the
     * connection to be completed should implement this interface to receive a
     * signal when the connection is established.
     * @since zerobandwidth-net/android 0.0.1 (#1)
     */
    @SuppressWarnings("unused") // because it's still a library
    public interface Listener<LS extends Service>
    {
        /** Invoked when the connection is bound. */
        void onServiceConnected( SimpleServiceConnection<LS> conn ) ;

        /** Invoked when the connection is unbound. */
        void onServiceDisconnected( SimpleServiceConnection<LS> conn ) ;
    }

    /**
     * The binder provided by the service needs to implement this interface,
     * which provides us with a standard way to reach into the service's own
     * public methods.
     * @since zerobandwidth-net/android 0.0.1 (#1)
     */
    public interface InstanceBinder<BS extends Service>
    extends IBinder
    {
        /** Provides the bound instance of the service. */
        BS getServiceInstance() ;
    }

    /**
     * Stores a consistent hint to the service class to which this connection
     * will bind.
     */
    protected Class<S> m_clsService = null ;
    /** The bound instance. */
    protected S m_srvInstance = null ;
    /** Indicates whether the connection is, indeed, bound to a service. */
    protected boolean m_bBound = false ;
    /** A collection of objects listening to this connection. */
    protected Vector<Listener<S>> m_vListeners = null ;

    /**
     * For log messages generated by methods of this class, or any descendant
     * class, use this method, which examines the class of the instance from
     * which it is called, and returns that class's simple name. This allows
     * other projects to extend this class without having to override this
     * method.
     * @return a logging tag for this connection object
     */
    protected final String getLogTag()
    { return this.getClass().getSimpleName() ; }

    /** Forbid use of the default constructor. */
    private SimpleServiceConnection() {}

    /**
     * Initializes a simple connection for a specified service class.
     * @param cls the service class
     */
    public SimpleServiceConnection( Class<S> cls )
    {
        m_clsService = cls ;
        this.initListeners() ;
    }

    /**
     * Initializes the pool of connection listeners.
     * @return the connection, for fluid invocations
     */
    protected SimpleServiceConnection<S> initListeners()
    {
        if( m_vListeners == null )
            m_vListeners = new Vector<>() ;
        else
            m_vListeners.clear() ;
        return this ;
    }

    /**
     * Accesses the pool of listeners, verifying first that such a pool has been
     * initialized.
     * @return a collection of listeners to this connection
     */
    protected Vector<Listener<S>> getListeners()
    {
        if( m_vListeners == null ) this.initListeners() ;
        return m_vListeners ;
    }

    /**
     * Registers a listener to this collection, if it is not already listening.
     * @param l the listener to be added
     * @return the connection, for fluid invocations
     */
    public SimpleServiceConnection<S> addListener( Listener<S> l )
    {
        if( ! this.getListeners().contains(l) )
            m_vListeners.add(l) ;
        return this ;
    }

    /**
     * Unregisters a listener to this collection, if it is found in the pool.
     * @param l the listener to be removed
     * @return the connection, for fluid invocations
     */
    public SimpleServiceConnection<S> removeListener( Listener<S> l )
    {
        if( this.getListeners().contains(l) )
            m_vListeners.remove(l) ;
        return this ;
    }

    /**
     * Accessor for the service class hint.
     * @return the service class hint
     */
    public Class<S> getServiceClass()
    { return m_clsService ; }

    /**
     * Indicates whether the connection is indeed bound to an instance of the
     * specified class.
     * @param cls the class to which we should compare our bound service
     * @return true iff the specified class is the bound service's class
     */
    public <C extends Service> boolean isServiceClass( Class<C> cls )
    { return( m_clsService.equals(cls) ) ; }

    /**
     * Indicates whether the connection is bound.
     * @return an indication that the service is bound
     */
    public synchronized boolean isBound()
    { return m_bBound ; }

    /**
     * Perhaps more useful than {@link #isBound}, this function verifies not
     * only that the connection is bound, but also that the persistent reference
     * to the service is not null.
     * @return an indication that the connection is, indeed, connected
     * @since zerobandwidth-net/android 0.0.1 (#5)
     */
    public synchronized boolean isConnected()
    { return ( m_bBound && m_srvInstance != null ) ; }

    /**
     * Accessor for the service instance, if the connection is bound.
     * @return the bound instance of the service, if any
     */
    public synchronized S getServiceInstance()
    { return m_srvInstance ; }

    /**
     * Attempts to connect to the service.
     * The caller should not process the return value of this method immediately
     * in order to access the service; rather, it should handle the consequences
     * of the connection by implementing {@link Listener#onServiceConnected}.
     * @param ctx the context in which to bind to the service
     * @param bmFlags a mask of optional binding flags; see {@link Context}
     * @return the connection, for fluid invocations
     * @throws IllegalArgumentException if the context is null
     */
    public synchronized SimpleServiceConnection<S> connect( Context ctx, int bmFlags )
    throws IllegalArgumentException
    {
        if( ctx == null )
        {
            throw new IllegalArgumentException(
                    "Cannot bind to service from a null context." ) ;
        }
        if( this.isConnected() )
        { Log.d( this.getLogTag(), "Already connected." ) ; }
        else
        { // Bind this connection to the service.
            Intent sig = new Intent( ctx, m_clsService ) ;
            ctx.bindService( sig, this, bmFlags ) ;
        }
        return this ;
    }

    /**
     * Attempts to connect to the service, using the default binding control
     * flags.
     * The caller should not process the return value of this method immediately
     * in order to access the service; rather, it should handle the consequences
     * of the connection by implementing {@link Listener#onServiceConnected}.
     * @param ctx the context in which to bind to the service
     * @return the connection, for fluid invocations
     * @see #DEFAULT_BINDING_FLAGS
     */
    public synchronized SimpleServiceConnection<S> connect( Context ctx )
    { return this.connect( ctx, DEFAULT_BINDING_FLAGS ) ; }

    /**
     * Attempts to break the connection to the service. Methods that tear down
     * the context in which the service was started (such as
     * {@link android.app.Activity#onStop}) should call this method to free the
     * service binding. The caller should not assume that the binding was
     * successfully released; any other consequences of releasing this binding
     * should be handled by implementing {@link Listener#onServiceDisconnected}.
     * @param ctx the context in which the binding should be broken
     * @return the connection, for fluid invocations
     */
    public synchronized SimpleServiceConnection<S> disconnect( Context ctx )
    {
        if( ctx == null )
        { // Return trivially but log a warning.
            Log.w( this.getLogTag(), (new StringBuilder())
                    .append( "Cannot disconnect from service [" )
                    .append( m_clsService.getCanonicalName() )
                    .append( "] from a null context. " )
                    .append( "A connection might be leaked!" )
                    .toString()
                );
            return this ;
        }
        if( ! this.isConnected() ) return this ; // trivially
        try { ctx.unbindService(this) ; }
        catch( RuntimeException x ) // includes IllegalArgumentException
        { Log.i( this.getLogTag(), "Service was already unbound." ) ; }
        return this ;
    }

    /**
     * Called by the Android OS when a the service accepts the connection's
     * binding. Notifies all of the connection's listeners that the binding has
     * been completed.
     * @param cn the name of the service component
     * @param binder a binder to the service
     */
    @SuppressWarnings( "unchecked" )     // We are indeed checking the typecast.
    @Override
    public void onServiceConnected( ComponentName cn, IBinder binder )
    {
        InstanceBinder<S> srvb ;
        try { srvb = ((InstanceBinder<S>)(binder)) ; }
        catch( ClassCastException xCast )
        {
            Log.w( this.getLogTag(), (new StringBuilder())
                    .append( "Class [" )
                    .append( m_clsService.getCanonicalName() )
                    .append( "] does not supply a compliant service binding." )
                    .toString()
                );
            return ;
        }
        m_srvInstance = srvb.getServiceInstance() ;
        m_bBound = true ;
        for( Listener<S> l : this.getListeners() )
            l.onServiceConnected(this) ;
    }

    /**
     * Called by the Android OS when a connection's binding to the service must
     * be broken. Notifies all of the connection's listeners that the binding
     * has been broken.
     * @param cn the name of the service component
     */
    @Override
    public void onServiceDisconnected( ComponentName cn )
    {
        m_srvInstance = null ;
        m_bBound = false ;
        for( Listener<S> l : this.getListeners() )
            l.onServiceDisconnected(this) ;
    }
}
