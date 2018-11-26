package net.zer0bandwidth.android.lib.database.sqlitehouse.testschema;

import net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.CustomStringLens;

/**
 * Data table class for the database which tests custom refractors.
 * @since zer0bandwidth-net/android 0.1.5 (#41)
 * @see net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.CustomLensTest
 * @see net.zer0bandwidth.android.lib.database.sqlitehouse.SQLightableReflectionColumnTest
 */
@SQLiteTable( "sparkles" )
public class Sparkle
implements SQLightable
{
	@SQLiteColumn( name = "sparkle", refractor = CustomStringLens.class )
	public String m_sValue = null ;

	@SuppressWarnings("unused") // used reflexively
	public Sparkle() {}

	public Sparkle( String s )
	{ m_sValue = s ; }
}
