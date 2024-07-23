package rojira.jsi4.exp;


public class MIO
{
	public static final MStream min = new MStream();

	public static final MStream mout = new MStream();

	public static final MStream merr = new MStream( -1 );


	public static int argv( Object... args )
	{
		min.push( args );

		return args.length;
	}


	public static Object[] retv( int retc )
	{
		if( retc > 0 )
		{
			return mout.pop();
		}
		else if( retc < 0 )
		{
			return merr.pop();
		}

		return new Object[ 0 ];
	}


/*
	public static void require( boolean b )
	{
		if( b ) return;

		throw new IllegalStateException( "Reqirement not satisfied @ " + trace_call( 3, "%f:%l:%m " ) );
	}
*/
}
