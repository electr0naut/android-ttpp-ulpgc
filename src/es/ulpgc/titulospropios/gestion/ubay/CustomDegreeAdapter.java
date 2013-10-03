package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomDegreeAdapter extends ArrayAdapter<ArrayList<String>> {

	private ArrayList<ArrayList<String>> entries;
	private Activity activity;

	public CustomDegreeAdapter(Activity a, int textViewResourceId,
			ArrayList<ArrayList<String>> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public static class ViewHolder {
		public Button boton;
		public TextView type;
		public TextView state;
		public TextView edition;
		public TextView code;
		public String id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.single_degree_row, null);
			holder = new ViewHolder();
			holder.boton = (Button) v.findViewById(R.id.button_degree);
			holder.type = (TextView) v.findViewById(R.id.textView_type);
			holder.state = (TextView) v.findViewById(R.id.textView_state);
			holder.edition = (TextView) v.findViewById(R.id.textView_edition);
			holder.code = (TextView) v.findViewById(R.id.textView_code);

			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final ArrayList<String> custom = entries.get(position);
		final String id = custom.get(5);
		final String edition = custom.get(3);
		final String state = custom.get(2);

		if (custom != null) {
			holder.id = id;

			holder.boton.setText(Html.fromHtml("<b>" + custom.get(0) + "</b>"));

			if (custom.get(1).equals("1"))
				holder.type.setText(Html.fromHtml(activity.getResources()
						.getString(R.string.ownDegreeBasic)));
			else if (custom.get(1).equals("2"))
				holder.type.setText(Html.fromHtml(activity.getResources()
						.getString(R.string.ownDegreeHigher)));
			else if (custom.get(1).equals("3"))
				holder.type.setText(Html.fromHtml(activity.getResources()
						.getString(R.string.expert)));
			else if (custom.get(1).equals("4"))
				holder.type.setText(Html.fromHtml(activity.getResources()
						.getString(R.string.mastery)));
			else if (custom.get(1).equals("5"))
				holder.type.setText(Html.fromHtml(activity.getResources()
						.getString(R.string.professionalDiploma)));

			String[] degree_states = activity.getResources().getStringArray(
					R.array.degree_states);

			String prefix = activity.getResources().getString(R.string.state);
			if (custom.get(2).equals("borrador"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[1]));
			else if (custom.get(2).equals("enviado"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[2]));
			else if (custom.get(2).equals("rechazado"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[3]));
			else if (custom.get(2).equals("aprobado"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[4]));
			else if (custom.get(2).equals("subsanacion"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[5]));
			else if (custom.get(2).equals("cancelado"))
				holder.state.setText(Html.fromHtml(prefix + degree_states[6]));
			else
				holder.state.setText("");

			holder.edition.setText(Html.fromHtml(activity.getResources()
					.getString(R.string.starts) + custom.get(3)));
			holder.code.setText(Html.fromHtml(activity.getResources()
					.getString(R.string.code) + custom.get(4)));

			holder.boton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(activity, DegreeMainMenu.class);
					intent.putExtra("id", id);
					intent.putExtra("edition", edition);
					intent.putExtra("state", state);
					activity.startActivity(intent);

				}
			});
		}
		return v;
	}

}
