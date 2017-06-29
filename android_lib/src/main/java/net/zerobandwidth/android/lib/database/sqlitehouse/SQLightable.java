package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.ContentValues;

import net.zerobandwidth.android.lib.database.SQLitePortal;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * Designates a class as a data container which can be used in a database
 * defined and managed by {@link SQLiteHouse}. This class must also be decorated
 * by a {@link net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable}
 * annotation which defines the attributes of that table.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public interface SQLightable
{
}
