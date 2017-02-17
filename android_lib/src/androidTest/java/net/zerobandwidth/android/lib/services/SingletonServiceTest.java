package net.zerobandwidth.android.lib.services;

import android.app.Service;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ServiceTestCase;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Exercises {@link SingletonService}.
 * <b>NOTE: Unit testing of services is currently broken. (#3)</b>
 * @since zerobandwidth-net/android 0.0.1 (#1)
 */
@SuppressWarnings("WeakerAccess")
@RunWith(AndroidJUnit4.class)
public class SingletonServiceTest
extends ServiceTestCase<SingletonService>
implements SimpleTestServiceConnection.Listener<SingletonService>
{
    public static final String LOG_TAG =
            SingletonServiceTest.class.getSimpleName() ;
/*
    public static class FirstContext
    extends Activity
    implements SingletonService.Connection.Listener
    {
        public SingletonService.Connection m_srvFirst = null ;

        public String m_sIdentifier = UUID.randomUUID().toString() ;

        public boolean m_bExecuted = false ;

        @Override
        public void onCreate( Bundle bndl )
        {
            super.onCreate(bndl) ;
            Log.d( LOG_TAG, "Starting context 1..." ) ;
            m_srvFirst = new SingletonService.Connection() ;
            m_srvFirst.addListener(this).connect(this) ;
            SingletonServiceTest.s_ctxOne = new WeakReference<>(this) ;
        }

        @Override
        public <LS extends Service> void onServiceConnected( SimpleServiceConnection<LS> conn )
        {
            Log.d( LOG_TAG, (new StringBuilder())
                    .append( "First context is writing string [" )
                    .append( m_sIdentifier )
                    .append( "] to the singleton service." )
                    .toString()
                );
            m_srvFirst.put( String.class, m_sIdentifier ) ;
            m_bExecuted = true ;
            SingletonServiceTest.verifyValuesAcrossContexts() ;
        }

        @Override
        public <LS extends Service> void onServiceDisconnected( SimpleServiceConnection<LS> conn )
        {}

        public FirstContext unbind()
        {
            Log.d( LOG_TAG, "Unbinding context 1." ) ;
            m_srvFirst.removeListener(this).disconnect(this) ;
            m_srvFirst = null ;
            return this ;
        }

        @Override
        public void onDestroy()
        {
            Log.d( LOG_TAG, "Destroying context 1." ) ;
            this.unbind() ;
            super.onDestroy() ;
        }
    }

    public static class SecondContext
    extends Activity
    implements SingletonService.Connection.Listener
    {
        public SingletonService.Connection m_srvSecond = null ;

        public Integer m_nIdentifier = (new Random()).nextInt(Integer.MAX_VALUE) ;

        public boolean m_bExecuted = false ;

        @Override
        protected void onCreate( Bundle bndl )
        {
            super.onCreate(bndl) ;
            Log.d( LOG_TAG, "Starting context 2..." ) ;
            SingletonServiceTest.s_ctxTwo = new WeakReference<>(this) ;
            m_srvSecond = new SingletonService.Connection() ;
            m_srvSecond.addListener(this).connect(this) ;
        }

        @Override
        public <LS extends Service> void onServiceConnected( SimpleServiceConnection<LS> conn )
        {
            Log.d( LOG_TAG, (new StringBuilder())
                    .append( "Second context is writing integer [" )
                    .append( m_nIdentifier )
                    .append( "] to the singleton service." )
                    .toString()
            );
            m_srvSecond.put( Integer.class, m_nIdentifier ) ;
            m_bExecuted = true ;
            SingletonServiceTest.verifyValuesAcrossContexts() ;
        }

        @Override
        public <LS extends Service> void onServiceDisconnected(SimpleServiceConnection<LS> conn)
        {}

        public SecondContext unbind()
        {
            Log.d( LOG_TAG, "Unbinding context 2." ) ;
            m_srvSecond.removeListener(this).disconnect(this) ;
            m_srvSecond = null ;
            return this ;
        }

        @Override
        public void onDestroy()
        {
            Log.d( LOG_TAG, "Destroying context 2." ) ;
            this.unbind() ;
            super.onDestroy() ;
        }
    }

    public static WeakReference<FirstContext> s_ctxOne = null ;
    public static String s_sFirst = null ;
    public static WeakReference<SecondContext> s_ctxTwo = null ;
    public static Integer s_nSecond = null ;
    protected static Semaphore s_lock = new Semaphore(1) ;
*/

    protected SimpleTestServiceConnection<SingletonService> m_srvOne = null ;
    protected SimpleTestServiceConnection<SingletonService> m_srvTwo = null ;
    protected String m_sOne = null ;
    protected Integer m_nTwo = null ;
    protected Semaphore m_lock = new Semaphore(0) ;

    public SingletonServiceTest()
    { super(SingletonService.class) ; }

    @Test
    public void testAcrossContexts()
    throws Exception
    {
        Log.d( LOG_TAG, "Starting testAcrossContexts()." ) ;
/*
        Context ctx = InstrumentationRegistry.getTargetContext() ;
        s_lock.acquire() ;
        ctx.startActivity( (new Intent( ctx, FirstContext.class ))
                .setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ) ;
        ctx.startActivity( (new Intent( ctx, SecondContext.class ))
                .setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ) ;
*/
        m_srvOne = new SimpleTestServiceConnection<>(SingletonService.class)
                .addListener(this).connectTest( new ServiceTestRule() ) ;
//        if( ! m_srvOne.isBound() ) fail( "Couldn't bind service 1." ) ;
        m_srvTwo = new SimpleTestServiceConnection<>(SingletonService.class)
                .addListener(this).connectTest( new ServiceTestRule() ) ;
//        if( ! m_srvTwo.isBound() ) fail( "Couldn't bind service 2." ) ;
        m_lock.acquire() ;
    }

    @Override
    public void onServiceConnected( SimpleServiceConnection<SingletonService> conn )
    {
        if( conn == m_srvOne )
        {
            m_sOne = UUID.randomUUID().toString() ;
            SingletonService srv = conn.getServiceInstance() ;
            srv.put( String.class, m_sOne ) ;
        }
        else if( conn == m_srvTwo )
        {
            m_nTwo = (new Random()).nextInt(Integer.MAX_VALUE) ;
            SingletonService srv = conn.getServiceInstance() ;
            srv.put( Integer.class, m_nTwo ) ;
        }
        this.verifyValuesAcrossContexts() ;
    }

    @Override
    public void onServiceDisconnected(SimpleServiceConnection<SingletonService> conn)
    {}

//    public static synchronized void verifyValuesAcrossContexts()
    public synchronized void verifyValuesAcrossContexts()
    {
        Log.d( LOG_TAG, "Called verifyValuesAcrossContexts()" ) ;
/*
        FirstContext ctxOne = s_ctxOne.get() ;
        SecondContext ctxTwo = s_ctxTwo.get() ;
        if( ctxOne != null && ctxOne.m_bExecuted
         && ctxTwo != null && ctxTwo.m_bExecuted )
        { // Results have been successfully captured; conclude the test.
            Log.d( LOG_TAG, "We have results!" ) ;
            assertEquals( s_sFirst, ctxOne.m_sIdentifier ) ;
            assertEquals( s_nSecond, ctxTwo.m_nIdentifier ) ;

            // Compare the result written by each context to the value returned
            // from the SingletonService as fetched by the other context.
            assertEquals( s_sFirst, ctxTwo.m_srvSecond.get(String.class) ) ;
            assertEquals( s_nSecond, ctxOne.m_srvFirst.get(Integer.class) ) ;

            ctxOne.unbind() ;
            ctxTwo.unbind() ;
            s_lock.release() ;
        }
        else
            Log.d( LOG_TAG, "No results yet!" ) ;
*/
        if( m_sOne != null && m_nTwo != null )
        {
            Log.d( LOG_TAG, "We have results!" ) ;
            SingletonService srvOne = m_srvOne.getServiceInstance() ;
            SingletonService srvTwo = m_srvTwo.getServiceInstance() ;
            if( srvOne == null || srvTwo == null )
            {
                Log.e( LOG_TAG, "Couldn't connect to at least one service." ) ;
                m_lock.release() ;
                fail( "Couldn't connect to at least one service.") ;
            }
            assertEquals( m_sOne, srvOne.get(String.class) ) ;
            assertEquals( m_nTwo, srvTwo.get(Integer.class) ) ;
            // and now the fun part
            assertEquals( m_nTwo, srvOne.get(Integer.class) ) ;
            assertEquals( m_sOne, srvTwo.get(String.class) ) ;
            m_lock.release() ;
        }
        else
            Log.d( LOG_TAG, "No results yet!" ) ;
    }
}
