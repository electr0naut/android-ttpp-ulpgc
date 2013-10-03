package es.ulpgc.titulospropios.gestion.ubay;

import es.ulpgc.titulospropios.gestion.ubay.R;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class EconomicInfo extends MyFragmentActivity implements
		TabHost.OnTabChangeListener {
	@SuppressWarnings("rawtypes")
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	public static HashMap<String, String> hashMap;
	private TabHost mTabHost;
	private Bundle bundle;
	private Context baseContext = this;

	private class TabInfo {
		private String tag;
		@SuppressWarnings("rawtypes")
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, @SuppressWarnings("rawtypes") Class clazz,
				Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		super.onCreate(savedInstanceState);
		setContentView(R.layout.economic_info_activity);
		bundle = savedInstanceState;
		new retrieveInfo().execute();

	}

	private class retrieveInfo extends AsyncTask<String, Void, Void> {
		IOException error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setUpProgressDialog(R.string.loadingMessage);
		}

		@Override
		protected Void doInBackground(String... params) {
			String target_url = setTargetURL();
			String economic_data = getXmlPage(target_url);
			String[] tags = getResources()
					.getStringArray(R.array.economic_info);

			XmlExtractor xml_extractor = new XmlExtractor(economic_data);
			hashMap = xml_extractor.extractTagsFromDoc(tags);
			return null;

		}

		private String setTargetURL() {
			final String UNIV_SPEC_DEG = "university_specific_degrees";
			final String ECONOMIC_REP = "economic_report";
			final String XML_EXTENSION = ".xml";
			final String SLASH = "/";

			GlobalState gs = (GlobalState) getApplication();
			String base_url = gs.getTargetAddress();

			String degree_id = getIntent().getStringExtra("id");

			String target_url = base_url + UNIV_SPEC_DEG + SLASH + degree_id
					+ SLASH + ECONOMIC_REP + XML_EXTENSION;

			return target_url;
		}

		private String getXmlPage(String target_url) {
			XmlPage xml_page = new XmlPage(baseContext);
			String xml_response = null;
			try {
				xml_response = xml_page.getPage(target_url);
			} catch (IOException e) {
				error = e;
				return null;
			}
			return xml_response;
		}

		@Override
		protected void onPostExecute(Void param) {

			dismissWindow();
			if (error != null) {
				launchErrorWindow(0);
			} else {
				initialiseTabHost(bundle);
				if (bundle != null) {
					mTabHost.setCurrentTabByTag(bundle.getString("tab"));
				}
			}

		}

	}

	@SuppressWarnings("unchecked")
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		EconomicInfo.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator(
						getResources().getString(R.string.overviewTab)),
				(tabInfo = new TabInfo("Tab1", TabOverview.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		EconomicInfo.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator(
						getResources().getString(R.string.incomeTab)),
				(tabInfo = new TabInfo("Tab2", TabIncome.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		EconomicInfo.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator(
						getResources().getString(R.string.expensesTab)),
				(tabInfo = new TabInfo("Tab3", TabExpenditure.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}

	private static void addTab(EconomicInfo activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}
		tabHost.addTab(tabSpec);
	}

	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}
			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}
}
