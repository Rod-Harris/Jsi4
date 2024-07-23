package rojira.jsi4.exp;

import static rojira.jsi4.LibSystem.*;
import rojira.jsi4.util.system.ThreadCache;

import java.util.*;


public class MStream
{
	private ThreadCache<LinkedList<Object[]>> objects_list_cache = new ThreadCache<LinkedList<Object[]>>();

	int order;

	MStream()
	{
		this( 1 );
	}

	MStream( int order )
	{
		this.order = order;
	}

	public int push( Object... objects )
	{
		LinkedList<Object[]> objects_list = get_objects_list_for_this_thread();

/*
		for( Object o : objects )
		{
			objects_list.push( o );
		}
*/

		objects_list.push( objects );

		return order * objects.length;
	}


	public Object[] pop()//( int num )
	{
		LinkedList<Object[]> objects_list = get_objects_list_for_this_thread();

		//check( objects_list.size() >= num ).on_fail().raise_state( "Can't pop %d objects: size is only %d", num, objects_list.size() );

		check( objects_list.size() >= 1 ).on_fail().raise_state( "Can't pop any objects: list is empty" );

/*
		Object[] objects = new Object[ num ];

		for( int i=0; i<num; i++ )
		{
			objects[ num - i - 1 ] = objects_list.pop();
		}

		return objects;
*/
		return objects_list.pop();
	}


/*
	public Object pop()
	{
		LinkedList<Object[]> objects_list = get_objects_list_for_this_thread();

		check( objects_list.size() >= 1 ).on_fail().raise_state( "Can't pop an object: list is empty" );

		return objects_list.pop();
	}
*/


	private LinkedList<Object[]> get_objects_list_for_this_thread()
	{
		LinkedList<Object[]> objects_list = objects_list_cache.get();

		if( objects_list != null ) return objects_list;

		objects_list = new LinkedList<Object[]>();

		objects_list_cache.put( objects_list );

		return objects_list;
	}
}
