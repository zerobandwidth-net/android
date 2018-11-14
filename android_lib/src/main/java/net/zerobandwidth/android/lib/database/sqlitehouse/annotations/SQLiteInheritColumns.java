package net.zerobandwidth.android.lib.database.sqlitehouse.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable}
 * classes to specify that a
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable.Reflection}
 * should examine the class's inheritance chain to pick up fields that are
 * annotated with {@link SQLiteColumn} in the parent classes.
 * @since zerobandwidth-net/android 0.2.1 (#56)
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface SQLiteInheritColumns
{
	/**
	 * Specifies the first schema version in which this class started inheriting
	 * columns from its ancestry. The inherited columns themselves might come
	 * from any version before or after this one.
	 * @return the first database schema version in which the reflected class
	 *  inherited any fields from its parents
	 * @see SQLiteColumn#since()
	 * @see SQLiteTable#since()
	 */
	int since() default 1 ;
}
