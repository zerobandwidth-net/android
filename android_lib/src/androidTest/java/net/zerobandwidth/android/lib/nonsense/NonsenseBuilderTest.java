package net.zerobandwidth.android.lib.nonsense;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before ;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.* ;

/**
 * Exercises {@link NonsenseBuilder}.
 *
 * @since zerobandwidth-net/android 0.0.1 (#7)
 */
@RunWith( AndroidJUnit4.class )
public class NonsenseBuilderTest
{
	public static final String LOG_TAG =
			NonsenseBuilderTest.class.getSimpleName() ;

	/**
	 * Context in which tests are executed.
	 * @since zerobandwidth-net/android 0.1.3 (#30)
	 */
	protected Context m_ctx ;

	/** The builder instance used in each test. */
	protected NonsenseBuilder m_xyzzy = null ;

	@Before
	public void setUp()
	{
		m_ctx = InstrumentationRegistry.getTargetContext() ;
		m_xyzzy = new NonsenseBuilder( m_ctx ) ;
	}

	/**
	 * Exercises the context-only constructor, verifying that the context
	 * supplied in {@link #setUp} is the same one reported by the builder.
	 *
	 * Since this constructor invokes {@link NonsenseBuilder#setContext}, there
	 * is no need to separately test that method.
	 */
	@Test
	public void testConstructor()
	{ assertEquals( m_ctx, m_xyzzy.m_ctx ) ; }

	/**
	 * Exercises the full constructor, and also the builder-based paradigm for
	 * passing in a {@link NonsenseBuilder.Configuration} instance.
	 *
	 * Since the constructor invokes {@link NonsenseBuilder#setConfiguration},
	 * there is no need to separately test that method.
	 */
	@Test
	public void testConfiguredConstructor()
	{
		m_xyzzy = new NonsenseBuilder( m_ctx,
				(new NonsenseBuilder.Configuration())
					.setSubjectAdjectiveChance( 25 )
					.setAdverbChance( NonsenseBuilder.Configuration.NEVER )
					.setObjectAdjectiveChance( NonsenseBuilder.Configuration.ALWAYS )
					.setObjectPhraseChance( 42 )
			);
		final NonsenseBuilder.Configuration cfg = m_xyzzy.m_cfg ;
		assertEquals( 25, cfg.m_nSubjectAdjectiveChance ) ;
		assertEquals( NonsenseBuilder.Configuration.NEVER,
					cfg.m_nAdverbChance ) ;
		assertEquals( NonsenseBuilder.Configuration.ALWAYS,
					cfg.m_nObjectAdjectiveChance ) ;
		assertEquals( 42, cfg.m_nObjectPhraseChance ) ;
	}

	/**
	 * Exercises the locking methods for each of the sentence segments.
	 */
	@Test
	public void testLockingMethods()
	{
		m_xyzzy.setSubject( "the fox" )
		       .setSubjectAdjective( "quick brown" )
		       .setVerb( "jumps over" )
		       .setAdverb( "always" )
		       .setObject( "the dog" )
		       .setObjectAdjective( "lazy" )
		       .setObjectModifier( "in the proverb" )
		       ;
		final String sOutput = m_xyzzy.getString() ;
		Log.d( LOG_TAG, sOutput ) ;
		assertEquals(
				"The quick brown fox always jumps over the lazy dog in the proverb.",
				sOutput ) ;
	}

	/**
	 * This method makes no assertions, and will trivially pass. It exercises
	 * the {@link NonsenseBuilder#getString()} method several times, using the
	 * default configuration, and dumps the output to the logs. Only by
	 * examining the output will you be able to see the strings that are
	 * generated, which, in Android Studio, is far more troublesome than you'd
	 * think.
	 */
	@Test
	public void testRandomOutputs()
	{
		final int ITERATIONS = 25 ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			final String sOutput = m_xyzzy.getString() ;
			Log.d( LOG_TAG, sOutput ) ;
		}
		assertTrue(true) ;
	}
}
