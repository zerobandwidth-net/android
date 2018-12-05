package net.zer0bandwidth.android.lib.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.* ;

/**
 * Exercises {@link IntentUtils}.
 * @since zer0bandwidth-net/android 0.1.2 (#3)
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

	/** Exercises {@link IntentUtils#getBoundIntent(Context,Class,String)} */
	@Test
	public void testGetBoundIntentWithAction()
	{
		final Context ctx = InstrumentationRegistry.getContext() ;
		Intent sig = IntentUtils.getBoundIntent( ctx, Activity.class, "foo" ) ;
		assertEquals( Activity.class.getName(), sig.getComponent().getClassName() ) ;
		assertEquals( "foo", sig.getAction() ) ;
	}

	/**
	 * Set of actions to be written into an {@link IntentFilter}.
	 * @see #testGetActionListIntentFilter()
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected static final String[] TEST_INTENT_FILTER_ACTIONS =
			{ "FOO", "BAR", "BAZ" } ;

	/**
	 * Fake authority string to be prepended to actions in an
	 * {@link IntentFilter}.
	 * @see #testGetActionListIntentFilter()
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected static final String TEST_INTENT_FILTER_AUTHORITY =
			"org.totallyfake.myservice" ;

	/**
	 * Exercises the intent filter generators.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	@Test
	public void testGetActionListIntentFilter()
	{
		IntentFilter filter = IntentUtils.getActionListIntentFilter(
				TEST_INTENT_FILTER_AUTHORITY, TEST_INTENT_FILTER_ACTIONS ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.FOO" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.BAR" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.BAZ" ) ) ;

		ArrayList<String> asActionList = new ArrayList<>() ;
		Collections.addAll( asActionList, TEST_INTENT_FILTER_ACTIONS ) ;
		asActionList.add( "BLARGH" ) ;
		filter = IntentUtils.getActionListIntentFilter(
				TEST_INTENT_FILTER_AUTHORITY, asActionList ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.FOO" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.BAR" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.BAZ" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.action.BLARGH" ) ) ;

		String sFormat = "%s.custom.action.%s" ;
		filter = IntentUtils.getActionListIntentFilter( sFormat,
				TEST_INTENT_FILTER_AUTHORITY, TEST_INTENT_FILTER_ACTIONS ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.FOO" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.BAR" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.BAZ" ) ) ;

		filter = IntentUtils.getActionListIntentFilter( sFormat,
				TEST_INTENT_FILTER_AUTHORITY, asActionList ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.FOO" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.BAR" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.BAZ" ) ) ;
		assertTrue( filter.matchAction(
				"org.totallyfake.myservice.custom.action.BLARGH" ) ) ;
	}

	/**
	 * Negative tests for the intent filter generators.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	@SuppressWarnings("ConstantConditions") // That's the point of the test.
	@Test
	public void testGetActionListIntentFilterExceptions()
	{
		IntentUtils.FilterFormatException xFilter = null ;
		String[] asNullArray = null ;
		List<String> asNullList = null ;
		String[] asEmptyArray = new String[] {} ;
		List<String> asEmptyList = new ArrayList<>() ;

		try { IntentUtils.getActionListIntentFilter( null, asNullArray ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for null domain
		assertEquals( IntentUtils.FilterFormatException.EMPTY_DOMAIN,
				xFilter.getMessage() ) ;

		xFilter = null ;
		try
		{ IntentUtils.getActionListIntentFilter( "", asNullArray ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for empty authority
		assertEquals( IntentUtils.FilterFormatException.EMPTY_DOMAIN,
				xFilter.getMessage() ) ;

		xFilter = null ;
		try
		{ IntentUtils.getActionListIntentFilter( "foo", asNullArray ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for null action array
		assertEquals( IntentUtils.FilterFormatException.EMPTY_ACTION_LIST,
				xFilter.getMessage() ) ;

		xFilter = null ;
		try
		{ IntentUtils.getActionListIntentFilter( "foo", asNullList ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for null action array
		assertEquals( IntentUtils.FilterFormatException.EMPTY_ACTION_LIST,
				xFilter.getMessage() ) ;

		xFilter = null ;
		try
		{ IntentUtils.getActionListIntentFilter( "foo", asEmptyArray ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for null action array
		assertEquals( IntentUtils.FilterFormatException.EMPTY_ACTION_LIST,
				xFilter.getMessage() ) ;

		xFilter = null ;
		try
		{ IntentUtils.getActionListIntentFilter( "foo", asEmptyList ) ; }
		catch( IntentUtils.FilterFormatException x ) { xFilter = x ; }
		assertNotNull( xFilter ) ; // thrown for null action array
		assertEquals( IntentUtils.FilterFormatException.EMPTY_ACTION_LIST,
				xFilter.getMessage() ) ;
	}
}
