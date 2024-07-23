package rojira.jsi4.util.system;

public abstract class Poolable
{
	private PoolFactory factory;

	public void set_factory( PoolFactory factory )
	{
		this.factory = factory;
	}

	public void return_to_factory()
	{
		factory.push( this );
	}

	protected void finalize()
	{
		// probably wont work

		return_to_factory();
	}

	protected abstract void reset();

	public abstract Object clone();
}
