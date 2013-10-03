package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginUser extends MyFragmentActivity {
	private String target_addr;
	private String ROLE = "manager";
	private static final String REMEMBER_USER = "remember_user";
	private static final String USER_INFO = "Preferences";
	private static final String LOGIN = "login";
	private static final String PASSWORD = "passwd";
	private static final String PARAM_LOGIN = "user_session[login]";
	private static final String PARAM_PASSWD = "user_session[password]";
	private static final String PARAM_DNI = "user_session[dni]";
	private static final String MANAGER_SESSION = "user_session";
	private static final String TEACHER_SESSION = "teacher_session";
	private static final String RESOURCE_URL = "university_specific_degrees.xml";
	private static final String ROLE_PREFERENCES = "role";

	private Context baseContext = this;
	private boolean manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getBoolean(R.bool.portrait_only)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.login_user_activity);

		setBaseAddress();

		setRoleMessageOnScreen();

		if (rememberUserFeatureisOn()) {
			setStoredUserDataOnEditBoxes();
		}

		setUpLoginButton();

		setUpRememberUserButton();
	}

	private void setBaseAddress() {
		target_addr = getResources().getString(R.string.production_addr);
	}

	private void setUpRememberUserButton() {
		CheckBox check_box = (CheckBox) findViewById(R.id.rememberButton);
		check_box.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(USER_INFO, 0);
				SharedPreferences.Editor editor = settings.edit();

				editor.putBoolean(REMEMBER_USER, ((CheckBox) v).isChecked());
				editor.commit();
			}
		});
	}

	private void setUpLoginButton() {
		Button login_button = (Button) findViewById(R.id.loginButton);
		login_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new performLogin().execute(target_addr);
			}
		});
	}

	private class performLogin extends AsyncTask<String, Void, Integer> {
		HttpContext localContext;
		CookieStore cookieStore;
		DefaultHttpClient client;
		HttpPost post;
		IOException error;
		int response_length;

		@Override
		protected void onPreExecute() {

			LoginUser.this.setUpProgressDialog(R.string.loggingIn);

		}

		@Override
		protected Integer doInBackground(String... url) {
			List<NameValuePair> params;

			initializeConnectionElements();
			if (manager) {
				params = setUpHTTPParametersForManager(url[0]);
			} else
				params = setUpHTTPParametersForTeacher(url[0]);

			sendPOSTPetition(params);

			String login_response = retrieveResponse(url[0]);
			if (error != null) {
				return null;
			}
			response_length = login_response.length();

			if (login_response != null) {
				saveSessionContext();
				saveLoginPreferences();
			}

			return null;
		}

		protected void onPostExecute(Integer i) {

			if (error != null) {
				launchErrorWindow(1);
			} else {
				if (response_length == 2172 || response_length == 2173) {
					setUpLoginAlertDialogError();
				} else {
					dismissWindow();

					manager = getIntent().getExtras().getBoolean(ROLE);

					if (manager)
						startActivity(new Intent(baseContext, SelectTask.class));
					else
						startActivity(new Intent(baseContext,
								SearchDegrees.class));

				}
			}
			return;
		}

		private void saveSessionContext() {
			GlobalState gs = (GlobalState) getApplication();
			gs.setCookieStore(cookieStore);
			gs.setHttpClient(client);
			gs.setLocalContext(localContext);
			gs.setRole(manager);
			gs.setBaseAddress(target_addr);

		}

		@SuppressLint("CommitPrefEdits")
		private void saveLoginPreferences() {

			SharedPreferences settings = getSharedPreferences(USER_INFO, 0);
			SharedPreferences.Editor editor = settings.edit();
			boolean remember_next_session = settings.getBoolean(REMEMBER_USER,
					false);

			if (remember_next_session) {
				saveLoginSession(editor);
			} else
				deleteLoginSession(editor);

		}

		private void deleteLoginSession(Editor editor) {
			editor.remove(LOGIN).remove(PASSWORD).remove(ROLE_PREFERENCES)
					.remove(REMEMBER_USER).commit();
		}

		private void saveLoginSession(Editor editor) {
			EditText login = (EditText) findViewById(R.id.login_message);
			EditText passwd = (EditText) findViewById(R.id.pass_message);
			editor.putString(LOGIN, login.getText().toString())
					.putString(PASSWORD, passwd.getText().toString())
					.putBoolean(ROLE_PREFERENCES, manager).commit();
		}

		private String retrieveResponse(String addr) {
			HttpGet httpGet = new HttpGet(addr + RESOURCE_URL);
			HttpResponse responseGET = null;
			String xml_response = null;
			try {
				responseGET = client.execute(httpGet, localContext);
				HttpEntity getEntity = responseGET.getEntity();

				if (getEntity != null) {
					xml_response = EntityUtils.toString(getEntity);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				error = e;
				e.printStackTrace();

				return null;
			}
			return xml_response;

		}

		private void sendPOSTPetition(List<NameValuePair> post_params) {
			UrlEncodedFormEntity ent = null;
			try {
				ent = new UrlEncodedFormEntity(post_params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = null;
				responsePOST = client.execute(post, localContext);
				responsePOST.getEntity().consumeContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private List<NameValuePair> setUpHTTPParametersForTeacher(String addr) {
			EditText login = (EditText) findViewById(R.id.login_message);
			EditText passwd = (EditText) findViewById(R.id.pass_message);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("utf8", "✓"));
			post = new HttpPost(addr + TEACHER_SESSION);
			params.add(new BasicNameValuePair(PARAM_DNI, login.getText()
					.toString()));
			params.add(new BasicNameValuePair(PARAM_PASSWD, passwd.getText()
					.toString()));
			return params;
		}

		private List<NameValuePair> setUpHTTPParametersForManager(String addr) {
			EditText login = (EditText) findViewById(R.id.login_message);
			EditText passwd = (EditText) findViewById(R.id.pass_message);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("utf8", "✓"));
			post = new HttpPost(addr + MANAGER_SESSION);
			params.add(new BasicNameValuePair(PARAM_LOGIN, login.getText()
					.toString()));
			params.add(new BasicNameValuePair(PARAM_PASSWD, passwd.getText()
					.toString()));
			return params;
		}

		private void initializeConnectionElements() {
			localContext = new BasicHttpContext();
			cookieStore = new BasicCookieStore();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			client = new DefaultHttpClient();
		}

	}

	private void setStoredUserDataOnEditBoxes() {
		SharedPreferences settings = getSharedPreferences(USER_INFO, 0);

		CheckBox check_box = (CheckBox) findViewById(R.id.rememberButton);
		EditText user = (EditText) findViewById(R.id.login_message);
		EditText passwd = (EditText) findViewById(R.id.pass_message);

		check_box.setChecked(true);
		user.setText(settings.getString(LOGIN, ""));
		passwd.setText(settings.getString(PASSWORD, ""));
	}

	private boolean rememberUserFeatureisOn() {
		SharedPreferences settings = getSharedPreferences(USER_INFO, 0);
		return settings.getBoolean(REMEMBER_USER, false);

	}

	private void setRoleMessageOnScreen() {

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		TextView textRole = (TextView) findViewById(R.id.login_manager);

		manager = getIntent().getExtras().getBoolean(ROLE);

		if (manager)
			textRole.setText(R.string.accessMessageManager);
		else
			textRole.setText(R.string.accessMessageTeacher);

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
		getMenuInflater().inflate(R.menu.login_user, menu);
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

}
