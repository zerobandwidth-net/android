package net.zerobandwidth.android.lib.database.sqlitehouse.exceptions;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;

/**
 * Thrown by {@link SQLiteHouse} and its descendant classes whenever the process
 * of reflexively constructing a database fails.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SuppressWarnings( "unused" )
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
	{
		return new IntrospectionException( (new StringBuilder())
					.append( "Could not create instance of class [" )
					.append( cls.getCanonicalName() )
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

	public IntrospectionException()
	{ super( DEFAULT_MESSAGE ) ; }

	public IntrospectionException( String sMessage )
	{ super( sMessage ) ; }

	public IntrospectionException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public IntrospectionException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
