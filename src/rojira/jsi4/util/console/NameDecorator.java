package rojira.jsi4.util.console;

public class NameDecorator implements StreamDecorator
{
	final String name;

	public NameDecorator( String name )
	{
		this.name = name;
	}

	public String pre()
	{
		return name;
	}

	public String post()
	{
		return "";
	}
}
