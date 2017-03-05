package net.zerobandwidth.android.lib.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.* ;

/**
 * Exercises {@link IntentUtils}.
 * @since zerobandwidth-net/android 0.1.2 (#3)
 */
@RunWith( AndroidJUnit4.class )
public class IntentUtilsTest
{
	/** Exercises {@link IntentUtils#discoverAction}. */
	@Test
	public void testDiscoverAction()
	{
		final Intent sig = new Intent() ;
		sig.setAction( "foo" ) ;
		assertEquals( "foo", IntentUtils.discoverAction( sig ) ) ;
	}

	/** Exercises {@link IntentUtils#getBoundIntent(Context,Class)} */
	@Test
	public void testGetBoundIntent()
	{
		final Context ctx = InstrumentationRegistry.getContext() ;
		final Intent sig = IntentUtils.getBoundIntent( ctx, Activity.class ) ;
		assertEquals( Activity.class.getName(), sig.getComponent().getClassName() ) ;
	}

	/** Exercises {@link IntentUtils#getBoundIntent(Context,Class,String) */
	@Test
	public void testGetBoundIntentWithAction()
	{
		final Context ctx = InstrumentationRegistry.getContext() ;
		Intent sig = IntentUtils.getBoundIntent( ctx, Activity.class, "foo" ) ;
		assertEquals( Activity.class.getName(), sig.getComponent().getClassName() ) ;
		assertEquals( "foo", sig.getAction() ) ;
	}
}
