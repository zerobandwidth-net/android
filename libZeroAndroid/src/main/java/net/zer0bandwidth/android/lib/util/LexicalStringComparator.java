package net.zer0bandwidth.android.lib.util;

/**
 * Sorts strings lexically.
 *
 * <h3>Algorithm Summary</h3>
 *
 * <ol type="1">
 * <li>If {@link String#equals} returns {@code true}, return 0 quickly.</li>
 * <li>
 *     Search across the strings, comparing the character at each index, and
 *     returning a value as soon as a difference is discovered.
 * </li>
 * <li>
 *     If characters at shared indices match, then the longer string is
 *     "greater".
 * </li>
 * </ol>
 *
 * @since zer0bandwidth-net/android 0.2.1 (#56)
 */
public class LexicalStringComparator
extends StringComparator.Base
implements StringComparator
{
	/**
	 * Compares the two strings lexically.
	 */
	@Override
	protected int executeComparison( String s1, String s2 )
	{
		for( int i = 0 ; i < s1.length() && i < s2.length() ; i++ )
		{ // With all the easy test exhausted, test them character-by-character.
			if( s1.charAt(i) > s2.charAt(i) ) return S1_IS_GREATER ;
			if( s1.charAt(i) < s2.charAt(i) ) return S2_IS_GREATER ;
		}

		// Shared characters all match, so the longer string is "greater".
		if( s1.length() == s2.length() ) return EQUAL ;   // maybe always false?
		else return( s1.length() > s2.length() ?
				S1_IS_GREATER : S2_IS_GREATER ) ;
	}
}
