package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;

public class DegreePage {
	private String xml_document;
	private static final String endTag = "</university-specific-degree>";
	private static final String emptyTag = "<nil-classes type=\"array\"/>";
	
	public DegreePage(String xml_document) {
		this.xml_document = xml_document;
	}
	public ArrayList<String> trimXML(){
		
		String temp_trimdoc = xml_document;
		ArrayList<String> trimmedDoc = new ArrayList<String>();
		boolean contains = xml_document.contains(endTag);

		while (contains) {
			trimmedDoc.add(temp_trimdoc.substring(0,
					temp_trimdoc.indexOf(endTag) + endTag.length()));
			temp_trimdoc = temp_trimdoc.substring(temp_trimdoc.indexOf(endTag)
					+ endTag.length());
			contains = temp_trimdoc.contains(endTag);
		}
		return trimmedDoc;
	}
	public boolean isEmpty(){
		return xml_document.contains(emptyTag);
	}

}
