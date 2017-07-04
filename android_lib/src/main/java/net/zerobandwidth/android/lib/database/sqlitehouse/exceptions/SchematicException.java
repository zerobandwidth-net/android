package net.zerobandwidth.android.lib.database.sqlitehouse.exceptions;

/**
 * Thrown by
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * whenever a method encounters a problem with the schema definition discovered
 * in some schematic class.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SuppressWarnings("unused") // This is a library.
public class SchematicException
extends RuntimeException
{
	/**
	 * Returns a new exception that notes a condition that can't happen: namely,
	 * that a field we had previously made accessible wasn't accessible when
	 * subsequently accessed.
	 * @param sName the field name
	 * @param xAccess the exception
	 * @return a new exception with an appropriate message.
	 */
	public static SchematicException fieldWasInaccessible(
			String sName, IllegalAccessException xAccess )
	{
		return new SchematicException( (new StringBuilder())
				.append( "Field corresponding to key column [" )
				.append( sName )
				.append( "] was inaccessible. This shouldn't be possible!" )
				.toString()
			, xAccess ) ;
	}

	public static final String DEFAULT_MESSAGE =
			"Defined database schema does not support this operation." ;

	public SchematicException()
	{ super( DEFAULT_MESSAGE ) ; }

	public SchematicException( String sMessage )
	{ super( sMessage ) ; }

	public SchematicException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public SchematicException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
