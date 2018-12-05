package net.zer0bandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

/**
 * Builds a deletion query against a given {@link ContentResolver} and
 * {@link Uri}.
 *
 * <h3>Examples</h3>
 *
 * <p>Delete all records in the data context:</p>
 *
 * <pre>
 *     int nDeleted = QueryBuilder.deleteFrom( rslv, uri )
 *             .deleteAll()
 *             .execute()
 *             ;
 * </pre>
 *
 * <p>Delete selected records from a data context:</p>
 *
 * <pre>
 *     int nDeleted = QueryBuilder.deleteFrom( rslv, uri )
 *             .where( "reasons_to_delete>?", Integer.toString(0) )
 *             .execute()
 *             ;
 * </pre>
 *
 * @since zer0bandwidth-net/android 0.1.7 (#39)
 */
public class DeletionBuilder
extends QueryBuilder<DeletionBuilder,Integer>
{
	public DeletionBuilder( ContentResolver rslv, Uri uri )
	{ super( rslv, uri ) ; }

	public DeletionBuilder( Context ctx, Uri uri )
	{ super( ctx, uri ) ; }

	public DeletionBuilder()
	{ super() ; }

	/**
	 * Convenience grammar specifying that all rows should be deleted from the
	 * data context.
	 * @return (fluid)
	 */
	public DeletionBuilder deleteAll()
	{ return this.where( null ) ; }

	/**
	 * Deletes rows from the data context based on the builder's current state.
	 * @return the number of rows deleted
	 */
	@Override
	public Integer executeQuery( ContentResolver rslv, Uri uri )
	throws Exception
	{
		return rslv.delete( uri,
				this.getWhereFormat(), this.getWhereParams() ) ;
	}
}
