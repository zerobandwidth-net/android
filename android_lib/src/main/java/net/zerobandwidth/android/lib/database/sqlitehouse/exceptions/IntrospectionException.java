package net.zerobandwidth.android.lib.database.sqlitehouse.exceptions;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;

/**
 * Thrown by {@link SQLiteHouse} and its descendant classes whenever the process
 * of reflexively constructing a database fails.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class IntrospectionException
extends RuntimeException
{
	protected static final String DEFAULT_MESSAGE =
			"Failed to reflexively discover database information." ;

	/**
	 * Used when an attempt to use {@link Class#newInstance()} fails with an
	 * {@link InstantiationException}.
	 * @param cls the class being instantiated
	 * @param xCause the cause of the failure
	 * @return a new exception with the appropriate message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static IntrospectionException instanceFailed(
			Class<?> cls, Throwable xCause )
	{ return instanceFailed( cls.getCanonicalName(), xCause ) ; }

	/**
	 * Used when an attempt to create an instance of a schematic class fails.
	 * @param sClass the canonical name of the class being instantiated
	 * @param xCause the cause of the failure
	 * @return a new exception with the appropriate message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static IntrospectionException instanceFailed(
			String sClass, Throwable xCause )
	{
		return new IntrospectionException( (new StringBuilder())
					.append( "Could not create instance of class [" )
					.append( sClass )
					.append( "]." )
					.toString()
				, xCause
			);
	}

	/**
	 * Used when an attempt to use {@link Class#newInstance()} fails with an
	 * {@link IllegalAccessException}.
	 * @param cls the class being instantiated
	 * @param xCause the cause of the failure
	 * @return a new exception with the appropriate message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static IntrospectionException instanceForbidden(
			Class<?> cls, Throwable xCause )
	{
		return new IntrospectionException( (new StringBuilder())
					.append( "Instance constructor inaccessible for class [" )
					.append( cls.getCanonicalName() )
					.append( "]." )
					.toString()
				, xCause
			);
	}

	/**
	 * Used when something tries to process a class that does not implement the
	 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable}
	 * interface.
	 * @param cls the unusable class
	 * @param xCause the root cause, if any
	 * @return a new exception with the appropriate message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static IntrospectionException illegalClassSpecification(
			Class<?> cls, Throwable xCause )
	{ return illegalClassSpecification( cls.getCanonicalName(), xCause ) ; }

	/**
	 * Used when something tries to process a class that does not implement the
	 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable}
	 * interface.
	 * @param sClass the canonical name of the unusable class
	 * @param xCause the root cause, if any
	 * @return a new exception with the appropriate message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static IntrospectionException illegalClassSpecification(
			String sClass, Throwable xCause )
	{
		return new IntrospectionException( (new StringBuilder())
					.append( "Class [" ).append( sClass )
					.append( "] does not implement SQLightable." )
					.toString()
				, xCause
			);
	}

	@SuppressWarnings("unused")
	public IntrospectionException()
	{ super( DEFAULT_MESSAGE ) ; }

	public IntrospectionException( String sMessage )
	{ super( sMessage ) ; }

	@SuppressWarnings("unused")
	public IntrospectionException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public IntrospectionException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
