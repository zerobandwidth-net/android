package net.zerobandwidth.android.lib.database.querybuilder;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_GROUP_BY;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_HAVING;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_LIMIT;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_ORDER_BY;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_WHERE;

/**
 * Builds a SQLite {@code SELECT} query.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * Cursor crsResult = QueryBuilder.selectFrom( sTableName )
 *     .allColumns()
 *     .where( "entity_id=?", sID )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * <pre>
 * Cursor crsResult = QueryBuilder.selectFrom( sTableName )
 *     .columns( "entity_id", "name", "start_ts", "stop_ts" )
 *     .where( "active=? AND start_ts>=?",
 *         QueryBuilder.WHERE_TRUE, SQLitePortal.now() )
 *     .groupBy( "name" )
 *     .orderBy( "name", SelectionBuilder.ORDER_ASC )
 *     .limit( 10 )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#query
 */
public class SelectionBuilder
extends QueryBuilder<SelectionBuilder,Cursor>
{
	/**
	 * @deprecated zerobandwidth-net/android 0.1.7 (#48) -
	 *  use {@link SQLiteSyntax#SELECT_ALL}
	 */
	@SuppressWarnings( "unused" ) // Great!
	public static final String SELECT_ALL_COLUMNS = SQLiteSyntax.SELECT_ALL ;

	/**
	 * @deprecated zerobandwidth-net/android 0.1.7 (#48) -
	 *  use {@link SQLiteSyntax#SQL_ORDER_ASC}
	 */
	@SuppressWarnings( "unused" ) // Great!
	public static final String ORDER_ASC = SQLiteSyntax.SQL_ORDER_ASC ;

	/**
	 * @deprecated zerobandwidth-net/android 0.1.7 (#48) -
	 *  use {@link SQLiteSyntax#SQL_ORDER_DESC}
	 */
	@SuppressWarnings( "unused" ) // Great!
	public static final String ORDER_DESC = SQLiteSyntax.SQL_ORDER_DESC ;

	/**
	 * Specifies that no result count limit should be enforced.
	 * @see #limit(int)
	 */
	public static final int NO_LIMIT = -1 ;

	/** Flag specifying whether to select distinct results. */
	protected boolean m_bDistinct = false ;

	/** The columns to be selected. */
	protected Vector<String> m_vColumns = null ;

	/** The SQLite {@code GROUP BY} clause to be used, if any. */
	protected String m_sGroupBy = null ;

	/** The SQLite {@code HAVING} clause to be used, if any. */
	protected String m_sHaving = null ;

	/** The mapping of SQLite {@code ORDER BY} clauses and directions, if any. */
	protected HashMap<String,String> m_mapOrderBy = null ;

	/** A limit on the number of results to be returned, if any. */
	protected int m_nLimit = NO_LIMIT ;

	public SelectionBuilder( String sTableName )
	{
		super( sTableName ) ;
		this.initColumns() ;
		m_mapOrderBy = new HashMap<>() ;
	}

	/**
	 * Initializes the vector of columns to be shown, if limited.
	 * @return (fluid)
	 */
	protected SelectionBuilder initColumns()
	{
		if( m_vColumns == null )
			m_vColumns = new Vector<>() ;
		return this ;
	}

	/**
	 * Specifies whether to select distinct results.
	 * @param b {@code true} if only distinct results are desired
	 * @return (fluid)
	 */
	public SelectionBuilder distinct( boolean b )
	{ m_bDistinct = b ; return this ; }

	/**
	 * Specifies that selection results should be distinct.
	 * @return (fluid)
	 */
	public SelectionBuilder distinct()
	{ return this.distinct(true) ; }

	/**
	 * Specifies that all columns should be selected (default).
	 * @return (fluid)
	 */
	public SelectionBuilder allColumns()
	{ m_vColumns = null ; return this ; }

	/**
	 * Sets the columns that should be returned in the selection set.
	 *
	 * If selecting all columns, then do not pass {@code null} to this method;
	 * use {@link #allColumns()} instead.
	 *
	 * @param asColumns the names of columns to be returned
	 * @return (fluid)
	 */
	public SelectionBuilder columns( String... asColumns )
	{
		if( asColumns == null ) // or SQLiteSyntax.SELECT_ALL_COLUMNS
		{ m_vColumns = null ; return this ; }
		this.initColumns() ;
		for( String sColumn : asColumns )
			if( ! m_vColumns.contains( sColumn ) ) m_vColumns.add( sColumn ) ;
		return this ;
	}

	/**
	 * Sets the columns that should be returned in the selection set.
	 *
	 * If selecting all columns, then do not pass {@code null} to this method;
	 * use {@link #allColumns()} instead.
	 *
	 * @param asColumns the names of columns to be returned
	 * @return (fluid)
	 */
	public SelectionBuilder columns( Collection<String> asColumns )
	{
		if( asColumns == null ) // or SQLiteSyntax.SELECT_ALL_COLUMNS
		{ m_vColumns = null ; return this ; }
		this.initColumns() ;
		for( String sColumn : asColumns )
			if( ! m_vColumns.contains( sColumn ) ) m_vColumns.add( sColumn ) ;
		return this ;
	}

	/**
	 * Generates the column list to be passed to {@link SQLiteDatabase#query}.
	 * @return a list of column names, or {@code null} if not limited.
	 */
	protected String[] getColumnList()
	{
		if( m_vColumns == null || m_vColumns.isEmpty() )
			return null ;
		else
			return m_vColumns.toArray( new String[m_vColumns.size()] ) ;
	}

	/**
	 * Specifies the SQLite {@code GROUP BY} clause for the selection.
	 * Pass {@code null} to specify no grouping.
	 * @param sGroupByClause the SQLite {@code GROUP BY} clause
	 * @return (fluid)
	 */
	public SelectionBuilder groupBy( String sGroupByClause )
	{ m_sGroupBy = sGroupByClause ; return this ; }

	/**
	 * Specifies the SQLite {@code HAVING} clause for the selection.
	 * Pass {@code null} to specify no clause.
	 * @param sHavingClause the SQLite {@code HAVING} clause
	 * @return (fluid)
	 */
	@SuppressWarnings("unused") // TODO Unit test this.
	public SelectionBuilder having( String sHavingClause )
	{ m_sHaving = sHavingClause ; return this ; }

	/**
	 * Adds an SQLite {@code ORDER BY} clause to the selection.
	 * This method can be invoked multiple times to construct a multi-layered
	 * clause.
	 * @param sColumnName the name of the column to be sorted
	 * @param sDirection the direction of sorting
	 * @return (fluid)
	 * @see #ORDER_ASC
	 * @see #ORDER_DESC
	 */
	public SelectionBuilder orderBy( String sColumnName, String sDirection )
	{
		if( ! m_mapOrderBy.containsKey( sColumnName ) )
			m_mapOrderBy.put( sColumnName, sDirection ) ;
		return this ;
	}

	/**
	 * Adds an SQLite {@code ORDER BY} clause to the selection.
	 * This method can be invoked multiple times to construct a multi-layered
	 * clause.
	 * The sorting direction set by this method is always "ascending".
	 * @param sColumnName the name of the column to be sorted
	 * @return (fluid)
	 */
	public SelectionBuilder orderBy( String sColumnName )
	{
		if( sColumnName == null )
		{
			m_mapOrderBy.clear() ;
			return this ;
		}
		else return this.orderBy( sColumnName, SQLiteSyntax.SQL_ORDER_ASC ) ;
	}

	/**
	 * Generates the selection's {@code ORDER BY} clause, if any.
	 * @return an {@code ORDER BY} clause for the selection
	 */
	protected String getOrderByClause()
	{
		if( m_mapOrderBy == null || m_mapOrderBy.isEmpty() )
			return null ;
		StringBuilder sb = new StringBuilder() ;
		for( Map.Entry<String,String> o : m_mapOrderBy.entrySet() )
		{
			if( sb.length() > 0 ) sb.append( ", " ) ;
			sb.append( o.getKey() )
			  .append( " " )
			  .append( o.getValue() )
			  ;
		}
		return sb.toString() ;
	}

	/**
	 * Adds an SQLite {@code LIMIT} clause to the selection.
	 * To explicitly enforce no limit, pass {@link #NO_LIMIT}.
	 * @param nLimit the limit to be enforced, or {@link #NO_LIMIT} (default)
	 * @return (fluid)
	 */
	public SelectionBuilder limit( int nLimit )
	{ m_nLimit = nLimit ; return this ; }

	/**
	 * Executes the selection query.
	 * @param db the database instance on which the query should be executed.
	 * @return a cursor on the result set
	 * @see SQLiteDatabase#query
	 */
	@Override
	public Cursor executeOn( SQLiteDatabase db )
	{
		return db.query(
				m_bDistinct,
				m_sTableName,
				this.getColumnList(),
				this.getWhereFormat(),
				this.getWhereParams(),
				m_sGroupBy,
				m_sHaving,
				this.getOrderByClause(),
				( m_nLimit == NO_LIMIT ? null : Integer.toString(m_nLimit) )
			);
	}

	/**
	 * Constructs a raw SQL {@code SELECT} query based on the attributes of the
	 * builder instance.
	 * @return a raw SQLite {@code SELECT} query
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( SQLiteSyntax.SQL_SELECT ) ;
		final String[] asColumns = this.getColumnList() ;
		sb.append(( asColumns == null ? SQLiteSyntax.SQL_SELECT_ALL_COLUMNS :
			TextUtils.join( ", ", asColumns )) )
		  ;
		sb.append( SQLiteSyntax.SQL_FROM ).append( m_sTableName ) ;
		final String sWhere = this.getWhereClause() ;
		if( sWhere != null )
			sb.append( SQL_WHERE ).append( sWhere ) ;
		if( m_sGroupBy != null )
			sb.append( SQL_GROUP_BY ).append( m_sGroupBy ) ;
		if( m_sHaving != null )
			sb.append( SQL_HAVING ).append( m_sHaving ) ;
		final String sOrderBy = this.getOrderByClause() ;
		if( sOrderBy != null )
			sb.append( SQL_ORDER_BY ).append( sOrderBy ) ;
		if( m_nLimit != NO_LIMIT )
			sb.append( SQL_LIMIT ).append( m_nLimit ) ;
		sb.append( " ;" ) ;
		return sb.toString() ;
	}
}
