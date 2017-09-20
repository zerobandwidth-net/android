package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import android.util.Log;

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
	public static final String LOG_TAG = Fargle.class.getSimpleName() ;

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

	@SuppressWarnings( "unused" ) // Invoked implicitly by reflection.
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
	 * Accesses the string field.
	 * @return the string field
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public String getString()
	{ return m_sString ; }

	/**
	 * Allows a test method to modify the contents of the string field.
	 * @param s the new string value
	 * @return (fluid)
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public Fargle setString( String s )
	{ m_sString = s ; return this ; }

	/**
	 * Tests for equality, writing detailed debug logs whenever the objects are
	 * not equal. The logging is intended to aid in diagnosis of unit test
	 * failures.
	 * @param that the object to be compared to this one.
	 * @return {@code true} if all fields are equivalent
	 */
	public boolean equals( Fargle that )
	{
		if( this.m_nFargleID != that.m_nFargleID )
		{
			Log.d( LOG_TAG, String.format( "m_nFargleID [%d] [%d]",
					this.m_nFargleID, that.m_nFargleID ) ) ;
			return false ;
		}
		if( this.m_sString == null )
		{
			if( that.m_sString != null )
			{
				Log.d( LOG_TAG, String.format( "m_sString <null> [%s]",
						that.m_sString ) ) ;
				return false ;
			}
		}
		else if( that.m_sString == null )
		{
			Log.d( LOG_TAG, String.format( "m_sString [%s] <null>",
					this.m_sString ) ) ;
			return false ;
		}
		else if( ! this.m_sString.equals( that.m_sString ) )
		{
			Log.d( LOG_TAG, String.format( "m_sString [%s] [%s]",
					this.m_sString, that.m_sString ) ) ;
			return false ;
		}
		if( this.m_zInteger != that.m_zInteger )
		{
			Log.d( LOG_TAG, String.format( "m_zInteger [%d] [%d]",
					this.m_zInteger, that.m_zInteger ) ) ;
			return false ;
		}

		return true ;
	}

	/**
	 * As {@link #equals(Fargle)}, but ignores {@link #m_nFargleID}.
	 * Use this when comparing a new instance whose ID might still be {@code -1}
	 * to another instance which has already been inserted into a DB and has a
	 * real ID.
	 * @param that the object to be compared to this one
	 * @return {@code true} if the non-ID content is equivalent
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public boolean matches( Fargle that )
	{
		return( this.m_sString.equals( that.m_sString )
		     && this.m_zInteger == that.m_zInteger ) ;
	}
}
