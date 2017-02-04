package net.zerobandwidth.android.lib.nonsense;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import org.junit.Test ;
import org.junit.Before ;
import static org.junit.Assert.* ;

/**
 * Exercises {@link NonsenseBuilder}.
 *
 * @since zerobandwidth-net/android 0.0.1 (#7)
 */
public class NonsenseBuilderTest
extends AndroidTestCase
{
	public static final String LOG_TAG =
			NonsenseBuilderTest.class.getSimpleName() ;

	/**
	 * Exposes additional information about a {@link NonsenseBuilder} to the
	 * test cases in {@link NonsenseBuilderTest}.
	 * @since zerobandwidth-net/android 0.0.1 (#7)
	 */
	public static class ExposedNonsenseBuilder
	extends NonsenseBuilder
	{
		public ExposedNonsenseBuilder( Context ctx )
		{ super(ctx) ; }

		public ExposedNonsenseBuilder( Context ctx, NonsenseBuilder.Configuration cfg )
		{ super(ctx,cfg) ; }

		public Context getContext()
		{ return m_ctx ; }

		public NonsenseBuilder.Configuration getConfiguration()
		{ return m_cfg ; }

		public String getSubject()
		{ return m_sSubject ; }

		public String getSubjectAdjective()
		{ return m_sSubjectAdjective ; }

		public String getVerb()
		{ return m_sVerb ; }

		public String getAdverb()
		{ return m_sVerbAdverb ; }

		public String getObject()
		{ return m_sObject ; }

		public String getObjectAdjective()
		{ return m_sObjectAdjective ; }

		public String getObjectModifier()
		{ return m_sObjectModifier ; }
	}

	/** The builder instance used in each test. */
	protected ExposedNonsenseBuilder m_xyzzy = null ;

	@Before
	@Override
	public void setUp()
	{
		m_xyzzy = new ExposedNonsenseBuilder( mContext ) ;
	}

	/**
	 * Exercises the context-only constructor, verifying that the context
	 * supplied in {@link #setUp)} is the same one reported by the builder.
	 *
	 * Since this constructor invokes {@link NonsenseBuilder#setContext}, there
	 * is no need to separately test that method.
	 */
	public void testConstructor()
	{
		assertEquals( mContext, m_xyzzy.getContext() ) ;
	}

	/**
	 * Exercises the full constructor, and also the builder-based paradigm for
	 * passing in a {@link NonsenseBuilder.Configuration} instance.
	 *
	 * Since the constructor invokes {@link NonsenseBuilder#setConfiguration},
	 * there is no need to separately test that method.
	 */
	public void testConfiguredConstructor()
	{
		m_xyzzy = new ExposedNonsenseBuilder( mContext,
				(new NonsenseBuilder.Configuration())
					.setSubjectAdjectiveChance( 25 )
					.setAdverbChance( NonsenseBuilder.Configuration.NEVER )
					.setObjectAdjectiveChance( NonsenseBuilder.Configuration.ALWAYS )
					.setObjectPhraseChance( 42 )
			);
		final NonsenseBuilder.Configuration cfg = m_xyzzy.getConfiguration() ;
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
