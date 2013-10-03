package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SearchResultsFragment extends Fragment {
	private View myFragmentView;
	ArrayList<ArrayList<String>> degree_list;
	ArrayList<ArrayList<String>> fetch;

	String[] degree_codes;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		if (bundle == null) {
			bundle = getActivity().getIntent().getExtras();
		}
		myFragmentView = inflater.inflate(R.layout.search_results_fragment,
				container, false);

		populateDegreeListFromInput(bundle);

		populateListAdapter();

		initializeListView();

		return myFragmentView;

	}

	private void initializeListView() {

		ListView listview = (ListView) myFragmentView.findViewById(R.id.list);
		CustomDegreeAdapter custom_adapter = new CustomDegreeAdapter(
				getActivity(), R.id.list, fetch);
		listview.setAdapter(custom_adapter);
	}

	private void populateListAdapter() {
		fetch = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < degree_codes.length; i++) {

			fetch.add(degree_list.get(i));
		}
	}

	private void populateDegreeListFromInput(Bundle bundle) {

		degree_codes = bundle.getStringArray("codes");
		degree_list = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < degree_codes.length; i++) {
			degree_list.add((ArrayList<String>) bundle
					.getStringArrayList(degree_codes[i]));
		}
	}
}
