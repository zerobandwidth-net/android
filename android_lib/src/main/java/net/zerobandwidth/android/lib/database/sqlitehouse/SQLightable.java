package net.zerobandwidth.android.lib.database.sqlitehouse;

/**
 * Designates a class as a data container which can be used in a database
 * defined and managed by {@link SQLiteHouse}. This class must also be decorated
 * by a {@link net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable}
 * annotation which defines the attributes of that table. Implementation classes
 * <b>must</b> also define a zero-argument constructor in order to be usable by
 * {@link SQLiteHouse#search}.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public interface SQLightable
{
}
