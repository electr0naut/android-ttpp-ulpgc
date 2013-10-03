package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class Degree {
	String[] tags;
	HashMap<String, String> information;

	public Degree(String xml_document, Context mContext) {
		tags = mContext.getResources().getStringArray(R.array.academic_info);
		information = new HashMap<String, String>(extractTags(xml_document));
	}

	private HashMap<String, String> extractTags(String xml_document) {
		XmlPullParserFactory factory;
		XmlPullParser xpp = null;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xpp = factory.newPullParser();
			xpp.setInput(new StringReader(xml_document));
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		HashMap<String, String> output = new HashMap<String, String>();

		int eventType = 0;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (Arrays.asList(tags).contains(xpp.getName())) {
					String current_tag = new String(xpp.getName());
					try {
						eventType = xpp.next();
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					output.put(current_tag, xpp.getText());
				}
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;

	}

	public HashMap<String, String> getData() {
		return information;
	}

}
