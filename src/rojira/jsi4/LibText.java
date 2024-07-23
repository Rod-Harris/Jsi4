package rojira.jsi4;

import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibConsole.cwarn;
//import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.raise_operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rojira.jsi4.util.text.EString;


public class LibText
{
	private LibText(){}


	public static String[] lines( String text )
	{
		return text.split( "\n" );
	}


	public static String fmt( String message_format, Object... args )
	{
		return String.format( message_format, args );
	}


	public static String sprintf( String message_format, Object... args )
	{
		return String.format( message_format, args );
	}


	public static <T> String cat( String pre, T[] data, String post, String delim )
	{
		String s = "";

		for( T o : data )
		{
			String os = null;

			if( o != null ) os = o.toString();

			s += pre + os + post + delim;
		}

		if( s.endsWith( delim ) ) s = s.substring( 0, s.length() - delim.length() );

		return s;
	}


	public static <T> String cat( List<T> data, String delim )
	{
		return cat( "", data, "", delim );
	}


	public static <T> String cat( String pre, List<T> data, String post, String delim )
	{
		String s = "";

		for( T o : data )
		{
			s += pre + o.toString() + post + delim;
		}

		if( s.endsWith( delim ) ) s = s.substring( 0, s.length() - delim.length() );

		return s;
	}


	public static int len( String s )
	{
		if( s == null ) return -1;

		return s.length();
	}


	public static boolean empty( String s )
	{
		if( s == null || s.length() == 0 ) return true;

		return false;
	}


	public static boolean has_data( String s )
	{
		return ! empty( s );
	}


	public static <T> String ncat( String pre, T data, String post, String delim, int num )
	{
		String value = null;

		if( data != null ) value = data.toString();

		if( value == null ) value = "[null]";

		String s = "";

		for( int i=0; i<num; i++ )
		{
			s += pre + data.toString() + post + delim;
		}

		if( s.endsWith( delim ) ) s = s.substring( 0, s.length() - delim.length() );

		return s;
	}


	/**
	 * <p> Returns new String( data )
	 *
	 * @throws NullPointerException if data is null
	 */
	public static String str( byte[] data )
	{
		return new String( data );
	}


	/**
	 * <p> Returns o.toString()
	 *
	 * @throws NullPointerException if o is null
	 */
	public static String str( Object o )
	{
		if( o == null ) return "null";

		return o.toString();
	}


	/**
	 * <p> Returns Arrays.toString( array )
	 *
	 * @throws NullPointerException if o is null
	 */
	public static <T> String str( T[] array )
	{
		return substring( Arrays.toString( array ), 1, -1 );
	}


	public static String substring( String s, int si, int ei )
	{
		if( ei < 0 ) ei += s.length();

		return s.substring( si, ei );
	}


	/**
	 * <p> Returns functionality similar to Arrays.toString( array )
	 *
	 * @throws NullPointerException if o is null
	 */
	public static <T> String str( Iterable<T> list )
	{
		String s = "[";

		for( T item : list )
		{
			s += item;

			s += ", ";
		}

		if( s.endsWith( ", " ) )
		{
			s = s.substring( 0, s.length() -2 );
		}

		s += "]";

		return s;
	}


	/**
	 * <p> If data ends with end it returns data - end
	 * <p> eg:
	 * <p>   data = "1, 2, 3, 4, 5, 6, "
	 * <p>   end = ", "
	 * <p> returns:
	 * <p>   "1, 2, 3, 4, 5, 6"
	 * <p> eg:
	 * <p>   data = "123abc"
	 * <p>   end = "abcdef123456"
	 * <p> returns:
	 * <p>   "123"
	 */
	public static String trim_end( String data, String end )
	{
		if( end.length() > data.length() )
		{
			int oi = overlap_index( data, end );

			if( oi == -1 ) return data;

			return data.substring( 0, oi );
		}

		if( data.endsWith( end ) )
		{
			return data.substring( 0, data.length() - end.length() );
		}

		return data;
	}


	public static String trim_end( String data, int num_chars )
	{
		if( data.length() <= num_chars ) return "";

		return data.substring( 0, data.length() - num_chars );
	}



	public static String left( String s, int i )
	{
		if( i <= 0 ) return "";

		if( i >= s.length() ) return s;

		return s.substring( 0, i );
	}


	public static String right( String s, int i )
	{
		if( i <= 0 ) return s;

		if( i >= s.length() ) return "";

		return s.substring( i );
	}

	/**
	 * <p> Return everything to the left of the first occurance of s2 in s or null if s2 doesn't occur in s
	*/
	public static String left_of_first( String s, String s2 )
	{
		return left( s, s.indexOf( s2 ) );
	}

	/**
	 * <p> Return everything to the left of the last occurance of s2 in s or null if s2 doesn't occur in s
	*/
	public static String left_of_last( String s, String s2 )
	{
		return left( s, s.lastIndexOf( s2 ) );
	}


	public static String reverse_line_order( String s )
	{
		String[] lines = s.split( "\n" );

		EString rv = new EString();

		for( int i=lines.length-1; i>=0; i-- )
		{
			rv.println( lines[ i ] );
		}

		return rv.toString();
	}


	public static String capitalise( String s )
	{
		char c = s.charAt( 0 );

		char C = Character.toUpperCase( c );

		return C + s.substring( 1 );
	}


	/**
	 * <p> Use HashMap<String,String> named_regex( String data, String named_regex ) instead
	 */
	@Deprecated
	public static HashMap<String, String> match( String data, String format )
	{
		cwarn.println( "%s id deprecated", "HashMap<String, String> match( String data, String format )" );

		//System.err.println( "data = " + data );

		//System.err.println( "format = " + format );

		if( data == null ) return null;

		if( format == null ) return null;

		String tokens_exp = "\\[.+?\\]";

		Pattern tkns = Pattern.compile( tokens_exp );

		if( ! format.matches( tokens_exp ) )
		{
			cverbose.println( "format doesn't match tokens regex" );

			return null;
		}

		Matcher tmatcher = tkns.matcher( format );

		ArrayList<String> vars = new ArrayList<String>();

		while( tmatcher.find() )
		{
			String var = tmatcher.group();

			var = var.substring( 1, var.length() - 1 );

			//System.err.printf( "var = %s\n", var );

			vars.add( var );
		}

		Pattern ptn = Pattern.compile( tmatcher.replaceAll( "(.+)" ) );

		//System.err.println( "ptn = " +  ptn.pattern() );

		Matcher matcher = ptn.matcher( data );

		ArrayList<String> vals = new ArrayList<String>();

		if( matcher.find() )
		{
			for( int i=1; i<=matcher.groupCount(); i++ )
			{
				String val = matcher.group( i );

				//System.err.printf( "val = %s\n", val );

				vals.add( val );
			}
		}


		if( vars.size() != vals.size() ) return null;

		HashMap<String, String> map = new HashMap<String, String>();

		for( int i=0; i<vars.size(); i++ )
		{
			map.put( vars.get( i ), vals.get( i ) );
		}

		//System.err.println( map );

		return map;
	}



	/**
	 * <p> This is an experimental method - it may not work at all, or return incorrect values
	 * <p> eg:
	 * <p> String data = "http://www.server.com.au:9090/file.mp3";
	 * <p> String named_regex = "(proto)://(host):(port)/(file).(ext)";
	 *
	 * @return a map containing all the names groups matched
	 * <p> eg: {port=9090, host=www.google.com.au, file=file, ext=mp3, proto=http}
	 */
	public static HashMap<String,String> named_regex( String data, String named_regex ) throws Exception
	{
		cwarn.println( "%s is experimental", "HashMap<String, String> match( String data, String format )" );

		//String regex = named_regex.replaceAll( "[${]\\w+[}]", "(.+)" );

		cverbose.println( "data = %s", data );

		cverbose.println( "named_regex = %s", named_regex );

		String regex = named_regex.replace( ".", "[.]" ).replaceAll( "[(]\\w+[)]", "(.+)" ).replace( " ", "\\s" );

		String regex2 = named_regex.replace( ".", "[.]" ).replaceAll( "[(]\\w+[)]", "(.+)" ).replace( " ", "\\s" ).replace( ".+", "\\w+" ).replace( "(", "([(]" ).replace( ")", "[)])" );

		//String regex2 = "(.*[(]\\w+[)].*)*";

		regex = "^" + regex + "$";

		regex2 = "^" + regex2 + "$";

		cverbose.println( "regex = %s", regex );

		cverbose.println( "regex2 = %s", regex2 );

		//String[] groups1 = regex( named_regex, ".*([(]\\w+[)])+?.*" );

		String[] groups1 = regex( named_regex, regex2 );

		cverbose.println( "groups1 = %s", str( groups1 ) );

		String[] groups2 = regex( data, regex );

		cverbose.println( "groups2 = %s", str( groups2 ) );

		if( groups1 == null ) return null;

		if( groups2 == null ) return null;

		cverbose.println( "groups1.length = %d", groups1.length );

		cverbose.println( "groups2.length = %d", groups2.length );

		if( groups1.length != groups2.length )
		{
			return null;
		}

		HashMap<String,String> map = new HashMap<String,String>();

		for( int i=0; i<groups1.length; i++ )
		{
			if( groups1[ i ].matches( "^[(]\\w+[)]$" ) )
			{
				String key = groups1[ i ].replace( "(", "" ).replace( ")", "" );

				String old_value = map.get( key );

				String value = groups2[ i ];

				if( old_value != null && ! old_value.equals( value ) )
				{
					raise_operation( "2 values for %s: %s and %s are not equal", key, value, old_value );
				}

				map.put( key, value );
			}
		}

		cverbose.println( map );

		return map;
	}


	/**
	 * <p> Compile the regex, match it against the data
	 *
	 * @return all matching groups as a String array
	 * @return null if regex doesn't match the data
	 */
	public static String[] regex( String data, String regex )
	{
		cverbose.println( "match pattern: %s", regex );

		Pattern p = Pattern.compile( regex );

		cverbose.println( "compiled pattern: %s", p );

		cverbose.println( "against data: %s", data );

		Matcher m = p.matcher( data );

		if( ! m.matches() )
		{
			cverbose.println( "no match" );

			return null;
		}

		cverbose.println( "%d matching groups found", m.groupCount() + 1 );

		String[] groups = new String[ m.groupCount() + 1 ];

		for( int gi=0; gi<=m.groupCount(); gi++ )
		{
			groups[ gi ] = m.group( gi );
		}

		return groups;
	}


	/**
	 * <p> Returns s concatenated m times
	 */
	public static String mult( String s, int m )
	{
		EString es = new EString();

		for( int i=0; i<m; i++ )
		{
			es.print( s );
		}

		return es.toString();
	}


	/**
	 * <p> Splits the test string into lines (splits on the \n character)
	 * <p> Returns the subset of lines that match the search
	 * <p> Note it wraps the search term with .* at the beginning and end then does a regexp match
	*/
	public static String[] grep( String test, String search )
	{
		return grep( test.split( "\n" ), search );
	}


	/**
	 * <p> Returns the subset of lines that match the search
	 * <p> Note it wraps the search term with .* at the beginning and end then does a regexp match
	 */
	public static String[] grep( String[] test_lines, String search )
	{
		ArrayList<String> matching_lines = new ArrayList<String>();

		for( String line : test_lines )
		{
			cverbose.println( "line %s matcher %s match %b", line, search, ( line.indexOf( search ) != -1 ) );

			//if( line.indexOf( search ) != -1 ) matching_lines.add( line );

			if( line.matches( ".*" + search + ".*" ) ) matching_lines.add( line );
		}

		return list_to_array( matching_lines );
	}


	/**
	 * <p> Just a wrapper around list.toArray
	 */
	public static String[] list_to_array( List<String> list )
	{
		return list.toArray( new String[ list.size() ] );
	}


	/**
	 * <p> Cuts the string s into tokens on the delimiter d and returns the token at index f (f starts at 0)
	 * <p> Returns null if f is positive and there is no token at index f
	 *
	 * <p> eg cut( "rod", "j", 0 ) == "rod"
	 * <p> eg cut( "rod", "j", 1 ) == null
	 * <p> eg cut( "rod", "o", 0 ) == "r"
	 * <p> eg cut( "rod", "o", 1 ) == "d"
	 * <p> eg cut( "rod", "o", 2 ) == null
	 */
	public static String cut( String s, String d, int f )
	{
		String[] tokens = s.split( d );

		if( tokens.length >= f + 1 )
		{
			return tokens[ f ];
		}

		return null;
	}


	public static String trim_start( String data, String start )
	{
		if( data.startsWith( start ) )
		{
			return data.substring( start.length() );
		}

		return data;
	}

	public static String trim_start( String data, int num_chars )
	{
		if( data.length() <= num_chars ) return "";

		return data.substring( num_chars );
	}


	public static String trim( String start, String text, String end )
	{
		text = trim_start( text, start );

		text = trim_end( text, end );

		return text;
	}


	public static int overlap_index( String s1, String s2 )
	{
		for( int i1=0; i1<s1.length(); i1++ )
		{
			String cs1 = s1.substring( i1 );

			String cs2 = s2;

			if( s2.length() > cs1.length() )
			{
				cs2 = s2.substring( 0, cs1.length() );
			}

			cverbose.println( "checking '%s' for occurance of '%s'", cs1, cs2 );

			int index = cs1.indexOf( cs2 );

			if( index != -1 )
			{
				cverbose.println( "overlap_index of '%s' in '%s' is %d", s2, s1, index + i1 );

				return index + i1;
			}
		}

		return -1;
	}



/*





	private static void load_plurals() throws Exception
	{
		plurals = new HashMap<String,String>();

		String plurals_filedata = load_text_resource( jsi3.lib.text.Statics.class, "plurals.txt" );

		//cverbose.println( plurals_filedata );

		for( String line : plurals_filedata.split( "\n" ) )
		{
			if( no_data( line ) ) continue;

			String[] tokens = line.trim().split( "\\s+" );

			state_check( tokens.length == 2, "couldn't split line in plurals file into 2 tokens" );

			plurals.put( tokens[ 0 ], tokens[ 1 ] );

			cverbose.println( "adding known plural %s for singular %s", tokens[ 1 ], tokens[ 0 ] );
		}
	}


	public static String pluralise( String singular ) throws Exception
	{
		if( plurals == null )
		{
			load_plurals();
		}

		// known cases

		for( String key : plurals.keySet() )
		{
			if( singular.endsWith( key ) )
			{
				return trim_end( singular, key ) + plurals.get( key );
			}
		}

		// general rules (not 100% applicable)

		if( singular.endsWith( "s" ) || singular.endsWith( "x" ) || singular.endsWith( "z" ) || singular.endsWith( "o" ) )
		{
			return singular + "es";
		}
		else if( singular.endsWith( "ch" ) || singular.endsWith( "sh" ) )
		{
			return singular + "es";
		}
		else if( singular.endsWith( "ife" ) )
		{
			return trim_end( singular, 3 ) + "ives";
		}
		else if( singular.endsWith( "y" ) )
		{
			return trim_end( singular, 1 ) + "ies";
		}
		else if( singular.endsWith( "f" ) )
		{
			return trim_end( singular, 1 ) + "ves";
		}

		return singular + "s";
	}
*/

}
