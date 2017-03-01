package net.zerobandwidth.android.lib.database.querybuilder;

import net.zerobandwidth.android.lib.database.SQLitePortal;

/**
 * Represents a conditional statement in a SQLite {@code WHERE} clause.
 * @since zerobandwidth-net/android 0.1.1 (#20)
 * @deprecated (experimental and incomplete)
 */
@SuppressWarnings( "unused" )                              // This is a library.
public class Condition
{
	public static final String OPERATOR_EQUALS = "=" ;

	public static final String OPERATOR_GREATER_THAN = ">" ;

	public static final String OPERATOR_GREATER_OR_EQUAL = ">=" ;

	public static final String OPERATOR_LESS_THAN = "<" ;

	public static final String OPERATOR_LESS_OR_EQUAL = "<=" ;

	public static final String OPERATOR_NOT_EQUALS = "<>" ;

	public static final String VALUE_PLACEHOLDER_NULL = "NULL" ;

	public static final String OPERATOR_IS_NULL = " IS " ;

	public static final String OPERATOR_IS_NOT_NULL = " IS NOT " ;

	public static Condition equals( String sColumnName, String sValue )
	{
		if( sValue == null )
			return Condition.isNull( sColumnName ) ;
		else
			return new Condition( sColumnName, OPERATOR_EQUALS, sValue ) ;
	}

	public static Condition equals( String sColumnName, boolean bValue )
	{
		return Condition.equals( sColumnName,
				Integer.toString( SQLitePortal.boolToInt(bValue) ) ) ;
	}

	public static Condition equals( String sColumnName, int zValue )
	{ return Condition.equals( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition equals( String sColumnName, long zValue )
	{ return Condition.equals( sColumnName, Long.toString(zValue) ) ; }

	public static Condition equals( String sColumnName, double rValue )
	{ return Condition.equals( sColumnName, Double.toString(rValue) ) ; }

	public static Condition notEquals( String sColumnName, String sValue )
	{
		if( sValue == null )
			return Condition.isNotNull( sColumnName ) ;
		else
			return new Condition( sColumnName, OPERATOR_NOT_EQUALS, sValue ) ;
	}

	public static Condition notEquals( String sColumnName, boolean bValue )
	{
		return Condition.notEquals( sColumnName,
				Integer.toString( SQLitePortal.boolToInt(bValue) ) ) ;
	}

	public static Condition notEquals( String sColumnName, int zValue )
	{ return Condition.notEquals( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition notEquals( String sColumnName, long zValue )
	{ return Condition.notEquals( sColumnName, Long.toString(zValue) ) ; }

	public static Condition notEquals( String sColumnName, double rValue )
	{ return Condition.notEquals( sColumnName, Double.toString(rValue) ) ; }

	public static Condition isGreaterThan( String sColumnName, String sValue )
	{ return new Condition( sColumnName, OPERATOR_GREATER_THAN, sValue ) ; }

	public static Condition isGreaterThan( String sColumnName, int zValue )
	{ return Condition.isGreaterThan( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition isGreaterThan( String sColumnName, long zValue )
	{ return Condition.isGreaterThan( sColumnName, Long.toString(zValue) ) ; }

	public static Condition isGreaterThan( String sColumnName, double rValue )
	{ return Condition.isGreaterThan( sColumnName, Double.toString(rValue) ) ; }

	public static Condition isGreaterOrEquals( String sColumnName, String sValue )
	{ return new Condition( sColumnName, OPERATOR_GREATER_OR_EQUAL, sValue ) ; }

	public static Condition isGreaterOrEquals( String sColumnName, int zValue )
	{ return Condition.isGreaterOrEquals( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition isGreaterOrEquals( String sColumnName, long zValue )
	{ return Condition.isGreaterOrEquals( sColumnName, Long.toString(zValue) ) ; }

	public static Condition isGreaterOrEquals( String sColumnName, double rValue )
	{ return Condition.isGreaterOrEquals( sColumnName, Double.toString(rValue) ) ; }

	public static Condition isLessThan( String sColumnName, String sValue )
	{ return new Condition( sColumnName, OPERATOR_LESS_THAN, sValue ) ; }

	public static Condition isLessThan( String sColumnName, int zValue )
	{ return Condition.isLessThan( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition isLessThan( String sColumnName, long zValue )
	{ return Condition.isLessThan( sColumnName, Long.toString(zValue) ) ; }

	public static Condition isLessThan( String sColumnName, double rValue )
	{ return Condition.isLessThan( sColumnName, Double.toString(rValue) ) ; }

	public static Condition isLessOrEquals( String sColumnName, String sValue )
	{ return new Condition( sColumnName, OPERATOR_LESS_OR_EQUAL, sValue ) ; }

	public static Condition isLessOrEquals( String sColumnName, int zValue )
	{ return Condition.isLessOrEquals( sColumnName, Integer.toString(zValue) ) ; }

	public static Condition isLessOrEquals( String sColumnName, long zValue )
	{ return Condition.isLessOrEquals( sColumnName, Long.toString(zValue) ) ; }

	public static Condition isLessOrEquals( String sColumnName, double rValue )
	{ return Condition.isLessOrEquals( sColumnName, Double.toString(rValue) ) ; }

	public static Condition isNull( String m_sColumnName )
	{
		return new Condition( m_sColumnName,
				OPERATOR_IS_NULL, VALUE_PLACEHOLDER_NULL ) ;
	}

	public static Condition isNotNull( String m_sColumnName )
	{
		return new Condition( m_sColumnName,
				OPERATOR_IS_NOT_NULL, VALUE_PLACEHOLDER_NULL ) ;
	}

	protected String m_sColumnName = null ;
	protected String m_sOperator = null ;
	protected String m_sValue = null ;

	/**
	 * Full initializer of the condition.
	 * @param sColumnName the name of the column to be checked
	 * @param sOperator the comparison operator
	 * @param sValue the value to be checked
	 */
	public Condition( String sColumnName, String sOperator, String sValue )
	{
		m_sColumnName = sColumnName ;
		m_sOperator = sOperator ;
		m_sValue = sValue ;
	}

	/**
	 * Renders the condition as it should appear in a SQLite {@code WHERE}
	 * clause.
	 * @return the condition as it would appear in a {@code WHERE} clause
	 */
	@Override
	public String toString()
	{
		return (new StringBuilder())
			.append( m_sColumnName )
			.append( m_sOperator )
			.append( m_sValue )
			.toString()
			;
	}
}
