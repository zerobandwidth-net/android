package net.zerobandwidth.android.lib.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Mocks an actual database cursor. The {@link #m_aValues} element holds an
 * array of rows that can be traversed.
 *
 * @since zerobandwidth-net/android 0.1.5 (#42)
 */
public class MockCursor
implements Cursor
{
	/** @see #m_nPosition */
	public static final int NOT_STARTED = -1 ;

	/** @see #getColumnIndex(String) */
	public static final int COLUMN_NOT_FOUND = -1 ;

	/** Data set for the mock cursor. */
	protected ArrayList<ContentValues> m_aValues = null ;

	/** Current position in the data set. */
	protected int m_nPosition = NOT_STARTED ;

	/**
	 * @see #setExtras
	 * @see #getExtras
	 */
	protected Bundle m_bndlExtras = Bundle.EMPTY ;

	public MockCursor()
	{
		m_aValues = new ArrayList<>() ;
		m_nPosition = NOT_STARTED ;
	}

	public MockCursor( ContentValues vals )
	{
		m_aValues = new ArrayList<>() ;
		m_aValues.add( vals ) ;
		m_nPosition = NOT_STARTED ;
	}

	public MockCursor( Collection<ContentValues> aValues )
	{ this.setValues( aValues ) ; }

	public ArrayList<ContentValues> getValues()
	{ return m_aValues; }

	public MockCursor setValues( Collection<ContentValues> aValues )
	{
		if( m_aValues == null ) m_aValues = new ArrayList<>( aValues.size() ) ;
		m_aValues.addAll(aValues) ;
		m_nPosition = NOT_STARTED ;
		return this ;
	}

	@Override
	public int getCount()
	{ return m_aValues.size() ; }

	@Override
	public int getPosition()
	{ return m_nPosition ; }

	@Override
	public boolean move( int zOffset )
	{
		m_nPosition += zOffset ;
		return this.assessPosition() ;
	}

	@Override
	public boolean moveToPosition( int zPosition )
	{
		m_nPosition = zPosition ;
		return this.assessPosition() ;
	}

	/**
	 * Pins position to {@link #NOT_STARTED} or the size of the list if the
	 * new position is out of bounds; otherwise returns {@code true}.
	 * @return {@code true} iff the position was valid
	 * @see #move(int)
	 * @see #moveToPosition(int)
	 */
	protected boolean assessPosition()
	{
		if( m_nPosition >= m_aValues.size() )
		{ m_nPosition = m_aValues.size() - 1 ; return false ; }
		else if( m_nPosition < 0 )
		{ m_nPosition = NOT_STARTED ; return false ; }

		return true ;
	}

	@Override
	public boolean moveToFirst()
	{
		if( m_aValues.size() > 0 )
		{
			m_nPosition = 0 ;
			return true ;
		}
		m_nPosition = NOT_STARTED ;
		return false ;
	}

	@Override
	public boolean moveToLast()
	{
		if( m_aValues.size() > 0 )
		{
			m_nPosition = m_aValues.size() - 1 ;
			return true ;
		}
		m_nPosition = 0 ;
		return false ;
	}

	@Override
	public boolean moveToNext()
	{
		++m_nPosition ;
		return this.assessPosition() ;
	}

	@Override
	public boolean moveToPrevious()
	{
		--m_nPosition ;
		return this.assessPosition() ;
	}

	@Override
	public boolean isFirst()
	{ return ( m_aValues.size() > 0 && m_nPosition == 0 ) ; }

	@Override
	public boolean isLast()
	{ return ( m_aValues.size() > 0 && m_nPosition == m_aValues.size() - 1 ) ; }

	@Override
	public boolean isBeforeFirst()
	{ return ( m_nPosition == NOT_STARTED ) ; }

	@Override
	public boolean isAfterLast()
	{ return( m_nPosition >= m_aValues.size() ) ; }

	@Override
	public int getColumnIndex( String sColName )
	{
		if( TextUtils.isEmpty(sColName) ) return COLUMN_NOT_FOUND ;

		String[] asKeys = this.getColumnNames() ;
		for( int n = 0 ; n < asKeys.length ; n++ )
		{
			if( asKeys[n].equals( sColName ) )
				return n ;
		}
		return COLUMN_NOT_FOUND ;
	}

	@Override
	public int getColumnIndexOrThrow( String sColName )
	throws IllegalArgumentException
	{
		int n = this.getColumnIndex(sColName) ;
		if( n == COLUMN_NOT_FOUND ) throw new IllegalArgumentException() ;
		return n ;
	}

	@Override
	public String getColumnName( int nIndex )
	{
		String[] asKeys = this.getColumnNames() ;
		if( nIndex < 0 || nIndex >= asKeys.length ) return null ;
		return asKeys[nIndex] ;
	}

	@Override
	public String[] getColumnNames()
	{ return m_aValues.get(m_nPosition).keySet().toArray( new String[0] ) ; }

	@Override
	public int getColumnCount()
	{ return this.getColumnNames().length ; }

	@Override
	public byte[] getBlob( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsByteArray(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public String getString( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsString(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public void copyStringToBuffer( int nIndex, CharArrayBuffer cb )
	{
		// Not worth implementing in the scope of this issue.
		throw new UnsupportedOperationException() ;
	}

	@Override
	public short getShort( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsShort(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public int getInt( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsInteger(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public long getLong( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsLong(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public float getFloat( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsFloat(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public double getDouble( int nIndex )
	{
		return m_aValues.get(m_nPosition).getAsDouble(
				this.getColumnNames()[nIndex] ) ;
	}

	@Override
	public int getType( int nIndex )
	{
		// Not worth implementing in the scope of this issue.
		throw new UnsupportedOperationException() ;
	}

	@Override
	public boolean isNull( int nIndex )
	{
		return ( m_aValues.get(m_nPosition)
		                  .get( this.getColumnNames()[nIndex] ) == null ) ;
	}

	@Override
	@Deprecated
	public void deactivate()
	{
		// Do nothing.
	}

	@Override
	@Deprecated
	public boolean requery()
	{
		// Do nothing.
		return true ;
	}

	@Override
	public void close()
	{
		// Just kidding; do nothing.
	}

	@Override
	public boolean isClosed()
	{
		return false ;
	}

	@Override
	public void registerContentObserver( ContentObserver observer )
	{
		// Do nothing; contents won't change.
	}

	@Override
	public void unregisterContentObserver( ContentObserver observer )
	{
		// Do nothing; contents won't change.
	}

	@Override
	public void registerDataSetObserver( DataSetObserver observer )
	{
		// Do nothing; contents won't change.
	}

	@Override
	public void unregisterDataSetObserver( DataSetObserver observer )
	{
		// Do nothing; contents won't change.
	}

	@Override
	public void setNotificationUri( ContentResolver cr, Uri uri )
	{
		// Not worth implementing in the scope of this issue.
		throw new UnsupportedOperationException() ;
	}

	@Override
	public Uri getNotificationUri()
	{ return null ; }

	@Override
	public boolean getWantsAllOnMoveCalls()
	{ return true ; }

	@Override
	public void setExtras( Bundle bndl )
	{ m_bndlExtras = bndl ; }

	@Override
	public Bundle getExtras()
	{ return m_bndlExtras ; }

	@Override
	public Bundle respond( Bundle bndl )
	{ return bndl ; }
}
