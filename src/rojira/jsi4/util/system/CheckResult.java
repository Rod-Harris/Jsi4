package rojira.jsi4.util.system;

import static rojira.jsi4.LibConsole.*;
import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibSystem.*;

public abstract class CheckResult
{
	public abstract <T extends Throwable> void raise( Class<T> ex_type, String msg_fmt, Object... args ) throws T;

	public abstract void raise_arg( String msg_fmt, Object... args );

	public abstract void raise_data( String msg_fmt, Object... args );

	public abstract void raise_value( String msg_fmt, Object... args );

	public abstract void raise_state( String msg_fmt, Object... args );

	public abstract void raise_operation( String msg_fmt, Object... args );

	public abstract void fatal( int exit_code, String msg_fmt, Object... args );
}


class CheckResultIgnore extends CheckResult
{
	@Override
	public <T extends Throwable> void raise( Class<T> ex_type, String msg_fmt, Object... args ) throws T
	{
	}

	@Override
	public void raise_data( String msg_fmt, Object... args )
	{
	}

	@Override
	public void raise_arg( String msg_fmt, Object... args )
	{
	}

	@Override
	public void raise_value( String msg_fmt, Object... args )
	{
	}

	@Override
	public void raise_state( String msg_fmt, Object... args )
	{
	}

	@Override
	public void raise_operation( String msg_fmt, Object... args )
	{
	}

	@Override
	public void fatal( int exit_code, String msg_fmt, Object... args )
	{
	}
}


class CheckResultApply extends CheckResult
{
	@Override
	public <T extends Throwable> void raise( Class<T> ex_type, String msg_fmt, Object... args ) throws T
	{
		String msg = fmt( msg_fmt, args );

		T t = null;

		try
		{
			t = ex_type.getConstructor( String.class ).newInstance( msg );
		}
		catch( NoSuchMethodException ex )
		{
			throw new RuntimeException( "Couldn't construct exception: " + ex.getMessage(), ex );
		}
		catch( InstantiationException ex )
		{
			throw new RuntimeException( "Couldn't construct exception: " + ex.getMessage(), ex );
		}
		catch( IllegalAccessException ex )
		{
			throw new RuntimeException( "Couldn't construct exception: " + ex.getMessage(), ex );
		}
		catch( java.lang.reflect.InvocationTargetException ex )
		{
			throw new RuntimeException( "Couldn't construct exception: " + ex.getMessage(), ex );
		}

		throw t;
	}

	@Override
	public void raise_data( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new DataException( msg );
	}

	@Override
	public void raise_arg( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ArgumentException( msg );
	}

	@Override
	public void raise_value( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new ValueException( msg );
	}

	@Override
	public void raise_state( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new StateException( msg );
	}

	@Override
	public void raise_operation( String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		throw new OperationException( msg );
	}

	@Override
	public void fatal( int exit_code, String msg_fmt, Object... args )
	{
		String msg = fmt( msg_fmt, args );

		cerr.println( msg );

		cdebug.println( trace( Thread.currentThread().getStackTrace(), 2 ) );

		exit( exit_code );
	}
}
