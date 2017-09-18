package net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions;

import net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseKeeper;
import net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseRelay;

/**
 * Thrown by {@link SQLiteHouseKeeper} when access through the provider can't be
 * achieved for some reason.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public class SQLiteContentException
extends RuntimeException
{
	public static final String DEFAULT_MESSAGE =
		"Failed to access the database content provider." ;

	/**
	 * Used when one of the extras expected by {@link SQLiteHouseKeeper} or
	 * {@link SQLiteHouseRelay} is not found.
	 * @param sExpectedExtra the name of the extra that was expected
	 * @param xCause the root cause, if any
	 * @return an exception with an appropriate error message
	 */
	public static SQLiteContentException expectedExtraNotFound(
			String sExpectedExtra, Throwable xCause )
	{
		return new SQLiteContentException( (new StringBuilder())
					.append( "No non-empty extra with tag [" )
					.append( sExpectedExtra )
					.append( "] found in received signal." )
					.toString()
				, xCause
			);
	}

	public SQLiteContentException()
	{ super(DEFAULT_MESSAGE) ; }

	public SQLiteContentException( String sMessage )
	{ super(sMessage) ; }

	public SQLiteContentException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	public SQLiteContentException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
