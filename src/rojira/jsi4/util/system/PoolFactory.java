package rojira.jsi4.util.system;

import static rojira.jsi4.LibSystem.*;
import java.util.*;

public class PoolFactory<T extends Poolable>
{
	private final HashSet<T> pool = new HashSet<T>();

	private final Class<T> type;

	private final T base;

	private Iterator<T> iterator;


	public PoolFactory( Class<T> type )
	{
		expect_arg( type != null );

		this.type = type;

		this.base = null;
	}


	public PoolFactory( T base )
	{
		expect_arg( base != null );

		this.base = base;

		this.type = null;
	}


	public void push( T t )
	{
		expect_arg( t != null );

		if( pool.add( t ) )
		{
			iterator = pool.iterator();
		}
	}


	public T pop() throws InstantiationException, IllegalAccessException
	{
		if( iterator == null )
		{
			iterator = pool.iterator();
		}

		if( iterator.hasNext() )
		{
			T next = iterator.next();

			iterator.remove();

			next.reset();

			return next;
		}

		if( base != null )
		{
			T t = (T) base.clone();

			require( t != base );

			t.set_factory( this );

			return t;
		}

		require( type != null );

		T t = type.newInstance();

		t.set_factory( this );

		return t;
	}
}
