package rojira.jsi4.modules.mainopt;

import java.util.*;

import rojira.jsi4.util.text.*;

import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;


public class ArgParser
{
	String[] args;

	ArrayList<Flag> flags = new ArrayList<Flag>();

	ArrayList<Flag[]> mutex_groups = new ArrayList<Flag[]>();

	Flag no_flag = new Flag( new String[]{} );


	public ArgParser( String[] args )
	{
		this.args = args;

		no_flag.set_description( "No flag, params passed before the first flag" );
	}


	public boolean has_flag( String... flags )
	{
		return flag_occurances( flags ) > 0;
	}


	private int flag_occurances( String... flags )
	{
		int total = 0;

		for( int i=0; i<args.length; i++ )
		{
			for( int j=0; j<flags.length; j++ )
			{
				if( args[ i ].equals( flags[ j ] ) )
				{
					total ++;
				}
			}
		}

		return total;
	}


	private int flag_index( String... flags )
	{
		for( int i=0; i<args.length; i++ )
		{
			for( int j=0; j<flags.length; j++ )
			{
				if( args[ i ].equals( flags[ j ] ) ) return i;
			}
		}

		return -1;
	}


	public String[] get_params( String... flags )
	{
		return get_params_from( flag_index( flags ) + 1 );
	}


	private String[] get_params_from( int bi )
	{
		ArrayList<String> params = new ArrayList<String>();

		for( int i=bi; i<args.length; i++ )
		{
			String arg = args[ i ];

			if( arg.startsWith( "-" ) && ! is_number( arg ) ) break;

			params.add( arg );
		}

		return params.toArray( new String[ params.size() ] );
	}



/*
	public boolean has_flag( Flag flag )
	{
		return has_flag( flag.flags );
	}
*/


	public void check_flags() throws Exception
	{
		check_flags( true );
	}


	public void check_flags( boolean check_expected ) throws Exception
	{
		no_flag.params = get_params_from( 0 );

		no_flag.present = no_flag.params.length > 0;

		no_flag.check( check_expected );

		for( Flag flag : flags )
		{
			check_flag( flag, check_expected );
		}

		for( int mi=0; mi<mutex_groups.size(); mi++ )
		{
			Flag[] mutex_group = mutex_groups.get( mi );

			int num_occurances = 0;

			for( Flag flag : mutex_group )
			{
				num_occurances += flag_occurances( flag.flags );
			}

			if( num_occurances > 1 ) throw new Exception( fmt( "Flags in mutex group (m%d) %s appear more than once", mi, Arrays.toString( mutex_group ) ) );
		}

		for( String arg : args )
		{
			if( arg.startsWith( "-" ) )
			{
				boolean found = false;

				for( Flag flag : flags )
				{
					for( String sflag : flag.flags )
					{
						if( arg.equals( sflag ) ) found = true;
					}
				}

				if( ! found && ! is_number( arg ) )
				{
					throw new Exception( fmt( "Unknown flag %s", arg ) );
				}
			}
		}
	}


	public static boolean is_number( String s )
	{
		try
		{
			new java.math.BigDecimal( s );

			return true;
		}
		catch( NumberFormatException ex )
		{
			return false;
		}
	}
	

	public void check_flag( Flag flag, boolean check_expected ) throws Exception
	{
		if( flag_occurances( flag.flags ) > 1 ) throw new Exception( fmt( "Flag %s appears more than once", flag ) );

		if( has_flag( flag.flags ) )
		{
			flag.present = true;

			flag.params = get_params( flag.flags );
		}

		flag.check( check_expected );
	}


	public Flag add_flag( String... flags )
	{
		for( String flag : flags )
		{
			check( ! is_number( flag ) ).on_fail().raise_arg( "Flag %s can't consist of numbers, such as %s", Arrays.toString( flags ), flag );

			check( flag.startsWith( "-" ) ).on_fail().raise_arg( "Flag %s needs to start with a '-', %s doesn't", Arrays.toString( flags ), flag );

			check( ! flag.matches( ".*\\s+.*" ) ).on_fail().raise_arg( "Flag %s must contain no whitespace, %s does", Arrays.toString( flags ), flag );
		}

		for( String sflag : flags )
		{
			for( Flag flag : this.flags )
			{
				for( String sflag2 : flag.flags )
				{
					check( ! ( sflag.equals( sflag2 ) ) ).on_fail().raise_value( "Flag %s sub-flag [%s] already present in flag %s", Arrays.toString( flags ), sflag, flag );
				}
			}
		}

		Flag flag = new Flag( flags );

		this.flags.add( flag );

		return flag;
	}


	public void mutex_group( Flag... flags ) throws Exception
	{
		if( flags.length < 1 )
		{
			throw new Exception( "What would be the point of adding one flag to a mutex group?" );
		}

		for( Flag flag : flags )
		{
			flag.add_to_mutex_group( mutex_groups.size() );
		}

		mutex_groups.add( flags );
	}


	public String usage()
	{
		EString es = new EString();

		es.println( no_flag.usage() );

		for( Flag flag : flags )
		{
			es.println( flag.usage() );
		}

		return es.toString();
	}
}
