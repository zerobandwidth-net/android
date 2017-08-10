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
	 * @param sClassName the name of the schematic class
	 * @param sFieldName the name of the field
	 * @param xAccess the exception
	 * @return a new exception with an appropriate message.
	 */
	public static SchematicException fieldWasInaccessible(
			String sClassName, String sFieldName, IllegalAccessException xAccess )
	{
		return new SchematicException( (new StringBuilder())
				.append( "Field [" ).append( sFieldName )
				.append( "] in class [" ).append( sClassName )
				.append( "] was inaccessible. This shouldn't be possible!" )
				.toString()
			, xAccess ) ;
	}

	/**
	 * Returns a new exception to be thrown when a field's mapping into an
	 * existing table can't be resolved. This shouldn't be possible when a
	 * {@code SQLiteHouse} database is used from the beginning, but might arise
	 * when there are mismatches in the annotations, or when migrating from a
	 * non-{@code SQLiteHouse} database implementation.
	 * @param sClassName the name of the field's class
	 * @param sFieldName the name of the sought field
	 * @param sTableName the table in which the column was expected
	 * @param x root-cause exception, if any
	 * @return a new exception with an informative message
	 */
	public static SchematicException columnNotFound(
			String sClassName, String sFieldName, String sTableName, Exception x )
	{
		return new SchematicException( (new StringBuilder())
				.append( "Column for field [" ).append( sFieldName )
				.append( "] in class [" ).append( sClassName )
				.append( "] not found in table [" ).append( sTableName )
				.append( "]; versions or annotations might be mismatched." )
				.toString()
			, x ) ;
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
