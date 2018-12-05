package net.zer0bandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.Collection;

/**
 * Builds a {@link ContentResolver} query using builder-style methods, rather
 * than the standard methods, which use long lists of often-null parameters.
 *
 * <p>This class is supposed to do very little; the consumer should use the
 * static methods {@link #insertInto}, {@link #update}, {@link #selectFrom}, and
 * {@link #deleteFrom} to spawn instances of the various implementation classes
 * corresponding to query actions.</p>
 *
 * @param <I> The implementation class which extends {@code QueryBuilder}. This
 *           is set explicitly in the declaration of the implementation class,
 *           and allows the superclass to provide concrete implementations of
 *           methods reusable by the descendants.
 * @param <R> The return type of the {@link #execute} method, which
 *           corresponds to a method in {@code ContentResolver}.
 *
 * @since zer0bandwidth-net/android 0.1.7 (#39)
 */
public abstract class QueryBuilder<I extends QueryBuilder, R>
{
/// Inner classes //////////////////////////////////////////////////////////////

	/**
	 * Informs a consumer of {@link QueryBuilder} that it has tried to invoke
	 * the {@link QueryBuilder#execute} method without first binding to a data
	 * source. To prevent this exception, ensure that the consumer code properly
	 * supplies a data source when it calls one of the static
	 * {@code QueryBuilder} methods that instantiates a builder.
	 * @since zer0bandwidth-net/android 0.1.7 (#39)
	 */
	public static class UnboundException
	extends IllegalStateException
	{
		protected static final String DEFAULT_MESSAGE =
			"Caller tried to execute a query without a data source reference." ;

		public UnboundException()
		{ super(DEFAULT_MESSAGE) ; }

		public UnboundException( String sMessage )
		{ super(sMessage) ; }

		public UnboundException( Throwable xCause )
		{ super( DEFAULT_MESSAGE, xCause ) ; }

		public UnboundException( String sMessage, Throwable xCause )
		{ super( sMessage, xCause ) ; }
	}

	/**
	 * Exception thrown by {@link #execute} and {@link #executeOn} when the
	 * underlying {@link ContentResolver} query operation fails.
	 * @since zer0bandwidth-net/android 0.1.7 (#39)
	 */
	public static class ExecutionException
	extends RuntimeException
	{
		protected static final String DEFAULT_MESSAGE =
			"Query execution failed." ;

		public ExecutionException()
		{ super(DEFAULT_MESSAGE) ; }

		public ExecutionException( String sMessage )
		{ super(sMessage) ; }

		/**
		 * Creates a standard exception with a message indicating the class
		 * which failed to execute.
		 * @param cls the class which failed to execute
		 */
		public ExecutionException( Class<? extends QueryBuilder> cls )
		{ super( getClassMessage(cls) ) ; }

		public ExecutionException( Throwable xCause )
		{ super( DEFAULT_MESSAGE, xCause ) ; }

		public ExecutionException( String sMessage, Throwable xCause )
		{ super( sMessage, xCause ) ; }

		/**
		 * Creates a standard exception with a message indicating the class
		 * which failed to execute.
		 * @param cls the class which failed to execute
		 * @param xCause the cause of the failure
		 */
		public ExecutionException( Class<? extends QueryBuilder> cls, Throwable xCause )
		{ super( getClassMessage(cls), xCause ) ; }

		/**
		 * Generates an exception message based on the name of the class that
		 * failed.
		 * @param cls the class whose execution method failed
		 * @return a standard message indicating that the selected class failed
		 *  to execute
		 */
		protected static String getClassMessage( Class<? extends QueryBuilder> cls )
		{
			return (new StringBuilder())
					.append( cls.getSimpleName() )
					.append( " execution failed." )
					.toString()
					;
		}
	}

/// Static kickoff methods (start queries of specific types) ///////////////////

	/**
	 * Kicks off construction of an insertion query.
	 * When constructing the builder in this way, the caller <i>must</i> also
	 * call {@link #onDataSource}, or use {@link #executeOn} instead of
	 * {@link #execute}.
	 * @return an instance of the builder that handles insertion queries
	 */
	public static InsertionBuilder insert()
	{ return new InsertionBuilder() ; }

	/**
	 * Kicks off construction of an insertion query.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles insertion queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static InsertionBuilder insertInto( ContentResolver rslv, Uri uri )
	throws UnboundException
	{ return new InsertionBuilder( rslv, uri ) ; }

	/**
	 * Kicks off construction of an insertion query.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles insertion queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static InsertionBuilder insertInto( Context ctx, Uri uri )
	throws UnboundException
	{ return new InsertionBuilder( ctx, uri ) ; }

	/**
	 * Kicks off construction of an update query.
	 * When constructing the builder in this way, the caller <i>must</i> also
	 * call {@link #onDataSource}, or use {@link #executeOn} instead of
	 * {@link #execute}.
	 * @return an instance of the builder that handles update queries
	 */
	public static UpdateBuilder update()
	{ return new UpdateBuilder() ; }

	/**
	 * Kicks off construction of an update query.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles update queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static UpdateBuilder update( ContentResolver rslv, Uri uri )
	throws UnboundException
	{ return new UpdateBuilder( rslv, uri ) ; }

	/**
	 * Kicks off construction of an update query.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles update queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static UpdateBuilder update( Context ctx, Uri uri )
	throws UnboundException
	{ return new UpdateBuilder( ctx, uri ) ; }

	/**
	 * Kicks off construction of a selection query.
	 * When constructing the builder in this way, the caller <i>must</i> also
	 * call {@link #onDataSource}, or use {@link #executeOn} instead of
	 * {@link #execute}.
	 * @return an instance of the builder that handles selection queries
	 */
	public static SelectionBuilder select()
	{ return new SelectionBuilder() ; }

	/**
	 * Kicks off construction of a selection query.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles selection queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static SelectionBuilder selectFrom( ContentResolver rslv, Uri uri )
	throws UnboundException
	{ return new SelectionBuilder( rslv, uri ) ; }
	/**
	 * Kicks off construction of a selection query.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles selection queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static SelectionBuilder selectFrom( Context ctx, Uri uri )
	throws UnboundException
	{ return new SelectionBuilder( ctx, uri ) ; }

	/**
	 * Kicks off construction of a deletion query.
	 * When constructing the builder in this way, the caller <i>must</i> also
	 * call {@link #onDataSource}, or use {@link #executeOn} instead of
	 * {@link #execute}.
	 * @return an instance of the builder that handles deletion queries
	 */
	public static DeletionBuilder delete()
	{ return new DeletionBuilder() ; }

	/**
	 * Kicks off construction of a deletion query.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles deletion queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static DeletionBuilder deleteFrom( ContentResolver rslv, Uri uri )
	throws UnboundException
	{ return new DeletionBuilder( rslv, uri ) ; }

	/**
	 * Kicks off construction of a deletion query.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return an instance of the builder that handles deletion queries
	 * @throws UnboundException if the data context is unusable
	 */
	public static DeletionBuilder deleteFrom( Context ctx, Uri uri )
	throws UnboundException
	{ return new DeletionBuilder( ctx, uri ) ; }

/// Other static methods ///////////////////////////////////////////////////////

	/**
	 * Obtains a {@link ContentResolver} from the specified context, throwing an
	 * exception if that context is null.
	 * @param ctx a context which can provide a content resolver.
	 * @return the content resolver for the specified context
	 * @throws UnboundException if the context is null
	 */
	protected static ContentResolver getContentResolver( Context ctx )
	throws UnboundException
	{
		if( ctx == null )
			throw new UnboundException( "Null context cannot provide resolver." ) ;
		return ctx.getContentResolver() ;
	}

	/**
	 * Ensures that the specified {@link ContentResolver} and {@link Uri} are
	 * non-null and usable as a data context.
	 * @throws ExecutionException if any problems occur
	 */
	protected static void validateDataContextBinding( ContentResolver rslv,
	                                                  Uri uri )
	throws QueryBuilder.UnboundException
	{
		if( rslv == null )
			throw new UnboundException( "A content resolver is required." ) ;
		if( uri == null )
			throw new UnboundException( "A valid URI is required." ) ;
	}

/// Shared member fields ///////////////////////////////////////////////////////

	/** The {@link ContentResolver} to which the builder is bound. */
	protected ContentResolver m_rslv = null ;

	/** The {@link Uri} to be supplied to the {@link ContentResolver}. */
	protected Uri m_uri = null ;

	/**
	 * For "insert" and "update" operations, these are the values to be written.
	 */
	protected ContentValues m_valsToWrite = null ;

	/**
	 * A substitute, explicit "where" format string, for which
	 * {@link #m_asExplicitWhereParams} provides the values.
	 */
	protected String m_sExplicitWhereFormat = null ;

	/**
	 * A substitute, explicit list of "where" parameters, to fit the format
	 * string stored in {@link #m_sExplicitWhereFormat}.
	 */
	protected String[] m_asExplicitWhereParams = null ;

/// Shared constructors ////////////////////////////////////////////////////////

	/**
	 * The default constructor; does not bind to a particular data source.
	 */
	public QueryBuilder() {}

	/**
	 * A shared constructor which binds the builder to a the resolver found in
	 * the given context, and the specified URI.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @throws QueryBuilder.UnboundException if either parameter is unusable
	 */
	public QueryBuilder( Context ctx, Uri uri )
	throws QueryBuilder.UnboundException
	{ this.onDataSource( ctx, uri ) ; }

	/**
	 * A shared constructor which binds the builder to a specific resolver and
	 * URI.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @throws QueryBuilder.UnboundException if either parameter is unusable
	 */
	public QueryBuilder( ContentResolver rslv, Uri uri )
	throws QueryBuilder.UnboundException
	{ this.onDataSource( rslv, uri ) ; }

/// Shared methods /////////////////////////////////////////////////////////////

	/**
	 * Binds the builder to a specific data context, to be used by the execution
	 * methods.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return (fluid)
	 * @throws QueryBuilder.UnboundException if either parameter is unusable
	 */
	public I onDataSource( ContentResolver rslv, Uri uri )
	throws QueryBuilder.UnboundException
	{
		validateDataContextBinding( rslv, uri ) ;
		m_rslv = rslv ;
		m_uri = uri ;
		//noinspection unchecked - guaranteed by generic parameterization
		return (I)this ;
	}

	/**
	 * Binds the builder to a specific data context, to be used by the execution
	 * methods.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return (fluid)
	 * @throws QueryBuilder.UnboundException if either parameter is unusable
	 */
	public I onDataSource( Context ctx, Uri uri )
	throws QueryBuilder.UnboundException
	{ return this.onDataSource( getContentResolver(ctx), uri ) ; }

	/**
	 * Sets values to be written as part of an "insert" or "update" operation,
	 * if applicable.
	 * @param vals the values to be written
	 * @return (fluid)
	 */
	public I setValues( ContentValues vals )
	{
		m_valsToWrite = vals ;
		//noinspection unchecked - guaranteed by generic parameterization
		return (I)this ;
	}

	/**
	 * Constructs an explicit "where" clause for a query.
	 *
	 * <p>The supplied string is used as-is in the underlying
	 * {@link ContentResolver} query function, and should contain only necessary
	 * columns with literal values (no variable substitutions). Based on this
	 * restriction, the collection of variable substitution sources will be set
	 * as {@code null}.</p>
	 * @param sWhereClause the explicit "where" clause, containing only literal
	 *                     values
	 * @return (fluid)
	 */
	public I where( String sWhereClause )
	{
		m_sExplicitWhereFormat = sWhereClause ;
		m_asExplicitWhereParams = null ;
		//noinspection unchecked - guaranteed by generic parameterization
		return (I)this ;
	}

	/**
	 * Sets the "where" clause format and values for a query.
	 * @param sWhereFormat the format string of the "where" clause, which must
	 *                     use {@code ?} for parameter substitution
	 * @param asWhereParams the parameters for the "where" clause, assigned to
	 *                      substitution markers in the format string
	 * @return (fluid)
	 * @see net.zer0bandwidth.android.lib.content.ContentUtils#QUERY_VARIABLE_MARKER
	 */
	public I where( String sWhereFormat, String... asWhereParams )
	{
		m_sExplicitWhereFormat = sWhereFormat ;
		m_asExplicitWhereParams = asWhereParams ;
		//noinspection unchecked - guaranteed by generic parameterization
		return (I)this ;
	}

	/**
	 * Sets the "where" clause format and values for a query.
	 * @param sWhereFormat the format string of the "where" clause, which must
	 *                     use {@code ?} for parameter substitution
	 * @param asWhereParams the parameters for the "where" clause, assigned to
	 *                      substitution markers in the format string
	 * @return (fluid)
	 * @see net.zer0bandwidth.android.lib.content.ContentUtils#QUERY_VARIABLE_MARKER
	 */
	public I where( String sWhereFormat, Collection<String> asWhereParams )
	{
		if( asWhereParams == null )
			return this.where( sWhereFormat ) ;
		else
		{
			return this.where( sWhereFormat,
				asWhereParams.toArray( new String[asWhereParams.size()] ) ) ;
		}
	}

	/**
	 * Creates the Android "where" clause format string to be passed to a
	 * {@link ContentResolver} query method.
	 * @return the "where" clause format string
	 */
	protected String getWhereFormat()
	{ return m_sExplicitWhereFormat ; }

	/**
	 * Creates the array of "where" clause value substitutions to be passed to a
	 * {@link ContentResolver} query method.
	 * @return the "where" clause format string's parameters
	 */
	protected String[] getWhereParams()
	{ return m_asExplicitWhereParams ; }

	/**
	 * Executes the query that has been built by the implementation class, using
	 * the {@link ContentResolver} and {@link Uri} to which the builder has been
	 * bound, either by the constructor, or by an invocation of
	 * {@link #onDataSource}.
	 * @return the return type appropriate to the query action
	 */
	public final R execute()
	throws UnboundException, ExecutionException
	{ return this.executeOn( this.m_rslv, this.m_uri ) ; }

	/**
	 * Executes the query that has been built by the implementation class, using
	 * the supplied {@link ContentResolver} and {@link Uri}. Usually, this is
	 * not invoked directly, but is instead consumed by {@link #execute}.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return the return type appropriate to the query action
	 * @throws UnboundException if the data context binding is inadequate
	 * @throws ExecutionException if the underlying query fails
	 */
	public final R executeOn( ContentResolver rslv, Uri uri )
	throws UnboundException, ExecutionException
	{
		validateDataContextBinding( rslv, uri ) ;
		try { return this.executeQuery( rslv, uri ) ; }
		catch( Exception x )
		{ throw new ExecutionException( this.getClass(), x ) ; }
	}

	/**
	 * Executes the query that has been built by the implementation class, using
	 * the {@link ContentResolver} provided by the specified {@link Context},
	 * and the specified {@link Uri}.
	 * @param ctx a context which can provide a {@link ContentResolver}
	 * @param uri the URI at which the query should be executed
	 * @return the return type appropriate to the query action
	 * @throws UnboundException if the data context binding is inadequate
	 * @throws ExecutionException if the underlying query fails
	 */
	public final R executeOn( Context ctx, Uri uri )
	throws UnboundException, ExecutionException
	{ return this.executeOn( getContentResolver(ctx), uri ) ; }

/// Abstract class specification ///////////////////////////////////////////////

	/**
	 * Consumed by {@link #execute} and {@link #executeOn} to actually carry out
	 * the operation. In the implementation class, this method should consist
	 * solely of the {@link ContentResolver} query method call, plus any other
	 * pre-checks which might be able to short-circuit the query execution.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @return the return type appropriate to the query action
	 * @throws Exception if anything goes wrong
	 */
	protected abstract R executeQuery( ContentResolver rslv, Uri uri )
		throws Exception ;
}
