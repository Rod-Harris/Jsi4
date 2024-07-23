package rojira.jsi4.util.text;

import java.io.*;


public class TStringWriter extends Writer
{
	TString back;

	private char[] c0 = new char[ 1 ];


	public TStringWriter( TString back )
	{
		super();

		this.back = back;
	}


	public void close()
	{
	}

	public void flush()
	{
	}

	public void write(char[] cbuf)
	{
		back.print( new String( cbuf ) );
	}

	public void write(char[] cbuf, int off, int len)
	{
		back.print( new String( cbuf, off, len ) );
	}

	public void write(int c)
	{
		c0[ 0 ] = (char) c;

		write( c0 );
	}

	public void write(String str)
	{
		back.print( str );
	}

	public void write(String str, int off, int len)
	{
		back.print( str.substring( off, off + len ) );
	}

}
