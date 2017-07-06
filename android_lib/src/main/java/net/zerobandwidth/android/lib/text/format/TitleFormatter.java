package net.zerobandwidth.android.lib.text.format;


import android.content.Context;

import net.zerobandwidth.android.lib.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Formats a string as a title. The class currently supports only an English
 * locale, but other locale supports may be added in the future.
 * @since zerobandwidth-net/android 0.1.5 (#11)
 */
public class TitleFormatter
{
	/**
	 * Formats a string as a title.
	 * @param ctx a context, so we can fetch localized string resources
	 * @param sInput the input string
	 * @return a new string, formatted as a title
	 */
	public static String format( Context ctx, String sInput )
	{ return format( ctx, sInput, Locale.getDefault() ) ; }

	/**
	 * Formats a string as a title, using the specified locale (eventually...).
	 * @param ctx a context, so we can fetch localized string resources
	 * @param sInput the input string
	 * @param loc the locale to be considered; this is currently ignored, but
	 *            may be used in a future version
	 * @return a new string, formatted as a title
	 */
	@SuppressWarnings("unused") // param "loc" is intended for future support
	public static String format( Context ctx, String sInput, Locale loc )
	{
		if( sInput == null ) return null ; // trivially
		if( sInput.length() == 0 ) return "" ; // trivially

		return formatEnglishTitle( ctx, sInput ) ;
	}

/// English locales ////////////////////////////////////////////////////////////

	/**
	 * A data structure which self-initializes with all the data related to
	 * evaluations in an English-speaking context.
	 * @since zerobandwidth-net/android 0.1.5 (#11)
	 * @see TitleFormatter#formatEnglishTitle
	 */
	protected static class EnglishLocaleContext
	{
		public Context ctx ;
		public List<String> asArticles ;
		public List<String> asPrepositions ;
		public EnglishLocaleContext( Context ctx )
		{
			this.ctx = ctx ;
			asArticles = Arrays.asList( ctx.getResources()
					.getStringArray( R.array.asEnglishArticles ) ) ;
			asPrepositions = Arrays.asList( ctx.getResources()
					.getStringArray( R.array.asEnglishPrepositions ) ) ;
		}
	}

	/**
	 * Formats a string as a title, assuming an English-speaking locale.
	 *
	 * <p>The criteria used are as follows:</p>
	 *
	 * <ul>
	 *     <li>Capitalize the first and last word.</li>
	 *     <li>Capitalize any word that is not an article, conjunction, or
	 *         preposition shorter than five letters.</li>
	 * </ul>
	 *
	 * <p>This most closely matches the AP Stylebook, as opposed to the Chicago
	 * Manual of Style or the MLA guidelines.</p>
	 *
	 * @param sInput the input string
	 * @return a new string, formatted as a title
	 * @see <a href="http://grammar.yourdictionary.com/capitalization/rules-for-capitalization-in-titles.html">Your Dictionary: Rules for Capitalization in Titles of Articles</a>
	 */
	protected static String formatEnglishTitle( Context ctx, String sInput )
	{
		String[] asTokens = sInput.split( "\\s" ) ;
		StringBuilder sbOutput = new StringBuilder() ;
		EnglishLocaleContext lctx = new EnglishLocaleContext(ctx) ;
		for( int i = 0 ; i < asTokens.length ; i++ )
		{ // Use numeric iterator so we know when we're on the last token.
			String sToken = asTokens[i] ;
			if( i > 0 )
			{ // Not the first word. Append a space, then capitalize.
				sbOutput.append( " " ) ;
				sbOutput.append( resolveEnglishToken( lctx, sToken,
						( i == asTokens.length - 1 ) ) ) ;  // is the last token
			}
			else
				sbOutput.append( resolveEnglishToken( lctx, sToken, true ) ) ;
		}
		return sbOutput.toString() ;
	}

	/**
	 * Resolves capitalization of a single English token.
	 * @param lctx contextual information about English-speaking locales
	 * @param sToken the input token
	 * @param bForce specifies that capitalization should be forced
 	 * @return the resolved token
	 */
	protected static String resolveEnglishToken(
			EnglishLocaleContext lctx, String sToken, boolean bForce )
	{
		String sNormal = sToken.toLowerCase() ;
		char[] acReturn = new char[ sNormal.length() ] ;
		if( ! bForce )
		{ // See if this is one of the words that must remain lower-case.
			if( lctx.asArticles.contains(sNormal) )
				return sNormal ;
			if( sToken.length() < 5 && lctx.asPrepositions.contains(sNormal) )
				return sNormal ;
		}
		for( int i = 0 ; i < sToken.length() ; i++ )
		{
			char c = sNormal.charAt(i) ;
			if( i == 0 )
				c = Character.toUpperCase(c) ;
			else if( i == 3 && sNormal.startsWith( "o'" ) )
				c = Character.toUpperCase(c) ;
			else if( i > 1 && sNormal.charAt(i-1) == '-' )
				c = Character.toUpperCase(c) ;
			acReturn[i] = c ;
		}
		return new String(acReturn) ;
	}

/// Other locales... ///////////////////////////////////////////////////////////

	// TBD

/// Other methods //////////////////////////////////////////////////////////////

	/** This class is static-only; it should not be instantiated. */
	private TitleFormatter() {}
}
