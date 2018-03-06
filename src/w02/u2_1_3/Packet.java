package w02.u2_1_3;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class Packet {
	private DocumentBuilder builder;	
	private Document doc;
	
	public Packet() {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void create(){
		try {
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.parse("src/w02/u2_1_3/message.xml");
			
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new InputSource("src/w02/u2_1_3/message.xml"), new DefaultHandler());
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}

	}
	
	
	public String getPayloadAsString() {
		return doc.getTextContent();
	}
	
	public static void main(String[] args) {
		Packet p = new Packet();
		p.create();
//		System.out.println(p.getPayloadAsString());
//		System.out.println(p.doc.getTextContent());
	}
	
}
