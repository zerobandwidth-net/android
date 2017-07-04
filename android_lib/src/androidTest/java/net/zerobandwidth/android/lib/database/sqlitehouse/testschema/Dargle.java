package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * Serves as one of the database schema definition classes for
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest}.
 *
 * Columns intentionally don't specify indices; they should be sorted
 * alphabetically, with {@code dargle_string} first, then {@code is_dargly}.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( "dargles" )
public class Dargle
implements SQLightable
{
	/**
	 * Should be discovered as column {@code dargle_string} of type {@code TEXT}
	 * and default value {@code "dargle"}.
	 */
	@SQLiteColumn( name = "dargle_string", is_nullable = false )
	@SQLitePrimaryKey
	protected String m_sString = "dargle" ;

	/**
	 * Should be discovered as column {@code is_dargly} of type {@code INTEGER}
	 * and default value {@code 1}.
	 */
	@SQLiteColumn( name = "is_dargly",
			sql_default = SQLitePortal.SQLITE_TRUE_INTSTRING )
	protected boolean m_bBoolean = true ;

	/**
	 * Should not be discovered as a column.
	 */
	protected int m_zIgnoreThisField = -1 ;

	/**
	 * Fully initializes an instance.
	 * @param s a string
	 * @param b a Boolean value
	 * @param z an ignored integer
	 */
	public Dargle( String s, boolean b, int z )
	{
		m_sString = s ;
		m_bBoolean = b ;
		m_zIgnoreThisField = z ;
	}

	/**
	 * Accesses the string.
	 * @return the string member
	 */
	public String getString()
	{ return m_sString ; }

	/**
	 * Toggles the Boolean member.
	 * @see net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest#testUpdate
	 * @return (fluid)
	 */
	public Dargle toggle()
	{ m_bBoolean = ! m_bBoolean ; return this ; }
}
