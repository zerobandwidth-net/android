package net.zerobandwidth.android.lib.database.sqlitehouse.exceptions;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.reflect.Field;

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
	/**
	 * Returns a new exception that notes a condition that can't happen: namely,
	 * that a field we had previously made accessible wasn't accessible when
	 * subsequently accessed.
	 * @param sClassName the name of the schematic class (since 0.1.6 #47)
	 * @param sFieldName the name of the field (since 0.1.6 #47)
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
	 * @since zerobandwidth-net/android 0.1.6 (#47)
	 */
	public static SchematicException columnNotFound(
			String sClassName, String sFieldName, String sTableName, Throwable x )
	{
		return new SchematicException( (new StringBuilder())
				.append( "Column for field [" ).append( sFieldName )
				.append( "] in class [" ).append( sClassName )
				.append( "] not found in table [" ).append( sTableName )
				.append( "]; versions or annotations might be mismatched." )
				.toString()
			, x ) ;
	}

	/**
	 * Returns an exception to be thrown when a field that was supposed to
	 * represent a column isn't properly annotated.
	 * @param fld the field
	 * @return a new exception with an informative message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static SchematicException fieldNotAnnotated( Field fld )
	{
		return new SchematicException( (new StringBuilder())
				.append( "Cannot analyze field [" ).append( fld.getName() )
				.append( "] of class [" )
				.append( fld.getDeclaringClass().getCanonicalName() )
				.append( "]: Field is not properly annotated." )
				.toString()
			);
	}

	/**
	 * Returns an exception to be thrown when an attempt to create or update the
	 * SQLite database fails because the SQL statement failed.
	 * @param refl a reflection of the schematic class that failed
	 * @param sSQL the SQL statement that was executed
	 * @param xCause the root cause of the failure, if any (may be null)
	 * @return a new exception with an informative message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static SchematicException tableCreationOrUpdateFailed(
			SQLightable.Reflection refl, String sSQL, Throwable xCause )
	{
		String sMessage = (new StringBuilder())
				.append( "Failed to create table for class [" )
				.append( refl.getTableClass().getCanonicalName() )
				.append( "] when executing SQL statement: " )
				.append(( sSQL == null ? "(null)" : sSQL ))
				.toString()
				;
		return new SchematicException( sMessage, xCause ) ;
	}

	/**
	 * Returns an exception to be thrown when a column operation cannot continue
	 * because the column's
	 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor}
	 * implementation cannot be determined.
	 * @param col the column on which the operation would be performed
	 * @param xCause the cause of the failure, if any (may be null)
	 * @return a new exception with an informative error message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static SchematicException noLensForColumn(
			SQLightable.Reflection.Column col, Throwable xCause )
	{ return noLensForField( col.getField(), xCause ) ; }

	/**
	 * Returns an exception to be thrown when an operation cannot continue
	 * because a field/column's
	 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor}
	 * implementation cannot be determined
	 * @param fld the field on which the operation would be performed
	 * @param xCause the cause of the failure, if any (may be null)
	 * @return a new exception with an informative error message
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static SchematicException noLensForField( Field fld, Throwable xCause )
	{
		String sMessage = (new StringBuilder())
				.append( "No refractor implementation found for field [" )
				.append( fld.getName() )
				.append( "] of class [" )
				.append( fld.getDeclaringClass().getCanonicalName() )
				.append( "]." )
				.toString()
				;
		return new SchematicException( sMessage, xCause ) ;
	}

	public static final String DEFAULT_MESSAGE =
			"Defined database schema does not support this operation." ;

	@SuppressWarnings( "unused" )
	public SchematicException()
	{ super( DEFAULT_MESSAGE ) ; }

	public SchematicException( String sMessage )
	{ super( sMessage ) ; }

	@SuppressWarnings( "unused" )
	public SchematicException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public SchematicException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
