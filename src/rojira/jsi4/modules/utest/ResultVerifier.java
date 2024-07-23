package rojira.jsi4.modules.utest;


import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;
import rojira.jsi4.util.text.*;

public class ResultVerifier
{
	UTestSuite suite;

	UTestResult result;

	int checks_run;

	int checks_passed;

	int checks_failed;


	public ResultVerifier( UTestSuite suite )
	{
		this.suite = suite;
	}


	public ResultVerifier set_result( UTestResult result )
	{
		this.result = result;

		return this;
	}


	public boolean test_ran()
	{
		checks_run ++;

		if( result.test.init_error != null )
		{
			checks_failed ++;

			cverbose.println( "test_ran failed: test.init_error != null" );

			return false;
		}

		if( result.test.run_error != null )
		{
			checks_failed ++;

			cverbose.println( "test_ran failed: test.run_error != null" );

			return false;
		}

		checks_passed ++;

		return true;
	}


	public boolean threw_nothing()
	{
		checks_run ++;

		if( result.test.actual_result == null )
		{
			checks_passed ++;

			return true;
		}

		Class actual_class = result.test.actual_result.getClass();

		if( inherited_from( actual_class, Throwable.class ) )
		{
			checks_failed ++;

			cverbose.println( "threw_nothing failed: test.actual_result threw " + actual_class );

			return false;
		}

		checks_passed ++;

		return true;
	}


	public <T extends Throwable> boolean threw_error_type( Class<T> error_type )
	{
		checks_run ++;

		if( result.test.actual_result == null )
		{
			checks_failed ++;

			cverbose.println( "threw_error_type failed: test.actual_result = null" );

			return false;
		}

		Class actual_class = result.test.actual_result.getClass();

		if( inherited_from( actual_class, error_type ) )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "threw_error_type (%s) failed: test.actual_result (type) = %s", error_type, actual_class );

		return false;
	}


	public boolean threw_error_with_message( String message )
	{
		checks_run ++;

		if( result.test.actual_result == null )
		{
			checks_failed ++;

			cverbose.println( "threw_error_with_message failed: test.actual_result = null" );

			return false;
		}

		Class actual_class = result.test.actual_result.getClass();

		if( inherited_from( actual_class, Throwable.class ) )
		{
			String error_message = ((Throwable) result.test.actual_result).getMessage();

			if( error_message.matches( message ) )
			{
				checks_passed ++;

				return true;
			}
			else
			{
				checks_failed ++;

				cverbose.println( "threw_error_with_message (%s) failed: test.actual_result (message) = %s", message, error_message );

				return false;
			}
		}

		checks_failed ++;

		cverbose.println( "threw_error_with_message (%s) failed: test.actual_result (type) = %s", message, actual_class );

		return false;
	}


	public boolean returned_type( Class expected )
	{
		checks_run ++;

		check( expected != null ).on_fail().raise_arg( "'expected' parameter is null" );

		if( result.test.actual_result == null )
		{
			checks_failed ++;

			cverbose.println( "returned_type (%s) failed: test.actual_result = null", expected );

			return false;
		}

		if( expected.isPrimitive() )
		{
			expected = UTestUtils.autobox_type( expected );
		}

		Class actual_class = result.test.actual_result.getClass();

		if( inherited_from( actual_class, expected ) )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "returned_type (%s) failed: test.actual_result.class = %s", expected, actual_class );

		return false;
	}


	public boolean returned_nothing()
	{
		checks_run ++;

		if( result.test.actual_result == Void.TYPE )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "returned_nothing failed: test.actual_result = %s", result.test.actual_result );

		return false;
	}


	public boolean returned_null()
	{
		checks_run ++;

		if( result.test.actual_result == null )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "returned_null failed: test.actual_result = %s", result.test.actual_result );

		return false;
	}


	public boolean equal_to( Object expected )
	{
		checks_run ++;

		check( expected != null ).on_fail().raise_arg( "'expected' parameter is null" );

		if( equal( result.test.actual_result, expected ) )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "equal_to (%s) failed: test.actual_result = %s", expected, result.test.actual_result );

		return false;
	}


	public boolean same_as( Object expected )
	{
		checks_run ++;

		check( expected != null ).on_fail().raise_arg( "'expected' parameter is null" );

		if( result.test.actual_result == expected )
		{
			checks_passed ++;

			return true;
		}

		checks_failed ++;

		cverbose.println( "same_as (#%x) failed: test.actual_result = #%x", System.identityHashCode( expected ), System.identityHashCode( result.test.actual_result ) );

		return false;
	}


	public String toString()
	{
		EString out = new EString();

		out.println( "checks_run: %d", checks_run );

		out.println( "checks_passed: %d", checks_passed );

		out.println( "checks_failed: %d", checks_failed );

		out.println( "check_errors: %d", checks_run - ( checks_passed +  checks_failed ) );

		return str( out );
	}
}
