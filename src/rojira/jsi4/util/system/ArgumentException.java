package rojira.jsi4.util.system;

/**
<p> Exception used to indicate an argument passed to a method is wrong in principle
<p> eg: passing null, a negative integer, a string to be parsed an an integer, etc
*/
public class ArgumentException extends IllegalArgumentException
{
	public ArgumentException()
	{
	}

	public ArgumentException( String message )
	{
		super( message );
	}

	public ArgumentException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public ArgumentException( Throwable cause )
	{
		super( cause );
	}
}
