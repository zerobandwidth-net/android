package net.zerobandwidth.android.lib.database.sqlitehouse;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteColumn;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLitePrimaryKey;
import net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;
import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.NullRefractor;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.Refractor;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.RefractorMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_TYPE_INT;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_TYPE_TEXT;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_ADD_COLUMN;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_ALTER_TABLE;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_COLUMN_DEFAULT;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_COLUMN_DEFAULT_NULL;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_COLUMN_IS_KEYLIKE;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_COLUMN_NOT_NULLABLE;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQL_COLUMN_NULLABLE;
import static net.zerobandwidth.android.lib.database.sqlitehouse.SQLiteHouse.MAGIC_ID_COLUMN_NAME;

/**
 * Designates a class as a data container which can be used in a database
 * defined and managed by {@link SQLiteHouse}. This class must also be decorated
 * by a {@link net.zerobandwidth.android.lib.database.sqlitehouse.annotations.SQLiteTable}
 * annotation which defines the attributes of that table. Implementation classes
 * <b>must</b> also define a zero-argument constructor in order to be usable by
 * {@link SQLiteHouse#search} or {@link SQLiteHouse#select}.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public interface SQLightable
{
	/**
	 * A full reflection of a {@link SQLightable} object, which represents, and
	 * is used to marshal data for, an SQLite table. Usable by
	 * {@link SQLiteHouse} and any other class that needs to know how this
	 * table class is defined.
	 * @param <T> the class being reflected
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	class Reflection<T extends SQLightable>
	{
		public static final String LOG_TAG = Reflection.class.getSimpleName() ;

		public static <ST extends SQLightable> Reflection<ST> reflect( Class<ST> cls )
		{ return new Reflection<>( cls ) ; }

		/**
		 * Provides a syntactic shorthand for working with maps of fields to
		 * column reflections.
		 * @since zerobandwidth-net/android 0.1.7 (#50)
		 */
		public static class ColumnMap<ST extends SQLightable>
		extends LinkedHashMap<
				Field, Reflection<ST>.Column >
		{
			public ColumnMap()
			{ super() ; }

			/**
			 * Returns the ordered set of column reflections as a list. This is
			 * provided because using the standard {@link #values()} method
			 * seems to confuse the compiler when used in contexts where the
			 * generic type parameter might be erased.
			 * @return a list of column reflections
			 */
			public List<Reflection<ST>.Column> getColumnsAsList()
			{
				List<Reflection<ST>.Column> aCols
						= new ArrayList<>( this.size() ) ;
				aCols.addAll( this.values() ) ;
				return aCols ;
			}
		}

		/**
		 * A full reflection of a {@link SQLiteColumn} field.
		 * Used by {@link Reflection}.
		 *
		 * Replaces {@code SQLiteHouse#getColumnDefinitionClause(QueryContext)}.
		 *
		 * @since zerobandwidth-net/android 0.1.7 (#50)
		 */
		public class Column
		{
			/** The field that defines the column schema. */
			protected Field m_fldColumn = null ;
			/** The annotation that defines the column schema. */
			protected SQLiteColumn m_antColumn = null ;
			/** Indicates whether the column is also annotated as a key. */
			protected boolean m_bKey = false ;
			/**
			 * The {@link Refractor} implementation to be used for the column.
			 */
			protected Refractor m_lens = null ;

			/**
			 * Initializes the object with the selected field's data.
			 * @param fld the field to be analyzed
			 * @throws SchematicException if the field does not have a
			 *  {@link SQLiteColumn} annotation
			 * @throws IntrospectionException if something goes wrong while
			 *  analyzing the field
			 */
			public Column( Field fld )
			throws SchematicException, IntrospectionException
			{
				m_fldColumn = fld ;
				m_antColumn = fld.getAnnotation( SQLiteColumn.class ) ;
				if( m_antColumn == null )
				{ throw SchematicException.fieldNotAnnotated( fld ) ; }
				m_bKey = fld.isAnnotationPresent( SQLitePrimaryKey.class ) ;
				m_lens = this.discoverRefractor() ;
			}

			/**
			 * Discovers the {@link Refractor} implementation to be used for
			 * this column field.
			 * @return the implementation which marshals this field's data
			 * @throws IntrospectionException if the refractor instance can't be
			 *  created
			 * @throws SchematicException if the refractor class can't be
			 *  resolved
			 */
			protected Refractor discoverRefractor()
			throws IntrospectionException, SchematicException
			{
				Class<? extends Refractor> clsLens = m_antColumn.refractor() ;
				if( clsLens != NullRefractor.class ) try
				{ // The field explicitly specifies a custom refractor. Use it.
					return clsLens.newInstance() ;
				}
				catch( InstantiationException xInstance )
				{
					throw IntrospectionException
							.instanceFailed( clsLens, xInstance ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw IntrospectionException
							.instanceForbidden( clsLens, xAccess ) ;
				}
				// Otherwise, get the standard refractor mapping.
				try
				{
					return RefractorMap.getRefractorFor(m_fldColumn.getType())
							.newInstance() ;
				}
				catch( InstantiationException xInstance )
				{
					throw IntrospectionException
							.instanceFailed( clsLens, xInstance ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw IntrospectionException
							.instanceForbidden( clsLens, xAccess ) ;
				}
				catch( NullPointerException xNull )
				{ throw SchematicException.noLensForColumn( this, xNull ) ; }
			}

			/** Accesses the schematic field. */
			public Field getField()
			{ return m_fldColumn ; }

			/** Accesses the schematic annotation. */
			public SQLiteColumn getColAttrs()
			{ return m_antColumn ; }

			/** Indicates whether the column was annotated as a key. */
			public boolean isKey()
			{ return m_bKey ; }

			/** Shorthand to get the DB column name from the annotation. */
			public String getName()
			{ return m_antColumn.name() ; }

			/** Accesses the column's {@link Refractor} implementation. */
			public Refractor getRefractor()
			{ return m_lens ; }

			/**
			 * Generates the SQL clause that will create this column as part of
			 * a {@code CREATE TABLE} or {@code ALTER TABLE ADD COLUMN}
			 * statement.
			 * @return an SQL statement which defines the column
			 */
			public String getColumnCreationClause()
			{
				StringBuilder sb = new StringBuilder() ;

				if( m_lens == null )
					throw SchematicException.noLensForColumn( this, null ) ;

				sb.append( this.getName() ).append( " " )
				  .append( m_lens.getSQLiteDataType() )
				  ;
				if( this.isKey() ) // Override the annotation's nullability.
					sb.append( SQL_COLUMN_IS_KEYLIKE ) ;
				else
				{
					sb.append(( m_antColumn.is_nullable() ?
							SQL_COLUMN_NULLABLE : SQL_COLUMN_NOT_NULLABLE )) ;
				}
				if( SQLITE_NULL.equals( m_antColumn.sql_default() ) )
				{ // Write "DEFAULT NULL" only if the column is really nullable.
					if( ! this.isKey() && m_antColumn.is_nullable() )
						sb.append( SQL_COLUMN_DEFAULT_NULL ) ;
				}
				else
				{ // Specify the column's default value.
					sb.append( SQL_COLUMN_DEFAULT ) ;
					if( SQLITE_TYPE_TEXT.equals( m_lens.getSQLiteDataType() ) )
					{
						sb.append( "'" )
						  .append( m_antColumn.sql_default() )
						  .append( "'" )
						  ;
					}
					else
						sb.append( m_antColumn.sql_default() ) ;
				}

				return sb.toString() ;
			}

			/**
			 * Tries to discover the value of this column within the
			 * corresponding field of an instance of the schematic class that
			 * defines it.
			 * @param o an instance of the schematic class that defined this
			 *          column
			 * @return the SQLite string representation of the value
			 * @throws SchematicException if something goes wrong while trying
			 *  to discover the value
			 */
			public String getSQLColumnValueFrom( T o )
			{
				if( o == null )
				{
					throw new IllegalArgumentException(
						"Cannot obtain column value from a null object." ) ;
				}
				if( m_lens == null )
				{ throw SchematicException.noLensForColumn( this, null ) ; }
				try
				{
					//noinspection unchecked
					return m_lens.toSQLiteString(
							m_lens.getValueFrom( o, m_fldColumn ) ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw SchematicException.fieldWasInaccessible(
							m_clsTable.getCanonicalName(),
							m_fldColumn.getName(),
							xAccess
						);
				}
			}
		}

		/** The class being reflected. */
		protected Class<T> m_clsTable ;

		/** The annotation on the reflected table class. */
		protected SQLiteTable m_antTable = null ;

		/** The name of the table. Stored once, read repeatedly. */
		protected String m_sTableName = null ;

		/** A map of fields to their column schemas. */
		protected ColumnMap<T> m_mapFields = null ;

		/** A map of DB column names to field definitions. */
		protected HashMap<String,Field> m_mapColNames = null ;

		/** The field that is the primary key for the table. */
		protected Field m_fldKey = null ;

		/**
		 * The field that is the class's placeholder for the magic SQLite
		 * auto-incremented row key, if any.
		 */
		protected Field m_fldMagicID = null ;

		/**
		 * Constructor kicks off a reflection of the selected class.
		 * @param cls the class being reflected
		 * @throws IntrospectionException if something goes wrong
		 */
		public Reflection( Class<T> cls )
		throws IntrospectionException
		{
			m_clsTable = cls ;
			m_antTable = cls.getAnnotation( SQLiteTable.class ) ;
			this.initFieldMap() ;
		}

		/**
		 * Initializes the map of field names to fields.
		 * Consumed by the constructor.
		 * @return (fluid)
		 * @throws IntrospectionException if the column def can't be loaded
		 */
		protected Reflection<T> initFieldMap()
		throws IntrospectionException
		{
			m_mapFields = new ColumnMap<>() ;
			m_mapColNames = new HashMap<>() ;
			List<Field> afldAll =
					Arrays.asList( m_clsTable.getDeclaredFields() ) ;
			ArrayList<Field> afldAnnotated = new ArrayList<>() ;
			for( Field fld : afldAll )
			{ // Find only the fields that are annotated as columns.
				if( fld.isAnnotationPresent( SQLiteColumn.class ) )
				{
					fld.setAccessible(true) ;
					afldAnnotated.add(fld) ;
				}
				if( fld.isAnnotationPresent( SQLitePrimaryKey.class ) )
					m_fldKey = fld ;
			}
			if( afldAnnotated.size() > 1 )
			{
				Collections.sort( afldAnnotated,
						new SQLiteHouse.ColumnIndexComparator() ) ;
			}
			for( Field fld : afldAnnotated )
			{
				Column col = new Column(fld) ;
				m_mapFields.put( fld, col ) ;
				m_mapColNames.put( col.getName(), fld ) ;
				if( SQLiteHouse.MAGIC_ID_COLUMN_NAME.equals( col.getName() ) )
				{ // The class uses this field to marshal the magic ID column.
					m_fldMagicID = fld ;
				}
			}

			return this ;
		}

		/**
		 * Accesses the schematic class reflected in this object.
		 * @return the class reflected in this object
		 */
		public Class<T> getTableClass()
		{ return m_clsTable ; }

		/**
		 * Accesses the {@link SQLiteTable} annotation that defines the
		 * attributes of the table described by this schematic class.
		 * @return the annotation of table attributes
		 */
		public SQLiteTable getTableAttrs()
		{ return m_antTable ; }

		/**
		 * Accesses the name of the database table described by this schematic
		 * class. If the name is not explicitly given in an annotation, then it
		 * will be derived by lower-casing the simple name of the schematic
		 * class itself.
		 * @return the name of the table
		 */
		public String getTableName()
		{
			if( m_sTableName == null )
			{ // Discover the name once, then use the stored value thereafter.
				if( m_antTable == null )
					m_sTableName = m_clsTable.getSimpleName().toLowerCase() ;
				else
					m_sTableName = m_antTable.value() ;
			}
			return m_sTableName ;
		}

		/**
		 * Accesses the field in the schematic class corresponding to the name
		 * of a column in the table described by the class.
		 * @param sColName the name of a column in the database table
		 * @return the field in this schematic class which corresponds to that
		 *  column
		 */
		public Field getField( String sColName )
		{ return m_mapColNames.get(sColName) ; }

		/**
		 * Accesses the complete map of fields and column reflections.
		 * @return the complete map of fields and columns
		 */
		public ColumnMap<T> getColumnMap()
		{ return m_mapFields ; }

		/**
		 * Accesses the SQLite column definition defined by the given field in
		 * the schematic class.
		 * @param fld the field that corresponds to a database table column
		 * @return a reflection of that column
		 */
		public Column getColumnDef( Field fld )
		{ return m_mapFields.get(fld) ; }

		/**
		 * Accesses the SQLite column definition for the given column name.
		 * @param sColName the name of the table column
		 * @return a reflection of that column
		 */
		public Column getColumnDef( String sColName )
		{ return this.getColumnDef( this.getField( sColName ) ) ; }

		/**
		 * Accesses the field that was defined as the practical key for the
		 * table.
		 * @return the field usable as a key
		 */
		public Field getKeyField()
		{ return m_fldKey ; }

		/**
		 * Accesses the column reflection for the field defined as the practical
		 * key for the table.
		 * @return the reflection of the table's key column
		 */
		public Column getKeyColumn()
		{
			return ( this.getKeyField() != null ?
				m_mapFields.get( this.getKeyField() ) : null ) ;
		}

		/**
		 * Accesses the field that was defined as the container for the SQLite
		 * magic auto-incremented row ID, if any.
		 * @return the field which holds the auto-inc row ID
		 */
		public Field getMagicIDField()
		{ return m_fldMagicID ; }

		/**
		 * Accesses the column reflection for the field defined as the container
		 * for the SQLite magic auto-incremented row ID, if any.
		 * @return the reflection of the table's row ID column
		 */
		public Column getMagicIDColumn()
		{
			return ( this.getMagicIDField() != null ?
				m_mapFields.get( this.getMagicIDField() ) : null ) ;
		}

		/**
		 * Tries to find a usable key column, by first looking for a field
		 * defined as the practical key, then looking for a field defined as the
		 * magic auto-incremented row ID, then returning {@code null} if neither
		 * is defined.
		 * @return a column usable as a key, or {@code null} if neither a
		 *  practical key nor a magic row ID column is defined
		 */
		public Column getKeyOrMagicIDColumn()
		{
			Column col = null ;
			if( m_fldKey != null )
				col = m_mapFields.get(m_fldKey) ;
			else if( m_fldMagicID != null )
				col = m_mapFields.get(m_fldMagicID) ;
			return col ;             // In very rare cases, might still be null.
		}

		/**
		 * Generates the SQL statement which will create the table represented
		 * by this schematic class, based on the class itself, and its
		 * {@link SQLiteTable} annotation (if any).
		 * @return an SQL statement which will create the SQLite table
		 */
		public String getTableCreationSQL()
		{
			StringBuilder sb = new StringBuilder() ;

			sb.append( "CREATE TABLE IF NOT EXISTS " )
			  .append( this.getTableName() )
			  .append( " ( " ).append( MAGIC_ID_COLUMN_NAME )
			  .append( " " ).append( SQLITE_TYPE_INT )
			  .append( " PRIMARY KEY AUTOINCREMENT" )
			  ;

			for( Map.Entry<Field,Reflection<T>.Column> pair : m_mapFields.entrySet() )
			{ // Add the column creation SQL for each schematic field.
				Column col = pair.getValue() ;
				if( MAGIC_ID_COLUMN_NAME.equals( col.getName() ) )
					continue ;   // Allow the schematic class to marshal the ID.

				sb.append( ", " )
				  .append( col.getColumnCreationClause() )
				  ;
			}

			sb.append( " )" ) ;

			Log.d( LOG_TAG, sb.toString() ) ; // DEBUG ONLY

			return sb.toString() ;
		}

		/**
		 * Generates the SQL statement which will add the specified column to
		 * this table.
		 * @param col the reflection of the column to be added
		 * @return an SQL statement which adds the column to this table
		 */
		public String getAddColumnSQL( Column col )
		{
			//noinspection StringBufferReplaceableByString
			StringBuilder sb = new StringBuilder() ;
			sb.append( SQL_ALTER_TABLE ).append( this.getTableName() )
			  .append( SQL_ADD_COLUMN )
			  .append( col.getColumnCreationClause() )
			  ;

			Log.d( LOG_TAG, sb.toString() ) ; // DEBUG ONLY

			return sb.toString() ;
		}

		/**
		 * Determines the first version of the schema in which this schematic
		 * class was included. If the {@link SQLiteTable} annotation is missing,
		 * then the method returns {@code 1}.
		 * @return the schema version in which the schematic class was
		 *  introduced
		 */
		public int getFirstSchemaVersion()
		{
			if( m_antTable == null ) return 1 ;
			else return m_antTable.since() ;
		}

		/**
		 * Constructs an empty instance of the {@link SQLightable}
		 * implementation class reflected in this object.
		 * @return an empty instance of the schematic class
		 * @throws IntrospectionException if the schematic class could not be
		 *  constructed for some reason
		 */
		public T getInstance()
		throws IntrospectionException
		{
			try
			{
				Constructor ctor = m_clsTable.getDeclaredConstructor() ;
				if( ctor == null ) // try something different
					ctor = m_clsTable.getConstructor() ;
				ctor.setAccessible(true) ;
				//noinspection unchecked - guaranteed
				return ((T)(ctor.newInstance())) ;
			}
			catch( Exception x )
			{ throw IntrospectionException.instanceFailed( m_clsTable, x ) ; }
		}

		/**
		 * Reads a row of data from the specified cursor, and marshals it into a
		 * schematic class instance corresponding to the table from which the
		 * row was fetched.
		 * @param crs the cursor which is currently pointing to a data row
		 * @return an instance of the class, containing the cursor's current row
		 * @throws IntrospectionException if the data class could not be
		 *  constructed for some reason
		 * @throws SchematicException if the data could not be properly
		 *  marshalled into the class instance
		 */
		public T fromCursor( Cursor crs )
		throws IntrospectionException, SchematicException
		{
			T oResult = this.getInstance() ; // Can throw IntrospectionException

			Collection<Column> aColumns = this.getColumnMap().values() ;
			for( Column col : aColumns )
			{
				try
				{
					col.getField().set( oResult,
						col.getRefractor().fromCursor( crs, col.getName() ) ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw SchematicException.fieldWasInaccessible(
							m_clsTable.getCanonicalName(),
							col.getName(), xAccess
						);
				}
			}

			return oResult ;
		}

		/**
		 * Reads fields from a supplied {@link Bundle}, and marshals it into a
		 * schematic class instance.
		 * @param bndl the bundle into which data was marshalled
		 * @return an instance of the class, containing the bundled data
		 * @throws IntrospectionException if the data class could not be
		 *  constructed for some reason
		 * @throws SchematicException if the data could not be properly
		 *  marshalled into the class instance
		 */
		public T fromBundle( Bundle bndl )
		throws IntrospectionException, SchematicException
		{
			T oResult = this.getInstance() ; // Can throw IntrospectionException

			Collection<Column> aColumns = this.getColumnMap().values() ;
			for( Column col : aColumns )
			{
				try
				{
					col.getField().set( oResult,
						col.getRefractor().fromBundle( bndl, col.getName() ) ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw SchematicException.fieldWasInaccessible(
							m_clsTable.getCanonicalName(),
							col.getName(), xAccess
						);
				}
			}

			return oResult ;
		}

		/**
		 * Extracts the values of all known fields, corresponding to database
		 * table columns, from a schematic class instance, and returns a
		 * {@link ContentValues} instance containing those values.
		 *
		 * Replaces {@code SQLiteHouse#toContentValues(SQLightable)}.
		 *
		 * @param oSource the object to be processed
		 * @return the values that would be stored in the database
		 * @throws SchematicException if no {@link Refractor} implementation can
		 *  be found for one of the columns/fields
		 */
		public ContentValues toContentValues( T oSource )
		throws SchematicException
		{
			ContentValues vals = new ContentValues() ;
			for( Column col : this.getColumnMap().values() )
			{
				Refractor lens = col.getRefractor() ;

				if( lens == null )
					throw SchematicException.noLensForColumn( col, null ) ;

				try
				{
					//noinspection unchecked - lens corresponds to field
					lens.addToContentValues( vals, col.getName(),
							lens.getValueFrom( oSource, col.getField() ) ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw SchematicException.fieldWasInaccessible(
							m_clsTable.getCanonicalName(),
							col.getField().getName(),
							xAccess
						);
				}
				catch( SchematicException xSchema )
				{
					Log.e( LOG_TAG, (new StringBuilder())
								.append( "Could not extract value for field [" )
								.append( col.getField().getName() )
								.append( "]:" )
								.toString(),
							xSchema
						);
				} // and continue
			}
			return vals ;
		}

		/**
		 * Extracts the values of all known fields, corresponding to database
		 * table columns, from a schematic class instance, and returns a
		 * {@link Bundle} instance containing those values.
		 * @param oSource the object to be processed
		 * @return the values that would be stored in the database
		 * @throws SchematicException if no {@link Refractor} implementation can
		 *  be found for one of the column's fields
		 * @since zerobandwidth-net/android 0.1.7 (#50)
		 */
		public Bundle toBundle( T oSource )
		throws SchematicException
		{
			Bundle bndl = new Bundle() ;
			for( Column col : this.getColumnMap().values() )
			{
				Refractor lens = col.getRefractor() ;

				if( lens == null )
					throw SchematicException.noLensForColumn( col, null ) ;

				try
				{
					// noinspection unchecked - lens corresponds to field
					lens.addToBundle( bndl, col.getName(),
							lens.getValueFrom( oSource, col.getField() ) ) ;
				}
				catch( IllegalAccessException xAccess )
				{
					throw SchematicException.fieldWasInaccessible(
							m_clsTable.getCanonicalName(),
							col.getField().getName(),
							xAccess
						);
				}
				catch( SchematicException xSchema )
				{
					Log.e( LOG_TAG, (new StringBuilder())
								.append( "Could not get value for field [" )
								.append( col.getField().getName() )
								.append( "] from a bundle:" )
								.toString(),
							xSchema
						);
				} // and continue
			}
			return bndl ;
		}
	}

	/**
	 * Provides a precise method for retrieving an entry from a map of schematic
	 * classes to their {@link Reflection} instances.
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	class ReflectionMap
	extends HashMap< Class<? extends SQLightable>,
			Reflection<? extends SQLightable> >
	{
		public ReflectionMap()
		{ super() ; }

		/**
		 * As {@link Map#get(Object)}, but forces a cast on the
		 * {@link Reflection} object that is returned from the map. This cast is
		 * made unchecked; it is up to the consumer to ensure that the types
		 * match.
		 * @param cls the schematic class
		 * @param <SC> the schematic class
		 * @return a reflection of the schematic class
		 */
		public <SC extends SQLightable> Reflection<SC> get( Class<SC> cls )
		{
			//noinspection unchecked - guaranteed logically
			return ((Reflection<SC>)( super.get(cls) )) ;
		}

		/**
		 * As {@link Map#put(Object,Object)}, but since we can obtain the
		 * {@link Reflection} instance on-the-fly, it is not required.
		 * @param cls the schematic class
		 * @param <SC> the schematic class
		 * @return the previously-mapped reflection, if any
		 */
		public <SC extends SQLightable> Reflection<SC> put( Class<SC> cls )
		{
			//noinspection unchecked - guaranteed logically
			return ((Reflection<SC>)
					( super.put( cls, Reflection.reflect(cls) ) ))  ;
		}
	}
}
