package net.zerobandwidth.android.lib.database.sqlitehouse.refractor;

import android.content.ContentValues;
import android.support.test.runner.AndroidJUnit4;

import net.zerobandwidth.android.lib.database.MockCursor;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_NULL ;
import static net.zerobandwidth.android.lib.database.SQLiteSyntax.SQLITE_TYPE_TEXT ;

/**
 * Exercises the canonical implementations of {@link StringCollectionLens},
 * which in turn exercise the concrete methods provided by the abstract base
 * class.
 * @since zerobandwidth-net/android 0.1.5 (#42)
 */
@RunWith( AndroidJUnit4.class )
public class StringListLensesTest
{
	protected static ArrayList<String> getTestList()
	{
		ArrayList<String> asValues = new ArrayList<>() ;
		Collections.addAll( asValues, "foo", "bar", "baz" ) ;
		return asValues ;
	}

	/**
	 * Exercises {@link CommaDelimStringsListLens}.
	 */
	@SuppressWarnings( "deprecation" ) // check deprecated stuff still works
	@Test
	public void testCommaDelimArrayList()
	{
		CommaDelimStringsListLens lens = new CommaDelimStringsListLens() ;

		final ArrayList<String> asTestValues = getTestList() ;

		assertNull( lens.toStringValue(null) ) ;
		assertEquals( "foo,bar,baz", lens.toStringValue( asTestValues ) ) ;

		assertEquals( SQLITE_TYPE_TEXT, lens.getSQLiteDataType() );

		assertEquals( SQLITE_NULL, lens.toSQLiteString(null) ) ;
		assertEquals( "'foo,bar,baz'", lens.toSQLiteString( asTestValues ) ) ;

		ContentValues vals = new ContentValues() ;
		lens.addToContentValues( vals, "test_null", null ) ;
		assertNull( vals.get("test_null") ) ;
		lens.addToContentValues( vals, "test_nonnull", asTestValues ) ;
		assertEquals( "foo,bar,baz", vals.get("test_nonnull") ) ;

		MockCursor crs = new MockCursor( vals ) ;
		crs.moveToFirst() ;
		assertNull( lens.fromCursor( crs, "test_null" ) ) ;
		ArrayList<String> asReturned = lens.fromCursor( crs, "test_nonnull" ) ;
		assertEquals( asTestValues.size(), asReturned.size() ) ;
		for( int n = 0 ; n < asTestValues.size() ; n++ )
			assertEquals( asTestValues.get(n), asReturned.get(n) ) ;
	}

	/**
	 * Exercises {@link FormFeedDelimStringsListLens}.
	 */
	@SuppressWarnings( "deprecation" ) // check deprecated stuff still works
	@Test
	public void testFormFeedDelimArrayList()
	{
		FormFeedDelimStringsListLens lens = new FormFeedDelimStringsListLens() ;

		final ArrayList<String> asTestValues = getTestList() ;

		assertNull( lens.toStringValue(null) ) ;
		assertEquals( "foo\fbar\fbaz", lens.toStringValue( asTestValues ) ) ;

		assertEquals( SQLITE_TYPE_TEXT, lens.getSQLiteDataType() ) ;

		assertEquals( SQLITE_NULL, lens.toSQLiteString(null) ) ;
		assertEquals( "'foo\fbar\fbaz'", lens.toSQLiteString( asTestValues ) ) ;

		ContentValues vals = new ContentValues() ;
		lens.addToContentValues( vals, "test_null", null ) ;
		assertNull( vals.get("test_null") ) ;
		lens.addToContentValues( vals, "test_nonnull", asTestValues ) ;
		assertEquals( "foo\fbar\fbaz", vals.get("test_nonnull") ) ;

		MockCursor crs = new MockCursor( vals ) ;
		crs.moveToFirst() ;
		assertNull( lens.fromCursor( crs, "test_null" ) ) ;
		ArrayList<String> asReturned = lens.fromCursor( crs, "test_nonnull" ) ;
		assertEquals( asTestValues.size(), asReturned.size() ) ;
		for( int n = 0 ; n < asTestValues.size() ; n++ )
			assertEquals( asTestValues.get(n), asReturned.get(n) ) ;
	}

}
