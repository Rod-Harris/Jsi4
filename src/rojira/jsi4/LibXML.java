package rojira.jsi4;

import static javax.xml.xpath.XPathConstants.NODE;
import static javax.xml.xpath.XPathConstants.NODESET;
import static rojira.jsi4.LibDevel.ASSERT;
import static rojira.jsi4.LibIO.read_file;
import static rojira.jsi4.LibSystem.check_state;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rojira.jsi4.util.text.EString;

/**
<p> Class methods provide stateless XML loading and parsing and xpath queries of XML documents
*/
public class LibXML
{
	private static TransformerFactory tFactory;

	private static XPath xpath = XPathFactory.newInstance().newXPath();

	private static byte[] ILLEGAL_XML_1_0_CHARS;


	private LibXML(){}


	/**
	 * Parses an XML file and returns a DOM document.
	*/
	public static Document parse_xml_file( String filename ) throws SAXException, ParserConfigurationException, IOException
	{
		return parse_xml_data( read_file( filename ), false );
	}

	/**
	 * Parses an XML file and returns a DOM document.
	*/
	public static Document parse_xml_file( File file ) throws SAXException, ParserConfigurationException, IOException
	{
		return parse_xml_file( file, false );
	}

	/**
	 * <p> Parses an XML file and returns a DOM document.
	 * <p> If validating is true, the contents is validated against the DTD specified in the file.
	 * <p> Got this code from javaalminac
	*/
	public static Document parse_xml_file( String filename, boolean validating ) throws SAXException, ParserConfigurationException, IOException
	{
		return parse_xml_data( read_file( filename ), validating );
	}


	/**
	 * <p> Parses an XML file and returns a DOM document.
	 * <p> If validating is true, the contents is validated against the DTD specified in the file.
	 * <p> Got this code from javaalminac
	*/
	public static Document parse_xml_file( File file, boolean validating ) throws SAXException, ParserConfigurationException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setValidating( validating );

		Document doc = factory.newDocumentBuilder().parse( file );

		return doc;
	}


	/**
	 * <p> Parses the string containing an xml document
	 */
	public static Document parse_xml_data( String xml_data ) throws SAXException, ParserConfigurationException, IOException
	{
		return parse_xml_data( xml_data, false );
	}


	/**
	 * <p> Parses the string containing an xml document
	 * <p> If validating is true, the contents is validated against the DTD specified in the file.
	 */
	public static Document parse_xml_data( String xml_data, boolean validating ) throws SAXException, ParserConfigurationException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		factory.setValidating( validating );

		//Document doc = factory.newDocumentBuilder().parse( new InputSource( new StringReader( xml_data.trim() ) ) );

		Document doc = factory.newDocumentBuilder().parse( new ByteArrayInputStream( xml_data.trim().getBytes() ) );


		return doc;
	}


	// --------------------------------------------------    END XML


	// --------------------------------------------------    XPATH


	/**
	 * <p> Does an xpath evaluation of the given expression on the current item (Node, Document, etc)
	 */
	public static Node xpath_node( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Object o = xpath.evaluate( expr, item, NODE );

		if( o == null ) return null;

		return (Node) o;
	}


	public static Node[] xpath_list( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Node n = xpath_node( item, expr );

		if( n == null ) return null;

		// cverbose.println( "found is not null and is a(n): " + get_node_type( n ) );

		// NodeList list = (NodeList) xpath.evaluate( ".", n, NODESET ); // This doesn't work :-(

		NodeList list = (NodeList) xpath.evaluate( expr, item, NODESET );

		ASSERT( list != null );

		Node[] nodes = new Node[ list.getLength() ];

		for( int i=0; i<nodes.length; i++ )
		{
			nodes[ i ] = list.item( i );
		}

		return nodes;
	}


	public static String xpath_value( Object item ) throws XPathExpressionException
	{
		return xpath_value( item, "." );
	}


	public static String xpath_value( Object item, String expr ) throws XPathExpressionException
	{
		//cinfo.println( "finding [%s]/[%s]", item, expr );
		
		xpath.reset();

		Node n = xpath_node( item, expr );

		if( n == null ) return null;
		
		//check_state( n != null );
		
		//cinfo.println( get_node_type( n ) );
		
		//cinfo.println( n.getClass() );
		
		check_state( n.getNodeType() == Node.ELEMENT_NODE || n.getNodeType() == Node.ATTRIBUTE_NODE );

		NodeList node_list = n.getChildNodes();
		
		check_state( node_list.getLength() == 1 );
		
		Node n2 = node_list.item( 0 );
			
		//cinfo.println( get_node_type( n2 ) );
		
		if( n2 instanceof CharacterData )
		{
			String txt = ( (CharacterData) n2 ).getData();

			return txt;
		}
		
		//return n.getNodeValue();
		
		throw new RuntimeException( "not a text node" );
		/*
		NodeList node_list = n.getChildNodes();
		
		Node[] nodes = new Node[ node_list.getLength() ];

		for( int i=0; i<nodes.length; i++ )
		{
			nodes[ i ] = node_list.item( i );
			
			cinfo.println( node_info( nodes[ i ] ) );
		}
		* */
		
		//n.normalize();
		
		//return n.getNodeValue();

		/*
		//cverbose.println( get_node_type( n ) );

		String value = (String) xpath.evaluate( ".", n, STRING );

		ASSERT( value != null );

		return value;
		*/
	}


	public static boolean xpath_exists( Object item, String expr ) throws XPathExpressionException
	{
		return xpath_node( item, expr ) != null;
	}


	public static int xpath_count( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Node n = xpath_node( item, expr );

		if( n == null ) return -1;

		// cverbose.println( "found is not null and is a(n): " + get_node_type( n ) );

		NodeList list = (NodeList) xpath.evaluate( expr, item, NODESET );

		return list.getLength();
	}

/*
	public static Map<String,String> get_node_attributes_map( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Object o = xpath.evaluate( expr, item, NODE );

		if( o == null ) return null;

		Node n = (Node) o;

		NamedNodeMap nnm = n.getAttributes();

		Map<String,String> map = new HashMap<String,String>();

		for( int ai=0; ai<nnm.getLength(); ai++ )
		{
			Node an = nnm.item( ai );

			map.put( an.getNodeName(), an.getNodeValue() );
		}

		return map;
	}
*/


/*
	public static String get_node_attributes_string( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Object o = xpath.evaluate( expr, item, NODE );

		if( o == null ) return null;

		Node n = (Node) o;

		NamedNodeMap nnm = n.getAttributes();

		String attrs = "";

		for( int ai=0; ai<nnm.getLength(); ai++ )
		{
			Node an = nnm.item( ai );

			attrs += fmt( "%s='%s' ", an.getNodeName(), an.getNodeValue() );
		}

		return attrs.trim();
	}
*/


/*
	private static String get_node_value( Object item ) throws XPathExpressionException
	{
		return get_node_value( item, "." );
	}
*/


/*
	private static String get_node_value( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Node n = (Node) xpath.evaluate( expr, item, NODE );

		if( n == null ) return null;

		cverbose.println( get_node_type( n ) );

		return (String) xpath.evaluate( ".", n, STRING );
	}
*/


/*
	public static Node[] get_node_list( Object item, String expr ) throws XPathExpressionException
	{
		xpath.reset();

		Object o = xpath.evaluate( expr, item, NODESET );

		if( o == null ) return null;

		NodeList list = ( NodeList ) o;

		Node[] nodes = new Node[ list.getLength() ];

		for( int i=0; i<nodes.length; i++ )
		{
			nodes[ i ] = list.item( i );
		}

		return nodes;
	}
*/






/*
	public static Node get_child_node( Node parent, String child_name )
	{
		NodeList nl = parent.getChildNodes();

		//cverbose.println( "child nodes length: %d", nl.getLength() );

		for( int i=0; i<nl.getLength(); i++ )
		{
			Node n = nl.item( i );

			//cverbose.println( "child node name: %s", n.getNodeName() );

			if( n.getNodeName().equals( child_name ) )
			{
				return n;
			}
		}

		return null;
	}
*/


	public static String get_node_type( Node node )
	{
		short type = node.getNodeType();

		if( type == Node.ATTRIBUTE_NODE ) return "ATTRIBUTE_NODE";

		if( type == Node.CDATA_SECTION_NODE ) return "CDATA_SECTION_NODE";

		if( type == Node.COMMENT_NODE ) return "COMMENT_NODE";

		if( type == Node.DOCUMENT_FRAGMENT_NODE ) return "DOCUMENT_FRAGMENT_NODE";

		if( type == Node.DOCUMENT_NODE ) return "DOCUMENT_NODE";

		if( type == Node.DOCUMENT_TYPE_NODE ) return "DOCUMENT_TYPE_NODE";

		if( type == Node.ELEMENT_NODE ) return "ELEMENT_NODE";

		if( type == Node.ENTITY_NODE ) return "ENTITY_NODE";

		if( type == Node.ENTITY_REFERENCE_NODE ) return "ENTITY_REFERENCE_NODE";

		if( type == Node.NOTATION_NODE ) return "NOTATION_NODE";

		if( type == Node.PROCESSING_INSTRUCTION_NODE ) return "PROCESSING_INSTRUCTION_NODE";

		if( type == Node.TEXT_NODE ) return "TEXT_NODE";

		return null;
	}


	public static String node_info( Node node )
	{
		EString es = new EString();

		es.println( "getBaseURI(): %s", node.getBaseURI() );

		es.println( "getLocalName(): %s", node.getLocalName() );

		es.println( "getNamespaceURI(): %s", node.getNamespaceURI() );

		es.println( "getNodeName(): %s", node.getNodeName() );

		es.println( "getNodeType(): %s", get_node_type( node ) );

		es.println( "getPrefix(): %s", node.getPrefix() );

		es.println( "hasAttributes(): %s", node.hasAttributes() );

		es.println( "hasChildNodes(): %s", node.hasChildNodes() );

		return es.toString();
	}


	/**
	* <p> Got this code from: http://www.javapractices.com/topic/TopicAction.do?Id=96
	*/
	public static String escape( String s )
	{
		final StringBuilder result = new StringBuilder();

		for( char character : s.toCharArray() )
		{
			if (character == '<')
			{
				result.append( "&lt;" );
			}
			else if (character == '>')
			{
				result.append( "&gt;" );
			}
			else if (character == '"')
			{
				result.append( "&quot;" );
			}
			else if (character == '\'')
			{
				result.append( "&apos;" );
			}
			else if (character == '&')
			{
				result.append( "&amp;" );
			}
			else
			{
				//the char is not a special one
				//add it to the result as is
				result.append(character);
			}
		}

		return result.toString();
	}
}
