package es.ulpgc.titulospropios.gestion.ubay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomTeacherAdapter extends ArrayAdapter<ArrayList<String>> {
	private ArrayList<ArrayList<String>> entries;
	private Fragment fragment;

	OnTeacherClickedListener mCallback;

	public static final String CONFIRMATION_PENDING = "Pendiente de confirmar participación";

	public CustomTeacherAdapter(Fragment a, int textViewResourceId,
			ArrayList<ArrayList<String>> entries, Context baseContext) {
		super(a.getActivity(), textViewResourceId, entries);
		this.entries = entries;
		this.fragment = a;
	}

	public static class ViewHolder {
		public Button boton_nombre;
		public Button boton_notify;

		public TextView type;
		public TextView state;
		public TextView last_notice;
		public TextView num_notice;
		public TextView degree;
		public TextView email;
		public String id;
		public String degree_id;
	}

	public interface OnTeacherClickedListener {
		public void OnTeacherClicked(String teacher_info);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) fragment.getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.single_teacher_row, null);
			holder = new ViewHolder();
			holder.boton_nombre = (Button) v.findViewById(R.id.button_teacher);
			holder.boton_notify = (Button) v.findViewById(R.id.button_notify);
			holder.type = (TextView) v.findViewById(R.id.textView_teacher_type);
			holder.state = (TextView) v
					.findViewById(R.id.textView_teacher_state);
			holder.last_notice = (TextView) v
					.findViewById(R.id.textView_teacher_lastnotified);
			holder.num_notice = (TextView) v
					.findViewById(R.id.textView_teacher_numnotice);
			holder.degree = (TextView) v
					.findViewById(R.id.textView_teacher_degree);
			holder.email = (TextView) v
					.findViewById(R.id.textView_teacher_email);

			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		String[] teacher_tags = fragment.getActivity().getResources()
				.getStringArray(R.array.teacher_forAdapter);
		final ArrayList<String> custom = entries.get(position);
		@SuppressWarnings("unused")
		final Context baseContext = fragment.getActivity()
				.getApplicationContext();
		final String id = custom.get(8);
		final String degree_id = custom.get(9);
		final String participation_id = custom.get(10);
		final String whole_teacher = custom.get(11);
		final String name = new String(custom.get(0) + " " + custom.get(1));
		if (custom != null) {
			holder.id = id;
			holder.degree_id = degree_id;
			holder.boton_nombre.setText(Html.fromHtml("<b>" + name + "</b>"));

			if (custom.get(3).equals(CONFIRMATION_PENDING)) {
				holder.boton_notify.setText(Html.fromHtml(fragment
						.getActivity().getResources()
						.getString(R.string.notify)));
				holder.boton_notify.setVisibility(Button.VISIBLE);
			} else {
				holder.boton_notify.setVisibility(Button.GONE);
			}
			holder.type.setText(Html.fromHtml(teacher_tags[0] + custom.get(2)));
			holder.state
					.setText(Html.fromHtml(teacher_tags[1] + custom.get(3)));
			holder.last_notice.setText(Html.fromHtml(teacher_tags[2]
					+ custom.get(4)));
			holder.num_notice.setText(Html.fromHtml(teacher_tags[3]
					+ custom.get(5)));
			Log.d(custom.get(1), custom.get(5));
			holder.degree
					.setText(Html.fromHtml(teacher_tags[4] + custom.get(6)));
			holder.email
					.setText(Html.fromHtml(teacher_tags[5] + custom.get(7)));
			holder.boton_nombre.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((OnTeacherClickedListener) fragment)
							.OnTeacherClicked(whole_teacher);

				}
			});

			final String degree_id_final = custom.get(9);

			holder.boton_notify.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setVisibility(Button.GONE);
					new notifyTeacher().execute(participation_id,
							degree_id_final);
				}
			});
		}
		return v;
	}

	private class notifyTeacher extends AsyncTask<String, Void, Integer> {
		IOException error;
		private static final String BASE_URL = "university_specific_degrees/";
		private static final String TEACHERS_URL = "/degree_teachers/";
		private static final String REQUEST_CONF = "/request_confirmation";
		private static final String PARAM_DEGREE = "university_specific_degree_id";

		protected void onPreExecute() {

			((MyFragmentActivity) fragment.getActivity())
					.setUpProgressDialog(R.string.notifying);

		}

		@Override
		protected Integer doInBackground(String... params) {
			GlobalState gs = (GlobalState) ((Activity) fragment.getActivity())
					.getApplication();
			HttpClient client = gs.getHttpClient();
			HttpContext localContext = gs.getLocalContext();
			String target_address = gs.getTargetAddress();
			target_address = target_address + BASE_URL + params[1];
			target_address = target_address + TEACHERS_URL + params[0]
					+ REQUEST_CONF;

			HttpPut httpPut = new HttpPut(target_address);
			List<NameValuePair> put_params = new ArrayList<NameValuePair>();
			put_params.add(new BasicNameValuePair(PARAM_DEGREE, params[0]));
			put_params.add(new BasicNameValuePair("id", params[1]));
			UrlEncodedFormEntity ent = null;
			@SuppressWarnings("unused")
			String xml_response = null;

			try {
				ent = new UrlEncodedFormEntity(put_params, HTTP.UTF_8);
				httpPut.setEntity(ent);

				HttpResponse responsePUT = client
						.execute(httpPut, localContext);
				HttpEntity getEntity = responsePUT.getEntity();
				xml_response = EntityUtils.toString(getEntity);
				getEntity.consumeContent();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				error = e;
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				error = e;
				return null;
			}

			return 1;
		}

		protected void onPostExecute(Integer result) {
			((MyFragmentActivity) fragment.getActivity()).dismissWindow();
			if (error != null) {
				((MyFragmentActivity) fragment.getActivity())
						.launchErrorWindow(1);

				return;
			}
			return;
		}
	}
}
