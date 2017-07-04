package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import net.zerobandwidth.android.lib.database.SQLitePortal;

import java.util.Calendar;

/**
 * Marshals {@link Calendar} objects.
 *
 * <p>The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Calendar} objects upon retrieval. This is
 * because integer timestamps are more reliably stored and are absolute (no
 * fretting about timezones).</p>
 *
 * <p>For custom calendar implementations, extend this class with your own empty
 * descendant templatized on your calendar:</p>
 *
 * <pre>
 *     public class CustomCalendarLens
 *     extends CalendarLens&lt;CustomCalendar&gt;
 *     implements Refractor&lt;CustomCalendar&gt;
 *    {@literal {}}
 * </pre>
 *
 * @since zerobandwidth-net/android 0.1.4 (#26)
 */
public class CalendarLens<C extends Calendar>
extends Lens<C>
implements Refractor<C>
{
	/**
	 * Because {@code SQLiteHouse} will manage all datetime data elements as
	 * integer timestamps, the SQLite data type chosen here is the integer type.
	 * @return {@code "INT"}
	 */
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	/**
	 * When a date cannot be null, this class provides a default value at the
	 * start of the epoch.
	 * @return a {@code Calendar} initialized at epoch time zero
	 */
	@Override
	public C getSQLiteDefaultValue()
	{
		//noinspection unchecked
		C cal = ((C)( C.getInstance() )) ;
		cal.setTimeInMillis(0) ;
		return cal ;
	}

	@Override
	public String toSQLiteString( C o )
	{
		return ( o == null ? SQLitePortal.SQLITE_NULL :
				Long.toString( o.getTimeInMillis() ) ) ;
	}

	@Override
	public Refractor<C> addToContentValues( ContentValues vals, String sKey, C val )
	{
		vals.put( sKey, val.getTimeInMillis() ) ;
		return this ;
	}

	@Override
	public C fromCursor( Cursor crs, String sKey )
	{
		long ts = crs.getLong( crs.getColumnIndex( sKey ) ) ;
		//noinspection unchecked
		C cal = (C)(C.getInstance()) ;
		cal.setTimeInMillis(ts) ;
		return cal ;
	}
}
