package rojira.jsi4.util.console;

public class ThreadDecorator implements StreamDecorator
{
	public String pre()
	{
		return "<" + Thread.currentThread().getName() + ">: ";
	}

	public String post()
	{
		return "";
	}
}
