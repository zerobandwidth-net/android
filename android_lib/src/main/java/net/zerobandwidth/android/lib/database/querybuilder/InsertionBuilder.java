package net.zerobandwidth.android.lib.database.querybuilder;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Builds a SQLite {@code INSERT} query.
 *
 * <h3>Examples</h3>
 *
 * Insert a row.
 *
 * <pre>
 * long nID = QueryBuilder.insertInto( sTableName )
 *     .setValues( vals )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * Insert a row, specifying that the operation should be rolled back if a
 * conflict arises.
 *
 * <pre>
 * long nID = QueryBuilder.insertInto( sTableName )
 *     .setValues( vals )
 *     .onConflict( SQLiteDatabase.CONFLICT_ROLLBACK )
 *     .executeOn( db )
 *     ;
 * </pre>
 *
 * Insert a series of rows.
 *
 * <pre>
 * Vector<Integer> vnIDs = new Vector<>() ;
 * for( ContentValues vals : aSeveralValues )
 * {
 *     nID = QueryBuilder.insertInto( sTableName )
 *         .setValues( vals )
 *         .executeOn( db )
 *         ;
 *     if( nID != SQLitePortal.INSERT_FAILED )
 *         vnIDs.add(nID) ;
 * }
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @see SQLiteDatabase#insertWithOnConflict(String, String, ContentValues, int)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class InsertionBuilder
extends QueryBuilder<InsertionBuilder,Long>
{
	protected static final String LOG_TAG =
			InsertionBuilder.class.getSimpleName() ;

	/**
	 * Magic number returned by {@link SQLiteDatabase#insert} and similar
	 * methods, when the insertion fails.
	 * @see SQLitePortal#INSERT_FAILED ;
	 */
	public static final long INSERT_FAILED = SQLitePortal.INSERT_FAILED ;

	protected static final String SQL_INSERT_INTO = "INSERT INTO " ;

	protected static final String SQL_SET = " SET " ;

	/**
	 * Specifies the column to use, if any, for the Android "null column hack"
	 * during insertion.
	 */
	protected String m_sNullableColumn = null ;

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

	public InsertionBuilder( String sTableName )
	{ super( sTableName ) ; }

	/**
	 * Sets the name of the nullable column to be used for the Android "null
	 * column hack".
	 * @param sColumnName the name of a nullable column
	 * @return (fluid)
	 */
	public InsertionBuilder withNullable( String sColumnName )
	{ m_sNullableColumn = sColumnName ; return this ; }

	/**
	 * Selects, by its numeric ID, the conflict resolution algorithm provided by
	 * Android.
	 * @param zAlgorithmID the ID of the algorithm to use
	 * @return (fluid)
	 */
	public InsertionBuilder onConflict( int zAlgorithmID )
	{ m_zConflictAlgorithmID = zAlgorithmID ; return this ; }

	/**
	 * Executes an insertion query on the values that have been appended to the
	 * builder.
	 *
	 * @param db the database instance on which the query should be executed.
	 * @return the ID of the newly-inserted row, or
	 *  {@link SQLitePortal#INSERT_FAILED} if the row could not be inserted
	 */
	@Override
	public Long executeOn( SQLiteDatabase db )
	{
		if( m_valsToWrite == null ) return SQLitePortal.INSERT_FAILED ;

		try
		{
			return db.insertWithOnConflict(
					m_sTableName,
					m_sNullableColumn,
					m_valsToWrite,
					m_zConflictAlgorithmID
				);
		}
		catch( Exception x )
		{
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Insertion of row with values [" )
					.append( m_valsToWrite.toString() )
					.append( "] failed:" )
					.toString()
				, x ) ;
			return INSERT_FAILED ;
		}
	}

	/**
	 * Constructs a raw SQL {@code INSERT} query based on the attributes of the
	 * builder instance.
	 * @return a raw SQLite {@code INSERT} query
	 */
	@Override
	public String toString()
	{
		return (new StringBuilder())
			.append( SQL_INSERT_INTO )
			.append( m_sTableName )
			.append( SQL_SET )
			.append( toSQLInputParams( m_valsToWrite ) )
			.append( " ;" )
			.toString()
			;
	}
}
