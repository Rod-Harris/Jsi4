package rojira.jsi4.util.console;

import java.io.*;
import java.util.*;

public final class OStream
{
	private final PrintStream out;

	private boolean on = true;

	public final ArrayList<StreamDecorator> decorators = new ArrayList<StreamDecorator>();

	static final Object console_lock = new Object();


	public OStream( PrintStream out )
	{
		this.out = out;
	}


	public void print( Object arg )
	{
		if( ! on ) return;

		synchronized( console_lock )
		{
			pre_decorate();

			out.print( arg );

			post_decorate();
		}
	}


	public void print( String fmt, Object... args )
	{
		if( ! on ) return;

		synchronized( console_lock )
		{
			while( fmt.startsWith( "\n" ) )
			{
				out.print( "\n" );

				fmt = fmt.substring( 1 );
			}

			pre_decorate();

			out.print( String.format( fmt, args ) );

			post_decorate();
		}
	}


	public void print( String fmt )
	{
		if( ! on ) return;

		if( fmt == null )
		{
			print( (Object) fmt );

			return;
		}

		synchronized( console_lock )
		{
			while( fmt.startsWith( "\n" ) )
			{
				out.print( "\n" );

				fmt = fmt.substring( 1 );
			}

			pre_decorate();

			out.print( fmt );

			post_decorate();
		}
	}


	public void println( Object arg )
	{
		if( ! on ) return;

		synchronized( console_lock )
		{
			pre_decorate();

			out.print( arg );

			post_decorate();

			out.println();
		}
	}


	public void println( String fmt )
	{
		if( ! on ) return;

		if( fmt == null )
		{
			println( (Object) fmt );

			return;
		}

		synchronized( console_lock )
		{
			while( fmt.startsWith( "\n" ) )
			{
				out.print( "\n" );

				fmt = fmt.substring( 1 );
			}

			pre_decorate();

			out.print( fmt );

			post_decorate();

			out.println();
		}
	}

	public void println( String fmt, Object... args )
	{
		if( ! on ) return;

		synchronized( console_lock )
		{
			while( fmt.startsWith( "\n" ) )
			{
				out.print( "\n" );

				fmt = fmt.substring( 1 );
			}

			pre_decorate();

			out.print( String.format( fmt, args ) );

			post_decorate();

			out.println();
		}
	}


	public void println()
	{
		if( ! on ) return;

		synchronized( console_lock )
		{
			out.println();
		}
	}


	public void on()
	{
		on = true;
	}


	public void off()
	{
		on = false;
	}


	public void on( boolean b )
	{
		on = b;
	}


	public boolean active()
	{
		return on;
	}


	public void add_decorators( StreamDecorator... new_decorators )
	{
		for( StreamDecorator decorator : new_decorators )
		{
			decorators.add( decorator );
		}
	}


	public void insert_decorator( StreamDecorator decorator )
	{
		decorators.add( 0, decorator );
	}


	void pre_decorate()
	{
		for( int i=0; i<decorators.size(); i++ )
		{
			out.print( decorators.get( i ).pre() );
		}
	}


	void post_decorate()
	{
		for( int i=decorators.size()-1; i>=0; i-- )
		{
			out.print( decorators.get( i ).post() );
		}
	}


	protected void finalize() throws Throwable
	{
		out.flush();

		out.close();

		super.finalize();
	}

/*	public void inspect( Object o )
	{
		println( LibSys.inspect( o ) );
	}*/
}
