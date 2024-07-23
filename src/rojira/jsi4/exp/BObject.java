package rojira.jsi4.exp;

import java.util.*;

import static rojira.jsi4.LibSystem.*;


public class BObject
{
	private Map<String,Object> meta;

	protected BObject()
	{
		this.meta = create_meta_map();
	}

	protected BObject( Map<String,Object> meta )
	{
		this.meta = meta;
	}

	protected boolean has( String key )
	{
		require( meta != null );

		return meta.containsKey( key );
	}

	protected Object get( String key )
	{
		require( has( key ) );

		return meta.get( key );
	}

	protected void set( String key, Object value )
	{
		meta.put( key, value );
	}

	protected void remove( String key )
	{
		require( has( key ) );

		meta.remove( key );
	}

	protected Map<String,Object> create_meta_map()
	{
		return new LinkedHashMap<String,Object>();
	}
}
