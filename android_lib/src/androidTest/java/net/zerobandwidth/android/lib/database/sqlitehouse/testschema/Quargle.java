package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightableReflectionTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse.MAGIC_ID_COLUMN_NAME;

/**
 * Used to test {@link SQLiteHouse}'s upgrade algorithm, and
 * {@link SQLightable.Reflection}'s ability to handle classes that marshal the
 * magic auto-incremented ID column, but do not define their own keys.
 * @see SQLiteHouseTest#testDatabaseUpgrade
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SQLiteTable( value = "quargles", since = 2 )
public class Quargle
implements SQLightable
{
	/**
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 * @see SQLightableReflectionTest#testGetKeyOrMagicIDOnQuargle()
	 */
	@SQLiteColumn( name = MAGIC_ID_COLUMN_NAME, sql_default = "-1" )
	protected long m_nID = -1 ;

	@SQLiteColumn( name = "quargle" )
	@SuppressWarnings("unused")
	protected String m_sQuargle = null ;
}
