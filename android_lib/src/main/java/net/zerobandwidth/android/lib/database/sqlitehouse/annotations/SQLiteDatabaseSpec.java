package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class that defines a SQLite database.
 * This should be applied only to classes that extend
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface SQLiteDatabaseSpec
{
	/**
	 * Specifies the name of the database file, as usually supplied to the
	 * constructor of {@code SQLiteOpenHelper} or {@code SQLitePortal}.
	 * @return the database name
	 */
	String database_name() ; // required

	/**
	 * The current schema version for the database overall, as usually supplied
	 * to the constructor of an {@code SQLiteOpenHelper} or
	 * {@code SQLitePortal}.
	 * @return the current database schema version
	 */
	int schema_version() default 1 ;

	/**
	 * A list of classes that constitute the data set being stored in the
	 * database. Each of these classes must be decorated with an
	 * {@link SQLiteTable} annotation and must be capable of containing a row
	 * of that table.
	 * @return the list of classes that represent tables of the database
	 */
	Class<? extends SQLightable>[] classes() default {} ;
}
