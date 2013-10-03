package es.ulpgc.titulospropios.gestion.ubay;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TabIncome extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		HashMap<String, String> info;

		if (getActivity()
				.getClass()
				.toString()
				.equals("class es.ulpgc.titulospropios.gestion.ubay.DegreeMainMenu")) {
			info = EconomicInfoFragment.hashMap;
		} else {
			info = EconomicInfo.hashMap;
		}
		ScrollView outer_layout = (ScrollView) inflater.inflate(
				R.layout.degree_income_tab, container, false);
		return populateViews(info, outer_layout);

	}

	@SuppressLint("CutPasteId")
	private View populateViews(HashMap<String, String> info,
			ScrollView outer_layout) {
		int[] views = { R.id.first_row, R.id.second_row, R.id.students_sum,
				R.id.direct_income_row };

		String[] tags = getResources().getStringArray(R.array.incomes);
		String[] etiquetas = getResources().getStringArray(R.array.incomeTags);

		LinearLayout inner_layout = null;
		float student_incomes = 0;
		for (int i = 0; i <= 1; i++) {
			inner_layout = (LinearLayout) outer_layout.findViewById(views[i]);
			TextView left_item = (TextView) inner_layout
					.findViewById(R.id.left_item);
			TextView center = (TextView) inner_layout.findViewById(R.id.center);
			TextView right_item = (TextView) inner_layout
					.findViewById(R.id.right_item);

			left_item.setText(Html.fromHtml(etiquetas[i * 2]
					+ info.get(tags[i * 2])));
			center.setText(Html.fromHtml(etiquetas[i * 2 + 1]
					+ info.get(tags[i * 2 + 1])));
			float income = (Float.parseFloat(info.get(tags[i * 2])) * Float
					.parseFloat(info.get(tags[i * 2 + 1])));
			student_incomes += income;
			left_item.setGravity(Gravity.CENTER);
			center.setGravity(Gravity.CENTER);
			right_item.setText(Html.fromHtml(etiquetas[4]
					+ Float.toString(income) + " €"));
			right_item.setGravity(Gravity.RIGHT);
		}
		inner_layout = (LinearLayout) outer_layout
				.findViewById(R.id.students_sum);
		TextView student_sum_tag = (TextView) inner_layout
				.findViewById(R.id.left_item);
		TextView student_sum = (TextView) inner_layout
				.findViewById(R.id.right_item);
		student_sum_tag.setText(R.string.student_sum);
		student_sum_tag.setGravity(Gravity.CENTER);
		student_sum_tag.setTypeface(null, Typeface.BOLD);
		student_sum.setText(Float.toString(student_incomes) + " €");
		student_sum.setGravity(Gravity.RIGHT);

		inner_layout = (LinearLayout) outer_layout
				.findViewById(R.id.direct_income_row);
		TextView external_income = (TextView) inner_layout
				.findViewById(R.id.left_item);
		TextView external_income_sum = (TextView) inner_layout
				.findViewById(R.id.right_item);

		external_income.setText(R.string.external_incomes);
		external_income.setGravity(Gravity.CENTER);
		external_income.setTypeface(null, Typeface.BOLD);

		external_income_sum.setText(info.get(tags[4]) + " €");
		external_income_sum.setGravity(Gravity.RIGHT);

		return outer_layout;
	}
}
