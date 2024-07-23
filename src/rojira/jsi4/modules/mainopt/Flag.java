package rojira.jsi4.modules.mainopt;


import java.util.*;

import rojira.jsi4.util.text.*;

import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;


/**
 * <p> eg: Flag opt = new Flag( "
 */
public class Flag
{
	String[] flags;

	String description = "";

	int num_params = 0;

	boolean expected = false;

	String[] param_names;

	String[] params;

	boolean present;

	ArrayList<String> mutex_indices = new ArrayList<String>();

	int shift;

	/**
	 * <p> Creates a new command line flag that is activated by any of the specified strings
	 * <p> Eg new Flag( "-h", "--help", "-u", "--uasge", "-?" );
	 */
	public Flag( String... activation_flags )
	{
		this.flags = activation_flags;
	}


	/**
	 * <p> Initialisation method: sets the description for when the application usage is displayed
	 */
	public Flag set_description( String description )
	{
		this.description = "\t\t" + description;

		return this;
	}


	/**
	 * <p> Initialisation method: adds another line to the description for when the application usage is displayed
	 */
	public Flag add_to_description( String description )
	{
		this.description += "\n\t\t" + description;

		return this;
	}


	/**
	 * <p> Initialisation method: sets the number of expected params to 0
	 */
	public Flag expects_params()
	{
		expects_params( 0 );

		return this;
	}


	/**
	 * <p> Initialisation method: sets the number of parameters this flag expects
	 * <p> default: 0
	 * <p> any number: -1
	 * <p> exact number: n
	 */
	public Flag expects_params( int num_params )
	{
		this.num_params = num_params;

		params = null;

		return this;
	}


	/**
	 * <p> Initialisation method: sets the number and names of the parameters associated with this flag
	 * <p> calling this with null sets the number of expected params to -1 (any number)
	 */
	public Flag expects_params( String... param_names )
	{
		if( param_names == null )
		{
			expects_params( -1 );
		}
		else
		{
			this.num_params = param_names.length;

			this.param_names = param_names;
		}

		return this;
	}


	/**
	 * <p> initialisation method: sets whether this flag is required
	 * <p> if it is, and its not present, an exception is thrown on the check method
	 */
	public Flag expected()
	{
		this.expected = true;

		return this;
	}


	/**
	 * <p> validates this flag against the command line arguments
	 */
	public void check( boolean check_expected ) throws Exception
	{
		if( is_present() )
		{
			int num_params = params.length;

			if( this.num_params == -1 )
			{
				if( num_params == 0 )
				{
					throw new Exception( fmt( "flag %s expects at least one param", this ) );
				}
			}
			else
			{
				if( num_params != this.num_params )
				{
					throw new Exception( fmt( "flag %s expects %d params, %d found", this, this.num_params, num_params ) );
				}
			}
		}
		else // not present
		{
			if( check_expected && this.expected )
			{
				throw new Exception( fmt( "Expected flag %s not present", this ) );
			}
		}
	}


	/**
	 * <p> is this flag present in the command line args
	 */
	public boolean is_present()
	{
		return present;
	}


	/**
	 * <p> retruns the parameter shift to its default setting
	 */
	public void reset_shift()
	{
		shift = 0;
	}


	/**
	 * <p> shifts the param indexes forward by 1
	 */
	public void shift() throws Exception
	{
		shift( 1 );
	}


	/**
	 * <p> shifts the param indexes forward by n
	 */
	public void shift( int n ) throws Exception
	{
		shift += n;

		if( shift >= params.length ) throw new Exception( fmt( "Flag %s - can't shift that far - not that many parameters", Arrays.toString( flags ) ) );
	}


	/**
	 * <p> shifts the param indexes back by 1
	 */
	public void unshift() throws Exception
	{
		unshift( 1 );
	}

	/**
	 * <p> shifts the param indexes back by n
	 */
	public void unshift( int n ) throws Exception
	{
		shift -= n;

		if( shift < 0 ) throw new Exception( fmt( "Flag %s - can't unshift to a negative parameter index", Arrays.toString( flags ) ) );
	}


	/**
	 * <p> gets the param by name
	 * <p> this flag must have been initialised with the expects_params( String... params ) method for this to be successful
	 * <p> this method is NOT sensitive to shifts
	 */
	public String get_param( String param_name ) throws Exception
	{
		if( num_params == 0 )
		{
			throw new Exception( fmt( "Flag %s - can't get param named %s - flag expects no params", Arrays.toString( flags ), param_name ) );
		}

		if( param_names == null || param_names.length == 0 )
		{
			throw new Exception( fmt( "Flag %s - can't get param named %s - params not named", Arrays.toString( flags ), param_name ) );
		}

		for( int i=0; i<param_names.length; i++ )
		{
			if( param_name.equals( param_names[ i ] ) )
			{
				return params[ i ];
			}
		}

		throw new Exception( fmt( "Flag %s - can't get param named %s - doesn't exist", Arrays.toString( flags ), param_name ) );
	}


	/**
	 * <p> gets the params by name
	 * <p> this flag must have been initialised with the expects_params( String... params ) method for this to be successful
	 * <p> this method is NOT sensitive to shifts
	 */
	public String[] get_params( String... param_names ) throws Exception
	{
		String[] sparams = new String[ param_names.length ];

		int si = 0;

		for( String param_name : param_names )
		{
			for( int i=0; i<this.param_names.length; i++ )
			{
				if( param_name.equals( this.param_names[ i ] ) )
				{
					sparams[ si++ ] = params[ i ];
				}
			}

			throw new Exception( fmt( "Flag %s - can't get param named %s - doesn't exist", Arrays.toString( flags ), param_name ) );
		}

		return sparams;
	}


	/**
	 * <p> gets the param by name, parsed as an int
	 * <p> this method is NOT sensitive to shifts
	 */
	public int get_int( String param_name ) throws Exception
	{
		String param = get_param( param_name );

		try
		{
			return _int( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as an integer", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> gets the params by name, parsed as an ints
	 * <p> this method is NOT sensitive to shifts
	 */
	public int[] get_ints( String... param_names ) throws Exception
	{
		int[] iparams = new int[ param_names.length ];

		for( int i=0; i<iparams.length; i++ )
		{
			iparams[ i ] = get_int( param_names[ i ] );
		}

		return iparams;
	}


	/**
	 * <p> gets the param by name, parsed as a double
	 * <p> this method is NOT sensitive to shifts
	 */
	public double get_double( String param_name ) throws Exception
	{
		String param = get_param( param_name );

		try
		{
			return _double( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as a double", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> gets the param by name, parsed as a double
	 * <p> this method is NOT sensitive to shifts
	 */
	public double[] get_doubles( String... param_names ) throws Exception
	{
		double[] dparams = new double[ param_names.length ];

		for( int i=0; i<dparams.length; i++ )
		{
			dparams[ i ] = get_double( param_names[ i ] );
		}

		return dparams;
	}



	/**
	 * <p> Gets the next parameter
	 * <p> this method is sensitive to shifts
	 */
	public String get_param() throws Exception
	{
		if( num_params == 0 )
		{
			throw new Exception( fmt( "Flag %s - can't get first - flag expects no params", Arrays.toString( flags ) ) );
		}

		return get_param( 0 );
	}


	/**
	 * <p> Get the specified parameter
	 * <p> this method is sensitive to shifts
	 */
	public String get_param( int i ) throws Exception
	{
		i += shift;

		if( i < 0 || i >= params.length )
		{
			throw new Exception( fmt( "Flag %s - can't get param at index %d - valid range is 0 -> %d", Arrays.toString( flags ), i - shift, params.length - shift ) );
		}

		return params[ i ];
	}


	/**
	 * <p> Get all remaining parameters
	 * <p> this method is sensitive to shifts
	 */
	public String[] get_params() throws Exception
	{
		return get_params( params.length );
	}


	/**
	 * <p> Get all remaining parameters
	 * <p> this method is sensitive to shifts
	 */
	public String[] get_params( int how_many ) throws Exception
	{
		if( how_many > params.length - shift )
		{
			throw new Exception( fmt( "Flag %s cant return number of parameters asked for [%d], only [%d] are present", Arrays.toString( flags ), how_many, params.length - shift ) );
		}

		String[] sparams = new String[ how_many ];

		for( int i=0; i<how_many; i++ )
		{
			sparams[ i ] = params[ i ];
		}

		return sparams;
	}


	/**
	 * <p> Get next parameter parsed as an int
	 * <p> this method is sensitive to shifts
	 */
	public int get_int() throws Exception
	{
		String param = get_param();

		try
		{
			return _int( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as an integer", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> Get the specified parameter parsed as an int
	 * <p> this method is sensitive to shifts
	 */
	public int get_int( int i ) throws Exception
	{
		String param = get_param( i );

		try
		{
			return _int( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as an integer", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> Get next parameter up to the specified parameter parsed as ints
	 * <p> this method is sensitive to shifts
	 */
	public int[] get_ints( int how_many ) throws Exception
	{
		if( how_many >= params.length - shift )
		{
			throw new Exception( fmt( "Flag %s get ints, asked for %d, but only %d are present", Arrays.toString( flags ), how_many, params.length - shift ) );
		}

		int[] iparams = new int[ how_many ];

		for( int i=0; i<how_many; i++ )
		{
			iparams[ i ] = get_int( i );
		}

		return iparams;
	}


	/**
	 * <p> Get next parameter parsed as a double
	 * <p> this method is sensitive to shifts
	 */
	public double get_double() throws Exception
	{
		String param = get_param();

		try
		{
			return _double( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as a double", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> Get the specified parameter parsed as a double
	 * <p> this method is sensitive to shifts
	 */
	public double get_double( int i ) throws Exception
	{
		String param = get_param( i );

		try
		{
			return _double( param );
		}
		catch( Exception ex )
		{
			throw new Exception( fmt( "Flag %s couln't parse value %s as a double", Arrays.toString( flags ), param ) );
		}
	}


	/**
	 * <p> Get next parameter up to the specified parameter parsed as doubles
	 * <p> this method is sensitive to shifts
	 */
	public double[] get_doubles( int how_many ) throws Exception
	{
		if( how_many >= params.length - shift )
		{
			throw new Exception( fmt( "Flag %s get ints, asked for %d, but only %d are present", Arrays.toString( flags ), how_many, params.length - shift ) );
		}

		double[] dparams = new double[ how_many ];

		for( int i=0; i<how_many; i++ )
		{
			dparams[ i ] = get_double( i );
		}

		return dparams;
	}



	/**
	 * <p> Add this flag to a mutex group
	 * <p> if multiple flags from the same mutex group are present an exception is throws when the flags are checked
	 * <p> a flag can belong to mutiple mutex groups
	 */
	public void add_to_mutex_group( int mutex_index )
	{
		mutex_indices.add( "m" + mutex_index );
	}


	/**
	 * <p> Returns the activation_flags passed to the constructor as a single line string
	 */
	public String toString()
	{
		return Arrays.toString( flags );
	}


	/**
	 * <p> returns the usage for this flag, based on activation_flags, expected_parameters, mutex_groups and its description
	 */
	public String usage()
	{
		EString es = new EString();

		if( flags.length == 0 )
		{
			es.print( "\t[no flag]" );
		}
		else
		{
			es.print( "\t" + cat( "", flags, "", ", " ) );
		}

		if( param_names != null && param_names.length != 0 )
		{
			es.print( " %s", cat( "<", param_names, ">", " " ) );
		}
		else
		{
			if( num_params == MainOpt2.ANY_NUMBER ) es.print( " <arg>..." );

			if( num_params > 0 ) es.print( " " + ncat( "", "<arg>", "", " ", num_params ) );
		}

		if( ! expected ) es.print( " (optional)" );

		es.println();
/*
		if( mutex_indices.size() > 0 )
		{
			es.print( " (Mutex Groups: %s)", toString( mutex_indices ) );
		}
*/

		es.println( description );

		return es.toString();
	}


	private String toString( ArrayList list )
	{
		if( list.size() == 0 ) return "[]";

		EString es = new EString();

		es.print( "[" );

		for( int i=0; i<list.size() -1; i++ )
		{
			es.print( list.get( i ) + ", " );
		}

		es.print( list.get( list.size() -1 ) );

		es.print( "]" );

		return es.toString();
	}
}
