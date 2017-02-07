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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.zerobandwidth.android.lib.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class checks a specified list of "dangerous" permissions, forcing the
 * user to grant each one at runtime. The class would be used during the
 * {@link Activity#onCreate onCreate} and/or {@link Activity#onResume onResume}
 * methods of an app's main activity.
 *
 * <h3>Defining the List of Permissions</h3>
 *
 * <p>By default, the list of dangerous permissions is drawn from the string
 * array resource named by {@link R.array#asDangerousPermissionsRequired}. In
 * this library's definition, the array is empty; the app consuming this library
 * may override the resource with its own list.</p>
 *
 * <p>The consumer of this class may, alternatively, supply a different resource
 * ID to one of the longer constructors. This will <i>replace</i> the default
 * array resource as the definitive list of required permissions.</p>
 *
 * @since zerobandwidth-net/android 0.0.3 (#10)
 */
public class PermissionCheckpoint
implements ActivityCompat.OnRequestPermissionsResultCallback
{
	public static final String LOG_TAG =
			PermissionCheckpoint.class.getSimpleName() ;

	/**
	 * An identifier used in permission requests.
	 */
	protected static final int DANGER_REQUEST_CODE = 4 ;

	/**
	 * Static method allowing any class to check the momentary status of a
	 * permission.
	 * @param ctx the context in which to perform the check
	 * @param sPermission the qualified name of the permission to check
	 * @return {@code true} iff the permission has been granted to the app
	 */
	public static boolean checkPermission( Context ctx, String sPermission )
	{
		int zStatus = ContextCompat.checkSelfPermission( ctx, sPermission ) ;
		boolean bGranted = ( zStatus == PackageManager.PERMISSION_GRANTED ) ;
		Log.d( LOG_TAG, (new StringBuilder())
				.append( "Permission [" )
				.append( sPermission )
				.append(( bGranted ? "] GRANTED." : "] DENIED" ))
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
	 * Given the activity which requested the instance, this member represents
	 * the context provided by that activity.
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

	/**
	 * The dialog that announces that the "write settings" permission needs to
	 * be granted. Confirmation of this dialog will take the user to the Android
	 * OS screen where the setting can be administered.
	 */
	protected AlertDialog m_diaAnnounce = null ;

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
		m_ctx = ( this.isContextAppCompat() ?
					m_actCompatContext.getBaseContext() :
					m_actContext.getBaseContext() ) ;
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
	 * @return {@code true} if all permissions are granted.
	 */
	protected boolean hasGrantedAll()
	{
		return( m_mapGranted.size() == 0             // No permissions to grant.
		     || ! m_mapGranted.containsValue(false) ) ;    // All marked "true".
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
				if( pair.getKey().equals("android.permission.WRITE_SETTINGS") )
					bWriteSettings = true ;
				else
					asRequired.add( pair.getKey() ) ;
			}
		}
		if( asRequired.size() > 0 )
			this.promptForOtherDangers( asRequired ) ;
		else if( bWriteSettings && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
			this.promptForWriteSettingsPermission() ;

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
			m_diaAnnounce = new AlertDialog.Builder( m_ctx )
				.setTitle( R.string.diaAnnounce_title )
				.setMessage( R.string.diaAnnounce_message )
				.setCancelable( false )
				.setPositiveButton( android.R.string.ok,
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick( DialogInterface dia, int zButtonID )
						{
							Intent sig = new Intent(
									Settings.ACTION_MANAGE_WRITE_SETTINGS ) ;
							sig.setData( Uri.parse( "package:" + m_ctx.getPackageName() ) ) ;
							m_ctx.startActivity(sig) ;
						}
					})
				.create()
				;
			m_diaAnnounce.show() ;
		}
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
	 * @param azResults an array of result indicators
	 */
	@Override
	public void onRequestPermissionsResult( int zCode,
        @NonNull String[] asDangers, @NonNull int[] azResults )
	{
		m_bDialogVisible = false ;
		this.performChecks() ;    // Keep insisting until everything is granted.
	}
}
