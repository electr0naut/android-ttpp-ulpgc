package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class SubjectsListFragment extends Fragment implements
		CustomSubjectsAdapter.OnSubjectClickedListener, OnClickListener {
	private ListView listview;
	private CustomSubjectsAdapter custom_adapter;
	private ArrayList<ArrayList<String>> fetch = new ArrayList<ArrayList<String>>();
	private View myView;
	private Handler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		new retrieveInfo().execute();

		myView = inflater.inflate(R.layout.subjects_list_fragment, container,
				false);

		listview = (ListView) myView.findViewById(R.id.subject_list);
		custom_adapter = new CustomSubjectsAdapter(this, R.id.subject_list,
				fetch);
		listview.setAdapter(custom_adapter);

		Button footer = (Button) myView.findViewById(R.id.amount_footer);
		footer.setOnClickListener(this);

		// setUpFooterButton();

		mHandler = new Handler() {
			@Override
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				custom_adapter.add((ArrayList<String>) msg.obj);
				custom_adapter.notifyDataSetChanged();
			}
		};

		return myView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.amount_footer: {
			v.setVisibility(View.INVISIBLE);
			break;
		}
		}
	}

	private class retrieveInfo extends AsyncTask<String, Void, Void> {
		ArrayList<String> subject_list;
		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(String... params) {

			String target_url = setTargetUrl();
			XmlPage xml_subjects_list = new XmlPage(getActivity());

			String subjects_list;
			try {
				subjects_list = xml_subjects_list.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}

			XmlExtractor xml_extractor = new XmlExtractor(subjects_list);
			subject_list = new ArrayList<String>(
					xml_extractor.unitSeparator("</subject>"));

			return null;
		}

		private String setTargetUrl() {
			final String UNIV_SPEC_DEG = "university_specific_degrees";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";
			final String SUBJECTS = "subjects";

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
					+ SLASH + SUBJECTS + XML_EXTENSION;

			return target_url;
		}

		protected void onPostExecute(Void param) {

			((MyFragmentActivity) getActivity()).dismissWindow();
			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);
			} else {
				populateViews(subject_list);
			}

		}

		private void populateViews(ArrayList<String> subject_list) {

			for (int i = 0; i < subject_list.size(); i++) {
				XmlExtractor xml_extractor = null;
				xml_extractor = new XmlExtractor(subject_list.get(i));
				HashMap<String, String> single_subject_data = null;

				single_subject_data = new HashMap<String, String>(
						xml_extractor.extractTagsFromDoc(getResources()
								.getStringArray(R.array.complete_subject_info)));
				single_subject_data.put("whole", subject_list.get(i));
				SubjectInfoContainer subject_info = new SubjectInfoContainer(
						single_subject_data);

				Message msg = new Message();
				msg.obj = subject_info.getArrayList();
				mHandler.sendMessage(msg);
			}
			Button footer = (Button) myView.findViewById(R.id.amount_footer);
			footer.setText(Html.fromHtml("<b>" + subject_list.size() + "/"
					+ subject_list.size() + " "
					+ getResources().getString(R.string.loadedSubjects)
					+ "</b>"));

		}
	}

	@Override
	public void OnSubjectClicked(String subject_info) {
		if (getActivity().findViewById(R.id.lateral_fragment) == null) {

			Intent intent = new Intent(getActivity(), SingleSubject.class);
			intent.putExtra("subject-info", subject_info);
			startActivity(intent);
		} else {
			Bundle args = new Bundle();
			args.putString("subject-info", subject_info);
			SingleSubjectFragment singleSubjectFragment = new SingleSubjectFragment();
			singleSubjectFragment.setArguments(args);
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.lateral_fragment, singleSubjectFragment)
					.commit();
		}
	}

}
