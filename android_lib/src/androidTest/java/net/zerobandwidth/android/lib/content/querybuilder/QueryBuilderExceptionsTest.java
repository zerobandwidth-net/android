package net.zerobandwidth.android.lib.content.querybuilder;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Exercises the exceptions defined as inner classes in {@link net.zerobandwidth.android.lib.database.querybuilder.QueryBuilder}.
 * @since zerobandwidth-net/android 0.1.7 (#39)
 */
@RunWith( AndroidJUnit4.class )
public class QueryBuilderExceptionsTest
{
	/** Exercises {@link QueryBuilder.UnboundException}. */
	@Test
	public void testUnboundException()
	{
		QueryBuilder.UnboundException xMeta =
				new QueryBuilder.UnboundException(
						new QueryBuilder.UnboundException("Foo") ) ;
		assertEquals( QueryBuilder.UnboundException.DEFAULT_MESSAGE,
				xMeta.getMessage() ) ;
		assertEquals( "Foo", xMeta.getCause().getMessage() ) ;
		xMeta = new QueryBuilder.UnboundException( "Bar",
				new QueryBuilder.UnboundException() ) ;
		assertEquals( "Bar", xMeta.getMessage() ) ;
		assertEquals( QueryBuilder.UnboundException.DEFAULT_MESSAGE,
				xMeta.getCause().getMessage() ) ;
	}

	/** Exercises {@link QueryBuilder.ExecutionException}. */
	@Test
	public void testExecutionException()
	{
		assertEquals( "MockQueryBuilder execution failed.",
				(new QueryBuilder.ExecutionException( MockQueryBuilder.class ))
					.getMessage() ) ;

		QueryBuilder.ExecutionException xMeta =
				new QueryBuilder.ExecutionException( MockQueryBuilder.class,
						new QueryBuilder.ExecutionException( "Foo" ) ) ;
		assertEquals( "MockQueryBuilder execution failed.",
				xMeta.getMessage() ) ;
		assertEquals( "Foo", xMeta.getCause().getMessage() ) ;

		xMeta = new QueryBuilder.ExecutionException( "Bar",
				new QueryBuilder.ExecutionException() ) ;
		assertEquals( "Bar", xMeta.getMessage() ) ;
		assertEquals( QueryBuilder.ExecutionException.DEFAULT_MESSAGE,
				xMeta.getCause().getMessage() ) ;

		xMeta = new QueryBuilder.ExecutionException(
				new QueryBuilder.ExecutionException( "Baz" ) ) ;
		assertEquals( QueryBuilder.ExecutionException.DEFAULT_MESSAGE,
				xMeta.getMessage() ) ;
		assertEquals( "Baz", xMeta.getCause().getMessage() ) ;
	}
}
