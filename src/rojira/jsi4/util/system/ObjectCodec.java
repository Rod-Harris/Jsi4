package rojira.jsi4.util.system;

public interface ObjectCodec<T>
{
	public boolean can_convert( Class type );

	public T encode( Class type, Object o );

	public <T2> T2 decode( Class<T2> type, T data );
}
