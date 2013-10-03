package es.ulpgc.titulospropios.gestion.ubay;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TabExpenditure extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	public static final String ANECA_COST = "550.0";
	public static final float INSURANCE_PER_STUDENT = 15;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		ScrollView outer_layout = (ScrollView) inflater.inflate(
				R.layout.degree_expenditures_tab, container, false);

		HashMap<String, String> info;

		if (getActivity()
				.getClass()
				.toString()
				.equals("class es.ulpgc.titulospropios.gestion.ubay.DegreeMainMenu")) {
			info = EconomicInfoFragment.hashMap;
		} else {
			info = EconomicInfo.hashMap;
		}

		return populateViews(info, outer_layout);

	}

	private View populateViews(HashMap<String, String> info,
			ScrollView outer_layout) {
		final int[] text_views = { R.id.teory_ulpgc, R.id.practice_ulpgc,
				R.id.seminars_ulpgc, R.id.exams_ulpgc, R.id.content_ulpgc,
				R.id.tutorship_ulpgc, R.id.final_project_ulpgc,
				R.id.exam_coordinator_ulpgc, R.id.directors, R.id.cost_sum,
				R.id.unit_profit, R.id.cost_total, R.id.teory, R.id.practice,
				R.id.seminars, R.id.exams, R.id.content, R.id.tutorship,
				R.id.final_project, R.id.exam_coordinator,
				R.id.others_external, R.id.external_cost_total, R.id.staff,
				R.id.scholarship_students, R.id.scholarship_specific,
				R.id.other_staff, R.id.presentation_report, R.id.final_report,
				R.id.information_point, R.id.support, R.id.other_support,
				R.id.travel_cost, R.id.hosting_cost, R.id.food_cost,
				R.id.scholarship_grant, R.id.other_travelcost,
				R.id.travel_total, R.id.office_material, R.id.lab_material,
				R.id.non_inventary, R.id.reprography, R.id.other_material,
				R.id.fungible_total, R.id.bibliography_cost,
				R.id.other_bibliography, R.id.bibliography_total,
				R.id.print_ads, R.id.press_ads, R.id.ads_total,
				R.id.inauguration, R.id.translation, R.id.certs,
				R.id.virtual_campus, R.id.evaluation, R.id.insurance,
				R.id.other_other, R.id.others_total, R.id.total_expenses };
		final String EURO = " €";

		String[] tags = getResources().getStringArray(R.array.economic_tags);
		String[] etiquetas = getResources()
				.getStringArray(R.array.expensesTags);

		for (int i = 0; i < etiquetas.length; i++) {
			Log.d("Etiqueta: " + etiquetas[i], "Tag: " + tags[i]);
		}
		int count = 0;
		for (int i = 0; i < 58; i++) {
			LinearLayout inner_layout = (LinearLayout) outer_layout
					.findViewById(text_views[i]);

			if (inner_layout.getChildCount() == 3) {
				TextView left_view = (TextView) inner_layout.getChildAt(0);
				TextView right_view = (TextView) inner_layout.getChildAt(2);

				if (info.get(tags[count]) == null) {
					left_view.setText(Html.fromHtml(etiquetas[count]));
				} else {
					left_view.setText(Html.fromHtml(etiquetas[count]
							+ info.get(tags[count])));
				}
				if (info.get(tags[count + 1]) == null) {
					right_view.setText(Html.fromHtml(etiquetas[count + 1]));
				} else {
					right_view.setText(Html.fromHtml(etiquetas[count + 1]
							+ info.get(tags[count + 1]) + EURO));
				}
				right_view.setGravity(Gravity.RIGHT);
				count += 2;
			} else if (inner_layout.getChildCount() == 5) {
				TextView left_view = (TextView) inner_layout.getChildAt(0);
				TextView center = (TextView) inner_layout.getChildAt(2);
				TextView right_view = (TextView) inner_layout.getChildAt(4);
				if (info.get(tags[count]) == null) {
					left_view.setText(Html.fromHtml(etiquetas[count]));
				} else {
					left_view.setText(Html.fromHtml(etiquetas[count]
							+ info.get(tags[count])));
				}
				if (info.get(tags[count + 1]) == null) {
					center.setText(Html.fromHtml(etiquetas[count + 1]));
				} else {
					center.setText(Html.fromHtml(etiquetas[count + 1]
							+ info.get(tags[count + 1]) + EURO));
				}
				if (info.get(tags[count + 2]) == null) {
					right_view.setText(Html.fromHtml(etiquetas[count + 2]));
				} else {
					right_view.setText(Html.fromHtml(etiquetas[count + 2]
							+ info.get(tags[count + 2]) + EURO));
				}
				left_view.setGravity(Gravity.CENTER);
				center.setGravity(Gravity.CENTER);
				right_view.setGravity(Gravity.RIGHT);
				count += 3;
			}
		}
		LinearLayout aneca_cost = (LinearLayout) outer_layout
				.findViewById(text_views[52]);
		TextView aneca_view = (TextView) aneca_cost.getChildAt(2);
		aneca_view.setText(Html.fromHtml(etiquetas[25] + ANECA_COST + EURO));
		aneca_view.setGravity(Gravity.RIGHT);

		LinearLayout insurance_cost = (LinearLayout) outer_layout
				.findViewById(text_views[53]);
		TextView insurance_view = (TextView) insurance_cost.getChildAt(2);

		float insurance_amount = (Float.parseFloat(info
				.get("granted-students-qty")) + Float.parseFloat(info
				.get("regular-students-qty")))
				* INSURANCE_PER_STUDENT;

		insurance_view.setText(Html.fromHtml(etiquetas[25]
				+ String.valueOf(insurance_amount) + EURO));
		insurance_view.setGravity(Gravity.RIGHT);

		return outer_layout;
	}
}
