package net.zerobandwidth.android.lib.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.* ;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Exercises {@link SingletonService}.
 * Note that the quality of Android {@code Service} testing is inherently
 * unstable, so a failure on any given run is not necessarily indicative of a
 * failure in the class's operation.
 * @since zerobandwidth-net/android 0.1.2 (#3)
 */
@RunWith( AndroidJUnit4.class )
public class SingletonServiceTest
{
    public static final String LOG_TAG =
            SingletonServiceTest.class.getSimpleName() ;

	/**
	 * Provides one of the multiple contexts across which we will test the
	 * service instance.
	 * @since zerobandwidth-net/android 0.1.2 (#3)
	 */
	public static abstract class SingletonServiceTestContext
	extends Activity
	implements SimpleServiceConnection.Listener<SingletonService>
	{
		public SimpleTestServiceConnection<SingletonService> m_conn = null ;

		public boolean m_bExecuted = false ;

		public SingletonServiceTestContext()
		{ super() ; }

		@Override
		public void onCreate( Bundle bndlState )
		{
			super.onCreate(bndlState) ;
			SingletonService.kickoff(this) ;
			try
			{
				m_conn = new SimpleTestServiceConnection<>( SingletonService.class )
					.addListener(this).connectTest( new ServiceTestRule() ) ;
			}
			catch( TimeoutException x )
			{ fail( "An activity's connection attempt timed out." ) ; }
		}

		@Override
		public void onServiceConnected( SimpleServiceConnection<SingletonService> conn )
		{
			m_conn = ((SimpleTestServiceConnection<SingletonService>)(conn)) ;
			this.putUniqueValue() ;
			m_bExecuted = true ;
			this.setVisible(false) ;
		}

		/** Implementations will write their distinctive value here. */
		public abstract void putUniqueValue() ;

		@Override
		public void onServiceDisconnected( SimpleServiceConnection<SingletonService> conn )
		{}

		public SingletonServiceTestContext unbind()
		{
			m_conn.removeListener(this).disconnect(this) ;
			m_conn = null ;
			return this ;
		}

		@Override
		public void onDestroy()
		{
			this.unbind() ;
			super.onDestroy() ;
		}
	}

	/**
	 * Provides one of the multiple contexts across which we will test the
	 * service instance. This context writes a string value to the service.
	 * @since zerobandwidth-net/android 0.1.2 (#3)
	 */
	public static class FirstContext extends SingletonServiceTestContext
    implements SimpleServiceConnection.Listener<SingletonService>
    {
        public String m_sIdentifier = UUID.randomUUID().toString() ;

	    public FirstContext() { super() ; }

        @Override
        public void onCreate( Bundle bndlState )
        {
            super.onCreate(bndlState) ;
            Log.d( LOG_TAG, "Starting context 1..." ) ;
            SingletonServiceTest.s_ctxOne = new WeakReference<>(this) ;
        }

        @Override
        public void putUniqueValue()
        {
            Log.d( LOG_TAG, (new StringBuilder())
                    .append( "First context is writing string [" )
                    .append( m_sIdentifier )
                    .append( "] to the singleton service." )
                    .toString()
                );
            m_conn.getServiceInstance().put( String.class, m_sIdentifier ) ;
        }
    }

	/**
	 * Provides one of the multiple contexts across which we will test the
	 * service instance. This context writes an integer value ot the service.
	 * @since zerobandwidth-net/android 0.1.2 (#3)
	 */
    public static class SecondContext extends SingletonServiceTestContext
    implements SimpleServiceConnection.Listener<SingletonService>
    {
        public Integer m_nIdentifier = (new Random()).nextInt(Integer.MAX_VALUE) ;

	    public SecondContext() { super() ; }

        @Override
        public void onCreate( Bundle bndlState )
        {
            super.onCreate(bndlState) ;
            Log.d( LOG_TAG, "Starting context 2..." ) ;
            SingletonServiceTest.s_ctxTwo = new WeakReference<>(this) ;
        }

        @Override
        public void putUniqueValue()
        {
            Log.d( LOG_TAG, (new StringBuilder())
                    .append( "Second context is writing integer [" )
                    .append( m_nIdentifier )
                    .append( "] to the singleton service." )
                    .toString()
            );
            m_conn.getServiceInstance().put( Integer.class, m_nIdentifier ) ;
        }
    }

	/** Persistent but weak reference to the first test context. */
    protected static WeakReference<FirstContext> s_ctxOne = null ;
	/** Persistent but weak reference to the second test context. */
    protected static WeakReference<SecondContext> s_ctxTwo = null ;

	/**
	 * The Android instrument that allows us to kick off the service inside the
	 * test.
	 */
	@Rule
	public final ServiceTestRule m_rule = new ServiceTestRule() ;

	/** A persistent connection to the service. */
	protected SimpleTestServiceConnection<SingletonService> m_conn ;
	/** A persistent reference to the service. */
	protected SingletonService m_srv = null ;

	/**
	 * Attempts to verify that the singletons written into the service are
	 * accessible even when written from multiple contexts.
	 *
	 * Note that, because of the way service testing works in Android, this test
	 * case is inherently unstable, and returns a lot of false negatives.
	 */
	@Test
	public void testAcrossContexts()
	{
		final int ACTIVITY_DELAY_MS = 7500 ; // Activity timeout delay. Tune to taste.
		final int SPIN_CYCLE = 5000 ; // Waiting for numbers to populate. Tune to taste.
		final int CONNECTION_DELAY_MS = 5000 ; // Service connection delay. Tune to taste.

		Log.d( LOG_TAG, "Kicking off service from test method..." ) ;
		final Context ctx = InstrumentationRegistry.getTargetContext() ;
		final Intent sigStart = ( new Intent( ctx, SingletonService.class ) )
				.setAction( SingletonService.ACTION_KICKOFF ) ;
		try { m_rule.startService( sigStart ) ; }
		catch( TimeoutException x )
		{ fail( "Initial kickoff action failed." ) ; }
		Log.d( LOG_TAG, "Binding to service from test method..." ) ;
		m_conn = new SimpleTestServiceConnection<>( SingletonService.class ) ;
		final SimpleServiceConnection.Listener<SingletonService> l =
			new SimpleServiceConnection.Listener<SingletonService>()
			{
				@Override
				public void onServiceConnected( SimpleServiceConnection<SingletonService> conn )
				{
					Log.i( LOG_TAG, "Service connected inside anonymous listener." ) ;
					SingletonServiceTest.this.m_srv = conn.getServiceInstance() ;
					Log.d( LOG_TAG, (
							SingletonServiceTest.this.m_srv == null ? "Service is null?!" : "Service is non-null." ) ) ;
				}

				@Override
				public void onServiceDisconnected( SimpleServiceConnection<SingletonService> conn )
				{}
			};
		try { m_conn.addListener(l).connectTest( m_rule ) ; }
		catch( TimeoutException x )
		{ fail( "Test method's connection attempt timed out." ) ; }
		Log.d( LOG_TAG, "Sleeping to wait for a connection..." ) ;
		try { Thread.sleep( CONNECTION_DELAY_MS ) ; }
		catch( InterruptedException x )
		{ fail( "Connection delay was interrupted." ) ; }
		if( m_srv == null ) fail( "Never connected to service." ) ;

		final Intent sigOne = new Intent( ctx, FirstContext.class )
				.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ;
		final Intent sigTwo = new Intent( ctx, SecondContext.class )
				.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ;
		ctx.startActivity( sigOne ) ;
		ctx.startActivity( sigTwo ) ;
		try { Thread.sleep( ACTIVITY_DELAY_MS ) ; }
		catch( InterruptedException x )
		{ fail( "Activity creation delay was interrupted." ) ; }
		if( s_ctxOne == null || s_ctxOne.get() == null )
			fail( "First context is dead. This might simply be a lost race condition." ) ;
		if( s_ctxTwo == null || s_ctxTwo.get() == null )
			fail( "Second context is dead. This might simply be a lost race condition." ) ;
		int nSpin = 0 ;
		Log.d( LOG_TAG, "Spin cycle..." ) ;
		//noinspection StatementWithEmptyBody
		while( ! s_ctxOne.get().m_bExecuted && ! s_ctxTwo.get().m_bExecuted && nSpin++ < SPIN_CYCLE ) ;
		if( nSpin == SPIN_CYCLE ) fail( "Never executed commands." ) ;
		else
		{
			Log.d( LOG_TAG, (new StringBuilder())
					.append( "Spin cycle ended after [" )
					.append( nSpin )
					.append( "] iterations." )
					.toString()
				);
		}

		// Now we can finally try the cross-context test.
		Log.d( LOG_TAG, (new StringBuilder())
				.append( "*** RESULT ***\n" )
				.append( "Context 1 identifier: " )
				.append( s_ctxOne.get().m_sIdentifier )
				.append( "\nService string:       " )
				.append( m_srv.get( String.class ) )
				.append( "\nContext 2 identifier: " )
				.append( s_ctxTwo.get().m_nIdentifier )
				.append( "\nService integer:      " )
				.append( m_srv.get( Integer.class ) )
				.toString()
			);
		assertEquals( s_ctxOne.get().m_sIdentifier,
				m_srv.get( String.class ) ) ;
		assertEquals( s_ctxTwo.get().m_nIdentifier,
				m_srv.get( Integer.class ) ) ;
	}

	/**
	 * Exercises the more exotic methods of {@link SingletonService}.
	 */
	@Test
	public void testNonMapMethods()
	{
		SingletonService srv = new SingletonService() ;
		srv.onCreate() ;
		assertEquals( "foo", srv.getOrPut( String.class, "foo" ) ) ;
		assertEquals( "foo", srv.getOrPut( String.class, "bar" ) ) ;
		assertEquals( "foo", srv.getOrPut( String.class, "baz" ) ) ;
		assertTrue( srv.hasInstanceOf( String.class ) ) ;
		assertFalse( srv.hasInstanceOf( Activity.class ) ) ;
	}

	/**
	 * Tears down the object references maintained by the test class.
	 */
	@After
	public void teardown()
	{
		Log.d( LOG_TAG, "Tearing down the test." ) ;
		if( s_ctxOne != null && s_ctxOne.get() != null ) s_ctxOne.get().finish() ;
		if( s_ctxTwo != null && s_ctxTwo.get() != null ) s_ctxTwo.get().finish() ;
		s_ctxOne = null ; s_ctxTwo = null ;
	}
}
