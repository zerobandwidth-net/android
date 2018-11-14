package net.zerobandwidth.android.lib.telephony;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import net.zerobandwidth.android.lib.app.Managers;
import net.zerobandwidth.android.lib.telephony.exceptions.ControllerConstructionException;
import net.zerobandwidth.android.lib.telephony.exceptions.ControllerInvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides programmatic access to common call control tasks.
 *
 * <h3>As an Instance</h3>
 *
 * An instance of this class provides programmatic access to the Android OS
 * internal telephony controller.
 *
 * This class is based on several interesting StackOverflow posts and is still
 * highly experimental.
 *
 * The member functions represent a large portion of the {@code ITelephony}
 * interface as discovered in the Android open source repository. However, in
 * this class, several of those methods are marked as deprecated. For each of
 * these deprecated methods, there is a static alternative that does not require
 * the reflexive workarounds of this class, and which will likely be more
 * efficient. Though marked as deprecated, there is no plan to remove them from
 * this library; the deprecation is merely an encouragement to choose
 * alternatives.
 *
 * <h3>As a Static Utils Class</h3>
 *
 * Static methods of the class are a more intuitive shorthand for "normal"
 * operations related to call control, and do not require the construction of an
 * instance.
 *
 * @see <a href="http://stackoverflow.com/questions/599443/how-to-hang-up-outgoing-call-in-android">
 *     StackOverflow Q.599443: How to hang up outgoing call in Android?</a>
 * @see <a href="http://stackoverflow.com/questions/2001146/reflection-to-access-advanced-telephony-features">
 *     StackOverflow Q.2001146: Reflection to access advanced telephony features</a>
 * @see <a href="http://prasanta-paul.blogspot.com/2010/09/call-control-in-android.html">
 *     Prasanta Paul, "Call Control in Android", 2010-09-03</a>
 * @see <a href="https://android.googlesource.com/platform/frameworks/base/+/master/telephony/java/com/android/internal/telephony/ITelephony.aidl">
 *     Android Source: com.android.internal.telephony.ITelephony</a>
 * @see <a href="http://stackoverflow.com/questions/4816683/how-to-make-a-phone-call-programatically">
 *     StackOverflow Q.4816683: How to make a phone call programmatically</a>
 *
 * @since zerobandwidth-net/android 0.0.5 (#16)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class TelephonyController
{
/// STATIC API - Provides static utility functions for telephony tasks. ////////

	/**
	 * Fetches an instance of the device's telephony manager.
	 * As of 0.2.0, this is merely an alias for {@link Managers#get} with the
	 * appropriate class selected.
	 * @param ctx the context in which services are available
	 * @return the device's telephony manager service
	 */
	public static TelephonyManager getManager( Context ctx )
	{ return Managers.get( ctx, TelephonyManager.class ) ; }

	/**
	 * Indicates the current call state.
	 *
	 * Equivalent to calling {@link TelephonyManager#getCallState} on an
	 * existing instance of the manager.
	 *
	 * This method <b>requires</b> the
	 * {@code android.permission.READ_PHONE_STATE} permission, and will throw a
	 * {@link SecurityException} if the permission is not granted at runtime.
	 *
	 * @param ctx the context in which to check call state
	 * @return one of the call state indicators provided by
	 *  {@code TelephonyManager}:
	 *  {@link TelephonyManager#CALL_STATE_RINGING CALL_STATE_RINGING},
	 *  {@link TelephonyManager#CALL_STATE_OFFHOOK CALL_STATE_OFFHOOK}, or
	 *  {@link TelephonyManager#CALL_STATE_IDLE CALL_STATE_IDLE}
	 * @throws SecurityException if the
	 *  {@code android.permission.READ_PHONE_STATE} permission is not granted to
	 *  the app at runtime
	 */
	public static int getCallState( Context ctx )
	throws SecurityException
	{ return getManager(ctx).getCallState() ; }

	/**
	 * Indicates whether the phone is in "idle" state &mdash; that is, no
	 * outbound or inbound calls are in progress.
	 * @param ctx the context in which to check call state
	 * @return {@code true} if the call state is "idle"
	 * @throws SecurityException if the
	 *  {@code android.permission.READ_PHONE_STATE} permission is not granted to
	 *  the app at runtime
	 */
	public static boolean isIdle( Context ctx )
	throws SecurityException
	{ return( getCallState(ctx) == TelephonyManager.CALL_STATE_IDLE ) ; }

	/**
	 * Indicates whether the phone is "off-hook" &mdash; that is, waiting for a
	 * call to complete, or fully engaged with an active call.
	 * @param ctx the context in which to check call state
	 * @return {@code true} if the call state is "off-hook"
	 * @throws SecurityException if the
	 *  {@code android.permission.READ_PHONE_STATE} permission is not granted to
	 *  the app at runtime
	 */
	public static boolean isOffhook( Context ctx )
	throws SecurityException
	{ return( getCallState(ctx) == TelephonyManager.CALL_STATE_OFFHOOK ) ; }

	/**
	 * Indicates whether there is currently an incoming call that is causing
	 * either a ring tone or a call waiting tone.
	 * @param ctx the context in which to check call state
	 * @return {@code true} if the call state is "ringing"
	 * @throws SecurityException if the
	 *  {@code android.permission.READ_PHONE_STATE} permission is not granted to
	 *  the app at runtime
	 */
	public static boolean isRinging( Context ctx )
	throws SecurityException
	{ return( getCallState(ctx) == TelephonyManager.CALL_STATE_RINGING ) ; }

	/**
	 * Indicates the current call state.
	 *
	 * Equivalent to calling {@link TelephonyManager#getDataState} on an
	 * existing instance of the manager.
	 *
	 * @param ctx the context in which to check data state
	 * @return one of the data state indicators provided by
	 *  {@code TelephonyManager}:
	 *  {@link TelephonyManager#DATA_DISCONNECTED},
	 *  {@link TelephonyManager#DATA_CONNECTING},
	 *  {@link TelephonyManager#DATA_CONNECTED}, or
	 *  {@link TelephonyManager#DATA_SUSPENDED}
	 */
	public static int getDataState( Context ctx )
	{ return getManager(ctx).getDataState() ; }

	/**
	 * Places an outbound call to the specified telephone number.
	 *
	 * This method <b>requires</b> the {@code android.permission.CALL_PHONE}
	 * permission, and will throw a {@link SecurityException} if the permission
	 * is not granted at runtime.
	 *
	 * @param ctx the context in which to initiate the call
	 * @param sNumber a telephone number
	 * @throws SecurityException if the {@code android.permission.CALL_PHONE}
	 *  permission is not granted to the app at runtime
	 * @see net.zerobandwidth.android.lib.security.PermissionCheckpoint#checkPermission
	 */
	@SuppressWarnings( "MissingPermission" ) // android.permission.CALL_PHONE
	public static void placeCall( Context ctx, String sNumber )
	throws SecurityException
	{
		Intent sig = new Intent( Intent.ACTION_CALL ) ;
		sig.setData( Uri.parse( "tel:" + sNumber ) ) ;
		sig.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) ;
		ctx.startActivity( sig ) ;
	}

/// INSTANCE API - Provides workaround access to native telephony functions. ///
/*
	Note: All functions from the ITelephony AIDL were indiscriminately added
	until line 266, when time started running short for the day and I decided to
	just cherry-pick the most interesting methods below that point. There would
	be a lot more tedious work to add the remaining functions from that
	interface, and many of those don't seem worthwhile because there are open
	alternatives provided by TelephonyManager.
*/

	public static final String LOG_TAG =
		TelephonyController.class.getSimpleName() ;

	public static final String SERVICE_MANAGER_CLASS =
			"android.os.ServiceManager" ;
	public static final String SERVICE_MANAGER_NATIVE_CLASS =
			"android.os.ServiceManagerNative" ;
	public static final String TELEPHONY_INTERFACE =
			"com.android.internal.telephony.ITelephony" ;

	/**
	 * The context in which the controller operates. Must be supplied in the
	 * constructor, and cannot be null.
	 */
	protected Context m_ctx = null ;

	/**
	 * A persistent reference to the Android internal service manager.
	 */
	protected Object m_mgrServices = null ;

	/**
	 * A persistent reflection into the Android internal telephony controller.
	 */
	protected Class<?> m_clsITelephony = null ;

	/**
	 * A persistent reference to the Android internal telephony controller.
	 * Member methods in this class will consume its interface via reflection.
	 */
	protected Object m_mgrTelephony = null ;

	/**
	 * Initializes the controller in the given context.
	 * @param ctx the context in which the controller will operate
	 * @throws ControllerConstructionException if any failures occur under the
	 *  hood
	 * @see #init()
	 */
	public TelephonyController( Context ctx )
	throws ControllerConstructionException
	{
		if( ctx == null ) throw new ControllerConstructionException(
				"Cannot create telephony controller without a context." ) ;

		m_ctx = ctx ;

		try { this.init() ; }
		catch( Exception x )
		{ throw new ControllerConstructionException( x ) ; }
	}

	/**
	 * Initializes the member object that represents the Android telephony
	 * controller.
	 * @return (fluid)
	 * @throws ClassNotFoundException if any of the various classes cannot be
	 *  found via reflection
	 * @throws NoSuchMethodException if any of the various methods cannot be
	 *  found via reflection
	 * @throws IllegalAccessException if a method invocation is forbidden
	 * @throws InvocationTargetException if a method cannot be invoked
	 */
	protected TelephonyController init()
	throws ClassNotFoundException,
			NoSuchMethodException,
			IllegalAccessException,
			InvocationTargetException
	{
		m_clsITelephony = Class.forName(TELEPHONY_INTERFACE) ;
		Class<?> clsStub = m_clsITelephony.getClasses()[0] ;
		Class<?> clsServiceManager = Class.forName(SERVICE_MANAGER_CLASS) ;
		Class<?> clsServiceManagerNative =
				Class.forName(SERVICE_MANAGER_NATIVE_CLASS) ;

		Binder bindServiceManager = new Binder() ;
		bindServiceManager.attachInterface( null, "foo" ) ;
		Method mthGetService = // ServiceManager.getService( String sServiceName )
				clsServiceManager.getMethod( "getService", String.class ) ;
		Method mthAsInterface = // ServiceManagerNative.asInterface( Class<?> clsService )
				clsServiceManagerNative.getMethod( "asInterface", IBinder.class ) ;
		m_mgrServices = mthAsInterface.invoke( null, bindServiceManager ) ;
		IBinder bindTelephonyService =
				((IBinder)( mthGetService.invoke( m_mgrServices, "phone" ) )) ;
		Method mthGetITelephony = // ITelephony.Stub.asInterface( Class<?> clsBinder )
				clsStub.getMethod( "asInterface", IBinder.class ) ;

		m_mgrTelephony = mthGetITelephony.invoke( null, bindTelephonyService ) ;

//		this.dumpDiscoveredMethods() ;    // Comment this out for normal builds.

		return this ;
	}

	/**
	 * Used to discover the actual definition of the {@code ITelephony}
	 * interface as visible to the current app.
	 * @return the dumped methods (since 0.2.1/#53)
	 * @since zerobandwidth-net/android 0.0.6 (#17)
	 */
	@SuppressWarnings( "unused" )               // For diagnostic purposes only.
	protected String dumpDiscoveredMethods()
	{
		Method[] amthITelephony = m_clsITelephony.getMethods() ;
		StringBuilder sb = new StringBuilder() ;
		sb.append( "*** DEBUG *** Methods discovered in ITelephony:" ) ;
		for( Method mth : amthITelephony )
			sb.append( "\n" ).append( mth.toString() ) ;
		String sDump = sb.toString() ;
		Log.d( LOG_TAG, sDump ) ;
		return sDump ;
	}

	/**
	 * Standard method of reflectively invoking a method of the telephony
	 * manager.
	 * @param sMethod the name of the underlying method being invoked
	 * @param aclsTypes an array of parameter types, corresponding to the
	 *                  parameters given in {@code aoParams}
	 *                  (since zerobandwidth-net/android 0.0.6 (#17))
	 * @param aoParams any parameters that would be passed to the underlying
	 *                method, corresponding to the types given in
	 *                {@code aclsTypes}
	 *                (since zerobandwidth-net/android 0.0.6 (#17))
	 * @return the return value of the underlying method
	 * @throws ControllerInvocationException if the invocation fails
	 */
	protected Object invoke( String sMethod, Class<?>[] aclsTypes, Object[] aoParams )
	throws ControllerInvocationException
	{
		try
		{
			return m_clsITelephony.getMethod( sMethod, aclsTypes )
						.invoke( m_mgrTelephony, aoParams ) ;
		}
		catch( Exception x )
		{ throw new ControllerInvocationException( sMethod, x ) ; }
	}

	/**
	 * Answers an incoming call.
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public void answerRingingCall()
	throws ControllerInvocationException
	{ this.invoke( "answerRingingCall", null, null ) ; }

	/**
	 * Answers an incoming call on behalf of a subscriber.
	 * @param id the subscriber ID
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public void answerRingingCallForSubscriber( int id )
	{
		this.invoke( "answerRingingCallForSubscriber",
				new Class<?>[] { Integer.class },
				new Integer[] { id }
			);
	}

	/**
	 * Dials the specified number and initiates an outbound call.
	 * @param pkgCaller the code package that is requesting the call
	 * @param sNumber the target phone number
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #placeCall} instead.
	 */
	public void call( String pkgCaller, String sNumber )
	throws ControllerInvocationException
	{
		this.invoke( "call",
				new Class<?>[] { String.class, String.class },
				new String[] { pkgCaller, sNumber }
			);
	}

	/**
	 * Dials the specified number, but does not call it.
	 * @param sNumber the target phone number
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public void dial( String sNumber )
	throws ControllerInvocationException
	{
		this.invoke( "dial",
				new Class<?>[] { String.class },
				new String[] { sNumber }
			);
	}

	/**
	 * Disables the data connection over the cell radio.
	 * @return (undocumented) indicates success?
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean disableDataConnectivity()
	throws ControllerInvocationException
	{
		return ((boolean)
				( this.invoke( "disableDataConnectivity", null, null ) )) ;
	}

	/**
	 * Enables the data connection over the cell radio.
	 * @return (undocumented) indicates success?
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean enableDataConnectivity()
	throws ControllerInvocationException
	{
		return ((boolean)
				( this.invoke( "enableDataConnectivity", null, null ) )) ;
	}

	/**
	 * Terminates a call in progress.
	 * @return an indication of whether the call actually hung up
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean endCall()
	throws ControllerInvocationException
	{ return ((boolean)( this.invoke( "endCall", null, null ) )) ; }

	/**
	 * Terminates a call in progress on behalf of a subscriber.
	 * @param id the subscriber ID
	 * @return an indication of whether the call actually hung up
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean endCallForSubscriber( int id )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "endCallForSubscriber",
				new Class<?>[] { Integer.class },
				new Integer[] { id }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates the state of the data connection.
	 * @return (undocumented)
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getDataState(Context)} instead.
	 */
	public int getDataState()
	throws ControllerInvocationException
	{ return ((int)( this.invoke( "getDataState", null, null ) )) ; }

	/**
	 * Indicates whether the cell radio supports data connectivity.
	 * @return duh, consult name of method
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean isDataConnectivityPossible()
	throws ControllerInvocationException
	{ return ((boolean)( this.invoke( "isDataConnectivityPossible", null, null ) )) ; }

	/**
	 * Indicates whether the phone is in "idle" state &mdash; that is, no
	 * outbound or inbound calls are in progress.
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the phone state is "idle"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isIdle(Context)}
	 *  instead.
	 */
	public boolean isIdle( String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isIdle",
				new Class<?>[] { String.class },
				new String[] { pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether the phone is in "idle" state &mdash; that is, no
	 * outbound or inbound calls are in progress.
	 * @param id the subscriber ID
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the phone state is "idle"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isIdle(Context)}
	 *  instead.
	 */
	public boolean isIdleForSubscriber( int id, String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isIdleForSubscriber",
				new Class<?>[] { Integer.class, String.class },
				new Object[] { id, pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether the phone is "off-hook" &mdash; that is, waiting for a
	 * call to complete, or fully engaged with an active call
	 * @param pkgCaller the code package that is requesting the function
	 * @return {@code true} if the phone state is "off-hook"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isOffhook(Context)}
	 *  instead.
	 */
	public boolean isOffhook( String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isOffhook",
				new Class<?>[] { String.class },
				new String[] { pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether the phone is "off-hook" &mdash; that is, waiting for a
	 * call to complete, or fully engaged with an active call
	 * @param id the subscriber ID
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the phone state is "off-hook"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isOffhook(Context)}
	 *  instead.
	 */
	public boolean isOffhookForSubscriber( int id, String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isOffhookForSubscriber",
				new Class<?>[] { Integer.class, String.class },
				new Object[] { id, pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether the cell radio is active.
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the cell radio is active
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean isRadioOn( String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isRadioOn",
				new Class<?>[] { String.class },
				new String[] { pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether the cell radio is active.
	 * @param id the subscriber ID
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the cell radio is active
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean isRadioOnForSubscriber( int id, String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isRadioOnForSubscriber",
				new Class<?>[] { Integer.class, String.class },
				new Object[] { id, pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether there is currently an incoming call that is causing
	 * either a ring tone or a call waiting tone.
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the phone state is "ringing"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isRinging(Context)}
	 *  instead.
	 */
	public boolean isRinging( String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isRinging",
				new Class<?>[] { String.class },
				new String[] { pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Indicates whether there is currently an incoming call that is causing
	 * either a ring tone or a call waiting tone.
	 * @param id the subscriber ID
	 * @param pkgCaller the code package making the inquiry
	 * @return {@code true} if the phone state is "ringing"
	 * @throws ControllerInvocationException if the invocation fails
	 * @deprecated Use {@link #getCallState} or {@link #isRinging(Context)}
	 *  instead.
	 */
	public boolean isRingingForSubscriber( int id, String pkgCaller )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "isRingingForSubscriber",
				new Class<?>[] { Integer.class, String.class },
				new Object[] { id, pkgCaller }
			);
		return (boolean)o ;
	}

	/**
	 * Sets the cell radio on or off.
	 * @param bEnabled {@code true} to enable; {@code false} to disable
	 * @return (undocumented)
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean setRadio( boolean bEnabled )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "setRadio",
				new Class<?>[] { Boolean.class },
				new Boolean[] { bEnabled }
			);
		return (boolean)o ;
	}

	/**
	 * Silences the ringer if an incoming call is ringing. Also silences the
	 * vibrator.
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public void silenceRinger()
	throws ControllerInvocationException
	{ this.invoke( "silenceRinger", null, null ) ; }

	/**
	 * Supply a PIN to unlock the phone's SIM card.
	 * @param sPIN the PIN to be checked
	 * @return indicates whether the operation succeeded
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean supplyPin( String sPIN )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "supplyPin",
				new Class<?>[] { String.class },
				new String[] { sPIN }
			);
		return (boolean)o ;
	}

	/**
	 * Supply a PIN to unlock the phone's SIM card.
	 * @param id the subscriber ID
	 * @param sPIN the PIN to be checked
	 * @return indicates whether the operation succeeded
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean supplyPinForSubscriber( int id, String sPIN )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "supplyPinForSubscriber",
				new Class<?>[] { Integer.class, String.class },
				new Object[] { id, sPIN }
			);
		return (boolean)o ;
	}

	/**
	 * Supply a PUK (Personal Unlocking Key) to unlock the SIM and set its PIN
	 * to a new value.
	 * @param sPUK the unlock key
	 * @param sPIN the new PIN to set
	 * @return indicates whether the operation succeeded
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean supplyPuk( String sPUK, String sPIN )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "supplyPuk",
				new Class<?>[] { String.class, String.class },
				new String[] { sPUK, sPIN }
			);
		return (boolean)o ;
	}

	/**
	 * Supply a PUK (Personal Unlocking Key) to unlock the SIM and set its PIN
	 * to a new value.
	 * @param id the subscriber ID
	 * @param sPUK the unlock key
	 * @param sPIN the new PIN to set
	 * @return indicates whether the operation succeeded
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public boolean supplyPukForSubscriber( int id, String sPUK, String sPIN )
	throws ControllerInvocationException
	{
		final Object o = this.invoke( "supplyPukForSubscriber",
				new Class<?>[] { Integer.class, String.class, String.class },
				new Object[] { id, sPUK, sPIN }
			);
		return (boolean)o ;
	}

	/**
	 * Toggles the state of the cell radio.
	 * @throws ControllerInvocationException if the invocation fails
	 */
	public void toggleRadio()
	throws ControllerInvocationException
	{ this.invoke( "toggleRadioOnOff", null, null ) ; }
}
