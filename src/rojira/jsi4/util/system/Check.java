package rojira.jsi4.util.system;


public abstract class Check// extends CheckResult
{
	public static final Check _true = new CheckTrue();

	public static final Check _false = new CheckFalse();

	public static final CheckResult _ignore = new CheckResultIgnore();

	public static final CheckResult _apply = new CheckResultApply();

	public static Check get( boolean b )
	{
		if( b ) return _true;

		return _false;
	}

	public abstract Check not();

	public abstract boolean passed();

	public abstract Check fail_if( boolean result );

	public abstract Check fail_when( boolean result );

	public abstract CheckResult on_fail();

	public abstract CheckResult on_pass();

/*
	@Override
	public <T extends Throwable> void raise( Class<T> ex_type, String msg_fmt, Object... args ) throws T
	{
		on_fail().raise( ex_type, msg_fmt, args );
	}


	@Override
	public void raise_arg( String msg_fmt, Object... args )
	{
		on_fail().raise_arg( msg_fmt, args );
	}


	@Override
	public void raise_value( String msg_fmt, Object... args )
	{
		on_fail().raise_value( msg_fmt, args );
	}

	@Override
	public void raise_state( String msg_fmt, Object... args )
	{
		on_fail().raise_state( msg_fmt, args );
	}

	@Override
	public void raise_operation( String msg_fmt, Object... args )
	{
		on_fail().raise_operation( msg_fmt, args );
	}

	@Override
	public void fatal( int exit_code, String msg_fmt, Object... args )
	{
		on_fail().fatal( exit_code, msg_fmt, args );
	}
*/
}


class CheckTrue extends Check
{
	@Override
	public Check not()
	{
		return _false;
	}

	@Override
	public Check fail_if( boolean result )
	{
		if( result == true ) return this;

		return _false;
	}

	@Override
	public Check fail_when( boolean result )
	{
		if( result == true ) return this;

		return _false;
	}

	@Override
	public CheckResult on_fail()
	{
		return _ignore;
	}

	@Override
	public CheckResult on_pass()
	{
		return _apply;
	}

	@Override
	public boolean passed()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return "true";
	}
}


class CheckFalse extends Check
{
	@Override
	public Check not()
	{
		return _true;
	}

	@Override
	public Check fail_if( boolean result )
	{
		if( result == true ) return _true;

		return this;
	}

	@Override
	public Check fail_when( boolean result )
	{
		if( result == true ) return _true;

		return this;
	}

	@Override
	public CheckResult on_fail()
	{
		return _apply;
	}

	@Override
	public CheckResult on_pass()
	{
		return _ignore;
	}

	@Override
	public boolean passed()
	{
		return false;
	}

	@Override
	public String toString()
	{
		return "false";
	}
}
