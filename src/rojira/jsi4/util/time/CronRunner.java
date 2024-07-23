package rojira.jsi4.util.time;


import java.util.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibTime.*;
import static rojira.jsi4.LibText.*;


public class CronRunner extends TimerTask
{
	public void	run()
	{
		cverbose.println( "***CronRunner***" );

		Date now = now();

		cverbose.println( now );

		int minute = minute( now );

		int hour = hour( now );

		int day_of_month = day_of_month( now );

		int month = month( now );

		int day_of_week = day_of_week( now ) - 1;

		if( day_of_week == -1 ) day_of_week = 6;

		cverbose.println( "minute = %d", minute );

		cverbose.println( "hour = %d", hour );

		cverbose.println( "day_of_month = %d", day_of_month );

		cverbose.println( "month = %d", month );

		cverbose.println( "day_of_week = %d", day_of_week );

		cverbose.println( "cron entriesL %d", cron_entries.size() );

		for( CronEntry cron_entry : cron_entries )
		{
			String entry = cron_entry.get_entry();

			cverbose.println( "Processing entry: %s", entry );

			if( empty( entry ) ) continue;

			String[] entry_tokens = entry.split( "\\s+" );

			if( entry_tokens.length != 5 ) continue;

			if( ! match( minute, entry_tokens[ 0 ] ) ) continue;

			if( ! match( hour, entry_tokens[ 1 ] ) ) continue;

			if( ! match( day_of_month, entry_tokens[ 2 ] ) ) continue;

			if( ! match( month, entry_tokens[ 3 ] ) ) continue;

			if( ! match( day_of_week, entry_tokens[ 4 ] ) ) continue;

			new Thread( cron_entry ).run();
		}
	}


	private boolean match( int actual, String expected )
	{
		if( "*".equals( expected ) ) return true;

		return expected.equals( "" + actual );
	}
}
