package es.ulpgc.titulospropios.gestion.ubay;

import java.util.HashMap;

import android.graphics.Color;
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

public class TabOverview extends Fragment {
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	public static final String EURO = "€";

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
				R.layout.degree_overview_tab, container, false);

		return populateViews(info, outer_layout);

	}

	private View populateViews(HashMap<String, String> info,
			ScrollView outer_layout) {
		int[] views = { R.id.incomes_total, R.id.parcial_cost,
				R.id.ulpgc_compensation, R.id.institute_compensation,
				R.id.manager_compensation, R.id.total, R.id.balance };
		String[] tags = getResources().getStringArray(R.array.overview);
		String[] etiquetas = getResources().getStringArray(
				R.array.overviewExpenses);
		
		LinearLayout inner_layout = null;
		for (int i = 0; i < tags.length - 1; i++) {
			inner_layout = (LinearLayout) outer_layout.findViewById(views[i]);
			TextView left_item = (TextView) inner_layout
					.findViewById(R.id.left_item);
			TextView right_item = (TextView) inner_layout
					.findViewById(R.id.right_item);
			left_item.setText(Html.fromHtml(etiquetas[i]));
			if (info.get(tags[i]) == null) {
				right_item.setText("0");
			} else {
				right_item.setText(info.get(tags[i]) + "€");
				right_item.setGravity(Gravity.RIGHT);
			}

		}
		LinearLayout layout_balance = (LinearLayout) outer_layout
				.findViewById(R.id.balance);
		TextView balance_tag = (TextView) layout_balance
				.findViewById(R.id.left_item);
		balance_tag.setText(R.string.balance);
		balance_tag.setGravity(Gravity.CENTER);
		balance_tag.setTypeface(null, Typeface.BOLD);
		TextView balance_sum = (TextView) layout_balance
				.findViewById(R.id.right_item);
		balance_sum.setText(info.get("sum") + EURO);
		balance_sum.setGravity(Gravity.RIGHT);

		if (Float.parseFloat(info.get("sum")) >= 0) {
			balance_sum.setBackgroundColor(Color.GREEN);
		} else {
			balance_sum.setBackgroundColor(Color.RED);
		}
		return outer_layout;
	}
}
