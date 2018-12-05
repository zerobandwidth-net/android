package net.zer0bandwidth.android.lib.database.sqlitehouse;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import net.zer0bandwidth.android.lib.database.SQLitePortal;
import net.zer0bandwidth.android.lib.database.querybuilder.DeletionBuilder;
import net.zer0bandwidth.android.lib.database.querybuilder.QueryBuilder;
import net.zer0bandwidth.android.lib.database.querybuilder.SelectionBuilder;
import net.zer0bandwidth.android.lib.database.querybuilder.UpdateBuilder;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteDatabaseSpec;
import net.zer0bandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zer0bandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.NullRefractor;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.Refractor;
import net.zer0bandwidth.android.lib.database.sqlitehouse.refractor.RefractorMap;
import net.zer0bandwidth.android.lib.util.LexicalStringComparator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
 *         protected Calendar m_dBirthdate ;
 *
 *        {@literal @}SQLiteColumn( name = "address", index = 4 )
 *         protected String m_sAddress ;
 *
 *         /** Schema classes must provide a default constructor. {@literal *}/
 *         public Person() {}
 *
 *         // other constructors, methods, etc. follow
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
 * However, the {@code SQLiteHouse} will behave as if this object field is the
 * actual primary key, allowing consumers to search tables by this field rather
 * than the magic numeric ID.</p>
 *
 * <p>For notes on the predictability of column order in the table definition,
 * see the {@link SQLightable.Reflection.ColumnSequencer} class.</p>
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
 * {@link RefractorMap} class.</p>
 *
 * <p>To customize this mapping with your own {@code Refractor} implementations,
 * you may explicitly name a refractor class in the {@link SQLiteColumn}
 * annotation for any individual field in a schematic class. This is the most
 * efficient way to define a custom refractor, as it will be picked up
 * automatically by {@code SQLiteHouse} during the introspection process.</p>
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
 * <h3>Using the Query Commands</h3>
 *
 * <p>The base {@code SQLiteHouse} class provides implementations of some basic
 * query operations &mdash; insertion, selection, updates, and deletion. Once
 * all of the schematic data is known to the instance, the grammar of
 * interacting with the database flows relatively simply.</p>
 *
 * <pre>
 *     MyDatabaseClass dbh = SQLiteHouse.Factory.init().getInstance(
 *             MyDatabaseClass.class, ctx, null ) ;
 *     dbh.openDB() ;
 *     // wait for connection, either by sleeping or catching connection event
 *
 *     Person alice = new Person( UUID.randomUUID().toString(),
 *         "Alice", "Appleton", new GregorianCalendar( 1980, 6, 3 ),
 *         "1687 Newton Way, Principia, NY 10705" ) ;
 *     dbh.insert( alice ) ;
 *
 *     Person bob = new Person( UUID.randomUUID().toString(),
 *         "Bob", "Bullhead", new GregorianCalendar( 1960, 9, 5 ),
 *         "230 South Bouquet St., Oakland, PA 15213" ) ;
 *     long idBob = dbh.insert( bob ) ;
 *
 *     bob.setAddress( "115 Federal Street, Pittsburgh, PA 15212" ) ;
 *     int nUpdated = dbh.update( bob ) ;                       // nUpdated == 1
 *     Person also_bob = dbh.select( idBob ) ;
 *     String sBobAddress = also_bob.getAddress() ;     // 115 Federal Street...
 *
 *     int nDeleted = dbh.delete( alice ) ;                     // nDeleted == 1
 *     nDeleted = dbh.delete( alice ) ;                         // nDeleted == 0
 *     nDeleted = dbh.delete( bob ) ;                           // nDeleted == 1
 *     nDeleted = dbh.delete( also_bob ) ;                      // nDeleted == 0
 *
 *     dbh.close() ;
 * </pre>
 *
 * <p>The descendant class may, as with {@link SQLitePortal}, define further
 * custom instance methods to perform more specific reusable queries.</p>
 *
 * @param <DSC> A descendant class. When creating a descendant class, it should
 *  extend {@code SQLiteHouse} templatized for itself. This will ensure that all
 *  methods inherited from {@code SQLiteHouse} return instances of the
 *  descendant class, rather than the parent class.
 *
 * @since zer0bandwidth-net/android 0.1.4 (#26)
 */
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
	 * <p>Given a {@code Context ctx} and {@code SQLiteDatabase.CursorFactory cf}
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
	 * @since zer0bandwidth-net/android 0.1.4 (#26)
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

		/** The context in which the {@code SQLiteHouse} will operate. */
		protected Context m_ctx = null ;

		/**
		 * The name of the database to be created. The factory will glean this
		 * from the {@link SQLiteDatabaseSpec} annotation of the class that is
		 * passed into the {@link #getInstance} method.
		 *
		 * The {@link SQLiteHouse#SQLiteHouse(Factory)} constructor retrieves
		 * this value from the factory to pass it to the parent class's
		 * constructor, {@link SQLitePortal#SQLitePortal}.
		 */
		protected String m_sDatabaseName = null ;

		/**
		 * The cursor factory to be used with the {@link SQLiteHouse} instance,
		 * if any. This is passed as an argument to {@link #getInstance}.
		 *
		 * The {@link SQLiteHouse#SQLiteHouse(Factory)} constructor retrieves
		 * this value from the factory to pass it to the parent class's
		 * constructor, {@link SQLitePortal#SQLitePortal}.
		 */
		protected SQLiteDatabase.CursorFactory m_cf = null ;

		/**
		 * The current schema version of the database. The factory will glean
		 * this from the {@link SQLiteDatabaseSpec} annotation of the class that
		 * is passed into the {@link #getInstance} method.
		 *
		 * The {@link SQLiteHouse#SQLiteHouse(Factory)} constructor retrieves
		 * this value from the factory to pass it to the parent class's
		 * constructor, {@link SQLitePortal#SQLitePortal}.
		 */
		protected int m_nSchemaVersion = SCHEMA_NOT_DEFINED ;

		/**
		 * The array of classes which, in aggregate, define the schema for the
		 * database. The factory will glean this list from the
		 * {@link SQLiteDatabaseSpec} annotation of the class that is passed to
		 * the {@link #getInstance} method.
		 *
		 * The {@link SQLiteHouse#SQLiteHouse(Factory)} constructor uses this
		 * list to construct the schema; see
		 * {@link SQLiteHouse#setSchemaClasses(List)}.
		 */
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
		 * @param <FDSC> the {@code SQLiteHouse} descendant being created; this
		 *  matches the class sent in the {@code cls} argument.
		 * @return an instance of the {@code SQLiteHouse} descendant,
		 *  initialized with the database attributes found in the class's
		 *  {@link SQLiteDatabaseSpec} annotation
		 * @throws IntrospectionException if something goes wrong while
		 *  processing the descendant class. When invoking the descendant's
		 *  constructor, there are several possible failure states; use
		 *  {@code .getCause()} to determine which one applies.
		 * @since zer0bandwidth-net/android 0.1.7 (#50)
		 */
		public <FDSC extends SQLiteHouse> FDSC getInstance(
				Class<FDSC> cls, Context ctx )
		throws IntrospectionException
		{ return this.getInstance( cls, ctx, null ) ; }

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
	 * @since zer0bandwidth-net/android 0.1.4 (#26)
	 * @deprecated zer0bandwidth-net/android 0.2.1 (#56) &mdash; replaced by
	 *  {@link SQLightable.Reflection.ColumnSequencer}
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
			return (new LexicalStringComparator()).compare(
					antFirst.name(), antSecond.name() ) ;
		}
	}

	/**
	 * A short-lived, open data structure which provides context for various
	 * operations within the class. Because so many of the values fetched here
	 * must be reused multiple times within the body of certain larger
	 * functions, or must be passed <i>between</i> functions, it is useful to
	 * have all of these fields gathered in a single contextual container.
	 * Since 0.1.7 (#50), this class is not particularly different from
	 * {@link SQLightable.Reflection}.
	 * @since zer0bandwidth-net/android 0.1.4 (#26)
	 * @deprecated zer0bandwidth-net/android 0.2.1 (#56) &mdash; use
	 *  {@link SQLightable.Reflection} instead, by using
	 *  {@link #getReflection(Class)} to fetch the one that the database class
	 *  has already generated, or by using
	 *  {@link SQLightable.Reflection#reflect(Class)} to generate a new one.
	 */
	@SuppressWarnings("deprecation") // TODO (deprecation) remove in future major version
	public static class QueryContext<DBH extends SQLiteHouse>
	{
		/** A persistent reference back to a database portal. */
		public DBH house = null ;
		/** The schematic class providing this context. */
		public Class<? extends SQLightable> clsTable = null ;
		/** The schematic class's table-defining annotation. */
		public SQLiteTable antTable = null ;
		/** The name of the table. */
		public String sTableName = null ;
		/** The field of the schematic class in this context, if any. */
		public Field fldColumn = null ;
		/** The context field's column-defining annotation. */
		public SQLiteColumn antColumn = null ;
		/** The name of the column. */
		public String sColumnName = null ;
		/** Indicates whether the column is annotated as a key. */
		public boolean bColumnIsKey = false ;
		/** The refractor appropriate for this column type. */
		public Refractor lens = null ;
		/** The value of this column in some instance, if set. */
		public String sColumnSQLValue = null ;

		/**
		 * Constructs the instance and binds it back to a {@link SQLiteHouse}.
		 * @param dbh the helper instance
		 * @see SQLiteHouse#getQueryContext()
		 */
		public QueryContext( DBH dbh )
		{ this.house = dbh ; }

		/**
		 * Loads contextual information pertaining to the table defined by the
		 * specified schematic class. This operation clears all
		 * previously-loaded table data, and any data that might have been
		 * loaded for a column of that table.
		 * @param cls the schematic class
		 * @param <SC> the schematic class
		 * @return (fluid)
		 * @throws SchematicException if the class isn't part of the schema
		 */
		public <SC extends SQLightable> QueryContext<DBH> loadTableDef( Class<SC> cls )
		throws SchematicException
		{
			this.clsTable = cls ;
			//noinspection unchecked - guaranteed logically
			SQLightable.Reflection<SC> tbl = this.house.getReflection(cls) ;
			this.antTable = tbl.getTableAttrs() ;
			this.sTableName = tbl.getTableName() ;
			this.clearColumnDef() ;
			return this ;
		}

		/**
		 * Loads contextual information pertaining to a column of the table
		 * already set by {@link #loadTableDef}. This operation will clear the
		 * value of any previously-analyzed column.
		 * @param fld the field to be set for context
		 * @return (fluid)
		 */
		public QueryContext<DBH> loadColumnDef( Field fld )
		{
			if( fld == null )
				return this.clearColumnDef() ;

			this.fldColumn = fld ;
			SQLightable.Reflection.Column col = this.house
					.m_mapReflections.get(this.clsTable).getColumnDef(fld) ;
			this.antColumn = col.getColAttrs() ;
			this.sColumnName = this.antColumn.name() ;
			this.bColumnIsKey = col.isKey() ;
			this.lens = col.getRefractor() ;
			this.sColumnSQLValue = null ;

			return this ;
		}

		/**
		 * Loads contextual information pertaining to a column of the table set
		 * by {@link #loadTableDef}, by searching for that column by its name.
		 * This operation will clear the value of any previously-analyzed
		 * column.
		 * @param sSoughtName the name of the column to be set for context
		 * @return (fluid)
		 */
		public QueryContext<DBH> loadColumnDef( String sSoughtName )
		{
			if( this.clsTable == null )
				throw new IllegalStateException( "No table loaded." ) ;
			if( TextUtils.isEmpty(sSoughtName) )
				return this.clearColumnDef() ;
			SQLightable.Reflection tbl =
					this.house.getReflection( this.clsTable ) ;
			SQLightable.Reflection.Column col = tbl.getColumn( sSoughtName ) ;
			if( col != null ) return this.loadColumnDef( col.getField() ) ;
			Log.w( LOG_TAG, (new StringBuilder())
					.append( "No column found with name [" )
					.append( sSoughtName )
					.append( "] in table [" )
					.append( this.sTableName )
					.append( "] defined by class [" )
					.append( this.clsTable.getCanonicalName() )
					.append( "]; CLEARING loaded column data." )
					.toString()
				);
			return this.clearColumnDef() ;
		}

		/**
		 * Clears any and all contextual data pertaining to a table column.
		 * @return (fluid)
		 */
		protected QueryContext<DBH> clearColumnDef()
		{
			this.fldColumn = null ;
			this.antColumn = null ;
			this.sColumnName = null ;
			this.bColumnIsKey = false ;
			this.lens = null ;
			this.sColumnSQLValue = null ;
			return this ;
		}

		/**
		 * If the context is bound to a specific column, then this method will
		 * try to discover the value of the field corresponding to that column
		 * in the specified object instance.
		 * @param o the schematic object that contains the column field
		 * @param <SC> the schematic class
		 * @return (fluid)
		 * @throws IllegalStateException if inadequate context has been loaded
		 * @throws NullPointerException if the schematic object is null
		 * @throws SchematicException if something goes wrong while setting the
		 *  value
		 */
		public <SC extends SQLightable> QueryContext<DBH> loadColumnValue( SC o )
		throws IllegalStateException, NullPointerException, SchematicException
		{
			this.sColumnSQLValue = null ;

			if( o == null )
			{
				throw new NullPointerException(
						"Cannot examine null instance." ) ;
			}

			if( this.fldColumn == null )
			{
				throw new IllegalStateException(
					"Cannot discover value if no column has been chosen." ) ;
			}

			if( this.lens == null )
			{ throw SchematicException.noLensForField( this.fldColumn, null ); }

			try
			{
				//noinspection unchecked
				this.sColumnSQLValue = this.lens.toSQLiteString(
						this.lens.getValueFrom( o, this.fldColumn ) ) ;
			}
			catch( IllegalAccessException xAccess )
			{
				throw SchematicException.fieldWasInaccessible(
						this.clsTable.getSimpleName(),
						this.fldColumn.getName(),
						xAccess
					);
			}

			return this ;
		}
	}

/// Static Methods /////////////////////////////////////////////////////////////

	// getTableName(Class,SQLiteTable) was removed in 0.2.0 (#49).

/// Static Constants ///////////////////////////////////////////////////////////

	/** The tag used by logging statements in this class. */
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
	 * @see #setSchemaClasses(List)
	 */
	protected List<Class<? extends SQLightable>> m_aclsSchema = null ;

	/**
	 * A map of schematic classes to their reflections.
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected SQLightable.ReflectionMap m_mapReflections = null ;

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
		    .processReflections()
			;
		m_mapRefractor = (new RefractorMap()).init() ;
		//noinspection deprecation - TODO (deprecation) remove in next major revision
		this.registerCustomRefractors() ;
	}

	/**
	 * Caches a list of classes that define the database schema.
	 *
	 * Consumed by {@link #SQLiteHouse(Factory)}.
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
	 * Given that the list of schematic classes has been populated, discover and
	 * cache their characteristics for future reference.
	 *
	 * Consumed by {@link #SQLiteHouse(Factory)}; must follow
	 * {@link #setSchemaClasses}.
	 *
	 * @return (fluid)
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected DSC processReflections()
	{
		if( m_mapReflections == null )
			m_mapReflections = new SQLightable.ReflectionMap() ;
		else m_mapReflections.clear() ;

		for( Class<? extends SQLightable> cls : m_aclsSchema )
		{
			SQLightable.Reflection<? extends SQLightable> tbl =
					SQLightable.Reflection.reflect(cls) ;
			m_mapReflections.put( cls, tbl ) ;
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
	 * @deprecated zer0bandwidth-net/android 0.2.1 (#56) &mdash; instead, use
	 *  the {@code refractor} attribute of the {@link SQLiteColumn} annotation
	 *  to define refractors for each relevant field in your schematic classes
	 */
	@SuppressWarnings( { "unchecked", "DeprecatedIsStillUsed" } )
	protected DSC registerCustomRefractors()
	{ return (DSC)this ; } // trivially

/// net.zer0bandwidth.android.lib.database.SQLitePortal ////////////////////////

	/**
	 * Called by Android when the consumer tries to connect to the database.
	 * This method will iterate over the list of table classes and execute the
	 * SQL statement which will create that table.
	 *
	 * <p>This method was designed to be a {@code final} implementation, but is
	 * left extensible for descendant classes, just in case they might need to
	 * perform any custom post-processing.</p>
	 *
	 * @param db a direct handle to the SQLite database (provided by the Android
	 *  OS)
	 * @throws SchematicException <i>(since 0.1.7 #50)</i> if something goes
	 *  wrong while processing the schema definition
	 */
	@Override
	public void onCreate( SQLiteDatabase db )
	throws SchematicException
	{
		Log.i( LOG_TAG, "Executing onCreate()" ) ;
		for( Class<? extends SQLightable> clsTable : m_aclsSchema )
		{
			SQLightable.Reflection tbl = this.getReflection(clsTable) ;
			String sSQL = null ;
			try
			{
				sSQL = tbl.getTableCreationSQL() ;
				db.execSQL( sSQL ) ;
			}
			catch( Exception x )
			{
				throw SchematicException
						.tableCreationOrUpdateFailed( tbl, sSQL, x ) ;
			}
		}
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
			this.upgradeTable( db, clsTable, nOld ) ;
	}

	/**
	 * Called by {@link #onUpgrade} to upgrade a single SQLite database table
	 * based on the information reflected by a specific schematic class.
	 * @param db a direct handle to the SQLite database (provided to
	 *           {@link #onUpgrade} by the Android OS)
	 * @param clsTable the schematic class that defines the table
	 * @param nOld the version of the schema that is installed
	 * @param <SC> the schematic class
	 * @return (fluid)
	 * @throws SchematicException if the class is not part of the schema
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected <SC extends SQLightable> SQLiteHouse<DSC> upgradeTable(
			SQLiteDatabase db, Class<SC> clsTable, int nOld )
	throws SchematicException
	{
		SQLightable.Reflection<SC> tbl = this.getReflection(clsTable) ;
		int nTableSince = tbl.getFirstSchemaVersion() ;
		if( nTableSince > nOld )
		{ // The whole table is new; create it.
			db.execSQL( tbl.getTableCreationSQL() ) ;
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Created table [" ).append( tbl.getTableName() )
					.append( "] from class [" )
					.append( clsTable.getCanonicalName() )
					.append( "]." )
					.toString()
				);
		}
		else
		{ // Examine the table's columns, creating new ones where needed.
//			SQLightable.Reflection.ColumnMap<SC> mapColumns = tbl.getColumnMap() ;
//			for( SQLightable.Reflection<SC>.Column col : mapColumns.values() )
			for( SQLightable.Reflection<SC>.Column col : tbl.getColumns() )
			{
				int nColSince = col.getColAttrs().since() ;
				if( nColSince > nOld )
				{
					db.execSQL( tbl.getAddColumnSQL(col) ) ;
					Log.i( LOG_TAG, (new StringBuilder())
							.append( "Added column [" )
							.append( col.getName() )
							.append( "] to table [" )
							.append( tbl.getTableName() )
							.append( "] from class [" )
							.append( clsTable.getCanonicalName() )
							.append( "]." )
							.toString()
						);
				}
			}
		}

		return this ;
	}

/// Query Commands /////////////////////////////////////////////////////////////

	/**
	 * Inserts an object of a known schematic class into the database.
	 *
	 * Since v0.1.5 (#43), the method also tries to write the auto-incremented
	 * row ID back into the object instance, if that class has a field annotated
	 * to contain that column.
	 *
	 * @param o the object to be inserted
	 * @return the row ID of the inserted record
	 */
	public <ROW extends SQLightable> long insert( ROW o )
	throws SchematicException
	{
		//noinspection unchecked
		Class<ROW> cls = ((Class<ROW>)( o.getClass() ));
		SQLightable.Reflection<ROW> tbl = this.getReflection(cls) ;
		SQLightable.Reflection<ROW>.Column col = tbl.getMagicIDColumn() ;
		ContentValues vals = tbl.toContentValues(o) ;
		// Don't allow the passed object to dictate the auto-inc ID.
		if( col != null ) vals.remove( MAGIC_ID_COLUMN_NAME ) ;

		long nID = QueryBuilder
				.insertInto( m_db, tbl.getTableName() )
				.setValues( vals )
				.execute()
				;

		if( col != null )
		{ // Try to write the ID back into the instance.
			try { col.getField().setLong( o, nID ) ; }
			catch( IllegalAccessException xAccess )
			{ Log.w( LOG_TAG, "Couldn't rewrite row ID into object." ) ; }
		}

		return nID ;
	}

	/**
	 * Updates the values of an object from a known schematic class.
	 * @param o the object to be updated
	 * @return the number of rows updated (generally 1)
	 * @throws SchematicException if the data object's class isn't part of the
	 *  schema, or if the table definition for this class didn't specify its own
	 *  primary key
	 */
	public <ROW extends SQLightable> int update( ROW o )
	throws SchematicException
	{
		//noinspection unchecked
		SQLightable.Reflection<ROW> tbl = ((SQLightable.Reflection<ROW>)
				( this.getReflection( o.getClass() ) )) ;
		SQLightable.Reflection<ROW>.Column col = tbl.getKeyOrMagicIDColumn() ;
		if( col == null )
		{
			throw new SchematicException(
					"Can't use update(SQLightable) without a key column." ) ;
		}
		return QueryBuilder.update( m_db, tbl.getTableName() )
				.setValues( tbl.toContentValues(o) )
				.where( String.format( "%s=%s",
						col.getName(), col.getSQLColumnValueFrom(o) ) )
				.execute()
				;
	}

	/**
	 * Shorthand to obtain an {@link UpdateBuilder} bound to this database and
	 * targeting the table corresponding to the specified schematic class.
	 * @param cls the class that defines part of the schema
	 * @return an {@code UPDATE} query builder prepared for that table
	 * @throws SchematicException if the class is not part of the schema
	 */
	public <SC extends SQLightable> UpdateBuilder update( Class<SC> cls )
	throws SchematicException
	{
		return QueryBuilder.update( m_db,
						this.getReflection(cls).getTableName() ) ;
	}

	/**
	 * Searches the database for a row of the table represented by the supplied
	 * object, such that the primary key value in that object equals the primary
	 * key found in the object. The method does not alter the supplied object;
	 * instead, it returns a new instance with the values found in the database.
	 * @param oCriteria the object whose primary key will be used as the
	 *  criteria for a search
	 * @param <ROW> the specific {@link SQLightable} implementation being sought
	 * @return a new instance of the schematic class, populated with values from
	 *  a row of the database
	 * @throws SchematicException if anything goes wrong along the way
	 */
	public <ROW extends SQLightable> ROW search( ROW oCriteria )
	throws SchematicException
	{
		//noinspection unchecked
		SQLightable.Reflection<ROW> tbl = ((SQLightable.Reflection<ROW>)
						( this.getReflection( oCriteria.getClass() ) )) ;
		SQLightable.Reflection<ROW>.Column col = tbl.getKeyOrMagicIDColumn() ;
		if( col == null )
		{
			throw new SchematicException(
					"Can't use search(SQLightable) without a key column." ) ;
		}
		Cursor crs = null ;
		try
		{
			crs = QueryBuilder.selectFrom( m_db, tbl.getTableName() )
					.where( String.format( "%s=%s",
			              col.getName(), col.getSQLColumnValueFrom(oCriteria) ))
					.execute()
					;
			if( ! crs.moveToFirst() ) return null ; // No such object found.
			return tbl.fromCursor(crs) ;
		}
		finally
		{ closeCursor(crs) ; }
	}

	/**
	 * Searches the database for a row of the table represented by the supplied
	 * schematic class, such that the primary key column value matches the value
	 * supplied in the method call.
	 * @param cls the schematic class being sought
	 * @param sID the unique identifier of the row, which <i>must</i> be a
	 *            string in this flavor of the method
	 * @param <ROW> the schematic class being sought
	 * @return a new instance of the schematic class, populated with values from
	 *  a row of the database
	 * @throws SchematicException if anything goes wrong along the way
	 * @since zer0bandwidth-net/android 0.1.5
	 */
	public <ROW extends SQLightable> ROW search( Class<ROW> cls, String sID )
	throws SchematicException
	{
		SQLightable.Reflection<ROW> tbl = this.getReflection(cls) ;
		SQLightable.Reflection<ROW>.Column colKey = tbl.getKeyColumn() ;
		if( colKey == null )
		{
			throw new SchematicException(
					"Can't use search(Class,String) without a key column." ) ;
		}
		Cursor crs = null ;
		try
		{
			crs = QueryBuilder.selectFrom( m_db, tbl.getTableName() )
					.where( String.format( "%s='%s'",
			              colKey.getName(), sID ) )
					.execute()
					;
			if( ! crs.moveToFirst() ) return null ;     // No such object found.
			return tbl.fromCursor(crs) ;
		}
		finally
		{ closeCursor(crs) ; }
	}

	/**
	 * Searches the database for a row of the table represented by the supplied
	 * object, where the specified integer is equal to the row's magic auto-ID.
	 * @param cls the schematic class that will contain the row
	 * @param nID the auto-incremented integer ID of the row
	 * @param <ROW> the schematic class
	 * @return a new instance of the schematic class, containing the row with
	 *  the specified auto-ID
	 * @throws SchematicException if the class is not part of the schema
	 */
	@SuppressLint("DefaultLocale")
	public <ROW extends SQLightable> ROW select( Class<ROW> cls, long nID )
	throws SchematicException
	{
		SQLightable.Reflection<ROW> tbl = this.getReflection(cls) ;
		Cursor crs = null ;
		try
		{
			crs = QueryBuilder.selectFrom( m_db, tbl.getTableName() )
					.where( String.format( "%s=%d",
							MAGIC_ID_COLUMN_NAME, nID ) )
					.execute()
					;
			if( ! crs.moveToFirst() ) return null ; // No such object found.
			return tbl.fromCursor(crs) ;
		}
		finally
		{ closeCursor(crs) ; }
	}

	/**
	 * Shorthand to obtain a {@link SelectionBuilder} bound to this database and
	 * targeting the table corresponding to the specified schematic class.
	 * @param cls the schematic class
	 * @param <SC> the schematic class
	 * @return a {@code SELECT} query builder prepared for that table
	 * @throws SchematicException if the class is not part of the schema
	 */
	public <SC extends SQLightable> SelectionBuilder selectFrom( Class<SC> cls )
	throws SchematicException
	{
		return QueryBuilder.selectFrom( m_db,
						this.getReflection(cls).getTableName() ) ;
	}

	/**
	 * Searches the database for a row of the table represented by the supplied
	 * objects, and deletes that row.
	 * @param o the schematic class instance to be deleted if found
	 * @param <ROW> the schematic class
	 * @return the number of rows deleted
	 * @throws SchematicException if the table is not part of the schema, or
	 *  doesn't specify a key column
	 */
	public <ROW extends SQLightable> int delete( ROW o )
	throws SchematicException
	{
		//noinspection unchecked
		SQLightable.Reflection<ROW> tbl = ((SQLightable.Reflection<ROW>)
				( this.getReflection( o.getClass() ) )) ;
		SQLightable.Reflection<ROW>.Column col = tbl.getKeyOrMagicIDColumn() ;
		if( col == null )
		{
			throw new SchematicException(
					"Can't use delete(SQLightable) without a key column." ) ;
		}
		return QueryBuilder.deleteFrom( m_db, tbl.getTableName() )
			.where( String.format( "%s=%s",
				col.getName(), col.getSQLColumnValueFrom(o) ) )
			.execute()
			;
	}

	/**
	 * Shorthand to obtain a {@link DeletionBuilder} bound to this database and
	 * targeting the table corresponding to the specified schematic class.
	 * @param cls the class that defines part of the schema
	 * @return a {@code DELETE} query builder prepared for that table
	 * @throws SchematicException if the class is not part of the schema
	 */
	public <SC extends SQLightable> DeletionBuilder deleteFrom( Class<SC> cls )
	throws SchematicException
	{
		return QueryBuilder.deleteFrom( m_db,
				this.getReflection(cls).getTableName() ) ;
	}

/// Other Instance Methods /////////////////////////////////////////////////////

	/**
	 * Accesses the reflection of a class that is marshalled by this instance.
	 * If the class was not included in the list of classes that is part of the
	 * instance's schema, then the method returns {@code null}, rather than
	 * invoking {@link SQLightable.Reflection#reflect(Class)}.
	 * @param cls the schematic class to be described
	 * @param <SC> the schematic class to be described
	 * @return the reflection of that class as stored in this instance
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public <SC extends SQLightable> SQLightable.Reflection<SC> describe( Class<SC> cls )
	{ return m_mapReflections.get(cls) ; }

	/**
	 * Similar to {@link #describe}, but will throw a {@link SchematicException}
	 * if the specified class is not found. This is more useful internally than
	 * as a generaly utility, hence the {@code protected} access control.
	 * @param cls the schematic class to be described
	 * @param <SC> the schematic class to be described
	 * @return the reflection of that class as stored in this instance
	 * @throws SchematicException if the class is not found in this instance
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	protected <SC extends SQLightable> SQLightable.Reflection<SC> getReflection( Class<SC> cls )
	throws SchematicException
	{
		SQLightable.Reflection<SC> tbl = m_mapReflections.get(cls) ;
		if( tbl == null )
			throw SchematicException.classNotInSchema( cls, m_aclsSchema ) ;
		return tbl ;
	}

	/**
	 * Reads a row of data from a cursor, and marshals it into a schematic class
	 * instance corresponding to the table from which the row was fetched.
	 * @param crs the cursor from which data will be marshalled
	 * @param cls the schematic class to which data will be marshalled
	 * @param <SC> the schematic class to which data will be marshalled
	 * @return an instance of the class, containing the cursor's current row
	 * @throws SchematicException if the class is not part of the schema
	 */
	public <SC extends SQLightable> SC fromCursor( Cursor crs, Class<SC> cls )
	throws SchematicException
	{ return this.getReflection(cls).fromCursor(crs) ; }

	/**
	 * Given a result set loaded into a {@link Cursor}, iterate over that cursor
	 * to produce a list of schematic class instances containing the rows in the
	 * result set.
	 * @param cls the schematic class which could contain each row
	 * @param crs the cursor containing the result set
	 * @param <SC> the schematic class which could contain each row
	 * @return a list of schematic class instances, containing the rows of the
	 *  result set
	 * @throws SchematicException if any instance cannot be instantiated
	 * @since zer0bandwidth-net/android 0.1.5 (#43)
	 */
	public <SC extends SQLightable> List<SC> processResultSet(
			Class<SC> cls, Cursor crs )
	throws SchematicException
	{
		List<SC> aResults = new ArrayList<>() ;
		if( crs.moveToFirst() )
		{ // Process each element in turn, marshalling it into the list.
			do aResults.add( this.getReflection(cls).fromCursor(crs) ) ;
			while( crs.moveToNext() ) ;
		}
		return aResults ;
	}

	/**
	 * Creates an empty query context bound to this database helper.
	 * @return a context object
	 * @deprecated zer0bandwidth-net/android 0.2.1 (#56)
	 */ // TODO (deprecation) remove in next major revision
	@SuppressWarnings( { "unchecked", "deprecation" } )
	public QueryContext<DSC> getQueryContext()
	{ return new QueryContext<>( (DSC)this ) ; }

	/**
	 * Creates a query context bound to this database helper, and pre-loads the
	 * information for a specified table.
	 * @param clsTable the schematic table to be pre-loaded
	 * @return a context object
	 * @deprecated zer0bandwidth-net/android 0.2.1 (#56) &mdash; use
	 *  {@link #getReflection(Class)} instead
	 */ // TODO (deprecation) remove in next major revision
	@SuppressWarnings( "deprecation" )
	public QueryContext<DSC> getQueryContext( Class<? extends SQLightable> clsTable )
	{ return this.getQueryContext().loadTableDef(clsTable) ; }

	/**
	 * Accesses the cache of schematic classes for the database.
	 * @return the list of schematic classes loaded into this instance
	 * @since zer0bandwidth-net/android 0.1.7 (#50)
	 */
	public List<Class<? extends SQLightable>> getSchemaClasses()
	{ return m_aclsSchema ; }

	/**
	 * Discovers the type of refractor needed to marshal the specified field.
	 *
	 * Since 0.1.5 (#41), the method will try to discover whether there is a
	 * usable custom implementation specified in the column annotation, and
	 * return that if such a specification exists.
	 *
	 * @param fld a field in a schematic class
	 * @return the refractor which would marshal that class
	 * @throws IntrospectionException if no refractor can be discovered
	 */
	public Refractor<?> getRefractorForField( Field fld )
	throws IntrospectionException
	{
		SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
		Class<? extends Refractor> clsLens = antCol.refractor() ;
		if( clsLens != NullRefractor.class ) try
		{ // The field explicitly defines a custom refractor. Use it.
			return clsLens.newInstance() ;
		}
		catch( Exception x )
		{
			Log.w( LOG_TAG, (new StringBuilder())
					.append( "Cannot instantiate custom refractor class [" )
					.append( clsLens.getCanonicalName() )
					.append( "]." )
					.toString(),
				x ) ;
		}

		try { return m_mapRefractor.get( fld.getType() ).newInstance() ; }
		catch( Exception x )
		{
			throw new IntrospectionException( (new StringBuilder())
					.append( "Could not instantiate a refractor for field [" )
					.append( fld.getName() )
					.append( "]:" )
					.toString(),
				x ) ;
		}
	}
}
