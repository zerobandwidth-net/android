package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * Serves as one of the database schema definition classes for
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest}.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( "dargles" )
public class Dargle
implements SQLightable
{
	/**
	 * Should be discovered as column <code>dargle_string</code> of type
	 * <code>TEXT</code> and default value <code>"dargle"</code>.
	 */
	@SQLiteColumn( value = "dargle_string", is_nullable = false )
	@SQLitePrimaryKey
	protected String m_sString = "dargle" ;

	/**
	 * Should be discovered as column <code>is_dargly</code> of type
	 * <code>INT</code> and default value <code>1</code>.
	 */
	@SQLiteColumn( "is_dargly" )
	protected boolean m_bBoolean = true ;

	/**
	 * Should not be discovered as a column.
	 */
	protected int m_zIgnoreThisField = -1 ;
}
