package rojira.jsi4.modules.eou;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibIO._file;
import static rojira.jsi4.LibIO.load_text_resource;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.inspect;
import static rojira.jsi4.LibSystem.retrace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import rojira.jsi4.util.system.ClassMeta;


public class ModEaseOfUse
{
	private ModEaseOfUse(){}


	private static File allsys_conf_file;

	public static ConfigParser allsys;


	public static File resolve_allsys_conf()
	{
		cdebug.println( "checking if allsys.conf file has already been resolved" );

		if( allsys_conf_file != null )
		{
			cdebug.println( "    it has: %s", allsys_conf_file );

			return allsys_conf_file;
		}

		cdebug.println( "    it has not" );

		cdebug.println( "checking r5.allsys.conf property" );

		String prop_value = System.getProperty( "r5.allsys.conf" );

		if( prop_value != null )
		{
			cdebug.println( "    found: %s", prop_value );

			allsys_conf_file = _file( prop_value );

			return allsys_conf_file;
		}

		cdebug.println( "    not found" );

		cdebug.println( "checking R5_ALLSYS_CONF env variable" );

		String env_value = System.getenv( "R5_ALLSYS_CONF" );

		if( env_value != null )
		{
			cdebug.println( "    found: %s", env_value );

			allsys_conf_file = _file( env_value );

			return allsys_conf_file;
		}

		cdebug.println( "    not found" );

		cdebug.println( "checking $HOME/.r5.allsys.conf file" );

		File test_file = _file( "$HOME/.r5.allsys.conf" );

		if( test_file.exists() && test_file.isFile() )
		{
			cdebug.println( "    found: %s", test_file );

			allsys_conf_file = test_file;

			return allsys_conf_file;
		}

		cdebug.println( "    not found" );

		return null;
	}


	public static ConfigParser load_allsys_conf() throws Throwable
	{
		if( allsys != null ) return allsys;

		allsys = new ConfigParser();

		allsys.load( resolve_allsys_conf() );

		return allsys;
	}


	public static String auto_conf_location( Class app ) throws Throwable
	{
		String name = app.getSimpleName();

		String pkg = app.getPackage().getName();

		cdebug.println( "pkg = %s", pkg );

		cdebug.println( "name = %s", name );


		// Check Runtime Property (passed to JRE via -d[prop_name]=[prov_value])

		String prop_name = pkg + ".conf";

		cdebug.println( "checking %s property", prop_name );

		String prop_value = System.getProperty( prop_name );

		if( prop_value != null )
		{
			cdebug.println( "    found: %s", prop_value );

			return prop_value;
		}

		cdebug.println( "    not found" );


		// Check Enivronment Variable

		String env_name = pkg.toUpperCase().replace( ".", "_" ) + "_CONF";

		cdebug.println( "checking %s environemnt variable", env_name );

		String env_value = System.getenv( env_name );

		if( env_value != null )
		{
			cdebug.println( "    found: %s", env_value );

			return env_value;
		}

		cdebug.println( "    not found" );


		// check usr conf file for app

		String usr_file_path = "$HOME/." + pkg + ".conf";

		cdebug.println( "checking %s file", usr_file_path );

		File test_file = _file( usr_file_path );

		if( test_file.exists() && test_file.isFile() )
		{
			cdebug.println( "    found: %s", test_file );

			return test_file.getAbsolutePath();
		}

		cdebug.println( "    not found" );

		throw new IllegalStateException( "Couldn't locate the conf file for " + app.getName() );
	}


	public static ConfigParser auto_conf( Class app ) throws Throwable
	{
		String auto_conf_location = auto_conf_location( app );

		return new ConfigParser().create_from_path( auto_conf_location );
	}


	public static void load_class_conf( Class type ) throws FileNotFoundException, IOException, IllegalAccessException
	{
		check( type != null ).on_fail().raise_arg( "'type' parameter is null" );

		String resource_name = type.getSimpleName() + ".conf";

		cverbose.println( "loading conf resource: %s", resource_name );

		ConfigParser config = new ConfigParser().parse( load_text_resource( type, resource_name ) );

		ClassMeta inspector = inspect( type );

		for( String field_name : config.section_keys( null ) )
		{
			cverbose.println( "field_name: %s", field_name );

			String value = config.get_value( field_name );

			try
			{
				inspector.set( field_name, value );
			}
			catch( NoSuchFieldException ex )
			{
				cerr.println( "Error setting field: '%s.%s' doesn't exist", type.getName(), field_name );

				cdebug.println( retrace( ex ) );
			}
		}
	}
}
