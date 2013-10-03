package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlExtractor {

	private XmlPullParserFactory factory;
	private XmlPullParser xpp;
	private String input;

	public XmlExtractor(String input) {

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(input));

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.input = input;

	}

	public void setInput(String newInput) throws XmlPullParserException {
		input = newInput;
		xpp.setInput(new StringReader(input));
	}

	public String extractSingleTag(String tag) {
		String output = null;

		int eventType = 0;
		try {
			eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (tag.equals(xpp.getName())) {
						eventType = xpp.next();
						output = xpp.getText();
						break;
					}
				}
				eventType = xpp.next();

			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;

	}

	public HashMap<String, String> extractTagsFromDoc(String[] tags) {

		HashMap<String, String> output = new HashMap<String, String>();

		int eventType = 0;
		try {
			eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (Arrays.asList(tags).contains(xpp.getName())) {
						String temp_tag = new String(xpp.getName());
						eventType = xpp.next();
						output.put(temp_tag, xpp.getText());
					}
				}
				eventType = xpp.next();

			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public ArrayList<String> unitSeparator(String endTag) {
		String temp_trimdoc = input;
		ArrayList<String> trimmedDoc = new ArrayList<String>();
		boolean contains = input.contains(endTag);

		while (contains) {
			trimmedDoc.add(temp_trimdoc.substring(0,
					temp_trimdoc.indexOf(endTag) + endTag.length()));
			temp_trimdoc = temp_trimdoc.substring(temp_trimdoc.indexOf(endTag)
					+ endTag.length());
			contains = temp_trimdoc.contains(endTag);
		}
		return trimmedDoc;
	}
}
