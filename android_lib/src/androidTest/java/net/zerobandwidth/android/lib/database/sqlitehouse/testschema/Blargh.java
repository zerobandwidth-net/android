package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;

/**
 * Serves as one of the database schema definition classes for
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest}.
 *
 * This class intentionally isn't decorated as a {@code SQLiteTable} so that the
 * fallback mechanism for recognizing tables will be used.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class Blargh
implements SQLightable
{
	/**
	 * Should be discovered as column <code>blargh_string</code> of type
	 * <code>TEXT</code> and default value <code>NULL</code>.
	 */
	@SQLiteColumn( name = "blargh_string" )
	protected String m_sString = null ;

	// None of the following should be discovered as columns.

	protected String m_sRedHerring1 ;
	protected String m_sRedHerring2 ;
	protected int m_zRedHerring3 ;
	protected float m_rRedHerring4 ;
	protected boolean m_bRedHerring5 ;
}
