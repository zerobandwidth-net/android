package net.zer0bandwidth.android.lib.nonsense;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import net.zer0bandwidth.android.lib.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Exercises {@link NonsenseShipOrPub}.
 * @since zer0bandwidth-net/android 0.1.5 (#11)
 */
@RunWith( AndroidJUnit4.class )
public class NonsenseShipOrPubTest
{
	protected NonsenseShipOrPub getTestInstance()
	{ return new NonsenseShipOrPub( InstrumentationRegistry.getContext() ) ; }

	@Test
	public void testSetArticleChance()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		assertEquals( NonsenseShipOrPub.DEFAULT_ARTICLE_CHANCE,
				drivel.m_nArticleChance ) ;
		drivel.setArticleChance(50) ; // succeeds
		assertEquals( 50, drivel.m_nArticleChance ) ;
		drivel.setArticleChance(-1) ; // fails
		assertEquals( 50, drivel.m_nArticleChance ) ;
		drivel.setArticleChance(9000) ; // fails
		assertEquals( 50, drivel.m_nArticleChance ) ;
	}

	@Test
	public void testSetAdjectivesToResource()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		String[] asArticles = drivel.m_ctx.getResources()
				.getStringArray( R.array.asEnglishArticles ) ; // a small one
		drivel.setAdjectives( R.array.asEnglishArticles ) ;
		assertTrue( Arrays.equals( asArticles, drivel.m_asAdjectives ) ) ;
	}

	@Test
	public void testSetAdjectivesToCollection()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		String[] asArticles = drivel.m_ctx.getResources()
				.getStringArray( R.array.asEnglishArticles ) ;
		drivel.setAdjectives( Arrays.asList(asArticles) ) ;
		assertTrue( Arrays.equals( asArticles, drivel.m_asAdjectives ) ) ;
	}

	@Test
	public void testSetNounsToResource()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		String[] asArticles = drivel.m_ctx.getResources()
				.getStringArray( R.array.asEnglishArticles ) ; // a small one
		drivel.setNouns( R.array.asEnglishArticles ) ;
		assertTrue( Arrays.equals( asArticles, drivel.m_asNouns ) ) ;
	}

	@Test
	public void testSetNounsToCollection()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		String[] asArticles = drivel.m_ctx.getResources()
				.getStringArray( R.array.asEnglishArticles ) ;
		drivel.setNouns( Arrays.asList(asArticles) ) ;
		assertTrue( Arrays.equals( asArticles, drivel.m_asNouns ) ) ;
	}

	/** Number of times we should try random things. */
	protected static final int ITERATIONS = 25 ;

	@Test
	public void testGetArticle()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		drivel.setArticleChance(0) ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
			assertEquals( "", drivel.getArticle() ) ;
		drivel.setArticleChance(100) ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
			assertEquals( "The ", drivel.getArticle() ) ;
		drivel.setArticleChance(75) ;
		int nYep = 0 ;
		int nNope = 0 ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			String s = drivel.getArticle() ;
			if( "".equals(s) ) ++nNope ; else ++nYep ;
		}
		assertTrue( nYep > nNope ) ; // Not absolute, but probable.
	}

	/**
	 * This test always passes trivially, as it contains no asserts; instead,
	 * examine the captured Android logs to judge whether the method, on the
	 * whole, succeeded.
	 * @see NonsenseShipOrPub#getString()
	 */
	@Test
	public void testGetString()
	{
		NonsenseShipOrPub drivel = getTestInstance() ;
		for( int i = 0 ; i < ITERATIONS ; i++ )
		{
			Log.i( NonsenseShipOrPubTest.class.getCanonicalName(),
					drivel.getString()) ;
		}
	}
}
