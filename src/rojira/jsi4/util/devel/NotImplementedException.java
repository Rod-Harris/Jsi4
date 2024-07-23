package rojira.jsi4.util.devel;

/**
<p> Exception used to indicate a feature has not been impleented
*/
public class NotImplementedException extends Exception
{
	public NotImplementedException()
	{
	}

	public NotImplementedException( String message )
	{
		super( message );
	}

	public NotImplementedException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public NotImplementedException( Throwable cause )
	{
		super( cause );
	}
}
