package rojira.jsi4;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibConsole.cinfo;
import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibMaths.rnd_hex;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.check_arg;
import static rojira.jsi4.LibSystem.expect_arg;
import static rojira.jsi4.LibSystem.home_dir_path;
import static rojira.jsi4.LibSystem.require;
import static rojira.jsi4.LibSystem.retrace;
import static rojira.jsi4.LibText.cat;
import static rojira.jsi4.LibText.empty;
import static rojira.jsi4.LibText.fmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;

import rojira.jsi4.modules.eou.ModEaseOfUse;
import rojira.jsi4.util.io.FileFinder;
import rojira.jsi4.util.io.Pipe;
import rojira.jsi4.util.io.Resource;
import rojira.jsi4.util.io.WGet;

/**
<p>
*/
public class LibIO
{
	private static String URL_REGEX;

	private static long MAX_FILE_SIZE;

	public static boolean FORCE_OVERWRITE = true;

	public static boolean NO_OVERWRITE = false;


	private LibIO(){};

	static
	{
		try
		{
			ModEaseOfUse.load_class_conf( LibIO.class );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			throw new ExceptionInInitializerError( error );
		}
	}

	public static String URL_REGEX()
	{
		return URL_REGEX;
	}

	public static long MAX_FILE_SIZE()
	{
		return MAX_FILE_SIZE;
	}



	public static byte[] load_file( String location ) throws MalformedURLException, FileNotFoundException, IOException
	{
		cinfo.println( "Loading file: %s", location );

		if( location.matches( URL_REGEX ) )
		{
			cinfo.println( "File is a url: doing a wget" );

			byte[] raw_data = wget( location );

			cinfo.println( "got %d bytes", raw_data.length );

			return raw_data;
		}
		else
		{
			return load_file( _file( location ) );
		}
	}


	public static File _file(String location)
	{
		return new File( location );
	}

	public static byte[] load( File file ) throws FileNotFoundException, IOException
	{
		return load_file( file );
	}


	public static byte[] load_file( File file ) throws FileNotFoundException, IOException
	{
		FileInputStream fin = null;

		try
		{
			fin = new FileInputStream( file );

			if( fin.available() > MAX_FILE_SIZE )
			{
				throw new IOException( "file size > " + MAX_FILE_SIZE + " bytes" );
			}

			byte[] data = new byte[ fin.available() ];

			int bytes_read = fin.read( data );

			if( bytes_read != data.length )
			{
				throw new IOException( "Incomplete read of file" );
			}

			return data;
		}
		finally
		{
			if( fin != null ) fin.close();
		}
	}


	public static String read_file( String location ) throws FileNotFoundException, IOException
	{
		return new String( load_file( location ) );
	}


	public static String read_file( File file ) throws FileNotFoundException, IOException
	{
		return new String( load_file( file ) );
	}


	public static String read( File file ) throws FileNotFoundException, IOException
	{
		return new String( load_file( file ) );
	}


	public static void save_file( File file, byte[] data ) throws FileNotFoundException, IOException
	{
		OutputStream out = buffer( new FileOutputStream( file ) );

		out.write( data );

		out.close();
	}


	public static void write( File file, String data ) throws FileNotFoundException, IOException
	{
		require( file != null );

		require( data != null );

		save_file( file, data.getBytes() );
	}


	public static void write_file( File file, String data ) throws FileNotFoundException, IOException
	{
		save_file( file, data.getBytes() );
	}


	public static void write_file( String filename, String data ) throws FileNotFoundException, IOException
	{
		save_file( _file( filename ), data.getBytes() );
	}


	public static InputStream get_resource_stream( Class base_jar_class, String path ) throws FileNotFoundException, IOException
	{
		check( base_jar_class != null ).on_fail().raise_arg( "base_jar_class is null" );

		check( ! empty( path ) ).on_fail().raise_arg( "path is empty" );

		if( path.startsWith( "/" ) ) path = path.substring( 1 );

		InputStream in = base_jar_class.getResourceAsStream( path );

		if( in == null ) throw new FileNotFoundException( fmt( "Resource: %s/%s doesn't exist", base_jar_class.getName(), path ) );

		return in;
	}


	public static InputStream get_resource_stream( Object base_jar_object, String file_name ) throws FileNotFoundException, IOException
	{
		return get_resource_stream( base_jar_object.getClass(), file_name );
	}


	public static byte[] load_resource( Class base_jar_class, String file_name ) throws FileNotFoundException, IOException
	{
		InputStream in = get_resource_stream( base_jar_class, file_name );

		byte[] data = new byte[ in.available() ];

		int bytes_read = in.read( data );

		return data;
	}


	public static byte[] load_resource( Object base_jar_object, String file_name ) throws FileNotFoundException, IOException
	{
		InputStream in = get_resource_stream( base_jar_object.getClass(), file_name );

		byte[] data = new byte[ in.available() ];

		int bytes_read = in.read( data );

		return data;
	}


	public static String read_resource( Class base_jar_class, String file_name ) throws FileNotFoundException, IOException
	{
		return load_text_resource( base_jar_class, file_name );
	}


	public static String load_text_resource( Class base_jar_class, String file_name ) throws FileNotFoundException, IOException
	{
		byte[] data = load_resource( base_jar_class, file_name );

		return new String( data );
	}


	public static String read_resource( Object base_jar_object, String file_name ) throws FileNotFoundException, IOException
	{
		return load_text_resource( base_jar_object, file_name );
	}


	public static String load_text_resource( Object base_jar_object, String file_name ) throws FileNotFoundException, IOException
	{
		byte[] data = load_resource( base_jar_object.getClass(), file_name );

		return new String( data );
	}


	public static BufferedInputStream buffer( InputStream in )
	{
		return new BufferedInputStream( in );
	}


	public static BufferedOutputStream buffer( OutputStream out )
	{
		return new BufferedOutputStream( out );
	}


	public static BufferedReader buffer( Reader in )
	{
		return new BufferedReader( in );
	}


	public static BufferedWriter buffer( Writer out )
	{
		return new BufferedWriter( out );
	}


	public static FileInputStream stream_from_file( String path ) throws FileNotFoundException, IOException
	{
		return new FileInputStream( path );
	}


	public static FileOutputStream stream_to_file( String path ) throws FileNotFoundException, IOException
	{
		return new FileOutputStream( path );
	}


	public static FileReader read_from_file( String path ) throws FileNotFoundException, IOException
	{
		return new FileReader( path );
	}


	public static FileWriter write_to_file( String path ) throws FileNotFoundException, IOException
	{
		return new FileWriter( path );
	}


	public static FileInputStream stream_from_file( File path ) throws FileNotFoundException, IOException
	{
		return new FileInputStream( path );
	}


	public static FileOutputStream stream_to_file( File path ) throws FileNotFoundException, IOException
	{
		return new FileOutputStream( path );
	}


	public static FileReader read_from_file( File path ) throws FileNotFoundException, IOException
	{
		return new FileReader( path );
	}


	public static FileWriter write_to_file( File path ) throws FileNotFoundException, IOException
	{
		return new FileWriter( path );
	}


	/**
	 * <p> Pipes in to out (non threaded)
	 * <p> returns from this method when in is closed*, out is closed* or the pipe is programatically broken by another thread
	 * <p> closed* means end of file reached, io errors, stream closed, etc
	 */
	public static Pipe connect( InputStream in, OutputStream out )
	{
		return new Pipe( in, out, false );
	}


	/**
	 * <p> Pipes in to out (threaded)
	 */
	public static Pipe pipe( InputStream in, OutputStream out )
	{
		return new Pipe( in, out, true );
	}



	/**
	 * get the data specified in this url
	 */
	public static void wget( String url, File dest ) throws MalformedURLException, FileNotFoundException, IOException
	{
		wget( url,stream_to_file( dest ) );
	}


	/**
	 * get the data specified in this url
	 */
	public static byte[] wget( String url ) throws MalformedURLException, FileNotFoundException, IOException
	{
		WGet wget = wget( url, (OutputStream) null );

		return wget.data_buffer().toByteArray();
	}

	/**
	 * get the data specified in this url
	 */
	public static WGet wget( String url, OutputStream dest ) throws MalformedURLException, FileNotFoundException, IOException
	{
		WGet wget = new WGet( url );

		wget.do_get( dest );

		if( wget.error() != null )
		{
			Throwable error = wget.error();

			if( error instanceof MalformedURLException ) throw (MalformedURLException) error;

			if( error instanceof FileNotFoundException ) throw (FileNotFoundException) error;

			if( error instanceof IOException ) throw (IOException) error;

			if( error instanceof RuntimeException ) throw (RuntimeException) error;

			throw new IllegalStateException( "wget failed", error );
		}

		//cout.println( "wget: %s", wget );

		//cout.println( "wget.data_buffer(): %s", wget.data_buffer() );

		//cout.println( "wget.data_buffer().toByteArray(): %s", wget.data_buffer().toByteArray() );

		return wget;
	}


	public static String basename( File file )
	{
		require( file != null );

		return file.getName();
	}


	public static String basename( File file, String ext )
	{
		require( file != null );

		require( ext != null );

		String basename = file.getName();

		if( basename.endsWith( ext ) )
		{
			return basename.substring( 0, basename.length() - ext.length() );
		}

		return basename;
	}


	public static String ext( File file )
	{
		return ext( file.getName() );
	}


	public static String ext( String file_name )
	{
		int i = file_name.lastIndexOf( "." );

		if( i == -1 ) return "";

		return file_name.substring( i + 1 );
	}


	public static String name( File file )
	{
		return name( file.getName() );
	}


	public static String name( String file_name )
	{
		cverbose.println( "filename: %s", file_name );

		int i0 = file_name.lastIndexOf( File.separatorChar );

		cverbose.println( "i0: %d", i0 );

		i0 ++;

		cverbose.println( "i0: %d", i0 );

		int i1 = file_name.lastIndexOf( ".", file_name.length() );

		cverbose.println( "i1: %d", i1 );

		if( i1 == -1 ) i1 = file_name.length();

		cverbose.println( "i0: %d", i1 );

		return file_name.substring( i0, i1 );
	}


	public static String name_ext( File file )
	{
		return name_ext( file.getName() );
	}


	public static String name_ext( String file_name )
	{
		int i0 = file_name.lastIndexOf( File.separatorChar );

		return file_name.substring( i0 + 1 );
	}


	public static String path( File file )
	{
		return path( file.getPath() );
	}


	public static String path( String file_name )
	{
		int i = file_name.lastIndexOf( File.separatorChar );

		if( i == -1 ) return "";

		return file_name.substring( 0, i );
	}


	public Resource resource( Class base, String path )
	{
		expect_arg( base != null );

		expect_arg( ! empty( path ) );

		return new Resource( base, path );
	}

	public Resource resource( Object base, String path )
	{
		expect_arg( base != null );

		expect_arg( ! empty( path ) );

		return new Resource( base.getClass(), path );
	}


	public static FileFinder find() throws Throwable
	{
		return new FileFinder();
	}

	public static FileFinder ls() throws Throwable
	{
		return new FileFinder().max_depth( 0 );
	}
/*
	public static File[] find( File dir, String... matchers )
	{
		ArrayList<File> files = new ArrayList<File>();

		if( ! dir.isDirectory() ) throw new IllegalArgumentException( dir + " is not a directory" );

		MatcherFilenameFilter filter = new MatcherFilenameFilter( matchers );

		_find( dir, filter, files );

		return files.toArray( new File[ files.size() ] );
	}


	private static void _find( File base_dir, FilenameFilter filter, ArrayList<File> files )
	{
		for( File file : base_dir.listFiles( filter ) )
		{
			files.add( file );
		}

		for( File dir : base_dir.listFiles( dir_filter ) )
		{
			_find( dir, filter, files );
		}
	}


	public static File[] find( File base_dir, FileFilter filter, boolean recurse )
	{
		ArrayList<File> files = new ArrayList<File>();

		if( ! base_dir.isDirectory() ) throw new IllegalArgumentException( base_dir + " is not a directory" );

		_find( base_dir, filter, files, recurse );

		return files.toArray( new File[ files.size() ] );
	}


	private static void _find( File base_dir, FileFilter filter, ArrayList<File> files, boolean recurse )
	{
		for( File file : base_dir.listFiles( filter ) )
		{
			files.add( file );
		}

		if( recurse )
		{
			for( File dir : base_dir.listFiles( dir_filter ) )
			{
				_find( dir, filter, files, recurse );
			}
		}
	}




	public static File[] list( File dir, String... matchers )
	{
		MatcherFilenameFilter filter = new MatcherFilenameFilter( matchers );

		return dir.listFiles( filter );
	}


	public static File[] list( File dir, String matcher )
	{
		MatcherFilenameFilter filter = new MatcherFilenameFilter( matcher );

		return dir.listFiles( filter );
	}


	public static File[] list( File dir, FileType type )
	{
		FileTypeFilter filter = new FileTypeFilter( type );

		return dir.listFiles( filter );
	}
	*/

	public static boolean exists( String path )
	{
		return new File( path ).exists();
	}


	public static void copy( File src, File dst, boolean force ) throws FileNotFoundException, IOException
	{
		check( src.exists() ).on_fail().raise_value( "Can't copy files: src file %s doesn't exist", full_path( src ) );

		check( src.isFile() ).on_fail().raise_value( "Can't copy files: src file %s isn't a regular file", full_path( src ) );

		if( ! force )
		{
			check( ! dst.exists() ).on_fail().raise_value( "Can't copy files: dst file %s already exists", full_path( dst ) );
		}

		connect( buffer( stream_from_file( src ) ), buffer( stream_to_file( dst ) ) );

		check( dst.exists() ).on_fail().raise_operation( "Copy file error: %s doesn't exist", full_path( dst ) );

		check( dst.length() == src.length() ).on_fail().raise_operation( "Copy file error: %s (%d bytes) != %s (%d bytes)", full_path( src ), src.length(), full_path( dst ), dst.length() );
	}


	public static void copy( File src, File dst ) throws FileNotFoundException, IOException
	{
		copy( src, dst, NO_OVERWRITE );
	}


	public static void delete( File file )
	{
		check( file.exists() ).on_fail().raise_state( "File %s doesn't exist", file );

		file.delete();

		check( ! file.exists() ).on_fail().raise_operation( "Couldn't delete File %s", file );
	}


	public static String full_path( File file ) throws IOException
	{
		return file.getCanonicalFile().getAbsolutePath();
	}


	public static String full_path( String path ) throws IOException
	{
		return full_path( _file( path ) );
	}


	public static File temporary_random_directory()
	{
		return random_directory( "/tmp" );
	}


	public static File random_directory( String path )
	{
		return random_directory( _file( path ) );
	}


	public static File random_directory( File parent )
	{
		File dir = null;

		do
		{
			dir = join_paths( parent, rnd_hex() );

			if( ! dir.exists() )
			{
				break;
			}
		}
		while( true );

		check( dir.mkdirs() ).on_fail().raise_state( "Couldn't make directory: %s", dir.getAbsolutePath() );

		return dir;
	}


	/**
	 * <p> replaces ~ and $HOME with user home directory path
	 * <p> replaces $TMP with /tmp
	 */
	public static File file( String base_path, String... path )
	{
		if( base_path.matches( URL_REGEX ) )
		{
			try
			{
				return new File( new URI( base_path ) );
			}
			catch( Throwable error )
			{
				cerr.println( "Couldn't create remote file: %s", error.getMessage() );

				cdebug.println( retrace( error ) );

				throw new IllegalStateException( fmt( "Couldn't create remote file: %s", base_path ), error );
			}
		}
		else
		{
			base_path = base_path.replace( "~", home_dir_path() );

			base_path = base_path.replace( "$HOME", home_dir_path() );

			base_path = base_path.replace( "$TMP", "/tmp" );

			String sub_path = cat( "", path, "", File.separator );

			return new File( base_path + File.separator + sub_path );
		}
	}


	public static File _file( File directory, String... path )
	{
		return join_paths( directory, path );
	}


	public static File join_paths( File directory, String... path )
	{
		// does isDirectory return true if directory doesn't exist (maybe the check should be ! isFile)

		//arg_check( directory.isDirectory(), "Couldn't join paths: %s is not a directory", full_path( directory ) );

		String sub_path = cat( "", path, "", File.separator );

		return new File( directory, sub_path );
	}


	public static void mkdir( File directory )
	{
		check( ! directory.exists() ).on_fail().raise_state( "Directory (%s) already exists", directory );

		directory.mkdirs();

		check( directory.exists() ).on_fail().raise_operation( "Failed to make directory (%s)", directory );
	}
/*
	public static FileSize file_size( double bytes )
	{
		return new FileSize( bytes );
	}


	public static FileSize file_size( double value, FileSize.Unit unit )
	{
		return new FileSize( value, unit );
	}


	public static long bytes( double value, FileSize.Unit unit )
	{
		return (long) FileSize.convert( value, unit, FileSize.Unit.BYTES );
	}
*/


	public static void close( InputStream in )
	{
		if( in == null ) return;

		try
		{
			in.close();
		}
		catch( Throwable error )
		{
			cerr.println( "Error closing input stream: %s", error.getMessage() );

			cverbose.println( retrace( error ) );
		}
	}


	public static void close( OutputStream out )
	{
		if( out == null ) return;

		try
		{
			out.flush();

			out.close();
		}
		catch( Throwable error )
		{
			cerr.println( "Error closing output stream: %s", error.getMessage() );

			cverbose.println( retrace( error ) );
		}
	}


	/**
	*  Returns the unqualified hostname of the local machine via the InetAddress.getLocalHost().getHostName() method
	*  ie. may not do a DNS lookup of the local host to find this
	*/
	public static String short_hostname() throws UnknownHostException
	{
		String hostname = InetAddress.getLocalHost().getHostName();

		int dot_index = hostname.indexOf( "." );

		if( dot_index == -1 ) return hostname;

		return hostname.substring( 0, dot_index );
	}
	
	
	public static boolean diff( File file1, File file2 ) throws IOException
	{
		check_arg( file1 != null );
		
		check_arg( file1.exists() );
		
		check_arg( file1.isFile() );
		
		check_arg( file1.canRead() );

		check_arg( file2 != null );
		
		check_arg( file2.exists() );
		
		check_arg( file2.isFile() );
		
		check_arg( file2.canRead() );
		
		InputStream	fi1 = null;
		
		InputStream	fi2 = null;

		try
		{
			fi1 = buffer( stream_from_file( file1 ) );
			
			fi2 = buffer( stream_from_file( file2 ) );
			
			while( true )
			{
				int b1 = fi1.read();
				
				int b2 = fi2.read();
				
				if( b1 != b2 ) return true;
				
				if( b1 == -1 && b2 == -1 ) return false;
			}
		}
		finally
		{
			close( fi1 );
			
			close( fi2 );
		}
	}
}


