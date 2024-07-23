package rojira.jsi4.util.system;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibText.*;
import java.lang.reflect.*;
import java.util.*;


public class ObjectDataLoader
{
	ArrayList<Field> fields = new ArrayList<Field>();

	Class type;

	String class_name;

	static ArrayList<ObjectCodec> codecs = new ArrayList<ObjectCodec>();

	static HashMap<Class,ObjectCodec> codecs_cache = new HashMap<Class,ObjectCodec>();

	static
	{
		codecs.add( new PrimitiveStringObjectCodec() );

		codecs.add( new StringStringObjectCodec() );

		codecs.add( new FileStringObjectCodec() );
	}

	/**
	@param class_name the fully qualified class to create objects from (must have a default constructor)
	*/
	public ObjectDataLoader( String class_name ) throws Throwable
	{
		this.class_name = class_name;

		cverbose.println( "creating ObjectDataLoader for class: %s", class_name );

		type = Class.forName( class_name );

		//type.getConstructor().newInstance();
	}

	/**
	@param field_name the name of the field to add data to (in order)
	@throws ? if field doesn't exist
	@throws ? if field can't be made accessible
	*/
	public void add_field( String field_name ) throws Throwable
	{
		Field field = type.getDeclaredField( field_name );

		field.setAccessible( true );

		fields.add( field );
	}

	/**
	<p> parse the string arguments into each of the fields prevoiusly set with the calls to add_field
	<p> the arguments passed must be in the same order as the fields added with the calls to add_field
	@throws AssertionError if field_data_tokens is null
	@throws IllegalStateException if field_data_tokens.length != number of fields added with the calls to add_field
	@return a new Object of type set in the constructor
	*/
	public Object set_field_data( String... field_data_tokens ) throws Throwable
	{
		expect_arg( field_data_tokens != null );

		require( field_data_tokens.length == fields.size() );

		Object object = type.newInstance();

		for( int i=0; i<fields.size(); i++ )
		{
			Field field = fields.get( i );

			String data = field_data_tokens[ i ];

			cverbose.println( "setting %s = %s", field.getName(), data );

			field.setAccessible( true );

			Class field_type = field.getType();

			Object cast_data = cast( data, field_type );

			cverbose.println( "setting %s.%s = (%s) %s", object, field.getName(), field_type, cast_data );

			field.set( object, cast_data );
		}

		return object;
	}

	public static Map<Class,Object[]> parse_data( String data ) throws Throwable
	{
		Scanner data_scanner = new Scanner( data );

		int line_no = 0;

		ObjectDataLoader loader = null;

		String current_package = "";

		ArrayList objects = null;

		Map<Class,ArrayList> map = new HashMap<Class, ArrayList>();

		while( data_scanner.hasNextLine() )
		{
			line_no ++;

			String line = data_scanner.nextLine();

			cdebug.println( "parsing line: %03d : %s", line_no, line );

			if( empty( line ) )
			{
				cdebug.println( "   line is empty - continuing" );

				continue;
			}

			line = line.trim();

			if( line.startsWith( "<" ) )
			{
				cdebug.println( "   line should contain a <package>" );

				String[] matches = regex( line, "[\\<](.+)[\\>]" );

				cdebug.println( "      Matches: %s", str( matches ) );

				require( matches != null && matches.length == 2 );

				cdebug.println( "      package: %s", matches[ 1 ] );

				current_package = matches[ 1 ];

				continue;
			}


			if( line.startsWith( "[" ) )
			{
				cdebug.println( "   line should contain a [ClassName]" );

				String[] matches = regex( line, "[\\[](.+)[\\]]" );

				cdebug.println( "      Matches: %s", str( matches ) );

				require( matches != null && matches.length == 2 );

				String class_name = current_package + "." +  matches[ 1 ];

				cdebug.println( "      ClassName: %s",class_name );

				loader = new ObjectDataLoader( class_name );

				objects = new ArrayList();

				map.put( loader.type, objects );

				continue;
			}


			if( line.startsWith( "{" ) )
			{
				cdebug.println( "   line should contain {fields}" );

				String[] matches = regex( line, "[\\{](.+)[\\}]" );

				cdebug.println( "      Matches: %s", str( matches ) );

				require( matches != null && matches.length == 2 );

				String[] field_tokens = matches[ 1 ].split( "\\s*,\\s+" );

				cdebug.println( "      Fields: %s", str( field_tokens ) );

				for( String field_token : field_tokens )
				{
					loader.add_field( field_token );
				}

				continue;
			}

			cdebug.println( "   line should contain field data" );

			String[] field_data_tokens = line.split( "\\s*,\\s+" );

			cdebug.println( "      Field data: %s", str( field_data_tokens ) );

			Object object = loader.set_field_data( field_data_tokens );

			objects.add( object );
		}


		Map<Class,Object[]> map2 = new HashMap<Class, Object[]>();

		for( Class key : map.keySet() )
		{
			ArrayList list = map.get( key );

			map2.put( key, list.toArray() );
		}

		cverbose.println( map );

		return map2;
	}


	/**
	@throws IllegalStateException if type is null
	@return null if no codec found
	*/
	static ObjectCodec get_codec( Class type )
	{
		require( type != null );

		// check cache
		{
			ObjectCodec codec = codecs_cache.get( type );

			if( codec != null )
			{
				return codec;
			}
		}

		// check list

		for( ObjectCodec codec : codecs )
		{
			if( codec.can_convert( type ) )
			{
				codecs_cache.put( type, codec );

				return codec;
			}
		}

		return null;
	}


	public static <T> T cast( String data, Class<T> type )
	{
		cverbose.println( "Casting data %s as type %s", data, type );

		if( data == null )
		{
			return null;
		}

		ObjectCodec codec = get_codec( type );

		require( codec != null );

		return (T) codec.decode( type, data );
	}

}




