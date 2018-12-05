package net.zer0bandwidth.android.lib.security;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import net.zer0bandwidth.android.lib.test.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Exercises parts of {@link PermissionCheckpoint}.
 * @since zer0bandwidth-net/android 0.2.1 (#53)
 */
@RunWith( AndroidJUnit4.class )
public class PermissionCheckpointTest
{
	/**
	 * Used to test constructors and methods that depend on an {@link Activity}.
	 */
	private class TestActivity extends Activity
	{}

	/**
	 * Used to test constructors and methods that depend on an
	 * {@link AppCompatActivity}.
	 */
	private class TestAppCompatActivity extends AppCompatActivity
	{}

	/** A test execution context. */
	private Context m_ctx ;

	/**
	 * The rights to check by default.
	 * Initialized in {@link #setup()}.
	 * @see PermissionCheckpoint#DEFAULT_DANGER_LIST_RESOURCE
	 * @see R.array#asDangerousPermissionsRequired
	 */
	private String[] m_asDefaultRights ;

	/**
	 * A selection of Android permissions to be tested in this class.
	 * Initialized in {@link #setup()}.
	 * @see R.array#asTestableListOfDangerousPermissions
	 */
	private String[] m_asTestableRights ;

	@Before
	public void setup()
	{
		m_ctx = InstrumentationRegistry.getTargetContext() ;
		m_asDefaultRights = m_ctx.getResources().getStringArray(
				PermissionCheckpoint.DEFAULT_DANGER_LIST_RESOURCE ) ;
		m_asTestableRights = m_ctx.getResources().getStringArray(
				R.array.asTestableListOfDangerousPermissions ) ;
	}

	/**
	 * Exercises {@link PermissionCheckpoint#checkPermission(Context, String)}.
	 */
	@Test
	public void testCheckPermission()
	{
		boolean bExpect = Settings.System.canWrite(m_ctx) ;
		assertEquals( bExpect, PermissionCheckpoint.checkPermission(
				m_ctx, PermissionCheckpoint.PERMISSION_WRITE_SETTINGS ) ) ;
		for( String sRight : m_asTestableRights )
		{
			bExpect = ( m_ctx.checkSelfPermission( sRight )
					== PackageManager.PERMISSION_GRANTED ) ;
			assertEquals( bExpect,
					PermissionCheckpoint.checkPermission( m_ctx, sRight ) ) ;
		}
	}

	/**
	 * Exercises all the various constructor flavors for "modern"
	 * {@link Activity}s, and, by extension,
	 * {@link PermissionCheckpoint#init(int)} and
	 * {@link PermissionCheckpoint#isContextAppCompat()}.
	 * <b>This test is currently disabled.</b>
	 * TODO (#57): Figure out how to make activity-dependent tests work.
	 */
//	@Test
	public void testModernConstructors()
	{
		Handler hndl = new Handler( Looper.getMainLooper() )
		{
			@Override
			public void handleMessage( Message msg )
			{
				TestActivity act = new TestActivity() ;

				PermissionCheckpoint pchk = new PermissionCheckpoint( act ) ;
				assertTrue( pchk.m_actContext == act ) ;
				assertNull( pchk.m_actCompatContext ) ;
				assertEquals( m_asDefaultRights.length,
						pchk.m_mapGranted.size() ) ;
				assertFalse( pchk.isContextAppCompat() ) ;

				pchk = new PermissionCheckpoint( act,
						R.array.asTestableListOfDangerousPermissions) ;
				assertTrue( pchk.m_actContext == act ) ;
				assertNull( pchk.m_actCompatContext ) ;
				assertEquals( m_asTestableRights.length,
						pchk.m_mapGranted.size() ) ;
				assertFalse( pchk.isContextAppCompat() ) ;
			}
		};
		hndl.obtainMessage().sendToTarget() ;
	}

	/**
	 * Exercises all the various constructor flavors for
	 * {@link AppCompatActivity}s, and, by extension,
	 * {@link PermissionCheckpoint#init(int)} and
	 * {@link PermissionCheckpoint#isContextAppCompat()}.
	 * <b>This test is currently disabled.</b>
	 * TODO (#57): Figure out how to make activity-dependent tests work.
	 */
	//@Test
	public void testCompatConstructors()
	{
		TestAppCompatActivity act = new TestAppCompatActivity() ;

		PermissionCheckpoint pchk = new PermissionCheckpoint( act ) ;
		assertTrue( pchk.m_actCompatContext == act ) ;
		assertNull( pchk.m_actContext ) ;
		assertEquals( m_asDefaultRights.length, pchk.m_mapGranted.size() ) ;
		assertTrue( pchk.isContextAppCompat() ) ;

		pchk = new PermissionCheckpoint( act,
				R.array.asTestableListOfDangerousPermissions) ;
		assertTrue( pchk.m_actCompatContext == act ) ;
		assertNull( pchk.m_actContext ) ;
		assertEquals( m_asTestableRights.length, pchk.m_mapGranted.size() ) ;
		assertTrue( pchk.isContextAppCompat() ) ;
	}
}
