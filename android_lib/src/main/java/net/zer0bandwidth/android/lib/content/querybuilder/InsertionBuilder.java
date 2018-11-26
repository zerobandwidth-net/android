package net.zer0bandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

/**
 * Builds an insertion query against a given {@link ContentResolver} and
 * {@link Uri}.
 *
 * <h3>Examples</h3>
 *
 * <p>Insert a row.</p>
 *
 * <pre>
 *     long nID = QueryBuilder.insertInto( rslv, uri )
 *         .setValues( vals )
 *         .execute()
 *         ;
 * </pre>
 *
 * <p>Insert a collection of rows.</p>
 *
 * <pre>
 *     Vector&lt;Uri&gt; vnIDs = new Vector&lt;&gt;() ;
 *     for( ContentValues vals : aSeveralValues )
 *     {
 *         Uri uriID = QueryBuilder.insertInto( rslv, uri )
 *             .setValues( vals )
 *             .execute()
 *             ;
 *         if( uriID != InsertionBuilder.FAILED )
 *             vnIDs.add(uriID) ;
 *     }
 * </pre>
 *
 * @since zer0bandwidth-net/android 0.1.7 (#39)
 */
public class InsertionBuilder
extends QueryBuilder<InsertionBuilder,Uri>
{
	protected static final String LOG_TAG =
			InsertionBuilder.class.getSimpleName() ;


	public static final Uri FAILED = null ;

	public InsertionBuilder( ContentResolver rslv, Uri uri )
	{ super( rslv, uri ) ; }

	public InsertionBuilder( Context ctx, Uri uri )
	{ super( ctx, uri ) ; }

	public InsertionBuilder()
	{ super() ; }

	/**
	 * Inserts the data values in this builder into the given data context.
	 * If no values are specified in the builder, then the method will return
	 * {@code null} trivially.
	 * @return the URI of the inserted data, or {@code null} if no values were
	 *  supplied
	 */
	@Override
	public Uri executeQuery( ContentResolver rslv, Uri uri )
	throws Exception
	{
		if( m_valsToWrite == null ) return null ;
		return rslv.insert( uri, m_valsToWrite ) ;
	}
}
