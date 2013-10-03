package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PeriodsFragment extends Fragment implements OnClickListener {
	private HashMap<String, String> current_period;
	private String current_period_timeframe;
	private Boolean time_sendable = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(R.layout.periods_fragment,
				container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		new downloadPeriods().execute();

		Button send_button = (Button) myFragmentView
				.findViewById(R.id.send_button);
		send_button.setOnClickListener(this);

		return myFragmentView;
	}

	private class downloadPeriods extends AsyncTask<Void, Void, Void> {

		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(Void... params) {
			final String PERIOD_ENDTAG = "</period>";
			String[] tags = getResources().getStringArray(R.array.periods);
			XmlPage periods = new XmlPage(getActivity());
			String target_url = setTargetUrl();
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
			((MyFragmentActivity) getActivity()).dismissWindow();

			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(1);
				return;
			}
			populateViews();
		}

		@SuppressLint("SimpleDateFormat")
		private void populateViews() {

			int[] rows = { R.id.second_row, R.id.third_row, R.id.fourth_row };
			String[] tags_order = { "request-from-date", "request-to-date",
					"first-rectification-from-date",
					"first-rectification-to-date",
					"second-rectification-from-date",
					"second-rectification-to-date" };
			String[] locale_tags = {
					getResources().getString(R.string.period_start),
					getResources().getString(R.string.period_end) };

			Date cDate = new Date();
			String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

			LinearLayout lView = (LinearLayout) getActivity().findViewById(
					R.id.first_row);
			TextView year = (TextView) lView.findViewById(R.id.item);
			current_period_timeframe = current_period.get("year");
			year.setText(current_period_timeframe);

			int tag_counter = 0;

			for (int i = 0; i < rows.length; i++) {
				lView = (LinearLayout) getActivity().findViewById(rows[i]);
				TextView left_item = (TextView) lView
						.findViewById(R.id.left_item);
				TextView right_item = (TextView) lView
						.findViewById(R.id.right_item);

				left_item
						.setText(Html.fromHtml("<b>" + locale_tags[0] + "</b>"
								+ "<br>"
								+ current_period.get(tags_order[tag_counter])));
				right_item.setText(Html.fromHtml("<b>" + locale_tags[1]
						+ "</b>" + "<br>"
						+ current_period.get(tags_order[tag_counter + 1])));
				left_item.setGravity(Gravity.CENTER);
				right_item.setGravity(Gravity.CENTER);

				if (fDate
						.compareTo(current_period.get(tags_order[tag_counter])) >= 0
						&& fDate.compareTo(current_period
								.get(tags_order[tag_counter + 1])) <= 0) {
					lView.setBackgroundColor(Color.GREEN);
					time_sendable = true;
				} else {
					lView.setBackgroundColor(Color.RED);
				}
				tag_counter = tag_counter + 2;

			}

		}

		private String setTargetUrl() {
			final String XML_EXTENSION = ".xml";
			final String PERIODS = "periods";

			GlobalState gs = (GlobalState) getActivity().getApplication();
			String base_url = gs.getTargetAddress();

			String target_url = base_url + PERIODS + XML_EXTENSION;

			return target_url;
		}
	}

	@Override
	public void onClick(View v) {

		Boolean state_sendable = determineState();
		String academic_year = determineAcademicYear();
		String reason = null;
		if (time_sendable && state_sendable) {
			if ((current_period_timeframe.equals(academic_year))) {
				new sendDegreeToEvaluate().execute();
			} else {
				reason = getResources().getString(R.string.outOfPeriod);
				Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();

			}

		} else {

			if (!time_sendable) {
				reason = getResources().getString(R.string.outOfTime);
			}
			if (!state_sendable) {
				if (reason != null) {
					reason = reason + " "
							+ getResources().getString(R.string.incorrectState);
				} else
					reason = getResources().getString(R.string.incorrectState);

			}
			Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
		}
	}

	private Boolean determineState() {
		Bundle bundle = getArguments();
		String state;
		Boolean state_sendable = false;
		if (bundle != null) {
			state = bundle.getString("state");
		} else {
			state = getActivity().getIntent().getStringExtra("state");
		}
		if (state.equals("enviado") || state.equals("cancelado")
				|| state.equals("rechazado")) {
			state_sendable = false;
		} else {
			state_sendable = true;
		}
		return state_sendable;
	}

	private class sendDegreeToEvaluate extends AsyncTask<Void, Void, Void> {
		IOException error;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) getActivity())
					.setUpProgressDialog(R.string.sendingDegree);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String target_url = setTargetUrl();
			XmlPage xml_page = new XmlPage(getActivity());
			try {
				xml_page.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void param) {

			((MyFragmentActivity) getActivity()).dismissWindow();
			if (error != null) {
				((MyFragmentActivity) getActivity()).launchErrorWindow(0);
			} else {
				Toast.makeText(getActivity(),
						getResources().getString(R.string.sent),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private String setTargetUrl() {
		final String UNIV_SPEC_DEG = "university_specific_degrees";
		final String CHECK = "check";
		final String SLASH = "/";

		GlobalState gs = (GlobalState) getActivity().getApplication();
		String base_url = gs.getTargetAddress();
		Bundle bundle = getArguments();
		String id;

		if (bundle != null) {
			id = bundle.getString("id");
		} else {
			id = getActivity().getIntent().getStringExtra("id");
		}

		String target_url = base_url + UNIV_SPEC_DEG + id + SLASH + CHECK;

		return target_url;
	}

	private String determineAcademicYear() {
		String edition;
		Bundle bundle = getArguments();
		if (bundle != null) {
			edition = bundle.getString("edition");
		} else {
			edition = getActivity().getIntent().getStringExtra("edition");
		}

		String year = edition.substring(0, 4);
		String month = edition.substring(5, 7);
		String academic_year;
		if (Integer.parseInt(month) > 8) {
			int academic_year_end = (Integer.parseInt(year) + 1);
			academic_year = year + "/" + String.valueOf(academic_year_end);
		} else {
			int academic_year_start = (Integer.parseInt(year) - 1);
			academic_year = academic_year_start + "/" + year;
		}

		return academic_year;
	}
}
