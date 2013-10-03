package es.ulpgc.titulospropios.gestion.ubay;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleTeacherFragment extends Fragment {
	private View myView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myView = inflater.inflate(R.layout.single_teacher_fragment, container,
				false);
		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = getActivity().getIntent().getExtras();
		}

		String teacher_data = bundle.getString("teacher-info");
		XmlExtractor xml_extractor = new XmlExtractor(teacher_data);
		String[] tags = getResources().getStringArray(
				R.array.generic_teachers_info);
		HashMap<String, String> teacher_info = xml_extractor
				.extractTagsFromDoc(tags);

		getActivity().setTitle(teacher_info.get("name")+ " " + teacher_info.get("last-name"));
		populateViews(teacher_info);

		return myView;
	}

	private void populateViews(HashMap<String, String> teacher_info) {
		setValuesToViews(sanitizeInput(teacher_info));
	}

	private void setValuesToViews(HashMap<String, String> clean_teacher) {
		LinearLayout first_row = (LinearLayout) myView
				.findViewById(R.id.first_row);
		LinearLayout second_row = (LinearLayout) myView
				.findViewById(R.id.second_row);
		LinearLayout third_row = (LinearLayout) myView
				.findViewById(R.id.third_row);
		LinearLayout fourth_row = (LinearLayout) myView
				.findViewById(R.id.fourth_row);

		TextView[] text_views = new TextView[9];

		text_views[0] = (TextView) first_row.findViewById(R.id.left_item); // degree
		text_views[1] = (TextView) first_row.findViewById(R.id.right_item); // email

		text_views[2] = (TextView) second_row.findViewById(R.id.left_item); // branch
		text_views[3] = (TextView) second_row.findViewById(R.id.right_item); // category

		text_views[4] = (TextView) third_row.findViewById(R.id.left_item); // ulpgc_graduate
		text_views[5] = (TextView) third_row.findViewById(R.id.right_item); // department

		text_views[6] = (TextView) fourth_row.findViewById(R.id.left_item); // entity
		text_views[7] = (TextView) fourth_row.findViewById(R.id.right_item); // active

		text_views[8] = (TextView) myView.findViewById(R.id.cv_textview); // cv_abstract

		TextView name = (TextView) myView.findViewById(R.id.teacher_name);
		String[] tags_asIndex = getResources().getStringArray(
				R.array.info_generica_profesores);
		String[] locale_tags = getResources().getStringArray(R.array.generic_teachers_info);
		name.setText(clean_teacher.get("name") + " " + clean_teacher.get("last-name"));
		
		for (int i = 2; i < clean_teacher.size() - 2; i++) {
			text_views[i - 2].setText(Html
					.fromHtml(tags_asIndex[i - 2]
							+ clean_teacher.get(locale_tags[i])));
		}		
		
	}

	private HashMap<String, String> sanitizeInput(
			HashMap<String, String> teacher_info) {
		if (teacher_info.get("graduate-in-ulpgc") == "false") {
			teacher_info.put("graduate-in-ulpgc", "No");
		} else {
			teacher_info.put("graduate-in-ulpgc",
					getResources().getString(R.string.yes));
		}
		if (teacher_info.get("department") == null) {
			teacher_info.put("department",
					getResources().getString(R.string.notUlpgcTeacher));
		}
		if (teacher_info.get("cv-abstract") == null) {
			teacher_info.put("cv-abstract",
					getResources().getString(R.string.notPresent));
		}
		if (teacher_info.get("active") == null) {
			teacher_info.put("active", "No");
		} else {
			teacher_info.put("active", getResources().getString(R.string.yes));
		}
		if (teacher_info.get("institute") == null) {
			teacher_info.put("institute",
					getResources().getString(R.string.notPresent));
		}
		return teacher_info;
	}

}