package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * Used to test {@link SQLiteHouse}'s upgrade algorithm.
 * @see SQLiteHouseTest#testDatabaseUpgrade
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( value = "quargles", since = 2 )
public class Quargle
implements SQLightable
{
	@SQLiteColumn( name = "quargle" )
	protected String m_sQuargle = null ;
}
