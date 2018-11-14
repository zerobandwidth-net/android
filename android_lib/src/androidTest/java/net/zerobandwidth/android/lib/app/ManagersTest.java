package net.zerobandwidth.android.lib.app;

import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStatsManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.midi.MidiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.UserManager;
import android.print.PrintManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.telephony.CarrierConfigManager;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.* ;

/**
 * Partially exercises {@link Managers}.
 * Because the logic of the class is so dependent on the compile-time
 * environment, it is not obviously feasible to provide full test coverage for
 * the class.
 * @since zerobandwidth-net/android 0.1.3 (#29)
 */
@RunWith( AndroidJUnit4.class )
public class ManagersTest
{
	protected static final String LOG_TAG = ManagersTest.class.getSimpleName() ;

	protected final Context m_ctx = InstrumentationRegistry.getContext() ;

	/**
	 * Exercises the "normal" operation of the {@link Managers#get} method. This
	 * test is limited in its ability to cover the code, because
	 */
	@Test
	public void testManagers()
	{
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
		{
			Log.i( LOG_TAG, "Testing for API 23+ ..." ) ;
			Exception xCaught = null ;
			try
			{ // Get instances of managers that were added in Android API 23.
				Managers.get( m_ctx, CarrierConfigManager.class ) ;
				Managers.get( m_ctx, FingerprintManager.class ) ;
				Managers.get( m_ctx, MidiManager.class ) ;
				Managers.get( m_ctx, NetworkStatsManager.class ) ;
			}
			catch( Exception x ) { xCaught = x ; }
			assertNull( xCaught ) ;
			return ;
		}

		// Otherwise, get instances of classes from the current, lower API.

		for( Map.Entry<Class<?>,String> pair : Managers.REVERSE_MAP.entrySet() )
		{
			final Class<?> cls = pair.getKey() ;
			final String sTok = pair.getValue() ;
			final Object mgr = Managers.tryToGet( m_ctx, cls ) ;
//			Log.d( LOG_TAG, this.dumpStatusBeforeAssert( cls, sTok, mgr ) ) ;
			assertTrue( mgr == null || cls.isAssignableFrom( mgr.getClass() ) ) ;
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Instance of [" ).append( cls )
					.append( "] referenced by token [" ).append( sTok )
					.append(( mgr == null ? "] IS NULL." : "] was obtained." ))
					.toString()
				);
		}
	}

	@SuppressWarnings( "unused" ) // Should be commented out unless things fail.
	private String dumpStatusBeforeAssert( Class<?> cls, String sTok, Object mgr )
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( "Class [" ).append( cls.getName() )
		  .append( "] Token [" ).append( sTok )
		  .append( "] Instance [" )
		  .append(( mgr == null ? "NULL]" : "NOT NULL]" ))
		  ;
		if( mgr != null )
		{
			sb.append( " Assignable [" )
			  .append(( mgr.getClass().isAssignableFrom(cls) ?
			        "TRUE]" : "FALSE]" ))
			  .append( " Manager Class Name [" )
			  .append( mgr.getClass().getName() ).append( "]" )
			  ;
		}
		return sb.toString() ;
	}

	/**
	 * Exercises {@link Managers#initReverseMap} by feeding it API version
	 * numbers explicitly.
	 * @since 0.2.1 (#53)
	 */
	@Test
	public void testInitReverseMap()
	{
		Managers.initReverseMap( Math.min( 22, Build.VERSION.SDK_INT ) ) ;

		// Check an obvious negative case.
		assertFalse( Managers.REVERSE_MAP.containsKey( String.class ) ) ;

		// Spot-check a manager from each API version.
		if( Build.VERSION.SDK_INT >= 22 )
			assertTrue( Managers.REVERSE_MAP.containsKey( UsageStatsManager.class ) ) ;
		if( Build.VERSION.SDK_INT >= 21 )
			assertTrue( Managers.REVERSE_MAP.containsKey( BatteryManager.class ) ) ;
		if( Build.VERSION.SDK_INT >= 19 )
			assertTrue( Managers.REVERSE_MAP.containsKey( PrintManager.class ) ) ;
		if( Build.VERSION.SDK_INT >= 18 )
			assertTrue( Managers.REVERSE_MAP.containsKey( BluetoothManager.class ) ) ;
		if( Build.VERSION.SDK_INT >= 17 )
			assertTrue( Managers.REVERSE_MAP.containsKey( UserManager.class ) ) ;
		if( Build.VERSION.SDK_INT >= 16 )
			assertTrue( Managers.REVERSE_MAP.containsKey( InputManager.class ) ) ;

		assertTrue( Managers.REVERSE_MAP.containsKey( AudioManager.class ) ) ;
	}
}
