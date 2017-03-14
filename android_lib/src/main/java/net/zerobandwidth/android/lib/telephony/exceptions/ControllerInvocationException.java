package net.zerobandwidth.android.lib.telephony.exceptions;

/**
 * Exception thrown by methods of
 * {@link net.zerobandwidth.android.lib.telephony.TelephonyController} when the
 * invocation of the underlying Android OS method fails. This may be caught and
 * inspected to determine the root cause of the error.
 * @since zerobandwidth.net/android 0.0.5 (#16)
 * @see RuntimeException#getCause()
 */
public class ControllerInvocationException
extends RuntimeException
{
	/**
	 * Generates the exception's standard message based on the method name.
	 * @param sMethod the name of the invoked method
	 * @return a standardized error message
	 */
	protected static String getMessage( String sMethod )
	{
		return (new StringBuilder())
				.append( "Failed to invoke method [" )
				.append( sMethod )
				.append( "] of telephony controller." )
				.toString()
				;
	}

	/**
	 * Constructs an exception with a standardized notice of the method name,
	 * and the cause of the failure.
	 * @param sMethod the name of the method that could not be invoked
	 * @param xCause the root cause of the failure
	 */
	public ControllerInvocationException( String sMethod, Throwable xCause )
	{ super( getMessage(sMethod), xCause ) ; }
}
