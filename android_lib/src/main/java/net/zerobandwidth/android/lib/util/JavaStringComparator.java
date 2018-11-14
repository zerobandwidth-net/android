package net.zerobandwidth.android.lib.util;

/**
 * The most basic implementation of {@link StringComparator}, this class is
 * merely a wrapper for the {@link String#compareTo} method. It exists not only
 * as a trivial example, but also to provide a "standard" comparator for
 * situations in which a reference to a {@code Comparator} instance is
 * necessary.
 * @since zerobandwidth-net/android 0.2.1 (#56)
 * @see <a href="https://stackoverflow.com/q/11804733">StackOverflow Q#11804733</a>
 */
public class JavaStringComparator
extends StringComparator.Base
implements StringComparator
{
	private boolean m_bForceConstantReturns = false ;

	/**
	 * Default constructor.
	 * Note that the value of {@link #compare} will be the value that would have
	 * been returned by {@link String#compareTo}. If you want to force the
	 * comparator to use the semantic constants that are defined in
	 * {@link StringComparator}, then use the alternative
	 * {@link #JavaStringComparator(boolean)} constructor and pass {@code true}.
	 */
	public JavaStringComparator()
	{ super() ; }

	/**
	 * Allows the caller to request that the {@link #compare} method still
	 * return the exact values that correspond to the semantic constants
	 * available in {@link StringComparator}.
	 * @param bForceReturnConstants if {@code true}, then the exact constants
	 *  from {@link StringComparator} will be used; otherwise, the raw return
	 *  value of {@link String#compareTo} will be returned.
	 */
	public JavaStringComparator( boolean bForceReturnConstants )
	{ super() ; m_bForceConstantReturns = bForceReturnConstants ; }

	/**
	 * Wrapper for {@link String#compareTo}, unless the instance was created
	 * with the "force return constants" switch set, in which case the value is
	 * coerced to one of the constants from {@link StringComparator}.
	 * @return the value that {@link String#compareTo} would have returned, or
	 *  one of the constants from {@link StringComparator}.
	 */
	@Override
	protected int executeComparison( String s1, String s2 )
	{
		int z = s1.compareTo(s2) ;
		if( m_bForceConstantReturns )
		{
			//noinspection UseCompareMethod
			if( z > 0 ) return S1_IS_GREATER ;
			else if( z < 0 ) return S2_IS_GREATER ;
			else return EQUAL ;
		}
		else return z ;
	}
}
