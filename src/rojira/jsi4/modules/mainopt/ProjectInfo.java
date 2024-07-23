package rojira.jsi4.modules.mainopt;


import java.util.*;

import rojira.jsi4.modules.eou.*;
import rojira.jsi4.util.text.*;

import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibTime.*;
import static rojira.jsi4.LibIO.*;


public class ProjectInfo
{
	private static final HashMap<Class,ProjectInfo> projects_info = new HashMap<Class,ProjectInfo>();


	Class app_class;

	Class main_class;

	String app_name;

	int version;

	int revision;

	int build;

	long build_time;

	String build_timestamp;

	String version_info;


	public static ProjectInfo get_info( Class project_class ) throws Throwable
	{
		if( projects_info.containsKey( project_class ) )
		{
			return projects_info.get( project_class );
		}

		ConfigParser build_info = new ConfigParser().parse( load_text_resource( project_class, "build-info" )  );

		ProjectInfo project_info = new ProjectInfo();

		project_info.app_name = build_info.get_value( "APP_NAME" );

		project_info.version = build_info.get_int( "VERSION" );

		project_info.revision = build_info.get_int( "REVISION" );

		project_info.build = build_info.get_int( "BUILD" );

		project_info.build_time = build_info.get_long( "BUILD_TIME" );

		project_info.build_timestamp = format_date( new Date( project_info.build_time ), "yyyy/MM/dd HH:mm:ss" );

		project_info.version_info = fmt( "%d.%02d-%03d", project_info.version, project_info.revision, project_info.build );

		projects_info.put( project_class, project_info );

		return project_info;
	}


	public Class main_class()
	{
		return this.main_class;
	}

	public Class app_class()
	{
		return this.app_class;
	}

	public String app_name()
	{
		return this.app_name;
	}

	public int version()
	{
		return this.version;
	}

	public int revision()
	{
		return this.revision;
	}

	public int build()
	{
		return this.build;
	}

	public long build_time()
	{
		return this.build_time;
	}

	public String build_timestamp()
	{
		return this.build_timestamp;
	}

	public String version_info()
	{
		return this.version_info;
	}

	public String toString()
	{
		EString es = new EString();

		es.println( app_name() + " " + version_info() );

		return es.toString().trim();
	}
}
