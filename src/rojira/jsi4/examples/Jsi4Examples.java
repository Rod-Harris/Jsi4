package rojira.jsi4.examples;

import rojira.jsi4.*;

import rojira.jsi4.util.system.*;
import rojira.jsi4.util.io.*;
import rojira.jsi4.modules.eou.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;


/**
 * <p> Example suite for Jsi4
 * <p> Class must be public, and all Example annotated methods must be public and static
 */
public final class Jsi4Examples
{
	/**
	 */
	public static void run_example( String example_name ) throws Throwable
	{
		Jsi4.run_example( Jsi4Examples.class, example_name );
	}


	@Example
	(
		value = "001",
		description = "creates a Jsi4 object"
	)
	public static void example001() throws Throwable
	{
		new Jsi4();
	}

	@Example
	(
		value = "002",
		description = "load conf resource for class"
	)
	public static void example002() throws Throwable
	{
		ModEaseOfUse.load_class_conf( LibSystem.class );

		cinfo.println( "PROP_NL: %s", LibSystem.PROP_NL() );

		cinfo.println( "PROP_USER_HOME: %s", LibSystem.PROP_USER_HOME() );

		cinfo.println( "PROP_USER_NAME: %s", LibSystem.PROP_USER_NAME() );

		cinfo.println( "PROP_OS_NAME: %s", LibSystem.PROP_OS_NAME() );

		cinfo.println( "PROP_USER_DIR: %s", LibSystem.PROP_USER_DIR() );

		cinfo.println( "PROP_TMP_DIR: %s", LibSystem.PROP_TMP_DIR() );
	}

	@Example
	(
		value = "003",
		description = "http get"
	)
	public static void example003() throws Throwable
	{
		WGet wget = new WGet( "http://www.google.com.au" );

		if( ! wget.retrieve_headers() )
		{
			cerr.println( retrace( wget.error() ) );
		}
		else
		{
			cout.println( "content_length: %d", wget.content_length() );

			cout.println( "content_type: %s", wget.content_type() );

			cout.println( "http_status_code: %d", wget.http_status_code() );

			cout.println( "http_response_message: %s", wget.http_response_message() );

			cout.println( "http_request_method: %s", wget.http_request_method() );
		}
	}


	@Example
	(
		value = "004",
		description = "method objects stream"
	)
	public static void example004() throws Throwable
	{

	}





	private Jsi4Examples(){}
}
