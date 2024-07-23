package rojira.jsi4.modules.eou;

import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibIO.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibDevel.*;

import rojira.jsi4.util.text.*;
import rojira.jsi4.util.system.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class ConfigParser
{
	Map<String,Map<String,String>> db = new LinkedHashMap<String,Map<String,String>>();
	
	String[] comment_chars = "# ;".split( "\\s+" );

	Map<String,String> default_map = new HashMap<String,String>();
	
	Map<String,String> current_map = default_map;

	String path_separator_char = "/";

	String entry_separator_char = ",\\s+";

	String separator = "=";

	File filepath;
	

	public static ConfigParser create_from_path( String location ) throws Exception
	{
		return new ConfigParser().load( location );
	}


	public static ConfigParser create_from_file( File path ) throws Exception
	{
		compile_check( path != null );
		
		runtime_check( path.exists() );
		
		return new ConfigParser().load( path );
	}


	public ConfigParser()
	{
		TODO( "map [] to default" );

		TODO( "map [/xxx] to close map" );

		db.put( "default", default_map );
	}


	public void new_test_method()
	{
		cinfo.println( "new_test_method()" );
	}


	public ConfigParser load( String file_path ) throws FileNotFoundException, IOException
	{
		load( _file( file_path ) );

		return this;
	}


	public ConfigParser load( File file ) throws FileNotFoundException, IOException
	{
		filepath = file;
		
		cverbose.println( "loading file: %s", file );

		String data = read_file( file );

		cverbose.println( "loaded %d characters", data.length() );

		parse( data );

		return this;
	}


	public ConfigParser set_separator( String separator )
	{
		this.separator = separator;

		return this;
	}


	public ConfigParser parse( String data )
	{
		int line_no = 0;

		for( String line : data.split( "\n" ) )
		{
			line_no ++;

			line = line.trim();

			cverbose.println( "line = %s", line );

			if( line.length() == 0 )
			{
				cverbose.println( "line is empty: skipping" );

				continue;
			}

			boolean skip_line = false;

			for( String comment_char : comment_chars )
			{
				if( line.startsWith( comment_char ) )
				{
					skip_line = true;

					break;
				}
			}

			if( skip_line )
			{
				cverbose.println( "line is commented: skipping" );

				continue;
			}

			if( line.matches( "\\[.+\\]" ) )
			{
				cverbose.println( "tag found" );

				String tag = line.substring( 1, line.length() - 1 ).trim();

				cverbose.println( "tag = %s", tag );

				current_map = db.get( tag );

				if( current_map == null )
				{
					cverbose.println( "Creating new map" );

					current_map = new HashMap<String,String>();

					db.put( tag, current_map );
				}
				else
				{
					cwarn.println( "Tag %s already defined: reusing old map", tag );
				}

				continue;
			}

			if( line.matches( ".+" + separator + ".*" ) )
			{
				String[] tokens = line.split( separator, 2 );

				check( tokens.length == 2 ).on_fail().raise_state( "conf error on line %d: couldn't parse 2 tokens", line_no );

				String key = tokens[ 0 ].trim();

				String value = tokens[ 1 ].trim();

				if( current_map.containsKey( key ) )
				{
					cwarn.println( "redefining current key [%s]", key );
				}

				if( empty( value ) )
				{
					cwarn.println( "line %d key %s contains no value", line_no, key );
				}

				current_map.put( key, value );
			}
			else
			{
				cwarn.println( "line %d not parsable", line_no );
			}
		}

		cverbose.println( db );

		return this;
	}


	public void set( String var, String val )
	{
		for( Map<String,String> map : db.values() )
		{
			for( String key : map.keySet() )
			{
				String value = map.get( key );

				if( value.indexOf( var ) != -1 )
				{
					map.put( key, value.replace( var, val ) );
				}
			}
		}

		cverbose.println( db );
	}


	public String get_value( String path )
	{
		 Map<String,String> map = null;

		 String tag = null;

		 String key = null;

		if( path.indexOf( path_separator_char ) == -1 )
		{
			map = default_map;

			tag = "default";

			key = path;
		}
		else
		{
			String[] path_tokens = path.split( path_separator_char );

			check( path_tokens.length == 2 ).on_fail().raise_arg( "%s is an invalid <tag>%s<key> path: too many tokens", path, path_separator_char );

			tag = path_tokens[ 0 ];

			map = db.get( tag );

			check( map != null ).on_fail().raise_arg( "no tag [%s]", tag );

			key = path_tokens[ 1 ];
		}

		check( map.containsKey( key ) ).on_fail().raise_arg( "tag [%s] has no key [%s]", tag, key );

		return map.get( key );
	}


	public String[] get_values( String path, String tmp_entry_separator_char )
	{
		String value = get_value( path );

		if( empty( value ) ) return new String[ 0 ];

		return value.split( tmp_entry_separator_char );
	}


	public String[] get_values( String path )
	{
		String value = get_value( path );

		if( empty( value ) ) return new String[ 0 ];

		return value.split( entry_separator_char );
	}


	public int get_int( String path )
	{
		String value = get_value( path );

		try
		{
			check( ! empty( value ) ).on_fail().raise_state( "value error for key %s: empty values can't be parsed as integers", path );

			return _int( value );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value %s couldn't be parsed as an integer", path, value );
		}

		ASSERT( false );

		return -1;
	}


	public long get_long( String path )
	{
		String value = get_value( path );

		try
		{
			check( ! empty( value ) ).on_fail().raise_state( "value error for key %s: empty values can't be parsed as longs", path );

			return _long( value );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value %s couldn't be parsed as a long", path, value );
		}

		ASSERT( false );

		return -1;
	}


	public int[] get_ints( String path )
	{
		String[] values = get_values( path );

		if( values.length == 0 ) return new int[ 0 ];

		int[] arr = new int[ values.length ];

		int idx = 0;

		try
		{
			for( idx=0; idx<values.length; idx++ )
			{
				arr[ idx ] = _int( values[ idx ] );
			}

			return arr;
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value[%d] [%s] couldn't be parsed as an integer", path, idx, values[ idx ] );
		}

		ASSERT( false );

		return null;
	}


	public double get_double( String path )
	{
		String value = get_value( path );

		try
		{
			check( ! empty( value ) ).on_fail().raise_state( "value error for key %s: empty values can't be parsed as doubles", path );

			return _double( value );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value %s couldn't be parsed as a double", path, value );
		}

		ASSERT( false );

		return -1;
	}


	public double[] get_doubles( String path )
	{
		String[] values = get_values( path );

		if( values.length == 0 ) return new double[ 0 ];

		double[] arr = new double[ values.length ];

		int idx = 0;

		try
		{
			for( idx=0; idx<values.length; idx++ )
			{
				arr[ idx ] = _double( values[ idx ] );
			}

			return arr;
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value[%d] [%s] couldn't be parsed as a double", path, idx, values[ idx ] );
		}

		ASSERT( false );

		return null;
	}


	public boolean get_boolean( String path )
	{
		String value = get_value( path );

		try
		{
			check( ! empty( value ) ).on_fail().raise_state( "value error for key %s: empty values can't be parsed as booleans", path );

			return _boolean( value );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value %s couldn't be parsed as a boolean", path, value );
		}

		ASSERT( false );

		return false;
	}


	public boolean[] get_booleans( String path )
	{
		String[] values = get_values( path );

		if( values.length == 0 ) return new boolean[ 0 ];

		boolean[] arr = new boolean[ values.length ];

		int idx = 0;

		try
		{
			for( idx=0; idx<values.length; idx++ )
			{
				arr[ idx ] = _boolean( values[ idx ] );
			}

			return arr;
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			raise_state( "value error for key %s: value[%d] [%s] couldn't be parsed as a boolean", path, idx, values[ idx ] );
		}

		ASSERT( false );

		return null;
	}


	/**
	@param id the named section ("" or null for the default, un-named, section)
	@return named section (will return a zero size map if section is defined but has no entries)
	@throws IllegalArgumentException if the named section doesn't exist
	*/
	public Map<String,String> get_section( String id )
	{
		Map<String,String> map = null;

		if( empty( id ) )
		{
			return default_map;
		}
		else
		{
			map = db.get( id );

			check( map != null ).on_fail().raise_arg( "no section [%s]", id );

			return map;
		}
	}


	/**
	@param id the named section ("" or null for the default, un-named, section)
	@return named section (will return a zero size map if section is defined but has no entries)
	@throws IllegalArgumentException if the named section doesn't exist
	*/
	public ConfigParser subsection( String id )
	{
		Map<String,String> map = get_section( id );

		EString es = new EString();

		for( String s : map.keySet() )
		{
			es.println( "%s = %s", s, map.get( s ) );
		}

		return new ConfigParser().parse( es.toString() );
	}


	/**
	@param id the named section ("" or null for the default, un-named, section)
	@return named section exists and has entries (ie. size > 0)
	*/
	public boolean has_section( String id )
	{
		 Map<String,String> map = null;

		if( empty( id ) )
		{
			return default_map.size() > 0;
		}
		else
		{
			return db.containsKey( id ) && db.get( id ).size() > 0;
		}
	}


	public Set<String> section_keys( String id )
	{
		Map<String,String> map = get_section( id );

		return map.keySet();
	}


	public Set<String> section_ids()
	{
		return db.keySet();
	}


	public File filepath()
	{
		return filepath;
	}

	/**
	<p> loads the embedded resource associated with the given object 't' and parses the 'default' section into it's fields
	*/
	public static <T> T load( T t ) throws Exception
	{
		return load( t, null );
	}


	/**
	* <p> loads the embedded resource associated with the given object 't' and parses the names 'section' into it's fields
	* <p> the embedded resource is located in the same location as the class file defining the object 't' but with the '.conf' extension instead of the '.class' extension
	* <p> fields are 'pushed' into the objects state - ie all fields fefined in the conf file must exist in the object
	* @param t the object to load the data into
	* @param section the named section parsed from the conf file (null or 'default') for the un-named (default) section
	* @return t (the object, with it's data loaded)
	* @throws RuntimeException if t is null
	* @throws NoSuchFieldException if there is no field in the object with the same name as a field in the map
	* @throws IOException if there is a problem loading the resource
	* @throws Exception on other problems
	*/
	public static <T> T load( T t, String section ) throws Exception
	{
		expect_arg( t != null );

		Class type = t.getClass();

		String resource_name = type.getSimpleName() + ".conf";

		String resource_data = read_resource( t, resource_name );

		ConfigParser parser = new ConfigParser().parse( resource_data );

		Map<String,String> field_data = parser.get_section( section );

		set_field_data( t, field_data );

		return t;
	}


	public <T> T set_field_data( T object ) throws Exception
	{
		return set_field_data( object, get_section( null ) );
	}
	

	public <T> T set_field_data( T object, String section ) throws Exception
	{
		return ConfigParser.set_field_data( object, get_section( section ) );
	}


	public static <T> T set_field_data( T object, Map<String,String> field_data ) throws Exception
	{
		expect_arg( object != null );

		expect_arg( field_data != null );

		Class type = object.getClass();

		for( String field_name : field_data.keySet() )
		{
			Field field = type.getDeclaredField( field_name );

			field.setAccessible( true );

			String data = field_data.get( field_name );

			Class field_type = field.getType();

			Object cast_data = ObjectDataLoader.cast( data, field_type );

			cverbose.println( "setting %s.%s = (%s) %s", object, field.getName(), field_type, cast_data );

			field.set( object, cast_data );
		}

/*
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
*/

		return object;
	}
}
