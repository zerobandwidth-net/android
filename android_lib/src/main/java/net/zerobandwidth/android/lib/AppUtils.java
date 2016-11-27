package net.zerobandwidth.android.lib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

/**
 * Provides utilities for dealing with common Android app tasks.
 * @since zerobandwidth-net/android 0.0.1 (#5)
 */
@SuppressWarnings("unused") // This is a library.
public class AppUtils
{
    /** A logging tag. */
    public static final String TAG = AppUtils.class.getSimpleName() ;

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
            Log.w( TAG, "Can't discover app version." ) ;
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
}
