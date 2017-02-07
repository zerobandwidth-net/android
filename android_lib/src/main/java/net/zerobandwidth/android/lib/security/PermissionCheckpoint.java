package net.zerobandwidth.android.lib.security;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.zerobandwidth.android.lib.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class checks a specified list of "dangerous" permissions, forcing the
 * user to grant each one at runtime.
 *
 * <h3>Using this Class</h3>
 *
 * <h4>Defining the List of Permissions</h4>
 *
 * <p>By default, the list of dangerous permissions is drawn from the string
 * array resource named by {@link R.array#asDangerousPermissionsRequired} in
 * this library. By default, the array is empty; the app consuming this library
 * may override the resource with its own values.</p>
 *
 * <p>The consumer of this class may, alternatively, supply a different resource
 * ID to one of the longer constructors. This will <i>replace</i> the default
 * array resource as the definitive list of required permissions.</p>
 *
 * <h4>Maintain a Persistent Instance in the Activity</h4>
 *
 * <p>The main activity of the app should construct an instance of this class in
 * its {@code onCreate} method, using itself as the operational context, and
 * optionally specifying an alternative list of permissions.</p>
 *
 * <pre>
 *     protected PermissionCheckpoint m_perms = null ;
 *
 *    {@literal @Override}
 *     protected void onCreate( Bundle bndlState )
 *     {
 *         super.onCreate(bndlState) ;
 *         this.setContentView( R.layout.whatever ) ;
 *         if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
 *             m_perms = new PermissionCheckpoint(this) ;
 *         // ...
 *     }
 * </pre>
 *
 * <h4>Refresh Permission State on Each {@code onResume}</h4>
 *
 * <p>The activity's {@code onResume} method must include a call to this class's
 * {@link #performChecks()} method, which re-evaluates the app's permissions
 * each time it is invoked.</p>
 *
 * <pre>
 *    {@literal @Override}
 *     protected void onResume()
 *     {
 *         super.onResume() ;
 *         if( m_perms != null ) m_perms.performChecks() ;
 *     }
 * </pre>
 *
 * <h4>Implement the Permission Request Callback</h4>
 *
 * <p>Finally, the activity must provide a callback for the Android OS in
 * response to a permission request.</p>
 *
 * <h5>{@link Activity}</h5>
 *
 * <p>Modern activities include their own callback method, which must be
 * overridden in your activity.</p>
 *
 * <pre>
 *     public class MyActivity extends Activity
 *     {
 *         protected PermissionCheckpoint m_perms = null ; // init in onCreate()
 *
 *         // etc
 *
 *        {@literal @RequiresApi( api = Build.VERSION_CODES.M )}
 *        {@literal @Override}
 *         public void onRequestPermissionsResult( int zRequestCode,
 *            {@literal @NonNull} String[] asPermissions,{@literal @NonNull} int[] azStatus )
 *         {
 *             super.onRequestPermissionsResult( zRequestCode, asPermissions, azStatus ) ;
 *             if( m_perms != null )
 *                 m_perms.processRequestResults( zRequestCode, asPermissions, azStatus ) ;
 *         }
 *     }
 * </pre>
 *
 * <h5>{@link AppCompatActivity}</h5>
 *
 * <p>An activity from the compatibility library should implement the
 * {@link ActivityCompat.OnRequestPermissionsResultCallback} interface.</p>
 *
 * <pre>
 *     public class MyCompatActivity extends AppCompatActivity
 *     implements ActivityCompat.OnRequestPermissionsResultCallback
 *     {
 *         protected PermissionCheckpoint m_perms = null ; // init in onCreate()
 *
 *         // etc
 *
 *        {@literal @RequiresApi( api = Build.VERSION_CODES.M )}
 *        {@literal @Override}
 *         public void onRequestPermissionsResult( int zRequestCode,
 *            {@literal @NonNull} String[] asPermissions,{@literal @NonNull} int[] azStatus )
 *         {
 *             if( m_perms != null )
 *                 m_perms.processRequestResults( zRequestCode, asPermissions, azStatus ) ;
 *         }
 *     }
 * </pre>
 *
 * <h3>Acknowledgements</h3>
 *
 * Special thanks to {@code @dapayne1} for doing the heavy lifting on the
 * research for Android 6 permissions management, and providing the original
 * reference implementation from which this class was adapted.
 *
 * @since zerobandwidth-net/android 0.0.3 (#10)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class PermissionCheckpoint
{
	public static final String LOG_TAG =
			PermissionCheckpoint.class.getSimpleName() ;

	/**
	 * An identifier used in permission requests.
	 */
	protected static final int DANGER_REQUEST_CODE = 4 ;

	protected static final String PERMISSION_WRITE_SETTINGS =
			"android.permission.WRITE_SETTINGS" ;

	/**
	 * Static method allowing any class to check the momentary status of a
	 * permission.
	 * @param ctx the context in which to perform the check
	 * @param sPermission the qualified name of the permission to check
	 * @return {@code true} iff the permission has been granted to the app
	 */
	public static boolean checkPermission( Context ctx, String sPermission )
	{
		boolean bGranted ;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
		 && PERMISSION_WRITE_SETTINGS.equals(sPermission) )
		{
			bGranted = Settings.System.canWrite( ctx ) ;
		}
		else
		{
			final int zStatus =
					ContextCompat.checkSelfPermission( ctx, sPermission ) ;
			bGranted = ( zStatus == PackageManager.PERMISSION_GRANTED ) ;
		}
		Log.d( LOG_TAG, (new StringBuilder())
				.append( "Permission [" )
				.append( sPermission )
				.append(( bGranted ? "]   GRANTED" : "]   DENIED" ))
				.toString()
			);
		return bGranted ;
	}

	/**
	 * An {@link AppCompatActivity} which provides the context from which
	 * resources are resolved and additional dialogs and activities may be
	 * launched. Only one of {@code m_actCompatContext} and
	 * {@link #m_actContext} should be set for any given instance.
	 */
	protected AppCompatActivity m_actCompatContext = null ;

	/**
	 * An {@link Activity} which provides the context from which resources
	 * are resolved and additional dialogs and activities may be launched.
	 * Only one of {@link #m_actCompatContext} and {@code m_actContext} should
	 * be set for any given instance.
	 */
	protected Activity m_actContext = null ;

	/**
	 * Since both {@link Activity} and {@link AppCompatActivity} are
	 * descendants of {@link Context}, this field allows whichever one we end up
	 * with to represent the operational context of the instance, where we can
	 * use {@code Context} for resource resolution, etc.
	 */
	protected Context m_ctx = null ;

	/**
	 * The default string array resource in which dangerous permissions are
	 * defined. Apps that use this library may choose to override this
	 * {@link R.array#asDangerousPermissionsRequired} resource, or have their
	 * own resource which is passed into the constructor.
	 */
	protected static final int DEFAULT_DANGER_LIST_RESOURCE =
			R.array.asDangerousPermissionsRequired ;
	/**
	 * The resource ID of the string array that defines the list of dangerous
	 * permissions that should be requested. This defaults to the value of
	 * {@link #DEFAULT_DANGER_LIST_RESOURCE}, which is
	 * {@link R.array#asDangerousPermissionsRequired}.
	 */
	protected int m_resDangerList = DEFAULT_DANGER_LIST_RESOURCE ;

	/**
	 * Tracks the list of "dangerous" permissions that need to be granted to the
	 * app, and the current status of each.
	 */
	protected HashMap<String,Boolean> m_mapGranted = null ;

	/** Indicates that any permission dialog is currently being displayed. */
	protected boolean m_bDialogVisible = false ;

	/** Indicates the "build flavor" used to build the app, if any. */
	protected String m_sFlavor = null ;

	/**
	 * Initializes the instance with an {@link Activity} as the context.
	 * The default string array resource,
	 * {@link R.array#asDangerousPermissionsRequired}, will be used to define
	 * the list of dangerous permissions required by the app.
	 * @param act the activity which provides operational context
	 */
	public PermissionCheckpoint( Activity act )
	{ this( act, DEFAULT_DANGER_LIST_RESOURCE ) ; }

	/**
	 * Initializes the instance with an {@link Activity} as the context.
	 * @param act the activity which provides operational context
	 * @param resDangerList the resource ID for the list of dangerous
	 *                      permissions required by the app
	 */
	public PermissionCheckpoint( Activity act, int resDangerList )
	{
		m_actCompatContext = null ;
		m_actContext = act ;
		this.init( resDangerList ) ;
	}

	/**
	 * Initializes the instance with an {@link AppCompatActivity} as the context.
	 * The default string array resource,
	 * {@link R.array#asDangerousPermissionsRequired}, will be used to define
	 * the list of dangerous permissions required by the app.
	 * @param act the activity which provides operational context
	 */
	public PermissionCheckpoint( AppCompatActivity act )
	{ this( act, DEFAULT_DANGER_LIST_RESOURCE ) ; }

	/**
	 * Initializes the instance with an {@link AppCompatActivity} as the context.
	 * @param act the activity which provides operational context
	 * @param resDangerList the resource ID for the list of dangerous
	 *                      permissions required by the app
	 */
	public PermissionCheckpoint( AppCompatActivity act, int resDangerList )
	{
		m_actCompatContext = act ;
		m_actContext = null ;
		this.init( resDangerList ) ;
	}

	/**
	 * Initializes various fields within the instance, once context has been
	 * established.
	 * @param resDangerList the resource ID for the list of dangerous
	 *                      permissions required by the app
	 * @return (fluid)
	 */
	protected PermissionCheckpoint init( int resDangerList )
	{
		m_resDangerList = resDangerList ;
		m_ctx = ( this.isContextAppCompat() ? m_actCompatContext : m_actContext ) ;
		String[] asDangers =
				m_ctx.getResources().getStringArray( m_resDangerList ) ;
		m_mapGranted = new HashMap<>( asDangers.length ) ;
		for( String sDanger : asDangers )
		{ // Add the status of that permission to the object's map.
			m_mapGranted.put( sDanger, checkPermission( m_ctx, sDanger ) ) ;
		}
		return this ;
	}

	/**
	 * Indicates whether the instance's operational context is an
	 * {@link AppCompatActivity}.
	 * @return {@code true} if an {@code AppCompatActivity} was used as the
	 *  initial context for the instance
	 */
	protected boolean isContextAppCompat()
	{ return ( m_actCompatContext != null && m_actContext == null ) ; }

	/**
	 * This method performs all the checks necessary to determine whether the
	 * app has all of its required permissions. If any permission is still
	 * denied, then the method will trigger dialogs challenging the user to
	 * grant those permissions to the app.
	 * @return (fluid)
	 */
	public PermissionCheckpoint performChecks()
	{
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.M )
			return this ; // trivially

		if( ! this.hasGrantedAll() )
			this.promptForPermissions() ;

		return this ;
	}

	/**
	 * Evaluates whether all required "dangerous" permissions have been granted.
	 *
	 * For most permissions, we have cached the state via
	 * {@link #processRequestResults}, but for the {@code WRITE_SETTINGS}
	 * permission, we have to explicitly re-check it, because there's no way to
	 * listen for a state-change event from the Android OS's application
	 * manager.
	 *
	 * @return {@code true} if all permissions are granted.
	 */
	protected boolean hasGrantedAll()
	{
//		this.logPermissionState() ;        // Comment out this line for release!
		if( m_mapGranted.size() == 0 ) return true ; // No permissions to grant.
		if( m_mapGranted.containsKey( PERMISSION_WRITE_SETTINGS ) )
		{ // Explicitly re-check whether we've been granted WRITE_SETTINGS.
			m_mapGranted.put( PERMISSION_WRITE_SETTINGS,
					checkPermission( m_ctx, PERMISSION_WRITE_SETTINGS ) ) ;
		}
		return( ! m_mapGranted.containsValue(false) ) ;    // All marked "true".
	}

	/**
	 * (debug only) Dumps the current state of the permission map to the Android
	 * logs.
	 * @return (fluid)
	 * @see #hasGrantedAll()
	 */
	protected PermissionCheckpoint logPermissionState()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append( "DEBUG Current permission state:\n" ) ;
		for( HashMap.Entry<String,Boolean> pair : m_mapGranted.entrySet() )
		{
			sb.append( "\t\t" )
			  .append( pair.getKey() )
			  .append( "\t" )
			  .append( pair.getValue() )
			  .append( "\n" )
			  ;
		}
		Log.d( LOG_TAG, sb.toString() ) ;
		return this ;
	}

	/**
	 * Prompts the user to accept all "dangerous" permission requests.
	 *
	 * The method tries to ask for just regularly-dangerous permissions first,
	 * then asks for the "write settings" permission if all others have been
	 * granted.
	 *
	 * @return (fluid)
	 */
	protected PermissionCheckpoint promptForPermissions()
	{
		ArrayList<String> asRequired = new ArrayList<>() ;
		boolean bWriteSettings = false ;
		for( HashMap.Entry<String,Boolean> pair : m_mapGranted.entrySet() )
		{
			if( ! pair.getValue() )
			{ // Found a permission that has not yet been granted.
				if( pair.getKey().equals( PERMISSION_WRITE_SETTINGS ) )
				{
					Log.w( LOG_TAG,
							"App needs special WRITE_SETTINGS permission." ) ;
					bWriteSettings = true;
				}
				else
				{
					Log.w( LOG_TAG, (new StringBuilder())
							.append( "App needs " )
							.append( pair.getKey() )
							.append( " permission." )
							.toString()
						);
					asRequired.add( pair.getKey() );
				}
			}
		}
		if( asRequired.size() > 0 )
			this.promptForOtherDangers( asRequired ) ;
		else if( bWriteSettings && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			this.promptForWriteSettingsPermission();

		return this ;
	}

	/**
	 * Prompts the user to enable the "write settings" permission, which can be
	 * administered only on a special Android OS dialog.
	 * @return (fluid)
	 */
	@RequiresApi( api = Build.VERSION_CODES.M )
	protected PermissionCheckpoint promptForWriteSettingsPermission()
	{
		if( ! m_bDialogVisible )
		{
			final WriteSettingsDialogClickListener listener =
					new WriteSettingsDialogClickListener() ;
			if( this.isContextAppCompat() )
				this.promptWithAppCompatDialog( listener ) ;
			else
				this.promptWithModernDialog( listener ) ;
		}
		return this ;
	}

	/**
	 * Defines a click listener for the dialog that prompts the user to grant
	 * the special {@code WRITE_SETTINGS} permission.
	 * @see PermissionCheckpoint#promptWithModernDialog
	 * @see PermissionCheckpoint#promptWithAppCompatDialog
	 * @since zerobandwidth-net/android 0.0.3 (#10)
	 */
	protected class WriteSettingsDialogClickListener
	implements DialogInterface.OnClickListener
	{
		/** An alert from the modern library. */
		public android.app.AlertDialog m_diaModernParent = null ;

		/** An alert dialog from the compatibility library. */
		public android.support.v7.app.AlertDialog m_diaCompatParent = null ;

		/** Sets the parent dialog from the modern library. */
		public WriteSettingsDialogClickListener setParent( android.app.AlertDialog dia )
		{ m_diaModernParent = dia ; m_diaCompatParent = null ; return this ; }

		/** Sets the parent dialog from the compatibility library. */
		public WriteSettingsDialogClickListener setParent( android.support.v7.app.AlertDialog dia )
		{ m_diaModernParent = null ; m_diaCompatParent = dia ; return this ; }

		@RequiresApi( api = Build.VERSION_CODES.M )
		@Override
		public void onClick( DialogInterface dia, int zButtonID )
		{
			Intent sig = new Intent( Settings.ACTION_MANAGE_WRITE_SETTINGS ) ;
			sig.setData( Uri.parse( "package:" + m_ctx.getPackageName() ) ) ;
			m_ctx.startActivity( sig ) ;
			if( m_diaModernParent != null ) m_diaModernParent.dismiss() ;
			else if( m_diaCompatParent != null ) m_diaCompatParent.dismiss() ;
		}
	}

	/**
	 * If our parent activity is an {@link AppCompatActivity}, then we need to
	 * use the corresponding {@code AlertDialog} flavor to prompt for the
	 * permission.
	 * @param listener the click listener for the "OK" button
	 * @return (fluid)
	 * @see #promptForWriteSettingsPermission
	 */
	protected PermissionCheckpoint promptWithAppCompatDialog(
			WriteSettingsDialogClickListener listener )
	{
		final android.support.v7.app.AlertDialog diaAnnounce =
			new android.support.v7.app.AlertDialog.Builder( m_ctx )
				.setTitle( R.string.diaAnnounce_title )
				.setMessage( R.string.diaAnnounce_message )
				.setCancelable( false )
				.setPositiveButton( android.R.string.ok, listener )
				.create()
				;
		listener.setParent( diaAnnounce ) ;
		diaAnnounce.show() ;
		return this ;
	}

	/**
	 * If our parent activity is an {@link Activity}, then we need to use the
	 * corresponding {@code AlertDialog} flavor to prompt for the permission.
	 * @param listener the click listener for the "OK" button
	 * @return (fluid)
	 * @see #promptForWriteSettingsPermission
	 */
	protected PermissionCheckpoint promptWithModernDialog(
			WriteSettingsDialogClickListener listener )
	{
		final android.app.AlertDialog diaAnnounce =
			new android.app.AlertDialog.Builder( m_ctx )
				.setTitle( R.string.diaAnnounce_title )
				.setMessage( R.string.diaAnnounce_message )
				.setCancelable( false )
				.setPositiveButton( android.R.string.ok, listener )
				.create()
				;
		listener.setParent( diaAnnounce ) ;
		diaAnnounce.show() ;
		return this ;
	}

	/**
	 * Prompts the user to enable "dangerous" permissions.
	 * @param asDangers a list of dangerous permissions
	 * @return (fluid)
	 */
	protected PermissionCheckpoint promptForOtherDangers( ArrayList<String> asDangers )
	{
		m_bDialogVisible = true ;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ! this.isContextAppCompat() )
		{
			m_actContext.requestPermissions(
					asDangers.toArray( new String[asDangers.size()] ),
					DANGER_REQUEST_CODE ) ;
		}
		else
		{
			ActivityCompat.requestPermissions( m_actCompatContext,
					asDangers.toArray( new String[asDangers.size()] ),
					DANGER_REQUEST_CODE ) ;
		}
		return this ;
	}

	/**
	 * Dialogs spawned by {@link ActivityCompat#requestPermissions} will call
	 * back to this method when the user chooses any option. We will use this
	 * callback to re-check the permission array and determine whether we need
	 * to try again.
	 * @param zCode the request code (we are interested in
	 *              {@link #DANGER_REQUEST_CODE})
	 * @param asDangers an array of permissions that were requested
	 * @param azStatus an array of result indicators
	 */
	@RequiresApi( api = Build.VERSION_CODES.M )
	public void processRequestResults( int zCode,
		@NonNull String[] asDangers, @NonNull int[] azStatus )
	{
		m_bDialogVisible = false ;
		Log.d( LOG_TAG, (new StringBuilder())
				.append( "Received new status for [" )
				.append( asDangers.length )
				.append(( asDangers.length == 1 ? "] permission." : "] permissions." ))
				.toString()
			);
		for( int i = 0 ; i < asDangers.length ; i++ )
		{ // Update our map of permissions to grant status.
			if( azStatus[i] == PackageManager.PERMISSION_GRANTED )
				m_mapGranted.put( asDangers[i], true ) ;
			else if( azStatus[i] == PackageManager.PERMISSION_DENIED )
				m_mapGranted.put( asDangers[i], false ) ;
		}
	}
}
