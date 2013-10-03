package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class EconomicInfoFragment extends Fragment {
	public static HashMap<String, String> hashMap;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myFragmentView = inflater.inflate(R.layout.economic_info_fragment,
				container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		new retrieveInfo().execute();
		
		return myFragmentView;
	}

	private class retrieveInfo extends AsyncTask<String, Void, Void> {
		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(String... params) {
			String target_url = setTargetURL();
			String economic_data = getXmlPage(target_url);
			String[] tags = getResources()
					.getStringArray(R.array.economic_info);

			XmlExtractor xml_extractor = new XmlExtractor(economic_data);
			hashMap = xml_extractor.extractTagsFromDoc(tags);
			return null;

		}

		private String setTargetURL() {
			final String UNIV_SPEC_DEG = "university_specific_degrees";
			final String ECONOMIC_REP = "economic_report";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";

			GlobalState gs = (GlobalState) getActivity().getApplication();
			String base_url = gs.getTargetAddress();

			String degree_id = getActivity().getIntent().getStringExtra("id");

			String target_url = base_url + UNIV_SPEC_DEG + SLASH + degree_id
					+ SLASH + ECONOMIC_REP + XML_EXTENSION;

			return target_url;
		}

		private String getXmlPage(String target_url) {
			XmlPage xml_page = new XmlPage(getActivity());
			String xml_response = null;
			try {
				xml_response = xml_page.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}
			return xml_response;
		}

		@Override
		protected void onPostExecute(Void param) {

			((MyFragmentActivity) getActivity()).dismissWindow();
			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);
			}

		}

	}

}
