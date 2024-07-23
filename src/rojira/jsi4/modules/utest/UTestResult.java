package rojira.jsi4.modules.utest;


import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibDevel.*;

import rojira.jsi4.util.system.*;
import rojira.jsi4.util.text.*;


public class UTestResult
{
	final UTest test;

	UTestResult( UTest test )
	{
		this.test = test;
	}


	public UTestResultState result_state()
	{
		if( test.init_error != null )
		{
			return UTestResultState.INIT_ERROR;
		}

		if( test.run_error != null )
		{
			return UTestResultState.RUN_ERROR;
		}

		if( test.expected_result_type == ExpectedResultType.IGNORE )
		{
			return UTestResultState.RUN_PASSED;
		}

		if( test.expected_result_type == ExpectedResultType.VOID )
		{
			ASSERT( test.expected_result == Void.TYPE );

			if( test.actual_result == Void.TYPE )
			{
				return UTestResultState.RUN_PASSED;
			}

			return UTestResultState.RUN_FAILED;
		}

		if( test.expected_result_type == ExpectedResultType.CLASS )
		{
			if( test.actual_result == null ) return UTestResultState.RUN_FAILED;

			Class actual_class = test.actual_result.getClass();

			Class expected_class = (Class) test.expected_result;

			if( inherited_from( actual_class, expected_class ) )
			{
				return UTestResultState.RUN_PASSED;
			}

			return UTestResultState.RUN_FAILED;
		}

		if( test.expected_result_type == ExpectedResultType.ERROR )
		{
			Class actual_class = test.actual_result.getClass();

			if( inherited_from( actual_class, (Class) test.expected_result ) )
			{
				return UTestResultState.RUN_PASSED;
			}

			return UTestResultState.RUN_FAILED;
		}

		if( test.expected_result_type == ExpectedResultType.OBJECT )
		{
			if( equal( test.expected_result, test.actual_result, NullsPolicy.EQUAL ) )
			{
				return UTestResultState.RUN_PASSED;
			}

			return UTestResultState.RUN_FAILED;
		}

		throw new IllegalStateException( fmt( "Unknown expected_result_type: %s", test.expected_result_type ) );
	}


	public String info()
	{
		EString out = new EString();

		out.println( "Test: %s", test.trace );

		out.println( "  Method: %s", test.expected_method_info );

		UTestResultState state = result_state();

		if( state == UTestResultState.INIT_ERROR )
		{
			out.println( "  ERROR: Test Initialisation Error: %s", test.init_error.getMessage() );

			out.println( "    Trace: %s", retrace( test.init_error ) );

			return out.toString().trim();
		}
		else if( state == UTestResultState.RUN_ERROR )
		{
			out.println( "  ERROR: Test Execution Error: %s", test.run_error.getMessage() );

			//out.println( "    Trace: %s", retrace( test.run_error ) );

			out.println( "    Trace: %s", trace( test.run_error ) );

			return out.toString().trim();
		}
		else
		{
			if( state == UTestResultState.RUN_PASSED )
			{
				out.println( "  PASSED:" );

	/*
				if( test.result_error != null )
				{
					out.println( "    Expected: Error: '%s'", test.expected_error_type );

					out.println( "    Actual: Error: '%s'", test.result_error.getClass() );
				}
				else
				{
					out.println( "    Expected: Result: '%s'", test.expected_result );

					out.println( "    Actual: Result: '%s'", test.actual_result );
				}
	*/
			}
			else
			{
				ASSERT( state == UTestResultState.RUN_FAILED );

				out.println( "  FAILED:" );

	/*
				if( test.result_error != null )
				{
					if( test.expected_error_type != null )
					{
						out.println( "    Expected: Error: '%s'", test.expected_error_type );
					}
					else
					{
						out.println( "    Expected: Result: '%s'", test.expected_result );
					}

					out.println( "    Actual: Error: '%s'", test.result_error.getClass() );

					out.println( "      Trace: %s", (Object) test.result_error.getStackTrace() );
				}
				else
				{
					if( test.expected_error_type != null )
					{
						out.println( "    Expected: Error: '%s'", test.expected_error_type );
					}
					else
					{
						out.println( "    Expected: Result: '%s'", test.expected_result );
					}

					out.println( "    Actual: Result: '%s'", test.actual_result );
				}
	*/
			}

			out.println( "    Expected: Result: '%s'", test.expected_result );

			out.println( "    Actual: Result: '%s'", test.actual_result );

		}

		return out.toString().trim();
	}
}


enum UTestResultState
{
	INIT_ERROR, RUN_ERROR, RUN_PASSED, RUN_FAILED;
}
