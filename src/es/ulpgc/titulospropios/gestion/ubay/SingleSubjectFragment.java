package es.ulpgc.titulospropios.gestion.ubay;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SingleSubjectFragment extends Fragment {
	private static final int[] textview_list = { R.id.name, R.id.branch,
			R.id.teaching_mode, R.id.state, R.id.edition, R.id.start_date,
			R.id.end_date, R.id.startDate, R.id.endDate, R.id.units,
			R.id.teachingPlace, R.id.class_hours,
			R.id.institutionRepresentative, R.id.misc_activities_hours,
			R.id.headquarters, R.id.exams_hours, R.id.final_work_hours,
			R.id.studying_hours, R.id.hours_sum, R.id.competences,
			R.id.activities, R.id.description, R.id.assesment_system };
	View myView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myView = inflater.inflate(R.layout.single_subject_fragment,
				container, false);

		Bundle bundle = getArguments();
		String subject_info;

		if (bundle != null) {
			subject_info = bundle.getString("subject-info");
		} else {
			subject_info = getActivity().getIntent().getStringExtra(
					"subject-info");
		}
		String[] tags = getActivity().getResources().getStringArray(
				R.array.complete_subject_info);
		XmlExtractor xml_extractor = new XmlExtractor(subject_info);
		HashMap<String, String> subject_data = xml_extractor
				.extractTagsFromDoc(tags);

		getActivity().setTitle(subject_data.get("name"));
		populateViews(subject_data);

		return myView;
	}

	private void populateViews(HashMap<String, String> subject_data) {
		String[] tags = getResources().getStringArray(
				R.array.completeSubjectTags);
		String[] tags_inOrder = getResources().getStringArray(
				R.array.complete_subject_info);

		getActivity().setTitle(subject_data.get("name"));

		for (int i = 0; i < tags_inOrder.length; i++) {
			if (subject_data.get(tags_inOrder[i]) == null) {
				subject_data.put(tags_inOrder[i], "");
			}
		}

		String kind = determineKind(Integer.parseInt(subject_data
				.get(tags_inOrder[3])));
		subject_data.put(tags_inOrder[3], kind);

		String branch = determineBranch(Integer.parseInt(subject_data
				.get(tags_inOrder[7])));
		subject_data.put(tags_inOrder[7], branch);

		String mode = determineMode(Integer.parseInt(subject_data
				.get(tags_inOrder[8])));
		subject_data.put(tags_inOrder[8], mode);

		for (int i = 0; i < 9; i++) {
			TextView text_holder = (TextView) myView.findViewById(
					textview_list[i]);
			text_holder.setText(Html.fromHtml("<b>" + tags[i] + ": " + "</b>"
					+ subject_data.get(tags_inOrder[i])));
		}
		for (int i = 9; i < 11; i++) {
			TextView text_holder = (TextView) myView.findViewById(
					textview_list[i]);
			text_holder.setText(Html.fromHtml("<b>"
					+ tags[i]
					+ ": "
					+ "</b><br>"
					+ subject_data.get(tags_inOrder[i]).replace("Z", "")
							.replace("T", " ")));
		}
		for (int i = 11; i < 19; i++) {
			TextView text_holder = (TextView) myView.findViewById(
					textview_list[i]);
			text_holder.setText(Html.fromHtml("<b>" + tags[i] + ": " + "</b>"
					+ subject_data.get(tags_inOrder[i]) + " "
					+ getResources().getString(R.string.hours)));
		}
		for (int i = 19; i < tags.length-1; i++) {
			TextView text_holder = (TextView) myView.findViewById(
					textview_list[i]);
			text_holder.setText(Html.fromHtml("<b>" + tags[i] + ": " + "</b>"
					+ subject_data.get(tags_inOrder[i])));
		}

		return;
	}

	private String determineMode(int value) {
		String[] mode_tags = getResources()
				.getStringArray(R.array.teachingMode);
		return mode_tags[value - 1];
	}

	private String determineBranch(int value) {
		String[] branch_tags = getResources().getStringArray(
				R.array.branchKnowledge);
		return branch_tags[value - 1];
	}

	private String determineKind(int value) {
		String[] subject_tags = getResources().getStringArray(
				R.array.subject_types);
		return subject_tags[value - 1];

	}

}
