package rojira.jsi4.util.system;

/**
<p> Exception used to indicate data the application has loaded has an invalid value
<p> eg: a missing field or unparsable value in a conf file
*/
public class DataException extends RuntimeException
{
	public DataException()
	{
	}

	public DataException( String message )
	{
		super( message );
	}

	public DataException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public DataException( Throwable cause )
	{
		super( cause );
	}
}
