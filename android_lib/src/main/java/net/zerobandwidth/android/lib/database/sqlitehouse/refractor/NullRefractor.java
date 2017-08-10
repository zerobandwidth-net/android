package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

/**
 * This "implementation" of {@link Refractor} doesn't actually do anything, nor
 * does it satisfactorily implement the interface. Instead, much like the
 * {@code Void} class, this class is a stand-in for "I don't really have a
 * refractor." Its primary use is as a null-ish value in the {@code refractor}
 * property of the {@code SQLiteColumn} annotation.
 * @see net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn
 * @since zerobandwidth-net/android 0.1.5 (#41)
 */
public abstract class NullRefractor
implements Refractor<Void>
{
}
