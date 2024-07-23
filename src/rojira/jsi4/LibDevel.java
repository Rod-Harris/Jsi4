package rojira.jsi4;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cdevel;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.trace_call;
import static rojira.jsi4.LibText.fmt;

import rojira.jsi4.util.text.EString;


public class LibDevel
{
	private LibDevel(){}


	public static <T> T NOT_NULL( T reference_to_check )
	{
		// say you do ...

		check( reference_to_check != null ).on_fail().fatal( -1, "Handle null at:" );

		cdebug.println( "Handle not null" );

		return reference_to_check;
	}


	public static <T> T[] NOT_EMPTY( T... values )
	{
		check( values != null ).on_fail().fatal( -1, "values array null at:" );

		cdebug.println( "values not null" );

		check( values.length > 0 ).on_fail().fatal( -1, "values array empty at:" );

		cdebug.println( "values not empty" );

		return values;
	}


	public static void TODO( String msg, Object... args )
	{
		if( ! cdevel.active() ) return;

		System.err.println( "TODO:    " + trace_call( 3, "%f:%l:%m " ) + fmt( msg, args ) );
	}


	public static void IDEA( String msg, Object... args )
	{
		if( ! cdevel.active() ) return;

		System.err.println( "IDEA:    " + trace_call( 3, "%f:%l:%m " ) + fmt( msg, args ) );
	}


	public static void ASSERT( boolean b )
	{
		if( ! cdevel.active() ) return;

		if( ! b )
		{
			cerr.println( trace_call( 3, "Devel Assertion Failed @ %f:%l in %c.%m()" ) );

			System.exit( -1 );
		}

		cdevel.println( trace_call( 3, "Devel Assertion Passed @ %f:%l in %c.%m()" ) );
	}


	public static void NOT_IMPLEMENTED()// throws NotImplementedException
	{
		String msg = trace_call( 3, "NOT IMPLEMENTED @ %f:%l in %c.%m()" );

		//cerr.println( "Code reached a 'not implemented' break point at:" );

		//cerr.println( cat( "", trace( 3 ), "", "\n" ) );

		cerr.println( msg );

		throw new IllegalStateException( msg );
	}


	public static void EXPECT( boolean b )
	{
		if( b ) return;

		String msg = trace_call( 3, "EXPECT FAILED @ %f:%l in %c.%m()" );

		cerr.println( msg );

		throw new IllegalStateException( msg );
	}


	public static String STACK_TRACE()
	{
		EString es = new EString();

		for( StackTraceElement ste : Thread.currentThread().getStackTrace() )
		{
			es.println( ste );
		}

		return es.toString();
	}
/*

	public static void NOT_IMPLEMENTED()
	{
		throw new IllegalStateException( "Not Implemented" );
	}
*/
}






