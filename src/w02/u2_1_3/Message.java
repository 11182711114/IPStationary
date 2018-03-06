package w02.u2_1_3;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Message extends DefaultHandler {
	private String protocolType;
	private String version;
	private String command;
	
	private String name;
	private String email;
	private String homepage;
	private String host;
	
	private String body;
	
	
	// This is fucking stupid
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		
	}
	
	@Override
    public void endElement(String uri, String localName, String qName) throws SAXException{
    
	}
	
	@Override
    public void characters(char ch[], int start, int length) throws SAXException {
		
	}
}
