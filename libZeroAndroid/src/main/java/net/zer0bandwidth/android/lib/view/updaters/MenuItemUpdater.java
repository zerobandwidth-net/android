package net.zer0bandwidth.android.lib.view.updaters;

import android.content.Context;
import android.view.MenuItem;

/**
 * Updates the text and icon of a known {@link MenuItem}.
 *
 * This task class should be run directly on the UI thread by the context that
 * creates it.
 *
 * <pre>this.runOnUiThread( new MenuItemUpdater( mi, sCaption, resIconID ) ) ;</pre>
 *
 * @since zer0bandwidth-net/android 0.1.2 (#28)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class MenuItemUpdater
implements Runnable
{
	/**
	 * Magic value to be used for {@link #m_resIcon} when no icon should be set.
	 */
	protected static final int ICON_NOT_SET = -1 ;

	/** The menu item to be updated. */
	protected MenuItem m_mi = null ;

	/** The text of the caption to be set for the item. */
	protected String m_sCaption = null ;

	/** The resource ID of the icon that should be used for the item. */
	protected int m_resIcon = ICON_NOT_SET ;

	/**
	 * Sets up the updater.
	 * @param mi the menu item to be updated
	 * @param sCaption the caption to be assigned
	 * @param resIcon the icon to be assigned
	 */
	public MenuItemUpdater( MenuItem mi, String sCaption, int resIcon )
	{ this.init( mi, sCaption, resIcon ) ; }

	/**
	 * Sets up the updater.
	 * No icon resource will be set for the item.
	 * @param mi the menu item to be updated
	 * @param sCaption the icon to be assigned
	 */
	public MenuItemUpdater( MenuItem mi, String sCaption )
	{ this.init( mi, sCaption, ICON_NOT_SET ) ; }

	/**
	 * Sets up the updater.
	 * @param mi the menu item to be updated
	 * @param ctx a context to provide string resources
	 * @param resCaption the resource ID of the caption to be set
	 * @param resIcon the icon to be set
	 */
	public MenuItemUpdater( MenuItem mi, Context ctx, int resCaption, int resIcon )
	{ this.init( mi, ctx.getString( resCaption ), resIcon ) ; }

	/**
	 * Sets up the updater.
	 * No icon resource will be set for the item.
	 * @param mi the menu item to be updated
	 * @param ctx a context to provide string resources
	 * @param resCaption the resource ID of the caption to be set
	 */
	public MenuItemUpdater( MenuItem mi, Context ctx, int resCaption )
	{ this.init( mi, ctx.getString( resCaption ), ICON_NOT_SET ) ; }

	/**
	 * Initializes the task instance. Called by the various constructors.
	 * @param mi the menu item to be updated
	 * @param sCaption the caption to be set
	 * @param resIcon the icon to be set
	 * @return (fluid)
	 */
	protected MenuItemUpdater init( MenuItem mi, String sCaption, int resIcon )
	{ m_mi = mi ; m_sCaption = sCaption ; m_resIcon = resIcon ; return this ; }

	@Override
	public void run()
	{
		m_mi.setTitle( m_sCaption ) ;
		if( m_resIcon != ICON_NOT_SET )
			m_mi.setIcon( m_resIcon ) ;
	}
}
