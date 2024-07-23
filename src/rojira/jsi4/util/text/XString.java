package rojira.jsi4.util.text;

import java.io.*;
import java.util.*;

import static rojira.jsi4.LibText.*;
import static rojira.jsi4.LibXML.*;

/**
 * An XML builder class
 */
public class XString implements TString
{
	private int indent;

	private ArrayList<String> tag_stack = new ArrayList<String>();

	private String indent_str = "";

	private final TString back;

	private boolean auto_indent = true;

	/**
	 * Create an XString backed by an EString (that's backed by a StringBuilder)
	 */
	public XString()
	{
		back = new EString();
	}


	/**
	 * Create an XString backed by the given TString
	 */
	public XString( TString back )
	{
		this.back = back;
	}


	/**
	 * <p> Opens the named element
	 */
	public void open_element( String tag )
	{
		open_element( tag, null );
	}


	/**
	 * <p> Opens the named element, attributes can be null
	 */
	public void open_element( String tag, String attributes )
	{
		back.print( indent_str );

		back.print( "<%s", tag );

		if( attributes != null )
		{
			back.print( " %s", escape_attributes( attributes ) );
		}

		back.println( ">" );

		tag_stack.add( tag );

		inc_indent();
	}


	/**
	 * <p> Sets the value of the current element, value can be null
	 */
	public void set_value( Object value )
	{
		if( value == null ) return;

		if( value instanceof byte[] )
		{
			value = new String( (byte[]) value );
		}

		back.print( indent_str );

		back.println( escape( value.toString() )  );
	}



	/**
	 * <p> Closes the named element
	 *
	 * @throws IllegalArgumentException if you try to close the wrong element
	 */
	public void close_element( String tag )
	{
		String ctag = tag_stack.remove( tag_stack.size() - 1 );

		if( ! ctag.equals( tag ) ) throw new IllegalArgumentException( sprintf( "XString error: tried to close tag %s, but should have closed tag %s", tag, ctag ) );

		dec_indent();

		back.print( indent_str );

		back.println( "</%s>", tag );
	}


	/**
	 * <p> Writes this element in one line
	 */
	public void add_element( String tag )
	{
		add_element( tag, null );
	}


	/**
	 * <p> Writes this element in one line (attributes can be null)
	 */
	public void add_element( String tag, String attributes )
	{
		add_element( tag, attributes, null );
	}


	/**
	 * <p> Writes this element in one line (attributes and/or value can be null)
	 */
	public void add_element( String tag, String attributes, Object value )
	{
		back.print( indent_str );

		back.print( "<%s", tag );

		if( attributes != null )
		{
			back.print( " %s", escape_attributes( attributes ) );
		}

		if( value == null )
		{
			back.println( "/>" );
		}
		else
		{
			back.print( ">" );

			back.print( escape( value.toString() )  );

			back.println( "</%s>", tag );
		}
	}


	private String escape_attributes( String attributes )
	{
		attributes = escape( attributes + " " );

		attributes = attributes.replace( "=&apos;", "='" );

		attributes = attributes.replace( "&apos; ", "' " );

		attributes = attributes.replace( "=&quot;", "=\"" );

		attributes = attributes.replace( "&quot; ", "\" " );

		return attributes.substring( 0, attributes.length() -1 );
	}


	private void inc_indent()
	{
		indent ++;

		if( auto_indent )
		{
			indent_str = mult( "\t", indent );
		}
		else
		{
			indent_str = "";
		}
	}


	private void dec_indent()
	{
		indent --;

		if( auto_indent )
		{
			indent_str = mult( "\t", indent );
		}
		else
		{
			indent_str = "";
		}
	}


	/**
	 * <p> Checks that no elements remain open, not intended to be a rigorous test of xml validity
	 */
	public boolean validate()
	{
		return indent == 0 && tag_stack.size() == 0;
	}


	/**
	 * Returns the backing TString.toString()
	 */
	public String toString()
	{
		return back.toString();
	}


	/**
	 * <p> Sets whether nested elements should be indented with tabs or left unindented
	 */
	public void auto_indent( boolean auto_indent )
	{
		this.auto_indent = auto_indent;
	}



	// The remaining methods are just wrappers for methods defined in the TStirng interface that call the same method on the 'back' object (which is also a TString)

	public void print( Object arg )
	{
		back.print( arg );
	}

	public void print( String s )
	{
		back.print( s );
	}

	public void print( String fmt, Object... args )
	{
		back.print( fmt, args );
	}

	public void println()
	{
		back.println();
	}

	public void println( Object arg )
	{
		back.println( arg );
	}

	public void println( String s )
	{
		back.println( s );
	}

	public void println( String fmt, Object... args )
	{
		back.println( fmt, args );
	}

	public void write_to( OutputStream out ) throws IOException
	{
		back.write_to( out );
	}

	public long length()
	{
		return back.length();
	}

	public String substring( int beginIndex, int endIndex )
	{
		return back.substring( beginIndex, endIndex );
	}

	public void complete()
	{
		back.complete();
	}
/*
	public void open_element( String tag, String... attributes )
	{
		back.print( indent_str );

		back.print( "<%s", tag );

		for( String attribute : attributes )
		{
			back.print( " %s", attribute );
		}

		back.println( ">" );

		tag_stack.add( tag );

		inc_indent();
	}


	public void close_element( String tag )
	{
		String ctag = tag_stack.remove( tag_stack.size() - 1 );

		if( ! ctag.equals( tag ) ) throw new IllegalArgumentException( sprintf( "XString error: tried to close tag %s, but should have closed tag %s", tag, ctag ) );

		dec_indent();

		back.print( indent_str );

		back.println( "</%s>", tag );
	}


	public void add_element( String tag, String attributes )
	{
		back.print( indent_str );

		back.print( "<%s %s />\n", tag, attributes );
	}


	public void add_element( String tag )
	{
		back.print( indent_str );

		back.print( "<%s />\n", tag );
	}


	public void print_value( Object value )
	{
		if( value == null ) return;

		if( value instanceof byte[] )
		{
			value = new String( (byte[]) value );
		}

		back.print( indent_str );

		back.println( escapeStringForXml( value.toString() )  );
	}


	public void print_tag_value( String tag, Object value )
	{
		back.print( indent_str );

		if( value == null )
		{
			back.println( "<%s/>", tag );
		}
		else
		{
			if( value instanceof byte[] )
			{
				value = new String( (byte[]) value );
			}

			back.println( "<%s>%s</%s>", tag, escapeStringForXml( value.toString() ), tag );
		}
	}


	private void inc_indent()
	{
		indent ++;

		indent_str = Statics.mult( "\t", indent );
	}


	private void dec_indent()
	{
		indent --;

		indent_str = Statics.mult( "\t", indent );
	}


	public boolean validate()
	{
		return indent == 0 && tag_stack.size() == 0;
	}


	public String toString()
	{
		return back.toString();
	}
	* */
}
