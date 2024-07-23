package rojira.jsi4.util.system;

/**
<p> Exception used to indicate the internal state of an object or the JRE, or the system as a whole is 'invalid'
<p> eg: trying to get a value from a map when the map is null
*/
public class StateException extends IllegalStateException
{
	public StateException()
	{
	}

	public StateException( String message )
	{
		super( message );
	}

	public StateException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public StateException( Throwable cause )
	{
		super( cause );
	}
}
