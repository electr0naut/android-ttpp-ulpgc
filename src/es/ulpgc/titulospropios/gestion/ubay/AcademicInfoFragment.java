package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AcademicInfoFragment extends Fragment {
	private static final int[] textview_list = { R.id.name, R.id.branch,
			R.id.teaching_mode, R.id.state, R.id.edition, R.id.code,
			R.id.proposer, R.id.startDate, R.id.endDate, R.id.units,
			R.id.teachingPlace, R.id.manager, R.id.institutionRepresentative,
			R.id.headquarters, R.id.evalAcecau, R.id.competencias,
			R.id.justificaCompetencias, R.id.resultadosEsperados,
			R.id.recursosDisponibles };

	private View myView;
	private AsyncTask<String, Void, Void> task;
	
	@Override
	public void onStop(){
		super.onStop();
		if (task != null){
			task.cancel(true);
		}
	}
	@Override
	public void onDestroy(){
		super.onStop();
		if (task != null){
			task.cancel(true);
		}
	}
	@Override
	public void onDetach(){
		super.onStop();
		if (task != null){
			task.cancel(true);
		}
	}
	@Override
	public void onDestroyView(){
		super.onStop();
		if (task != null){
			task.cancel(true);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		task = new retrieveInfo();
		task.execute();
		myView = inflater.inflate(R.layout.academic_info_fragment, container,
				false);
		return myView;
	}

	private class retrieveInfo extends AsyncTask<String, Void, Void> {
		Degree degree;
		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(String... params) {
			String target_url = setTargetURL();
			String degree_data_xml = getXmlPage(target_url);
			if (degree_data_xml != null) {
				degree = new Degree(degree_data_xml, getActivity());
			}

			return null;

		}

		private String setTargetURL() {
			final String UNIV_SPEC_DEG = "university_specific_degrees";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";

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
					+ XML_EXTENSION;

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
			return xml_response;//
		}

		@Override
		protected void onPostExecute(Void param) {

			((MyFragmentActivity) getActivity()).dismissWindow();
			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);
			} else {
				populateViews(degree.getData());
			}

		}

	}

	public void populateViews(HashMap<String, String> hashMap) {
		TextView textview;
		String[] tags_eng = getResources()
				.getStringArray(R.array.academic_info);

		String[] tags_es = getResources().getStringArray(
				R.array.informacion_academica);

		for (int i = 0; i < tags_es.length - 1; i++) {

			textview = (TextView) myView.findViewById(textview_list[i]);
			/*
			 * if ((i > 1) && (i < 15) && (i % 2 == 0)) {
			 * textview.setGravity(Gravity.RIGHT); }
			 */
			switch (i) {

			case 0: {
				if (hashMap.containsKey(tags_eng[i])) {
					textview.setText(tags_es[i] + " "
							+ hashMap.get(tags_eng[i]));
				} else
					textview.setText(tags_es[i]);
				break;
			}
			case 1: {
				String[] tags = getResources().getStringArray(
						R.array.branchKnowledge);

				if (hashMap.containsKey(tags_eng[i])) {
					String rama = null;
					if (hashMap.get(tags_eng[i]).equals("1")) {
						rama = tags[0];
					} else if (hashMap.get(tags_eng[i]).equals("2")) {
						rama = tags[1];
					} else if (hashMap.get(tags_eng[i]).equals("3")) {
						rama = tags[2];
					} else if (hashMap.get(tags_eng[i]).equals("4")) {
						rama = tags[3];
					} else if (hashMap.get(tags_eng[i]).equals("5")) {
						rama = tags[4];
					}
					textview.setText(Html.fromHtml("<b>" + tags_es[i]
							+ "</b><br>" + rama));
				} else
					textview.setText(Html.fromHtml("<b>" + tags_es[i] + "</b>"));
				break;
			}
			case 2: {
				String[] tags = getResources().getStringArray(
						R.array.degree_types);
				if (hashMap.containsKey(tags_eng[i])) {
					String tipo = null;
					if (hashMap.get(tags_eng[i]).equals("1")) {
						tipo = tags[1];
					} else if (hashMap.get(tags_eng[i]).equals("2")) {
						tipo = tags[2];
					} else if (hashMap.get(tags_eng[i]).equals("3")) {
						tipo = tags[3];
					} else if (hashMap.get(tags_eng[i]).equals("4")) {
						tipo = tags[4];
					} else if (hashMap.get(tags_eng[i]).equals("5")) {
						tipo = tags[5];
					}
					textview.setText(Html.fromHtml("<b>" + tags_es[i] + "</b> "
							+ tipo));

				} else
					textview.setText(tags_es[i]);
				break;
			}
			case 3:
			case 4:
			case 6:
			case 7: {
				if (hashMap.containsKey(tags_eng[i])) {
					textview.setText(Html.fromHtml("<b>" + tags_es[i] + "</b> "
							+ hashMap.get(tags_eng[i])));
				} else
					textview.setText(Html.fromHtml("<b>" + tags_es[i] + "</b>"));
				break;
			}
			case 8:
			case 9: {

				if (hashMap.containsKey(tags_eng[i])) {
					String temp = hashMap.get(tags_eng[i]);
					if (!(temp == null)) {
						textview.setText(Html.fromHtml("<b>" + tags_es[i]
								+ "</b><br> " + hashMap.get(tags_eng[i])));
					} else
						textview.setText(Html.fromHtml("<b>"
								+ tags_es[i]
								+ "</b><br> "
								+ getResources()
										.getString(R.string.unspecified)));
				}
				break;

			}
			case 5:
			case 10:
			case 11:
			case 12:

			case 15:
			case 16:
			case 17:
			case 18: {
				textview = (TextView) myView.findViewById(textview_list[i]);
				if (hashMap.containsKey(tags_eng[i])) {
					textview.setText(Html.fromHtml("<b>" + tags_es[i]
							+ "</b><br> " + hashMap.get(tags_eng[i])));
				} else
					textview.setText(Html.fromHtml("<b>" + tags_es[i]
							+ "</b><br>"));
				break;
			}
			case 13: {
				textview = (TextView) myView.findViewById(textview_list[i]);
				if (hashMap.containsKey(tags_eng[i])) {
					String temp = hashMap.get(tags_eng[i]);
					if (!(temp == null)) {
						textview.setText(Html.fromHtml("<b>" + tags_es[i]
								+ "</b><br> " + hashMap.get(tags_eng[i])));
					} else
						textview.setText(Html.fromHtml("<b>"
								+ tags_es[i]
								+ "</b><br> "
								+ getResources()
										.getString(R.string.unspecified)));
				}
				break;
			}
			case 14: {
				if (hashMap.containsKey(tags_eng[i])) {
					String temp = hashMap.get(tags_eng[i]);
					if (!(temp == null)) {
						textview.setText(Html.fromHtml("<b>" + tags_es[i]
								+ "</b><br> " + hashMap.get(tags_eng[i])));
					} else
						textview.setText(Html.fromHtml("<b>"
								+ tags_es[i]
								+ "</b><br> "
								+ getResources()
										.getString(R.string.unspecified)));
				}
				break;
			}

			}
		}
	}
}