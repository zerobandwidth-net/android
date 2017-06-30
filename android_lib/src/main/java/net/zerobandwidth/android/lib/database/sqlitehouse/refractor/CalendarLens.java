package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Calendar;

/**
 * Marshals {@link Calendar} objects.
 *
 * The lens converts all dates into integer GMT timestamps for storage in the
 * DB, then recreates the {@code Calendar} objects upon retrieval. This is
 * because integer timestamps are more reliably stored and are absolute (no
 * fretting about timezones).
 *
 * For custom calendar implementations, extend this class with your own empty
 * descendant templatized on your calendar.
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
implements Refractor<C>
{
	@Override
	public String getSQLiteDataType()
	{ return SQLITE_TYPE_INT ; }

	@Override
	public String toSQLiteString( C o )
	{ return Long.toString( o.getTimeInMillis() ) ; }

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
