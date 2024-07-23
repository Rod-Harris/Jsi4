package rojira.jsi4.util.io;

import static rojira.jsi4.LibIO.*;
import static rojira.jsi4.LibSystem.*;
import static rojira.jsi4.LibText.*;

import java.io.*;

public class Resource
{
	final Class base;

	final String path;

	public Resource( Class base, String path )
	{
		expect_arg( base != null );

		expect_arg( ! empty( path ) );

		this.base = base;

		this.path = path;
	}

	public byte[] data() throws FileNotFoundException, IOException
	{
		return load_resource( base, path );
	}

	public String text() throws FileNotFoundException, IOException
	{
		return read_resource( base, path );
	}
}
