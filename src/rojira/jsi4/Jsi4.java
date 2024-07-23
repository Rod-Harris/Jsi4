package rojira.jsi4;

import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibText.empty;

import rojira.jsi4.util.system.*;
import java.lang.reflect.*;
/**
 * <p>
 *
 * @author Rod Harris
 */
public class Jsi4 extends Main
{
	public static void run_example( Class examples_class, String example_name ) throws Throwable
	{
		check( ! empty( example_name ) ).on_fail().raise_arg( "'example_name' parameter is empty" );

		ClassMeta inspector = inspect( examples_class );

		if( "?".equals( example_name ) )
		{
			//cout.println( "Listing Examples for class: %s", examples_class.getName() );

			cout.println( "%s:", examples_class.getSimpleName() );

			cout.println( "\t*: run all examples in order" );

			for( Method method : inspector.get_methods_with_annotation_type( Example.class ) )
			{
				Example example = method.getAnnotation( Example.class );

				cout.println( "\t%s: %s", example.value(), example.description() );
			}
		}
		else
		{
			//cout.println( "Listing Examples for class: %s", examples_class.getName() );

			cout.println( "%s:", examples_class.getSimpleName() );

			for( Method method : inspector.get_methods_with_annotation_type( Example.class ) )
			{
				Example example = method.getAnnotation( Example.class );

				if( "*".equals( example_name ) || example.value().equals( example_name ) )
				{
					method.invoke( null );
				}
			}
		}
	}
}
