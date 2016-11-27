package net.zerobandwidth.android.lib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Surface;
import android.widget.LinearLayout;

/**
 * Provides utilities for dealing with common Android app tasks.
 * @since zerobandwidth-net/android 0.0.1 (#5)
 */
@SuppressWarnings("unused") // This is a library.
public class AppUtils
{
    /** A logging tag. */
    public static final String LOG_TAG = AppUtils.class.getSimpleName() ;

    /**
     * Replacement text to be used if the app's version name can't be found.
     * @see #getAppVersion
     * @see #getAppNameAndVersion
     */
    public static final String APP_VERSION_NOT_FOUND = "" ;

    /**
     * Returns the string containing the app's current version.
     * @param ctx a context in which to fetch a package manager
     * @return the app version as a string, or an empty string if not found
     */
    public static String getAppVersion( Context ctx )
    {
        try
        {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0 ) ;
            return ( info.versionName == null ?
                APP_VERSION_NOT_FOUND : info.versionName ) ;
        }
        catch( Exception x )
        {
            Log.w( LOG_TAG, "Can't discover app version." ) ;
            return "" ;
        }
    }

    /**
     * Returns a string containing the app's name and version (if found).
     * @param ctx a context in which to fetch resources
     * @return the app's name and version, formatted as specified by the default
     *  string resource
     * @see R.string#app_name_and_version_format
     */
    public static String getAppNameAndVersion( Context ctx )
    {
        return AppUtils.getAppNameAndVersion( ctx,
                R.string.app_name_and_version_format ) ;
    }

    /**
     * Returns a string containing the app's name and version (if found).
     * @param ctx a context in which to fetch resources
     * @param resFormat a custom string resource specifying the format in which
     *                  the string should be rendered; must contain two string
     *                  variables (one for the name, one for the version)
     * @return the app's name and version, formatted as specified by the
     *  supplied string resource
     */
    public static String getAppNameAndVersion( Context ctx, int resFormat )
    {
        final String sAppName = ctx.getString( R.string.app_name ) ;
        final String sAppVersion = AppUtils.getAppVersion(ctx) ;
        if( ! sAppVersion.equals( APP_VERSION_NOT_FOUND ) )
            return ctx.getString( resFormat, sAppName, sAppVersion ) ;
        else
            return sAppName ;
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param act the activity containing the layout to be adjusted
     * @param w the layout group to be adjusted
     * @see #setLinearOrientation(LinearLayout, int)
     */
    public static void setLinearOrientation( Activity act, LinearLayout w )
    {
        AppUtils.setLinearOrientation( w,
                act.getWindowManager().getDefaultDisplay().getRotation() ) ;
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param act the activity containing the layout to be adjusted
     * @param w the layout group to be adjusted
     * @see #setLinearOrientation(LinearLayout, int)
     */
    public static void setLinearOrientation( AppCompatActivity act, LinearLayout w )
    {
        AppUtils.setLinearOrientation( w,
                act.getWindowManager().getDefaultDisplay().getRotation() ) ;
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param act the activity containing the layout to be adjusted
     * @param w the layout group to be adjusted
     * @see #setLinearOrientation(LinearLayoutCompat, int)
     */
    public static void setLinearOrientation( Activity act, LinearLayoutCompat w )
    {
        AppUtils.setLinearOrientation( w,
                act.getWindowManager().getDefaultDisplay().getRotation() ) ;
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param act the activity containing the layout to be adjusted
     * @param w the layout group to be adjusted
     * @see #setLinearOrientation(LinearLayoutCompat, int)
     */
    public static void setLinearOrientation( AppCompatActivity act, LinearLayoutCompat w )
    {
        AppUtils.setLinearOrientation( w,
                act.getWindowManager().getDefaultDisplay().getRotation() ) ;
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param w the layout to be adjusted
     * @param nRotation the window's current rotation
     */
    public static void setLinearOrientation( LinearLayout w, int nRotation )
    {
        switch( nRotation )
        {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                w.setOrientation( LinearLayout.HORIZONTAL ) ;
                break ;
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
            default:
                w.setOrientation( LinearLayout.VERTICAL ) ;
                break ;
        }
    }

    /**
     * Adjusts a linear layout's orientation based on the orientation of the
     * screen.
     * The layout's long dimension will be matched to the long dimension of the
     * screen.
     * @param w the layout to be adjusted
     * @param nRotation the window's current rotation
     */
    public static void setLinearOrientation( LinearLayoutCompat w, int nRotation )
    {
        switch( nRotation )
        {
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                w.setOrientation( LinearLayoutCompat.HORIZONTAL ) ;
                break ;
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
            default:
                w.setOrientation( LinearLayoutCompat.VERTICAL ) ;
                break ;
        }
    }

    /**
     * Initializes a back button for the activity.
     *
     * In the activity's
     * {@link Activity#onOptionsItemSelected onOptionsItemSelected} method,
     * remember to set a handler for this button.
     *
     * <pre>
     *     public boolean onOptionsItemSelected( MenuItem item )
     *     {
     *         switch( item.getItemId() )
     *         {
     *             case android.R.item.home:
     *                 this.onBackPressed() ;
     *                 break ;
     *             // (other items...)
     *         }
     *     }
     * </pre>
     *
     * @param act an activity.
     * @see <a href="http://stackoverflow.com/a/33041114">StackOverflow answer #33041114</a>
     */
    @SuppressWarnings("ConstantConditions") // Exception is caught.
    public static void initBackButtonForActivity( Activity act )
    {
        try { act.getActionBar().setDisplayHomeAsUpEnabled(true) ; }
        catch( Exception x )
        { Log.d( LOG_TAG, "Could not initialize back button.", x ) ; }
    }

    /**
     * Initializes a back button for the activity.
     *
     * In the activity's
     * {@link AppCompatActivity#onOptionsItemSelected onOptionsItemSelected}
     * method, remember to set a handler for this button.
     *
     * <pre>
     *     public boolean onOptionsItemSelected( MenuItem item )
     *     {
     *         switch( item.getItemId() )
     *         {
     *             case android.R.item.home:
     *                 this.onBackPressed() ;
     *                 break ;
     *             // (other items...)
     *         }
     *     }
     * </pre>
     *
     * @param act an activity.
     * @see <a href="http://stackoverflow.com/a/33041114">StackOverflow answer #33041114</a>
     */
    @SuppressWarnings("ConstantConditions") // Exception is caught.
    public static void initBackButtonForActivity( AppCompatActivity act )
    {
        try { act.getActionBar().setDisplayHomeAsUpEnabled(true) ; }
        catch( Exception x )
        { Log.d( LOG_TAG, "Could not initialize back button.", x ) ; }
    }
}
