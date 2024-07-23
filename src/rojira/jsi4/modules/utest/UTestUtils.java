package rojira.jsi4.modules.utest;


import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibConsole.cwarn;
import static rojira.jsi4.LibSystem.empty;
import static rojira.jsi4.LibSystem.equal;
import static rojira.jsi4.LibSystem.mtrace;
import static rojira.jsi4.LibSystem.same;
import static rojira.jsi4.LibText.empty;
import static rojira.jsi4.LibText.fmt;

import rojira.jsi4.util.system.NullsPolicy;


public class UTestUtils
{
	protected UTestUtils(){}


	public static void assert_true( boolean b )
	{
		cverbose.println( mtrace( b ) );

		assert_true( b, null );
	}


	public static void assert_true( boolean b, String error_message )
	{
		cverbose.println( mtrace( b, error_message ) );

		if( b )
		{
			cverbose.println( "Test Assertion (True) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (True) Failed" );

		if( error_message == null ) error_message = "Assertion (true) failed: Argument is false";

		throw new IllegalStateException( error_message );
	}



	public static void assert_false( boolean b )
	{
		cverbose.println( mtrace( b ) );

		assert_false( b, null );
	}


	public static void assert_false( boolean b, String error_message )
	{
		cverbose.println( mtrace( b, error_message ) );

		if( ! b )
		{
			cverbose.println( "Test Assertion (False) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (False) Failed" );

		if( error_message == null ) error_message = "Assertion (false) failed: Argument is true";

		throw new IllegalStateException( error_message );
	}


	public static void assert_same( Object o1, Object o2 )
	{
		cverbose.println( mtrace( o1, o2 ) );

		assert_same( o1, o2, null );
	}


	public static void assert_same( Object o1, Object o2, String error_message )
	{
		cverbose.println( mtrace( o1, o2, error_message ) );

		if( same( o1, o2 ) )
		{
			cverbose.println( "Test Assertion (Same) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (Same) Failed" );

		if( error_message == null ) error_message = "Assertion (same) failed: Object Handles are different";

		throw new IllegalStateException( error_message );
	}


	public static void assert_equal( Object o1, Object o2 )
	{
		cverbose.println( mtrace( o1, o2 ) );

		assert_equal( o1, o2, false, null );
	}


	/**
	@param o1 expected value
	@param o2 actual value
	*/
	public static void assert_equal( Object o1, Object o2, boolean nulls_equal, String error_message )
	{
		cverbose.println( mtrace( o1, o2, nulls_equal, error_message ) );

		NullsPolicy nulls_policy = NullsPolicy.NOT_EQUAL;

		if( nulls_equal ) nulls_policy = NullsPolicy.EQUAL;

		if( equal( o1, o2, nulls_policy ) )
		{
			cverbose.println( "Test Assertion (Equal) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (Equal) Failed" );

		if( error_message == null ) error_message = fmt( "Assertion (equal) failed: Objects are different: expected '%s' actual '%s'", o1, o2 );

		throw new IllegalStateException( error_message );
	}


	public static void assert_not_equal( Object o1, Object o2 )
	{
		cverbose.println( mtrace( o1, o2 ) );

		assert_not_equal( o1, o2, false, null );
	}


	public static void assert_not_equal( Object o1, Object o2, boolean nulls_equal, String error_message )
	{
		cverbose.println( mtrace( o1, o2, nulls_equal, error_message ) );

		NullsPolicy nulls_policy = NullsPolicy.NOT_EQUAL;

		if( nulls_equal ) nulls_policy = NullsPolicy.EQUAL;

		if( ! equal( o1, o2, nulls_policy ) )
		{
			cverbose.println( "Test Assertion (Not Equal) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (Not Equal) Failed" );

		if( error_message == null ) error_message = "Assertion (not equal) failed: Objects are equal";

		throw new IllegalStateException( error_message );
	}


	public static void assert_not_null( Object o )
	{
		cverbose.println( mtrace( o ) );

		assert_not_null( o, null );
	}


	public static void assert_not_null( Object o, String error_message )
	{
		cverbose.println( mtrace( o, error_message ) );

		if( o != null )
		{
			cverbose.println( "Test Assertion (Not Null) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (Not Null) Failed" );

		if( error_message == null ) error_message = "Assertion (not null) failed: Object Handle is null";

		throw new IllegalStateException( error_message );
	}


	public static void assert_not_empty( String o )
	{
		cverbose.println( mtrace( o ) );

		assert_not_empty( o, null );
	}


	public static void assert_not_empty( String o, String error_message )
	{
		cverbose.println( mtrace( o, error_message ) );

		if( ! empty( o ) )
		{
			cverbose.println( "Test Assertion (String Not Empty) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (String Not Empty) Failed" );

		if( error_message == null ) error_message = "Assertion (string not empty) failed: String Handle is null or String contains no data";

		throw new IllegalStateException( error_message );
	}


	public static <T> void assert_not_empty( T[] o )
	{
		cverbose.println( mtrace( o ) );

		assert_not_empty( o, null );
	}


	public static <T> void assert_not_empty( T[] o, String error_message )
	{
		cverbose.println( mtrace( o, error_message ) );

		if( ! empty( o ) )
		{
			cverbose.println( "Test Assertion (Array Not Empty) Passed" );

			return;
		}

		cverbose.println( "Test Assertion (Array Not Empty) Failed" );

		if( error_message == null ) error_message = "Assertion (array not empty) failed: Array Handle is null or Array contains no entries";

		throw new IllegalStateException( error_message );
	}


	static Class autobox_type( Class primitive )
	{
		if( ! primitive.isPrimitive() )
		{
			cwarn.println( "Called %s on a non-primitive type: %s", mtrace(), primitive.getName() );

			return primitive;
		}

		if( primitive == byte.class )
		{
			return Byte.class;
		}

		if( primitive == short.class )
		{
			return Short.class;
		}

		if( primitive == int.class )
		{
			return Integer.class;
		}

		if( primitive == long.class )
		{
			return Long.class;
		}

		if( primitive == float.class )
		{
			return Float.class;
		}

		if( primitive == double.class )
		{
			return Double.class;
		}

		if( primitive == boolean.class )
		{
			return Boolean.class;
		}

		if( primitive == char.class )
		{
			return Character.class;
		}

		throw new IllegalStateException( "Is there a new Java primitive type? => " + primitive.getName() );

	}
}
