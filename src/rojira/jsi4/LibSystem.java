package rojira.jsi4;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibConsole.cinfo;
import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibDevel.ASSERT;
import static rojira.jsi4.LibDevel.IDEA;
import static rojira.jsi4.LibDevel.TODO;
import static rojira.jsi4.LibText.cat;
import static rojira.jsi4.LibText.fmt;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;

import rojira.jsi4.modules.eou.ModEaseOfUse;
import rojira.jsi4.util.system.ArgumentException;
import rojira.jsi4.util.system.Check;
import rojira.jsi4.util.system.ClassMeta;
import rojira.jsi4.util.system.Exec;
import rojira.jsi4.util.system.Mappable;
import rojira.jsi4.util.system.NullsPolicy;
import rojira.jsi4.util.system.OSTypes;
import rojira.jsi4.util.system.OperationException;
import rojira.jsi4.util.system.StateException;
import rojira.jsi4.util.system.ValueException;
import rojira.jsi4.util.text.EString;

public class LibSystem
{
	private static String PROP_NL;

	private static String PROP_USER_HOME;

	private static String PROP_USER_NAME;

	private static String PROP_OS_NAME;

	private static String PROP_USER_DIR;

	private static String PROP_TMP_DIR;

	public static final String FILE_SEPARATOR = File.separator;

	public static final String PATH_SEPARATOR = File.pathSeparator;

	private static OSTypes OS;

	private static Map<String,String> env_override = new HashMap<String,String>();


	private LibSystem(){}

	static
	{
		cverbose.println( "Adding garbage collection shutdown hook" );

		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			public void run()
			{
				cverbose.println( "Running garbage collection shutdown hook" );

				System.gc();

				System.runFinalization();
			}
		});

		try
		{
			ModEaseOfUse.load_class_conf( LibSystem.class );
		}
		catch( Throwable error )
		{
			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			throw new ExceptionInInitializerError( error );
		}
		
		OS = determin_OS_Type();
	}

	public static String PROP_NL()
	{
		return PROP_NL;
	}

	public static String PROP_USER_HOME()
	{
		return PROP_USER_HOME;
	}

	public static String PROP_USER_NAME()
	{
		return PROP_USER_NAME;
	}

	public static String PROP_OS_NAME()
	{
		return PROP_OS_NAME;
	}

	public static String PROP_USER_DIR()
	{
		return PROP_USER_DIR;
	}

	public static String PROP_TMP_DIR()
	{
		return PROP_TMP_DIR;
	}

	public static String FILE_SEPARATOR()
	{
		return FILE_SEPARATOR;
	}

	public static String PATH_SEPARATOR()
	{
		return PATH_SEPARATOR;
	}

	public static OSTypes OS()
	{
		return OS;
	}



	public static long systime()
	{
		return System.currentTimeMillis();
	}

	/**
	sets to OS variable after querying the system properties
	*/
	private static OSTypes determin_OS_Type()
	{
		if( os_name().startsWith( "Linux" ) ) return OSTypes.LINUX;

		if( os_name().startsWith( "Windows" ) ) return OSTypes.WINDOWS;

		if( os_name().startsWith( "Mac" ) ) return OSTypes.MAC;

		return null;
	}


	public static String env()
	{
		EString out = new EString();

		Map<String,String> env = System.getenv();

		for( String key : env.keySet() )
		{
			out.println( "%s=%s", key, env.get( key ) );
		}

		return out.toString();
	}


	public static void _set_env( String name, String value )
	{
		check_arg( ! LibText.empty( name ) );
		
		env_override.put( name, value );
	}
	

	public static String _env( String name )
	{
		check_arg( ! LibText.empty( name ) );
		
		if( env_override.containsKey( name ) )
		{
			cdebug.println( "returning overridden environment variable [%s]", name );
			
			return env_override.get( name );
		}
		
		String value = System.getenv( name );

		check( value != null ).on_fail().raise_arg( "No Environment variable with name: %s", name );

		return value;
	}


	/**
	is the operating system a Linux variant
	*/
	public static boolean linux()
	{
		return OS == OSTypes.LINUX;
	}


	/**
	is the operating system a Windows variant
	*/
	public static boolean windows()
	{
		return OS == OSTypes.WINDOWS;
	}


	/**
	is the operating system a Mac variant
	*/
	public static boolean mac()
	{
		return OS == OSTypes.MAC;
	}


	public static void exit()
	{
		exit( 0 );
	}

	/**
	Stops the JRE
	*/
	public static void exit( int code )
	{
		System.exit( code );
	}

	/**
	get the specified system property
	*/
	public static String get_property( String prop )
	{
		return java.lang.System.getProperty( prop );
	}


	/**
	get the user name as specified in the system properties
	*/
	public static String user_name()
	{
		return get_property( PROP_USER_NAME );
	}


	/**
	get the OS name as specified in the system properties
	*/
	public static String os_name()
	{
		return get_property( PROP_OS_NAME );
	}


	/**
	get the path to the directory that the JRE was started in
	*/
	public static String exec_dir_path()
	{
		return get_property( PROP_USER_DIR );
	}


	/**
	get the directory that the JRE was started in
	*/
	public static File exec_dir()
	{
		return new File( exec_dir_path() );
	}


	/**
	get the path to the users home directory
	*/
	public static String home_dir_path()
	{
		return get_property( PROP_USER_HOME );
	}


	/**
	get the users home directory
	*/
	public static File home_dir()
	{
		return new File( home_dir_path() );
	}


	/**
	get the path to the tmp directory
	*/
	public static String tmp_dir_path()
	{
		return get_property( PROP_TMP_DIR );
	}


	/**
	get the tmp directory
	*/
	public static File tmp_dir()
	{
		return new File( tmp_dir_path() );
	}


	//----------------------------------    Sercurity


	public static String md5( String... s ) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance( "MD5" );

		for( String d : s )
		{
			md.update( d.getBytes(), 0, d.length() );
		}

		return String.format( "%032x", new BigInteger( 1, md.digest() ) );
	}


	//----------------------------------    LANG

	public static String retrace( Throwable ex )
	{
		return _ex_to_string( ex );
	}


	public static String trace( Throwable ex )
	{
		if( ex == null ) return null;

		EString es = new EString();

		es.println( "%s: %s", ex.getClass().getName(), ex.getMessage() );

		for( StackTraceElement ste : ex.getStackTrace() )
		{
			es.println( "\tat " + ste );
		}

		Throwable cause = ex.getCause();

		if( cause != null )
		{
			es.println( trace( cause ) );
		}

		return es.toString();
	}

	public static String ex_to_string( Throwable ex )
	{
		return _ex_to_string( ex );
	}

/*

	public static String _ex_to_string( Throwable ex )
	{
		if( ex == null ) return null;

		String s = "";

		String cs = _ex_to_string( ex.getCause() );

		if( cs != null )
		{
			s += cs + "\nWhich Caused\n";
		}

		EString es = new EString();

		ex.printStackTrace( new java.io.PrintStream( es ) );

		return s + es.toString();
	}
*/

	/**
	 * <p>Returns the root cause of the Throwable
	 */
	public static Throwable root_cause( Throwable ex )
	{
		if( ex.getCause() == null ) return ex;

		return get_cause( ex.getCause() );
	}


	/**
	 * <p>Returns the root cause of the Throwable
	 */
	public static Throwable get_cause( Throwable ex )
	{
		if( ex.getCause() == null ) return ex;

		return get_cause( ex.getCause() );
	}


	public static String _ex_to_string( Throwable ex )
	{
		if( ex == null ) return null;

		EString es = new EString();

		Throwable cause = ex.getCause();

		if( cause == null )
		{
			es.println( "%s: %s", ex.getClass().getName(), ex.getMessage() );

			for( StackTraceElement ste : ex.getStackTrace() )
			{
				es.println( "\tat " + ste );
			}
		}
		else
		{
			es.println( _ex_to_string( cause ) );

			es.println( "\tWhich Caused: (%s) %s", ex.getClass().getName(), ex.getMessage() );
		}

		return es.toString();
	}


	/**
	<p> Checks if 2 object references a and b are the same reference (ie a == b)
	<p> edge cases include null == null -> true
	<p> careful using primitives here: eg, 5 == 5 -> true, but new Interger( 5 ) == new Integer( 5 ) -> false
	*/
	public static <T> boolean same( T a, T b )
	{
		return a == b;
	}


	/**
	<p> The default behaviour for the equal method is for nulls to not be equal
	<p> use equal( T a, T b, boolean nulls_equal ) with the nulls_policy parameter set to NullsPolicy.EQUAL or NullsPolicy.ERROR to change this
	*/
	public static <T> boolean equal( T a, T b )
	{
		return equal( a, b, null ); // same as equal( a, b, NullsPolicy.NOT_EQUAL );
	}


	/**
	<p> checks if 2 objects are equal to each other (via the .equals method of the runtime class T)
	<p> if the nulls_equal parameter is true and both a and b are null this method returns true
	<p> if the nulls_equal parameter is false and both a and b are null this method returns false
	<p> if only one of a or b are null this method returns false, regardless of the value of the nulls_equal parameter
	<p> if a and b are the same object (a == b) returns true
	<p> returns the result of a.equals( b )
	*/
	public static <T> boolean equal( T a, T b, NullsPolicy nulls_policy )
	{
		if( nulls_policy == null ) nulls_policy = NullsPolicy.NOT_EQUAL;

		if( nulls_policy == NullsPolicy.ERROR )
		{
			if( a == null ) throw new IllegalArgumentException( "Object reference 'a' is null" );

			if( b == null ) throw new IllegalArgumentException( "Object reference 'b' is null" );
		}
		else
		{
			boolean nulls_equal = false; // Default for NullsPolicy.NOT_EQUAL

			if( nulls_policy == NullsPolicy.EQUAL )
			{
				nulls_equal = true;
			}

			if( a == null && b == null ) return nulls_equal;

			if( a == null )
			{
				if( b != null )
				{
					return false;
				}
			}

			ASSERT( a != null );

			if( b == null )
			{
				return false;
			}
		}

		ASSERT( a != null && b != null );

		if( a == b ) return true;

		cverbose.println( "a.class: %s", a.getClass() );

		cverbose.println( "a.class.isArray: %b", a.getClass().isArray() );

		if( a.getClass().isArray() )
		{
			T[] a_arr = (T[]) a;

			cverbose.println( "Objects a is an array: length %d", a_arr.length );

			if( b.getClass().isArray() )
			{
				T[] b_arr = (T[]) b;

				cverbose.println( "Objects b is an array: length %d", b_arr.length );

				return Arrays.equals( a_arr, b_arr );
			}

			cverbose.println( "Objects a is not an array" );

			return false;
		}

		return a.equals( b );
	}


// 	public static void throw_exception( Class ex_class, String msg, Object... args )
// 	{
//
// 	}


	//---------------------------------    COLLECTIONS


	public static <T> int len( T[] array )
	{
		if( array == null ) return -1;

		return array.length;
	}


	public static <T> int len( Collection<T> collection )
	{
		if( collection == null ) return -1;

		return collection.size();
	}


	public static <T> boolean empty( T[] array )
	{
		return array == null || array.length == 0;
	}


	public static <T> boolean empty( Collection<T> collection )
	{
		return collection == null || collection.size() == 0;
	}

	/**
	*	sort the given array based on their natural ordering
	*/
	public static <T extends Comparable> T[] sort( T[] array )
	{
		Arrays.sort( array );

		return array;
	}

	/**
	*	sort the given array according to the comparators rules
	*/
	public static <T> T[] sort( T[] array, Comparator<T> comparator )
	{
		Arrays.sort( array, comparator );

		return array;
	}


	/**
	*	sort the given list elements based on their natural ordering
	*/
	public static <T extends Comparable> List<T> sort( List<T> list )
	{
		Collections.sort( list );

		return list;
	}

	/**
	*	sort the given list elements according to the comparators rules
	*/
	public static <T> List<T> sort( List<T> list, Comparator<T> comparator )
	{
		Collections.sort( list, comparator );

		return list;
	}


	/**
	*	sort the given array based on objects returned by the given method for each element of that array
	*/
/*
	public static <T> T[] sort( T[] array, String method )
	{
		Arrays.sort( array, new MethodComparator( method ) );

		return array;
	}
*/


	/**
	*	reverse sort the given array based on objects returned by the given method for each element of that array
	*/
/*
	public static <T> T[] rsort( T[] array, String method )
	{
		Arrays.sort( array, new MethodComparator( method, -1 ) );

		return array;
	}
*/

	/**
	*	returns true if there is any overlap in the 2 sets
	*/
	public static boolean overlap( Object[] args1, Object... args2 )
	{
		for( Object o1 : args1 )
		{
			for( Object o2 : args2 )
			{
				//cverbose.println( "checking %s = %s", o1, o2 );

				if( o1.equals( o2 ) ) return true;
			}
		}

		return false;
	}


	/**
	*	returns a new array that has objs added to the end
	*/
	public static <T> T[] extend( T[] arr, T... objs )
	{
		if( arr == null || arr.length == 0 ) return objs;

		if( objs == null || objs.length == 0 ) return arr;

		//		For Java 1.6

		T[] new_arr = Arrays.copyOf( arr, arr.length + objs.length );

		System.arraycopy( objs, 0, new_arr, arr.length, objs.length );

		//		For Java 1.5
		//
		// 		T[] new_arr = ( T[] ) Array.newInstance( arr[ 0 ].getClass(), arr.length + objs.length );
		//
		// 		System.arraycopy( arr, 0, new_arr, 0, arr.length );
		//
		// 		System.arraycopy( objs, 0, new_arr, arr.length, objs.length );

		return new_arr;
	}


	public static <T> T[] reverse( T[] arr )
	{
	   for( int start=0, end=arr.length-1; start<=end; start++, end-- )
	   {
		  T temp = arr[start];
		  arr[start] = arr[end];
		  arr[end] = temp;
	   }

	   return arr;
	}




	// inspection
	/**
	*	print out the key:value pairs in the map
	*/
/*
	public static <K, V> String inspect( Map<K,V> map )
	{
		EString ret = new EString();

		ret.println( "size: %d", map.size() );

		for( K k : map.keySet() )
		{
			ret.println( "%s = %s", k, map.get( k ) );
		}

		return ret.toString().trim();
	}
*/

	/**
	*	print out the contents of the array
	*/
/*
	public static <T> String inspect_arr( T[] arr )
	{
		EString ret = new EString();

		for( T t : arr )
		{
			ret.print( "%s, ", t );
		}

		return ret.toString().trim();
	}
*/


	/**
	*	returns true if the array is null or its length is zero
	*/
/*
	public static boolean arrnull( Object[] arr )
	{
		return arr == null || arr.length == 0;
	}
*/


	// system

	public static void fatal( Exception ex )
	{
		cerr.println( "a fatal error has been caused by:" );

		ex.printStackTrace();

		exit( -1 );
	}


	public static void fatal()
	{
		cerr.println( "a fatal error has occured at:" );

		cerr.println( trace( 3 ) );

		exit( -1 );
	}


	public static void nonfatal( Exception ex )
	{
		cerr.println( "a non-fatal error has been caused by:" );

		cerr.println( ex );
	}


	public static void nonfatal()
	{
		cerr.println( "a non-fatal error has occured at:" );

		cerr.println( trace( 3 ) );
	}


	/**
	*	my version of assert (fatal if false)
	*/
	public static void affirm( boolean b )
	{
		if( b ) return;

		cerr.println( "Affirmation failed at:" );

		cerr.println( trace( 3 ) );

		exit( -1 );
	}


	/**
	*	my version of assert (fatal if false)
	*/
	public static void affirm( boolean b, String s, Object... args )
	{
		if( b ) return;

		cerr.println( "Affirmation failed: %s", String.format( s, args ) );

		cerr.println( trace( 3 ) );

		exit( -1 );
	}


	public static String trace( StackTraceElement[] elements )
	{
		return trace( Thread.currentThread().getStackTrace(), 2 );
	}


	public static String trace( StackTraceElement[] elements, int start_at )
	{
		EString es = new EString();

		int i = 0;

		for( StackTraceElement element : elements )
		{
			i ++;

			if( i <= start_at ) continue;

			es.println( element );
		}

		return es.toString();
	}


	public static <T> T[] _array( List<T> list, Class<T> array_type )
	{
		return list.toArray( (T[]) Array.newInstance( array_type, list.size() ) );
	}


	public static StackTraceElement[] trace( int start_at )
	{
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		StackTraceElement[] retrace = Arrays.copyOfRange( trace, start_at, trace.length );

		return retrace;
	}


	// native


	public static ByteBuffer byte_buffer( int capacity )
	{
		ByteBuffer byte_buffer = ByteBuffer.allocateDirect( capacity );

		byte_buffer.order( ByteOrder.nativeOrder() );

		return byte_buffer;
	}


	public static FloatBuffer float_buffer( int capacity )
	{
		return byte_buffer( capacity ).asFloatBuffer();
	}



	public static Exec exec( String command, String... args )
	{
		return new Exec( command, args );
	}


/*

	public static Command command( String cmd )
	{
		return new Command( cmd );
	}


	public static ProcessResults exec( String cmd, Object... args ) throws IOException, InterruptedException
	{
		String[] cmd_args = new String[ args.length + 1 ];

		cmd_args[ 0 ] = cmd;

		int i = 0;

		for( Object o : args )
		{
			i++;

			cmd_args[ i ] = o.toString();
		}

		Exec exec = new Exec( cmd_args );

		cverbose.println( "executing command: %s", exec );

		return exec.run();
	}


	public static ProcessResults run( Exec exec ) throws IOException, InterruptedException, NativeException
	{
		cdebug.println( exec );

		ProcessResults pr = exec.run();

		cdebug.println( pr );

		if( pr.exit_code != 0 ) throw new NativeException( pr );

		return pr;
	}


	@Deprecated
	public static ProcessResults runtime_exec( String cmd_fmt, Object... args ) throws IOException, InterruptedException
	{
		String cmd = String.format( cmd_fmt, args );

		cverbose.println( "Runtime.exec( %s )", cmd );

		Process p = Runtime.getRuntime().exec( cmd );

		return handle_process( p );
	}


	public static ProcessResults handle_process( Process p )// throws IOException, InterruptedException
	{
		ProcessStreamReader process_stream_reader = new ProcessStreamReader( p );

		Thread process_stream_reader_thread = new Thread( process_stream_reader );

		process_stream_reader_thread.start();

		cverbose.println( "Waiting for process to complete" );

		ProcessResults pr = new ProcessResults();

		while( true )
		{
			try
			{
				try
				{
					p.waitFor();
				}
				catch( InterruptedException ex )
				{
					cerr.println( ex );
				}

				pr.exit_code = p.exitValue();

				cverbose.println( "Process completed [%d]", pr.exit_code );

				break;
			}
			catch( IllegalThreadStateException ex )
			{
				cerr.println( ex );
			}
		}

		process_stream_reader.set_finished( true );

		process_stream_reader.read_streams();

		pr.std_out = process_stream_reader.get_stdout();

		pr.std_err = process_stream_reader.get_stderr();

		return pr;
	}


	static class ProcessStreamReader implements Runnable
	{
		private boolean finished;

		private BufferedInputStream pout;

		private BufferedInputStream perr;

		private EString std_out = new EString();

		private EString std_err = new EString();


		ProcessStreamReader( Process p )
		{
			pout = new BufferedInputStream( p.getInputStream() );

			perr = new BufferedInputStream( p.getErrorStream() );
		}

		void set_finished( boolean finished )
		{
			this.finished = finished;
		}

		public void run()
		{
			while( ! finished )
			{
				try
				{
					Thread.currentThread().sleep( 50 );
				}
				catch( InterruptedException ex )
				{
					cerr.println( ex );
				}

				read_streams();
			}
		}

		void read_streams()
		{
			try
			{
				std_out.read_from( pout );

				std_err.read_from( perr );
			}
			catch( IOException ex )
			{
				cerr.println( ex );
			}
		}

		String get_stdout()
		{
			return std_out.toString().trim();
		}

		String get_stderr()
		{
			return std_err.toString().trim();
		}
	}
*/

	// -------------------------------    LANG



	public static boolean _boolean( String a )
	{
		if( "true".equals( a ) ) return true;

		if( "false".equals( a ) ) return false;

		throw new IllegalArgumentException( a + " cannot be parsed as a boolean" );
	}


	public static boolean _boolean( long l )
	{
		if( l == 0 ) return false;

		return true;
	}


	public static boolean _boolean( double d )
	{
		if( d == 0 ) return false;

		return true;
	}


	public static boolean _boolean( Object o )
	{
		if( o == null ) return false;

		return true;
	}


	public static char _char( String a )
	{
		return a.charAt( 0 );
	}


	public static byte _byte( String a )
	{
		return ( byte ) _long( a );
	}


	public static short _short( String a )
	{
		return ( short ) _long( a );
	}


	public static int _int( String a )
	{
		return ( int ) _long( a );
	}


	public static int _int( double d )
	{
		return ( int ) d;
	}


	public static long _long( String a )
	{
		try
		{
			a = a.trim();

			if( a.toLowerCase().startsWith( "0x" ) )
			{
				a = a.substring( 2 );

				return new BigInteger( a, 16 ).longValue();
			}
			else if( a.startsWith( "#" ) )
			{
				a = a.substring( 1 );

				return new BigInteger( a, 16 ).longValue();
			}

			return new BigInteger( a ).longValue();
		}
		catch( NumberFormatException ex )
		{
			cerr.println( "Couldn't parse [%s] as a whole number", a );
			
			cverbose.println( retrace( ex ) );
			
			throw ex;
		}
	}


	public static float _float( String a )
	{
		return ( float ) _double( a );
	}


	public static double _double( String a )
	{
		try
		{
			a = a.trim();
			
			return new BigDecimal( a ).doubleValue();
		}
		catch( NumberFormatException ex )
		{
			cerr.println( "Couldn't parse [%s] as a real number", a );
			
			cverbose.println( retrace( ex ) );
			
			throw ex;
		}
	}


	public static String _string( Object o )
	{
		return o.toString();
	}


	public static int[] _ints( String s )
	{
		String[] vals = s.split( "," );

		int[] ints = new int[ vals.length ];

		for( int i=0; i<vals.length; i++ )
		{
			ints[ i ] = _int( vals[ i ].trim() );
		}

		return ints;
	}


	public static int[] _ints( String[] vals )
	{
		int[] ints = new int[ vals.length ];

		for( int i=0; i<vals.length; i++ )
		{
			ints[ i ] = _int( vals[ i ].trim() );
		}

		return ints;
	}


	public static double[] _doubles( String s )
	{
		String[] vals = s.split( "," );

		double[] doubles = new double[ vals.length ];

		for( int i=0; i<vals.length; i++ )
		{
			doubles[ i ] = _double( vals[ i ].trim() );
		}

		return doubles;
	}


	public static String[] _strings( String s )
	{
		String[] vals = s.split( "," );

		for( int i=0; i<vals.length; i++ )
		{
			vals[ i ] = vals[ i ].trim();
		}

		return vals;
	}


	public static Integer _Integer( String s )
	{
		try
		{
			return _int( s );
		}
		catch( Exception ex )
		{
			cerr.println( String.format( "couldn't parse '%s' as an Integer", s ) );

			cerr.println( ex );
		}

		return null;
	}


	public static Double _Double( String s )
	{
		try
		{
			return _double( s );
		}
		catch( Exception ex )
		{
			cerr.println( String.format( "couldn't parse '%s' as a Double", s ) );

			cerr.println( ex );
		}

		return null;
	}

	/**
	<br>%c - class name
	<br>%m - method name
	<br>%f - file name
	<br>%l - line number
	*/
	public static String trace_call( int level, String fmt )
	{
		//StackTraceElement[] es = new Throwable().getStackTrace();

		StackTraceElement[] es = Thread.currentThread().getStackTrace();

		if( es.length > level )
		{
			StackTraceElement ste = es[ level ];

			fmt = fmt.replace( "%c", ste.getClassName() );

			fmt = fmt.replace( "%m", ste.getMethodName() );

			fmt = fmt.replace( "%f", ste.getFileName() );

			fmt = fmt.replace( "%l", Integer.toString( ste.getLineNumber() ) );

			return fmt;
		}

		return "[no trace]";
	}


	public static String mtrace( Object... args )
	{
		String arglist = cat( "", args, "", ", " );

		return mtrace( get_stack_trace_element( 3 ), arglist );
	}


	public static String mtracef( String fmt, Object... args )
	{
		return mtrace( get_stack_trace_element( 3 ), fmt( fmt, args ) );
	}


	private static String mtrace( StackTraceElement ste, String msg )
	{
		return String.format( "%s.%s( %s )", ste.getClassName(), ste.getMethodName(), msg );
	}


	private static StackTraceElement get_stack_trace_element( int index )
	{
		StackTraceElement ste = Thread.currentThread().getStackTrace()[ index ];

		return ste;
	}


	public static String inspect( Object o )
	{
		if( o == null ) return "null";

		EString es = new EString();

		Map<String,String> fields = get_state( o, true );

		es.println( o.getClass().getName() );

		es.println( "{" );

		for( String s : fields.keySet() )
		{
			es.println( "   %s = %s", s, fields.get( s ) );
		}

		es.println( "}" );

		return es.toString();
	}


	public static ClassMeta inspect( Class type )
	{
		TODO( "cache class meta objects" );

		return new ClassMeta( type );
	}
/*
	public static String inspect( Object o )
	{
		if( o == null ) return "null";

		return inspect( o, o.getClass(), false );
	}


	public static String inspect( Class c )
	{
		return inspect( null, c, false );
	}


	public static String inspect( Object o, boolean recurse )
	{
		if( o == null ) return "null";

		return inspect( o, o.getClass(), recurse );
	}


	public static String inspect( Class c, boolean recurse )
	{
		return inspect( null, c, recurse );
	}


	private static String inspect( Object o, Class c, boolean recurse )
	{
		StringBuilder buf = new StringBuilder();

		String s;

		String vv;

		String[] primitives =
		{
			 "boolean", "byte", "char", "short", "int", "long", "float", "double"
		}
		;

		while( true )
		{
			if( c == null ) break;

			s = String.format( "  Class: %s\n", c.getName() );

			buf.append( s );

			Field[] fields = c.getDeclaredFields();

			AccessibleObject.setAccessible( fields, true );

			for( Field field : fields )
			{
				if( is_field_static( field ) == ( o == null ) )
				{
					String field_type = field.getType().toString();

					if( field_type.startsWith( "class " ) ) field_type = field_type.substring( "class ".length() );

					String var = field.getName();

					String val = null;

					try
					{
						if( overlap( primitives, field_type ) )
						{
							vv = "    %s %s = %s\n";

							val = String.format( "%s", field.get( o ) );
						}
						else
						{
							if( field.get( o ) == null )
							{
								vv = "    %s %s = %s\n";

								//val = String.format( "[%s] %s", "null", field_type );

								val = "null";
							}
							else
							{
								if( field_type.equals( "java.lang.String" ) )
								{
									vv = "    %s %s = \"%s\"\n";

									val = String.format( "%s", field.get( o ) );

									val = val.replaceAll( "\n", "[\\\\n]" );
								}
								else
								{
									vv = "    %s %s = %s\n";

									//val = field_type;

									val = field.get( o ).toString();

									//val = val.replaceAll( "\n", "<nl>" );
								}
							}
						}

						s = String.format( vv, field_type, var, val );

						buf.append( s );
					}
					catch( IllegalAccessException ex )
					{
						vv = "    %s %s = %s\n";

						s = String.format( vv, field_type, var, "[IllegalAccessException]" );

						buf.append( s );
					}
				}
			}

			//buf.append( "\n--------------------------------------------\n\n" );

			if( ! recurse ) break;

			c = c.getSuperclass();
		}

		return buf.toString().trim();
	}
*/


	private static boolean is_field_static( Field field )
	{
		int mods = field.getModifiers();

		return Modifier.isStatic( mods );
	}


	public static Map<String, String> get_state( Object o )
	{
		return get_state( o, o.getClass(), false );
	}


	public static Map<String, String> get_state( Class c )
	{
		return get_state( null, c, false );
	}


	public static Map<String, String> get_state( Object o, boolean recurse )
	{
		return get_state( o, o.getClass(), recurse );
	}


	public static Map<String, String> get_state( Class c, boolean recurse )
	{
		return get_state( null, c, recurse );
	}


	private static Map<String, String> get_state( Object o, Class c, boolean recurse )// throws IllegalAccessException
	{
		Map<String, String> map = new LinkedHashMap<String, String>();

		while( true )
		{
			if( c == null ) break;

			Field[] fields = c.getDeclaredFields();

			AccessibleObject.setAccessible( fields, true );

			for( Field field : fields )
			{
				if( is_field_static( field ) == ( o == null ) )
				{
					try
					{
						String field_type = field.getType().toString();

						field_type = field_type.replace( "class ", "" );

						String var = fmt( "%s %s", field_type, field.getName() );

						Object val = field.get( o );

						if( val == null )
						{
							map.put( var, null );
						}
						else
						{
							map.put( var, val.toString() );
						}
					}
					catch( IllegalAccessException ex )
					{
					}
				}
			}

			//buf.append( "\n--------------------------------------------\n\n" );

			Method[] methods = c.getDeclaredMethods();

			for( Method method : methods )
			{
				if( method.isAnnotationPresent( (Class<? extends Annotation>) Mappable.class ) )
				{
					String var = method.getName();

					try
					{
						Object val = method.invoke( o );

						if( val == null )
						{
							map.put( var, null );
						}
						else
						{
							map.put( var, val.toString() );
						}
					}
					catch( IllegalAccessException ex )
					{
						map.put( var, null );
					}
					catch( InvocationTargetException ex )
					{
						map.put( var, null );
					}
				}
			}

			if( ! recurse ) break;

			c = c.getSuperclass();
		}

		return map;
	}


	public static String object_to_xml( Object o )
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		XMLEncoder xmlenc = new XMLEncoder( baos );

		xmlenc.writeObject( o );

		xmlenc.close();

		String xml = baos.toString();

		//cdebug.println( xml );

		return xml;
	}


	public static Object xml_to_object( String xml )
	{
		XMLDecoder xmldec = new XMLDecoder( new ByteArrayInputStream( xml.getBytes() ) );

		return xmldec.readObject();

	}


	public static Object call_method( Object target, String method_name, Object... args ) throws Exception
	{
		Method method = get_method( target, method_name );

		return method.invoke( target, args );
	}


	public static Object call_cstyle_getter( Object target, String field ) throws Exception
	{
		return call_method( target, "get_" + field );
	}


	public static void call_cstyle_setter( Object target, String field, Object value ) throws Exception
	{
		call_method( target, "set_" + field, value );
	}


	public static Method get_method( Object target, String method_name ) throws Exception
	{
		return get_method( target.getClass(), method_name );
	}


	public static Method get_method( Class target, String method_name ) throws Exception
	{
		Method[] methods = target.getDeclaredMethods();

		AccessibleObject.setAccessible( methods, true );

		cverbose.println( "Searching class %s for method %s", target.getName(), method_name );

		for( Method method : methods )
		{
			cverbose.println( "Checking method: %s", method.getName() );

			if( method_name.equals( method.getName() ) )
			{
				cverbose.println( "found" );

				return method;
			}
		}

		cverbose.println( "not found" );

		throw new NoSuchMethodException( fmt( "couldn't find method: %s.%s", target.getName(), method_name ) );
	}


	public static void sleep( long millis )
	{
		long end = systime() + millis;

		while( millis > 0 )
		{
			try
			{
				Thread.currentThread().sleep( millis );
			}
			catch( InterruptedException ex )
			{
				cerr.println( ex );
			}

			millis = end - systime();
		}
	}


	/**
	* <p> Starts a new thread runnning the specified Runnable's run method
	* @param Runnable the object to run
	* @return the new thread (already started)
	*/
	public static Thread fork( Runnable runnable )// throws Exception
	{
		Thread thread = Executors.defaultThreadFactory().newThread( runnable );

		thread.start();

		return thread;
	}


	public static Thread fork( final Object target, final String method, final Object... args ) throws Exception
	{
		check( target != null ).on_fail().raise_arg( "target arg is null" );

		check( ! LibText.empty( method ) ).on_fail().raise_arg( "method arg is empty" );

		Class type = target.getClass();

		Class[] param_types = new Class[ args.length ];

		for( int i=0; i<param_types.length; i++ )
		{
			param_types[ i ] = args[ i ].getClass();
		}

		final Method call = type.getMethod( method, param_types );

		check( call != null ).on_fail().raise_value( "Could not locate method in class %s with signature %s( %s )", type.getName(), method, cat( "", param_types, "", ", " ) );

		Runnable runnable = new Runnable()
		{
			public void run()
			{
				try
				{
					call.invoke( target, args );
				}
				catch( Exception ex )
				{
					cerr.println( retrace( ex ) );
				}
			}
		};

		Thread thread = new Thread( runnable );

		thread.start();

		return thread;
	}


	public static void fast_array_copy( Object src, Object dest, int length )
	{
		System.arraycopy( src, 0, dest, 0, length );
	}


	public static void slow_array_copy( Object[] src, int src_pos, Object[] dst, int dst_pos )
	{
		check( src != null ).on_fail().raise_arg( "Can't copy array element references: src is null" );

		check( dst != null ).on_fail().raise_arg( "Can't copy array element references: dst is null" );

		check( src_pos >= 0 ).on_fail().raise_arg( "Can't copy array element references: invalid src_pos (%d)", src_pos );

		check( src_pos < src.length ).on_fail().raise_value( "Can't copy array element references: invalid src_pos (%d) for src array length (%d)", src_pos, src.length );

		check( dst_pos >= 0 ).on_fail().raise_arg( "Can't copy array element references: invalid dst_pos (%d)", dst_pos );

		check( dst_pos < dst.length ).on_fail().raise_value( "Can't copy array element references: invalid dst_pos (%d) for dst array length (%d)", dst_pos, dst.length );

		TODO( "more checks for slow_array_copy method" );

		IDEA( "if dst array is null create it and return" );

		for( int i1=src_pos; i1<src.length; i1++ )
		{
			int i2 = i1 - src_pos + dst_pos;

			dst[ i2 ] = src[ i1 ];
		}
	}


	public static boolean is_a( Object o, Class c )
	{
		return is_a( o.getClass(), c );
	}


	public static boolean is_a( Class oc, @SuppressWarnings("rawtypes") Class c )
	{
		cverbose.println( "Checking if object of class [%s] is an instance of class [%s]", oc.getName(), c.getName() );

		if( oc.equals( c ) ) return true;

		cverbose.println( "Checking interfaces" );

		for( Class ic : oc.getInterfaces() )
		{
			cverbose.println( "Checking interface: %s", ic.getName() );

			if( ic.equals( c ) ) return true;
		}

		Class sc = oc.getSuperclass();

		cverbose.println( "Checking superclass: %s", sc );

		if( sc == null ) return false;

		return is_a( sc, c );
	}


	public static boolean inherited_from( Class sub, Class sup )
	{
		return is_a( sub, sup );
	}



	/**
	*	throws a IllegalArgumentException if false but doesn't exit the jvm
	*/
	public static void expect_arg( boolean b )
	{
		if( b ) return;

		IllegalArgumentException ex =  new IllegalArgumentException( trace_call( 3, "Expectation Failed at: %f:%l" ) );

		ex.setStackTrace( trace( 3 ) );

		throw ex;
	}


	/**
	*	throws a IllegalStateException if false but doesn't exit the jvm
	*/
	public static void expect_state( boolean b )
	{
		if( b ) return;

		IllegalStateException ex =  new IllegalStateException( trace_call( 3, "Expectation Failed at: %f:%l" ) );

		ex.setStackTrace( trace( 3 ) );

		throw ex;
	}


	/**
	<p> used to check that the final (or intermediate) state of the system is 'b'
	*/
	public static void require( boolean b )
	{
		if( b ) return;

		IllegalStateException error =  new IllegalStateException( trace_call( 3, "Requirement not met at: %f:%l" ) );

		error.setStackTrace( trace( 3 ) );

		throw error;
	}


	/**
	<p> used to check that the final (or intermediate) state of the system is 'b'
	*/
	public static void require( boolean b, String err_msg_fmt, Object... err_msg_args )
	{
		if( b ) return;

		IllegalStateException error =  new IllegalStateException( trace_call( 3, "Requirement not met at: %f:%l" ) + ": " + String.format( err_msg_fmt, err_msg_args ) );

		error.setStackTrace( trace( 3 ) );

		throw error;
	}


	public static void new_method_test()
	{
		cinfo.println( "new_method_test()" );
	}


	public static void check_arg( boolean pass ) throws IllegalArgumentException
	{
		if( pass ) return;
		
		IllegalArgumentException ex = new IllegalArgumentException( "Invalid argument" );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}
	

	public static void check_state( boolean pass ) throws IllegalStateException
	{
		if( pass ) return;
		
		IllegalStateException ex = new IllegalStateException( "Invalid state" );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}


	public static void check_arg( boolean pass, String err_msg, Object... err_msg_args ) throws IllegalArgumentException
	{
		if( pass ) return;
		
		IllegalArgumentException ex = new IllegalArgumentException( fmt( err_msg, err_msg_args ) );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}
	

	public static void check_state( boolean pass, String err_msg, Object... err_msg_args ) throws IllegalStateException
	{
		if( pass ) return;
		
		IllegalStateException ex = new IllegalStateException( fmt( err_msg, err_msg_args ) );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}
	
	
	public static Check check( boolean value_to_check )
	{
		return Check.get( value_to_check );
	}
	
	
	public static void runtime_check( boolean pass )
	{
		runtime_check( pass, "" );
	}
	
	
	public static void runtime_check( boolean pass, String err_msg, Object... err_msg_args )
	{
		if( pass ) return;
		
		RuntimeException ex = new RuntimeException( fmt( err_msg, err_msg_args ) );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}


	public static void compile_check( boolean pass )
	{
		runtime_check( pass, "" );
	}
	

	public static void compile_check( boolean pass, String err_msg, Object... err_msg_args ) throws Exception
	{
		if( pass ) return;
		
		Exception ex = new Exception( fmt( err_msg, err_msg_args ) );
		
		ex.setStackTrace( trace( 3 ) );
		
		throw ex;
	}
	

	public static void raise_arg( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ArgumentException( msg );
	}


	public static void raise_value( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ValueException( msg );
	}


	public static void raise_state( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new StateException( msg );
	}


	public static void raise_operation( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new OperationException( msg );
	}


	public static void fatal( int exit_code, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		cerr.println( msg );

		cdebug.println( trace( Thread.currentThread().getStackTrace(), 2 ) );

		exit( exit_code );
	}


	public static void raise_arg( Throwable cause, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ArgumentException( msg, cause );
	}


	public static void raise_value( Throwable cause, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ValueException( msg, cause );
	}


	public static void raise_state( Throwable cause, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new StateException( msg, cause );
	}


	public static void raise_operation( Throwable cause, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new OperationException( msg, cause );
	}


	public static void fatal( Throwable cause, int exit_code, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		cerr.println( msg );

		cdebug.println( trace( Thread.currentThread().getStackTrace(), 2 ) );

		exit( exit_code );
	}


	public static String gen_uuid()
	{
		return UUID.randomUUID().toString();
	}

/*
	public static int compare( Comparable c1, Comparable c2 )
	{
		require( c1 != null );

		require( c2 != null );

		return c1.compareTo( c2 );
	}
*/

	public static <T extends Comparable> int compare( T t1, T t2 )
	{
		check_state( t1 != null );
		
		check_state( t2 != null );
		
		return t1.compareTo( t2 );
	}
}
