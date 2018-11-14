package net.zerobandwidth.android.lib.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * An instance of this class acts as a portal to the app's shared preferences.
 * It provides some convenience wrappers for some of the more commonly-used
 * methods of {@link android.content.SharedPreferences}; most notably:
 *
 * <ul>
 *     <li>
 *         Getter methods that substitute sane defaults, so you don't have to
 *         keep supplying them yourself.
 *     </li>
 *     <li>
 *         Quick methods for writing preference values to a
 *         {@link SharedPreferences.Editor}; these methods can then be chained
 *         with {@link SharedPreferences.Editor#apply apply()} or
 *         {@link SharedPreferences.Editor#commit commit()} (though the latter
 *         may draw the ire of Lint without a
 *         {@literal @SuppressLint( "CommitPrefEdits" )} annotation.
 *     </li>
 *     <li>
 *         A set of methods for marshalling integer-value preferences as strings
 *         in the app's preferences. This is a workaround for occasional wonky
 *         behavior of native integer-value preferences in Android.
 *     </li>
 *     <li><i>(since 0.2.1)</i>
 *         A set of methods for marshalling Boolean-value preferences as strings
 *         in the app's preferences.
 *     </li>
 * </ul>
 *
 * <p>An app may choose to extend this class to further provide easy methods for
 * getting and setting specific preferences.</p>
 *
 * <p>Note that this class is basically syntactic sugar; an app can easily do
 * all these things itself, instead. This author accepts no responsibility for
 * any taunting or other lack of respect for using this class to clean up what
 * would otherwise be a pretty tedious coding task. However, in the case of
 * stringified integer values, the class does provide static versions of those
 * methods, for those who don't want to lean heavily on the whole class.</p>
 *
 * @since zerobandwidth-net/android 0.0.4 (#14)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class PreferencePortal
{
	public static final String LOG_TAG = PreferencePortal.class.getSimpleName();

	/** The default value for Boolean preferences. ({@code false}) */
	public static final boolean DEFAULT_BOOLEAN = false ;
	/**
	 * A default value for integer fields, which indicates "not set".
	 * ({@code -1}). Assign this default when your integer preference is
	 * intended to be unsigned, but knowing that the value has not been set at
	 * all is valuable.
	 */
	public static final int DEFAULT_INT_NOT_SET = -1 ;
	/**
	 * The usual default value for integer fields. ({@code 0}) The class will
	 * choose this default value on its own, but it may be reconfigured with
	 * {@link #useIntegerDefaultValue}.
	 */
	public static final int DEFAULT_INT_ZERO = 0 ;

	/** The default value for string fields. ({@code null} */
	public static final String DEFAULT_STRING = null ;

	/**
	 * Get the value of a Boolean preference that was stored as a string.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param bDefault the default value, if not set
	 * @return the Boolean value of the preference
	 * @since zerobandwidth-net/android 0.2.1 (#55)
	 */
	public static boolean getStringifiedBoolean(
			SharedPreferences prefs, String sKey, boolean bDefault )
	{
		return Boolean.valueOf(
				prefs.getString( sKey, String.valueOf( bDefault ) ) ) ;
	}

	/**
	 * Edit the value of a Boolean preference that is stored as a string.
	 * This method should be chained with either the {@code apply()} or
	 * {@code commit()} method of the preference editor to complete the change.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param b the value to write
	 * @return a preference editor which would be chained with either
	 *  {@code apply()} or {@code commit()}
	 * @since zerobandwidth-net/android 0.2.1 (#55)
	 */
	public static SharedPreferences.Editor putStringifiedBoolean(
			SharedPreferences prefs, String sKey, boolean b )
	{ return prefs.edit().putString( sKey, String.valueOf(b) ) ; }

	/**
	 * Get the value of an integer preference that is stored as a string.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param zDefault the default value, if not set
	 * @return the integer value of the preference
	 * @throws NumberFormatException if the preference's value can't be parsed
	 */
	public static int getStringifiedInt( SharedPreferences prefs,
		String sKey, int zDefault )
	throws NumberFormatException
	{
		return Integer.valueOf(
				prefs.getString( sKey, String.valueOf(zDefault) ) ) ;
	}

	/**
	 * Edit the value of an integer preference that is stored as a string.
	 * This method should be chained with either the {@code apply()} or
	 * {@code commit()} method of the preference editor to complete the change.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param z the value to write
	 * @return a preference editor which would be chained with either
	 *  {@code apply()} or {@code commit()}
	 */
	public static SharedPreferences.Editor putStringifiedInt(
		SharedPreferences prefs, String sKey, int z )
	{ return prefs.edit().putString( sKey, String.valueOf(z) ) ; }

	/**
	 * Get the value of a long-integer preference that is stored as a string.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param zDefault the default value, if not set
	 * @return the long-integer value of the preference
	 * @throws NumberFormatException if the preference's value can't be parsed
	 */
	public static long getStringifiedLong( SharedPreferences prefs,
		String sKey, long zDefault )
	throws NumberFormatException
	{
		return Long.valueOf(
				prefs.getString( sKey, String.valueOf(zDefault) ) ) ;
	}

	/**
	 * Edit the value of a long-integer preference that is stored as a string.
	 * This method should be chained with either the {@code apply} or
	 * {@code commit()} method of the preference editor to complete the change.
	 * @param prefs the app's preferences
	 * @param sKey the preference key
	 * @param z the value to write
	 * @return a preference editor which would be chained with either
	 *  {@code apply()} or {@code commit()}
	 */
	public static SharedPreferences.Editor putStringifiedLong(
		SharedPreferences prefs, String sKey, long z )
	{ return prefs.edit().putString( sKey, String.valueOf(z) ) ; }


	protected int m_zDefaultInteger = DEFAULT_INT_ZERO ;

	/** The context in which preferences are managed. */
	protected Context m_ctx ;

	/** A persistent reference to the app's preferences. */
	protected SharedPreferences m_prefs ;

	public PreferencePortal( Context ctx )
	{
		m_ctx = ctx ;
		m_prefs = PreferenceManager.getDefaultSharedPreferences( ctx ) ;
	}

	/**
	 * Accesses the {@link SharedPreferences} instance used by this class.
	 * @return the app's preferences
	 */
	public SharedPreferences getPrefs()
	{ return m_prefs ; }

	/**
	 * Specifies the value that should be used as the integer default.
	 * @param zDefault a value to be used as the default for all integers
	 * @return (fluid)
	 * @see #DEFAULT_INT_ZERO
	 * @see #DEFAULT_INT_NOT_SET
	 */
	public PreferencePortal useIntegerDefaultValue( int zDefault )
	{ m_zDefaultInteger = zDefault ; return this ; }

	public boolean getBoolean( String sKey, boolean bDefault )
	{ return m_prefs.getBoolean( sKey, bDefault ) ; }

	public boolean getBoolean( String sKey )
	{ return this.getBoolean( sKey, DEFAULT_BOOLEAN ) ; }

	public boolean getBoolean( int resKey, boolean bDefault )
	{ return this.getBoolean( m_ctx.getString(resKey), bDefault ) ; }

	public boolean getBoolean( int resKey )
	{ return this.getBoolean( resKey, DEFAULT_BOOLEAN ) ; }

	/**
	 * Fetches a Boolean preference that was stored as a string.
	 * @param sKey the preference key
	 * @param bDefault a default if not set
	 * @return the preference value
	 * @since zerobandwidth-net/android 0.2.1
	 */
	public boolean getStringifiedBoolean( String sKey, boolean bDefault )
	{ return PreferencePortal.getStringifiedBoolean(m_prefs,sKey,bDefault) ; }

	/**
	 * Fetches a Boolean preference that was stored as a string, using
	 * {@link #DEFAULT_BOOLEAN} as the default if the preference is not set.
	 * @param sKey the preference key
	 * @return the preference value
	 * @since zerobandwidth-net/android 0.2.1
	 */
	public boolean getStringifiedBoolean( String sKey )
	{ return this.getStringifiedBoolean( sKey, DEFAULT_BOOLEAN ) ; }

	/**
	 * Fetches a Boolean preference that was stored as a string.
	 * @param resKey the ID of a string resource whose value is the preference
	 *               key
	 * @param bDefault a default if not set
	 * @return the preference value
	 * @since zerobandwidth-net/android 0.2.1
	 */
	public boolean getStringifiedBoolean( int resKey, boolean bDefault )
	{ return this.getStringifiedBoolean( m_ctx.getString(resKey), bDefault ) ; }

	/**
	 * Fetches a Boolean preference that was stored as a string, using
	 * {@link #DEFAULT_BOOLEAN} as the default if the preference is not set.
	 * @param resKey the ID of a string resource whose value is the preference
	 *               key
	 * @return the preference value
	 * @since zerobandwidth-net/android 0.2.1
	 */
	public boolean getStringifiedBoolean( int resKey )
	{ return this.getStringifiedBoolean( resKey, DEFAULT_BOOLEAN ) ; }

	public int getInt( String sKey, int zDefault )
	{ return m_prefs.getInt( sKey, zDefault ) ; }

	public int getInt( String sKey )
	{ return this.getInt( sKey, m_zDefaultInteger ) ; }

	public int getInt( int resKey, int zDefault )
	{ return this.getInt( m_ctx.getString(resKey), zDefault ) ; }

	public int getInt( int resKey )
	{ return this.getInt( resKey, m_zDefaultInteger ) ; }

	public int getStringifiedInt( String sKey, int zDefault )
	throws NumberFormatException
	{ return PreferencePortal.getStringifiedInt( m_prefs, sKey, zDefault ) ; }

	public int getStringifiedInt( String sKey )
	{ return this.getStringifiedInt( sKey, m_zDefaultInteger ) ; }

	public int getStringifiedInt( int resKey, int zDefault )
	{ return this.getStringifiedInt( m_ctx.getString(resKey), zDefault ) ; }

	public int getStringifiedInt( int resKey )
	{ return this.getStringifiedInt( resKey, m_zDefaultInteger ) ; }

	public long getLong( String sKey, long zDefault )
	{ return m_prefs.getLong( sKey, zDefault ) ; }

	public long getLong( String sKey )
	{ return this.getLong( sKey, ((long)(m_zDefaultInteger)) ) ; }

	public long getLong( int resKey, long zDefault )
	{ return this.getLong( m_ctx.getString(resKey), zDefault ) ; }

	public long getLong( int resKey )
	{ return this.getLong( resKey, ((long)(m_zDefaultInteger)) ) ; }

	public long getStringifiedLong( String sKey, long zDefault )
	throws NumberFormatException
	{ return PreferencePortal.getStringifiedLong( m_prefs, sKey, zDefault ) ; }

	public long getStringifiedLong( String sKey )
	throws NumberFormatException
	{ return this.getStringifiedLong( sKey, ((long)(m_zDefaultInteger)) ) ; }

	public long getStringifiedLong( int resKey, long zDefault )
	throws NumberFormatException
	{ return this.getStringifiedLong( m_ctx.getString(resKey), zDefault ) ; }

	public long getStringifiedLong( int resKey )
	throws NumberFormatException
	{ return this.getStringifiedLong( resKey, ((long)(m_zDefaultInteger)) ) ; }

	public String getString( String sKey, String sDefault )
	{ return m_prefs.getString( sKey, sDefault ) ; }

	public String getString( String sKey )
	{ return this.getString( sKey, DEFAULT_STRING ) ; }

	public String getString( int resKey, String sDefault )
	{ return this.getString( m_ctx.getString(resKey), sDefault ) ; }

	public String getString( int resKey )
	{ return this.getString( resKey, DEFAULT_STRING ) ; }

	public SharedPreferences.Editor putBoolean( String sKey, boolean b )
	{ return m_prefs.edit().putBoolean( sKey, b ) ; }

	public SharedPreferences.Editor putBoolean( int resKey, boolean b )
	{ return this.putBoolean( m_ctx.getString(resKey), b ) ; }

	/**
	 * Edit the value of a Boolean preference that is stored as a string.
	 * This method should be chained with either the {@code apply()} or
	 * {@code commit()} method of the preference editor to complete the change.
	 * @param sKey the preference key
	 * @param b the value to write
	 * @return a preference editor which would be chained with either
	 *  {@code apply()} or {@code commit()}
	 * @since zerobandwidth-net/android 0.2.1 (#55)
	 */
	public SharedPreferences.Editor putStringifiedBoolean( String sKey, boolean b )
	{ return m_prefs.edit().putString( sKey, String.valueOf(b) ) ; }

	/**
	 * Edit the value of a Boolean preference that is stored as a string.
	 * This method should be chained with either the {@code apply()} or
	 * {@code commit()} method of the preference editor to complete the change.
	 * @param resKey the ID of a string resource whose value is the preference
	 *               key
	 * @param b the value to write
	 * @return a preference editor which would be chained with either
	 *  {@code apply()} or {@code commit()}
	 * @since zerobandwidth-net/android 0.2.1 (#55)
	 */
	public SharedPreferences.Editor putStringifiedBoolean( int resKey, boolean b )
	{ return this.putStringifiedBoolean( m_ctx.getString(resKey), b ) ; }

	public SharedPreferences.Editor putInt( String sKey, int z )
	{ return m_prefs.edit().putInt( sKey, z ) ; }

	public SharedPreferences.Editor putInt( int resKey, int z )
	{ return this.putInt( m_ctx.getString(resKey), z ) ; }

	public SharedPreferences.Editor putStringifiedInt( String sKey, int z )
	{ return m_prefs.edit().putString( sKey, String.valueOf(z) ) ; }

	public SharedPreferences.Editor putStringifiedInt( int resKey, int z )
	{ return this.putStringifiedInt( m_ctx.getString(resKey), z ) ; }

	public SharedPreferences.Editor putLong( String sKey, long z )
	{ return m_prefs.edit().putLong( sKey, z ) ; }

	public SharedPreferences.Editor putLong( int resKey, long z )
	{ return this.putLong( m_ctx.getString(resKey), z ) ; }

	public SharedPreferences.Editor putStringifiedLong( String sKey, long z )
	{ return m_prefs.edit().putString( sKey, String.valueOf(z) ) ; }

	public SharedPreferences.Editor putStringifiedLong( int resKey, long z )
	{ return this.putStringifiedLong( m_ctx.getString(resKey), z ) ; }

	public SharedPreferences.Editor putString( String sKey, String s )
	{ return m_prefs.edit().putString( sKey, s ) ; }

	public SharedPreferences.Editor putString( int resKey, String s )
	{ return this.putString( m_ctx.getString(resKey), s ) ; }
}
