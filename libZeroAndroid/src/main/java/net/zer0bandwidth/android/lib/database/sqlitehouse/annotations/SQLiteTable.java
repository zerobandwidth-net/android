package net.zer0bandwidth.android.lib.database.sqlitehouse.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class that represents a table in a SQLite database and can
 * contain a row of that table.
 *
 * See {@link net.zer0bandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * for details of how this fits into the overall framework.
 *
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface SQLiteTable
{
	/**
	 * Specifies the name of the database table defined by this annotation, rows
	 * of which may be contained in instances of the decorated class.
	 * @return the database table name
	 */
	String value() ; // required

	/**
	 * Specifies the first schema version in which this table was defined. The
	 * {@code SQLiteHouse} will use this information to determine when and
	 * whether to add the table to the database during {@code onCreate()} or
	 * {@code onUpdate()}.
	 * @return the first database schema version that includes this table
	 */
	int since() default 1 ;
}
