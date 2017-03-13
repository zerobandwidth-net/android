package net.zerobandwidth.android.lib.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.* ;

/**
 * Exercises {@link PreferencePortal}.
 * @since zerobandwidth-net/android 0.1.2 (#3)
 */
@RunWith( AndroidJUnit4.class )
@SuppressLint( "CommitPrefEdits" )
public class PreferencePortalTest
{
	/** Context in which tests are executed. */
	protected Context m_ctx ;
	/** A shared preferences object for the test class. */
	protected SharedPreferences m_prefs ;

	/** Constructs persistent context and preference instance. */
	@Before
	public void setUp()
	{
		m_ctx = InstrumentationRegistry.getTargetContext() ;
		m_prefs = PreferenceManager.getDefaultSharedPreferences(m_ctx) ;
	}

	/** Exercises {@link PreferencePortal#getStringifiedInt(SharedPreferences,String,int) */
	@SuppressLint("ApplySharedPref")
	@Test
	public void testStaticGetStringifiedInt()
	{
		m_prefs.edit().putString( "foo", "42" ).commit() ;
		assertEquals( 42,
				PreferencePortal.getStringifiedInt( m_prefs, "foo", 0 ) ) ;
	}

	/** Exercises {@link PreferencePortal#putStringifiedInt(SharedPreferences,String,int)}. */
	@Test
	public void testStaticPutStringifiedInt()
	{
		PreferencePortal.putStringifiedInt( m_prefs, "foo", 47 ).commit() ;
		assertEquals( 47, Integer.parseInt(m_prefs.getString("foo","0")) ) ;
	}

	/** Exercises {@link PreferencePortal#getStringifiedLong(SharedPreferences,String,long)} */
	@SuppressLint("ApplySharedPref")
	@Test
	public void testStaticGetStringifiedLong()
	{
		m_prefs.edit().putString( "bar", "1234567890123456789" ).commit() ;
		assertEquals( 1234567890123456789L,
				PreferencePortal.getStringifiedLong( m_prefs, "bar", 0 ) ) ;
	}

	/** Exercises {@link PreferencePortal#putStringifiedLong(SharedPreferences,String,long)} */
	@Test
	public void testStaticPutStringifiedLong()
	{
		PreferencePortal.putStringifiedLong( m_prefs, "bar", 987654321987654321L ).commit() ;
		assertEquals( 987654321987654321L,
				Long.parseLong( m_prefs.getString( "bar", "0" ) ) ) ;
	}

	/** Exercises the constructor and {@link PreferencePortal#getPrefs}. */
	@Test
	public void testBasicConstruction()
	{
		PreferencePortal p = new PreferencePortal(m_ctx) ;
		assertNotNull( p.getPrefs() ) ;
		assertEquals( m_prefs, p.getPrefs() ) ;
	}

	/**
	 * Exercises {@link PreferencePortal#useIntegerDefaultValue} and its effects
	 * on {@link PreferencePortal#getInt}.
	 */
	@Test
	public void testIntegerDefaultControl()
	{
		PreferencePortal p = new PreferencePortal(m_ctx) ;
		final String sPrefKey = UUID.randomUUID().toString() ;
		assertEquals( 0, p.getInt( sPrefKey ) ) ;
		p.useIntegerDefaultValue( PreferencePortal.DEFAULT_INT_NOT_SET ) ;
		assertEquals( -1, p.getInt( sPrefKey ) ) ;
	}

	/** Exercises the various put and get methods of {@link PreferencePortal}. */
	@Test
	public void testPutsAndGets()
	{
		PreferencePortal p = new PreferencePortal(m_ctx) ;
		final String sPrefKey = UUID.randomUUID().toString() ;
		p.putBoolean( sPrefKey, true ).commit() ;
		assertTrue( p.getBoolean( sPrefKey ) ) ;
		p.putInt( sPrefKey, 50 ).commit() ;
		assertEquals( 50, p.getInt( sPrefKey ) ) ;
		p.putStringifiedInt( sPrefKey, 1987 ).commit() ;
		assertEquals( 1987, p.getStringifiedInt( sPrefKey ) ) ;
		p.putLong( sPrefKey, 321654987321654987L ).commit() ;
		assertEquals( 321654987321654987L, p.getLong( sPrefKey ) ) ;
		p.putStringifiedLong( sPrefKey, 789456123789456123L ).commit() ;
		assertEquals( 789456123789456123L, p.getStringifiedLong( sPrefKey ) ) ;
		p.putString( sPrefKey, "qarnfarglebarg" ).commit() ;
		assertEquals( "qarnfarglebarg", p.getString( sPrefKey ) ) ;
	}
}
