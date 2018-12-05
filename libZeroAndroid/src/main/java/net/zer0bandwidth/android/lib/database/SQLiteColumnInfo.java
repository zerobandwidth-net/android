package net.zer0bandwidth.android.lib.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information returned by the {@code table_info} pragma in SQLite.
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
public class SQLiteColumnInfo
{
	/**
	 * Gathers information about the columns in the specified table, and returns
	 * the column descriptions in a list.
	 * @param db the database to be analyzed
	 * @param sTableName the name of the table to be analyzed
	 * @return a list of objects describing the columns of the table
	 * @see SQLitePortal#getColumnListForTable
	 */
	public static List<SQLiteColumnInfo> gatherColumnList(
			SQLiteDatabase db, String sTableName )
	{
		Cursor crs = null ;
		List<SQLiteColumnInfo> aInfo = new ArrayList<>() ;
		try
		{
			crs = db.rawQuery(
					String.format( "PRAGMA main.table_info('%s')", sTableName ),
					null ) ;
			if( crs.moveToFirst() )
			{
				do
				{
					SQLiteColumnInfo info = new SQLiteColumnInfo() ;
					info.sTableName = sTableName ;
					info.nColumnID = crs.getInt(0) ;
					info.sColumnName = crs.getString(1) ;
					info.sColumnType = crs.getString(2) ;
					info.bNotNull = SQLitePortal.intToBool( crs.getInt(3) ) ;
					info.sDefault = crs.getString(4) ;
					info.bPrimaryKey = SQLitePortal.intToBool( crs.getInt(5) ) ;
					aInfo.add(info) ;
				} while( crs.moveToNext() ) ;
			}
		}
		finally
		{ SQLitePortal.closeCursor(crs) ; }

		return aInfo ;
	}

	/**
	 * Gathers information about the columns in the specified table, and returns
	 * the column descriptions in a map.
	 * @param db the database to be analyzed
	 * @param sTableName the name of the table to be analyzed
	 * @return a map of column names to objects describing those columns
	 * @see SQLitePortal#getColumnMapForTable
	 */
	public static Map<String,SQLiteColumnInfo> gatherColumnMap(
			SQLiteDatabase db, String sTableName )
	{
		List<SQLiteColumnInfo> aInfo = gatherColumnList( db, sTableName ) ;
		Map<String,SQLiteColumnInfo> mapInfo = new HashMap<>() ;
		for( SQLiteColumnInfo info : aInfo )
			mapInfo.put( info.sColumnName, info ) ;
		return mapInfo ;
	}

	/** The name of the table containing this column. */
	public String sTableName = null ;

	/** The numeric ID of the column in any given row. */
	public int nColumnID = -1 ;

	/** The name of the column in the table definition. */
	public String sColumnName = null ;

	/** The magic SQLite token indicating this column's data type. */
	public String sColumnType = null ;

	/** Indicates whether the column is defined {@code NOT NULL}. */
	public boolean bNotNull = false ;

	/** The default value of the column, as a string. */
	public String sDefault = null ;

	/** Indicates whether the column is the table's primary key. */
	public boolean bPrimaryKey = false ;
}
