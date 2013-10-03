package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateDegree extends MyFragmentActivity {

	private Context baseContext = this;
	private Handler mHandler, senderHandler;
	private ArrayAdapter<CharSequence> types_adapter, modes_adapter,
			oriented_adapter, knowledge_adapter, institutions_adapter;
	private String type, mode, oriented, knowledge, institutions;
	private Spinner option1, option2, option3, option4, option5;
	private static final String NONE_SELECTED = "nothing";
	private String period_id;
	private String institution_id;
	private int institution_index;
	private boolean outsideCanaryIslands = false;
	private int type_index, mode_index, oriented_index, knowledge_index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new downloadPeriods().execute();

		mHandler = new Handler() {
			@SuppressLint("SimpleDateFormat")
			@Override
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				String[] period_tags = { "request-from-date", "request-to-date" };
				Date cDate = new Date();
				String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
				HashMap<String, String> current_period = (HashMap<String, String>) msg.obj;

				if (!(fDate.compareTo(current_period.get(period_tags[0])) >= 0 && fDate
						.compareTo(current_period.get(period_tags[1])) <= 0)) {

					AlertDialogGoingBack(R.string.outOfPeriodForProposal);

				} else {
					setContentView(R.layout.create_degree_activity);
					setSpinnerValues();
					setUpCheckBox();

					TextView period = (TextView) findViewById(R.id.period);
					period.setText(current_period.get("year"));
					period_id = current_period.get("id");
				}

			}
		};

		senderHandler = new Handler() {
			public void handleMessage(Message msg) {
				new sendVerifiedProposal().execute();
			}
		};

	}

	private void setUpCheckBox() {
		CheckBox check_box = (CheckBox) findViewById(R.id.teaching_outside);
		check_box.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				outsideCanaryIslands = ((CheckBox) v).isChecked();
			}
		});
	}

	private void setSpinnerValues() {

		createSpinnersFromResources();
		connectSpinnersWithAdapters();

	}

	private void connectSpinnersWithAdapters() {

		option1 = (Spinner) findViewById(R.id.type_spinner);
		option2 = (Spinner) findViewById(R.id.mode_spinner);
		option3 = (Spinner) findViewById(R.id.oriented_spinner);
		option4 = (Spinner) findViewById(R.id.knowledge_spinner);
		option5 = (Spinner) findViewById(R.id.institutions_spinner);

		option1.setAdapter(types_adapter);
		option2.setAdapter(modes_adapter);
		option3.setAdapter(oriented_adapter);
		option4.setAdapter(knowledge_adapter);
		option5.setAdapter(institutions_adapter);

		option1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				type = (String) types_adapter.getItem(position);
				type_index = position;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				type = NONE_SELECTED;
			}
		});
		option2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				mode = (String) modes_adapter.getItem(position);
				mode_index = position;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				mode = NONE_SELECTED;
			}
		});
		option3.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				oriented = (String) oriented_adapter.getItem(position);
				oriented_index = position;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				oriented = NONE_SELECTED;
			}
		});
		option4.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				knowledge = (String) knowledge_adapter.getItem(position);
				knowledge_index = position;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				knowledge = NONE_SELECTED;
			}
		});
		option5.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				institutions = (String) institutions_adapter.getItem(position);
				EditText institutions_address = (EditText) findViewById(R.id.teaching_address);
				institutions_address.setText(getResources().getStringArray(
						R.array.institutions_addresses)[position]);
				institution_id = getResources().getStringArray(
						R.array.institutions_id)[position];
				institution_index = position;
			}

			public void onNothingSelected(AdapterView<?> parentView) {
				institutions = NONE_SELECTED;
				institution_id = "24";
				institution_index = 0;
			}
		});
	}

	private void createSpinnersFromResources() {
		types_adapter = ArrayAdapter.createFromResource(this,
				R.array.degree_types, android.R.layout.simple_spinner_item);
		modes_adapter = ArrayAdapter.createFromResource(this,
				R.array.teachingModeComplete,
				android.R.layout.simple_spinner_item);
		oriented_adapter = ArrayAdapter.createFromResource(this,
				R.array.orientedTo, android.R.layout.simple_spinner_item);
		knowledge_adapter = ArrayAdapter.createFromResource(this,
				R.array.branchKnowledgeComplete,
				android.R.layout.simple_spinner_item);
		institutions_adapter = ArrayAdapter.createFromResource(this,
				R.array.institutions, android.R.layout.simple_spinner_item);

		types_adapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		modes_adapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		oriented_adapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		knowledge_adapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		institutions_adapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
	}

	private class downloadPeriods extends AsyncTask<Void, Void, Void> {

		IOException error;
		HashMap<String, String> current_period;

		@Override
		protected void onPreExecute() {
			setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(Void... params) {
			final String PERIOD_ENDTAG = "</period>";
			String[] tags = getResources().getStringArray(R.array.periods);
			XmlPage periods = new XmlPage(baseContext);
			String target_url = setPeriodsTargetUrl();
			String xml_periods;
			try {
				xml_periods = periods.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}
			XmlExtractor xml_extractor = new XmlExtractor(xml_periods);
			ArrayList<String> periods_list = xml_extractor
					.unitSeparator(PERIOD_ENDTAG);

			int[] year = { 0, 0 };
			ArrayList<HashMap<String, String>> single_period_list = new ArrayList<HashMap<String, String>>();

			for (int i = 0; i < periods_list.size(); i++) {
				XmlExtractor single_period = new XmlExtractor(
						periods_list.get(i));

				current_period = single_period.extractTagsFromDoc(tags);
				if (year[0] < Integer.parseInt(current_period.get("year")
						.substring(0, 4))) {
					year[0] = Integer.parseInt(current_period.get("year")
							.substring(0, 4));
					year[1] = i;
				}
				single_period_list.add(current_period);
			}
			current_period = single_period_list.get(year[1]);
			return null;

		}

		protected void onPostExecute(Void result) {
			dismissWindow();

			if (error != null) {
				launchErrorWindow(1);
				return;
			}

			Message msg = new Message();
			msg.obj = current_period;
			mHandler.sendMessage(msg);

		}

		private String setPeriodsTargetUrl() {
			final String XML_EXTENSION = ".xml";
			final String PERIODS = "periods";

			GlobalState gs = (GlobalState) getApplication();
			String base_url = gs.getTargetAddress();

			String target_url = base_url + PERIODS + XML_EXTENSION;

			return target_url;
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_degree, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void sendNewProposal(View v) {
		ArrayList<Integer> fields_with_no_content = new ArrayList<Integer>();
		fields_with_no_content = checkFieldsForBlanksAndNonNumbers();
		fields_with_no_content.addAll(checkSpinnersForNoneSelected());
		ArrayList<Integer> fields_with_no_number = new ArrayList<Integer>();
		fields_with_no_number = checkFieldsForNoNumbers();

		if (fields_with_no_content.size() != 0) {
			Toast.makeText(baseContext,
					getResources().getString(R.string.emptyFields),
					Toast.LENGTH_LONG).show();
			highlightFieldsWithErrors(fields_with_no_content);
		} else {
			if (fields_with_no_number.size() != 0) {
				Toast.makeText(baseContext,
						getResources().getString(R.string.noNumberFields),
						Toast.LENGTH_LONG).show();
				highlightFieldsWithErrors(fields_with_no_number);
			} else {
				checkId();

			}

		}
	}

	private ArrayList<Integer> checkFieldsForNoNumbers() {
		int[] numberFields = { R.id.degree_min_alumns, R.id.degree_max_alumns,
				R.id.degree_min_credits };

		ArrayList<Integer> fields_with_no_number = new ArrayList<Integer>();

		for (int i = 0; i < numberFields.length; i++) {
			EditText textBox = (EditText) findViewById(numberFields[i]);
			String content = textBox.getText().toString();
			if (!(TextUtils.isDigitsOnly(content))) {
				fields_with_no_number.add(numberFields[i]);
			}
		}

		return fields_with_no_number;
	}

	private void highlightFieldsWithErrors(ArrayList<Integer> fields_with_errors) {
		int[] spinners = { R.id.type_spinner, R.id.mode_spinner,
				R.id.oriented_spinner, R.id.knowledge_spinner };
		for (int i = 0; i < fields_with_errors.size(); i++) {
			if (fields_with_errors.get(i).equals(spinners[0])
					|| fields_with_errors.get(i).equals(spinners[1])
					|| fields_with_errors.get(i).equals(spinners[2])
					|| fields_with_errors.get(i).equals(spinners[3])) {
				Spinner field = (Spinner) findViewById(fields_with_errors
						.get(i));
				field.setBackgroundResource(R.drawable.grey_buttons_with_red_border);

			} else {

				EditText field = (EditText) findViewById(fields_with_errors
						.get(i));
				field.setBackgroundResource(R.drawable.red_border);
			}
		}
	}

	private ArrayList<Integer> checkSpinnersForNoneSelected() {
		ArrayList<Integer> spinners_with_errors = new ArrayList<Integer>();

		if (type.equals(getResources().getStringArray(R.array.degree_types)[0])) {
			spinners_with_errors.add(R.id.type_spinner);
		} else {
			Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
			spinner.setBackgroundResource(R.drawable.grey_buttons);
		}
		if (mode.equals(getResources().getStringArray(
				R.array.teachingModeComplete)[0])) {
			spinners_with_errors.add(R.id.mode_spinner);
		} else {
			Spinner spinner = (Spinner) findViewById(R.id.mode_spinner);
			spinner.setBackgroundResource(R.drawable.grey_buttons);
		}
		if (oriented
				.equals(getResources().getStringArray(R.array.orientedTo)[0])) {
			spinners_with_errors.add(R.id.oriented_spinner);
		} else {
			Spinner spinner = (Spinner) findViewById(R.id.oriented_spinner);
			spinner.setBackgroundResource(R.drawable.grey_buttons);
		}
		if (knowledge.equals(getResources().getStringArray(
				R.array.branchKnowledgeComplete)[0])) {
			spinners_with_errors.add(R.id.knowledge_spinner);
		} else {
			Spinner spinner = (Spinner) findViewById(R.id.knowledge_spinner);
			spinner.setBackgroundResource(R.drawable.grey_buttons);
		}

		return spinners_with_errors;

	}

	private ArrayList<Integer> checkFieldsForBlanksAndNonNumbers() {

		int[] textFields = { R.id.degree_denomination, R.id.degree_competences,
				R.id.degree_proposed_by, R.id.degree_units,
				R.id.degree_min_alumns, R.id.degree_max_alumns,
				R.id.degree_min_credits, R.id.degree_economic_manager,
				R.id.teaching_address };

		ArrayList<Integer> textFields_with_errors = new ArrayList<Integer>();

		for (int i = 0; i < textFields.length; i++) {
			EditText textBox = (EditText) findViewById(textFields[i]);
			String content = textBox.getText().toString();
			if (content.equals("")) {
				textFields_with_errors.add(textFields[i]);
			} else
				textBox.setBackgroundResource(R.drawable.blue_border);
		}
		return textFields_with_errors;
	}

	private void checkId() {
		EditText box_teacher_dni = (EditText) findViewById(R.id.degree_academic_director);
		String teacher_dni = box_teacher_dni.getText().toString();

		Pattern p2 = Pattern.compile("^\\d{8}[A-Za-z]$");
		Matcher m2 = p2.matcher(teacher_dni);

		if (m2.matches()) {
			String teacher_dni_verified = teacher_dni.substring(0,
					teacher_dni.length() - 1)
					+ teacher_dni.substring(teacher_dni.length() - 1,
							teacher_dni.length()).toUpperCase();
			
			EditText dni_box = (EditText) findViewById(R.id.degree_academic_director);
			dni_box.setText(teacher_dni_verified);
			new checkTeacherExists().execute(teacher_dni_verified);
		} else {
			box_teacher_dni.setBackgroundResource(R.drawable.red_border);
			Toast.makeText(baseContext,
					getResources().getString(R.string.teacherNotPresent),
					Toast.LENGTH_LONG).show();
		}
	}

	private class checkTeacherExists extends AsyncTask<String, Void, Void> {

		IOException error;
		String xml_teachers;

		@Override
		protected void onPreExecute() {
			setUpProgressDialog(R.string.checkingMessage);
		}

		@Override
		protected Void doInBackground(String... params) {
			XmlPage teacher = new XmlPage(baseContext);
			String target_url = setTeacherTargetUrl(params[0]);
			try {
				xml_teachers = teacher.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			dismissWindow();

			if (error != null) {
				launchErrorWindow(1);
				return;
			}
			if (xml_teachers.length() == 67) {
				Toast.makeText(baseContext,
						getResources().getString(R.string.teacherNotPresent),
						Toast.LENGTH_LONG).show();
			} else {
				if (xml_teachers.subSequence(40, 45).equals("ulpgc")) {
					Message msg = new Message();
					senderHandler.sendMessage(msg);
				} else
					Toast.makeText(
							baseContext,
							getResources()
									.getString(R.string.teacherNotPresent),
							Toast.LENGTH_LONG).show();

			}

		}
	}

	private String setTeacherTargetUrl(String dni) {
		final String PARAMS_PRE_DNI = "teachers.xml?utf8=✓&search[dni_like]=";
		final String PARAMS_POST_DNI = "&search[full_name_like]=&search[type_eq]=&search[aasm_state_eq]=&search[ulpgc_department_id_eq]=&commit=Buscar";
		GlobalState gs = (GlobalState) getApplication();
		String base_url = gs.getTargetAddress();

		return base_url + PARAMS_PRE_DNI + dni + PARAMS_POST_DNI;
	}

	private class sendVerifiedProposal extends AsyncTask<Void, Void, Void> {

		IOException error;
		int statusCode;

		@Override
		protected void onPreExecute() {
			setUpProgressDialog(R.string.sendingDegree);
		}

		@Override
		protected Void doInBackground(Void... params) {

			final String UNIV_SP_DEG = "university_specific_degrees";
			GlobalState gs = (GlobalState) ((Activity) baseContext)
					.getApplication();
			HttpClient client = gs.getHttpClient();
			HttpContext localContext = gs.getLocalContext();
			String target_address = gs.getTargetAddress() + UNIV_SP_DEG;

			HttpPost post = new HttpPost(target_address);
			List<NameValuePair> post_params = new ArrayList<NameValuePair>();

			post_params = setPostParams();

			UrlEncodedFormEntity ent = null;
			try {
				ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = null;
				responsePOST = client.execute(post, localContext);
				responsePOST.getEntity().consumeContent();
				statusCode = responsePOST.getStatusLine().getStatusCode();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				error = e;
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(Void result) {
			dismissWindow();
			if (error != null) {
				launchErrorWindow(1);
				return;
			}
			if (statusCode == 200) {
				Toast.makeText(baseContext,
						getResources().getString(R.string.proposalSent),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(baseContext,
						getResources().getString(R.string.unidentifiedError),
						Toast.LENGTH_LONG).show();
			}

		}

		private List<NameValuePair> setPostParams() {

			String[] string_params = { "name", "mode_id", "audience_id",
					"competences", "proposed_by", "manager_dni",
					"academic_management_units", "branch_of_knowledge_id",
					"min_attendees", "max_attendees", "period_id",
					"min_credits_per_academic_period",
					"outside_canary_islands", "institution_id",
					"teaching_place", "head_office", "administrator" };

			String[] PARAM_UTF = { "utf8", "✓" };
			String[] COMMIT = { "commit", "Guardar" };
			String[] EVAL_ACECAU = { "evaluated_by_acecau_at", "" };
			String UNIV_PREFIX = "university_specific_degree[";
			String UNIV_POST = "]";
			String PERIOD_ID = "period_id";
			String HEAD_OFFICE = "head_office";
			String INSTITUTION_ID = "institution_id";
			String OUTSIDE = "outside_canary_islands";
			String DEGREE_TYPE = "degree_type_id";
			String DEGREE_MODE = "mode_id";
			String DEGREE_AUDIENCE = "audience_id";
			String DEGREE_BRANCH = "branch_of_knowledge_id";

			int[] string_views = { R.id.degree_denomination,
					R.id.degree_competences, R.id.degree_proposed_by,
					R.id.degree_units, R.id.degree_min_alumns,
					R.id.degree_max_alumns, R.id.degree_min_credits,
					R.id.degree_economic_manager, R.id.teaching_address,
					R.id.degree_academic_director };

			String[] param_string_views = { "name", "competences",
					"proposed_by", "academic_management_units",
					"min_attendees", "max_attendees",
					"min_credits_per_academic_period", "administrator",
					"teaching_place", "manager_dni" };

			List<NameValuePair> post_params = new ArrayList<NameValuePair>();
			post_params.add(new BasicNameValuePair(PARAM_UTF[0], PARAM_UTF[1]));

			for (int i = 0; i < param_string_views.length; i++) {
				EditText content = (EditText) findViewById(string_views[i]);
				String param_name = UNIV_PREFIX + param_string_views[i]
						+ UNIV_POST;
				String string_content = content.getText().toString();
				post_params.add(new BasicNameValuePair(param_name,
						string_content));
			}
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + EVAL_ACECAU[0]
					+ UNIV_POST, EVAL_ACECAU[1]));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + PERIOD_ID
					+ UNIV_POST, period_id));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + INSTITUTION_ID
					+ UNIV_POST, getResources().getStringArray(
					R.array.institutions_id)[institution_index]));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + HEAD_OFFICE
					+ UNIV_POST, getResources().getStringArray(
					R.array.institutions_addresses)[institution_index]));

			if (outsideCanaryIslands) {
				post_params.add(new BasicNameValuePair(UNIV_PREFIX + OUTSIDE
						+ UNIV_POST, "1"));
			} else
				post_params.add(new BasicNameValuePair(UNIV_PREFIX + OUTSIDE
						+ UNIV_POST, "0"));

			post_params.add(new BasicNameValuePair(COMMIT[0], COMMIT[1]));

			post_params.add(new BasicNameValuePair(UNIV_PREFIX + DEGREE_TYPE
					+ UNIV_POST, Integer.toString(type_index)));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + DEGREE_MODE
					+ UNIV_POST, Integer.toString(mode_index)));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX
					+ DEGREE_AUDIENCE + UNIV_POST, Integer
					.toString(oriented_index)));
			post_params.add(new BasicNameValuePair(UNIV_PREFIX + DEGREE_BRANCH
					+ UNIV_POST, Integer.toString(knowledge_index)));

			return post_params;
		}
	}
}
