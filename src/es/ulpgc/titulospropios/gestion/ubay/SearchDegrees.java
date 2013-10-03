package es.ulpgc.titulospropios.gestion.ubay;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SearchDegrees extends MyFragmentActivity implements
		SearchDegreesFragment.OnShowResultsListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_degrees_activity);

		if (getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			BlankFragment initialFragment = new BlankFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, initialFragment).commit();

		}
	}

	public void onShowResults(Degree[] degrees) {
		if (findViewById(R.id.fragment_container) == null) {
			Intent intent = new Intent(this, SearchResults.class);
			String[] codes = new String[degrees.length];
			for (int i = 0; i < degrees.length; i++) {
				codes[i] = degrees[i].getData().get("code");
				DegreeInfoContainer degree_with_simple_data = new DegreeInfoContainer(
						degrees[i].getData());
				intent.putExtra(degrees[i].getData().get("code"),
						degree_with_simple_data.getArrayList());
			}
			intent.putExtra("codes", codes);
			startActivity(intent);
		} else {
			Bundle args = new Bundle();
			String[] codes = new String[degrees.length];

			for (int i = 0; i < degrees.length; i++) {
				codes[i] = degrees[i].getData().get("code");
				DegreeInfoContainer degree_with_simple_data = new DegreeInfoContainer(
						degrees[i].getData());
				args.putStringArrayList(degrees[i].getData().get("code"),
						degree_with_simple_data.getArrayList());
			}
			args.putStringArray("codes", codes);
			SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
			searchResultsFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_container, searchResultsFragment)
					.commit();
		}

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

}
