package es.ulpgc.titulospropios.gestion.ubay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class DegreeMainMenu extends MyFragmentActivity implements
		DegreeMainMenuFragment.OnAcademicInfoClickedListener,
		DegreeMainMenuFragment.OnEconomicInfoClickedListener,
		DegreeMainMenuFragment.OnReportClickedListener,
		DegreeMainMenuFragment.OnSubjectsClickedListener,
		DegreeMainMenuFragment.OnTeachersClickedListener {

	private Context baseContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.degree_main_menu_activity);

		if (getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		getMenuInflater().inflate(R.menu.degree_main_menu, menu);
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

	public void academicInfo(View view) {
		OnAcademicInfoClicked();
	}

	public void subjectsInfo(View view) {
		OnSubjectsClicked();
	}

	public void economicInfo(View view) {
		OnEconomicInfoClicked();
	}

	public void teachersInfo(View view) {
		OnTeachersClicked();
	}

	public void reportInfo(View view) {
		OnReportClicked();
	}

	public void sendDegreeToEvaluate(View view) {
		if (findViewById(R.id.central_fragment) == null) {
			Intent intent = prepareIntent();
			intent.putExtra("edition", getIntent().getStringExtra("edition"));
			intent.putExtra("state", getIntent().getStringExtra("state"));
			intent.setClass(this, Periods.class);
			startActivity(intent);
		} else {
			hideUnwantedFragmentLayouts();
			unfoldFullScreenFragmentLayout();

			Bundle args = getIntent().getExtras();
			PeriodsFragment periodsFragment = new PeriodsFragment();
			periodsFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.full_screen_fragment, periodsFragment)
					.commit();
		}
	}

	@Override
	public void OnAcademicInfoClicked() {
		if (findViewById(R.id.central_fragment) == null) {

			Intent intent = prepareIntent();
			intent.setClass(this, AcademicInfo.class);
			startActivity(intent);
		} else {
			hideUnwantedFragmentLayouts();
			unfoldFullScreenFragmentLayout();
			Bundle args = getIntent().getExtras();
			AcademicInfoFragment academicInfoFragment = new AcademicInfoFragment();
			academicInfoFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.full_screen_fragment, academicInfoFragment)
					.commit();
		}

	}

	@Override
	public void OnSubjectsClicked() {
		if (findViewById(R.id.central_fragment) == null) {

			Intent intent = prepareIntent();
			intent.setClass(this, SubjectsList.class);
			startActivity(intent);
		} else {
			hideFullScreenLayout();
			unfoldSmallerFrameLayouts();

			Bundle args = getIntent().getExtras();
			SubjectsListFragment subjectsListFragment = new SubjectsListFragment();
			subjectsListFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.central_fragment, subjectsListFragment)
					.commit();
		}
	}

	@Override
	public void OnTeachersClicked() {
		if (findViewById(R.id.central_fragment) == null) {

			Intent intent = prepareIntent();
			intent.setClass(this, TeachersList.class);
			startActivity(intent);
		} else {
			hideFullScreenLayout();
			unfoldSmallerFrameLayouts();

			Bundle args = getIntent().getExtras();
			TeachersListFragment teachersListFragment = new TeachersListFragment();
			teachersListFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.central_fragment, teachersListFragment)
					.commit();
		}
	}

	@Override
	public void OnEconomicInfoClicked() {
		if (findViewById(R.id.central_fragment) == null) {

			Intent intent = prepareIntent();
			intent.setClass(this, EconomicInfo.class);
			startActivity(intent);
		} else {
			hideFullScreenLayout();
			unfoldSmallerFrameLayouts();

			Bundle args = getIntent().getExtras();
			EconomicInfoFragment economicInfoFragment = new EconomicInfoFragment();
			economicInfoFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.central_fragment, economicInfoFragment)
					.commit();
		}
	}

	@Override
	public void OnReportClicked() {
		if (findViewById(R.id.central_fragment) == null) {

			Intent intent = prepareIntent();
			intent.setClass(this, ReportsInfo.class);
			startActivity(intent);
		} else {
			hideUnwantedFragmentLayouts();
			unfoldFullScreenFragmentLayout();

			Bundle args = getIntent().getExtras();
			ReportsInfoFragment reportsInfoFragment = new ReportsInfoFragment();
			reportsInfoFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.full_screen_fragment, reportsInfoFragment)
					.commit();
		}
	}

	public void downloadDegree(View view) {
		String pdf_url = getPDF_URL();

		new downloadPdf().execute(pdf_url);

	}

	public void overview(View view) {
		hideFullScreenLayout();
		unfoldSmallerFrameLayouts();

		Bundle args = getIntent().getExtras();
		TabOverview economicInfoFragment = new TabOverview();
		economicInfoFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lateral_fragment, economicInfoFragment).commit();
	}

	public void income(View view) {
		hideFullScreenLayout();
		unfoldSmallerFrameLayouts();

		Bundle args = getIntent().getExtras();
		TabIncome tabIncomeFragment = new TabIncome();
		tabIncomeFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lateral_fragment, tabIncomeFragment).commit();
	}

	public void expenditure(View view) {
		hideFullScreenLayout();
		unfoldSmallerFrameLayouts();

		Bundle args = getIntent().getExtras();
		TabExpenditure tabExpenditureFragment = new TabExpenditure();
		tabExpenditureFragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.lateral_fragment, tabExpenditureFragment)
				.commit();
	}

	private void unfoldFullScreenFragmentLayout() {
		FrameLayout fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_fragment);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 2);

		fullScreenLayout.setLayoutParams(lp);

	}

	private void hideUnwantedFragmentLayouts() {
		FrameLayout centralLayout = (FrameLayout) findViewById(R.id.central_fragment);
		FrameLayout lateralLayout = (FrameLayout) findViewById(R.id.lateral_fragment);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 0);
		centralLayout.setLayoutParams(lp);
		lateralLayout.setLayoutParams(lp);
	}

	private void unfoldSmallerFrameLayouts() {
		FrameLayout centralLayout = (FrameLayout) findViewById(R.id.central_fragment);
		FrameLayout lateralLayout = (FrameLayout) findViewById(R.id.lateral_fragment);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1);
		centralLayout.setLayoutParams(lp);
		lateralLayout.setLayoutParams(lp);
	}

	private void hideFullScreenLayout() {
		FrameLayout fullScreenLayout = (FrameLayout) findViewById(R.id.full_screen_fragment);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 0);
		fullScreenLayout.setLayoutParams(lp);
	}

	private Intent prepareIntent() {
		Bundle bundle = getIntent().getExtras();
		Intent intent = new Intent();
		intent.putExtra("id", bundle.getString("id"));
		return intent;
	}

	private String getPDF_URL() {
		final String UNIV_SPEC_DEG = "university_specific_degrees";
		final String PDF_EXTENSION = ".pdf";
		final String SLASH = "/";

		GlobalState gs = (GlobalState) getApplication();
		String base_url = gs.getTargetAddress();
		String degree_id = getIntent().getExtras().getString("id");

		String target_url = base_url + UNIV_SPEC_DEG + SLASH + degree_id
				+ PDF_EXTENSION;
		return target_url;
	}

	private class downloadPdf extends AsyncTask<String, Integer, Integer> {

		IOException error;
		File file;

		@Override
		protected void onPreExecute() {
			((MyFragmentActivity) baseContext)
					.setUpProgressDialog(R.string.downloading);
		}

		@Override
		protected Integer doInBackground(String... params) {
			GlobalState gs = (GlobalState) baseContext.getApplicationContext();
			HttpContext localContext = gs.getLocalContext();
			DefaultHttpClient client = gs.getHttpClient();

			HttpGet httpGet = new HttpGet(params[0]);

			HttpResponse responseGET = null;
			try {
				responseGET = client.execute(httpGet, localContext);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				error = e;
				e.printStackTrace();
				return null;
			}
			HttpEntity getEntity = responseGET.getEntity();
			File path;

			if (getEntity != null) {
				try {
					InputStream in = getEntity.getContent();
					if (!Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// SD card is not available
						return null;
					}
					path = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
					path.mkdirs();
					String name_id_degree = getIntent().getExtras().getString(
							"id");
					file = new File(path, name_id_degree + ".pdf");
					FileOutputStream fos = new FileOutputStream(file);

					byte[] buffer = new byte[1024];
					int len1 = 0;
					int cont = 0;
					while ((len1 = in.read(buffer)) > 0) {
						fos.write(buffer, 0, len1);
						cont += len1;
						publishProgress(cont);
					}
					fos.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					error = ex;
					return null;

				} catch (RuntimeException ex) {
					httpGet.abort();
					throw ex;
				}
			}
			return null;

		}

		protected void onProgressUpdate(Integer... values) {
			updateProgressDialog(baseContext.getResources().getString(
					R.string.downloading)
					+ "<br>"
					+ values[0]
					+ " "
					+ baseContext.getResources().getString(R.string.downloaded));
		}

		@SuppressLint("NewApi")
		protected void onPostExecute(Integer result) {
			((MyFragmentActivity) baseContext).dismissWindow();

			if (error != null) {
				((MyFragmentActivity) baseContext).launchErrorWindow(1);
				return;
			}

			if (file != null) {
				if (file.exists()) {
					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(
								baseContext,
								getResources().getString(
										R.string.noAppAvailable),
								Toast.LENGTH_LONG).show();
					}
				}
			} else
				Toast.makeText(baseContext,
						getResources().getString(R.string.noStorageAvailable),
						Toast.LENGTH_LONG).show();

			if (((int) Build.VERSION.SDK_INT) >= 12) {
				DownloadManager mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
				mgr.addCompletedDownload(getIntent().getExtras().getString(
							"id"), "Memoria propuesta título propio", false, "application/pdf", Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+getIntent().getExtras().getString(
									"id") + ".pdf", file.length(), true);
			}
			return;
		}
	}

}
