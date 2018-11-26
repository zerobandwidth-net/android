package net.zer0bandwidth.android.lib.text.format;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Exercises {@link TitleFormatter}.
 * @since zer0bandwidth-net/android 0.1.5 (#11)
 */
@RunWith( AndroidJUnit4.class )
public class TitleFormatterTest
{
	/** Exercises {@link TitleFormatter#resolveEnglishToken}. */
	@Test
	public void testResolveEnglishToken()
	{
		Context ctx = InstrumentationRegistry.getContext() ;
		TitleFormatter.EnglishLocaleContext lctx =
				new TitleFormatter.EnglishLocaleContext( ctx ) ;
		assertEquals( "Fargle", TitleFormatter
				.resolveEnglishToken( lctx, "fargle", false ) ) ;
		assertEquals( "Fargle", TitleFormatter
				.resolveEnglishToken( lctx, "FARGLE", false ) ) ;
		assertEquals( "To", TitleFormatter
				.resolveEnglishToken( lctx, "to", true ) ) ;
		assertEquals( "to", TitleFormatter
				.resolveEnglishToken( lctx, "to", false ) ) ;
		assertEquals( "to", TitleFormatter
				.resolveEnglishToken( lctx, "TO", false ) ) ;
		assertEquals( "Before", TitleFormatter
				.resolveEnglishToken( lctx, "before", false ) ) ;
		assertEquals( "Before", TitleFormatter
				.resolveEnglishToken( lctx, "BEFORE", false ) ) ;
		assertEquals( "The", TitleFormatter
				.resolveEnglishToken( lctx, "the", true ) ) ;
		assertEquals( "the", TitleFormatter
				.resolveEnglishToken( lctx, "the", false ) ) ;
		assertEquals( "the", TitleFormatter
				.resolveEnglishToken( lctx, "THE", false ) ) ;
		assertEquals( "Little-Endian", TitleFormatter
				.resolveEnglishToken( lctx, "little-endian", false ) ) ;
	}

	/** Exercises {@link TitleFormatter#formatEnglishTitle}. */
	@Test
	public void testFormatEnglishTitle()
	{
		Context ctx = InstrumentationRegistry.getContext() ;
		assertEquals( "For Whom the Bell Tolls", TitleFormatter
				.formatEnglishTitle( ctx, "FOR WHOM THE BELL TOLLS" ) ) ;
		assertEquals( "The Sun Also Rises", TitleFormatter
				.formatEnglishTitle( ctx, "the sun also rises" ) ) ;
		assertEquals( "The Catcher in the Rye", TitleFormatter
				.formatEnglishTitle( ctx, "the Catcher IN THE rye" ) ) ;
		assertEquals( "One Flew over the Cuckoo's Nest", TitleFormatter
				.formatEnglishTitle( ctx, "one flew OVER THE cuckoo's NesT" ) );
	}
}
