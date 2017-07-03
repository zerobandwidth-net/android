package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.zerobandwidth.android.lib.database.SQLitePortal;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.RefractorMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses custom annotations to automatically construct and manage SQLite
 * databases with tables in which each row holds a serialization of a specified
 * Java class.
 *
 * <p>This class is based on {@link SQLitePortal} and provides all of the same
 * methods for accessing the database.</p>
 *
 * <h2>Usage</h2>
 *
 * <h3>Defining a Database</h3>
 *
 * <p>Define your SQLite database by creating an empty descendant of
 * {@code SQLiteHouse}, decorated with an {@link SQLiteDatabaseSpec} annotation
 * which describes the basic parameters of the database itself. In the example
 * below, we create a database to hold information about people, places, and
 * things.</p>
 *
 * <pre>
 *    {@literal @}SQLiteDatabaseSpec(
 *         database_name = "my_database",
 *         schema_version = 1,
 *         classes = { Person.class, Place.class, Thing.class }
 *     )
 *     public class MyDatabaseClass
 *     extends SQLiteHouse&lt;MyDatabaseClass&gt;
 *     {}
 * </pre>
 *
 * <p>Note that the descendant class extends {@code SQLiteHouse} with a generic
 * parameter pointing back to itself. This template parameter is used in
 * {@code SQLiteHouse}'s method definitions to ensure that all methods that are
 * "fluid" (<i>i.e.</i>, which return the same object) will return instances of
 * that descendant class, rather than being typecast up the hierarchy to
 * {@code SQLiteHouse} itself. This pattern allows for more effective method
 * chaining in case the descendant has custom methods that are also fluid.</p>
 *
 * <h3>Defining the Database Schema</h3>
 *
 * <p>In the example above, the {@code classes} element of the annotation names
 * three other classes. These are the data objects that you would use in your
 * app to contain the data elements that are stored in the database. By
 * decorating these classes with annotations, the {@code SQLiteHouse} can
 * recognize those classes as data schema definitions, and use the annotations
 * to construct and manage the database automatically.</p>
 *
 * <p>To continue the previous example, the {@code Person} class is shown below;
 * {@code Place} and {@code Thing} would be similarly defined.</p>
 *
 * <pre>
 *    {@literal @}SQLiteTable( "people" )
 *     public class Person implements SQLightable
 *     {
 *        {@literal @}SQLiteColumn( name = "person_id", index = 0 )
 *        {@literal @}SQLitePrimaryKey
 *         protected String m_sID ;
 *
 *        {@literal @}SQLiteColumn( name = "first_name", index = 1 )
 *         protected String m_sFirstName ;
 *
 *        {@literal @}SQLiteColumn( name = "last_name", index = 2 )
 *         protected String m_sLastName ;
 *
 *        {@literal @}SQLiteColumn( name = "birthday", index = 3 )
 *         protected Date m_dBirthdate ;
 *
 *        {@literal @}SQLiteColumn( name = "address", index = 4 )
 *         protected String m_sAddress ;
 *
 *         // usual constructors, methods, etc. follow
 *     }
 * </pre>
 *
 * <p>Note that the instance members that hold the data need not have the same
 * name as their database columns, nor are they forced to be {@code public}.
 * This system of annotations is designed such that it interferes as little as
 * possible with the other design decisions that might go into the data classes.
 * As long as the fields that correspond to database table columns are properly
 * decorated, they will be discovered and used in the database. Note also that
 * this allows the data object to have any other member fields it wants, which
 * are <i>not</i> serialized into the database, merely by leaving those members
 * undecorated.</p>
 *
 * <p>The {@code @SQLitePrimaryKey} annotation explicitly designates a data
 * element which could be used as a primary key for the table. However, the
 * {@code SQLiteHouse} will not actually define the column as such; it will
 * merely be {@code UNIQUE NOT NULL} in the table creation SQL, and a standard,
 * magic {@code _id} column will be used as the actual primary key. This is done
 * because of SQLite's inherent preference for auto-incremented integer keys.
 * However, the {@code SQLiteHouse} will behave as if this function is the
 * actual primary key, allowing consumers to search tables by this field rather
 * than the magic numeric ID.</p>
 *
 * <p>For notes on the predictability of column order in the table definition,
 * see the {@link ColumnIndexComparator} inner class.</p>
 *
 * <h3>Constructing a Database Instance</h3>
 *
 * <p>Use the {@link SQLiteHouse.Factory} class to construct an instance of the
 * database class. The factory will perform all the necessary pre-processing of
 * the {@link SQLiteDatabaseSpec} annotation and feed those parameters into the
 * constructor for the database class instance. The factory is templatized such
 * that your database class does not need to extend it.</p>
 *
 * <p>The code below continues our example of the {@code MyDatabaseClass} by
 * constructing an instance.</p>
 *
 * <pre>
 *     // given some Context ctx in which the class will operate
 *     // given some SQLiteHouse.CursorFactory cf for the database helper
 *
 *     MyDatabaseClass dbh = SQLiteHouse.Factory.init().getInstance(
 *             MyDatabaseClass.class, ctx, cf ) ;
 * </pre>
 *
 * <h3>Custom Processors for Data Classes</h3>
 *
 * <p>{@code SQLiteHouse} uses implementations of the {@link Refractor}
 * interface to process various data types. The standard set of implementations,
 * generally named "lenses", are automatically constructed and mapped by the
 * {@link RefractorMap} class. To customize this mapping with your own
 * {@code Refractor} implementations, override the
 * {@link #registerCustomRefractors()} method, which is called by the
 * {@link #SQLiteHouse(Factory)} constructor.</p>
 *
 * <h3>Connecting to the Database</h3>
 *
 * <p>Since this class extends {@link SQLitePortal}, which in turn is descended
 * from {@link android.database.sqlite.SQLiteOpenHelper}, it provides the same
 * methods for managing connections to the database. Connections may be
 * established with {@link SQLitePortal#openDB()} and released with
 * {@link SQLitePortal#close()}.</p>
 *
 * <p>The {@link #onCreate} and {@link #onUpgrade} methods, which ensure that
 * the underlying database is always installed with the current schema, are
 * already implemented in {@code SQLiteHouse}, and use the schematic information
 * discovered by the constructor to handle the database creation and upgrade
 * operations automatically. Descendant classes need not provide their own
 * implementations of these methods, unless they require some exotic
 * post-processing logic after the normal creation/update process has been
 * completed.</p>
 *
 * @param <DSC> A descendant class. When creating a descendant class, it should
 *  extend {@code SQLiteHouse} templatized for itself. This will ensure that all
 *  methods inherited from {@code SQLiteHouse} return instances of the
 *  descendant class, rather than the parent class.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
@SuppressWarnings("StringBufferReplaceableByString")
public class SQLiteHouse<DSC extends SQLiteHouse>
extends SQLitePortal
{
/// Static Inner Classes ///////////////////////////////////////////////////////

	/**
	 * Creates instances of a {@link SQLiteHouse}. Because we have to have all
	 * our ducks in a row before the {@code SQLiteHouse} calls its ancestor's
	 * constructor, the factory needs to parse the {@link SQLiteDatabaseSpec} in
	 * advance, and feed that information into the constructor as parameters.
	 *
	 * <h3>Usage</h3>
	 *
	 * <p>Given a {@code Context ctx} and {@code SQLiteDatbase.CursorFactory cf}
	 * (which may be null):</p>
	 *
	 * <pre>
	 *     MyDatabaseClass dbh = SQLiteHouse.Factory.init().getInstance(
	 *             MyDatabaseClass.class, ctx, cf ) ;
	 * </pre>
	 *
	 * <p>See {@link SQLiteHouse} for detailed information regarding how this
	 * fits into the overall lifecycle of the object instance.</p>
	 *
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 */
	public static class Factory
	{
		/**
		 * Simply an alias for the default constructor, to allow for a cleaner
		 * grammar when creating a factory instance to obtain a reference to a
		 * database.
		 * @return an instance of the factory
		 */
		public static SQLiteHouse.Factory init()
		{ return new SQLiteHouse.Factory() ; }

		protected Context m_ctx = null ;

		protected String m_sDatabaseName = null ;

		protected SQLiteDatabase.CursorFactory m_cf = null ;

		protected int m_nSchemaVersion = SCHEMA_NOT_DEFINED ;

		protected ArrayList<Class<? extends SQLightable>> m_aclsSchema = null ;

		/**
		 * Uses annotations found in a {@link SQLiteHouse} descendant to
		 * construct an instance of the database class.
		 *
		 * Since this method is templatized on the class that is being
		 * instantiated, there is no need for the {@code SQLiteHouse} descendant
		 * to provide its own extension of {@code SQLiteHouse.Factory}; this
		 * method will return an instance of the descendant class.
		 * @param cls the {@code SQLiteHouse} descendant class being created
		 * @param ctx the context in which the object will operate
		 * @param cf a cursor factory as allowed by the
		 *  {@link android.database.sqlite.SQLiteOpenHelper} constructor (may be
		 *  null)
		 * @param <FDSC> the {@code SQLiteHouse} descendant being created; this
		 *  matches the class sent in the {@code cls} argument.
		 * @return an instance of the {@code SQLiteHouse} descendant,
		 *  initialized with the database attributes found in the class's
		 *  {@link SQLiteDatabaseSpec} annotation
		 * @throws IntrospectionException if something goes wrong while
		 *  processing the descendant class. When invoking the descendant's
		 *  constructor, there are several possible failure states; use
		 *  {@code .getCause()} to determine which one applies.
		 */
		@SuppressWarnings( "unchecked" ) // Constructor is invoked from class.
		public <FDSC extends SQLiteHouse> FDSC getInstance( Class<FDSC> cls,
				Context ctx, SQLiteDatabase.CursorFactory cf )
		throws IntrospectionException
		{
			this.m_ctx = ctx ;
			this.m_cf = cf ;

			try
			{
				SQLiteDatabaseSpec spec =
						cls.getAnnotation( SQLiteDatabaseSpec.class ) ;
				m_sDatabaseName = spec.database_name() ;
				m_nSchemaVersion = spec.schema_version() ;
				this.m_aclsSchema = new ArrayList<>() ;
				this.m_aclsSchema.addAll( Arrays.asList( spec.classes() ) ) ;
			}
			catch( NullPointerException x )
			{
				throw new IntrospectionException(
						"Could not initialize the database instance." ) ;
			}

			try
			{
				Constructor ctor = cls.getDeclaredConstructor(
						SQLiteHouse.Factory.class ) ;
				ctor.setAccessible( true ) ;
				return ((FDSC)( ctor.newInstance( this ) )) ;
			}
			catch( Exception x )
			{
				// Might catch any of:
				// IllegalAccessException, InstantiationException,
				// InvocationTargetException, NoSuchMethodException
				throw new IntrospectionException(
						"Could not find appropriate constructor in descendant.",
						x ) ;
			}
		}
	}

	/**
	 * Used by {@link SQLiteHouse} to sort the indices and/or names of columns
	 * within a table specification.
	 * @since zerobandwidth-net/android 0.1.4 (#26)
	 * @see SQLiteHouse#processFieldsOfClasses()
	 */
	public static class ColumnIndexComparator
	implements Comparator<Field>
	{
		/**
		 * The algorithm in this method prefers to sort a column with an
		 * explicit index definition before any column with no index definition.
		 * For any pair of columns that have the same defined index, or where
		 * neither column has a defined index, the algorithm will sort columns
		 * alphabetically by name instead. The only way to have this method
		 * return {@code 0} (equal) would be to have two columns with the same
		 * name, which is a violation of SQL table requirements anyway.
		 * @param fldFirst the first column to be compared
		 * @param fldSecond the second column to be compared
		 * @return {@code -1} if the first column should be before the second;
		 *  {@code 1} if the first column should be after the second; {@code 0}
		 *  if no sort criteria can be resolved.
		 */
		@Override
		public int compare( Field fldFirst, Field fldSecond )
		{
			SQLiteColumn antFirst =
					fldFirst.getAnnotation( SQLiteColumn.class ) ;
			SQLiteColumn antSecond =
					fldSecond.getAnnotation( SQLiteColumn.class ) ;

			// Try comparing the "index" attribute first.
			if( antFirst.index() == SQLiteColumn.NO_INDEX_DEFINED )
			{
				if( antSecond.index() != SQLiteColumn.NO_INDEX_DEFINED )
				{ // Always sort cols without indices after cols with indices.
					return 1 ;
				}
			}
			else if( antSecond.index() == SQLiteColumn.NO_INDEX_DEFINED )
			{ // Always sort cols without indices after cols with indices.
				return -1 ;
			}
			else if( antFirst.index() < antSecond.index() )
				return -1 ;
			else if( antFirst.index() > antSecond.index() )
				return 1 ;

			// If "index" is equal, the sort alphabetically.
			String sFirst = antFirst.name() ;
			String sSecond = antSecond.name() ;

			int nCharIndex = 0 ;
			while( nCharIndex < sFirst.length() && nCharIndex < sSecond.length() )
			{
				if( sFirst.charAt(nCharIndex) < sSecond.charAt(nCharIndex) )
					return -1 ;
				if( sFirst.charAt(nCharIndex) > sSecond.charAt(nCharIndex) )
					return 1 ;
				++nCharIndex ;
			}

			if( sFirst.length() < sSecond.length() )
				return -1 ;
			if( sFirst.length() > sSecond.length() )
				return 1 ;

			return 0 ;
		}
	}

/// Static Methods /////////////////////////////////////////////////////////////

	/**
	 * Standardized way to choose the name of a SQLite table based on the class
	 * definition and its annotations, if any.
	 *
	 * <p>Consumed by {@link #getTableCreationSQL} and
	 * {@link #getAddColumnSQL}.</p>
	 *
	 * @param clsTable the class which defines the SQLite table
	 * @param antTableArg the annotation which relates the class to the schema,
	 *  if any; if {@code null} is passed, this method will still try to
	 *  discover one for itself
	 * @param <T> ensures that the table class implements {@link SQLightable}
	 * @return either the name specified in the annotation, or a lower-cased
	 *  transformation of the class name itself if the annotation is not
	 *  provided
	 */
	protected static <T extends SQLightable> String getTableName( Class<T> clsTable, SQLiteTable antTableArg )
	{
		SQLiteTable antTable = ( antTableArg == null ?
			clsTable.getAnnotation( SQLiteTable.class ) : antTableArg ) ;
		return ( antTable == null ?
			clsTable.getSimpleName().toLowerCase() : antTable.value() ) ;
	}

/// Static Constants ///////////////////////////////////////////////////////////

	public static final String LOG_TAG = SQLiteHouse.class.getSimpleName() ;

	/**
	 * Magic constant to indicate that the schema version has not yet been
	 * resolved.
	 * @see Factory#m_nSchemaVersion
	 */
	protected static final int SCHEMA_NOT_DEFINED = -1 ;

	/**
	 * This magic column name is used in every table to auto-create a row ID as
	 * preferred by SQLite. Table classes should not define a member with this
	 * name.
	 */
	public static final String MAGIC_ID_COLUMN_NAME = "_id" ;

/// Instance Members ///////////////////////////////////////////////////////////

	/**
	 * A list of classes that, in aggregate, define the schema for the database.
	 */
	protected List<Class<? extends SQLightable>> m_aclsSchema = null ;

	/**
	 * A map of schema classes to lists of their fields.
	 */
	protected Map<Class<? extends SQLightable>,List<Field>> m_mapFields = null ;

	/**
	 * A map of schema classes to the columns that are annotated as primary
	 * keys, using the {@link SQLitePrimaryKey} annotation.
	 */
	protected Map<Class<? extends SQLightable>,Field> m_mapKeys = null ;

	/**
	 * A persistent instance of a refractor map. Descendant classes may be
	 * registered for certain data classes if desired.
	 */
	protected RefractorMap m_mapRefractor = null ;

/// Constructors and Initializers //////////////////////////////////////////////

	/**
	 * Constructor used by the {@link SQLiteHouse.Factory} to create an instance
	 * of the class. The factory passes itself into this constructor, so that it
	 * can provide values for all of the parameters necessary to invoke the
	 * superclass's constructor.
	 *
	 * <p>Descendant classes <b>must</b> extend this constructor in order to use
	 * the {@link SQLiteHouse.Factory} to properly process the schematic data in
	 * the various data classes.</p>
	 *
	 * <pre>
	 *     protected MyDatabaseClass( SQLiteHouse.Factory factory )
	 *     { super(factory) ; }
	 * </pre>
	 *
	 * @param factory the factory which has resolved information about the
	 *  database to be bound to this class
	 */
	protected SQLiteHouse( SQLiteHouse.Factory factory )
	{
		super( factory.m_ctx, factory.m_sDatabaseName,
				factory.m_cf, factory.m_nSchemaVersion ) ;
		this.setSchemaClasses( factory.m_aclsSchema )
			.processFieldsOfClasses()
			;
		m_mapRefractor = (new RefractorMap()).init() ;
		this.registerCustomRefractors() ;
	}

	/**
	 * Caches a list of classes that define the database schema.
	 *
	 * Consumed by {@link #SQLiteHouse(Factory)}; must precede
	 * {@link #processFieldsOfClasses()}.
	 *
	 * @param aclsSchema the list of classes
	 * @return (fluid)
	 */
	protected DSC setSchemaClasses( List<Class<? extends SQLightable>> aclsSchema )
	{
		if( this.m_aclsSchema == null )
			this.m_aclsSchema = new ArrayList<>() ;
		else
			this.m_aclsSchema.clear() ;
		this.m_aclsSchema.addAll( aclsSchema ) ;

		//noinspection unchecked
		return (DSC)this ;
	}

	/**
	 * Given that the list of schema classes has been populated, discover and
	 * cache their characteristics for future reference.
	 *
	 * Consumed by {@link #SQLiteHouse(Factory)}; must follow
	 * {@link #setSchemaClasses(List)}.
	 *
	 * @return (fluid)
	 */
	protected DSC processFieldsOfClasses()
	{
		if( m_mapFields == null )
			m_mapFields = new HashMap<>() ;
		if( m_mapKeys == null )
			m_mapKeys = new HashMap<>() ;
		for( Class<? extends SQLightable> cls : m_aclsSchema )
		{
			List<Field> afldAll = Arrays.asList( cls.getDeclaredFields() ) ;
			List<Field> afldAnnotated = new ArrayList<>() ;
			for( Field fld : afldAll )
			{ // Find only the fields that are annotated as columns.
				if( fld.isAnnotationPresent( SQLiteColumn.class ) )
					afldAnnotated.add(fld) ;
				if( fld.isAnnotationPresent( SQLitePrimaryKey.class ) )
					m_mapKeys.put( cls, fld ) ;
			}
			if( afldAnnotated.size() > 1 )
				Collections.sort( afldAnnotated, new ColumnIndexComparator() ) ;
			m_mapFields.put( cls, afldAnnotated ) ;
		}

		//noinspection unchecked
		return (DSC)this ;
	}

	/**
	 * Consumed by the constructor, this method registers any custom
	 * {@link Refractor} implementations that should be used by the instance.
	 * The default implementation of this method returns trivially; descendants
	 * of {@code SQLiteHouse} may override this method to add any custom
	 * {@code Refractor} implementations here.
	 * @return (fluid)
	 * @see Refractor
	 * @see RefractorMap
	 */
	protected DSC registerCustomRefractors()
	{
		//noinspection unchecked
		return (DSC)this ;
	}

/// net.zerobandwidth.android.lib.database.SQLitePortal ////////////////////////

	/**
	 * Called by Android when the consumer tries to connect to the database.
	 * This method will iterate over the list of table classes and execute the
	 * SQL statement which will create that table.
	 *
	 * <p>This method was designed to be a {@code final} implementation, but is
	 * left extensible for descendant classes, just in case they might need to
	 * perform any custom post-processing.</p>
	 *
	 * <p>Consumes {@link #getTableCreationSQL}.</p>
	 *
	 * @param db a direct handle to the SQLite database (provided by the Android
	 *  OS)
	 */
	@Override
	public void onCreate( SQLiteDatabase db )
	{
		Log.i( LOG_TAG, "Executing onCreate()" ) ;
		for( Class<? extends SQLightable> clsTable : m_aclsSchema )
			db.execSQL( this.getTableCreationSQL( clsTable, null ) ) ;
	}

	/**
	 * Called by Android when the consumer tries to connect to the database, and
	 * the current schema version in the class is newer than the one that is
	 * currently installed. This method iterates over the list of schema classes
	 * and, if the table's {@code since} version is newer than the old version,
	 * will create the table. Otherwise, it will analyze the table's columns,
	 * and if any column's {@code since} version is newer than the old version,
	 * the method will add the column to the table.
	 *
	 * <p>This method was designed to be a {@code final} implementation, but is
	 * left extensible for descendant classes, just in case they might need to
	 * perform any custom post-processing.</p>
	 *
	 * <p>Consumes {@link #getTableCreationSQL} and
	 * {@link #getAddColumnSQL}.</p>
	 *
	 * @param db a direct handle to the SQLite database (provided by the Android
	 *  OS)
	 * @param nOld the version of the schema that is installed
	 * @param nNew the version of the schema that is defined
	 */
	@Override
	public void onUpgrade( SQLiteDatabase db, int nOld, int nNew )
	{
		Log.i( LOG_TAG, (new StringBuilder())
				.append( "Executing onUpgrade() from old version [" )
				.append( nOld ).append( "] to new version [" )
				.append( nNew ).append( "]..." )
				.toString()
			);
		for( Class<? extends SQLightable> clsTable : m_aclsSchema )
		{ // Determine what's new in each table.
			SQLiteTable antTable = clsTable.getAnnotation( SQLiteTable.class ) ;
			String sTableName = getTableName( clsTable, antTable ) ;
			int nTableSince = ( antTable == null ? 1 : antTable.since() ) ;
			if( nTableSince > nOld )
			{ // Whole table is new; create it and move on.
				db.execSQL( this.getTableCreationSQL( clsTable, antTable ) ) ;
				Log.d( LOG_TAG, (new StringBuilder())
						.append( "Created table [" )
						.append( sTableName ).append( "]." )
						.toString()
					);
				continue ;
			}
			for( Field fld : m_mapFields.get(clsTable) )
			{ // Determine which columns are new.
				SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
				// If antCol were null, we wouldn't be processing it, so...
				int nColSince = antCol.since() ;
				if( nColSince > nOld )
				{
					db.execSQL( this.getAddColumnSQL(
							clsTable, antTable, fld, antCol ) ) ;
					Log.d( LOG_TAG, (new StringBuilder())
							.append( "Added column [" ).append( antCol.name() )
							.append( "] to table [" )
							.append( getTableName( clsTable, antTable ) )
							.toString()
						);
				}
			}
		}
	}

/// Schema Processor Methods ///////////////////////////////////////////////////

	/**
	 * Generates the SQL statement which will create one of the tables, based on
	 * the table class itself, and its {@link SQLiteTable} annotation (if any).
	 *
	 * <p>Consumed by {@link #onCreate} and {@link #onUpgrade}. Consumes
	 * {@link #getTableName} and {@link #getColumnDefinitionClause}.</p>
	 *
	 * @param clsTable the class which will be rendered as a SQLite table.
	 * @param antTable the class's schema annotation; if {@code null} is given,
	 *  then methods that depend on it will search for it again or choose
	 *  various default values instead
	 * @param <T> ensures that the table class implements {@link SQLightable}
	 * @return an SQL statement which will create the SQLite table based on the
	 *  information discovered within the class definition.
	 */
	protected <T extends SQLightable> String getTableCreationSQL(
			Class<T> clsTable, SQLiteTable antTable )
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( "CREATE TABLE IF NOT EXISTS " )
		  .append( getTableName( clsTable, antTable ) )
		  .append( " ( " ).append( MAGIC_ID_COLUMN_NAME )
		  .append( " " ).append( Refractor.SQLITE_TYPE_INT )
		  .append( " PRIMARY KEY AUTOINCREMENT" )
		  ;

		for( Field fld : m_mapFields.get(clsTable) )
		{
			sb.append( ", " )
			  .append( this.getColumnDefinitionClause( fld, null ) )
			  ;
		}

		sb.append( " )" ) ;

		Log.d( LOG_TAG, sb.toString() ) ;

		return sb.toString() ;
	}

	/**
	 * Generates the SQL statement which will add a column to a table, based on
	 * the table class itself, its annotation, a field within that table, and
	 * its column annotation.
	 *
	 * <p>Consumed by {@link #onUpgrade}. Consumes {@link #getTableName} and
	 * {@link #getColumnDefinitionClause}.</p>
	 *
	 * @param clsTable the class which is rendered as an SQLite table.
	 * @param antTable the table class's schema annotation; if {@code null} is
	 *  given, then methods that depend on it will search for it again or choose
	 *  various default values instead
	 * @param fld the field to be added as a column
	 * @param antCol the column's annotation
	 * @param <T> ensures that the table class implements {@link SQLightable}
	 * @return an SQL statement which adds a column to an existing table
	 */
	protected <T extends SQLightable> String getAddColumnSQL(
			Class<T> clsTable, SQLiteTable antTable,
			Field fld, SQLiteColumn antCol )
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( "ALTER TABLE " ).append( getTableName( clsTable, antTable ) )
		  .append( " ADD COLUMN " )
		  .append( this.getColumnDefinitionClause( fld, antCol ) )
		  ;

		return sb.toString() ;
	}

	/**
	 * Generates an SQLite column definition clause for the specified field in
	 * an {@link SQLightable} class. This can be used as part of a
	 * {@code CREATE TABLE} or {@code ALTER TABLE} statement.
	 *
	 * <p>Consumed by {@link #getTableCreationSQL} and
	 * {@link #getAddColumnSQL}.</p>
	 *
	 * @param fld the class field to be added
	 * @param antColArg the field's column-definition annotation, if already
	 *  fetched; the caller may pass {@code null} here to have this method fetch
	 *  the annotation for itself instead of pre-fetching it
	 * @return a SQLite column definition clause for the specified field
	 */
	protected String getColumnDefinitionClause(
			Field fld, SQLiteColumn antColArg )
	{
		SQLiteColumn antCol = ( antColArg == null ?
			fld.getAnnotation( SQLiteColumn.class ) : antColArg ) ;

		StringBuilder sb = new StringBuilder() ;

		boolean bIsKey =
				( fld.getAnnotation( SQLitePrimaryKey.class ) != null ) ;

		Refractor<?> lens ;
		try { lens = m_mapRefractor.get( fld.getType() ).newInstance() ; }
		catch( Exception x )
		{
			Log.e( LOG_TAG, "Could not instantiate a refractor.", x ) ;
			return null ;
		}

		sb.append( antCol.name() ).append( " " )
		  .append( lens.getSQLiteDataType() )
		  ;

		if( bIsKey ) // Override the annotation's nullability declaration.
			sb.append( " UNIQUE NOT NULL" ) ; // but we'll use it as a key
		else
			sb.append(( antCol.is_nullable() ? " NULL" : " NOT NULL" )) ;

		String sDefaultValue = antCol.sql_default() ;

		if( SQLitePortal.SQLITE_NULL.equals(sDefaultValue) )
		{ // Write "DEFAULT NULL" only if the column is actually nullable.
			if( ! bIsKey && antCol.is_nullable() )
				sb.append( " DEFAULT NULL" ) ;
		}
		else
		{ // Write whatever the default is.
			sb.append( " DEFAULT " ) ;
			if( Refractor.SQLITE_TYPE_TEXT.equals( lens.getSQLiteDataType() ) )
				sb.append( "'" ).append( sDefaultValue ).append( "'" ) ;
			else
				sb.append( sDefaultValue ) ;
		}

		return sb.toString() ;
	}

/// Other Instance Methods /////////////////////////////////////////////////////

}
