package rojira.jsi4.util.text;

import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibSystem.*;
import java.io.*;


public class WString implements TString
{
	final OutputStream out;

	private long length;

	protected boolean complete;


	public WString( OutputStream out )
	{
		this.out = out;
	}


	private void write( byte[] data )
	{
		try
		{
			out.write( data );

			length += data.length;
		}
		catch( IOException ex )
		{
			cerr.println( ex_to_string( ex ) );
		}
	}


	public void print( Object arg )
	{
		if( arg == null ) arg = "null";

		this.write( arg.toString().getBytes() );
	}


	public void print( String s )
	{
		if( s == null ) s = "null";

		this.write( s.getBytes() );
	}


	public void print( String fmt, Object... args )
	{
		String s = sprintf( fmt, args );

		this.write( s.getBytes() );
	}


	public void println()
	{
		this.write( NL_BYTES );
	}


	public void println( Object arg )
	{
		if( arg == null ) arg = "null";

		this.write( arg.toString().getBytes() );

		this.write( NL_BYTES );
	}


	public void println( String s )
	{
		if( s == null ) s = "null";

		this.write( s.getBytes() );

		this.write( NL_BYTES );
	}


	public void println( String fmt, Object... args )
	{
		String s = sprintf( fmt, args );

		this.write( s.getBytes() );

		this.write( NL_BYTES );
	}


	/**
	 * Always throws an exception as this String is streamed as it is created, and not cached
	 */
	public void write_to( OutputStream out ) throws IOException
	{
		throw new RuntimeException( "WString.write_to( OutputStream out ) is not valid as the data is not cached" );
	}


	/**
	 * Returns the number of bytes send to the output stream
	 */
	public long length()
	{
		return length;
	}


	/**
	 * Always throws an exception as this String is streamed as it is created, and not cached
	 */
	public String substring( int beginIndex, int endIndex )
	{
		throw new RuntimeException( "WString.substring( int beginIndex, int endIndex ) is not valid as the data is not cached" );
	}


	/**
	 * Flushes and closes the output stream
	 */
	public void complete()
	{
		if( complete ) return;

		try
		{
			out.flush();

			out.close();
		}
		catch( IOException ex )
		{
			cerr.println( ex_to_string( ex ) );
		}

		complete = true;
	}


	/**
	 * Completes the wstring (if it hasn't already been completed)
	 */
	public void finalize() throws Throwable
	{
		if( ! complete ) complete();
	}
}
