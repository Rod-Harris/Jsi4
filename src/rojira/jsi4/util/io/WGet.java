package rojira.jsi4.util.io;

import java.io.*;
import java.net.*;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibIO.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibDevel.*;


public class WGet
{
	final String url;

	ByteArrayOutputStream data_buffer;

	Throwable error;

	URLConnection connection;


	public WGet( String url )
	{
		check( ! empty( url ) ).on_fail().raise_arg( "'url' parameter contains no data" );

		this.url = url;
	}


	public boolean retrieve_headers()
	{
		try
		{
			URL url = new URL( this.url );

			connection = url.openConnection();

			cverbose.println( connection.getHeaderFields() );
		}
		catch( Throwable error )
		{
			cerr.println( "Http Get Headers error: %s", error.getMessage() );

			cdebug.println( retrace( error ) );

			this.error = error;
		}

		return no_error();
	}


	public boolean retrieve_data()
	{
		return retrieve_data( null );
	}


	public boolean retrieve_data( OutputStream out )
	{
		check( connection != null ).on_fail().raise_state( "URL connection not opened" );

		check( error == null ).on_fail().raise_state( "URL connection has errors" );

		InputStream in = null;

		try
		{
			in = connection.getInputStream();

			int content_length = content_length();

			if( out == null )
			{
				if( content_length != -1 )
				{
					TODO( "Check if there is enough memory left in the JVM to hold this download" );

					cinfo.println( "Content-Length: %d", content_length );

					data_buffer = new ByteArrayOutputStream( content_length );
				}
				else
				{
					cinfo.println( "Content-Length: ?" );

					data_buffer = new ByteArrayOutputStream();
				}

				out = data_buffer;
			}

			connect( buffer( in ), buffer( out ) ); // Blocks here until EOF or a stream is unexpectedly closed
		}
		catch( Throwable error )
		{
			cerr.println( "Http Stream Data error: %s", error.getMessage() );

			cdebug.println( retrace( error ) );

			this.error = error;
		}
		finally
		{
			close( in );

			close( out );
		}

		return no_error();
	}


	/**
	<p> Retrieves the content of the url specified in the constructor into a data buffer
	@see do_get( OutputStream out )
	*/
	public boolean do_get()
	{
		return do_get( null );
	}


	/**
	<p> Retrieves the content of the url specified in the constructor
	<p> Opens a connection to the given url
	<p> Reads the header info
	<p> If the specified output stream is null a new data buffer is created as the output stream for the content
	<p> - The initial size of the data buffer is specified by the content-length header if found (but it can grow if needed)
	<p> - Otherwise creates a default sized data_buffer (that can grow as needed)
	<p> Reads the content of the url into the output stream
	<p> Closes the connection to the url
	<p> If an error occurs it is cached
	*/
	public boolean do_get( OutputStream out )
	{
		if( retrieve_headers() && retrieve_data( out ) )
		{
			return true;
		}

		return false;
	}


	public int content_length()
	{
		return connection.getContentLength();
	}


	public String content_type()
	{
		return connection.getContentType();
	}


	public int http_status_code() throws IOException
	{
		check( connection instanceof HttpURLConnection ).on_fail().raise_state( "Not a HTTP connection" );

		return ((HttpURLConnection)connection).getResponseCode();
	}


	public String http_response_message() throws IOException
	{
		check( connection instanceof HttpURLConnection ).on_fail().raise_state( "Not a HTTP connection" );

		return ((HttpURLConnection)connection).getResponseMessage();
	}


	public String http_request_method()
	{
		check( connection instanceof HttpURLConnection ).on_fail().raise_state( "Not a HTTP connection" );

		return ((HttpURLConnection)connection).getRequestMethod();
	}


	public String url()
	{
		return this.url;
	}


	public ByteArrayOutputStream data_buffer()
	{
		check( data_buffer != null ).on_fail().raise_state( "Did not use the in built data buffer to do this wget" );

		return this.data_buffer;
	}


	public Throwable error()
	{
		return this.error;
	}


	public boolean no_error()
	{
		return this.error == null;
	}


	public String get_header_value( String header )
	{
		return connection.getHeaderField( header );
	}

}
