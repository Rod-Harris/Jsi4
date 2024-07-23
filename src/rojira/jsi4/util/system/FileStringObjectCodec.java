package rojira.jsi4.util.system;

import java.io.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibIO.*;

public class FileStringObjectCodec implements ObjectCodec<String>
{
	public boolean can_convert( Class type )
	{
		require( type != null );

		if( File.class.isAssignableFrom( type ) )
		{
			return true;
		}

		return false;
	}

	public String encode( Class type, Object o )
	{
		require( can_convert( type ) );

		if( o == null ) return null;

		return o.toString();
	}

	public <T2> T2 decode( Class<T2> type, String data )
	{
		cverbose.println( "Casting data %s as type %s", data, type );

		if( data == null )
		{
			return null;
		}

		Object out = _file( data );

		require( out != null );

		return type.cast( out );
	}
}
