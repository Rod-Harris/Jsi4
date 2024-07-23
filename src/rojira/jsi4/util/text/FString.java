package rojira.jsi4.util.text;

import java.io.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibMaths.*;
import static rojira.jsi4.LibSystem.*;


public class FString extends WString
{
	final File file;

	final RandomAccessFile file_reader;

	boolean delete_on_exit;


	public FString() throws IOException
	{
		this( true );
	}


	public FString( boolean delete_on_exit ) throws IOException
	{
		this( new File( "/tmp/fstring-" + rnd_hex() + ".txt" ) );

		this.delete_on_exit = delete_on_exit;
	}


	public FString( File file ) throws IOException
	{
		super( new BufferedOutputStream( new FileOutputStream( file ) ) );

		this.file = file;

		file_reader = new RandomAccessFile( file, "r" );
	}


	public FString( String path ) throws IOException
	{
		this( new File( path ) );
	}


	public void write_to( OutputStream out ) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream( new FileInputStream( file ) );

		byte[] buf = new byte[ 4096000 ];

		int avail = 0;

		do
		{
			//avail = in.available();

			avail = in.read( buf );

			if( avail == -1 ) break;

			if( avail > 0 ) out.write( buf, 0, avail );

		}while( true );

		out.flush();
	}


	public String substring( int beginIndex, int endIndex )
	{
		//cdebug.println( mtrace( "beginIndex %d, endIndex, %d", beginIndex, endIndex ) );

		try
		{
			file_reader.seek( beginIndex );

			byte[] buffer = new byte[ endIndex - beginIndex ];

			file_reader.readFully( buffer );

			return new String( buffer );
		}
		catch( Exception ex )
		{
			cerr.println( retrace( ex ) );
		}

		return null;
	}


	public long length()
	{
		if( ! super.complete ) cwarn.println( "Getting the length of an FString before it is complete (flushed) may be inaccurate" );

		return file.length();
	}


	public void finalize() throws Throwable
	{
		super.finalize();

		if( delete_on_exit ) file.delete();
	}
}
