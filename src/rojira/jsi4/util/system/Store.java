package rojira.jsi4.util.system;

import static rojira.jsi4.LibDevel.*;
import java.util.*;


public class Store
{
	/**
	*	inserts a new object into the underlying store
	*/
	public <T extends PObject> T put( T pobject )
	{
		NOT_IMPLEMENTED();

		return null;
	}

	/**
	*	retrieves the specified Object from the store
	*/
	public <T extends PObject> T get( Class<T> pclass, String uuid )
	{
		NOT_IMPLEMENTED();

		return null;
	}

	/**
	*	re-stores an existing object
	*/
	public <T extends PObject> T update( T pobject )
	{
		NOT_IMPLEMENTED();

		return null;
	}

	/**
	*	refreshes an existing object from the store
	*/
	public <T extends PObject> T refresh( T pobject )
	{
		NOT_IMPLEMENTED();

		return null;
	}

	/**
	*	retrieves all Object from the store that match the condition
	*/
	public <T extends PObject> List<T> get_all( Class<T> pclass, String condition )
	{
		NOT_IMPLEMENTED();

		return null;
	}
}
