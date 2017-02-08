package net.zerobandwidth.android.lib.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.zerobandwidth.android.lib.app.AppUtils;
import net.zerobandwidth.android.lib.R;

/**
 * Allows specification of a number of multiple taps before the positive button
 * takes action.
 *
 * Unlike the standard {@link AlertDialog}, this class is its own builder.
 *
 * Special thanks to <code><b>@dapayne1</b></code> for the original inspiration.
 *
 * @since zerobandwidth-net/android 0.0.2 (#8)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class MultitapAlertCompatDialog
extends AlertDialog
implements DialogInterface.OnClickListener
{
	public static final String LOG_TAG =
			MultitapAlertCompatDialog.class.getSimpleName() ;

	/**
	 * Specifies the default number of taps required on the positive button
	 * before its action is executed.
	 */
	public static final int DEFAULT_TAPS_REQUIRED = 5 ;

	/** The number of times that the positive button has been tapped so far. */
	protected int m_nTapsCurrent = 0 ;

	/** The number of taps required to execute the positive button's action. */
	protected int m_nTapsRequired = DEFAULT_TAPS_REQUIRED ;

	/**
	 * The resource ID of the string which formats the positive button's label.
	 * This defaults to a left-to-right format, but may be changed to a
	 * right-to-left format at runtime if the context demands it.
	 * @see R.string#label_btnMultitapPositive_LTR
	 * @see R.string#label_btnMultitapPositive_RTL
	 */
	protected int m_resPositiveLabelFormat =
			R.string.label_btnMultitapPositive_LTR ;

	/**
	 * The positive button label. This will be used as the first part of the
	 * string format.
	 * @see #m_resPositiveLabelFormat
	 * @see R.string#label_btnMultitapPositive_LTR
	 * @see R.string#label_btnMultitapPositive_RTL
	 */
	protected String m_sPositiveLabel = null ;

	/** The task to be executed if the positive button is tapped sufficiently. */
	protected Runnable m_taskPositive = null ;

	/** The task to be executed if the negative button is tapped. */
	protected Runnable m_taskNegative = null ;

	/**
	 * Once the dialog is constructed, this will hold a persistent reference to
	 * the positive button itself, so that we can dynamically update its text.
	 */
	protected Button m_btnPositive = null ;

	/**
	 * Additional constructor allowing initial setup of title and message.
	 * @param ctx the context that will spawn the dialog
	 * @param resTitle the resource ID of the title string
	 * @param resMessage the resource ID of the message string
	 */
	public MultitapAlertCompatDialog( Context ctx, int resTitle, int resMessage )
	{
		super(ctx) ;
		this.setTitle( resTitle ) ;
		this.setMessage( ctx.getString( resMessage ) ) ;
		m_resPositiveLabelFormat = ( AppUtils.isTextCompatRTL(ctx) ?
				R.string.label_btnMultitapPositive_RTL :
				R.string.label_btnMultitapPositive_LTR )
			;
	}

	/**
	 * Sets the number of taps required to execute the positive action.
	 * @param nTaps the number of taps required
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setPositiveTapsRequired( int nTaps )
	{ this.m_nTapsRequired = nTaps ; return this ; }

	/**
	 * Add a positive button with the specified label, such that the specified
	 * task is executed when tapped the requisite number of times.
	 * @param resLabel the label for the button
	 * @param task the task to be executed
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setPositiveButton( int resLabel, Runnable task )
	{
		final Context ctx = this.getContext() ;
		m_taskPositive = task ;
		m_sPositiveLabel = ctx.getString( resLabel ) ;
		this.setButton( AlertDialog.BUTTON_POSITIVE, m_sPositiveLabel, this ) ;
		return this ;
	}

	/**
	 * Add a positive button which will execute the specified task when tapped
	 * the requisite number of times.
	 *
	 * The Android standard "OK" text ({@link android.R.string#ok}) will be used
	 * as the button label.
	 *
	 * @param task the task to be executed
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setPositiveButton( Runnable task )
	{ return this.setPositiveButton( android.R.string.ok, task ) ; }

	/**
	 * Add a negative button with the specified label, such that the specified
	 * task is executed when tapped <i>once</i>.
	 * @param resLabel the label for the button
	 * @param task the task to be executed
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setNegativeButton( int resLabel, Runnable task )
	{
		final Context ctx = this.getContext() ;
		m_taskNegative = task ;
		final String sNegativeLabel = ctx.getString( resLabel ) ;
		this.setButton( AlertDialog.BUTTON_NEGATIVE, sNegativeLabel, this ) ;
		return this ;
	}

	/**
	 * Add a negative button which will execute the specified task when tapped
	 * <i>once</i>.
	 *
	 * The Android standard "Cancel" ({@link android.R.string#cancel}) will be
	 * used as the button label.
	 *
	 * @param task the task to be executed
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setNegativeButton( Runnable task )
	{ return this.setNegativeButton( android.R.string.cancel, task ) ; }

	/**
	 * Sets up a standard "OK" positive button and a standard "Cancel" negative
	 * button, using the specified tasks.
	 * @param taskPositive the task to be executed if the positive button is
	 *                     tapped the requisite number of times
	 * @param taskNegative the task to be executed if the negative button is
	 *                     tapped <i>once</i>
	 * @return (fluid)
	 */
	public MultitapAlertCompatDialog setStandardButtons( Runnable taskPositive, Runnable taskNegative )
	{
		return this.setPositiveButton( taskPositive )
		           .setNegativeButton( taskNegative )
				;
	}

	/**
	 * Regenerates the label of the positive button, showing the difference
	 * between the number of taps required and the number of taps already
	 * intercepted.
	 * @return the positive button label
	 */
	protected MultitapAlertCompatDialog regeneratePositiveLabel()
	{
		final int nTapsRemaining = m_nTapsRequired - m_nTapsCurrent ;
		final String sLabel = this.getContext().getString(
				m_resPositiveLabelFormat,
				m_sPositiveLabel, Integer.toString( nTapsRemaining )
			);
		m_btnPositive.setText( sLabel ) ;

		return this ;
	}

	@Override
	public void onClick( DialogInterface dia, int zButtonID )
	{
		switch( zButtonID )
		{
			case AlertDialog.BUTTON_POSITIVE:
			{
				Log.d( LOG_TAG, "Dismissing after positive press." ) ;
				this.dismiss() ;
				if( m_taskPositive != null ) m_taskPositive.run() ;
			} break ;
			case AlertDialog.BUTTON_NEGATIVE:
			default:
			{
				Log.d( LOG_TAG, "Clicked the negative button with " + Integer.toString(m_nTapsCurrent) + " prior taps." ) ;
				this.dismiss() ;
				if( m_taskNegative != null ) m_taskNegative.run() ;
			}
		}
	}

	@Override
	public void show()
	{
		super.show() ;
		m_nTapsCurrent = 0 ; // Just in case a value weirdly persisted.
		m_btnPositive = this.getButton( AlertDialog.BUTTON_POSITIVE ) ;
		if( m_btnPositive != null )
		{ // Override the dialog's click listener with our own.
			this.regeneratePositiveLabel() ;
			m_btnPositive.setOnClickListener( new Button.OnClickListener()
			{
				protected final MultitapAlertCompatDialog m_dia =
						MultitapAlertCompatDialog.this ;

				@Override
				public void onClick( View w )
				{
					if( ++(m_dia.m_nTapsCurrent) >= m_dia.m_nTapsRequired )
						m_dia.onClick( m_dia, AlertDialog.BUTTON_POSITIVE ) ;
					else
						m_dia.regeneratePositiveLabel() ;
				}
			});
		}
	}
}
