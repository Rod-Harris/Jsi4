package rojira.jsi4;

import rojira.jsi4.tests.*;
import rojira.jsi4.examples.*;

import rojira.jsi4.modules.mainopt.*;

import static rojira.jsi4.LibDevel.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibConsole.*;

/**
 * <p> This is the main executable class for Jsi4
 */
public class Main extends MainOpt3
{
	public static void main( String[] args )
	{
		try
		{
			MainOpt3.initialise( Main.class, args );

			Flag run_tests_opts = add_flag( "--test" ).set_description( "Run units tests" );

			Flag run_example_opts = add_flag( "--example" ).expects_params( "example" ).set_description( "Run specified example" ).add_to_description( "'?': list all examples" ).add_to_description( "'all': run all examples" );

			// add project specific flags here

			check_flags( false );

			if( run_tests_opts.is_present() )
			{
				Jsi4Tests.run_tests();
			}
			else if( run_example_opts.is_present() )
			{
				Jsi4Examples.run_example( run_example_opts.get_param( "example" ) );
			}
			else
			{
				NOT_IMPLEMENTED();
			}
		}
		catch( Throwable t )
		{
			cerr.println( t.getMessage() );

			cerr.println( retrace( t ) );

			exit( -1 );
		}
	}

	// protected Main(){}
}
