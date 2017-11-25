package net.zerobandwidth.android.lib.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Provides additional utility methods for working with collections, beyond the
 * stock {@link Arrays} and {@link java.util.Collections} classes.
 *
 * The class is meant to look like the {@link java.util.Collections} class in
 * use. For methods that can be written statically, the class may be used as
 * such. However, certain operations that need tighter links to particular class
 * specifications require explicit type definition in the method prototypes.
 * To support this, the class provides the {@link #of} method, which creates a
 * type-bound instance of the utility class, whose instance methods may then
 * operate under this more controlled context. {@link #arrayConcat} is an
 * illustrative example of a method that needs this context:
 *
 * <pre>
 *     String[] asFoo = { "foo", "Foo", "FOO" } ;
 *     String[] asBar = { "bar", "Bar", "BAR" } ;
 *     String[] asFoobar = CollectionsZ.of( String.class )
 *             .arrayConcat( asFoo, asBar ) ;
 *     assertEquals( 6, asFoobar.length ) ;
 * </pre>
 *
 * @param <I> the class of item contained in the collection, if any
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public final class CollectionsZ<I>
{
	/**
	 * This method provides a static way to obtain a parameterized instance of
	 * the {@code CollectionsZ} class so that its other nifty methods may be
	 * invoked under these conditions.
	 * @param cls the class of item contained in the collection
	 * @param <T> the class of item contained in the collection
	 * @return a parameterized instance, for more finely-tuned operations
	 */
	public static <T> CollectionsZ<T> of( Class<T> cls )
	{ return new CollectionsZ<>(cls) ; }

	/**
	 * A persistent reference to the class to which the instance's generic
	 * parameter was bound at instantiation.
	 */
	private Class<I> m_cls = null ;

	/**
	 * The constructor should not be invoked directly; use {@link #of} instead.
	 * @param cls the class to which the instance's generic parameter should be
	 *            bound
	 */
	private CollectionsZ( Class<I> cls )
	{ m_cls = cls ; }

	/**
	 * Creates a new empty array of the specified size.
	 * @param nSize the initial capacity
	 * @return an array of objects of the templatized type
	 */
	@SuppressWarnings("unchecked") // guaranteed by restriction on m_cls
	protected I[] newArray( int nSize )
	{ return ((I[])( Array.newInstance( m_cls, nSize ) )) ; }

	/**
	 * Provides a sane implementation of {@code toArray()} for collections which
	 * returns an array of the object's type.
	 * @param a the collection to be converted to an array
	 * @return an array of elements in the collection, or {@code null} if the
	 *  input was also null
	 */
	public I[] toArray( Collection<I> a )
	{
		if( a == null )
			return null ;
		else if( a.size() == 0 )
			return this.newArray(0) ;
		else
			return a.toArray( this.newArray( a.size() ) ) ;
	}

	/**
	 * Concatenates an arbitrary number of arrays of the same object type
	 * @param aArrays the arrays to be concatenated
	 * @return all arrays concatenated in the order they were specified
	 */
	@SuppressWarnings("unchecked") // guaranteed logically
	public I[] arrayConcat( I[]... aArrays )
	{
		if( aArrays == null || aArrays.length == 0 ) return null ; // trivially
		if( aArrays.length == 1 ) return aArrays[0] ; // trivially

		ArrayList<I> aAll = new ArrayList<>() ;
		for( I[] aArray : aArrays )
		{
			if( aArray != null && aArray.length > 0 )
				aAll.addAll( Arrays.asList(aArray) ) ;
		}
		return aAll.toArray( this.newArray( aAll.size() ) ) ;
	}

}
