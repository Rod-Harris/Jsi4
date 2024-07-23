package rojira.jsi4.modules.mainopt;


import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.exit;
import static rojira.jsi4.LibText.fmt;

/**
 * <p> MainOpt2, MainOpt3 ArgParser and Flag replace KnownParam, KnownFlag and MainOpt which are now deprecated
 */
public class MainOpt2
{
	static Class main_class;

	static ArgParser ap;

	public static final int ANY_NUMBER = -1;


	public static void initialise( Class main_class, String[] args ) throws Throwable
	{
		MainOpt2.main_class = main_class;

		MainOpt2.parse_args( args );
	}


	protected static void parse_args( String[] args ) throws Throwable
	{
		check( ap == null ).on_fail().raise_state( "parse_args method has already been called" );

		ap = new ArgParser( args );
	}


	public static Flag add_flag( String... flags )
	{
		check( ap != null ).on_fail().raise_state( "parse_args method has not been called" );

		return ap.add_flag( flags );
	}


	public static Flag get_no_flag()
	{
		check( ap != null ).on_fail().raise_state( "parse_args method has not been called" );

		return ap.no_flag;
	}


	public static void mutex_group( Flag... flags ) throws Exception
	{
		check( ap != null ).on_fail().raise_state( "parse_args method has not been called" );

		ap.mutex_group( flags );
	}


	public static void check_flags() throws Throwable
	{
		check_flags( true );
	}


	public static void check_flags( boolean check_expected ) throws Throwable
	{
		check( ap != null ).on_fail().raise_state( "parse_args method has not been called" );

		ap.check_flags( check_expected );
	}


	public static void usage( Exception ex )
	{
		usage( ex, -1 );
	}


	public static void usage( int exit_code )
	{
		usage( null, exit_code );
	}


	public static void usage( Throwable error, int exit_code )
	{
		if( error != null )
		{
			System.err.println( "\nError:" );

			System.err.println( "\t" + error.getMessage() );
		}

		System.err.println( fmt( "\nUsage:" ) );

		System.err.println( fmt( "\tjava [java_opts] %s [options]", main_class.getName() ) );

		System.err.println( "\nOptions:" );

		System.err.println( ap.usage() );

		System.err.println( "Java Options:" );

		System.err.println( "\trun java with no args for a list of options" );

		System.err.println( "\tspecifically the [-cp, -classpath] option is useful" );

		System.err.println();

		if( exit_code < 1 ) exit( exit_code );
	}


	public static void version( int exit_code ) throws Throwable
	{
		ProjectInfo project_info = ProjectInfo.get_info( main_class );

		System.out.println( fmt( "%s %s", project_info.app_name, project_info.version_info ) );

		System.err.println( project_info.build_timestamp );

		if( exit_code < 1 ) exit( exit_code );
	}
}



