package net.zerobandwidth.android.lib.nonsense;

import android.content.Context;

import net.zerobandwidth.android.lib.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Builds a random name of a ship&hellip; or a pub&hellip; or something.
 *
 * <p>Both ships and pubs seem to have names that follow the general pattern of
 * "The &lt;adjective&gt; &lt;noun&gt;", like "The Salty Dog" or "The Mourning
 * Glory" or "The Rapacious Tardigrade". OK, maybe not the last one. Anyway,
 * since {@link NonsenseBuilder} worked out so well, and this is actually a
 * <i>simpler</i> algorithm, it seemed only right to go ahead with this now.</p>
 *
 * <p>As with {@code NonsenseBuilder}, this class makes use of two of the array
 * resources that are included with the library:</p>
 *
 * <ul>
 *     <li>{@link R.array#asNonsenseAdjectives}</li>
 *     <li>{@link R.array#asNonsenseNouns}</li>
 * </ul>
 *
 * <p>Applications using this class may choose to have overrides for either or
 * both of these string resources, in order to customize the text that might be
 * rendered in the randomized names.</p>
 *
 * @since zerobandwidth-net/android 0.1.5 (#11)
 */
public class NonsenseShipOrPub
implements NonsenseGenerator
{
	/**
	 * The internal RNG of the builder, used to select each token in the name.
	 */
	protected static final Random RANDOM = new Random() ;

	/**
	 * By default, this class will always begin the name with a "The". Use the
	 *
	 */
	protected static final int DEFAULT_ARTICLE_CHANCE = 100 ;

	/** A context in which the string resources are available. */
	protected Context m_ctx = null ;

	/**
	 * The percent chance that the builder will begin the random name with
	 * "The". By default, this chance is 100%; consumers may change this by
	 * calling {@link #setArticleChance(int)}.
	 */
	protected int m_nArticleChance = DEFAULT_ARTICLE_CHANCE ;

	/**
	 * A list of adjectives. This may be overridden with {@link #setAdjectives}.
	 */
	protected String[] m_asAdjectives = null ;

	/**
	 * A list of nouns. This may be overridden with {@link #setNouns}.
	 */
	protected String[] m_asNouns = null ;

	/**
	 * A constructor which sets the resource context.
	 * @param ctx a context in which string resources are available
	 */
	public NonsenseShipOrPub( Context ctx )
	{
		this.setContext( ctx )
		    .setAdjectives( R.array.asNonsenseAdjectives )
		    .setNouns( R.array.asNonsenseNouns )
		    ;
	}

	/**
	 * Sets the resource context.
	 * @param ctx the context in which string resources are available
	 * @return (fluid)
	 */
	@Override
	public NonsenseShipOrPub setContext( Context ctx )
	{ m_ctx = ctx ; return this ; }

	/**
	 * Sets the chance that the builder will begin the name with "The". By
	 * default, this chance is 100%.
	 * If the parameter given is less than 0 or greater than 100, then the new
	 * value will be silently ignored.
	 * @param nChance an integer between 0 and 100 (inclusive)
	 * @return (fluid)
	 */
	public NonsenseShipOrPub setArticleChance( int nChance )
	{
		if( nChance < 0 || nChance > 100 ) return this ; // trivially
		m_nArticleChance = nChance ;
		return this ;
	}

	/**
	 * Defines the set of adjectives that should be considered when generating
	 * the name.
	 * @param resAdjectives the resource ID of an array of adjectives
	 * @return (fluid)
	 */
	public NonsenseShipOrPub setAdjectives( int resAdjectives )
	{
		m_asAdjectives = m_ctx.getResources().getStringArray( resAdjectives ) ;
		return this ;
	}

	/**
	 * Defines the set of adjectives that should be considered when generating
	 * the name.
	 * @param asAdjectives a collection of adjective strings
	 * @return (fluid)
	 */
	public NonsenseShipOrPub setAdjectives( Collection<String> asAdjectives )
	{
		m_asAdjectives =
				asAdjectives.toArray( new String[asAdjectives.size()] ) ;
		return this ;
	}

	/**
	 * Defines the set of nouns that should be considered when generating the
	 * name.
	 * @param resNouns the resource ID of an array of nouns
	 * @return (fluid)
	 */
	public NonsenseShipOrPub setNouns( int resNouns )
	{
		m_asNouns = m_ctx.getResources().getStringArray( resNouns ) ;
		return this ;
	}

	/**
	 * Defines the set of nouns that should be considered when generating the
	 * name.
	 * @param asNouns a collection of noun strings
	 * @return (fluid)
	 */
	public NonsenseShipOrPub setNouns( Collection<String> asNouns )
	{
		m_asNouns = asNouns.toArray( new String[asNouns.size()] ) ;
		return this ;
	}

	/**
	 * Generates the name.
	 * @return the name of a ship, or a pub, or whatever
	 */
	@Override
	public String getString()
	{
		StringBuilder sb = new StringBuilder() ;

		sb.append( this.getArticle() )
		  .append( this.getRandomNonsense( m_asAdjectives ) )
		  .append( " " )
		  .append( this.getRandomNonsense( m_asNouns ) )
		  ;

		return sb.toString() ;
	}

	/**
	 * Might start us off with ah "The". Or it might not.
	 * @return "The " or an empty string
	 */
	protected String getArticle()
	{
		if( m_nArticleChance == 0 ) return "" ; // trivially
		if( m_nArticleChance < 100 && RANDOM.nextInt(100) >= m_nArticleChance )
			return "" ; // trivially

		return "The " ;
	}

	protected String getRandomNonsense( String[] asNonsense )
	{
		String sNonsense = asNonsense[ RANDOM.nextInt( asNonsense.length ) ] ;
		String[] asTokens = sNonsense.split( "\\w" ) ;
		StringBuilder sb = new StringBuilder() ;
		for( String sToken : asTokens )
		{
			if( sb.length() > 0 ) sb.append( " " ) ;

		}
		return sb.toString() ;
	}


}
