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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses custom annotations to automatically construct and manage SQLite
 * databases with tables in which each row holds a serialization of a specified
 * Java class.
 *
 * This class is based on {@link SQLitePortal} and provides all of the same
 * methods for accessing the database.
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
 *     public class MyDatabaseClass extends SQLiteHouse<MyDatabaseClass> {}
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
 *        {@literal @}SQLiteColumn( "person_id" )
 *        {@literal @}SQLitePrimaryKey
 *         protected String m_sID ;
 *
 *        {@literal @}SQLiteColumn( "first_name" )
 *         protected String m_sFirstName ;
 *
 *        {@literal @}SQLiteColumn( "last_name" )
 *         protected String m_sLastName ;
 *
 *        {@literal @}SQLiteColumn( "birthday" )
 *         protected Date m_dBirthdate ;
 *
 *        {@literal @}SQLiteColumn( "address" )
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
 * are <i>not</i> serialized into the database, merely be leaving those members
 * undecorated.</p>
 *
 * <p>Note also the {@code @SQLitePrimaryKey} designation; this allows you to
 * explicitly specify a data element that should be used as the primary key for
 * the table.</p>
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
 * <h3>Connecting to the Database</h3>
 *
 * <p>Since this class extends {@link SQLitePortal}, which in turn is descended
 * from {@link android.database.sqlite.SQLiteOpenHelper}, it provides the same
 * methods for managing connections to the database. Connections may be
 * established with {@link SQLitePortal#openDB()} and released with
 * {@link SQLitePortal#close()}.</p>
 *
 * @param <DSC> A descendant class. When creating a descendant class, it should
 *  extend {@code SQLiteHouse} templatized for itself. This will ensure that all
 *  methods inherited from {@code SQLiteHouse} return instances of the
 *  descendant class, rather than the parent class.
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
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
		@Override
		public int compare( Field fldFirst, Field fldSecond )
		{
			SQLiteColumn antFirst =
					fldFirst.getAnnotation( SQLiteColumn.class ) ;
			SQLiteColumn antSecond =
					fldSecond.getAnnotation( SQLiteColumn.class ) ;

			// Try comparing the "index" attribute first.
			if( antFirst.index() < antSecond.index() ) return -1 ;
			if( antFirst.index() > antSecond.index() ) return 1 ;

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


/// Static Constants ///////////////////////////////////////////////////////////

	public static final String LOG_TAG = SQLiteHouse.class.getSimpleName() ;

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

	protected RefractorMap m_mapRefractor = null ;

/// Constructors and Initializers //////////////////////////////////////////////

	protected SQLiteHouse( SQLiteHouse.Factory factory )
	{
		super( factory.m_ctx, factory.m_sDatabaseName,
				factory.m_cf, factory.m_nSchemaVersion ) ;
		this.setSchemaClasses( factory.m_aclsSchema )
			.processFieldsOfClasses()
			;
		m_mapRefractor = (new RefractorMap()).init() ;
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

/// net.zerobandwidth.android.lib.database.SQLitePortal ////////////////////////

	@Override
	public void onCreate( SQLiteDatabase db )
	{
		Log.i( LOG_TAG, "Executing onCreate()" ) ;
		for( Class<? extends SQLightable> clsTable : m_aclsSchema )
			this.executeTableCreationSQL( clsTable ) ;
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int nOld, int nNew )
	{ // TODO Can we provide a final implementation here?
		Log.i( LOG_TAG, "Executing onUpdate()" ) ;
	}

/// Instance Methods ///////////////////////////////////////////////////////////

	protected <T extends SQLightable> DSC executeTableCreationSQL( Class<T> clsTable )
	{
		String sName = null ;
		int nSince = 1 ;
		SQLiteTable antTable = clsTable.getAnnotation( SQLiteTable.class ) ;
		if( antTable != null )
		{
			sName = antTable.value() ;
			nSince = antTable.since() ;
		}
		else // No annotation, but was added to DB spec. Fake a name.
			sName = clsTable.getSimpleName().toLowerCase() ;


		StringBuilder sb = new StringBuilder() ;
		sb.append( "CREATE TABLE IF NOT EXISTS " )
		  .append( sName )
		  .append( " ( " )
		  .append( MAGIC_ID_COLUMN_NAME )
		  .append( " INT PRIMARY KEY AUTOINCREMENT )" )
		  ;

		T oTable = null ;
		try { oTable = clsTable.newInstance() ; }
		catch( Exception x )
		{
			Log.d( LOG_TAG, String.format(
					"Table class [%s] has no default constructor.",
					clsTable.getCanonicalName()
				)) ;
			oTable = null ;
		}

		for( Field fld : m_mapFields.get(clsTable) )
		{
			SQLiteColumn antCol = fld.getAnnotation( SQLiteColumn.class ) ;
			sb.append( ", ( " )
			  .append( antCol.name() )
			  .append( " " )
			  .append( m_mapRefractor.getSQLiteColumnTypeFor( fld.getType() ) )
			  ;
			if( fld.getAnnotation( SQLitePrimaryKey.class ) != null )
				sb.append( " UNIQUE NOT NULL" ) ; // but we'll use it as a key
			else
				sb.append(( antCol.is_nullable() ? " NULL" : " NOT NULL" )) ;

		}

		//noinspection unchecked
		return (DSC)this ;
	}

}
