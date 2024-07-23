package rojira.jsi4.util.system;

/**
<p> Exception used to indicate that an unforseeable problem has occurred while performing some task
<p> eg: streaming from a socket, running a native process
*/
public class OperationException extends IllegalStateException
{
	public OperationException()
	{
	}

	public OperationException( String message )
	{
		super( message );
	}

	public OperationException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public OperationException( Throwable cause )
	{
		super( cause );
	}
}
