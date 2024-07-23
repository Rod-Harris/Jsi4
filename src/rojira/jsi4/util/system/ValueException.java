package rojira.jsi4.util.system;

/**
<p> Exception used to indicate an argument passed to a method is ok in principle, but invalid in the curent context
<p> eg: passing a key that doesn't exist to a map, or a file that has no parnet directory (but could actually be valid filename)
*/
public class ValueException extends IllegalArgumentException
{
	public ValueException()
	{
	}

	public ValueException( String message )
	{
		super( message );
	}

	public ValueException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public ValueException( Throwable cause )
	{
		super( cause );
	}
}
