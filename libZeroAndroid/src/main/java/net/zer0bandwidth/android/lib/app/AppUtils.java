package net.zer0bandwidth.android.lib.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.zer0bandwidth.android.lib.R;

import java.util.Locale;

/**
 * Provides utilities for dealing with common Android app tasks.
 * @since zer0bandwidth-net/android 0.0.1 (#5), moved in 0.0.4 (#13)
 */
@SuppressWarnings("unused") // This is a library.
public class AppUtils
{
    /** A logging tag. */
    public static final String LOG_TAG = AppUtils.class.getSimpleName() ;

    /**
     * Replacement text to be used if the app's version name can't be found.
     * In version 0.2.1 of this library, the value changed from an
     * empty string to {@code "(unknown)"}.
     * @see #getAppVersion
     * @see #getAppNameAndVersion
     */
    public static final String APP_VERSION_NOT_FOUND = "(unknown)" ;

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
            Log.w( LOG_TAG, "Can't discover app version.", x ) ;
            return APP_VERSION_NOT_FOUND ;
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
     * Initializes a back button for the activity.
     *
     * Call this method in your activity's {@link Activity#onCreate onCreate}
     * method. Then, in the activity's
     * {@link Activity#onOptionsItemSelected onOptionsItemSelected} method,
     * remember to set a handler for this button.
     *
     * <pre>
     *     public boolean onOptionsItemSelected( MenuItem item )
     *     {
     *         switch( item.getItemId() )
     *         {
     *             case android.R.id.home:
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
     * Call this method in your activity's
     * {@link AppCompatActivity#onCreate onCreate} method. Then, in the activity's
     * {@link AppCompatActivity#onOptionsItemSelected onOptionsItemSelected}
     * method, remember to set a handler for this button.
     *
     * <pre>
     *     public boolean onOptionsItemSelected( MenuItem item )
     *     {
     *         switch( item.getItemId() )
     *         {
     *             case android.R.id.home:
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
        try { act.getSupportActionBar().setDisplayHomeAsUpEnabled(true) ; }
        catch( Exception x )
        { Log.d( LOG_TAG, "Could not initialize back button.", x ) ; }
    }

	/**
     * (in AppCompat context) Determines whether the current text layout is
     * right-to-left.
     * @param ctx the context in which to evaluate text directionality
     * @return {@code true} if text layout is right-to-left
     * @see TextUtilsCompat#getLayoutDirectionFromLocale
     * @see <a href="http://stackoverflow.com/a/14389640">Stack Overflow answer #14389640</a>
     * @see <a href="http://stackoverflow.com/a/23203698">Stack Overflow answer #23203698</a>
     * @since zer0bandwidth-net/android 0.0.2 (#8)
     */
    public static boolean isTextCompatRTL( Context ctx )
    {
        Configuration cfg = ctx.getResources().getConfiguration() ;

        if( Build.VERSION.SDK_INT < 17 )
            return legacyIsTextRTL(cfg) ;

        Locale loc ;
        if( Build.VERSION.SDK_INT < 24 )
        { // The `locale` property was deprecated in API 24.
            //noinspection deprecation
            loc = cfg.locale ;
        }
        else
            loc = cfg.getLocales().get(0) ;

        int nLayoutDirection =
                TextUtilsCompat.getLayoutDirectionFromLocale(loc) ;
        return ( nLayoutDirection != ViewCompat.LAYOUT_DIRECTION_LTR ) ;
    }

	/**
     * (in non-compat context) Determines whether the current text layout is
     * right-to-left.
     * @param ctx the context in which to evaluate text directionality
     * @return {@code true} if text layout is right-to-left
     * @see TextUtils#getLayoutDirectionFromLocale
     * @see <a href="http://stackoverflow.com/a/14389640">Stack Overflow answer #14389640</a>
     * @see <a href="http://stackoverflow.com/a/23203698">Stack Overflow answer #23203698</a>
     * @since zer0bandwidth-net/android 0.0.2 (#8)
     */
    public static boolean isTextRTL( Context ctx )
    {
        Configuration cfg = ctx.getResources().getConfiguration() ;

        if( Build.VERSION.SDK_INT < 17 )
            return legacyIsTextRTL(cfg) ;

        Locale loc ;
        if( Build.VERSION.SDK_INT < 24 )
        { // The `locale` property was deprecated in API 24.
            //noinspection deprecation
            loc = cfg.locale ;
        }
        else
            loc = cfg.getLocales().get(0) ;

        int nLayoutDirection = TextUtils.getLayoutDirectionFromLocale(loc) ;
        return ( nLayoutDirection != View.LAYOUT_DIRECTION_LTR ) ;
    }

    /**
     * (prior to API 17) Determines whether the current text layout is
     * right-to-left.
     * Consumed by {@link #isTextRTL} and {@link #isTextCompatRTL}.
     * @param cfg the current context's configuration
     * @return {@code true} if text layout is right-to-left
     * @see <a href="http://stackoverflow.com/a/23203698">Stack Overflow answer #23203698</a>
     * @since zer0bandwidth-net/android 0.1.5 (#32)
     */
    @SuppressWarnings( "deprecation" ) // locale deprecated only after API 24
    protected static boolean legacyIsTextRTL( Configuration cfg )
    {
        int nDir = Character.getDirectionality(
                cfg.locale.getDisplayName().charAt(0) ) ;
        return( nDir == Character.DIRECTIONALITY_RIGHT_TO_LEFT
             || nDir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC ) ;
    }
}
