package rojira.jsi4.util.system;

class Action
{
}
/*

public abstract class Action
{
	//private final Class<? extends ActionReport> report_class;

	protected ActionReport report;

	protected <? extends ActionConfiguration> conf;

	public Action()
	{
	}

	public void reset()
	{
		report = null;
	}

	public void configure( ActionConfiguration conf )
	{
		this.conf = conf;
	}

	public abstract void run();
}


class ActionRunner
{
	public <T1 extends Action, T2 extends ActionConfiguration> ActionReport do_sync( T1 action, T2 conf )
	{
		action.configure( conf );

		action.run();

		ActionReport report = action.get_report();

		action.reset();

		return report;
	}

	public <T1 extends Action, T2 extends ActionConfiguration> void do_async( T1 action, T2 conf, ActionReportCallback callback )
	{
		ActionReport report = do_sync( action, conf );

		callback.action_complete( report );
	}
}


class ActionConfiguration
{
}


class ActionReport
{
	Throwable initialisation_error;

	EString log = new EString();

	ArrayList<Throwable> action_errors = new ArrayList<Throwable>();
}


interface ActionReportCallback
{
	public void action_complete( ActionReport report );
}


class Tester
{
	public void run_test()
	{

	}
}

class TestAction extends Action
{
	private void validate()
	{
		EXPECT( super.conf != null );

		EXPECT( super.conf instanceof TestActionConfiguration );

		EXPECT( super.report != null );

		EXPECT( super.conf instanceof TestActionReport );
	}

	public void run()
	{
		validate();

		report.log( "Starting" );

		conf.a = 5;

		report.log( "Finished" );
	}
}

class TestActionConfiguration extends ActionConfiguration
{
	int a = 0;
}


class TestActionReport extends ActionReport
{
}
*/
