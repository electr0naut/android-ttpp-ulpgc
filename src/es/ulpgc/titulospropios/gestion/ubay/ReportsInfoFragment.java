package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportsInfoFragment extends Fragment {
	public static final String ENDTAG = "</issue>";
	public static final String MESSAGE = "message";
	View myView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myView = inflater.inflate(R.layout.reports_info_fragment, container,
				false);
		Bundle bundle = getArguments();

		if (bundle == null) {
			bundle = getActivity().getIntent().getExtras();
		}
		String id = bundle.getString("id");

		new retrieveIssues().execute(id);

		return myView;
	}

	private class retrieveIssues extends AsyncTask<String, Void, Integer> {
		String issues = null;
		ArrayList<String> message_list;
		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.loadingMessage);
		}

		protected Integer doInBackground(String... id) {
			XmlPage xml_issues = new XmlPage(getActivity());
			String target_url = setTargetUrl();
			try {
				issues = xml_issues.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}

			message_list = extractMessagesFromXmlPage(issues);
			return 1;

		}

		protected void onPostExecute(Integer result) {
			((MyFragmentActivity) getActivity()).dismissWindow();

			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);
				return;
			}

			if (message_list.size() == 0) {
				setNoIssuesMessage();

			} else {
				populateLayoutWithIssues(message_list);

			}
		}
	}

	private void populateLayoutWithIssues(ArrayList<String> message_list) {
		LinearLayout base_layout = (LinearLayout) myView
				.findViewById(R.id.base_issues_layout);
		for (int i = 0; i < message_list.size(); i++) {
			TextView message = new TextView(getActivity());
			message.setText(message_list.get(i));
			message.setBackgroundResource(R.drawable.blue_border);
			base_layout.addView(message);
		}
	}

	private void setNoIssuesMessage() {
		LinearLayout base_layout = (LinearLayout) myView
				.findViewById(R.id.base_issues_layout);
		TextView message = new TextView(getActivity());
		message.setText(R.string.message_no_errors);
		message.setBackgroundResource(R.drawable.blue_border);
		base_layout.addView(message);
	}

	private ArrayList<String> extractMessagesFromXmlPage(String issues) {
		XmlExtractor myXmlExtractor = new XmlExtractor(issues);
		ArrayList<String> message_list = new ArrayList<String>();

		ArrayList<String> issues_list = myXmlExtractor.unitSeparator(ENDTAG);

		for (int i = 0; i < issues_list.size(); i++) {
			String single_issue = issues_list.get(i);
			XmlExtractor single_issueXmlExtractor = new XmlExtractor(
					single_issue);
			message_list
					.add(single_issueXmlExtractor.extractSingleTag(MESSAGE));
		}
		return message_list;
	}

	private String setTargetUrl() {
		final String UNIV_SPEC_DEG = "university_specific_degrees";
		final String XML_EXTENSION = ".xml";
		final String SLASH = "/";
		final String ISSUES = "issues";

		GlobalState gs = (GlobalState) getActivity().getApplication();
		String base_url = gs.getTargetAddress();

		String degree_id = "";
		Bundle bundle = getArguments();

		if (bundle != null) {
			degree_id = bundle.getString("id");
		} else {
			degree_id = getActivity().getIntent().getStringExtra("id");
		}

		String target_url = base_url + UNIV_SPEC_DEG + SLASH + degree_id
				+ SLASH + ISSUES + XML_EXTENSION;

		return target_url;
	}
}
