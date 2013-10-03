package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class SearchDegreesFragment extends Fragment {

	private ArrayAdapter<CharSequence> types_adapter, states_adapter,
			lectures_adapter;
	private String edicion, estado, tipo;
	private Spinner option1, option2, option3;
	private Button search_button;
	private Degree[] degree_list;
	private View myFragmentView;
	private OnShowResultsListener mCallback;

	public interface OnShowResultsListener {
		public void onShowResults(Degree[] degrees);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnShowResultsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnShowResultsListener");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myFragmentView = inflater.inflate(R.layout.search_degrees_fragment,
				container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setSpinnerValues();

		setSearchButton();
		return myFragmentView;
	}

	private class search extends AsyncTask<String, Integer, Integer> {
		private static final String PAGE_URL = "page=";
		private IOException error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.searching);

		}

		@Override
		protected Integer doInBackground(String... params) {

			int page_number = 1;
			String base_url = params[0];
			String resource_url = params[1];

			String target_url = base_url + PAGE_URL + page_number
					+ resource_url;

			String response = getXmlPage(target_url);
			if (response == null) {

				return 0;
			}

			DegreePage degrees_page = new DegreePage(response);
			ArrayList<String> degrees = new ArrayList<String>();

			while (!degrees_page.isEmpty()) {
				degrees.addAll(degrees_page.trimXML());
				target_url = setNewPageAddress(base_url, resource_url,
						++page_number);
				response = getXmlPage(target_url);
				if (response == null) {
					return null;
				}
				degrees_page = new DegreePage(response);
				if (page_number > 2)
					publishProgress(page_number - 2);

			}
			degree_list = new Degree[degrees.size()];

			for (int i = 0; i < degrees.size(); i++) {

				degree_list[i] = new Degree(degrees.get(i), getActivity()
						.getApplication());
			}

			return degrees.size();
		}

		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

			((MyFragmentActivity) getActivity())
					.updateProgressDialog(getResources().getString(
							R.string.searchMessage_before)
							+ " "
							+ Integer.toString(values[0] * 20)
							+ " "
							+ getResources().getString(
									R.string.searchMessage_after));

		}

		@Override
		protected void onPostExecute(Integer i) {
			((MyFragmentActivity) getActivity()).dismissWindow();

			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(2);

			} else {
				showAlertDialog(i);
			}

		}

		private void showAlertDialog(Integer final_result_number) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());

			alertDialogBuilder.setTitle(getResources().getString(
					R.string.searchResults_before)
					+ " "
					+ final_result_number
					+ " "
					+ getResources().getString(R.string.searchResults_after));

			alertDialogBuilder
					.setMessage(R.string.finishSearch)
					.setCancelable(false)
					.setPositiveButton(R.string.showResults,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									mCallback.onShowResults(degree_list);
								}
							})
					.setNegativeButton(R.string.repeatSearch,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									getActivity().finish();
									startActivity(getActivity().getIntent());
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

		private String setNewPageAddress(String base_url, String resource_url,
				int page_number) {
			return base_url + PAGE_URL + page_number + resource_url;
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

	}

	private void setSearchButton() {
		search_button = (Button) myFragmentView
				.findViewById(R.id.search_button);
		search_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String[] search_url = createSearchURLFromInput();

				new search().execute(search_url);
			}

			private String[] createSearchURLFromInput() {
				final String BASE_URL = "university_specific_degrees.xml?";
				final String SEARCH_BY_CODE = "&search[code_like]=";
				final String SEARCH_BY_NAME = "&search[name_like]=";
				final String SEARCH_BY_DATE = "&search[period_id_eq]=";
				final String SEARCH_BY_TYPE = "&search[degree_type_id_eq]=";
				final String SEARCH_BY_STAT = "&search[state_in][]=";
				final String COMMIT = "&commit=Buscar";

				GlobalState gs = (GlobalState) getActivity().getApplication();
				String base_url = new String(gs.getTargetAddress());
				String resource_url;

				int edition, type;
				String state, code, name;

				name = getDegreeName();
				code = getDegreeCode();
				edition = getDegreeEditionNumber();
				type = getDegreeType();
				state = getDegreeState();

				base_url += BASE_URL;
				resource_url = SEARCH_BY_CODE + code;
				resource_url += SEARCH_BY_NAME + name;

				resource_url += SEARCH_BY_DATE;
				if (edition != 0) {
					resource_url += edition;
				}

				resource_url += SEARCH_BY_TYPE;
				if (type != 0) {
					resource_url += type;
				}
				if (!state.equals("")) {
					resource_url += SEARCH_BY_STAT + state;
				}
				resource_url += COMMIT;

				String[] output_urls = new String[2];
				output_urls[0] = base_url;
				output_urls[1] = resource_url;
				return output_urls;
			}

			private String getDegreeCode() {
				EditText search_bycode;
				search_bycode = (EditText) myFragmentView
						.findViewById(R.id.busqueda_codigo);

				return search_bycode.getText().toString();
			}

			private String getDegreeName() {
				EditText search_byname;

				search_byname = (EditText) myFragmentView
						.findViewById(R.id.busqueda_nombre);

				return search_byname.getText().toString();
			}

			private String getDegreeState() {
				if (estado.equals("Borrador"))
					return "borrador";
				else if (estado.equals("Enviado"))
					return "enviado";
				else if (estado.equals("Rechazado"))
					return "rechazado";
				else if (estado.equals("Aprobado"))
					return "aprobado";
				else if (estado.equals("En subsanación"))
					return "subsanacion";
				else if (estado.equals("Cancelado"))
					return "cancelado";
				else
					return "";
			}

			private int getDegreeType() {
				if (tipo.equals("Enseñanza Propia Básica"))
					return 1;
				else if (tipo.equals("Enseñanza Propia Superior"))
					return 2;
				else if (tipo.equals("Experto"))
					return 3;
				else if (tipo.equals("Maestría"))
					return 4;
				else if (tipo.equals("Diploma Profesional"))
					return 5;
				else
					return 0;
			}

			private int getDegreeEditionNumber() {
				if (edicion.equals("2011/2012"))
					return 2;
				else if (edicion.equals("2012/2013"))
					return 3;
				else if (edicion.equals("2013/2014"))
					return 4;
				else if (edicion.equals("2014/2015"))
					return 5;
				else
					return 0;
			}
		});
	}

	private void setSpinnerValues() {

		createSpinnersFromResources();
		connectSpinnersWithAdapters();

	}

	private void connectSpinnersWithAdapters() {
		final String NONE_SELECTED = "nothing";

		LinearLayout linear_layout = (LinearLayout) myFragmentView
				.findViewById(R.id.base_linear_search);

		option1 = (Spinner) linear_layout.findViewById(R.id.edicion_spinner);
		option2 = (Spinner) linear_layout.findViewById(R.id.tipo_spinner);
		option3 = (Spinner) linear_layout.findViewById(R.id.estado_spinner);

		option1.setAdapter(types_adapter);
		option2.setAdapter(lectures_adapter);
		option3.setAdapter(states_adapter);

		option1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				tipo = (String) types_adapter.getItem(position);
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				tipo = NONE_SELECTED;
			}
		});
		option2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				edicion = (String) lectures_adapter.getItem(position);
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				edicion = NONE_SELECTED;
			}
		});
		option3.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				estado = (String) states_adapter.getItem(position);
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				estado = NONE_SELECTED;
			}
		});
	}

	private void createSpinnersFromResources() {
		types_adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.degree_types, android.R.layout.simple_spinner_item);
		states_adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.degree_states, android.R.layout.simple_spinner_item);
		lectures_adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.degree_editions, android.R.layout.simple_spinner_item);

		types_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		states_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lectures_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
}
