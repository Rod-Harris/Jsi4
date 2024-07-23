package rojira.jsi4.util.system;

import java.util.*;


public class ThreadCache<T>
{
	private final HashMap<Thread,T> cache = new HashMap<Thread,T>();

	public T get()
	{
		Thread thread = Thread.currentThread();

		return cache.get( thread );
	}

	public T put( T t )
	{
		Thread thread = Thread.currentThread();

		cache.put( thread, t );

		return t;
	}
}

