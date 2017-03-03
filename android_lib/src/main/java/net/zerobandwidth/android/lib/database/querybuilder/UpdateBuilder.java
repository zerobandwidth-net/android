package net.zerobandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Builds a SQLite {@code UPDATE} query.
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#updateWithOnConflict(String, ContentValues, String, String[], int)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class UpdateBuilder
extends QueryBuilder<UpdateBuilder,Integer>
{
	protected static final String LOG_TAG = UpdateBuilder.class.getSimpleName();

	/**
	 * Similar to the standard magic number returned when an insertion query
	 * fails (see {@link SQLitePortal#INSERT_FAILED}, this return value from
	 * {@link #executeOn(SQLiteDatabase)} indicates that the update operation
	 * could not be carried out.
	 */
	public static final int UPDATE_FAILED = -1 ;

	protected static final String SQL_UPDATE = "UPDATE " ;

	protected static final String SQL_SET = " SET " ;

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
	 * Convenience grammar specifying that all rows should be deleted.
	 * @return (fluid)
	 */
	public UpdateBuilder updateAll()
	{ return this.where( SQLitePortal.UPDATE_ALL ) ; }

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
	 * @return the number of rows that were modified, or {@link #UPDATE_FAILED}
	 *  if the update operation could not proceed
	 */
	@Override
	public Integer executeOn( SQLiteDatabase db )
	{
		if( m_valsToWrite == null ) return UPDATE_FAILED ;
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
			return UPDATE_FAILED ;
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
