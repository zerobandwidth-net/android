package net.zerobandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import net.zerobandwidth.android.lib.database.SQLitePortal;

import java.util.Collection;

/**
 * Builds a SQLite query using methods, rather than the methods from the
 * {@link SQLiteDatabase} methods that use long lists of parameters.
 *
 * <p>This class is supposed to do very little; the consumer should use the
 * static methods {@link #insertInto}, {@link #update}, {@link #selectFrom}, and
 * {@link #deleteFrom}) to spawn instances of the various implementation classes
 * corresponding to database actions ({@code INSERT}, {@code UPDATE},
 * {@code SELECT}, and {@code DELETE}).</p>
 *
 * <p>In its initial implementation, the class provides methods to set an
 * explicit format string and parameter list for the {@code WHERE} clause,
 * similar to the syntax used by the various Android functions in
 * {@link SQLiteDatabase}. In the future, the API of this class will be extended
 * to provide a grammar for constructing the conditional statement through
 * builder methods.</p>
 *
 * <p>The class builds on the foundation of {@link SQLitePortal} and derives
 * several of its static constants and design decisions from features of that
 * class.</p>
 *
 * @param <I> The implementation class which extends {@code QueryBuilder}. This
 *           is set explicitly in the declaration of the implementation class,
 *           and allows the superclass to provide concrete implementations of
 *           shared methods.
 * @param <R> The return type of the underlying {@code SQLiteDatabase} function.
 *           This is set explicitly in the declaration of the implementation
 *           class, and will be the return type of {@link #executeOn}.
 *
 * @since zerobandwidth-net/android 0.1.1 (#20)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public abstract class QueryBuilder<I extends QueryBuilder, R>
{
/// Static constants ///////////////////////////////////////////////////////////

	/**
	 * The character that stands in for a variable value in the Android format
	 * string that is passed to {@link SQLiteDatabase} query methods.
	 */
	protected static final String ANDROID_VARIABLE_MARKER = "?" ;

	/**
	 * SQL keyword marking the beginning of a {@code WHERE} clause.
	 * @see InsertionBuilder#toString()
	 * @see UpdateBuilder#toString()
	 * @see SelectionBuilder#toString()
	 * @see DeletionBuilder#toString()
	 */
	protected static final String SQL_WHERE = " WHERE " ;

	/**
	 * If using integer columns to store Boolean values, where {@code 1} is true
	 * and {@code 0} is false, use this constant when supplying {@code WHERE}
	 * value substitutions for "true".
	 * @see SQLitePortal#boolToInt(boolean)
	 * @see SQLitePortal#intToBool(int)
	 * @see SQLitePortal#WHERE_TRUE
	 */
	public static final String WHERE_TRUE = SQLitePortal.WHERE_TRUE ;

	/**
	 * If using integer columns to store Boolean values, where {@code 1} is true
	 * and {@code 0} is false, use this constant when supplying {@code WHERE}
	 * value substitutions for "false".
	 * @see SQLitePortal#boolToInt(boolean)
	 * @see SQLitePortal#intToBool(int)
	 * @see SQLitePortal#WHERE_FALSE
	 */
	public static final String WHERE_FALSE = SQLitePortal.WHERE_FALSE ;

/// Static kickoff methods (starts a query of a given type) ////////////////////

	/**
	 * Kicks off construction of an {@code INSERT} query.
	 * @param sTableName the name of the table into which rows will be inserted
	 * @return an instance of a builder that can handle insertion queries
	 */
	public static InsertionBuilder insertInto( String sTableName )
	{ return new InsertionBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of an {@code UPDATE} query.
	 * @param sTableName the name of the table in which rows will be updated
	 * @return an instance of a builder that can handle update queries
	 */
	public static UpdateBuilder update( String sTableName )
	{ return new UpdateBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of a {@code SELECT} query.
	 * @param sTableName the name of the table from which rows will be selected
	 * @return an instance of a builder that can handle selection queries
	 */
	public static SelectionBuilder selectFrom( String sTableName )
	{ return new SelectionBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of a {@code DELETE} query.
	 * @param sTableName the name of the table from which rows will be deleted
	 * @return an instance of a builder that can handle deletion queries
	 */
	public static DeletionBuilder deleteFrom( String sTableName )
	{ return new DeletionBuilder( sTableName ) ; }

/// Other static methods ///////////////////////////////////////////////////////

	/**
	 * Returns the number of milliseconds since epoch UTC. Use this value when
	 * comparing to timestamps stored in the database as {@code long} integers.
	 * @return milliseconds since epoch UTC
	 * @see SQLitePortal#now()
	 */
	public long now()
	{ return SQLitePortal.now() ; }

/// Shared member fields ///////////////////////////////////////////////////////

	/** The name of the table on which the query will operate. */
	protected String m_sTableName = null ;

	/**
	 * For {@code INSERT} and {@code DELETE} operations, these are the values to
	 * be written.
	 */
	protected ContentValues m_valsToWrite = null ;

	/**
	 * A substitute, explicit format string for a {@code WHERE} clause, for
	 * which {@link #m_asExplicitWhereParams} provides the values.
	 */
	protected String m_sExplicitWhereFormat = null ;

	/**
	 * A substitute, explicit list of parameters for the {@code WHERE} clause
	 * format specified in {@link #m_sExplicitWhereFormat}.
	 */
	protected String[] m_asExplicitWhereParams = null ;

/// Shared constructor /////////////////////////////////////////////////////////

	/**
	 * Superclass's constructor, which initializes the shared member fields.
	 * @param sTableName the name of the table on which the query will be
	 *                   performed
	 */
	public QueryBuilder( String sTableName )
	{
		m_sTableName = sTableName ;
	}

/// Shared methods /////////////////////////////////////////////////////////////

	/**
	 * Sets the table name in which the query will be executed.
	 * @param sTableName the name of the table
	 * @return (fluid)
	 */
	@SuppressWarnings( "unchecked" )
	protected I setTableName( String sTableName )
	{ m_sTableName = sTableName ; return (I)this ; }

	/**
	 * Sets values to be written as part of an {@code INSERT} or {@code UPDATE}
	 * query.
	 * @param vals the values to be written
	 * @return (fluid)
	 */
	@SuppressWarnings( "unchecked" )
	public I setValues( ContentValues vals )
	{
		m_valsToWrite = vals ;
		return (I)this ;
	}

	/**
	 * Constructs an explicit {@code WHERE} clause for the query.
	 *
	 * The supplied string is used as a format string for the {@code WHERE}
	 * clause and should contain <b>no</b> variable substitutions. The
	 * collection of variable values will be set to {@code null} by this method,
	 * based on this assumption.
	 *
	 * @param sWhereClause the explicit {@code WHERE} clause, containing
	 *  <b>no</b> variable substitution placeholders
	 * @return (fluid)
	 */
	@SuppressWarnings( "unchecked" )
	public I where( String sWhereClause )
	{
		m_sExplicitWhereFormat = sWhereClause ;
		m_asExplicitWhereParams = null ;
		return (I)this ;
	}

	/**
	 * Constructs an explicit {@code WHERE} clause for the query.
	 * @param sWhereFormat the format string for the {@code WHERE} clause; uses
	 *                     {@code ?} for parameter substitution
	 * @param asWhereParams the parameters for the {@code WHERE} clause
	 * @return (fluid)
	 */
	@SuppressWarnings( "unchecked" )
	public I where( String sWhereFormat, String... asWhereParams )
	{
		m_sExplicitWhereFormat = sWhereFormat ;
		m_asExplicitWhereParams = asWhereParams ;
		return (I)this ;
	}

	/**
	 * Constructs an explicit {@code WHERE} clause for the query.
	 * @param sWhereFormat the format string for the {@code WHERE} clause; uses
	 *                     {@code ?} for parameter substitution
	 * @param asWhereParams the parameters for the {@code WHERE} clause
	 * @return (fluid)
	 */
	@SuppressWarnings( "unchecked" )
	public I where( String sWhereFormat, Collection<String> asWhereParams )
	{
		m_sExplicitWhereFormat = sWhereFormat ;
		m_asExplicitWhereParams =
			asWhereParams.toArray( new String[asWhereParams.size()] ) ;
		return (I)this ;
	}

	/**
	 * Creates the Android {@code WHERE} clause template to be passed to a
	 * {@link SQLiteDatabase} function.
	 * @return the {@code WHERE} clause template
	 */
	protected String getWhereFormat()
	{
		if( m_sExplicitWhereFormat != null )
			return m_sExplicitWhereFormat ;

		return null ;
	}

	/**
	 * Creates the array of {@code WHERE} clause value substitutions to be
	 * passed to a {@link SQLiteDatabase} function.
	 * @return the {@code WHERE} clause template parameters
	 */
	protected String[] getWhereParams()
	{
		if( m_sExplicitWhereFormat != null )
			return m_asExplicitWhereParams ; // even if THEY are null

		return null ;
	}

	/**
	 * Creates a raw SQLite {@code WHERE} clause based on the format and params
	 * created for the instance.
	 * @return a raw {@code WHERE} clause
	 */
	protected String getWhereClause()
	{
		if( m_sExplicitWhereFormat == null ) return null ;
		if( ! m_sExplicitWhereFormat.contains( "?" ) )
			return m_sExplicitWhereFormat ;        // Contains no substitutions.
		if( m_asExplicitWhereParams == null || m_asExplicitWhereParams.length == 0 )
			throw new IllegalStateException( "Need parameters but don't have them" ) ;
		String sFormat = m_sExplicitWhereFormat.replace( "?", "%s" ) ;
		return String.format( sFormat, ((Object[])(m_asExplicitWhereParams)) ) ;
	}

/// Abstract class specification ///////////////////////////////////////////////

	/**
	 * Executes the query that has been built by the implementation class.
	 * @param db the database instance on which the query should be executed.
	 * @return the usual return value of the underlying method
	 */
	public abstract R executeOn( SQLiteDatabase db ) ;
}
