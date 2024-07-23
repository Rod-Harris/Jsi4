package rojira.jsi4.util.time;

public interface CronEntry extends Runnable
{
	public String get_entry();

	public void run();
}
