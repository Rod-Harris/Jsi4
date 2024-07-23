package rojira.jsi4.modules.utest;


import java.util.*;
import java.lang.reflect.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibDevel.*;

import rojira.jsi4.util.text.*;


public class UTest
{
	final Class target_class;

	final Object target_object;

	final String target_method;

	final Method[] all_methods;

	final String trace;

	String expected_method_info;

/*
	Object expected_result = Void.TYPE;

	Class<? extends Throwable> expected_error_type;
*/

	Object expected_result = Void.TYPE;

	ExpectedResultType expected_result_type = ExpectedResultType.VOID;

	Object actual_result;

	Throwable init_error;

	Throwable run_error;

	//Throwable result_error;

	Object[] values;

	Class[] arg_types;

	UTestResult result;

	boolean expecting_result_set;


	UTest( Class target_class, Object target_object, String target_method, String trace )
	{
		cdebug.println( mtrace() );

		this.target_class = target_class;

		this.target_object = target_object;

		this.target_method = target_method;

		this.trace = trace;

		Method[] all_methods = null;

		try
		{
			all_methods = target_class.getDeclaredMethods();
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Couldn't get the declared methods of " + target_class.getName() );

			all_methods = null;
		}

		this.all_methods = all_methods;
	}

	/**
	<p> Specify the arguments to pass to the test method
	*
	@throws IllegalStateException if the arguments have already been set
	*/
	public UTest with_args( Object... values )
	{
		cdebug.println( mtrace( values ) );

		try
		{
			check( this.values == null ).on_fail().raise_state( "This test has already had its arguments set" );

			this.values = values;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}

	/**
	<p> Don't pass any arguments to the test method
	<p> Don't actually need to call this method - it is the default behavoiur
	*
	@throws IllegalStateException if the arguments have already been set
	*/
	public UTest with_no_args()
	{
		cdebug.println( mtrace() );

		try
		{
			check( this.values == null ).on_fail().raise_state( "This test has already had its arguments set" );

			this.values = new Object[ 0 ];
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}

	/**
	<p> Specify the value the test method is expected to return
	*
	@throws IllegalStateException if the expected_error_type or expected_result is already set
	*/
	public UTest expecting_result( Object expected_result )
	{
		cdebug.println( mtrace( expected_result ) );

		try
		{
			check( ! expecting_result_set ).on_fail().raise_state( "This test has already had its expected result set" );

			this.expected_result = expected_result;

			expected_result_type = ExpectedResultType.OBJECT;

			expecting_result_set = true;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}

	/**
	<p> Indicates the test method's return type is 'void'
	<p> Don't actually need to call this method - it is the default behavoiur
	*
	@throws IllegalStateException if the expected_error_type or expected_result is already set
	*/
	public UTest expecting_no_result()
	{
		cdebug.println( mtrace() );

		try
		{
			check( ! expecting_result_set ).on_fail().raise_state( "This test has already had its expected result set" );

			this.expected_result = Void.TYPE;

			expected_result_type = ExpectedResultType.VOID;

			expecting_result_set = true;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}

	/**
	<p> Specify the type of error the test method is expected to throw
	<p> Doesn't have to be exact, more general types than the one actually thrown also will pass
	<p> eg. Specifying Throwable.class here will always pass, provided any type of exception/error is actually thrown
	*
	@throws IllegalStateException if the expected_error_type or expected_result is already set
	*/
	public <T extends Throwable> UTest expecting_error_type( Class<T> expected_error_type )
	{
		cdebug.println( mtrace( expected_error_type ) );

		try
		{
			check( ! expecting_result_set ).on_fail().raise_state( "This test has already had its expected result set" );

			this.expected_result = expected_error_type;

			expected_result_type = ExpectedResultType.ERROR;

			expecting_result_set = true;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}


	/**
	<p> Specify the type of error the test method is expected to throw
	<p> Doesn't have to be exact, more general types than the one actually thrown also will pass
	<p> eg. Specifying Throwable.class here will always pass, provided any type of exception/error is actually thrown
	*
	@throws IllegalStateException if the expected_error_type or expected_result is already set
	*/
	public UTest expecting_result_type( Class expected_return_type )
	{
		cdebug.println( mtrace( expected_return_type ) );

		try
		{
			check( ! expecting_result_set ).on_fail().raise_state( "This test has already had its expected result set" );

			this.expected_result = expected_return_type;

			expected_result_type = ExpectedResultType.CLASS;

			expecting_result_set = true;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}


	public UTest ignore_result()
	{
		cdebug.println( mtrace() );

		try
		{
			check( ! expecting_result_set ).on_fail().raise_state( "This test has already had its expected result set" );

			this.expected_result = null;

			expected_result_type = ExpectedResultType.IGNORE;

			expecting_result_set = true;
		}
		catch( Throwable error )
		{
			init_error = new Exception( "Test Initialisation Error", error );
		}

		return this;
	}


	public UTest run()
	{
		cdebug.println( mtrace() );

		result = new UTestResult( this );

		if( init_error != null ) return this;

		if( values == null ) values = new Object[ 0 ];

		arg_types = new Class[ values.length ];

		for( int i=0; i<values.length; i++ )
		{
			if( values[ i ] != null )
			{
				arg_types[ i ] = values[ i ].getClass();

				if( arg_types[ i ].isPrimitive() )
				{
					arg_types[ i ] = UTestUtils.autobox_type( arg_types[ i ] );
				}
			}
			else
			{
				arg_types[ i ] = null;
			}
		}

		expected_method_info = fmt( "%s.%s(%s)", target_class.getSimpleName(), target_method, inline_class_list( arg_types ) );

/*
		if( expected_result == null )
		{
			String error_messge = fmt( "Expected value has not been set for test '%s.%s(%s)'", target_class.getSimpleName(), target_method, str( arg_types ) );

			run_error = new Exception( error_messge );

			return;
		}
*/

		// locate method

		Method method = locate_method();

		if( method == null )
		{
			String error_messge = fmt( "couldn't find method '%s'", expected_method_info );

			run_error = new Exception( error_messge );

			return this;
		}

		method.setAccessible( true );

		Class return_type = method.getReturnType();

		cdebug.println( "return_type: %s", return_type );

		cdebug.println( "is return type void: %b", same( Void.TYPE, return_type ) );

		try
		{
			cdebug.println( "running method: %s", expected_method_info );

			if( method.isVarArgs() )
			{
				cdebug.println( "method is var args" );

				Class[] ptypes = method.getParameterTypes();

				Class ptype = ptypes[ 0 ].getComponentType();

				cdebug.println( "Casting values to objects of type: " + ptype );

				Object arr = Array.newInstance( ptype, values.length );

				fast_array_copy( values, arr, values.length );

				//cdebug.println( "Parameter Type: " + ptype );

				//cdebug.println( "Parameter Type: " + Array.get( ptypes, 0 ) );

				//Class ptype = ptypes[ 0 ];

				//ptype.cast( values );

				//actual_result = method.invoke( target_object, (Object) values );

				actual_result = method.invoke( target_object, arr );
			}
			else
			{
				actual_result = method.invoke( target_object, values );
			}

			if( actual_result == null && same( Void.TYPE, return_type ) )
			{
				actual_result = Void.TYPE;
			}

			Class actual_result_class = null;

			if( actual_result != null && actual_result != Void.TYPE )
			{
				actual_result_class = actual_result.getClass();
			}

			cdebug.println( "method has successfully completed, returning: (%s) %s", actual_result_class, actual_result );
		}
		catch( IllegalAccessException iaex )
		{
			String error_messge = fmt( "method '%s' is inaccessible", method.toGenericString() );

			run_error = new Exception( error_messge, iaex );

			return this;
		}
		catch( InvocationTargetException itex ) // this could actually be a successful result
		{
			//result_error = itex.getCause();

			actual_result = itex.getCause();

			cdebug.println( "method has thrown the error: %s", actual_result );
		}
		catch( Throwable error )
		{
			String error_messge = fmt( "method '%s' has thrown a runtime error", method.toGenericString() );

			run_error = new Exception( error_messge, error );

			return this;
		}

		return this;
	}


	/**
	Retrieve the results of the method invocation
	*
	@throws IllegalStateException if the test has not been run
	*/
	public Object result()
	{
		check( result != null ).on_fail().raise_state( "Test not run yet" );

		if( run_error != null ) return run_error;

		return actual_result;
	}


	/**
	Retrieve the results of this test
	*
	@throws IllegalStateException if the test has not been run
	*/
	public UTestResult test_result()
	{
		check( result != null ).on_fail().raise_state( "Test not run yet" );

		cverbose.println( this );

		return result;
	}


	private Method locate_method()
	{
		ASSERT( all_methods != null );

		ASSERT( arg_types != null );

		ASSERT( expected_method_info != null );

		cdebug.println( "Searching for method: '%s'", expected_method_info );

		cdebug.println( "Class %s has %d declared methods", target_class.getSimpleName(), all_methods.length );

		try
		{
			cdebug.println( "Trying simple method" );

			Method possible_method = target_class.getMethod( target_method, arg_types );

			ASSERT( possible_method != null ); //should have thrown a NoSuchMethodException

			cdebug.println( "Found! - returning method" );

			return possible_method;
		}
		catch( NoSuchMethodException ex )
		{
			cdebug.println( "No such luck - trying sweep" );
		}

		ArrayList<Method> possible_methods = new ArrayList<Method>();

		for( Method method : all_methods )
		{
			cdebug.println( "Inspecting method: '%s'", method.toGenericString() );

			if( ! equal( method.getName(), target_method ) )
			{
				cdebug.println( "Methods don't have the same name - continuing" );

				continue;
			}

			cdebug.println( "Methods do have the same name - adding" );

			possible_methods.add( method );
		}

		if( possible_methods.size() == 0 )
		{
			cdebug.println( "Couldn't find any methods with name '%s' - returning null", target_method );

			return null;
		}

		if( possible_methods.size() == 1 )
		{
			cdebug.println( "Only found 1 method with name '%s' - returning this one", target_method );

			return possible_methods.get( 0 );
		}

		cdebug.println( "Found %d methods with name '%s' - checking parameter numbers and types", possible_methods.size(), target_method );

		for( Method method : possible_methods )
		{
			Class[] param_types = method.getParameterTypes();

			cdebug.println( "checking %s( %s )", method.getName(), str( param_types ) );

			if( param_types.length != arg_types.length )
			{
				cdebug.println( "Not the same number of parameters" );

				continue;
			}

			boolean same_params = true;

			cdebug.println( "checking parameter types" );

			for( int i=0; i<arg_types.length; i++ )
			{
				if( param_types[ i ].isPrimitive() )
				{
					param_types[ i ] = UTestUtils.autobox_type( param_types[ i ] );
				}

				cdebug.println( "   param[ %d ] = %s (expecting %s)", i, param_types[ i ], arg_types[ i ] );

				if( ! same( param_types[ i ], arg_types[ i ] ) )
				{
					cdebug.println( "      parameters are different" );

					same_params = false;

					break;
				}
			}

			if( same_params == true )
			{
				cdebug.println( "name and parameter types (signatures) are the same: method found" );

				return method;
			}
		}

		cdebug.println( "no method found" );

		return null;
	}


	public String inline_class_list( Class... classes )
	{
		String s = "";

		for( Class c : classes )
		{
			String csn = null;

			if( c != null ) csn = c.getSimpleName();

			s += csn + ", ";
		}

		s = " " + trim_end( s, ", " ) + " ";

		return s;
	}


	public String toString()
	{
		EString out = new EString();

		out.println( "Test: " );
		out.println( "  Definition: " );
		out.println( "    Class     : %s", target_class );
		out.println( "    Object    : %s", target_object );
		out.println( "    Method    : %s", target_method );
		out.println( "    Parameters: " );
		for( int i=0; i<values.length; i++ )
		{
			out.println( "      %s: %s", arg_types[ i ], values[ i ] );
		}
		out.println( "    Expecting : %s", expected_result );
		out.println( "  Result: " );
		if( init_error != null )
		{
			out.println( "    Error     : %s", init_error );
		}
		else if( run_error != null )
		{
			out.println( "    Error     : %s", run_error );
		}
		else
		{
			out.println( "    Actual    : %s", actual_result );
		}

		return str( out );
	}
}

enum ExpectedResultType
{
	VOID, OBJECT, CLASS, ERROR, IGNORE
}
