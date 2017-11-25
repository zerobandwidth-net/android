package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import net.zerobandwidth.android.lib.util.CollectionsZ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static net.zerobandwidth.android.lib.content.ContentUtils.QUERY_ORDER_ASCENDING;

/**
 * Builds an insertion query against a given {@link ContentResolver} and
 * {@link Uri}.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 *     Cursor crs = QueryBuilder.selectFrom( rslv, uri )
 *             .allColumns()
 *             .where( "entity_id=?", sID )
 *             .execute()
 *             ;
 * </pre>
 *
 * <pre>
 *     Cursor crs = QueryBuilder.selectFrom( rslv, uri )
 *             .columns( "entity_id", "name", "start_ts", "stop_ts" )
 *             .where( "start_ts>?", TimeUtils.now() )
 *             .orderBy( "name", ContentUtils.QUERY_ORDER_ASCENDING )
 *             .execute()
 *             ;
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.7 (#39)
 * @see net.zerobandwidth.android.lib.content.ContentUtils#QUERY_ORDER_ASCENDING
 * @see net.zerobandwidth.android.lib.content.ContentUtils#QUERY_ORDER_DESCENDING
 */
public class SelectionBuilder
extends QueryBuilder<SelectionBuilder,Cursor>
{
	protected static final String LOG_TAG =
			SelectionBuilder.class.getSimpleName() ;

	/** The columns to be included in the result set. */
	protected Vector<String> m_vColumns = null ;

	/** The mapping of sortable columns to sorting directions, if any. */
	protected HashMap<String,String> m_mapSortSpec = null ;

	public SelectionBuilder( ContentResolver rslv, Uri uri )
	{
		super( rslv, uri ) ;
		this.initColumns().initSortSpec() ;
	}

	public SelectionBuilder( Context ctx, Uri uri )
	{
		super( ctx, uri ) ;
		this.initColumns().initSortSpec() ;
	}

	public SelectionBuilder()
	{
		super() ;
		this.initColumns().initSortSpec() ;
	}

	/**
	 * Initializes the vector of columns to be included in the result set.
	 * @return (fluid)
	 */
	protected SelectionBuilder initColumns()
	{
		if( m_vColumns == null )
			m_vColumns = new Vector<>() ;
		else
			m_vColumns.clear() ;
		return this ;
	}

	/**
	 * Initializes the map of sortable columns to sorting directions.
	 * @return (fluid)
	 */
	protected SelectionBuilder initSortSpec()
	{
		if( m_mapSortSpec == null )
			m_mapSortSpec = new HashMap<>() ;
		else
			m_mapSortSpec.clear() ;
		return this ;
	}

	/**
	 * Specifies that all columns should be included in the result set.
	 * This is the default behavior if left unspecified.
	 * @return (fluid)
	 */
	public SelectionBuilder allColumns()
	{ return this.initColumns() ; }

	/**
	 * Sets the columns that should be included in the result set.
	 *
	 * If selecting all columns, then do not pass {@code null} to this method;
	 * use {@link #allColumns()} instead.
	 *
	 * @param asColumns the names of the columns to be included
	 * @return (fluid)
	 */
	public SelectionBuilder columns( String... asColumns )
	{
		this.initColumns() ;
		if( asColumns == null ) return this ;
		for( String sColumn : asColumns )
		{
			if( ! m_vColumns.contains( sColumn ) )
				m_vColumns.add( sColumn ) ;
		}
		return this ;
	}

	/**
	 * Sets the columns that should be included in the result set.
	 *
	 * If selecting all columns, then do not pass {@code null} to this method;
	 * use {@link #allColumns()} instead.
	 *
	 * @param asColumns the names of the columns to be included
	 * @return (fluid)
	 */
	public SelectionBuilder columns( Collection<String> asColumns )
	{
		if( asColumns == null || asColumns.isEmpty() )
			return this.allColumns() ;

		return this.columns(
				asColumns.toArray( new String[ asColumns.size() ] ) ) ;
	}

	/**
	 * Generates the list of columns to be included in the result set, as an
	 * array of strings to be passed to {@link ContentResolver#query}.
	 * @return a list of column names, or {@code null} if all columns are to be
	 *  included
	 */
	protected String[] getColumns()
	{
		if( m_vColumns == null || m_vColumns.isEmpty() )
			return null ;
		else
			return m_vColumns.toArray( new String[ m_vColumns.size() ] ) ;
	}

	/**
	 * Adds a sorting specification to the query.
	 * @param sColumn the column to be added to the sort specification
	 * @param sDirection the direction
	 * @return (fluid)
	 * @see net.zerobandwidth.android.lib.content.ContentUtils#QUERY_ORDER_ASCENDING
	 * @see net.zerobandwidth.android.lib.content.ContentUtils#QUERY_ORDER_DESCENDING
	 */
	public SelectionBuilder orderBy( String sColumn, String sDirection )
	{
		m_mapSortSpec.put( sColumn, sDirection ) ;
		return this ;
	}

	/**
	 * Adds a sorting specification to the query. This column will be sorted in
	 * ascending order.
	 * @param sColumn the column to be added to the sort specification
	 * @return (fluid)
	 */
	public SelectionBuilder orderBy( String sColumn )
	{ return this.orderBy( sColumn, QUERY_ORDER_ASCENDING ) ; }

	/**
	 * Generates the selection's sort criteria as a string, to be supplied to
	 * {@link ContentResolver#query}.
	 * @return a sort specification
	 */
	protected String getSortSpecString()
	{
		if( m_mapSortSpec == null || m_mapSortSpec.isEmpty() )
			return null ;
		StringBuilder sb = new StringBuilder() ;
		for( Map.Entry<String,String> spec : m_mapSortSpec.entrySet() )
		{
			if( sb.length() > 0 ) sb.append( ", " ) ;
			sb.append( spec.getKey() ).append( " " ).append( spec.getValue() ) ;
		}
		return sb.toString() ;
	}

	/**
	 * Selects results from the data context.
	 * @return a set of results from the data context
	 */
	@Override
	public Cursor executeQuery( ContentResolver rslv, Uri uri )
	throws Exception
	{
		return rslv.query( uri,
				this.getColumns(),
				this.getWhereFormat(),
				this.getWhereParams(),
				this.getSortSpecString()
			);
	}

	/**
	 * <b><i>(API 16+)</i></b> Selects results from the data context, while
	 * allowing the query to be cancelled in response to the specified signal.
	 * @param sig the signal which would cancel the query
	 * @return a set of results from the data context
	 */
	@RequiresApi(16)
	public Cursor executeOrCancel( CancellationSignal sig )
	throws UnboundException, ExecutionException
	{ return this.executeOrCancel( m_rslv, m_uri, sig ) ; }

	/**
	 * <b><i>(API 16+)</i></b> Selects results from the data context, while
	 * allowing the query to be cancelled in response to the specified signal.
	 * Usually, this is not invoked directly, but is instead consumed by
	 * {@link #executeOrCancel(CancellationSignal)}.
	 * @param rslv the resolver through which the query should be executed
	 * @param uri the URI at which the query should be executed
	 * @param sig the signal which would cancel the query
	 * @return a set of results from the data context
	 * @throws UnboundException if the data context binding is inadequate
	 * @throws ExecutionException if the underlying query fails
	 */
	@RequiresApi(16)
	public Cursor executeOrCancel( ContentResolver rslv, Uri uri, CancellationSignal sig )
	throws UnboundException, ExecutionException
	{
		validateDataContextBinding( rslv, uri ) ;
		if( sig.isCanceled() )
		{
			Log.i( LOG_TAG, "Query already cancelled; returning trivially." ) ;
			return null ;
		}
		try
		{
			return rslv.query( uri,
					this.getColumns(),
					this.getWhereFormat(),
					this.getWhereParams(),
					this.getSortSpecString(),
					sig
				);
		}
		catch( OperationCanceledException xCancel )
		{
			Log.i( LOG_TAG, "Query cancelled while in progress." ) ;
			return null ;
		}
		catch( Exception x )
		{ throw new ExecutionException( LOG_TAG, x ) ; }
	}

	/**
	 * Creates a bundle that describes the query itself, <i>not</i> the result
	 * set that it would select. Used to pass the query specification across an
	 * intent broadcast, in a provider/resolver model. The schema for this
	 * bundle is consistent and is defined as follows:
	 *
	 * <dl>
	 *     <dt>{@link String} {@code uri}</dt>
	 *     <dd>The URI at which the query is aimed.</dd>
	 *     <dt>{@link String}[] {@code columns}</dt>
	 *     <dd>The list of selection columns. Null implies all columns.</dd>
	 *     <dt>{@link String} {@code where_format}</dt>
	 *     <dd>The format string for the query's {@code WHERE} clause.</dd>
	 *     <dt>{@link String}[] {@code where_params}</dt>
	 *     <dd>
	 *         The list of parameters to be substituted in the query's
	 *         {@code WHERE} clause format string.
	 *     </dd>
	 *     <dt>{@link String}[] {@code order_by_cols} <i>(optional)</i></dt>
	 *     <dd>The list of columns on which the query is to be sorted.</dd>
	 *     <dt>{@link String}[] {@code order_by_dirs} <i>(optional)</i></dt>
	 *     <dd>
	 *         The list of sort directions (ascending/descending) for each
	 *         column mentioned in {@code order_by_cols}.
	 *     </dd>
	 * </dl>
	 *
	 * @return a bundle describing the selection query itself
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public Bundle toBundle()
	{
		Bundle bndl = new Bundle() ;
		bndl.putString( "uri", ( this.m_uri != null ?
				this.m_uri.toString() : null ) ) ;
		bndl.putStringArray( "columns", this.getColumns() ) ;
		bndl.putString( "where_format", this.getWhereFormat() ) ;
		bndl.putStringArray( "where_params", this.getWhereParams() ) ;
		if( ! this.m_mapSortSpec.isEmpty() )
		{
			ArrayList<String> asOrderByCols = new ArrayList<>() ;
			ArrayList<String> asOrderByDirs = new ArrayList<>() ;
			for( Map.Entry<String,String> spec : this.m_mapSortSpec.entrySet() )
			{
				asOrderByCols.add( spec.getKey() ) ;
				asOrderByDirs.add( spec.getValue() ) ;
			}
			bndl.putStringArray( "order_by_cols",
					CollectionsZ.of(String.class).toArray(asOrderByCols) ) ;
			bndl.putStringArray( "order_by_dirs",
					CollectionsZ.of(String.class).toArray(asOrderByDirs) ) ;
		}
		return bndl ;
	}
}
