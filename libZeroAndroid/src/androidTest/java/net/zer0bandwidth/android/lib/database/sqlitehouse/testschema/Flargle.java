package net.zer0bandwidth.android.lib.database.sqlitehouse.testschema;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * A version-2 extension of {@link Fargle}, to test database upgrades.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( value = "fargles" )
@SuppressWarnings("unused") // They're used by proxy (named by DB column names)
public class Flargle
implements SQLightable
{
	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	public Flargle( int nID, String s, int z )
	{
		m_nFargleID = nID ;
		m_sString = s ;
		m_zInteger = z ;
	}

	@SQLiteColumn( name = "fargle_id", index = 0, is_nullable = false )
	@SQLitePrimaryKey
	protected int m_nFargleID ;

	@SQLiteColumn( name = "fargle_string", index = 1 )
	protected String m_sString = null ;

	@SQLiteColumn( name = "fargle_num", sql_default = "42" )
	protected int m_zInteger = 42 ;

	@SQLiteColumn( name = "flargle_addition", since = 2, sql_default = "NEW!" )
	protected String m_sAddition = "NEW!" ;

	/** @since zer0bandwidth-net/android 0.1.7 (#50) */
	public String getString()
	{ return m_sString ; }
}
