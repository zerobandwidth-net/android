package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a column as the primary key of its table.
 *
 * <p>Because SQLite prefers to have auto-incremented integer keys in all of its
 * tables, the definition of "primary key" here is a bit loose. Rather than
 * trying to declare this column as a primary key, {@code SQLiteHouse} will
 * actually create this table as merely {@code UNIQUE} but then <i>treat</i>
 * the column as a primary key for all operations that would want one, including
 * key-based searches, references across tables, etc. Meanwhile, it will
 * silently also manage a <code>_id</code> field as the <i>actual</i> primary
 * key that is reported to SQLite.</p>
 *
 * <p>See {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * for details of how this fits into the overall framework.</p>
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface SQLitePrimaryKey
{
}
