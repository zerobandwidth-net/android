package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * Builds an insertion query against a given {@link ContentResolver} and
 * {@link Uri}.
 *
 * <h3>Examples</h3>
 *
 * <p>Update all rows.</p>
 *
 * <pre>
 *     int nUpdated = QueryBuilder.update( rslv, uri )
 *             .setValues( vals )
 *             .updateAll()
 *             .execute()
 *             ;
 * </pre>
 *
 * <p>Update specific rows.</p>
 *
 * <pre>
 *     int nUpdated = QueryBuilder.update( rslv, uri )
 *             .setValues( vals )
 *             .where( "some_column=?", sSomeValue )
 *             .execute()
 *             ;
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.7 (#38)
 */
public class UpdateBuilder
extends QueryBuilder<UpdateBuilder,Integer>
{
	public static final String LOG_TAG = UpdateBuilder.class.getSimpleName() ;

	public UpdateBuilder( ContentResolver rslv, Uri uri )
	{ super( rslv, uri ) ; }

	public UpdateBuilder( Context ctx, Uri uri )
	{ super( ctx, uri ) ; }

	public UpdateBuilder()
	{ super() ; }

	/**
	 * Convenience grammar specifying that all rows should be updated.
	 * @return (fluid)
	 */
	public UpdateBuilder updateAll()
	{ return this.where(null) ; }

	@Override
	public Integer executeQuery( ContentResolver rslv, Uri uri )
	throws Exception
	{
		if( m_valsToWrite == null )
		{
			Log.i( LOG_TAG, "Trivial update: no values were specified." ) ;
			return 0 ;
		}

		return rslv.update( uri, m_valsToWrite,
				m_sExplicitWhereFormat, m_asExplicitWhereParams ) ;
	}
}
