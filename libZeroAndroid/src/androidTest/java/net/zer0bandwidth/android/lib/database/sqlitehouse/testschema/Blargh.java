package net.zer0bandwidth.android.lib.database.sqlitehouse.testschema;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;

/**
 * Serves as one of the database schema definition classes for
 * {@link net.zer0bandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest}.
 *
 * This class intentionally isn't decorated as a {@code SQLiteTable} so that the
 * fallback mechanism for recognizing tables will be used.
 *
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
@SuppressWarnings("unused") // The "red herring" fields are intentionally unused.
public class Blargh
implements SQLightable
{
	/**
	 * Should be discovered as column {@code blargh_string} of type {@code TEXT}
	 * and default value {@code NULL}.
	 */
	@SQLiteColumn( name = "blargh_string" )
	protected String m_sString = null ;

	// None of the following should be discovered as columns.

	public String m_sRedHerring1 ;
	protected String m_sRedHerring2 ;
	protected int m_zRedHerring3 ;
	protected float m_rRedHerring4 ;
	protected boolean m_bRedHerring5 ;

	/** Default constructor required by {@code SQLiteHouse}. */
	public Blargh() {}

	/** Specifies the value of the string member. */
	public Blargh( String s )
	{ m_sString = s ; }

	/** Tests for the equality of the string member. */
	public boolean equals( Blargh that )
	{ return this.m_sString.equals( that.m_sString ) ; }
}
