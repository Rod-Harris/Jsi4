package rojira.jsi4;

import rojira.jsi4.util.console.*;

import java.io.*;



//import static jsi3.lib.system.Statics.*;


public class LibConsole
{
	private static int print_level;


	/**
	 * <p> prints to std out and has no decorators
	 */
	public static final OStream cout = new OStream( System.out );

	/**
	 * <p> prints to std err and has no decorators
	 */
	public static final OStream calt = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 */
	public static final OStream cerr = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 */
	public static final OStream cwarn = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 */
	public static final OStream cinfo = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 */
	public static final OStream cdebug = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 */
	public static final OStream cverbose = new OStream( System.err );

	/**
	 * <p> prints to std err and has a name and trace decorator
	 * <p> cdevel is separate and can be turned on or off independantly
	 */
	public static final OStream cdevel = new OStream( System.err );


	private static final OStream[] cstreams =
	{
		cout,
		calt,
		cerr,
		cwarn,
		cinfo,
		cdebug,
		cverbose
	};

	private static boolean colour_output = false;

	private static boolean timestamp_output = false;


	static
	{
		cerr.add_decorators( new NameDecorator( "ERROR:   " ), new TraceDecorator( "%f:%l: " ) );

		cwarn.add_decorators( new NameDecorator( "WARN:    " ), new TraceDecorator( "%f:%l: " ) );

		cinfo.add_decorators( new NameDecorator( "INFO:    " ), new TraceDecorator( "%f:%l: " ) );

		cdebug.add_decorators( new NameDecorator( "DEBUG:   " ), new TraceDecorator( "%f:%l: " ) );

		cverbose.add_decorators( new NameDecorator( "VERBOSE: " ), new TraceDecorator( "%f:%l: " ) );

		cdevel.add_decorators( new NameDecorator( "DEVEL:   " ), new TraceDecorator( "%f:%l: " ) );

		cstream_level( cinfo );

		String cdevel_env_value = System.getenv( "CDEVEL" );

		cverbose.println( "CDEVEL env variable = '%s'", cdevel_env_value );

		if( cdevel_env_value == null || ! "ON".equals( cdevel_env_value ) )
		{
			cdevel.off();

			cverbose.println( "turning cdevel stream off" );

			cverbose.println( "assertions disabled" );
		}
		else
		{
			cdevel.on();

			cverbose.println( "turning cdevel stream on" );

			cverbose.println( "assertions enabled" );
		}
	}


	/**
	 * Inserts new colour decorators into the console streams:
	 * cerr: Red
	 * cwarn: Yellow
	 * cinfo: Green
	 * cdebug: Blue
	 * cverbose: Purple
	 */
	public static void colour_output()
	{
		if( colour_output ) return;

		colour_output = true;

		cerr.insert_decorator( new ColourDecorator( "Red" ) );

		cinfo.insert_decorator( new ColourDecorator( "Green" ) );

		cwarn.insert_decorator( new ColourDecorator( "Yellow" ) );

		cdebug.insert_decorator( new ColourDecorator( "Blue" ) );

		cverbose.insert_decorator( new ColourDecorator( "Purple" ) );
	}


	/**
	 * Inserts new timestamp decorators into each console stream (including cout and calt)
	 * dd/MM/YYYY hh:mm:ss >>
	 */
	public static void timestamp_output()
	{
		if( timestamp_output ) return;

		timestamp_output = true;

		cout.insert_decorator( new DateTimeDecorator() );

		calt.insert_decorator( new DateTimeDecorator() );

		cerr.insert_decorator( new DateTimeDecorator() );

		cinfo.insert_decorator( new DateTimeDecorator() );

		cwarn.insert_decorator( new DateTimeDecorator() );

		cdebug.insert_decorator( new DateTimeDecorator() );

		cverbose.insert_decorator( new DateTimeDecorator() );
	}


	public static void insert_decorator( StreamDecorator decorator, OStream... streams )
	{
		for( OStream stream : streams )
		{
			stream.insert_decorator( decorator );
		}
	}


	/**
	 * <p> Macro to set the level of console logging (ie. turn streams on of off)
	 * <p> levels include (in order):
	 * <p> [null]
	 * <p> cout
	 * <p> calt
	 * <p> cerr
	 * <p> cwarn
	 * <p> cinfo
	 * <p> cdebug
	 * <p> cverbose
	 * <p>
	 * <p> The default level is cinfo
	 * <p>    ie. cdebug and cverbose streams won't print
	 * <p>    ie. but cout, calt, cerr, cwarn and cinfo will
	 */
	public static void cstream_level( OStream cstream )
	{
		int level = get_stream_level( cstream );

		for( int i=0; i<cstreams.length; i++ )
		{
			if( i <= level )
			{
				cstreams[ i ].on();

				//System.out.printf( "turning stream %d on\n", i );
			}
			else
			{
				//System.out.printf( "turning stream %d off\n", i );

				cstreams[ i ].off();
			}
		}

		cverbose.println( "setting output level to %d", level );
	}


	private static int get_stream_level( OStream cstream )
	{
		if( cstream == null ) return -1;

		for( int i=0; i<cstreams.length; i++ )
		{
			if( cstream == cstreams[ i ] ) return i;
		}

		return -1;
	}


	/**
	 * prints prompt to screen (a question mark is not appended automatically) and reads input from System.in
	*/
	public static String input( String fmt, Object... args ) throws IOException
	{
		System.out.print( String.format( fmt, args ) );

		BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

		String input = in.readLine();

		return input;
	}


	public static void alert( String msg_fmt, Object... args )
	{
		calt.println();

		calt.println( "********************************************************************************" );
		calt.println( "***********************************  ALERT  ************************************" );
		calt.println( "********************************************************************************" );
		calt.println( "***                                                                          ***" );
		calt.println( msg_fmt, args );
		calt.println( "***                                                                          ***" );
		calt.println( "********************************************************************************" );
		calt.println( "***********************************  ALERT  ************************************" );
		calt.println( "********************************************************************************" );
	}


	private LibConsole(){};
}
