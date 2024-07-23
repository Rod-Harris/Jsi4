package rojira.jsi4;

import static rojira.jsi4.LibConsole.cdebug;
import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibConsole.cverbose;
import static rojira.jsi4.LibSystem._int;
import static rojira.jsi4.LibSystem.check;
import static rojira.jsi4.LibSystem.raise_arg;
import static rojira.jsi4.LibSystem.require;
import static rojira.jsi4.LibSystem.retrace;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import rojira.jsi4.util.system.ThreadCache;
import rojira.jsi4.util.time.CronEntry;
import rojira.jsi4.util.time.CronRunner;
import rojira.jsi4.util.time.Timer;


public class LibTime
{
	/**
	 * <p> Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec
	 */
	public static final String[] MONTH_SHORT_NAMES = "Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec".split( "\\s" );

	/**
	 * <p> January February March April May June July August September October November December
	 */
	public static final String[] MONTH_NAMES = "January February March April May June July August September October November December".split( "\\s" );

	public static final String[] DAY_SHORT_NAMES = "Mon Tue Wed Thu Fri Sat Sun".split( "\\s" );

	public static final String[] DAY_NAMES = "Monday Tuesday Wednesday Thursday Friday Saturday Sunday".split( "\\s" );

	public static final int[] DAYS_IN_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	//public static enum Unit{ YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS };

	public static final HashSet<CronEntry> cron_entries = new HashSet<CronEntry>();

	private static final ThreadCache<Calendar> calendars_cache = new ThreadCache<Calendar>();

	private static java.util.Timer cron_timer;

	private static final ThreadCache<Timer> stopwatches = new ThreadCache<Timer>();

	/**
		<p>Add a very simple cron entry
		<p>you can add the same entry any number of times - subsequent calls with the same object will be ignored
		<p>all 5 time fields must be either integers or * no other special characters or lines or Names are parsed yet
		<p>every minute the entry is asked for its cron-entry string (so changes take place automatically)
		<p>if the cron entry is matched the entry's run method is called in a new thread
	*/
	public static void add_cron_entry( CronEntry entry )
	{
		check( entry.get_entry() != null ).on_fail().raise_arg( "cron entry is empty" );

		String[] entry_tokens = entry.get_entry().split( "\\s+" );

		check( entry_tokens.length == 5 ).on_fail().raise_arg( "Cron entries must have 5 tokens" );

		validate_cron_token( entry_tokens[ 0 ], "Minute", 0, 59 );

		validate_cron_token( entry_tokens[ 1 ], "Hour", 0, 23 );

		validate_cron_token( entry_tokens[ 2 ], "Day Of Month", 1, 31 );

		validate_cron_token( entry_tokens[ 3 ], "Month", 1, 12 );

		validate_cron_token( entry_tokens[ 4 ], "Day Of Week", 0, 6 );

		cron_entries.add( entry );
	}


	private static void validate_cron_token( String token, String name, int min, int max )
	{
		try
		{
			if( "*".equals( token ) ) return;

			int value = _int( token );

			check( value >= min && value <= max ).on_fail().raise_arg( name + " token expects values between " + min + " and " + max + ", actual was " + token );
		}
		catch( Exception ex )
		{
			raise_arg( "Couddn't parse " + name + " token as an integer: " + ex.getMessage() );
		}
	}


	public static void start_cron_timer()
	{
		check( cron_timer == null ).on_fail().raise_state( "Cron timer already started" );

		Date now = now();

		int seconds = second( now );

		int millis = millisecond( now );

		cdebug.println( "%02d.%03d", seconds, millis );

		int wait = 61500 - ( seconds * 1000 + millis );

		if( wait > 60000 ) wait -= 60000;

		cron_timer = new java.util.Timer();

		cron_timer.scheduleAtFixedRate( new CronRunner(), wait, 60000L );

		cverbose.println( "wait %dms to run cron", wait );

		Runtime.getRuntime().addShutdownHook
		(
			new Thread()
			{
				public void run()
				{
					cverbose.println( "running shutdown hook to cancel cron timer thread" );

					cron_timer.cancel();
				}
			}
		);
	}



/*
	public static long milliseconds( Unit unit )
	{
		long milliseconds = 1;

		if( unit.ordinal() <= Unit.MILLISECONDS.ordinal() )
		{
			milliseconds *= 1;
		}

		if( unit.ordinal() <= Unit.SECONDS.ordinal() )
		{
			milliseconds *= 1000;
		}

		if( unit.ordinal() <= Unit.MINUTES.ordinal() )
		{
			milliseconds *= 60;
		}

		if( unit.ordinal() <= Unit.HOURS.ordinal() )
		{
			milliseconds *= 60;
		}

		if( unit.ordinal() <= Unit.DAYS.ordinal() )
		{
			milliseconds *= 24;
		}

		if( unit.ordinal() == Unit.MONTHS.ordinal() )
		{
			throw new IllegalArgumentException( "Months don't have equals lengths" );
		}

		if( unit.ordinal() <= Unit.YEARS.ordinal() )
		{
			//throw new IllegalArgumentException( "Years don't have equals lengths" );

			milliseconds *= 365;
		}

		return milliseconds;
	}


	public static double seconds( Unit unit )
	{
		return 0.001 * milliseconds( unit );
	}


	public static final String datestamp()
	{
		return datestamp( now() );
	}


	public static final String datestamp( long unix_time )
	{
		return datestamp( new Date( unix_time ) );
	}


	public static final String datestamp( Date date )
	{
		return timestamp( date, Unit.DAYS );
	}


	public static final String timestamp()
	{
		return timestamp( now() );
	}


	public static final String timestamp( Unit max_precision )
	{
		return timestamp( now(), max_precision );
	}


	public static final String timestamp( long unix_time )
	{
		return timestamp( new Date( unix_time ) );
	}


	public static final String timestamp( long unix_time, Unit max_precision )
	{
		return timestamp( new Date( unix_time ), max_precision );
	}


	public static final String timestamp( Date date )
	{
		return timestamp( date, Unit.MILLISECONDS );
	}


	public static final String timestamp( Date date, Unit max_precision )
	{
		return timestamp( date, max_precision, "/", " ", ":" );
	}

	public static final String timestamp( Date date, Unit max_precision, String date_separator, String date_time_separator, String time_separator )
	{
		Calendar c = new GregorianCalendar();

		c.setTime( date );

		int year = c.get( Calendar.YEAR );

		int month = c.get( Calendar.MONTH ) + 1;

		int day = c.get( Calendar.DAY_OF_MONTH );

		int hour = c.get( Calendar.HOUR_OF_DAY );

		int minute = c.get( Calendar.MINUTE );

		int second = c.get( Calendar.SECOND );

		int millisecond = c.get( Calendar.MILLISECOND );

		int microsecond = 0;

		int nanosecond = 0;

		EString es = new EString();

		es.print( "%d", year );

		if( max_precision.ordinal() >= Unit.MONTHS.ordinal() )
		{
			es.print( date_separator + "%02d", month );
		}

		if( max_precision.ordinal() >= Unit.DAYS.ordinal() )
		{
			es.print( date_separator + "%02d", day );
		}

		if( max_precision.ordinal() >= Unit.HOURS.ordinal() )
		{
			es.print( date_time_separator + "%02d", hour );
		}

		if( max_precision.ordinal() >= Unit.MINUTES.ordinal() )
		{
			es.print( time_separator + "%02d", minute );
		}

		if( max_precision.ordinal() >= Unit.SECONDS.ordinal() )
		{
			es.print( time_separator+ "%02d", second );
		}

		if( max_precision.ordinal() >= Unit.MILLISECONDS.ordinal() )
		{
			es.print( ".%03d", millisecond );
		}

		return es.toString();
	}


	public static final String format_timestamp( Date date, String fmt )
	{
		Calendar c = new GregorianCalendar();

		c.setTime( date );

		int year = c.get( Calendar.YEAR );

		int month = c.get( Calendar.MONTH ) + 1;

		int day = c.get( Calendar.DAY_OF_MONTH );

		int hour = c.get( Calendar.HOUR_OF_DAY );

		int minute = c.get( Calendar.MINUTE );

		int second = c.get( Calendar.SECOND );

		int millisecond = c.get( Calendar.MILLISECOND );

		int microsecond = 0;

		int nanosecond = 0;

		fmt = fmt.replace( "YYYY", fmt( "%04d", year ) );

		fmt = fmt.replace( "MM", fmt( "%02d", month ) );

		fmt = fmt.replace( "DD", fmt( "%02d", day ) );

		fmt = fmt.replace( "hh", fmt( "%02d", hour ) );

		fmt = fmt.replace( "mm", fmt( "%02d", minute ) );

		fmt = fmt.replace( "mss", fmt( "%03d", millisecond ) );

		fmt = fmt.replace( "ss", fmt( "%02d", second ) );

		fmt = fmt.replace( "ms", fmt( "%d", millisecond ) );

		fmt = fmt.replace( "YY", fmt( "%d", year ) );

		fmt = fmt.replace( "M", fmt( "%d", month ) );

		fmt = fmt.replace( "D", fmt( "%d", day ) );

		fmt = fmt.replace( "h", fmt( "%d", hour ) );

		fmt = fmt.replace( "m", fmt( "%d", minute ) );

		fmt = fmt.replace( "s", fmt( "%d", second ) );

		if( hour < 12 )
		{
			fmt = fmt.replace( "ap", "am" );

			fmt = fmt.replace( "AP", "AM" );

			fmt = fmt.replace( "Ap", "Am" );
		}
		else
		{
			fmt = fmt.replace( "ap", "pm" );

			fmt = fmt.replace( "AP", "PM" );

			fmt = fmt.replace( "Ap", "Pm" );
		}

		return fmt;
	}
	*/

	/**
	 * <p> Get the current date and time
	*/
	public static Date now()
	{
		return new Date();
	}


	/**
	 * <p> Get todays date for the time at 12 midnight (00 hours)
	*/
	public static Date today()
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.set( c.HOUR_OF_DAY, 0 );

		c.set( c.MINUTE, 0 );

		c.set( c.SECOND, 0 );

		c.set( c.MILLISECOND, 0 );

		return c.getTime();
	}


	public static Date set_time( Date date, int hour, int minute, int second )
	{
		return set_time( date, hour, minute, second, 0 );
	}


	public static Date set_time( Date date, int hour, int minute, int second, int millisecond )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		c.set( c.HOUR_OF_DAY, hour );

		c.set( c.MINUTE, minute );

		c.set( c.SECOND, second );

		c.set( c.MILLISECOND, millisecond );

		return c.getTime();
	}

	/**
	 * <p> Get todays date for the given time
	*/
	public static Date today( int hour, int minute )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.set( c.HOUR_OF_DAY, hour );

		c.set( c.MINUTE, minute );

		c.set( c.SECOND, 0 );

		c.set( c.MILLISECOND, 0 );

		return c.getTime();
	}


	/**
	 * <p> get todays date for the given time
	*/
	public static Date today( int hour, int minute, int second )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.set( c.HOUR_OF_DAY, hour );

		c.set( c.MINUTE, minute );

		c.set( c.SECOND, second );

		c.set( c.MILLISECOND, 0 );

		return c.getTime();
	}


	/**
	 * <p> get the given date at 12 midnight (00 hours)
	 */
	public static Date date( int year, int month, int day )
	{
		GregorianCalendar gc = new GregorianCalendar( year, month - 1, day );

		return gc.getTime();
	}


	/**
	 * <p> get the given date and time
	 */
	public static Date date( int year, int month, int day, int hour, int minute, int second )
	{
		GregorianCalendar gc = new GregorianCalendar( year, month - 1, day, hour, minute, second );

		return gc.getTime();
	}


	/**
	 * <p> add n days to the given date
	 * <p> n can be negative
	 * <p> will modify other fields as required
	 */
	public static Date add( Date d, int n, int field )
	{
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime( d );

		cal.add( field, n );

		return cal.getTime();
	}


	/**
	 * <p> add n days to the given date
	 * <p> n can be negative
	 * <p> will modify other fields as required
	 */
	public static Date add_days( Date d, int n )
	{
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime( d );

		cal.add( cal.DAY_OF_MONTH, n );

		return cal.getTime();
	}


	/**
	 * <p> add n months to the given date
	 * <p> n can be negative
	 * <p> will modify other fields as required
	 */
	public static Date add_months( Date d, int n )
	{
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime( d );

		cal.add( cal.MONTH, n );

		return cal.getTime();
	}


	/**
	 * <p> add n years to the given date
	 * <p> n can be negative
	 * <p> will modify other fields as required
	 */
	public static Date add_years( Date d, int n )
	{
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime( d );

		cal.add( cal.MONTH, n );

		return cal.getTime();
	}

	/**
	 * get the hour field from the specified date (0-23)
	 */
	public static int hour( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.HOUR_OF_DAY );
	}


	/**
	 * get the minute field from the specified date
	 */
	public static int minute( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.MINUTE );
	}


	/**
	 * get the second field from the specified date
	 */
	public static int second( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.SECOND );
	}


	/**
	 * get the millisecond field from the specified date
	 */
	public static int millisecond( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.MILLISECOND );
	}

	/**
	 * get the day_of_month field from the specified date (1-[28,29,30,31])
	 */
	public static int day( Date date )
	{
		return day_of_month( date );
	}


	/**
	 * get the day_of_month field from the specified date (1-[28,29,30,31])
	 */
	public static int day_of_month( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.DAY_OF_MONTH );
	}

	/**
	 * get the day_of_week field from the specified date (1-[28,29,30,31])
	 */
	public static int day_of_week( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.DAY_OF_WEEK );
	}

	/**
	 * get the month field from the specified date (1-12)
	 */
	public static int month( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.MONTH ) + 1;
	}


	/**
	 * get the year field from the specified date
	 */
	public static int year( Date date )
	{
		Calendar c = calendars_cache.get();

		if( c == null ) c = calendars_cache.put( new GregorianCalendar() );

		c.setTime( date );

		return c.get( Calendar.YEAR );
	}


	// -------------------------------   Timing functions


	/**
	Starts (or restarts) a timer for the current thread
	@throws IllegalStateException if calls to stopwatch_start are nested (ie. 2 calls to stopwatch_start happen without a call to stopwatch_stop in between)
	*/
	public static void stopwatch_start()
	{
		Timer timer = stopwatches.get();

		if( timer == null )
		{
			timer = new Timer();

			stopwatches.put( timer );
		}
		else
		{
			check( ! timer.counting() ).on_fail().raise_state( "Can't nest stopwatches" );

			timer.restart();
		}
	}


	/**
	Stops the timer for the current thread
	@return the time in milliseconds between the call to stopwatch_start and stopwatch_stop
	@throws IllegalStateException if no call to stopwatch_start has been made in this thread
	@throws IllegalStateException if a call to stopwatch_start has already been made in this thread, with out a call to stopwatch_start in between
	*/
	public static long stopwatch_stop()
	{
		Timer timer = stopwatches.get();

		check( timer != null ).on_fail().raise_state( "No stopwatch started for this thread" );

		return timer.stop();
	}


	/**
	Creates and starts a new Timer
	*/
	public static Timer start_timer()
	{
		return new Timer();
	}


	// -------------------------------   Parsing Strng to dates, timestamps, etc

/*
	public static Timestamp _timestamp( String date_string )
	{
		String[] time_date_tokens = date_string.split( "\\s+" );

		if( time_date_tokens.length != 2 ) throw new IllegalArgumentException( "Date format should be [hh:mm:ss dd/mm/yyyy]" );

		String[] time_tokens = time_date_tokens[ 0 ].split( ":" );

		if( time_tokens.length != 3 ) throw new IllegalArgumentException( "Time format should be hh:mm:ss" );

		String[] date_tokens = time_date_tokens[ 1 ].split( "/" );

		if( date_tokens.length != 3 ) throw new IllegalArgumentException( "Date format should be dd/mm/yyyy" );

		int hh = _int( time_tokens[ 0 ].trim() );

		int mm = _int( time_tokens[ 1 ].trim() );

		int ss = _int( time_tokens[ 2 ].trim() );

		int dd = _int( date_tokens[ 0 ].trim() );

		int MM = _int( date_tokens[ 1 ].trim() );

		int yyyy = _int( date_tokens[ 2 ].trim() );

		if( yyyy < 100 ) yyyy += 2000;

		return _timestamp( date( yyyy, MM, dd, hh, mm, ss ) );
	}
*/

	public static Timestamp _timestamp( Date d )
	{
		return new Timestamp( d.getTime() );
	}


	public static Date _ddmmyyyy( String date_arg ) throws ParseException
	{
		return parse_date( date_arg, "dd/MM/yyyy" );
	}


/*
	public static Date _date( String date_string )
	{
		String[] time_date_tokens = date_string.split( "\\s+" );

		if( time_date_tokens.length != 2 ) throw new IllegalArgumentException( "Date format should be [hh:mm:ss dd/mm/yyyy]" );

		String[] time_tokens = time_date_tokens[ 0 ].split( ":" );

		if( time_tokens.length != 3 ) throw new IllegalArgumentException( "Time format should be hh:mm:ss" );

		String[] date_tokens = time_date_tokens[ 1 ].split( "/" );

		if( date_tokens.length != 3 ) throw new IllegalArgumentException( "Date format should be dd/mm/yyyy" );

		int hh = _int( time_tokens[ 0 ].trim() );

		int mm = _int( time_tokens[ 1 ].trim() );

		int ss = _int( time_tokens[ 2 ].trim() );

		int dd = _int( date_tokens[ 0 ].trim() );

		int MM = _int( date_tokens[ 1 ].trim() );

		int yyyy = _int( date_tokens[ 2 ].trim() );

		if( yyyy < 100 ) yyyy += 2000;

		return date( yyyy, MM, dd, hh, mm, ss );
	}
*/

	/*
	 * <p> Converts the given date to a calendar and calls the format function on that
	 *
	 * @see #format( Calendar cal, String fmt )

	public static String format( Date date, String fmt )
	{
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime( date );

		return format( cal, fmt );
	}
*/

	/*
	 * <p> This method doesn't quite work properly yet: it has problems determining the correct suffix
	 *
	 * <p> Recognised flags
	 * <ul>
	 * 	<li>DAYNAME</li>
	 * 	<li>DAYOFWEEK</li>
	 * 	<li>DAYOFMONTH</li>
	 * 	<li>SUFFIX</li>
	 * 	<li>MONTHNAME</li>
	 * 	<li>DAY</li>
	 * 	<li>MONTH</li>
	 * 	<li>YEAR</li>
	 * </ul>

	public static String format( Calendar cal, String fmt )
	{
		final String[] days = "Saturday Sunday Monday Tuesday Wednesday Thursday Friday".split( "\\s+" );

		final String[] suffixes = "th st nd rd th th th th th th".split( "\\s+" );

		final String[] months = "January February March April May June July August September October November December".split( "\\s+" );

		fmt = fmt.replace( "DAYNAME", days[ cal.get( cal.DAY_OF_WEEK ) % 7 ] );

		fmt = fmt.replace( "DAYOFWEEK", _string( cal.get( cal.DAY_OF_WEEK ) ) );

		fmt = fmt.replace( "DAYOFMONTH", _string( cal.get( cal.DAY_OF_MONTH ) ) );

		fmt = fmt.replace( "SUFFIX", suffixes[ cal.get( cal.DAY_OF_MONTH ) % 10 ] );// wrong

		fmt = fmt.replace( "MONTHNAME", months[ cal.get( cal.MONTH ) ] );

		fmt = fmt.replace( "DAY", _string( cal.get( cal.DAY_OF_MONTH ) ) );

		fmt = fmt.replace( "MONTH", _string( cal.get( cal.MONTH ) ) );

		fmt = fmt.replace( "YEAR", _string( cal.get( cal.YEAR ) ) );

		return fmt;
	}
*/

	/*
	public static int month_from_short_name( String short_month_name ) throws Exception
	{
		for( int i=0; i<MONTH_SHORT_NAMES.length; i++ )
		{
			if( MONTH_SHORT_NAMES[ i ].equals( short_month_name ) ) return i + 1;
		}

		throw new IllegalArgumentException( fmt( "%s is not a short month name: valid values are %s", short_month_name, str( MONTH_SHORT_NAMES ) ) );
	}


	@Deprecated
	private static int month_from_short_name2( String short_month_name ) throws Exception
	{
		if( "Jan".equals( short_month_name ) ) return 1;

		if( "Feb".equals( short_month_name ) ) return 2;

		if( "Mar".equals( short_month_name ) ) return 3;

		if( "Apr".equals( short_month_name ) ) return 4;

		if( "May".equals( short_month_name ) ) return 5;

		if( "Jun".equals( short_month_name ) ) return 6;

		if( "Jul".equals( short_month_name ) ) return 7;

		if( "Aug".equals( short_month_name ) ) return 8;

		if( "Sep".equals( short_month_name ) ) return 9;

		if( "Oct".equals( short_month_name ) ) return 10;

		if( "Nov".equals( short_month_name ) ) return 11;

		if( "Dec".equals( short_month_name ) ) return 12;

		raise( "No month with short name: %s", short_month_name );

		assert( false );

		return -1;
	}
	*/


	public static Date parse_date( String value, String format ) throws ParseException
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat( format );

			return sdf.parse( value );
		}
		catch( ParseException error )
		{
			cerr.println( "Couldn't parse date value: '%s' against the format: '%s'", value, format );

			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			throw error;
		}
	}


	public static Date _date( long unix_time )
	{
		return new Date( unix_time );
	}


	public static String format_date( long unix_time, String format )
	{
		return format_date( _date( unix_time ), format );
	}


	public static String format_date( Date date, String format )
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat( format );

			return sdf.format( date );
		}
		catch( RuntimeException error )
		{
			cerr.println( "Couldn't format date value: '%s' in the format: '%s'", date, format );

			cerr.println( error.getMessage() );

			cdebug.println( retrace( error ) );

			throw error;
		}
	}


	public static int days_in_month( int year, int month )
	{
		require( month >= 1 );

		require( month <= 12 );

		if( month != 2 )
		{
			return DAYS_IN_MONTH[ month - 1 ];
		}

		GregorianCalendar cal = new GregorianCalendar( year, month-1, 1 );

		if( cal.isLeapYear( year ) )
		{
			return 29;
		}

		return 28;
	}


	private LibTime() {}
}
