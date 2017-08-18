package net.zerobandwidth.android.lib.content.querybuilder;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.OperationCanceledException;

import net.zerobandwidth.android.lib.database.MockCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Used to exercise {@link QueryBuilder} and its implementation classes.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
public class MockContentProvider
extends android.test.mock.MockContentProvider
{
	public static final int EXPECTED_DELETE_COUNT = 4 ;

	public static final Uri EXPECTED_INSERT_URI =
			Uri.parse( "content://net.foo/bar/baz" ) ;

	public static final int EXPECTED_SELECT_CURSOR_SIZE = 5 ;

	public static final int EXPECTED_UPDATE_COUNT = 21 ;

	public static final MockCursor EXPECTED_SELECT_CURSOR = new MockCursor() ;
	static
	{
		List<ContentValues> avals = new ArrayList<>() ;
		for( int i = 0 ; i < EXPECTED_SELECT_CURSOR_SIZE ; i++ )
		{
			ContentValues vals = new ContentValues() ;
			vals.put( "id", UUID.randomUUID().toString() ) ;
			vals.put( "intval", i ) ;
			avals.add(vals) ;
		}
		EXPECTED_SELECT_CURSOR.setValues( avals ) ;
	}

	protected boolean m_bBrokenSelect = false ;

	/** Allows the caller to force selection queries to be broken. */
	public MockContentProvider setBrokenSelect( boolean b )
	{ m_bBrokenSelect = b ; return this ; }

	/** @return {@link #EXPECTED_DELETE_COUNT} */
	@Override
	public int delete( Uri uri, String sWhereFormat, String[] asWhereParams )
	{ return EXPECTED_DELETE_COUNT ; }

	/** @return {@link #EXPECTED_INSERT_URI} */
	@Override
	public Uri insert( Uri uri, ContentValues vals )
	{ return EXPECTED_INSERT_URI ; }

	/** @return {@link #EXPECTED_SELECT_CURSOR} */
	@Override
	public Cursor query( Uri uri, String[] asColumns, String sWhereFormat,
	                     String[] asWhereParams, String sOrderBy )
	{
		if( Build.VERSION.SDK_INT > 16 && m_bBrokenSelect )
			throw new OperationCanceledException() ;
		return EXPECTED_SELECT_CURSOR ;
	}

	/** @return {@link #EXPECTED_UPDATE_COUNT} */
	@Override
	public int update( Uri uri, ContentValues vals, String sWhereFormat,
	                   String[] asWhereParams )
	{ return EXPECTED_UPDATE_COUNT; }
}
