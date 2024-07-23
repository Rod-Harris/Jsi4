package rojira.jsi4.util.text;

import java.io.*;

import static rojira.jsi4.LibMaths.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibConsole.*;

public class TStringInputStream extends InputStream
{
	TString source;

	int ptr;

	byte[] b0 = new byte[ 1 ];


	public TStringInputStream( TString source )
	{
		super();

		boolean supported = false;

		if( source instanceof EString ) supported = true;

		if( source instanceof FString ) supported = true;

		if( ! supported )
		{
			throw new RuntimeException( fmt( "TStringInputStream is not implemented for %s objects", source.getClass().getName() ) );
		}

		this.source = source;
	}


	public int available()
	{
		//cdebug.println( mtrace() );

		return (int) source.length() - ptr;
	}


	public void close()
	{
		//cdebug.println( mtrace() );
	}


	public void mark( int readlimit )
	{
		throw new RuntimeException( "TStringInputStream does not support marking" );
	}


	public boolean markSupported()
	{
		return false;
	}


	public int read()
	{
		//cdebug.println( mtrace() );

		read( b0 );

		return (int) b0[ 0 ];
	}


	public int read( byte[] b )
	{
		//cdebug.println( mtrace() );

		return read( b, 0, b.length );
	}


	public int read( byte[] b, int off, int len )
	{
		if( ptr >= source.length() ) return -1;

		len = min( b.length - off, len );

		len = min( available(), len );

		byte[] ssb = source.substring( ptr, ptr + len ).getBytes();

		ptr += len;

		System.arraycopy( ssb, 0, b, off, len );

		return len;
	}


	public void	reset()
	{
		throw new RuntimeException( "TStringInputStream does not support marking" );
	}


	public long	skip( long n )
	{
		cdebug.println( mtrace() );

		int s = (int) n;

		s = min( s, available() );

		ptr += s;

		return s;
	}
}

/*

 int 	available()
          Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the next invocation of a method for this input stream.
 void 	close()
          Closes this input stream and releases any system resources associated with the stream.
 void 	mark(int readlimit)
          Marks the current position in this input stream.
 boolean 	markSupported()
          Tests if this input stream supports the mark and reset methods.
abstract  int 	read()
          Reads the next byte of data from the input stream.
 int 	read(byte[] b)
          Reads some number of bytes from the input stream and stores them into the buffer array b.
 int 	read(byte[] b, int off, int len)
          Reads up to len bytes of data from the input stream into an array of bytes.
 void 	reset()
          Repositions this stream to the position at the time the mark method was last called on this input stream.
 long 	skip(long n)
*/
