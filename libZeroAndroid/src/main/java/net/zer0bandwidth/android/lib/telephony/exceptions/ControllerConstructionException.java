package net.zer0bandwidth.android.lib.telephony.exceptions;

/**
 * Exception thrown by
 * {@link net.zer0bandwidth.android.lib.telephony.TelephonyController#TelephonyController}
 * when any part of the initialization process critically fails. This may be
 * caught and inspected to determine the root cause of the failure.
 * @since zer0bandwidth-net/android 0.0.5 (#16)
 * @see RuntimeException#getCause()
 */
public class ControllerConstructionException
extends RuntimeException
{
	protected static final String DEFAULT_MESSAGE =
			"Failed to construct the telephony controller." ;

	/** Constructs an exception with the default message. */
	@SuppressWarnings("unused")
	public ControllerConstructionException()
	{ super( DEFAULT_MESSAGE ) ; }

	/** Constructs an exception with the specified message. */
	public ControllerConstructionException( String sMessage )
	{ super( sMessage ) ; }

	/** Constructs an exception with the specified message and cause. */
	public ControllerConstructionException( Throwable xCause )
	{ super( DEFAULT_MESSAGE, xCause ) ; }

	/** Constructs an exception with the default message and specified cause. */
	@SuppressWarnings("unused")
	public ControllerConstructionException( String sMessage, Throwable xCause )
	{ super( sMessage, xCause ) ; }
}
