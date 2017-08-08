package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.NullRefractor;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Correlates a member field with a corresponding column in an SQLite database
 * table.
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
	int NO_INDEX_DEFINED = -1 ;

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
	 * @see net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse.ColumnIndexComparator
	 */
	int index() default NO_INDEX_DEFINED ;

	/**
	 * Specifies whether the database column should be nullable. The default,
	 * {@code true}, will explicitly allow null values. Setting this attribute
	 * to {@code false} will explicitly forbid null values.
	 * @return indicates whether the column should allow null values
	 */
	boolean is_nullable() default true ;

	/**
	 * Specifies the <b>string</b> which would be used in a SQLite
	 * {@code CREATE TABLE} statement to define the field's default value.
	 *
	 * <p><b>Field Declaration:</b> Unfortunately it is not possible to
	 * introspectively use the default value that is declared for the field in
	 * the class itself. However, since we specify the default in this
	 * annotation, we also allow the consumer to intentionally define something
	 * different from the field's declared default. Whether this freedom is of
	 * any interesting use is left as an exercise for the reader.</p>
	 *
	 * <p><b>String Enclosure:</b> If the field decorated by this annotation is
	 * a string type, then {@code SQLiteHouse} will automatically surround the
	 * value with quotes as appropriate. An explicit value of {@code "NULL"} for
	 * this parameter will instruct {@code SQLiteHouse} to define the default as
	 * a literal {@code NULL} instead. Do not try to enclose a text value in
	 * single-quotes.</p>
	 *
	 * <p><b>Validation:</b> The algorithms that process the supplied value
	 * make no attempt to validate the value with regards to its correspondence
	 * to the field's type, etc. For example, if the annotated field is an
	 * integer, and a non-numeric string value is supplied here, then you will
	 * eventually see exceptions raised from parts of the code that try to
	 * parse or use the value.</p>
	 *
	 * <p><b>Handling Null Default Values:</b>The default value for the
	 * annotation parameter is {@code "NULL"}. If the column is nullable, and
	 * this annotation parameter is not specified or is set to {@code "NULL"}
	 * explicitly, then the column will be defined with {@code DEFAULT NULL}.
	 * Otherwise, if the column is not nullable, then this annotation parameter
	 * will effectively be required, as a default value must be set for
	 * non-nullable columns.</p>
	 *
	 * @return the string which would specify the default in a SQLite column
	 *  definition clause
	 */
	String sql_default() default "NULL" ;

	/**
	 * Specifies the first schema version in which this column was defined. The
	 * {@code SQLiteHouse} will use this information to determine when and
	 * whether to add the column to the database during {@code onCreate()} or
	 * {@code onUpdate()}.
	 * @return the first database schema version that includes this column
	 */
	int since() default 1 ;

	/**
	 * Optionally specifies a custom {@link Refractor} implementation to be used
	 * to marshal this column to/from the database. This defaults to the
	 * stand-in {@link NullRefractor} class, which will behave as if "null"
	 * were specified here (but we can't, because annotations don't let you do
	 * that).
	 * @return the custom refractor implementation to be used for this column
	 * @since zerobandwidth-net/android 0.1.5 (#41)
	 */
	Class<? extends Refractor> refractor() default NullRefractor.class ;

}
