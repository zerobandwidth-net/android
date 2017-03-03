package net.zerobandwidth.android.lib.database.querybuilder;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Builds a SQLite {@code DELETE} query.
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#delete(String, String, String[])
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class DeletionBuilder
extends QueryBuilder<DeletionBuilder,Integer>
{
	protected static final String LOG_TAG =
			DeletionBuilder.class.getSimpleName() ;

	/**
	 * Similar to the standard magic number returned when an insertion query
	 * fails (see {@link SQLitePortal#INSERT_FAILED}, this return value from
	 * {@link #executeOn(SQLiteDatabase)} indicates that the delete operation
	 * could not be carried out because of an exception.
	 */
	public static final int DELETE_FAILED = -1 ;

	protected static final String SQL_DELETE_FROM = "DELETE FROM " ;

	public DeletionBuilder( String sTableName )
	{ super( sTableName ) ; }

	/**
	 * Convenience grammar specifying that all rows should be deleted.
	 * @return (fluid)
	 */
	public DeletionBuilder deleteAll()
	{ return this.where( SQLitePortal.DELETE_ALL ) ; }

	/**
	 * Deletes rows based on the builder's {@code WHERE} clause.
	 * @param db the database instance on which the query should be executed.
	 * @return the number of rows deleted, or {@link #DELETE_FAILED} if the
	 *  operation fails
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
