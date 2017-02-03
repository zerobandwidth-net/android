package net.zerobandwidth.android.lib.nonsense;

import android.content.Context;

/**
 * This interface provides a specification for a "nonsense generator" &mdash; a
 * class which generates a fully-formed sentence from randomly-chosen words.
 * @since zerobandwidth-net/android 0.0.1 (#7)
 */
public interface NonsenseGenerator
{
	/**
	 * Sets the context in which string resources can be fetched.
	 *
	 * If this context has not yet been set, then the class will not be able to
	 * fetch string resources to assemble the nonsense string.
	 *
	 * Implementations of this interface should also provide a constructor which
	 * sets this context upfront.
	 *
	 * @param ctx the context in which string resources are available
	 * @return (fluid)
	 */
	NonsenseGenerator setContext( Context ctx ) ;

	/**
	 * Generates the nonsense string.
	 *
	 * The algorithm for creating the string is implementation-dependent.
	 *
	 * @return a fully-formed sentence from randomly-selected words
	 */
	String getString() ;
}
