package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.IntrospectionException;

import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Provides a mapping of Java classes to their {@link Refractor}
 * implementations. The initializer will define a default mapping, which can be
 * overridden or extended by putting new entries into the map.
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class RefractorMap
extends HashMap<Class<?>,Class<? extends Refractor>>
{
	public static HashMap<Class<?>,Class<? extends Refractor>>
			DEFAULT_MAPPING = new HashMap<>() ;

	static
	{
		// Boolean types...
		DEFAULT_MAPPING.put( boolean.class, BooleanLens.class ) ;
		DEFAULT_MAPPING.put( Boolean.class, BooleanLens.class ) ;

		// Integer types...
		DEFAULT_MAPPING.put( int.class, IntegerLens.class ) ;
		DEFAULT_MAPPING.put( Integer.class, IntegerLens.class ) ;
		DEFAULT_MAPPING.put( long.class, LongLens.class ) ;
		DEFAULT_MAPPING.put( Long.class, LongLens.class ) ;
		DEFAULT_MAPPING.put( short.class, ShortLens.class ) ;
		DEFAULT_MAPPING.put( Short.class, ShortLens.class ) ;

		// Real-number types...
		DEFAULT_MAPPING.put( double.class, DoubleLens.class ) ;
		DEFAULT_MAPPING.put( Double.class, DoubleLens.class ) ;
		DEFAULT_MAPPING.put( float.class, FloatLens.class ) ;
		DEFAULT_MAPPING.put( Float.class, FloatLens.class ) ;

		// String types...
		DEFAULT_MAPPING.put( char.class, CharacterLens.class ) ;
		DEFAULT_MAPPING.put( Character.class, CharacterLens.class ) ;
		DEFAULT_MAPPING.put( String.class, StringLens.class ) ;

		// Date/time types...
		DEFAULT_MAPPING.put( java.util.Date.class, DateLens.class ) ;
		DEFAULT_MAPPING.put( java.sql.Date.class, SQLDateLens.class ) ;
		DEFAULT_MAPPING.put( GregorianCalendar.class, GregorianCalendarLens.class ) ;

		// Other types...
	}

	/**
	 * Gets the default refractor class for the specified data class.
	 * @param cls the data class
	 * @return the default refractor for that class
	 * @since zerobandwidth-net/android 0.1.7 (#50)
	 */
	public static Class<? extends Refractor> getRefractorFor( Class<?> cls )
	{ return DEFAULT_MAPPING.get(cls) ; }

	/**
	 * Initializes the instance with the default mapping.
	 */
	public RefractorMap()
	{
		super( DEFAULT_MAPPING.size() ) ;
		this.init() ;
	}

	/**
	 * Wipes out all current entries and restores the default mapping.
	 * @return (fluid)
	 */
	public RefractorMap init()
	{
		this.clear() ;
		this.putAll( DEFAULT_MAPPING ) ;
		return this ;
	}

	/**
	 * Discovers the SQLite column type for a given class, by calling the
	 * class's refractor.
	 * @param cls the class to be refracted
	 * @return the SQLite column type given by that class's refractor
	 * @throws IntrospectionException if the refractor can't be instantiated
	 */
	public String getSQLiteColumnTypeFor( Class<?> cls )
	throws IntrospectionException
	{
		Class<? extends Refractor> clsRefractor = this.get(cls) ;
		if( clsRefractor == null )
		{
			throw new IntrospectionException( String.format(
					"Could not resolve refractor for class [%s].",
					cls.getCanonicalName() ) ) ;
		}
		try { return clsRefractor.newInstance().getSQLiteDataType() ; }
		catch( Exception x )
		{
			throw new IntrospectionException( String.format(
					"Could not instantiate refractor [%s]",
					clsRefractor.getCanonicalName() ) ) ;
		}
	}
}
