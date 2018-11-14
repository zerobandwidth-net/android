package net.zerobandwidth.android.lib.telephony;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.app.ManagersTest;
import net.zerobandwidth.android.lib.telephony.exceptions.ControllerConstructionException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Exercises the methods of {@link TelephonyController} that are easy to test.
 * Testing the more advanced telephony functions will require a bit more
 * research.
 * @since 0.2.1 (#53)
 */
@RunWith( AndroidJUnit4.class )
public class TelephonyControllerTest
{
	protected static final String LOG_TAG = ManagersTest.class.getSimpleName() ;

	protected final Context m_ctx = InstrumentationRegistry.getContext() ;

	/** Exercises {@link TelephonyController#getManager}. */
	@Test
	public void testGetManager()
	{ assertNotNull( TelephonyController.getManager(m_ctx) ) ; }

	@Test
	public void testConstruction()
	{
		ControllerConstructionException xCtor = null ;
		TelephonyController ctrl ;
		try { ctrl = new TelephonyController(null) ; }
		catch( ControllerConstructionException x ) { xCtor = x ; }
		assertNotNull(xCtor) ;

		xCtor = null ;
		ctrl = new TelephonyController( m_ctx ) ;

		String sMethodDump = ctrl.dumpDiscoveredMethods() ;
		assertNotNull(sMethodDump) ;
		// spot-check some basic methods
		assertTrue( sMethodDump.contains( "answerRingingCall" ) ) ;
		assertTrue( sMethodDump.contains( "call" ) ) ;
		assertTrue( sMethodDump.contains( "dial" ) ) ;
		assertTrue( sMethodDump.contains( "endCall" ) ) ;
		assertTrue( sMethodDump.contains( "isOffhook" ) ) ;
		assertTrue( sMethodDump.contains( "isRinging" ) ) ;
	}

	// TODO: Testing the other methods will require some more creativity.
}
