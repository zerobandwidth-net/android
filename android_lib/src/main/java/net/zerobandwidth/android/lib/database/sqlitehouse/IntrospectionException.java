package net.zerobandwidth.android.lib.database.sqlitehouse;

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

	public IntrospectionException()
	{ super( DEFAULT_MESSAGE ) ; }

	public IntrospectionException( String sMessage )
	{ super( sMessage ) ; }

	public IntrospectionException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public IntrospectionException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
