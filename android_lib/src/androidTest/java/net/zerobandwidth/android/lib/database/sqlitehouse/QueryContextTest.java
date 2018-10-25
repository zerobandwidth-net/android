package net.zerobandwidth.android.lib.database.sqlitehouse;

//import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.sqlitehouse.exceptions.SchematicException;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.IntegerLens;
import net.zerobandwidth.android.lib.database.sqlitehouse.refractor.StringLens;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.Fargle;
import net.zerobandwidth.android.lib.database.sqlitehouse.testschema.ValidSpecClass;

//import org.junit.Test;
//import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Exercises {@link SQLiteHouse.QueryContext}.
 * Much of the class's code is already exercised by {@link SQLiteHouseTest};
 * this class completes coverage of the {@code QueryContext} class by targeting
 * specific methods not covered elsewhere.
 * @since zerobandwidth-net/android 0.1.7 (#50)
 * @deprecated zerobandwidth-net/android [NEXT] (#56) &mdash; This class will be
 *  removed entirely when {@link SQLiteHouse.QueryContext} is removed.
 */ // TODO (deprecation) remove in next major version
@SuppressWarnings({ "unused", "deprecation" })
public class QueryContextTest
{
	/**
	 * Exercises {@link SQLiteHouse.QueryContext#loadColumnDef(Field)}.
	 */
//	@Test // disabled zerobandwidth-net/android [NEXT] (#56)
	public void testLoadColumnDef()
	throws Exception // Any uncaught exception is a failure.
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SQLiteHouse.QueryContext<ValidSpecClass> qctx =
				dbh.getQueryContext().loadTableDef( Fargle.class ) ;
		assertNull( qctx.fldColumn ) ;
		qctx.loadColumnDef( ((Field)(null)) ) ;
		assertNull( qctx.fldColumn ) ;
		qctx.loadColumnDef( Fargle.class.getDeclaredField("m_sString") ) ;
		assertEquals( "m_sString", qctx.fldColumn.getName() ) ;
		assertNotNull( qctx.antColumn ) ;
		assertEquals( "fargle_string", qctx.antColumn.name() ) ;
		assertEquals( "fargle_string", qctx.sColumnName ) ;
		assertFalse( qctx.bColumnIsKey ) ;
		assertTrue( qctx.lens instanceof StringLens ) ;
		assertNull( qctx.sColumnSQLValue ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse.QueryContext#loadColumnDef(String)}.
	 */
//	@Test // disabled zerobandwidth-net/android [NEXT] (#56)
	public void testLoadColumnDefByName()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SQLiteHouse.QueryContext<ValidSpecClass> qctx =
				dbh.getQueryContext() ;
		IllegalStateException xState = null ;
		try { qctx.loadColumnDef( "m_sString" ) ; }
		catch( IllegalStateException x ) { xState = x ; }
		assertNotNull(xState) ;
		qctx.loadTableDef( Fargle.class ) ;
		qctx.loadColumnDef( ((String)(null)) ) ;
		assertNull( qctx.fldColumn ) ;
		qctx.loadColumnDef( "frangle" ) ;
		assertNull( qctx.fldColumn ) ; // There should also be a special log msg
		qctx.loadColumnDef( "fargle_num" ) ;
		assertEquals( "m_zInteger", qctx.fldColumn.getName() ) ;
		assertNotNull( qctx.antColumn ) ;
		assertEquals( "42", qctx.antColumn.sql_default() ) ;
		assertEquals( "fargle_num", qctx.sColumnName ) ;
		assertFalse( qctx.bColumnIsKey ) ;
		assertTrue( qctx.lens instanceof IntegerLens ) ;
		assertNull( qctx.sColumnSQLValue ) ;
	}

	/**
	 * Exercises {@link SQLiteHouse.QueryContext#loadColumnValue}.
	 */
//	@Test // disabled zerobandwidth-net/android [NEXT] (#56)
	public void testLoadColumnValue()
	{
		ValidSpecClass dbh = ValidSpecClass.getTestInstance() ;
		SQLiteHouse.QueryContext<ValidSpecClass> qctx =
				dbh.getQueryContext()
				   .loadTableDef( Fargle.class )
				   ;

		NullPointerException xNull = null ;
		try { qctx.loadColumnValue( null ) ; }
		catch( NullPointerException x ) { xNull = x ; }
		assertNotNull(xNull) ;                      // Don't accept null values.
		IllegalStateException xState = null ;
		Fargle fargle = new Fargle( 1, "Foo!", 79 ) ;
		try { qctx.loadColumnValue( fargle ) ; }
		catch( IllegalStateException x ) { xState = x ; }
		assertNotNull(xState) ;      // Can't continue when no column is chosen.
		SchematicException xSchema = null ;
		qctx.loadColumnDef( "fargle_num" ) ;
		qctx.lens = null ;    // Artificially blow away the refractor reference.
		try { qctx.loadColumnValue( fargle ) ; }
		catch( SchematicException x ) { xSchema = x ; }
		assertNotNull(xSchema) ;          // Can't continue without a refractor.
		qctx.loadColumnDef( "fargle_num" ) ; // Restore the refractor reference.
		assertEquals( "79", qctx.loadColumnValue(fargle).sColumnSQLValue ) ;
	}
}
