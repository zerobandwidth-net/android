package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Correlates a member field with a corresponding column in an SQLite database
 * table.
 *
 * <p>The discovery algorithm will use the default value supplied in the member
 * field declaration to determine a {@code DEFAULT} value for the database
 * column schema.</p>
 *
 * <p>See {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse}
 * for details of how this fits into the overall framework.</p>
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface SQLiteColumn
{
	/**
	 * Specifies the name of the database table column whose schema definition
	 * is derived from this member field; in instances of the class, this field
	 * will contain the values from that database table column.
	 * @return the name of this field's database corresponding table column
	 */
	String name() ; // required

	/**
	 * Roughly-specifies the column index. This is <i>not</i> a one-to-one
	 * concordance; rather, it is used as a sorting order when organizing
	 * columns for table creation. <i>Do not</i> depend on this index as a
	 * one-to-one concordance of columns to numeric indices.
	 * @return a rough estimation of column index, which is really a sort
	 *  order
	 */
	int index() default 0 ;

	/**
	 * Specifies whether the database column should be nullable. The default,
	 * {@code true}, will explicitly allow null values. Setting this attribute
	 * to {@code false} will explicitly forbid null values.
	 * @return indicates whether the column should allow null values
	 */
	boolean is_nullable() default true ;
}
