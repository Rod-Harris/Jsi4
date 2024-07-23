package rojira.jsi4.util.system;

import java.util.*;

public class PObject
{
	private String id;

	public PObject()
	{
		synchronized( PObject.class )
		{
			//id = fmt( "%x-%x", systime(), System.nanoTime() );

			id = UUID.randomUUID().toString();
		}
	}

	void set_id( String id )
	{
		this.id = id;
	}

	String get_id()
	{
		return this.id;
	}
}
