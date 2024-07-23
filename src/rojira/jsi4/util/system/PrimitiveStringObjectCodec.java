package rojira.jsi4.util.system;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibText.*;

/**
 *	Converts primitives (and their wrappers) to and from Strings
 */
public class PrimitiveStringObjectCodec implements ObjectCodec<String>
{
	final Class[] primitive_types =
	{
		byte.class, short.class, int.class, long.class, float.class, double.class, char.class, boolean.class
	};

	final Class[] primitive_wrapper_types =
	{
		Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Character.class, Boolean.class
	};

	public boolean can_convert( Class type )
	{
		require( type != null );

		for( Class ptype : primitive_types )
		{
			if( ptype.isAssignableFrom( type ) )
			{
				return true;
			}
		}

		for( Class pwtype : primitive_wrapper_types )
		{
			if( pwtype.isAssignableFrom( type ) )
			{
				return true;
			}
		}

		return false;
	}

	public String encode( Class type, Object o )
	{
		require( can_convert( type ) );

		if( o == null ) return null;

		return str( o );
	}

	public <T2> T2 decode( Class<T2> type, String data )
	{
		cverbose.println( "Casting data %s as type %s", data, type );

		if( data == null )
		{
			return null;
		}

		Object out = null;

		if( byte.class.isAssignableFrom( type ) || Byte.class.isAssignableFrom( type )  )
		{
			out = _byte( data );
			//out = new Byte( data );
		}

		if( short.class.isAssignableFrom( type ) || Short.class.isAssignableFrom( type )  )
		{
			out = _short( data );
			//out = new Short( data );
		}

		if( int.class.isAssignableFrom( type ) || Integer.class.isAssignableFrom( type ) )
		{
			out = _int( data );
			//out = new Integer( data );
		}

		if( long.class.isAssignableFrom( type ) || Long.class.isAssignableFrom( type )  )
		{
			out = _long( data );
			//out = new Long( data );
		}

		if( float.class.isAssignableFrom( type ) || Float.class.isAssignableFrom( type )  )
		{
			out = _float( data );
			//out = new Float( data );
		}

		if( double.class.isAssignableFrom( type ) || Double.class.isAssignableFrom( type )  )
		{
			out = _double( data );
			//out = new Double( data );
		}

		if( char.class.isAssignableFrom( type ) || Character.class.isAssignableFrom( type )  )
		{
			require( data.length() == 1 );

			out = new Character( data.charAt( 0 ) );
		}

		if( boolean.class.isAssignableFrom( type ) || Boolean.class.isAssignableFrom( type )  )
		{
			out = _boolean( data );
			//out = new Boolean( data );
		}

		require( out != null );

		cverbose.println( "out: type = %s, value = %s", out.getClass(), out );

		return (T2) out;

	}
}

