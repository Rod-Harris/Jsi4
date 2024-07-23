package rojira.jsi4.util.time;


import static rojira.jsi4.LibSystem.*;


public class Timer
{
	long t0;

	boolean counting;

	public Timer()
	{
		restart();
	}

	public void restart()
	{
		counting = true;

		t0 = systime();
	}

	public long elapsed()
	{
		check( counting ).on_fail().raise_state( "Timer is not counting" );

		return systime() - t0;
	}

	public long stop()
	{
		check( counting ).on_fail().raise_state( "Timer is not counting" );

		counting = false;

		return systime() - t0;
	}

	public boolean counting()
	{
		return counting;
	}
}
