package net.zer0bandwidth.android.lib.nonsense;

import android.content.Context;

/**
 * This interface provides a specification for a "nonsense generator" &mdash; a
 * class which generates a fully-formed sentence from randomly-chosen words.
 * @since zer0bandwidth-net/android 0.0.1 (#7)
 */
public interface NonsenseGenerator
{
	/**
	 * Static methods that are useful for a wide range of nonsensical
	 * operations.
	 * @since zer0bandwidth-net/android 0.1.5 (#11)
	 */
	class Utils
	{
		/**
		 * Given a string, indicates whether that string begins with an article,
		 * which could be split from the front of the noun string.
		 *
		 * <p>This was formerly {@link NonsenseBuilder}'s
		 * {@code hasArticle(String)} method.</p>
		 *
		 * @param sNoun the string which is probably a noun and might or might
		 *  not also begin with an article
		 * @return {@code true} iff the string begins with an article
		 */
		public static boolean startsWithArticle( String sNoun )
		{
			String sNormal = sNoun.toLowerCase() ;
			return( sNormal.startsWith( "a " )
			     || sNormal.startsWith( "an " )
			     || sNormal.startsWith( "the " )
			     );
		}

		/**
		 * Evaluates which indefinite article should be used before the "next"
		 * word, which is, or modifies, a noun.
		 *
		 * <p>This was formerly {@link NonsenseBuilder}'s
		 * {@code whichIndefiniteArticle(String,boolean)} method.</p>
		 *
		 * @param sNext the "next" word in a phrase or sentence
		 * @param bCapitalize specifies whether to capitalize the article
		 * @return {@code "a"} or {@code "an"}
		 */
		public static String whichIndefiniteArticle(
				String sNext, boolean bCapitalize )
		{
			String sNormal = sNext.toLowerCase() ;
			switch( sNormal.charAt(0) )
			{
				case 'a': case 'e': case 'i': case 'o': case 'u':
					return ( bCapitalize ? "An" : "an" ) ;
				default:
					return ( bCapitalize ? "A" : "a" ) ;
			}
		}
	}

	/**
	 * Sets the context in which string resources can be fetched.
	 *
	 * If this context has not yet been set, then the class will not be able to
	 * fetch string resources to assemble the nonsense string.
	 *
	 * Implementations of this interface should also provide a constructor which
	 * sets this context upfront.
	 *
	 * @param ctx the context in which string resources are available
	 * @return (fluid)
	 */
	NonsenseGenerator setContext( Context ctx ) ;

	/**
	 * Generates the nonsense string.
	 *
	 * The algorithm for creating the string is implementation-dependent.
	 *
	 * @return a fully-formed sentence from randomly-selected words
	 */
	String getString() ;
}
