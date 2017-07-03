package net.zerobandwidth.android.lib.database.sqlitehouse.exceptions;

/**
 * Thrown by
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * whenever a method encounters a problem with the schema definition discovered
 * in some schematic class.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class SchematicException
extends RuntimeException
{
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
