package net.zer0bandwidth.android.lib.database.sqlitehouse.testschema;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;

/**
 * Used as the canonical "valid" {@link SQLiteHouse} implementation for
 * various unit tests. This class defines a database that includes tables
 * marshalled by {@link Fargle}, {@link Dargle}, and {@link Blargh}.
 * @since zer0bandwidth-net/android 0.1.4 (#26), extracted from
 *  {@link SQLiteHouseTest} in 0.1.7 (#50)
 */
@SuppressWarnings( "DefaultAnnotationParam" )
@SQLiteDatabaseSpec(
		database_name = "valid_spec_class_db",
		schema_version = 1,
		classes = { Fargle.class, Dargle.class, Blargh.class }
)
public class ValidSpecClass
extends SQLiteHouse<ValidSpecClass>
{
	/**
	 * Constructs an instance of the test DB class, bound to the test
	 * environment's instrumentation context.
	 * @return an instance of the test DB class
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public static ValidSpecClass getTestInstance()
	{
		return SQLiteHouse.Factory.init().getInstance(
				ValidSpecClass.class,
				InstrumentationRegistry.getTargetContext()
			);
	}

	protected ValidSpecClass( Factory factory )
	{ super(factory) ; }

	public SQLiteDatabase getDB()
	{ return m_db ; }
}
