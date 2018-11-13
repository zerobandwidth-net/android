package net.zerobandwidth.android.lib.util;

import java.util.Comparator;

/**
 * There might be lots of ways to compare strings. There are at least two of
 * them just in this library. Also, I get pretty crossed up as to how the value
 * returned by a comparator works. So here's an interface with some handy
 * semantic constants for derpy developers like myself.
 * @since zerobandwidth-net/android [NEXT] (#56)
 */
public interface StringComparator
extends Comparator<String>
{
	/**
	 * An abstract base class which provides logic that will be common to most,
	 * if not all, actual implementations of the interface. The implementation
	 * of {@link #compare} begins by comparing the strings as objects, examining
	 * their nullness/emptiness. If that algorithm is already enough to identify
	 * the "greater" string, then there is no need for the actual implementation
	 * class to do anything further. Otherwise, the implementation class's
	 * {@link #executeComparison} may proceed already knowing that the strings
	 * are non-null and non-empty, and that {@link String#equals} has already
	 * been tried and is not worth trying again.
	 * @since zerobandwidth-net/android [NEXT] (#56)
	 */
	abstract class Base implements StringComparator
	{
		/**
		 * Semantic constant not intended as an actual return value; instead,
		 * the internal {@link #compareAsObjects} method will return this when
		 * its na&iuml;ve evaluation of the strings (comparing their nullness
		 * and emptiness) isn't enough to resolve the comparison.
		 */
		protected static final int EVALUATION_INDETERMINATE = Integer.MIN_VALUE;

		/**
		 * Compares the strings as objects &mdash; that is, it bases its
		 * comparison on the simple question of whether one or both of the
		 * strings is null or empty.
		 *
		 * <ul>
		 * <li>Two references to the same object are trivially equal.</li>
		 * <li>Null and/or empty strings are equal.</li>
		 * <li>Non-null strings for which {@code s1.equals(s2)} are equal.</li>
		 * <li>
		 *     Otherwise, a non-null, non-empty string is "greater than" a null
		 *     or empty string.
		 * </li>
		 * </ul>
		 *
		 * @param s1 the first string to compare
		 * @param s2 the second string to compare
		 * @return an indication of order or equality
		 *  <ul>
		 *      <li>{@link #EQUAL} (0) if the strings are equal</li>
		 *      <li>{@link #S1_IS_GREATER} (1) if the first is "greater"</li>
		 *      <li>{@link #S2_IS_GREATER} (-1) if the second is "greater"</li>
		 *      <li>
		 *          {@link #EVALUATION_INDETERMINATE} if this method's
		 *          na&iuml;ve algorithm was not enough to figure out which is
		 *          greater
		 *      </li>
		 *  </ul>
		 */
		protected static int compareAsObjects( String s1, String s2 )
		{
			if( s1 == null )
				return( ( s2 == null || s2.isEmpty() ) ? EQUAL : S2_IS_GREATER ) ;
			else if( s2 == null ) // and s1 is not
				return( s1.isEmpty() ? EQUAL : S1_IS_GREATER ) ;
			else if( s1.isEmpty() ) // and neither string is null
				return( s2.isEmpty() ? EQUAL : S2_IS_GREATER ) ;
			else if( s1.equals(s2) )
				return EQUAL ;
			else
				return EVALUATION_INDETERMINATE ;
		}

		/**
		 * Executes the comparison that is specific to this class. If the base
		 * class's implementation of {@link #compare} is used as-is, then this
		 * method is guaranteed that the strings are non-null, non-empty, and
		 * that {@link String#equals} has returned {@code false}.
		 * @param s1 the first string to compare
		 * @param s2 the second string to compare
		 * @return an indication of order or equality
		 *  <ul>
		 *      <li>{@link #EQUAL} (0) if the strings are equal</li>
		 *      <li>{@link #S1_IS_GREATER} (1) if the first is "greater"</li>
		 *      <li>{@link #S2_IS_GREATER} (-1) if the second is "greater"</li>
		 *  </ul>
		 */
		protected abstract int executeComparison( String s1, String s2 ) ;

		/**
		 * Executes {@link #compareAsObjects} first to determine whether there
		 * is already a clear winner, and if not, calls the comparator's more
		 * specific {@link #executeComparison} method. Implementation classes
		 * must override this method if they don't want {@code compareAsObjects}
		 * to be executed first.
		 *
		 * <p>This method <i>does not</i> throw a {@link NullPointerException},
		 * as it is specifically designed to handle null values.</p>
		 *
		 * @param s1 the first string to compare
		 * @param s2 the second string to compare
		 * @return an indication of order or equality
		 *  <ul>
		 *      <li>{@link #EQUAL} (0) if the strings are equal</li>
		 *      <li>{@link #S1_IS_GREATER} (1) if the first is "greater"</li>
		 *      <li>{@link #S2_IS_GREATER} (-1) if the second is "greater"</li>
		 *      <li>
		 *          {@link #EVALUATION_INDETERMINATE} if this method's
		 *          na&iuml;ve algorithm was not enough to figure out which is
		 *          greater
		 *      </li>
		 *  </ul>
		 */
		@Override
		public int compare( String s1, String s2 )
		{
			int zAsObjects = compareAsObjects( s1, s2 ) ;
			return( zAsObjects == EVALUATION_INDETERMINATE ?
				this.executeComparison( s1, s2 ) : zAsObjects ) ;
		}
	}

	/** Semantic constant indicating the <b>first</b> string is "greater". */
	int S1_IS_GREATER = 1 ;
	/** Semantic constant indicating the <b>second</b> string is "greater". */
	int S2_IS_GREATER = -1 ;
	/** Semantic constant indicating that the strings are equal. */
	int EQUAL = 0 ;
}
