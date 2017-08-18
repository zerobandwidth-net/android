package net.zerobandwidth.android.lib.content;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.zerobandwidth.android.lib.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Provides convenience methods and canonical implementations of methods for
 * working with content in general.
 * @since zerobandwidth-net/android 0.1.2 (#27)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class ContentUtils
{
	protected static final String LOG_TAG = ContentUtils.class.getSimpleName() ;

/// Static constants: ContentResolver syntax ///////////////////////////////////

	/**
	 * The magic word used by {@link android.content.ContentResolver#query} to
	 * specify that results should be sorted in ascending order.
	 * @since zerobandwidth-net/android 0.1.7 (#39)
	 */
	public static final String QUERY_ORDER_ASCENDING = "ASC" ;

	/**
	 * The magic word used by {@link android.content.ContentResolver#query} to
	 * specify that results should be sorted in descending order.
	 * @since zerobandwidth-net/android 0.1.7 (#39)
	 */
	public static final String QUERY_ORDER_DESCENDING = "DESC" ;

	/**
	 * The character that stands in for a variable value in the Android format
	 * string that is supplied to methods of
	 * {@link android.content.ContentResolver}.
	 * @since zerobandwidth-net/android 0.1.7 (#39)
	 */
	public static final String QUERY_VARIABLE_MARKER = "?" ;

/// Static constants: MIME types ///////////////////////////////////////////////

	/** MIME type "text/plain" */
	public static final String MIMETYPE_TEXT_PLAIN = "text/plain" ;

/// Static singleton references ////////////////////////////////////////////////

	/**
	 * A persistent reference to the system's clipboard manager.
	 */
	public static ClipboardManager s_mgrClipboard = null ;

/// Static methods /////////////////////////////////////////////////////////////

	/**
	 * Retrieves the system's clipboard manager. If an instance is not already
	 * maintained in the class, then it will be fetched and bound there.
	 * @param ctx the context from which to retrieve the manager instance
	 * @return the manager instance
	 */
	public static ClipboardManager getClipboardManager( Context ctx )
	{
		if( s_mgrClipboard == null )
		{
			s_mgrClipboard = ((ClipboardManager)
					( ctx.getSystemService( Context.CLIPBOARD_SERVICE ) )) ;
		}
		return s_mgrClipboard ;
	}

	/**
	 * Copies the specified plain text into the clipboard.
	 * @param ctx the context in which to operate
	 * @param sClipLabel the "label" of the new clipboard item
	 * @param sText the plain text to be copied
	 * @see ClipboardManager#setPrimaryClip(ClipData)
	 */
	public static void copyTextToClipboard( Context ctx, String sClipLabel, String sText )
	{
		ClipData clip = ClipData.newPlainText( sClipLabel, sText ) ;
		getClipboardManager(ctx).setPrimaryClip(clip) ;
	}

	/**
	 * Copies the specified plain text into the clipboard.
	 * The "label" of the new clipboard item is defaulted to the value of the
	 * {@link R.string#app_name} resource, which is usually the name of the
	 * current app.
	 * @param ctx the context in which to operate
	 * @param sText the plain text to be copied
	 * @see ClipboardManager#setPrimaryClip(ClipData)
	 */
	public static void copyTextToClipboard( Context ctx, String sText )
	{ copyTextToClipboard( ctx, ctx.getString(R.string.app_name), sText ) ; }

	/**
	 * Copies the specified plain text into the clipboard.
	 * @param ctx the context in which to operate
	 * @param sClipLabel the "label" of the new clipboard item
	 * @param resText the resource ID of the text to be added to the clipboard
	 * @see ClipboardManager#setPrimaryClip(ClipData)
	 */
	public static void copyTextToClipboard( Context ctx, String sClipLabel, int resText )
	{ copyTextToClipboard( ctx, sClipLabel, ctx.getString(resText) ) ; }

	/**
	 * Copies the specified plain text into the clipboard.
	 * The "label" of the new clipboard item is defaulted to the value of the
	 * {@link R.string#app_name} resource, which is usually the name of the
	 * current app.
	 * @param ctx the context in which to operate
	 * @param resText the resource ID of the text to be added to the clipboard
	 * @see ClipboardManager#setPrimaryClip(ClipData)
	 */
	public static void copyTextToClipboard( Context ctx, int resText )
	{
		copyTextToClipboard( ctx, ctx.getString( R.string.app_name ),
				ctx.getString(resText) ) ;
	}

	/**
	 * Opens the OS-standard "share" dialog, using the specified text content to
	 * be shared.
	 * @param ctx the context in which to open the dialog
	 * @param sText the text to be shared
	 * @param sTitle the title of the dialog
	 */
	public static void shareText( Context ctx, String sText, String sTitle )
	{
		Intent sigShare = new Intent( Intent.ACTION_SEND ) ;
		sigShare.putExtra( Intent.EXTRA_TEXT, sText ) ;
		sigShare.setType( MIMETYPE_TEXT_PLAIN ) ;
		ctx.startActivity( Intent.createChooser( sigShare, sTitle ) ) ;
	}

	/**
	 * Opens the OS-standard "share" dialog, using the specified text content to
	 * be shared.
	 * @param ctx the context in which to open the dialog
	 * @param sText the text to be shared
	 * @param resTitle the resource ID of the string for the dialog title
	 */
	public static void shareText( Context ctx, String sText, int resTitle )
	{ shareText( ctx, sText, ctx.getString( resTitle ) ) ; }

	/**
	 * Opens the OS-standard "share" dialog, using the specified text content to
	 * be shared.
	 * The title of the dialog is defaulted to the value of
	 * {@link R.string#title_diaShare_default} provided by this library, which
	 * may be overridden in the app that consumes this library.
	 * @param ctx the context in which to operate
	 * @param sText the text to be shared
	 */
	public static void shareText( Context ctx, String sText )
	{ shareText( ctx, sText, R.string.title_diaShare_default ) ; }

	/**
	 * Dispatches an intent to view Twitter's "tweet" intent page, using the
	 * specified text as the contents of the tweet. This will usually trigger
	 * either the Twitter app or a browser window to open, allowing the user to
	 * confirm the tweet.
	 * @param ctx the context in which to attempt the tweet
	 * @param sTweet the tweet text
	 * @throws UnsupportedEncodingException if the tweet text cannot be
	 *  URL-encoded in UTF-8, as specified within the function
	 */
	public static void tweetText( Context ctx, String sTweet )
	throws UnsupportedEncodingException
	{
		final String sShare = (new StringBuilder())
				.append( "https://twitter.com/intent/tweet?text=" )
				.append( URLEncoder.encode( sTweet, "UTF-8" ) )
				.toString()
				;
		Uri uriShare = Uri.parse( sShare ) ;
		ctx.startActivity( new Intent( Intent.ACTION_VIEW, uriShare ) ) ;
	}

	/**
	 * As {@link #tweetText(Context, String)}, but catches the possible
	 * {@link UnsupportedEncodingException} internally, by logging an error.
	 * @param ctx the context in which to attempt the tweet
	 * @param sTweet the tweet text
	 * @return {@code true} if the attempt succeeded, or {@code false} if an
	 *  {@link UnsupportedEncodingException} was caught
	 */
	public static boolean tryToTweetText( Context ctx, String sTweet )
	{
		try { tweetText( ctx, sTweet ) ; return true ; }
		catch( UnsupportedEncodingException x )
		{
			Log.e( LOG_TAG, (new StringBuilder())
					.append( "Could not encode text: " )
					.append( sTweet )
					.toString()
				, x ) ;
			return false ;
		}
	}
}
