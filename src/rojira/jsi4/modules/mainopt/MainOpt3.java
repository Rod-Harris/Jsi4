package rojira.jsi4.modules.mainopt;


import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cinfo;
import static rojira.jsi4.LibConsole.cstream_level;
import static rojira.jsi4.LibConsole.cverbose;


public class MainOpt3 extends MainOpt2
{
	protected static Flag none_flag;

	protected static Flag info_flag;

	protected static Flag debug_flag;

	protected static Flag verbose_flag;

	protected static Flag usage_flag;

	protected static Flag version_flag;


	public static void initialise( Class main_class, String[] args ) throws Throwable
	{
		MainOpt2.initialise( main_class, args );

		MainOpt3.add_flags();
	}


	protected static void add_flags() throws Throwable
	{
		none_flag = add_flag( "--", "-n", "--none" ).set_description( "Set output level to none" );

		info_flag = add_flag( "-i", "--info" ).set_description( "Set output level to info" );

		debug_flag = add_flag( "-d", "--debug" ).set_description( "Set output level to debug" );

		verbose_flag = add_flag( "-v", "--verbose" ).set_description( "Set output level to verbose" );

		usage_flag = add_flag( "-h", "-u", "-?", "--help", "--usage" ).set_description( "Show application usage" );

		version_flag = add_flag( "-V", "--version" ).set_description( "Print version info" );

		mutex_group( none_flag, info_flag, debug_flag, verbose_flag );

		mutex_group( none_flag, usage_flag );
	}


	public static void check_flags() throws Throwable
	{
		check_flags( true );
	}


	public static void check_flags( boolean check_expected ) throws Throwable
	{
		MainOpt2.check_flags( check_expected );

		if( usage_flag.is_present() ) usage( 0 );

		if( version_flag.is_present() ) version( 0 );

		if( none_flag.is_present() ) cstream_level( null );

		if( info_flag.is_present() ) cstream_level( cinfo );

		if( debug_flag.is_present() ) cstream_level( cdebug );

		if( verbose_flag.is_present() ) cstream_level( cverbose );
	}
}
