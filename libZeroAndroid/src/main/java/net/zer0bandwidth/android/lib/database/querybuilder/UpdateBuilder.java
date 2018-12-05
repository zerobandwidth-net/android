package net.zer0bandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.zer0bandwidth.android.lib.database.SQLiteSyntax;

import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQL_SET;
import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQL_UPDATE;
import static net.zer0bandwidth.android.lib.database.SQLiteSyntax.SQL_WHERE;

/**
 * Builds a SQLite {@code UPDATE} query.
 *
 * <h3>Examples</h3>
 *
 * Update all rows.
 *
 * <pre>
 * int nUpdated = QueryBuilder.update( sTableName )
 *     .setValues( vals )
 *     .updateAll()
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * Update specific rows, specifying that conflicts should be ignored.
 *
 * <pre>
 * int nUpdated = QueryBuilder.update( sTableName )
 *     .setValues( vals )
 *     .where( "some_column=?", sBogusValue )
 *     .onConflict( SQLiteDatabase.CONFLICT_IGNORE )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * @since zer0bandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#updateWithOnConflict(String, ContentValues, String, String[], int)
 */
public class UpdateBuilder
extends QueryBuilder<UpdateBuilder,Integer>
{
	protected static final String LOG_TAG = UpdateBuilder.class.getSimpleName();

	/**
	 * The numeric ID of the conflict resolution algorithm provided by Android.
	 * By default, no conflict resolution algorithm is requested.
	 * @see SQLiteDatabase#CONFLICT_ABORT
	 * @see SQLiteDatabase#CONFLICT_FAIL
	 * @see SQLiteDatabase#CONFLICT_IGNORE
	 * @see SQLiteDatabase#CONFLICT_NONE
	 * @see SQLiteDatabase#CONFLICT_REPLACE
	 * @see SQLiteDatabase#CONFLICT_ROLLBACK
	 */
	protected int m_zConflictAlgorithmID = SQLiteDatabase.CONFLICT_NONE ;

	public UpdateBuilder( String sTableName )
	{ super( sTableName ) ; }

	/**
	 * Convenience grammar specifying that all rows should be updated.
	 * @return (fluid)
	 */
	public UpdateBuilder updateAll()
	{ return this.where( SQLiteSyntax.UPDATE_ALL ) ; }

	/**
	 * Selects, by its numeric ID, the conflict resolution algorithm provided by
	 * Android.
	 * @param zAlgorithmID the ID of the algorithm to use
	 * @return (fluid)
	 */
	public UpdateBuilder onConflict( int zAlgorithmID )
	{ m_zConflictAlgorithmID = zAlgorithmID ; return this ; }

	/**
	 * Executes an update query on the value that has been appended to the
	 * builder.
	 * @param db the database instance on which the query should be executed
	 * @return the number of rows that were modified, or
	 *  {@link SQLiteSyntax#UPDATE_FAILED} if the update operation could not
	 *  proceed
	 */
	@Override
	public Integer executeOn( SQLiteDatabase db )
	{
		if( m_valsToWrite == null ) return SQLiteSyntax.UPDATE_FAILED ;
		try
		{
			return db.updateWithOnConflict(
					m_sTableName,
					m_valsToWrite,
					this.getWhereFormat(),
					this.getWhereParams(),
					m_zConflictAlgorithmID
				);
		}
		catch( Exception x )
		{
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Update for values [" )
					.append( m_valsToWrite.toString() )
					.append( "] failed: " )
					.toString()
				, x ) ;
			return SQLiteSyntax.UPDATE_FAILED ;
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( SQL_UPDATE ).append( m_sTableName )
		  .append( SQL_SET )
		  .append( toSQLInputParams( m_valsToWrite ) ) // TODO might be bogus
		  ;
		final String sWhere = this.getWhereClause() ;
		if( sWhere != null )
			sb.append( SQL_WHERE ).append( sWhere ) ;
		sb.append( " ;" ) ;
		return sb.toString() ;
	}
}
