package rojira.jsi4.util.text;

import java.io.*;

public interface TString
{
	public static final byte[] NL_BYTES = "\n".getBytes();

	public void print( Object arg );

	public void print( String s );

	public void print( String fmt, Object... args );

	public void println();

	public void println( Object arg );

	public void println( String s );

	public void println( String fmt, Object... args );

	public void write_to( OutputStream out ) throws IOException;

	public long length();

	public String substring( int beginIndex, int endIndex );

	public void complete();
}
