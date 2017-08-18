package net.zerobandwidth.android.lib.util;

import java.util.Date;

/**
 * Provides canonical methods for dealing with dates and times.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
public final class TimeUtils
{
	/**
	 * Returns milliseconds since the epoch, GMT.
	 * @return the number of milliseconds since the epoch
	 */
	public static long now()
	{ return (new Date()).getTime() ; }

	/** Forbid instantiation. */
	private TimeUtils() {}
}
