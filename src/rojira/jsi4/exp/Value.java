package rojira.jsi4.exp;

public class Value<T>
{
	private T t;

	public T get()
	{
		return this.t;
	}

	public T value()
	{
		return this.t;
	}

	public void set( T t )
	{
		this.t = t;
	}

	public String toString()
	{
		return String.valueOf( this.t );
	}
}
