package rojira.jsi4.exp;

public class ImmutableValue<T>
{
	private final T t;

	public ImmutableValue( T t )
	{
		this. t = t;
	}

	public T value()
	{
		return this.t;
	}

	public T get()
	{
		return this.t;
	}


	public String toString()
	{
		return String.valueOf( this.t );
	}
}
