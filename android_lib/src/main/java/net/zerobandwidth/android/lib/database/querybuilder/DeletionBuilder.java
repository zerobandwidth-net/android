package net.zerobandwidth.android.lib.database.querybuilder;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.DELETE_ALL;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.DELETE_FAILED;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_DELETE_FROM;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_WHERE;

/**
 * Builds a SQLite {@code DELETE} query.
 *
 * <h3>Examples</h3>
 *
 * Delete all records from a table:
 *
 * <pre>
 * int nDeleted = QueryBuilder.deleteFrom( sTableName )
 *     .deleteAll()
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * Delete select records from a table:
 *
 * <pre>
 * int nDeleted = QueryBuilder.deleteFrom( sTableName )
 *     .where( "active=? OR last_active_ts<=?",
 *         QueryBuilder.WHERE_FALSE, TimeUtils.now() - 86400 )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#delete(String, String, String[])
 */
public class DeletionBuilder
extends QueryBuilder<DeletionBuilder,Integer>
{
	protected static final String LOG_TAG =
			DeletionBuilder.class.getSimpleName() ;

	public DeletionBuilder( String sTableName )
	{ super( sTableName ) ; }

	/**
	 * Convenience grammar specifying that all rows should be deleted.
	 * @return (fluid)
	 */
	public DeletionBuilder deleteAll()
	{ return this.where( DELETE_ALL ) ; }

	/**
	 * Deletes rows based on the builder's {@code WHERE} clause.
	 * @param db the database instance on which the query should be executed.
	 * @return the number of rows deleted, or
	 *  {@link net.zerobandwidth.android.lib.database.SQLiteSyntax#DELETE_FAILED}
	 *  if the operation fails
	 */
	@Override
	public Integer executeOn( SQLiteDatabase db )
	{
		try
		{
			return db.delete(
					m_sTableName,
					this.getWhereFormat(),
					this.getWhereParams()
				);
		}
		catch( Exception x )
		{
			Log.e( LOG_TAG, "Deletion query failed:", x ) ;
			return DELETE_FAILED ;
		}
	}

	/**
	 * Constructs a raw SQL {@code DELETE} query based on the attributes of the
	 * builder instance
	 * @return a raw SQLite {@code DELETE} query
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( SQL_DELETE_FROM ).append( m_sTableName ) ;
		final String sWhere = this.getWhereClause() ;
		if( sWhere != null )
			sb.append( SQL_WHERE ).append( sWhere ) ;
		sb.append( " ;" ) ;
		return sb.toString() ;
	}
}
