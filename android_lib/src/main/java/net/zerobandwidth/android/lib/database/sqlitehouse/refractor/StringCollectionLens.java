package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import net.zerobandwidth.android.lib.database.SQLiteSyntax;

import java.util.Collection;
import java.util.Collections;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL;

/**
 * Provides a canonical implementation of a {@link Lens} which can marshal a
 * collection of strings to/from a single column in a database row.
 * @since zerobandwidth-net/android 0.1.5 (#42)
 */
public abstract class StringCollectionLens<C extends Collection<String>>
extends Lens<C>
implements Refractor<C>
{
	/**
	 * Implementation classes must provide a concrete override of this method to
	 * declare the delimiter that is used/sought between items in the serialized
	 * list.
	 * @return the delimiter in the serialized list
	 */
	protected abstract String getDelimiter() ;

	/**
	 * Implementation classes must provide a concrete override of this method to
	 * generate instances of the collection class which contains the list items.
	 * @return a collection capable of storing the list items
	 */
	protected abstract C getCollectionInstance() ;

	/**
	 * Serializes the list as a string, using the delimiter provided by
	 * {@link #getDelimiter()}.
	 * @param asValues a collection of string values
	 * @return a serialization of the list
	 */
	public String toStringValue( C asValues )
	{
		return ( asValues == null ? null :
				TextUtils.join( this.getDelimiter(), asValues ) ) ;
	}

	@Override
	public String getSQLiteDataType()
	{ return SQLiteSyntax.SQLITE_TYPE_TEXT ; }

	@Override
	public String toSQLiteString( C o )
	{
		return ( o == null ? SQLITE_NULL :
			String.format( "'%s'", this.toStringValue(o) ) ) ;
	}

	@Override
	public Refractor<C> addToContentValues( ContentValues vals, String sKey, C val )
	{
		vals.put( sKey, this.toStringValue(val) ) ;
		return this ;
	}

	@Override
	public C fromCursor( Cursor crs, String sKey )
	{
		String sValues = crs.getString( crs.getColumnIndex( sKey ) ) ;
		if( sValues == null ) return null ;
		C asValues = this.getCollectionInstance() ;
		Collections.addAll( asValues, sValues.split( this.getDelimiter() ) ) ;
		return asValues ;
	}
}
