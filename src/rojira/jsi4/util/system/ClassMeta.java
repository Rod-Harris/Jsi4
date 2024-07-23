package rojira.jsi4.util.system;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;


import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibDevel.*;
import rojira.jsi4.util.devel.*;


public class ClassMeta
{
	public final Class type;

	Method[] declared_methods;


	public ClassMeta( Class type )
	{
		this.type = type;
	}


	public String toString()
	{
		return type.getName();
	}

	public boolean extends_type( Class super_type ) throws NotImplementedException
	{
		check( super_type != null ).on_fail().raise_arg( "'super_type' parameter is null" );

		NOT_IMPLEMENTED();

		return false;
	}


	public <T extends Annotation> Method[] get_methods_with_annotation_type( Class<T> annotation_type )
	{
		check( annotation_type != null ).on_fail().raise_arg( "'annotation_type' parameter is null" );

		ArrayList<Method> out_list = new ArrayList<Method>();

		if( declared_methods == null )
		{
			declared_methods = type.getDeclaredMethods();

			AccessibleObject.setAccessible( declared_methods, true );
		}

		for( Method method : declared_methods )
		{
			if( method.getAnnotation( annotation_type ) != null )
			{
				out_list.add( method );
			}
		}

		return _array( out_list, Method.class );
	}


	public void set( String field_name, String value ) throws NoSuchFieldException, IllegalAccessException
	{
		set( null, field_name, value );
	}
	

	public void set( Object o, String field_name, String value ) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = type.getDeclaredField( field_name );

		field.setAccessible( true );

		Type field_type = field.getType();

		if( field_type == boolean.class || field_type == Boolean.class )
		{
			field.setBoolean( o, _boolean( value ) );
		}
		else if( field_type == byte.class || field_type == Byte.class  )
		{
			field.setByte( o, _byte( value ) );
		}
		else if( field_type == char.class || field_type == Character.class  )
		{
			field.setChar( o, _char( value ) );
		}
		else if( field_type == short.class || field_type == Short.class  )
		{
			field.setShort( o, _short( value ) );
		}
		else if( field_type == int.class || field_type == Integer.class  )
		{
			field.setInt( o, _int( value ) );
		}
		else if( field_type == long.class || field_type == Long.class  )
		{
			field.setLong( o, _long( value ) );
		}
		else if( field_type == float.class || field_type == Float.class  )
		{
			field.setFloat( o, _float( value ) );
		}
		else if( field_type == double.class || field_type == Double.class  )
		{
			field.setDouble( o, _double( value ) );
		}
		else if( field_type == String.class)
		{
			if( value != null )
			{
				field.set( o, value.toString() );
			}
			else
			{
				field.set( o, value );
			}
		}
		else
		{
			field.set( o, value );
		}
	}


	public void set( String field_name, Object value ) throws NoSuchFieldException, IllegalAccessException
	{
		set( null, field_name, value );
	}

	
	public void set( Object o, String field_name, Object value ) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = type.getDeclaredField( field_name );

		field.setAccessible( true );

		field.set( o, value );
	}
}
