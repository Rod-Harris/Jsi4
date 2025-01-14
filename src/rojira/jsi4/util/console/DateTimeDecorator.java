package rojira.jsi4.util.console;

import java.util.*;

public class DateTimeDecorator implements StreamDecorator
{
	public final String pre()
	{
		return date_time() + " >> ";
	}

	public final String post()
	{
		return "";
	}


	private static String date_time()
	{
		Calendar c = new GregorianCalendar();

		int millis = c.get( Calendar.MILLISECOND );

		int second = c.get( Calendar.SECOND );

		int minute = c.get( Calendar.MINUTE );

		int hour = c.get( Calendar.HOUR_OF_DAY );

		int day_of_month = c.get( Calendar.DAY_OF_MONTH );

		int month = c.get( Calendar.MONTH ) + 1;

		int year = c.get( Calendar.YEAR );

		return String.format( "%02d/%02d/%d %02d:%02d:%02d.%03d", day_of_month, month, year, hour, minute, second, millis );
	}
}
