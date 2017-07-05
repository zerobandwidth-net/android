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
	 * Should be discovered as column {@code fargle_id} of type {@code INTEGER}
	 * and no default value.
	 */
	@SQLiteColumn( name = "fargle_id", index = 0, is_nullable = false )
	@SQLitePrimaryKey
	protected int m_nFargleID ;

	/**
	 * Should be discovered as column {@code fargle_string} of type {@code TEXT}
	 * and default value {@code NULL}.
	 */
	@SuppressWarnings("DefaultAnnotationParam") // We're testing this explicitly.
	@SQLiteColumn( name = "fargle_string", index = 1, sql_default = "NULL" )
	protected String m_sString = null ;

	/**
	 * Should be discovered as column {@code fargle_num} of type {@code INTEGER}
	 * and default value {@code 42}. Should be sorted after
	 * {@code fargle_string} because its column index is unspecified and all
	 * other columns have specific column numbers.
	 */
	@SQLiteColumn( name = "fargle_num", sql_default = "42" )
	protected int m_zInteger = 42 ;

	public Fargle() {}

	/**
	 * Fully initializes an instance.
	 * @param nID a unique numeric ID
	 * @param s a string
	 * @param z an integer
	 */
	public Fargle( int nID, String s, int z )
	{
		m_nFargleID = nID ;
		m_sString = s ;
		m_zInteger = z ;
	}

	/**
	 * Tests for equality.
	 * @param that the object to be compared to this one.
	 * @return {@code true} if all fields are equivalent
	 */
	public boolean equals( Fargle that )
	{
		return ( this.m_nFargleID == that.m_nFargleID
		      && this.m_sString.equals( that.m_sString )
		      && this.m_zInteger == that.m_zInteger ) ;
	}
}