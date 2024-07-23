package rojira.jsi4.util.text;

import java.io.*;


public class TStringOutputStream extends OutputStream
{
	TString back;


	public TStringOutputStream( TString back )
	{
		super();

		this.back = back;
	}


	public void close()
	{
		//cdebug.println( mtrace() );
	}


	public void flush()
	{
		//cdebug.println( mtrace() );
	}


	public void write( byte[] b )
	{
		//cdebug.println( mtrace() );

		back.print( new String( b ) );
	}


	public void write( byte[] b, int off, int len )
	{
		//cdebug.println( mtrace() );

		back.print( new String( b, off, len ) );
	}


	public void write( int b )
	{
		//cdebug.println( mtrace() );

		write( new byte[]
		{
			 ( byte ) ( b & 0xFF )
		}
		);
	}
}
