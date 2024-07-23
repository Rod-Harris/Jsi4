package rojira.jsi4.modules.mainopt;


import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibIO.*;

import java.util.*;


public class MainOpt4 extends MainOpt2
{
	static LinkedHashMap<String,ArrayList<String>> flag_params = new LinkedHashMap<String,ArrayList<String>>();


	public static void initialise( Class main_class, String[] args ) throws Throwable
	{
		String flag = null;

		ArrayList<String> params = new ArrayList<String>();

		flag_params.put( flag, params );

		for( String arg : args )
		{
			if( arg.startsWith( "-" ) )
			{
				if( ! is_number( arg ) )
				{
					flag = arg;

					params = flag_params.get( flag );

					if( params == null )
					{
						params = new ArrayList<String>();

						flag_params.put( flag, params );
					}

					continue;
				}
			}

			params.add( arg );
		}
	}


	private static boolean is_number( String param )
	{
		try
		{
			_double( param );

			return true;
		}
		catch( NumberFormatException cause )
		{
			return false;
		}
	}
}
