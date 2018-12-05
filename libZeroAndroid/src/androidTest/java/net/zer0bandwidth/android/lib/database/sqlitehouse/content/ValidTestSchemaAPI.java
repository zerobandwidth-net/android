package net.zer0bandwidth.android.lib.database.sqlitehouse.content;

/**
 * Used by unit test classes for the SQLiteHouse keeper/relay feature.
 * @since zer0bandwidth-net/android 0.1.7 (#50)
 */
public class ValidTestSchemaAPI
extends SQLiteHouseSignalAPI
{
	public static final String INTENT_DOMAIN = "org.totallyfake.unittest" ;

	@Override
	protected String getIntentDomain()
	{ return INTENT_DOMAIN ; }
}
