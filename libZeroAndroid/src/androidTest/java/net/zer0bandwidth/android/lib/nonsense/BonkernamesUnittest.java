package net.zer0bandwidth.android.lib.nonsense;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Exercises {@link BuildertextJavaclass}.
 * @since zer0bandwidth-net/android 0.1.5 (#33)
 */
@RunWith( AndroidJUnit4.class )
public class BonkernamesUnittest
{
	/** Number of silly names that are generated for each test. */
	public static final int ITERATIONS = 20 ;

	/**
	 * This method passes trivially; it just generates several names for review.
	 * Examine the Android logs to see these.
	 */
	@Test
	public void testGetString()
	{
		BuildertextJavaclass khaaaaan =
			new BuildertextJavaclass( InstrumentationRegistry.getContext() ) ;

		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			Log.d( BonkernamesUnittest.class.getSimpleName(),
					khaaaaan.getString() ) ;
		}
	}
}
