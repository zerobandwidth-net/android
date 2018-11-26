package net.zer0bandwidth.android.lib.nonsense;

import android.content.Context;

import net.zer0bandwidth.android.lib.R;

import java.util.Random;

/**
 * The canonical implementation of {@link NonsenseGenerator}.
 *
 * <p>The builder will assemble a sentence of the following form:
 * <i>adjective</i> <b>subject</b> <i>adverb</i> <b>verb</b> <i>adjective</i>
 * <b>object</b> <i>phrase</i>. The terms listed in bold (subject, verb, object)
 * will always be rendered, while the terms in italics (adjectives, adverbs,
 * additional phrases) will be added randomly based on the builder's
 * configuration, which can be tuned by creating a custom instance of the
 * {@link NonsenseBuilder.Configuration} inner class.</p>
 *
 * <p>The class uses the following string resources to get its random words:</p>
 *
 * <ul>
 *     <li>{@link R.array#asNonsenseNouns}</li>
 *     <li>{@link R.array#asNonsenseVerbs}</li>
 *     <li>{@link R.array#asNonsenseAdjectives}</li>
 *     <li>{@link R.array#asNonsenseAdverbs}</li>
 *     <li>{@link R.array#asNonsensePhrases}</li>
 * </ul>
 *
 * <p>Applications using this class may choose to have overrides for any or all
 * of these string resources, in order to customize the text that might be
 * rendered in the randomized sentences.</p>
 *
 * @since zer0bandwidth-net/android 0.0.1 (#7)
 */
@SuppressWarnings({ "unused", "WeakerAccess" })            // This is a library.
public class NonsenseBuilder
implements NonsenseGenerator
{
	/**
	 * This class controls aspects of a {@link NonsenseBuilder} related to the
	 * probability of certain random features.
	 *
	 * To set non-default values, construct an instance of the class, and then
	 * use its {@code set*()} methods to change any or all of the desired
	 * settings. This may be done inline as follows:
	 *
	 * <pre>
	 *     NonsenseBuilder xyzzy = new NonsenseBuilder( ctx,
	 *             (new NonsenseBuilder.Configuration())
	 *                 .setSubjectAdjectiveChance(25)
	 *                 .setAdverbChance( NonsenseBuilder.Configuration.ALWAYS )
	 *                 .setObjectAdjectiveChance(42)
	 *                 .setObjectPhraseChance( NonsenseBuilder.Configuration.NEVER )
	 *         );
	 * </pre>
	 *
	 * @since zer0bandwidth-net/android 0.0.1 (#7)
	 */
	public static class Configuration
	{
		/**
		 * When specifying a probability, this constant indicates that a word
		 * should <i>always</i> be added to the sentence.
		 */
		public static final int ALWAYS = 100 ;

		/**
		 * When specifying a probability, this constant indicates that a word
		 * should <i>never</i> be added to the sentence.
		 */
		public static final int NEVER = 0 ;

		/**
		 * The percentage chance that an adjective will be added to modify the
		 * subject of the sentence.
		 */
		public int m_nSubjectAdjectiveChance = 50 ;
		/**
		 * The percentage chance that an adverb will be added to modify the verb
		 * of the sentence.
		 */
		public int m_nAdverbChance = 50 ;
		/**
		 * The percentage chance that an adjective will be added to modify the
		 * object of the sentence.
		 */
		public int m_nObjectAdjectiveChance = 50 ;
		/**
		 * The percentage chance that a prepositional phrase will be added to
		 * modify the object of the sentence.
		 */
		public int m_nObjectPhraseChance = 50 ;

		/**
		 * Sets the probability that an adjective will be added to modify the
		 * subject of the sentence.
		 * If the parameter given is less than 0 or greater than 100, then the
		 * new value will be silently ignored.
		 * @param n an integer between 0 and 100, inclusive
		 * @return (fluid)
		 */
		public Configuration setSubjectAdjectiveChance( int n )
		{
			if( n < 0 || n > 100 ) return this ; // trivially
			m_nSubjectAdjectiveChance = n ;
			return this ;
		}

		/**
		 * Sets the probability that an adverb will be added to modify the verb
		 * of the sentence.
		 * If the parameter given is less than 0 or greater than 100, then the
		 * new value will be silently ignored.
		 * @param n an integer between 0 and 100, inclusive
		 * @return (fluid)
		 */
		public Configuration setAdverbChance( int n )
		{
			if( n < 0 || n > 100 ) return this ; // trivially
			m_nAdverbChance = n ;
			return this ;
		}

		/**
		 * Sets the probability that an adjective will be added to modify the
		 * object of the sentence.
		 * If the parameter given is less than 0 or greater than 100, then the
		 * new value will be silently ignored.
		 * @param n an integer between 0 and 100, inclusive
		 * @return (fluid)
		 */
		public Configuration setObjectAdjectiveChance( int n )
		{
			if( n < 0 || n > 100 ) return this ; // trivially
			m_nObjectAdjectiveChance = n ;
			return this ;
		}

		/**
		 * Sets the probability that a prepositional phrase will be added to
		 * modify the object of the sentence.
		 * If the parameter given is less than 0 or greater than 100, then the
		 * new value will be silently ignored.
		 * @param n an integer between 0 and 100, inclusive
		 * @return (fluid)
		 */
		public Configuration setObjectPhraseChance( int n )
		{
			if( n < 0 || n > 100 ) return this ;
			m_nObjectPhraseChance = n ;
			return this ;
		}
	}

	/**
	 * A canonical instance of a {@link NonsenseBuilder.Configuration}, using
	 * the default values defined in that inner class.
	 */
	protected static final Configuration CANONICAL_CONFIGURATION =
			new Configuration() ;

	/**
	 * The internal RNG of the builder, used to determine the value selected for
	 * each item in the sentence, and the presence of certain optional items.
	 */
	protected static final Random RANDOM = new Random() ;

	/**
	 * A context in which string resources are available.
	 */
	protected Context m_ctx = null ;

	/**
	 * The configuration settings that will control the random elements of the
	 * builder. This defaults to the {@link #CANONICAL_CONFIGURATION}.
	 */
	protected Configuration m_cfg = CANONICAL_CONFIGURATION ;

	/**
	 * (noun) The subject of the sentence.
	 * This is always set by the builder.
	 */
	protected String m_sSubject = null ;
	/**
	 * (adjective) A modifier of the subject.
	 * This is randomly set by the builder.
	 */
	protected String m_sSubjectAdjective = null ;
	/**
	 * (verb) The action verb in the sentence.
	 * This is always set by the builder.
	 */
	protected String m_sVerb = null ;
	/**
	 * (adverb) A modifier of the sentence's verb.
	 * This is randomly set by the builder.
	 */
	protected String m_sVerbAdverb = null ;
	/**
	 * (noun) The object of the sentence.
	 * This is always set by the builder.
	 */
	protected String m_sObject = null ;
	/**
	 * (adjective) A modifier of the object of the sentence.
	 * This is randomly set by the builder.
	 */
	protected String m_sObjectAdjective = null ;
	/**
	 * (prepositional phrase) A phrase further modifying the object of the
	 * sentence.
	 * This is randomly set by the builder.
	 */
	protected String m_sObjectModifier = null ;

	/**
	 * A constructor which sets the resource context.
	 * @param ctx a context in which string resources are available
	 */
	public NonsenseBuilder( Context ctx )
	{ this.setContext( ctx ) ; }

	/**
	 * A constructor which sets both the resource context and a custom
	 * configuration to control the random aspects of the builder.
	 * @param ctx a context in which string resources are available
	 * @param cfg the configuration parameters of the builder
	 */
	public NonsenseBuilder( Context ctx, NonsenseBuilder.Configuration cfg )
	{ this.setContext( ctx ).setConfiguration( cfg ) ; }

	/**
	 * Sets the configuration parameters of the builder.
	 * @param cfg specifies the randomization parameters for the builder
	 * @return (fluid)
	 */
	public NonsenseBuilder setConfiguration( NonsenseBuilder.Configuration cfg )
	{
		if( cfg == null ) m_cfg = CANONICAL_CONFIGURATION ;
		else m_cfg = cfg ;
		return this ;
	}

	@Override
	public NonsenseBuilder setContext( Context ctx )
	{ m_ctx = ctx ; return this ; }

	/**
	 * Locks a value for the subject of the sentence.
	 *
	 * If {@code null} is specified, then the builder's subject will be
	 * "cleared", meaning that it will be chosen at random.
	 *
	 * @param sSubject a specific subject for the sentence, or {@code null} to
	 *                 ensure that the subject is randomized
	 * @return (fluid)
	 */
	public NonsenseBuilder setSubject( String sSubject )
	{ m_sSubject = sSubject ; return this ; }

	/**
	 * Locks a value for the adjective modifying the subject of the sentence.
	 *
	 * If {@code null} is specified, then the builder might or might not add a
	 * random adjective, as specified by
	 * {@link Configuration#m_nSubjectAdjectiveChance}.
	 *
	 * @param sAdj a specific adjective to modify the subject of the sentence,
	 *             or {@code null} to randomize the presence and value of the
	 *             adjective
	 * @return (fluid)
	 */
	public NonsenseBuilder setSubjectAdjective( String sAdj )
	{ m_sSubjectAdjective = sAdj ; return this ; }

	/**
	 * Locks a value for the verb of the sentence.
	 *
	 * If {@code null} is specified, then the builder's verb will be "cleared",
	 * meaning that it will be chosen at random.
	 *
	 * @param sVerb a specific verb for the sentence, or {@code null} to ensure
	 *              that the verb is randomized
	 * @return (fluid)
	 */
	public NonsenseBuilder setVerb( String sVerb )
	{ m_sVerb = sVerb ; return this ; }

	/**
	 * Locks a value for the adverb modifying the verb of the sentence.
	 *
	 * If {@code null} is specified, then the builder might or might not add a
	 * random adverb, as specified by {@link Configuration#m_nAdverbChance}.
	 *
	 * @param sAdverb a specific adverb to modify the verb of the sentence, or
	 *                {@code null} to randomize the presence and value of the
	 *                adverb
	 * @return (fluid)
	 */
	public NonsenseBuilder setAdverb( String sAdverb )
	{ m_sVerbAdverb = sAdverb ; return this ; }

	/**
	 * Locks a value for the object of the sentence.
	 *
	 * If {@code null} is specified, then the builder's object will be
	 * "cleared", meaning that it will be chosen at random.
	 *
	 * @param sObject a specific object for the sentence, or {@code null} to
	 *                ensure that the object is randomized
	 * @return (fluid)
	 */
	public NonsenseBuilder setObject( String sObject )
	{ m_sObject = sObject ; return this ; }

	/**
	 * Locks a value for the adjective modifying the object of the sentence.
	 *
	 * If {@code null} is specified, then the builder might or might not add a
	 * random adjective, as specified by
	 * {@link Configuration#m_nObjectAdjectiveChance}.
	 *
	 * @param sAdj a specific adjective to modify the object of the sentence, or
	 *             {@code null} to randomize the presence and value of the
	 *             adjective
	 * @return (fluid)
	 */
	public NonsenseBuilder setObjectAdjective( String sAdj )
	{ m_sObjectAdjective = sAdj ; return this ; }

	/**
	 * Locks a value for additional text (expected to be a prepositional phrase)
	 * modifying the object of the sentence.
	 *
	 * If {@code null} is specified, then the builder might or might not add a
	 * random phrase, as specified by
	 * {@link Configuration#m_nObjectPhraseChance}.
	 *
	 * @param sPhrase specific text to modify the object of the sentence, or
	 *                {@code null} to randomize the presence and value of such
	 *                text
	 * @return (fluid)
	 */
	public NonsenseBuilder setObjectModifier( String sPhrase )
	{ m_sObjectModifier = sPhrase ; return this ; }

	@Override
	public String getString()
	{
		StringBuilder sb = new StringBuilder() ;

		this.appendSubject( sb )
		    .appendVerb( sb )
		    .appendObject( sb )
		    ;

		return sb.toString() ;
	}

	/**
	 * Appends the subject and its adjective (if any) to the buffer in which the
	 * sentence is being constructed.
	 * @param sb the buffer in which the sentence is being constructed
	 * @return (fluid)
	 */
	protected NonsenseBuilder appendSubject( StringBuilder sb )
	{
		final String sSubject = ( m_sSubject == null ?
				this.getRandomNonsense( R.array.asNonsenseNouns,
						Configuration.ALWAYS ) :
				m_sSubject
			);
		final String sAdj = ( m_sSubjectAdjective == null ?
				this.getRandomNonsense( R.array.asNonsenseAdjectives,
						m_cfg.m_nSubjectAdjectiveChance ) :
				m_sSubjectAdjective
			);

		if( sAdj == null )
		{ // Just capitalize and append the subject as-is and move on.
			sb.append( sSubject.substring(0,1).toUpperCase() )
			  .append( sSubject.substring(1) )
			  ;
		}
		else if( Utils.startsWithArticle( sSubject ) )
		{ // Split the article from the subject string and insert the adjective.
			String[] asTokens = sSubject.split( " ", 2 ) ;
			switch( asTokens[0] )
			{
				case "a":
				case "an":
					sb.append( Utils.whichIndefiniteArticle( sAdj, true ) )
					  .append( ' ' )
					  ;
					break ;
				case "the":
					sb.append( "The " ) ;
					break ;
				default:
					sb.append( asTokens[0].substring(0,1).toUpperCase() )
					  .append( asTokens[0].substring(1) )
					  .append( ' ' )
					  ;
			}
			sb.append( sAdj )
			  .append( ' ' )
			  .append( asTokens[1] )
			  ;
		}
		else
		{ // Capitalize the adjective and stick it before the noun.
			sb.append( sAdj.substring( 0, 1 ).toUpperCase() )
			  .append( sAdj.substring(1) )
			  .append( ' ' )
			  .append( sSubject )
			  ;
		}

		sb.append( ' ' ) ;
		return this ;
	}

	/**
	 * Appends the verb and its adverb (if any) to the buffer in which the
	 * sentence is being constructed.
	 * @param sb the buffer in which the sentence is being constructed
	 * @return (fluid)
	 */
	protected NonsenseBuilder appendVerb( StringBuilder sb )
	{
		final String sVerb = ( m_sVerb == null ?
				this.getRandomNonsense( R.array.asNonsenseVerbs,
						Configuration.ALWAYS ) :
				m_sVerb
			);
		final String sAdverb = ( m_sVerbAdverb == null ?
				this.getRandomNonsense( R.array.asNonsenseAdverbs,
						m_cfg.m_nAdverbChance ) :
				m_sVerbAdverb
			);

		if( sAdverb == null )
			sb.append( sVerb ) ;
		else
			sb.append( sAdverb ).append( ' ' ).append( sVerb ) ;

		sb.append( ' ' ) ;
		return this ;
	}

	/**
	 * Appends the object and its adjective and modifier phrase (if any) to the
	 * buffer in which the sentence is being constructed.
	 * @param sb the buffer in which the sentence is being constructed
	 * @return (fluid)
	 */
	protected NonsenseBuilder appendObject( StringBuilder sb )
	{
		final String sObject = ( m_sObject == null ?
				this.getRandomNonsense( R.array.asNonsenseNouns,
						Configuration.ALWAYS ) :
				m_sObject
			);
		final String sAdj = ( m_sObjectAdjective == null ?
				this.getRandomNonsense( R.array.asNonsenseAdjectives,
						m_cfg.m_nObjectAdjectiveChance ) :
				m_sObjectAdjective
			);
		final String sPhrase = ( m_sObjectModifier == null ?
				this.getRandomNonsense( R.array.asNonsensePhrases,
						m_cfg.m_nObjectPhraseChance ) :
				m_sObjectModifier
			);

		if( sAdj == null )
			sb.append( sObject ) ;
		else if( Utils.startsWithArticle( sObject ) )
		{
			String[] asTokens = sObject.split( " ", 2 ) ;
			switch( asTokens[0] )
			{
				case "a":
				case "an":
					sb.append( Utils.whichIndefiniteArticle( sAdj, false ) )
					  .append( ' ' )
					  ;
					break ;
				default:
					sb.append( asTokens[0] ).append( ' ' ) ;
			}
			sb.append( sAdj )
			  .append( ' ' )
			  .append( asTokens[1] )
			  ;
		}
		else
			sb.append( sAdj ).append( ' ' ).append( sObject ) ;

		if( sPhrase != null )
			sb.append( ' ' ).append( sPhrase ) ;

		sb.append( '.' ) ;
		return this ;
	}

	/**
	 * Randomly selects a string resource from the selected string array.
	 * @param resStrings the resource ID of a string array
	 * @param nChance chance that we should return anything at all, expressed as
	 *                a percentage (expected range [0,100])
	 * @return a random string from that array of strings
	 */
	public String getRandomNonsense( int resStrings, int nChance )
	{
		if( nChance < Configuration.ALWAYS && RANDOM.nextInt(100) >= nChance )
				return null ;

		final String[] asStrings =
				m_ctx.getResources().getStringArray( resStrings ) ;
		final int nIndex = RANDOM.nextInt( asStrings.length ) ;
		return asStrings[nIndex] ;
	}

}
