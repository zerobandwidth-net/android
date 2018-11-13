package net.zerobandwidth.android.lib.database.sqlitehouse.testschema;

import android.support.test.InstrumentationRegistry;

import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteInheritColumns;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;

/**
 * Used as the canonical "valid" {@link SQLiteHouse} implementation with which
 * to test table schema reflection in which the schematic classes are part of a
 * hierarchy from which column-definition fields are inherited from ancestors.
 * This schematic specification represents an upgrade from that of
 * {@link ValidSpecClass}.
 * @since zerobandwidth-net/android [NEXT] (#56)
 */
@SQLiteDatabaseSpec(
		database_name = "inheritance_spec_class_db",
		schema_version = 2,
		classes =
			{
				InheritanceDBSpec.FargleX.class,
				InheritanceDBSpec.DargleX.class,
				Quargle.class,
				Blargh.class
			}
)
public class InheritanceDBSpec
extends SQLiteHouse<InheritanceDBSpec>
{
	/**
	 * This class is intended to act as a schematic <i>update</i> to
	 * {@link Fargle}, triggering the addition of an {@code extension_data}
	 * column to the existing {@code fargles} table.
	 *
	 * Normally you wouldn't define these as inner classes, but as the scope of
	 * the class's reasons for existence is limited, we can get away with it
	 * just this once.
	 *
	 * @since zerobandwidth-net/android [NEXT] (#56)
	 */
	@SQLiteTable( "fargles" )
	@SQLiteInheritColumns
	public static class FargleX
	extends Fargle
	implements SQLightable
	{
		/** Represents an additional column beyond those in {@link Fargle}. */
		@SQLiteColumn( name="extension_data", index=3 )
		protected String m_sExtension = null ;

		@SuppressWarnings( "unused" ) // required for reflection
		public FargleX() {}

		public FargleX( int nID, String sOrig, int z, String sExt )
		{
			super( nID, sOrig, z ) ;
			m_sExtension = sExt ;
		}

		public String getExtension()
		{ return m_sExtension ; }
	}

	/**
	 * This class is intended to add a new {@code extended_dargles} table that
	 * <i>does not</i> trigger changes in the table that was created by
	 * {@link Dargle}. It is expected to have columns {@code _id},
	 * {@code dargle_string}, {@code is_dargly}, and {@code extension_data}.
	 *
	 * Normally you wouldn't define these as inner classes, but as the scope of
	 * the class's reasons for existence is limited, we can get away with it
	 * just this once.
	 *
	 * @since zerobandwidth-net/android [NEXT] (#56)
	 */
	@SQLiteTable( value = "extended_dargles", since=2 )
	@SQLiteInheritColumns
	public static class DargleX
	extends Dargle
	implements SQLightable
	{
		/** Represents an additional column beyond those in {@link Dargle}. */
		@SQLiteColumn( name="extension_data", index=4 )
		protected String m_sExtension = null ;

		/** Should not be discovered as a column. */
		protected int m_zIgnoreThisToo = -2 ;

		@SuppressWarnings( "unused" ) // required for reflection
		public DargleX() {}

		public DargleX( String sOrig, boolean b, int z1, String sExt, int z2 )
		{
			super( sOrig, b, z1 ) ;
			m_sExtension = sExt ;
			m_zIgnoreThisToo = z2 ;
		}

		public String getExtension()
		{ return m_sExtension ; }

		public int getIgnored2()
		{ return m_zIgnoreThisToo ; }
	}

	protected InheritanceDBSpec( Factory factory )
	{ super(factory) ; }
}
