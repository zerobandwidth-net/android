package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import android.support.test.InstrumentationRegistry;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouseTest;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;

/**
 * Used as the canonical "valid" {@link SQLiteHouse} implementation for various
 * unit tests. This represents a database "upgrade" from {@link ValidSpecClass},
 * and defines a database marshalled by {@link Flargle} (upgrades
 * {@link Fargle}), {@link Dargle}, {@link Blargh}, and {@link Quargle} (new in
 * this schema version).
 * @since zerobandwidth-net/android 0.1.4 (#26), extracted from
 *  {@link SQLiteHouseTest} in 0.1.7 (#50)
 */
@SQLiteDatabaseSpec(
		database_name = "valid_spec_class_db",
		schema_version = 2,
		classes =
				{ Flargle.class, Dargle.class, Quargle.class, Blargh.class }
)
public class UpgradeSpecClass
		extends SQLiteHouse<UpgradeSpecClass>
{
	/**
	 * Constructs an instance of the test DB class, bound to the test
	 * environment's instrumentation context.
	 * @return an instance of the test DB class
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static UpgradeSpecClass getTestInstance()
	{
		return SQLiteHouse.Factory.init().getInstance(
				UpgradeSpecClass.class,
				InstrumentationRegistry.getTargetContext()
			);
	}

	protected UpgradeSpecClass( Factory factory )
	{ super( factory ); }
}
