package rojira.jsi4.modules.utest;

import java.util.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;


public class UTestSuite
{
	private UTester tester;

	private final ArrayList<UTester> testers = new ArrayList<UTester>();

	private ResultVerifier result_verifier;

/*
	private int defined;

	private int valid;

	private int invalid;

	private int run;

	private int pass;

	private int fail;

	private int error;
*/

	private boolean quiet;


	public UTestSuite()
	{
		result_verifier = new ResultVerifier( this );
	}


	public void clear_tests()
	{
		testers.clear();

		tester = null;
	}


	public void set_target( Object target )
	{
		check( target != null ).on_fail().raise_arg( "Target Object can't be null" );

		tester = new UTester();

		testers.add( tester );

		tester.target_object = target;

		tester.target_class = target.getClass();
	}


	public <T extends Class> void set_target_class( T target_class )
	{
		check( target_class != null ).on_fail().raise_arg( "target_class can't be null" );

		tester = new UTester();

		testers.add( tester );

		tester.target_object = target_class;

		tester.target_class = target_class.getClass();
	}


	public void set_target( Class target )
	{
		check( target != null ).on_fail().raise_arg( "Target Class can't be null" );

		tester = new UTester();

		testers.add( tester );

		tester.target_object = null;

		tester.target_class = target;
	}


	public void create_target( Class target, Object... constructor_args )
	{
		check( target != null ).on_fail().raise_arg( "Target Class can't be null" );

		throw new IllegalStateException( "Not Implemented" );

		//this.target_object = null;

		//this.target_class = target;
	}


	public UTestSuite quiet()
	{
		this.quiet = true;

		return this;
	}


	public UTest test_method( String method )
	{
		check( tester != null ).on_fail().raise_arg( "Must set a target Object or Class before adding test methods" );

		String trace = trace_call( 3, "%f:%l" );

		UTest test = new UTest( tester.target_class, tester.target_object, method, trace );

		tester.tests.add( test );

		tester.defined ++;

		return test;
	}


	public int run_tests()
	{
		cverbose.println( "num testers: %d", testers.size() );

		for( UTester tester : testers )
		{
			cverbose.println( "   num tests: %d", tester.tests.size() );

			for( UTest test : tester.tests )
			{
				//cout.println( "Test outcome: %s", test.run().result().info() );

				test.run();

				cdebug.println( test.result.info() );

				UTestResultState state = test.result.result_state();

				if( state == UTestResultState.INIT_ERROR )
				{
					tester.invalid ++;

					if( ! quiet ) cout.print( "X" );

					continue;
				}

				tester.valid ++;

				if( state == UTestResultState.RUN_ERROR )
				{
					tester.error ++;

					if( ! quiet ) cout.print( "X" );
				}
				else if( state == UTestResultState.RUN_PASSED )
				{
					tester.pass ++;

					if( ! quiet ) cout.print( "." );
				}
				else if( state == UTestResultState.RUN_FAILED )
				{
					tester.fail ++;

					if( ! quiet ) cout.print( "x" );
				}
			}

			boolean no_problems = tester.invalid + tester.fail + tester.error == 0;

			if( ! quiet )
			{
				String result = null;

				if( no_problems )
				{
					result = "OK";
				}
				else
				{
					result = "PROBLEMS";
				}

				cout.println();

				cout.println( "UTests for:     %s", tester.target_class.getName() );

				cout.println( "  Defined:      %d", tester.defined );

				cout.println( "    Invalid:    %d", tester.invalid );

				cout.println( "    Valid:      %d", tester.valid );

				cout.println( "      Passed:   %d", tester.pass );

				cout.println( "      Failed:   %d", tester.fail );

				cout.println( "      Errors:   %d", tester.error );

				cout.println( "  Result:       %s", result );

				show_problems( tester );
			}
		}

		int problem_count = problem_count();

		if( ! quiet )
		{
			String result = null;

			if( problem_count == 0 )
			{
				result = "OK";
			}
			else
			{
				result = fmt( "ERROR: %d problems", problem_count );
			}

			cout.println( "\nTest Suite Results: %s", result );
		}

		return problem_count;
	}


	public void show_problems()
	{
		for( UTester tester : testers )
		{
			show_problems( tester );
		}
	}


	public void show_problems( UTester tester )
	{
		for( UTest test : tester.tests )
		{
			UTestResultState state = test.result.result_state();

			if( state != UTestResultState.RUN_PASSED )
			{
				cout.println( test.result.info() );
			}
		}
	}


	public int defined_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.defined;

		return count;
	}


	public int valid_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.valid;

		return count;
	}


	public int invalid_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.invalid;

		return count;
	}


	public int run_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.run;

		return count;
	}


	public int pass_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.pass;

		return count;
	}


	public int fail_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.fail;

		return count;
	}


	public int error_count()
	{
		int count = 0;

		for( UTester tester : testers ) count += tester.error;

		return count;
	}


	public int problem_count()
	{
		int count = 0;

		for( UTester tester : testers )
		{
			count += tester.invalid;

			count += tester.error;

			count += tester.fail;
		}

		return count;
	}


	public ResultVerifier check_result( UTestResult result )
	{
		result_verifier.set_result( result );

		return result_verifier;
	}
}


class UTester
{
	Object target_object;

	Class target_class;

	final ArrayList<UTest> tests = new ArrayList<UTest>();

	int defined;

	int valid;

	int invalid;

	int run;

	int pass;

	int fail;

	int error;
}
