package rojira.jsi4.util.system;

import rojira.jsi4.util.text.*;
import rojira.jsi4.util.io.*;

import java.io.*;

import static rojira.jsi4.LibIO.*;

public class Exec
{
	public final String command;

	public final String[] args;

	public final String std_out;

	public final String std_err;

	public final Integer exit_code;

	public final Throwable error;

	public Exec( String command, String... args )
	{
		this.command = command;

		this.args = args;

		EString std_out = new EString();

		EString std_err = new EString();

		String[] cmd_line = new String[ args.length + 1 ];

		cmd_line[ 0 ] = command;

		for( int i=0; i<args.length; i++ )
		{
			cmd_line[ i + 1 ] = args[ i ];
		}

		Integer exit_code = null;

		Throwable error = null;

		InputStream pout = null;

		InputStream perr = null;

		Pipe std_out_pipe = null;

		Pipe std_err_pipe = null;

		try
		{
			Process process = new ProcessBuilder().command( cmd_line ).start();

			pout = process.getInputStream();

			perr = process.getErrorStream();

			std_out_pipe = pipe( buffer( pout ), buffer( std_out ) );

			std_err_pipe = pipe( buffer( perr ), buffer( std_err ) );

			exit_code = process.waitFor();
		}
		catch( Throwable _error )
		{
			error = _error;
		}
		finally
		{
			close( std_out_pipe );

			close( std_err_pipe );
		}

		this.std_out = std_out.toString();

		this.std_err = std_err.toString();

		this.exit_code = exit_code;

		this.error = error;
	}


	private void close( Pipe pipe )
	{
		if( pipe == null ) return;

		try
		{
			pipe.wait_for();

			pipe.out.flush();
		}
		catch( Throwable error )
		{
		}
	}
}
