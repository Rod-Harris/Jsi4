package rojira.jsi4.util.io;

import static rojira.jsi4.LibConsole.cerr;
import static rojira.jsi4.LibSystem.retrace;
import static rojira.jsi4.LibText.fmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Pipe implements Runnable
{
	private boolean looping = true;

	public final InputStream in;

	public final OutputStream out;

	boolean broken;

	private Thread thread;

	//public static int new_buffer_size = (int) bytes( 125, FileSize.Unit.KILOBYTES );

	public static int new_buffer_size = 125; // bytes

	private int total_read;

	private int total_written;

	private long time_elapsed;

	private double rate;

	/**
	*  Constructs (and starts) a pipe between the input and output streams
	*/
	public Pipe( InputStream in, OutputStream out )
	{
		this( in, out, true );
	}


	/**
	*  Constructs (and starts) a pipe between the input and output streams
	*/
	public Pipe( InputStream in, OutputStream out, boolean threaded )
	{
		this.in = in;

		this.out = out;

		if( threaded )
		{
			thread = new Thread( this );

			thread.start();
		}
		else
		{
			run();
		}
	}


	/**
	*  Stops the pipe
	*/
	public void break_pipe()
	{
		looping = false;
	}


	/**
	*  Don't call this directly - its automatically called in the constructor
	*/
	public void run()
	{
		byte[] buf = new byte[ new_buffer_size ];

		int read = 0;

		String break_reason = "";

		//Timer timer = start_timer();

		do
		{
			try
			{
				read = in.read( buf );
			}
			catch( IOException ex )
			{
				cerr.println( retrace( ex ) );
			}


			if( read < 0 )
			{
				looping = false;
			}
			else if( read == 0 )
			{
				Thread.yield();
			}
			else
			{
				try
				{
					total_read += read;

					out.write( buf, 0, read );

					total_written += read;
				}
				catch( IOException ex )
				{
					cerr.println( retrace( ex ) );

					looping = false;
				}
			}
		}
		while( looping );

		//time_elapsed = timer.elapsed();

		//String elapsed_str = range( "%.1f %s", time_elapsed, "ms", "s" );

		//rate = 1000.0 * total_written / time_elapsed;

		//String rate_str = range( "%.1f %s", rate, "B/s", "KB/s", "MB/s", "GB/s" );

		//cdebug.println( "Pipe broken: %s received : %s sent in %s (%s/s)", file_size( total_read ), file_size( total_written ), elapsed_str, file_size( rate ) );

		try
		{
			in.close();
		}
		catch( Exception ex )
		{
			cerr.println( retrace( ex ) );
		}

		try
		{
			out.flush();
		}
		catch( Exception ex )
		{
			cerr.println( retrace( ex ) );
		}

		try
		{
			out.close();
		}
		catch( Exception ex )
		{
			cerr.println( retrace( ex ) );
		}

		broken = true;
	}


	public static String range( String fmt, double value, String... suffixes )
	{
		int si = 0;

		while( true )
		{
			if( value > 1000 )
			{
				if( si >= suffixes.length -1 )
				{
					break;
				}

				value *= 0.001;

				si ++;
			}
			else
			{
				break;
			}
		}

		return fmt( fmt, value, suffixes[ si ] );
	}


	public int bytes_read()
	{
		return total_read;
	}

	public int bytes_written()
	{
		return total_written;
	}

	/**
	 * Returns data throughput rate in Bytes/sec
	 */
	public double rate()
	{
		return rate;
	}

	public void wait_for() throws InterruptedException
	{
		if( thread != null )
		{
			thread.join();
		}

		else
		{
			while( ! broken )
			{
				Thread.currentThread().yield();
			}
		}
	}
}
