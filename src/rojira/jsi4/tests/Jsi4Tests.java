package rojira.jsi4.tests;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibIO.read_resource;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.inspect;
import static rojira.jsi4.LibSystem.require;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;

import rojira.jsi4.LibConsole;
import rojira.jsi4.LibDevel;
import rojira.jsi4.LibIO;
import rojira.jsi4.LibMaths;
import rojira.jsi4.LibSystem;
import rojira.jsi4.LibText;
import rojira.jsi4.LibTime;
import rojira.jsi4.LibXML;
import rojira.jsi4.modules.utest.UTestResult;
import rojira.jsi4.modules.utest.UTestSuite;
import rojira.jsi4.modules.utest.UTestUtils;
import rojira.jsi4.util.io.WGet;
import rojira.jsi4.util.system.ObjectDataLoader;

/**
 * <p> Test suite for Jsi4
 */
public final class Jsi4Tests extends UTestUtils
{
	static UTestSuite suite = new UTestSuite();

	/**
	<p> Returns the number of tests that have had problems (ie. invalid initialisation, runtime errors, of failed expectations).
	<p> Note if a test throws an exception, but the expected error type has been set (and is correct) this is not an error, as the expectation has been met.
	*/
	public static void run_tests() throws Throwable
	{
		jsi4_tests();

		lib_system_tests();

		lib_text_tests();

		lib_io_tests();



		int problem_count = suite.run_tests();

		check( problem_count == 0 ).on_fail().raise_operation( "Test Suite has encountered %d problems", problem_count );
	}


	private static void jsi4_tests()
	{
		suite.set_target( Jsi4Tests.class );

		suite.test_method( "test_lib_class_constructors" ).with_args( LibConsole.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibDevel.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibIO.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibMaths.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibSystem.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibText.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibTime.class ).expecting_no_result();

		suite.test_method( "test_lib_class_constructors" ).with_args( LibXML.class ).expecting_no_result();

	}


	/**
	Assert that the specified lib class has exactly 1 private constructor
	@throws IllegalStateExcpetion if the above condition is not met
	*/
	private static void test_lib_class_constructors( Class lib_class )
	{
		Constructor[] constructors = lib_class.getDeclaredConstructors();

		assert_not_null( constructors );

		assert_true( constructors.length == 1 );

		Constructor constructor = constructors[ 0 ];

		assert_not_null( constructor );

		int modifiers = constructor.getModifiers();

		assert_equal( ( modifiers & Modifier.PRIVATE ), Modifier.PRIVATE );
	}


	private static void lib_text_tests()
	{
		UTestResult result = null;

		suite.set_target( LibText.class );

		suite.test_method( "overlap_index" ).with_args( "123abc", "abcdef123456" ).expecting_result( 3 );

		suite.test_method( "overlap_index" ).with_args( "123abc", "ab" ).expecting_result( 3 );

		suite.test_method( "overlap_index" ).with_args( "abcdef123456", "123456" ).expecting_result( 6 );

		suite.test_method( "overlap_index" ).with_args( "abcdef123456", "123abc" ).expecting_result( -1 );

		suite.test_method( "trim_end" ).with_args( "1, 2, 3, ", ", " ).expecting_result( "1, 2, 3" );

		suite.test_method( "trim_end" ).with_args( "1, 2, 3, ", "123" ).expecting_result( "1, 2, 3, " );

		suite.test_method( "trim_end" ).with_args( "123abc", "abcdef123456" ).expecting_result( "123" );

		suite.test_method( "trim_end" ).with_args( "123abc", 3 ).expecting_result( "123" );

		suite.test_method( "trim_start" ).with_args( "123abc", 3 ).expecting_result( "abc" );

		suite.test_method( "trim_end" ).with_args( "123abc", 6 ).expecting_result( "" );

		suite.test_method( "trim_start" ).with_args( "123abc", 6 ).expecting_result( "" );


/*
		suite.check_result( result ).test_ran();

		suite.check_result( result ).threw_nothing();

		//suite.check_result( result ).threw_error_type( Throwable.class );

		suite.check_result( result ).returned_type( int.class );

		suite.check_result( result ).returned_type( Integer.class );

		//suite.check_result( result ).returned_nothing(  );

		suite.check_result( result ).equal_to( 3 );

		//suite.check_result( result ).same_as( 3 );

		cverbose.println( suite.check_result( result ) );
*/
	}


	private static void lib_io_tests()
	{
		suite.set_target( new WGet( "http://www.google.com.au" ) );

		suite.test_method( "retrieve_headers" ).with_no_args().expecting_result( true );

		suite.test_method( "retrieve_data" ).with_no_args().expecting_result( true );

		suite.test_method( "http_status_code" ).with_no_args().expecting_result( 200 );

	}



	private static void lib_system_tests() throws Throwable
	{
		Map<Class,Object[]> parsed_objects = ObjectDataLoader.parse_data( read_resource( Jsi4Tests.class, "data.txt" ) );

		require( parsed_objects != null );

		Object[] data_objects = parsed_objects.get( DataObject.class );

		require( data_objects != null );

		require( data_objects.length == 2 );

		require( data_objects[ 0 ] != null );

		require( data_objects[ 0 ] instanceof DataObject );

		DataObject data_object_0 = (DataObject) data_objects[ 0 ];

		cdebug.println( inspect( data_object_0 ) );

		require( data_object_0.f == 1.1f );

		require( data_object_0.d == 2.2 );

		require( data_object_0.b == 1 );

		require( data_object_0.s == 2 );

		require( data_object_0.i == 3 );

		require( data_object_0.l == 4 );

		require( data_object_0.c == 'C' );

		require( data_object_0.bl == true );

		require( data_object_0.st.equals( "Hello" ) );

		require( data_objects[ 1 ] != null );

		require( data_objects[ 1 ] instanceof DataObject );

		DataObject data_object_1 = (DataObject) data_objects[ 1 ];

		cdebug.println( inspect( data_object_1 ) );

		require( data_object_1.f == 9.9f );

		require( data_object_1.d == 10.1 );

		require( data_object_1.b == 5 );

		require( data_object_1.s == 6 );

		require( data_object_1.i == 7 );

		require( data_object_1.l == 8 );

		require( data_object_1.c == 'A' );

		require( data_object_1.bl == false );

		require( data_object_1.st.equals( "Goodbye" ) );
	}
}


