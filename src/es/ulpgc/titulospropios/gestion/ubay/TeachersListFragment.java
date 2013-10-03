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

public class TeachersListFragment extends Fragment implements
		CustomTeacherAdapter.OnTeacherClickedListener, OnClickListener {
	private ArrayList<ArrayList<String>> fetch = new ArrayList<ArrayList<String>>();
	private ArrayList<String> arrayList_degreeTeachers;
	private CustomTeacherAdapter custom_adapter;
	private Handler mDegreeTeachersHandler;
	private Handler mFullTeachersHandler;
	private ListView listview;
	private View myView;
	private Button footer;
	private int total_teachers;
	private int current_teachers;
	private Runnable run;
	private ArrayList<HashMap<String, String>> completeDegreeTeachers = new ArrayList<HashMap<String, String>>();
	private AsyncTask<String, Void, Void> first_task;
	private AsyncTask<String, Integer, Void> second_task;

	@Override
	public void onStop(){
		super.onStop();
		if (first_task != null){
			first_task.cancel(true);
		}
		if (second_task != null){
			second_task.cancel(true);
		}
	}
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		if (first_task != null){
			first_task.cancel(true);
		}
		if (second_task != null){
			second_task.cancel(true);
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (first_task != null){
			first_task.cancel(true);
		}
		if (second_task != null){
			second_task.cancel(true);
		}
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
		if (first_task != null){
			first_task.cancel(true);
		}
		if (second_task != null){
			second_task.cancel(true);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		first_task = new retrieveDegreeParticipations();
		first_task.execute();

		myView = inflater.inflate(R.layout.teachers_list_fragment, container,
				false);
		listview = (ListView) myView.findViewById(R.id.teachers_list);
		custom_adapter = new CustomTeacherAdapter(this, R.id.teachers_list,
				fetch, getActivity().getApplicationContext());
		listview.setAdapter(custom_adapter);

		footer = (Button) myView.findViewById(R.id.amount_footer);
		footer.setOnClickListener(this);

		mDegreeTeachersHandler = setUpDegreeTeachersHandler();

		mFullTeachersHandler = setUpFullTeachersHandler();

		run = new Runnable() {
			public void run() {
				custom_adapter.notifyDataSetChanged();
				footer.setText(Html.fromHtml("<b>" + current_teachers + "/"
						+ total_teachers + " "
						+ getResources().getString(R.string.loadedTeachers)
						+ "</b>"));

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

	private Handler setUpFullTeachersHandler() {
		return new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				/*
				 * ArrayList<HashMap<String, String>> temp =
				 * (ArrayList<HashMap<String, String>>) msg.obj; for (int i = 0;
				 * i < temp.size(); i++) { TeacherInfoContainer teacher_info =
				 * new TeacherInfoContainer( temp.get(i));
				 * custom_adapter.add(teacher_info.getArrayList()); }
				 */
				TeacherInfoContainer teacher_info = new TeacherInfoContainer(
						(HashMap<String, String>) msg.obj);
				custom_adapter.add(teacher_info.getArrayList());
				getActivity().runOnUiThread(run);
			}
		};
	}

	private Handler setUpDegreeTeachersHandler() {
		return new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				ArrayList<String> temp = (ArrayList<String>) msg.obj;
				XmlExtractor xml_extractor;	
				String[] tags = getResources().getStringArray(
						R.array.degree_teachers_info);
				total_teachers = temp.size();
				for (int i = 0; i < temp.size(); i++) {

					xml_extractor = new XmlExtractor(temp.get(i));
					HashMap<String, String> single_teacher_part = new HashMap<String, String>(
							xml_extractor.extractTagsFromDoc(tags));
					completeDegreeTeachers.add(single_teacher_part);
				}
				second_task = new retrieveFullTeachers();
				second_task.execute();
			}
		};
	}

	private class retrieveDegreeParticipations extends
			AsyncTask<String, Void, Void> {
		IOException error;

		protected Void doInBackground(String... params) {
			String target_url = setTargetUrl();
			XmlPage xml_retriever = new XmlPage(getActivity());
			String xml_response = null;
			try {
				xml_response = xml_retriever.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}

			XmlExtractor xml_extractor = new XmlExtractor(xml_response);

			arrayList_degreeTeachers = new ArrayList<String>(
					xml_extractor.unitSeparator("</degree-teacher>"));
			return null;
		}

		private String setTargetUrl() {
			final String UNIV_SPEC_DEG = "university_specific_degrees";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";
			final String DEGREE_TEACHERS = "degree_teachers";

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
					+ SLASH + DEGREE_TEACHERS + XML_EXTENSION;

			return target_url;

		}

		protected void onPostExecute(Void param) {

			if (error != null) {
				((MyFragmentActivity) getActivity()).dismissWindow();

				((MyFragmentActivity) getActivity()).launchErrorWindow(0);

			} else {
				Message msg = new Message();
				msg.obj = arrayList_degreeTeachers;
				mDegreeTeachersHandler.sendMessage(msg);
			}

		}
	}

	private class retrieveFullTeachers extends AsyncTask<String, Integer, Void> {
		IOException error;

		protected Void doInBackground(String... params) {
			XmlPage xml_retriever = new XmlPage(getActivity());

			String[] tags = getResources().getStringArray(
					R.array.generic_teachers_info);

			for (int i = 0; i < completeDegreeTeachers.size(); i++) {

				String target_url = setTargetUrl(i);

				String xml_response;
				try {
					xml_response = xml_retriever.getPage(target_url);
				} catch (IOException e) {
					error = e;
					return null;
				}

				XmlExtractor xml_extractor = new XmlExtractor(xml_response);
				HashMap<String, String> extracted_teacher = xml_extractor
						.extractTagsFromDoc(tags);
				String teacher_type = determineTeacherTypeFromBaseXMLTag(xml_response);
				completeDegreeTeachers.get(i).putAll(extracted_teacher);
				completeDegreeTeachers.get(i).put("type", teacher_type);
				completeDegreeTeachers.get(i).put("whole", xml_response);
				publishProgress(i);

			}

			return null;

		}

		protected void onProgressUpdate(Integer... values) {

			Message msg = new Message();
			msg.obj = completeDegreeTeachers.get(values[0]);
			mFullTeachersHandler.sendMessage(msg);
			current_teachers = values[0] + 1;
		}

		protected void onPostExecute(Void param) {

			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);

			}
		}

		private String determineTeacherTypeFromBaseXMLTag(String xml_response) {

			return xml_response.substring(40, 45);
		}

		private String setTargetUrl(int i) {
			final String PARTICIPATIONS_URL = "teachers";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";

			GlobalState gs = (GlobalState) getActivity().getApplication();
			String base_url = gs.getTargetAddress();

			String target_url = base_url + PARTICIPATIONS_URL + SLASH
					+ completeDegreeTeachers.get(i).get("teacher-id")
					+ XML_EXTENSION;
			return target_url;
		}

	}

	@Override
	public void OnTeacherClicked(String teacher_info) {
		if (getActivity().findViewById(R.id.lateral_fragment) == null) {

			Intent intent = new Intent(getActivity(), SingleTeacher.class);
			intent.putExtra("teacher-info", teacher_info);
			startActivity(intent);
		} else {
			Bundle args = new Bundle();
			args.putString("teacher-info", teacher_info);
			SingleTeacherFragment singleTeacherFragment = new SingleTeacherFragment();
			singleTeacherFragment.setArguments(args);
			getActivity().getSupportFragmentManager().beginTransaction()
					.replace(R.id.lateral_fragment, singleTeacherFragment)
					.commit();
		}
	}
}
