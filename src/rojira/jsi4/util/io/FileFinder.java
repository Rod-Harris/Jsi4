package rojira.jsi4.util.io;

import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibIO._file;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.empty;
import static rojira.jsi4.LibText.empty;
import static rojira.jsi4.LibText.str;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class FileFinder
{
	File dir;

	FileType[] types;

	int min_depth = 0;

	int max_depth = Integer.MAX_VALUE;

	FilenameFilter filter;

	DirectoryFilter dir_filter = new DirectoryFilter();

	/**
	<p> Construct a new FileFinder that by default looks in the current directory, and will recursively find any file (or directory, or link) regardless of type or name
	*/
	public FileFinder() throws Throwable
	{
		dir = new File( "." ).getCanonicalFile().getAbsoluteFile();

		types = new FileType[]{ FileType.ANY };

		filter = new AcceptAllFilenameFilter();
	}


	/**
	<p> change the starting location for the find
	*/
	public FileFinder in( String location )
	{
		check( ! empty( location ) ).on_fail().raise_arg( "location is null" );

		dir = _file( location );

		check( dir.exists() ).on_fail().raise_value( "Directory %s doesn't exist", location );

		check( dir.isDirectory() ).on_fail().raise_value( "File %s isn't a directory", location );

		return this;
	}


	/**
	<p> change the starting location for the find
	*/
	public FileFinder in( File location )
	{
		check( location != null ).on_fail().raise_arg( "location is null" );

		dir = location;

		check( dir.exists() ).on_fail().raise_value( "Directory %s doesn't exist", location );

		check( dir.isDirectory() ).on_fail().raise_value( "File %s isn't a directory", location );

		return this;
	}


	/**
	<p> change the file type that is matched
	*/
	public FileFinder type( FileType type )
	{
		types( type );

		return this;
	}


	/**
	<p> change the file types that are matched
	*/
	public FileFinder types( FileType... types )
	{
		this.types = types;

		return this;
	}


	/**
	<p> set the minimum depth of directory recursion (starting at 0 for the contents of the current directory)
	@throws IllegalArgumentException if min_depth < 0
	@throws IllegalArgumentException if min_depth > max_depth
	*/
	public FileFinder min_depth( int min_depth )
	{
		check( min_depth >= 0 ).on_fail().raise_arg( "min_depth value (%d) invalid: must be at least 0", min_depth );

		check( min_depth <= max_depth ).on_fail().raise_arg( "min_depth value (%d) invalid: must be less than or equal to max_depth (%d)", min_depth, max_depth );

		this.min_depth = min_depth;

		return this;
	}


	/**
	<p> set the maximum depth of directory recursion (starting at 0 for the contents of the current directory)
	@throws IllegalArgumentException if max_depth < min_depth
	*/
	public FileFinder max_depth( int max_depth )
	{
		check( max_depth >= min_depth ).on_fail().raise_value( "max_depth value (%d) invalid: must be greater than or equal to min_depth (%d)", max_depth, min_depth );

		this.max_depth = max_depth;

		return this;
	}


	public FileFinder match( String regex )
	{
		check( ! empty( regex ) ).on_fail().raise_arg( "regex is empty" );

		matches( regex );

		return this;
	}


	public FileFinder matches( String... regexes )
	{
		check( ! empty( regexes ) ).on_fail().raise_arg( "regexes is empty" );

		filter = new RegexMatcherFilenameFilter( regexes );

		return this;
	}


	public FileFinder name( String name )
	{
		check( ! empty( name ) ).on_fail().raise_arg( "name is empty" );

		names( name );

		return this;
	}


	public FileFinder names( String... names )
	{
		check( ! empty( names ) ).on_fail().raise_arg( "names is empty" );

		filter = new MatcherFilenameFilter( true, names );

		return this;
	}


	public FileFinder iname( String iname )
	{
		check( ! empty( iname ) ).on_fail().raise_arg( "iname is empty" );

		inames( iname );

		return this;
	}


	public FileFinder inames( String... inames )
	{
		check( ! empty( inames ) ).on_fail().raise_arg( "inames is empty" );

		filter = new MatcherFilenameFilter( false, inames );

		return this;
	}


	public File[] do_search()
	{
		return run();
	}


	public File[] run()
	{
		ArrayList<File> files_list = new ArrayList<File>();

		find_files( dir, 0, files_list );

		return files_list.toArray( new File[ files_list.size() ] );
	}


	private void find_files( File cdir, int cdepth, ArrayList<File> files_list )
	{
		cverbose.println( "Finding files in: %s", cdir );

		cverbose.println( "   recursion level %d out of %d", cdepth, max_depth );

		if( cdepth >= min_depth && cdepth <= max_depth )
		{
			for( File file : cdir.listFiles( filter ) )
			{
				if( type_match( file, types ) )
				{
					files_list.add( file );
				}
			}
		}

		if( cdepth < max_depth )
		{
			for( File ndir : cdir.listFiles( dir_filter ) )
			{
				find_files( ndir, cdepth + 1, files_list );
			}
		}
	}


	private boolean type_match( File file, FileType[] types )
	{
		for( FileType type : types )
		{
			if( type == FileType.ANY ) return true;

			else if( type == FileType.FILE && file.isFile() ) return true;

			else if( type == FileType.DIRECTORY && file.isDirectory() ) return true;
		}

		return false;
	}
}


class DirectoryFilter implements FileFilter
{
	public boolean accept( File pathname )
	{
		return pathname.exists() && pathname.isDirectory();
	}
}


class MatcherFilenameFilter implements FilenameFilter
{
	boolean case_sensitive;

	String[] matchers;


	public MatcherFilenameFilter( boolean case_sensitive, String... matchers )
	{
		this.case_sensitive = case_sensitive;

		this.matchers = matchers;
	}


	public boolean accept( File dir, String name )
	{
		boolean accept = false;

		try
		{
			cverbose.println( "checking for filename match [%s] against [%s]", name, str( matchers ) );

			for( String matcher : matchers )
			{
				if( case_sensitive )
				{
					if( name.equals( matcher ) ) return accept = true;
				}
				else
				{
					if( name.toLowerCase().equals( matcher.toLowerCase() ) ) return accept = true;
				}
			}

			return accept = false;
		}
		finally
		{
			cverbose.println( "   name accepted: %s", accept );
		}
	}
}


class RegexMatcherFilenameFilter implements FilenameFilter
{
	String[] matchers;

	public RegexMatcherFilenameFilter( String... matchers )
	{
		this.matchers = matchers;
	}


	public boolean accept( File dir, String name )
	{
		boolean accept = false;

		try
		{
			cverbose.println( "checking for filename regex match [%s] against [%s]", name, str( matchers ) );

			for( String matcher : matchers )
			{
				if( name.matches( matcher ) ) return accept = true;
			}

			return accept = false;
		}
		finally
		{
			cverbose.println( "   name accepted: %s", accept );
		}
	}
}

class AcceptAllFilenameFilter implements FilenameFilter
{
	public boolean accept( File dir, String name )
	{
		return true;
	}
}
