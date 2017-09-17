package net.zerobandwidth.android.lib.database.sqlitehouse.content;

import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLightable;
import net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions.SQLiteContentException;

import java.util.List;

/**
 * A canonical implementation of a {@link ContentProvider} which is bound to a
 * {@link SQLiteHouse} implementation.
 * @param <H> the {@link SQLiteHouse} implementation to which this provider is
 *           bound
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public class SQLiteHouseKeeper<H extends SQLiteHouse>
extends ContentProvider
{
/// Static constants ///////////////////////////////////////////////////////////

	/**
	 * A suffix appended to the canonical name of the {@link SQLiteHouse}
	 * implementation class, to form the fully-qualified "authority" string for
	 * the provider.
	 */
	public static final String PROVIDER_AUTHORITY_SUFFIX = ".provider" ;

/// Inner instance classes /////////////////////////////////////////////////////

	/**
	 * A canonical implementation of {@link UriMatcher} which uses the
	 * information encoded into an {@link SQLiteHouse} instance to resolve URIs
	 * that are sent to the enclosing {@link SQLiteHouseKeeper}.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public class Switchboard
	extends UriMatcher
	{
		/** A persistent reference to the enclosing provider instance. */
		protected SQLiteHouseKeeper<H> m_pvd = SQLiteHouseKeeper.this ;

		/**
		 * Default constructor; initializes with {@link UriMatcher#NO_MATCH}.
		 */
		public Switchboard()
		{
			super( UriMatcher.NO_MATCH ) ;
		}

		/**
		 * Called by the constructor to automatically construct a mapping of URI
		 * patterns into integer values, based on the table names recorded in
		 * the {@link SQLiteHouse} implementation to which the keeper is bound.
		 *
		 * <p>Rather than forcing the consumer to construct a contract class
		 * full of constants, the initializer iterates over the ordered list of
		 * classes defined in the schema, assigning the bare table name as the
		 * URI pattern, and one plus the list item's index as the numeric key.
		 * It then also defines a pattern match for a URI referring to a row ID
		 * within that table, by adding the standard {@code "/#"} grammar to the
		 * end of the URI pattern, and then subtracting one plus the list item's
		 * index from {@link Integer#MAX_VALUE} to obtain the match key.</p>
		 *
		 * <p>For example, for a schema which includes tables {@code foo},
		 * {@code bar}, and {@code baz}, the initializer defines the following
		 * match table:</p>
		 *
		 * <table>
		 *     <thead>
		 *         <tr> <th>URI Pattern</th> <th>Match Key</th> </tr>
		 *     </thead>
		 *     <tbody>
		 *         <tr> <td><code>foo</code></td> <td>1</td> </tr>
		 *         <tr> <td><code>foo/#</code></td> <td>2147483646</td> </tr>
		 *         <tr> <td><code>bar</code></td> <td>2</td> </tr>
		 *         <tr> <td><code>bar/#</code></td> <td>2147483645</td> </tr>
		 *         <tr> <td><code>baz</code></td> <td>3</td> </tr>
		 *         <tr> <td><code>baz/#</code></td> <td>2147483644</td> </tr>
		 *     </tbody>
		 * </table>
		 *
		 * @return (fluid)
		 */
		protected Switchboard initURIPatterns()
		{
			//noinspection unchecked
			List<Class<? extends SQLightable>> aclsSchema =
					m_pvd.m_dbh.getSchemaClasses() ;
			for( int i = 0 ; i < aclsSchema.size() ; i ++ )
			{ // Add a URI pattern for each table defined by a schematic class.
				Class<? extends SQLightable> cls = aclsSchema.get(i) ;
				String sTableName =
						m_pvd.m_dbh.describe(cls).getTableName() ;
				int nKey = i + 1 ;
				this.addURI( m_pvd.getAuthority(), sTableName, nKey ) ;
				this.addURI( m_pvd.getAuthority(),
						this.getTableAndIDPattern( sTableName ),
						Integer.MAX_VALUE - nKey ) ;
			}

			return this ;
		}

		protected String getTableAndIDPattern( String sTableName )
		{
			return (new StringBuilder())
				.append( sTableName ).append( "/#" ).toString() ;
		}

		public boolean verify( Uri uri )
		throws SQLiteContentException
		{
			String sAuthority = uri.getAuthority() ;
			if( ! m_pvd.getAuthority().equals( sAuthority ) )
				throw SQLiteContentException.wrongAuthority( sAuthority ) ;
			return true ;
		}
	}

/// Member fields //////////////////////////////////////////////////////////////

	/** A persistent reference to the database helper instance. */
	protected H m_dbh = null ;

	/** A class which resolves URIs into the provider. */
	protected Switchboard m_urim = null ;

	/**
	 * The provider's authority string, which is initialized programmatically by
	 * default, but which may be overridden in a descendant class.
	 */
	protected String m_sAuthority = null ;

/// Constructors and initializers //////////////////////////////////////////////

	/**
	 * Bind to a specific, existing instance of a {@link SQLiteHouse}
	 * implementation.
	 * @param dbh the instance to which this provider must bind
	 */
	public SQLiteHouseKeeper( H dbh )
	{
		m_dbh = dbh ;
		this.initSwitchboard().initAuthority() ;
	}

	/**
	 * Create, and bind to, an instance of a {@link SQLiteHouse} implementation.
	 * @param cls the implementation class for the database helper
	 * @param ctx the context in which to create the database instance
	 */
	public SQLiteHouseKeeper( Class<H> cls, Context ctx )
	{ this.bind( cls, ctx, null ).initSwitchboard().initAuthority() ; }

	/**
	 * Create, and bind to, an instance of a {@link SQLiteHouse} implementation.
	 * @param cls the implementation class for the database helper
	 * @param ctx the context in which to create the database instance
	 * @param cf the cursor factory to be used for the database instance
	 */
	public SQLiteHouseKeeper( Class<H> cls, Context ctx, SQLiteDatabase.CursorFactory cf )
	{ this.bind( cls, ctx, cf ).initSwitchboard().initAuthority() ; }

	/**
	 * Creates, and binds to, the {@link SQLiteHouse} implementation class
	 * instance.
	 * @param cls the implementation class for the database helper
	 * @param ctx the context in which to create the database instance
	 * @param cf the cursor factory to be used for the database instance
	 * @return (fluid)
	 * @see SQLiteHouseKeeper#SQLiteHouseKeeper(Class, Context)
	 * @see SQLiteHouseKeeper#SQLiteHouseKeeper(Class, Context, SQLiteDatabase.CursorFactory)
	 */
	protected SQLiteHouseKeeper<H> bind( Class<H> cls, Context ctx, SQLiteDatabase.CursorFactory cf )
	{
		m_dbh = SQLiteHouse.Factory.init().getInstance( cls, ctx, cf ) ;
		return this ;
	}

	/**
	 * Initializes the instance's URI matcher, which is an instance of the inner
	 * {@link Switchboard} class. Implementations of {@link SQLiteHouseKeeper}
	 * may override this method to define custom URI mappings, which will take
	 * precedence over the default reflection-based URI matching performed by
	 * {@code Switchboard} when applicable.
	 * @return (fluid)
	 */
	protected SQLiteHouseKeeper<H> initSwitchboard()
	{
		m_urim = new Switchboard() ;
		return this ;
	}

	/**
	 * Initializes the authority string for which this provider will be
	 * registered, and which will be used to resolve URIs. The string is formed
	 * by reading the full canonical name of the underlying {@link SQLiteHouse}
	 * implementation class, and appending the value of the
	 * {@link #PROVIDER_AUTHORITY_SUFFIX} constant ({@code ".provider"}).
	 * Descendants of this class may choose to override this method, setting the
	 * value of the provider's {@link #m_sAuthority} member to a custom value.
	 * However, such a change must also be carried through to the corresponding
	 * {@link android.content.ContentResolver} implementation.
	 * @return the provider's authority string
	 */
	protected SQLiteHouseKeeper<H> initAuthority()
	{
		m_sAuthority = (new StringBuilder())
				.append( m_dbh.getClass().getCanonicalName() )
				.append( PROVIDER_AUTHORITY_SUFFIX )
				.toString()
				;
		return this ;
	}

/// android.content.ContentProvider ////////////////////////////////////////////

	@Override
	public int delete( Uri uri, String sWhereFormat, String[] asWhereArgs )
	{

	}

	protected int customDelete( Uri uri, String sWhereFormat, String[] asWhereArgs )
	{ return 0 ; }

/// Other accessors and mutators ///////////////////////////////////////////////

	/**
	 * Returns the provider's "authority" string, which contributes to the URI
	 * at which the provider is reached.
	 * @return the provider's authority string
	 */
	public String getAuthority()
	{ return m_sAuthority ; }


}
