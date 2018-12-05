package net.zer0bandwidth.android.lib.database;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Provides an implementation of {@link SQLiteAssetPortal} which will test that
 * class's ability to create and manage a read-only database constructed from an
 * asset file.
 * @since zer0bandwidth-net/android 0.1.4 (#34)
 */
public class SQLiteAssetTestDB
extends SQLiteAssetPortal
{
//	public static final String LOG_TAG =
//			SQLiteAssetTestDB.class.getSimpleName() ;

	public static final String TEST_DATABASE_NAME = "asset_test_db" ;

	public static final String TEST_ASSET_NAME_FORMAT = "db_asset_test.v%d.db" ;

	protected int m_nVersion = 0 ;

	public SQLiteAssetTestDB( Context ctx, int nVersion )
	{
		super( ctx, TEST_DATABASE_NAME, null, nVersion ) ;
		m_nVersion = nVersion ;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String getAssetName()
	{ return String.format( TEST_ASSET_NAME_FORMAT, m_nVersion ) ; }
}
