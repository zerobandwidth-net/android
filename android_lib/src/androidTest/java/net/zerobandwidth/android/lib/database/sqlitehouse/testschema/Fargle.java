package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * Serves as one of the database schema definition classes for
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest}.
 *
 * Columns should be sorted in the order {@code fargle_id},
 * {@code fargle_string}, {@code fargle_num}.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( "fargles" )
public class Fargle
implements SQLightable
{
	/**
	 * Should be discovered as column <code>fargle_id</code> of type
	 * <code>INT</code> and no default value.
	 */
	@SQLiteColumn( name = "fargle_id", index = 0, is_nullable = false )
	@SQLitePrimaryKey
	protected int m_nFargleID ;

	/**
	 * Should be discovered as column <code>fargle_string</code> of type
	 * <code>TEXT</code> and default value <code>NULL</code>.
	 */
	@SQLiteColumn( name = "fargle_string", index = 1 )
	protected String m_sString = null ;

	@SQLiteColumn( name = "fargle_num", index = 2 )
	protected int m_zInteger = 42 ;
}
