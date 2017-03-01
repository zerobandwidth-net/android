package net.zerobandwidth.android.lib.database.querybuilder;

import android.database.sqlite.SQLiteDatabase;

/**
 * Builds a SQLite {@code DELETE} query.
 * @since zerobandwidth-net/android 0.1.1 (#20)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class DeletionBuilder
extends QueryBuilder<DeletionBuilder,Integer>
{
	public static final String DELETE_FROM = "DELETE FROM " ;

	public DeletionBuilder( String sTableName )
	{ super( sTableName ) ; }

	@Override
	public Integer executeOn( SQLiteDatabase db )
	{
		return 0 ; // TODO actual deletion query
	}
}
