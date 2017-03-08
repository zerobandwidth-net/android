package net.zerobandwidth.android.lib.app;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.* ;

/**
 * Exercises {@link Managers}.
 * @since zerobandwidth-net/android 0.1.3 (#29)
 */
@RunWith( AndroidJUnit4.class )
public class ManagersTest
{
	protected static final String LOG_TAG = ManagersTest.class.getSimpleName() ;

	protected final Context m_ctx = InstrumentationRegistry.getContext() ;

	@Test
	public void testManagers()
	{
		for( Map.Entry<Class<?>,String> pair : Managers.REVERSE_MAP.entrySet() )
		{
			final Class<?> cls = pair.getKey() ;
			final String sTok = pair.getValue() ;
			final Object mgr = Managers.get( m_ctx, cls ) ;
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
}
