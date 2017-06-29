package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class that defines a SQLite database.
 * This should be applied only to classes that extend
 * {@link new.zerobandwidth.android.lib.database.SQLiteHouse}.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface SQLiteDatabaseSpec
{
	String database_name() ;
	int schema_version() default 1 ;
	Class[] classes() default {} ;
}
