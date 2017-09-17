package net.zerobandwidth.android.lib.database.sqlitehouse.content.exceptions;

/**
 * Thrown by
 * {@link net.zerobandwidth.android.lib.database.sqlitehouse.content.SQLiteHouseKeeper}
 * when access through the provider can't be achieved for some reason.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 */
public class SQLiteContentException
extends RuntimeException
{
	public static final String DEFAULT_MESSAGE =
		"Failed to access the database content provider." ;

	/**
	 * Used when a request's authority doesn't match the one expected by the
	 * provider.
	 * @param sBadAuthority the bad authority
	 * @return an exception with an appropriate message
	 */
	public static SQLiteContentException wrongAuthority( String sBadAuthority )
	{
		return new SQLiteContentException( (new StringBuilder())
				.append( "Request authority [" )
				.append( sBadAuthority )
				.append( "] does not match provider." )
				.toString()
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
