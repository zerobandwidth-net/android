package net.zerobandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.SQLiteSyntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_VAR;

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
/// Inner Classes //////////////////////////////////////////////////////////////

	/**
	 * Informs a consumer that the builder's {@link #execute} method was invoked
	 * while the builder was not bound to a target database instance. When
	 * encountering this exception in code that consumes {@code QueryBuilder},
	 * ensure that the code either uses one of the two-argument kickoff methods
	 * that includes a database binding, or uses {@link #onDatabase} to
	 * define a binding, or uses {@link #executeOn} instead of {@link #execute}.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public static class UnboundException
	extends IllegalStateException
	{
		protected static final String DEFAULT_MESSAGE =
			"Caller tried to execute a query without a database reference." ;

		public UnboundException()
		{ super(DEFAULT_MESSAGE) ; }

		public UnboundException( String sMessage )
		{ super(sMessage) ; }

		public UnboundException( Throwable xCause )
		{ super( DEFAULT_MESSAGE, xCause ) ; }

		public UnboundException( String sMessage, Throwable xCause )
		{ super( sMessage, xCause ) ; }
	}

/// Static constants ///////////////////////////////////////////////////////////

	/**
	 * @deprecated zerobandwidth-net/android 0.1.7 (#48) -
	 *  use {@link SQLiteSyntax#SQLITE_VAR}
	 */
	protected static final String ANDROID_VARIABLE_MARKER =
			SQLITE_VAR;

/// Static kickoff methods (starts a query of a given type) ////////////////////

	/**
	 * Kicks off construction of an {@code INSERT} query.
	 * @param sTableName the name of the table into which rows will be inserted
	 * @return an instance of a builder that can handle insertion queries
	 */
	public static InsertionBuilder insertInto( String sTableName )
	{ return new InsertionBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of an {@code INSERT} query, bound to a specific
	 * database instance.
	 * @param db the database on which the query should be executed
	 * @param sTableName the name of the table into which rows will be inserted
	 * @return an instance of a builder that can handle insertion queries
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public static InsertionBuilder insertInto( SQLiteDatabase db, String sTableName )
	{ return insertInto(sTableName).onDatabase(db) ; }

	/**
	 * Kicks off construction of an {@code UPDATE} query.
	 * @param sTableName the name of the table in which rows will be updated
	 * @return an instance of a builder that can handle update queries
	 */
	public static UpdateBuilder update( String sTableName )
	{ return new UpdateBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of an {@code UPDATE} query, bound to a specific
	 * database instance.
	 * @param db the database on which the query should be executed
	 * @param sTableName the name of the table in which rows will be updated
	 * @return an instance of a builder that can handle update queries
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public static UpdateBuilder update( SQLiteDatabase db, String sTableName )
	{ return update(sTableName).onDatabase(db) ; }

	/**
	 * Kicks off construction of a {@code SELECT} query.
	 * @param sTableName the name of the table from which rows will be selected
	 * @return an instance of a builder that can handle selection queries
	 */
	public static SelectionBuilder selectFrom( String sTableName )
	{ return new SelectionBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of a {@code SELECT} query, bound to a specific
	 * database instance.
	 * @param db the database on which the query should be executed
	 * @param sTableName the name of the table from which rows will be selected
	 * @return an instance of a builder that can handle selection queries
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public static SelectionBuilder selectFrom( SQLiteDatabase db, String sTableName )
	{ return selectFrom(sTableName).onDatabase(db) ; }

	/**
	 * Kicks off construction of a {@code DELETE} query.
	 * @param sTableName the name of the table from which rows will be deleted
	 * @return an instance of a builder that can handle deletion queries
	 */
	public static DeletionBuilder deleteFrom( String sTableName )
	{ return new DeletionBuilder( sTableName ) ; }

	/**
	 * Kicks off construction of a {@code DELETE} query, bound to a specific
	 * database instance.
	 * @param db the database on which the query should be executed
	 * @param sTableName the name of the table from which rows will be deleted
	 * @return an instance of a builder that can handle deletion queries
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public static DeletionBuilder deleteFrom( SQLiteDatabase db, String sTableName )
	{ return deleteFrom(sTableName).onDatabase(db) ; }

/// Other static methods ///////////////////////////////////////////////////////

	/**
	 * Returns the number of milliseconds since epoch UTC. Use this value when
	 * comparing to timestamps stored in the database as {@code long} integers.
	 * @return milliseconds since epoch UTC
	 * @see SQLitePortal#now()
	 */
	public long now()
	{ return SQLitePortal.now() ; }

	/**
	 * Renders the key/value pairs in a set of {@link ContentValues} as a list
	 * of input parameters to an SQL {@code INSERT} or {@code UPDATE} query's
	 * {@code SET} clause.
	 *
	 * So that the fields always appear in consistent order (rather than by hash
	 * code), the method will sort lexically by key before rendering the output
	 * string.
	 *
	 * @param vals the key/value pairs to be rendered
	 * @return an SQL {@code SET} clause body
	 */
	public static String toSQLInputParams( ContentValues vals )
	{
		StringBuilder sb = new StringBuilder() ;
		List<Map.Entry<String,Object>> aEntries =
				new ArrayList<>( vals.valueSet() ) ;
		Collections.sort( aEntries, new Comparator<Map.Entry<String,Object>>()
		{
			@Override
			public int compare( Map.Entry<String,Object> lhs, Map.Entry<String,Object> rhs )
			{ return (lhs.getKey()).compareTo(rhs.getKey()) ; }
		});
		for( Map.Entry<String,Object> pair : aEntries )
		{
			if( sb.length() > 0 ) sb.append( ", " ) ;
			sb.append( pair.getKey() ).append( "=" ) ;
			if( pair.getValue() instanceof Number )
				sb.append( pair.getValue() ) ;
			else
				sb.append( "'" ).append( pair.getValue() ).append( "'" ) ;
		}
		return sb.toString() ;
	}

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

	/**
	 * A persistent binding to a specific database, used by {@link #execute}.
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	protected SQLiteDatabase m_dbTarget = null ;

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
	 * Binds the builder to a specific database instance, to be used by
	 * {@link #execute}.
	 * @param db the database instance on which the query should be executed
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	@SuppressWarnings( "unchecked" )
	public I onDatabase( SQLiteDatabase db )
	{ m_dbTarget = db ; return (I)this ; }

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
		if( ! m_sExplicitWhereFormat.contains( SQLITE_VAR ) )
			return m_sExplicitWhereFormat ;        // Contains no substitutions.
		if( m_asExplicitWhereParams == null || m_asExplicitWhereParams.length == 0 )
		{
			throw new IllegalStateException(
					"Need parameters but don't have them." ) ;
		}
		String sFormat = m_sExplicitWhereFormat.replace( SQLITE_VAR, "%s" ) ;
		return String.format( sFormat, ((Object[])(m_asExplicitWhereParams)) ) ;
	}

/// Abstract class specification ///////////////////////////////////////////////

	/**
	 * Executes the query that has been built by the implementation class.
	 * @param db the database instance on which the query should be executed.
	 * @return the usual return value of the underlying method
	 */
	public abstract R executeOn( SQLiteDatabase db ) ;

	/**
	 * Executes the query that has been built by the implementation class, on
	 * the database instance to which the builder has been bound, either by a
	 * constructor, or by {@link #onDatabase}.
	 * @return the usual return value of the underlying method
	 * @throws QueryBuilder.UnboundException if the builder is not yet bound to
	 *  a database instance
	 * @since zerobandwidth-net/android 0.1.4 (#37)
	 */
	public final R execute()
	throws QueryBuilder.UnboundException
	{
		if( m_dbTarget == null )
			throw new QueryBuilder.UnboundException() ;

		return this.executeOn( m_dbTarget ) ;
	}
}
