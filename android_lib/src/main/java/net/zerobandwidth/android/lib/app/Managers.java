package net.zerobandwidth.android.lib.app;

import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.job.JobScheduler;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.RestrictionsManager;
import android.content.pm.LauncherApps;
import android.hardware.ConsumerIrManager;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.media.midi.MidiManager;
import android.media.projection.MediaProjectionManager;
import android.media.session.MediaSessionManager;
import android.media.tv.TvInputManager;
import android.net.ConnectivityManager;
import android.net.nsd.NsdManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.DropBoxManager;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.print.PrintManager;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.TextServicesManager;

import java.util.HashMap;

/**
 * Provides instances of Android OS managers.
 *
 * This class is largely irrelevant after Android API 23, which adds the new
 * {@code getSystemService(Class)} method to the {@link Context} class.
 * System service classes added after Android API 23 are not supported by this
 * class; if you are using a system service added after API 23, then you should
 * consider this class deprecated and use the new method in {@code Context}
 * instead.
 *
 * For backward compatibility from Android API 14 onward, the class provides
 * constant values for all static manager tags that were added to
 * {@code Context} after API 14.
 *
 * @since zerobandwidth-net/android 0.1.3 (#29)
 */
public class Managers
{
	protected static final String LOG_TAG = Managers.class.getSimpleName() ;

/// Android API 16 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.INPUT_SERVICE}
	 * (added in Android API 16)
	 */
	public static final String INPUT_SERVICE = "input" ;

	/**
	 * Backward-compatibility constant for {@code Context.MEDIA_ROUTER_SERVICE}
	 * (added in Android API 16)
	 */
	public static final String MEDIA_ROUTER_SERVICE = "media_router" ;

	/**
	 * Backward-compatibility constant for {@code Context.NSD_SERVICE}
	 * (added in Android API 16)
	 */
	public static final String NSD_SERVICE = "servicediscovery" ;

/// Android API 17 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.DISPLAY_SERVICE}
	 * (added in Android API 17)
	 */
	public static final String DISPLAY_SERVICE = "display" ;

	/**
	 * Backward-compatibility constant for {@code Context.USER_SERVICE}
	 * (added in Android API 17)
	 */
	public static final String USER_SERVICE = "user" ;

/// Android API 18 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.BLUETOOTH_SERVICE}
	 * (added in Android API 18)
	 */
	public static final String BLUETOOTH_SERVICE = "bluetooth" ;

/// Android API 19 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.APP_OPS_SERVICE}
	 * (added in Android API 19)
	 */
	public static final String APP_OPS_SERVICE = "appops" ;

	/**
	 * Backward-compatibility constant for {@code Context.CAPTIONING_SERVICE}
	 * (added in Android API 19)
	 */
	public static final String CAPTIONING_SERVICE = "captioning" ;

	/**
	 * Backward-compatibility constant for {@code Context.CONSUMER_IR_SERVICE}
	 * (added in Android API 19)
	 */
	public static final String CONSUMER_IR_SERVICE = "consumer_ir" ;

	/**
	 * Backward-compatibility constant for {@code Context.PRINT_SERVICE}
	 * (added in Android API 19)
	 */
	public static final String PRINT_SERVICE = "print" ;

/// Android API 21 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.APPWIDGET_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String APPWIDGET_SERVICE = "appwidget" ;

	/**
	 * Backward-compatibility constant for {@code Context.BATTERY_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String BATTERY_SERVICE = "batterymanager" ;

	/**
	 * Backward-compatibility constant for {@code Context.CAMERA_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String CAMERA_SERVICE = "camera" ;

	/**
	 * Backward-compatibility constant for {@code Context.JOB_SCHEDULER_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String JOB_SCHEDULER_SERVICE = "taskmanager" ;

	/**
	 * Backward-compatibility constant for {@code Context.LAUNCHER_APPS_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String LAUNCHER_APPS_SERVICE = "launcherapps" ;

	/**
	 * Backward-compatibility constant for {@code Context.MEDIA_PROJECTION_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String MEDIA_PROJECTION_SERVICE = "media_projection" ;

	/**
	 * Backward-compatibility constant for {@code Context.MEDIA_SESSION_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String MEDIA_SESSION_SERVICE = "media_session" ;

	/**
	 * Backward-compatibility constant for {@code Context.RESTRICTIONS_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String RESTRICTIONS_SERVICE = "restrictions" ;

	/**
	 * Backward-compatibility constant for {@code Context.TELECOM_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String TELECOM_SERVICE = "telecom" ;

	/**
	 * Backward-compatibility constant for {@code Context.TV_INPUT_SERVICE}
	 * (added in Android API 21)
	 */
	public static final String TV_INPUT_SERVICE = "tv_input" ;

/// Android API 22 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.TELECOM_SUBSCRIPTION_SERVICE}
	 * (added in Android API 22)
	 */
	public static final String TELEPHONY_SUBSCRIPTION_SERVICE =
				"telephony_subscription_service" ;

	/**
	 * Backward-compatibility constant for {@code Context.USAGE_STATS_SERVICE}
	 * (added in Android API 22)
	 */
	public static final String USAGE_STATS_SERVICE = "usagestats" ;

/// Android API 23 Constants ///////////////////////////////////////////////////

	/**
	 * Backward-compatibility constant for {@code Context.CARRIER_CONFIG_SERVICE}
	 * (added in Android API 23)
	 */
	public static final String CARRIER_CONFIG_SERVICE = "carrier_config" ;

	/**
	 * Backward-compatibility constant for {@code Context.FINGERPRINT_SERVICE}
	 * (added in Android API 23)
	 */
	public static final String FINGERPRINT_SERVICE = "fingerprint" ;

	/**
	 * Backward-compatibility constant for {@code Context.MIDI_SERVICE}
	 * (added in Android API 23)
	 */
	public static final String MIDI_SERVICE = "midi" ;

	/**
	 * Backward-compatibility constant for {@code Context.NETWORK_STATS_SERVICE}
	 * (added in Android API 23)
	 */
	public static final String NETWORK_STATS_SERVICE = "netstats" ;

/// And now, the interesting bits //////////////////////////////////////////////

	/**
	 * Defines a reverse mapping from class definitions to the constants by
	 * which they are identified in {@link Context}. Where those constants are
	 * not defined until later API versions, the map instead uses one of the
	 * compatibility constants defined in this class.
	 *
	 * <h3>Documentation Conflicts</h3>
	 *
	 * The following errors in the Android API documentation for {@code Context}
	 * were observed during unit testing.
	 *
	 * <table>
	 *     <thead>
	 *         <tr>
	 *             <th>Context Token</th>
	 *             <th>Documented Class</th>
	 *             <th>Actual Class</th>
	 *             <th>Resolution</th>
	 *         </tr>
	 *     </thead>
	 *     <tbody>
	 *         <tr>
	 *             <td>{@link Context#SENSOR_SERVICE}</td>
	 *             <td>{@link SensorManager}</td>
	 *             <td>{@code android.hardware.SystemSensorManager}</td>
	 *             <td>The actual class is assignable.</td>
	 *         </tr>
	 *         <tr>
	 *             <td>{@link Context#WALLPAPER_SERVICE}</td>
	 *             <td>{@link android.service.wallpaper.WallpaperService}</td>
	 *             <td>{@link WallpaperManager}</td>
	 *             <td>
	 *                 The returned class is not assignable; the map uses
	 *                 {@code WallpaperManager} <b>instead of</b>
	 *                 {@code WallpaperService}.
	 *             </td>
	 *         </tr>
	 *     </tbody>
	 * </table>
	 *
	 * <b>NOTE:</b> The official Android documentation appears to be incorrect
	 * for the {@link Context#WALLPAPER_SERVICE} tag. This does not return an
	 * instance of {@code WallpaperService}; instead, it returns a
	 * {@link WallpaperManager}.
	 *
	 * @see Context
	 */
	public static HashMap<Class<?>,String> REVERSE_MAP ;
	static // initializer for REVERSE_MAP
	{
		REVERSE_MAP = new HashMap<>() ;
		int nAPI = 14 ;
		// Start with managers known to exist in and before API 14.
		// These entries MUST be arranged in ascending order of API introduction
		// and SHOULD be arranged alphabetically per API version.
		REVERSE_MAP.put( AccessibilityManager.class, Context.ACCESSIBILITY_SERVICE ) ;
		REVERSE_MAP.put( AccountManager.class, Context.ACCOUNT_SERVICE ) ;
		REVERSE_MAP.put( ActivityManager.class, Context.ACTIVITY_SERVICE ) ;
		REVERSE_MAP.put( AlarmManager.class, Context.ALARM_SERVICE ) ;
		REVERSE_MAP.put( AudioManager.class, Context.AUDIO_SERVICE ) ;
		REVERSE_MAP.put( ClipboardManager.class, Context.CLIPBOARD_SERVICE ) ;
		REVERSE_MAP.put( ConnectivityManager.class, Context.CONNECTIVITY_SERVICE ) ;
		REVERSE_MAP.put( DevicePolicyManager.class, Context.DEVICE_POLICY_SERVICE ) ;
		REVERSE_MAP.put( DownloadManager.class, Context.DOWNLOAD_SERVICE ) ;
		REVERSE_MAP.put( DropBoxManager.class, Context.DROPBOX_SERVICE ) ;
		REVERSE_MAP.put( InputMethodManager.class, Context.INPUT_METHOD_SERVICE ) ;
		REVERSE_MAP.put( KeyguardManager.class, Context.KEYGUARD_SERVICE ) ;
		REVERSE_MAP.put( LayoutInflater.class, Context.LAYOUT_INFLATER_SERVICE ) ;
		REVERSE_MAP.put( LocationManager.class, Context.LOCATION_SERVICE ) ;
		REVERSE_MAP.put( NfcManager.class, Context.NFC_SERVICE ) ;
		REVERSE_MAP.put( NotificationManager.class, Context.NOTIFICATION_SERVICE ) ;
		REVERSE_MAP.put( PowerManager.class, Context.POWER_SERVICE ) ;
		REVERSE_MAP.put( SearchManager.class, Context.SEARCH_SERVICE ) ;
		REVERSE_MAP.put( SensorManager.class, Context.SENSOR_SERVICE ) ;
		REVERSE_MAP.put( StorageManager.class, Context.STORAGE_SERVICE ) ;
		REVERSE_MAP.put( TelephonyManager.class, Context.TELEPHONY_SERVICE ) ;
		REVERSE_MAP.put( TextServicesManager.class, Context.TEXT_SERVICES_MANAGER_SERVICE ) ;
		REVERSE_MAP.put( UiModeManager.class, Context.UI_MODE_SERVICE ) ;
		REVERSE_MAP.put( UsbManager.class, Context.USB_SERVICE ) ;
		REVERSE_MAP.put( Vibrator.class, Context.VIBRATOR_SERVICE ) ;
		REVERSE_MAP.put( WallpaperManager.class, Context.WALLPAPER_SERVICE ) ;
		REVERSE_MAP.put( WifiManager.class, Context.WIFI_SERVICE ) ;
		REVERSE_MAP.put( WifiP2pManager.class, Context.WIFI_P2P_SERVICE ) ;
		REVERSE_MAP.put( WindowManager.class, Context.WINDOW_SERVICE ) ;
		try
		{ // As long as these are in order, we get all the ones we can.
			++nAPI ;// API 15
			++nAPI ;// API 16
			REVERSE_MAP.put( InputManager.class, INPUT_SERVICE ) ;
			REVERSE_MAP.put( MediaRouter.class, MEDIA_ROUTER_SERVICE ) ;
			REVERSE_MAP.put( NsdManager.class, NSD_SERVICE ) ;
			++nAPI ;// API 17
			REVERSE_MAP.put( DisplayManager.class, DISPLAY_SERVICE ) ;
			REVERSE_MAP.put( UserManager.class, USER_SERVICE ) ;
			++nAPI ;// API 18
			REVERSE_MAP.put( BluetoothManager.class, BLUETOOTH_SERVICE ) ;
			++nAPI ;// API 19
			REVERSE_MAP.put( AppOpsManager.class, APP_OPS_SERVICE ) ;
			REVERSE_MAP.put( CaptioningManager.class, CAPTIONING_SERVICE ) ;
			REVERSE_MAP.put( ConsumerIrManager.class, CONSUMER_IR_SERVICE ) ;
			REVERSE_MAP.put( PrintManager.class, PRINT_SERVICE ) ;
			++nAPI ;// API 20
			++nAPI ;// API 21
			REVERSE_MAP.put( AppWidgetManager.class, APPWIDGET_SERVICE ) ;
			REVERSE_MAP.put( BatteryManager.class, BATTERY_SERVICE ) ;
			REVERSE_MAP.put( CameraManager.class, CAMERA_SERVICE ) ;
			REVERSE_MAP.put( JobScheduler.class, JOB_SCHEDULER_SERVICE ) ;
			REVERSE_MAP.put( LauncherApps.class, LAUNCHER_APPS_SERVICE ) ;
			REVERSE_MAP.put( MediaProjectionManager.class, MEDIA_PROJECTION_SERVICE ) ;
			REVERSE_MAP.put( MediaSessionManager.class, MEDIA_SESSION_SERVICE ) ;
			REVERSE_MAP.put( RestrictionsManager.class, RESTRICTIONS_SERVICE ) ;
			REVERSE_MAP.put( TelecomManager.class, TELECOM_SERVICE ) ;
			REVERSE_MAP.put( TvInputManager.class, TV_INPUT_SERVICE ) ;
			++nAPI ;// API 22
			REVERSE_MAP.put( SubscriptionManager.class, TELEPHONY_SUBSCRIPTION_SERVICE ) ;
			REVERSE_MAP.put( UsageStatsManager.class, USAGE_STATS_SERVICE ) ;
			++nAPI ;// API 23
			REVERSE_MAP.put( CarrierConfigManager.class, CARRIER_CONFIG_SERVICE ) ;
			REVERSE_MAP.put( FingerprintManager.class, FINGERPRINT_SERVICE ) ;
			REVERSE_MAP.put( MidiManager.class, MIDI_SERVICE ) ;
			REVERSE_MAP.put( NetworkStatsManager.class, NETWORK_STATS_SERVICE ) ;
		}
		catch( NoClassDefFoundError x )
		{
			Log.i( LOG_TAG, (new StringBuilder())
					.append( "Stopped loading manager definitions at API " )
					.append( nAPI )
					.toString()
				);
		}
	}

	/**
	 * Obtains an instance of the Android OS service whose class is given.
	 * The return value of this method is already an instance of the type
	 * specified in the parameter; there is no need for additional typecasting
	 * after the instance is returned to the caller.
	 * This is exactly how the {@code getSystemService(Class)} method works in
	 * the API 23+ version of {@link Context}.
	 * @param ctx the context in which to obtain the service
	 * @param cls the class to be obtained, as a parameter
	 * @param <MGR> the class to be obtained, as a generic return type
	 * @return an instance of the specified class
	 */
	@SuppressWarnings({ "unchecked", "WrongConstant" })
	public static <MGR> MGR get( Context ctx, Class<MGR> cls )
	{
		try
		{
			if( REVERSE_MAP.containsKey(cls) )
				return ((MGR)( ctx.getSystemService( REVERSE_MAP.get(cls) ) )) ;
			else
			{
				Log.w( LOG_TAG, (new StringBuilder())
						.append( "Cannot obtain instance of class [" )
						.append( cls.getCanonicalName() )
						.append( "] not found in the reverse map." )
						.toString()
					);
			}
		}
		catch( Exception x )
		{ Log.e( LOG_TAG, "Exception thrown while obtaining a manager.", x ) ; }

		return null ;
	}
}
