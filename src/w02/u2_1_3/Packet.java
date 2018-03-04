package w02.u2_1_3;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

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
	
	public void create() {
		doc = builder.newDocument();

	}
	
	
	public String getPayloadAsString() {
		return doc.getTextContent();
	}
	
	public static void main(String[] args) {
		Packet p = new Packet();
		p.create();
		System.out.println(p.getPayloadAsString());
//		System.out.println(p.doc.getTextContent());
	}
	
}
