package es.ulpgc.titulospropios.gestion.ubay;

import java.util.ArrayList;

import android.content.Context;
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

public class CustomSubjectsAdapter extends ArrayAdapter<ArrayList<String>> {
	private ArrayList<ArrayList<String>> entries;
	private Fragment fragment;
	OnSubjectClickedListener mCallback;

	public CustomSubjectsAdapter(Fragment a, int textViewResourceId,
			ArrayList<ArrayList<String>> entries) {
		super(a.getActivity(), textViewResourceId, entries);
		this.entries = entries;
		this.fragment = a;
	}

	public static class ViewHolder {
		public Button boton;
		public TextView name;
		public TextView course;
		public TextView semester;
		public TextView ECTSCredits;
		public TextView starts;
		public TextView ends;
		public TextView kind;
		public String id;
	}

	public interface OnSubjectClickedListener {
		public void OnSubjectClicked(String subject_info);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) fragment.getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.single_subject_row, null);
			holder = new ViewHolder();
			holder.boton = (Button) v.findViewById(R.id.button_subject);

			holder.course = (TextView) v
					.findViewById(R.id.textView_subject_course);
			holder.semester = (TextView) v
					.findViewById(R.id.textView_subject_semester);
			holder.ECTSCredits = (TextView) v
					.findViewById(R.id.textView_subject_ects);
			holder.starts = (TextView) v
					.findViewById(R.id.textView_subject_starts);
			holder.ends = (TextView) v.findViewById(R.id.textView_subject_ends);
			holder.kind = (TextView) v.findViewById(R.id.textView_subject_kind);

			v.setTag(holder);
		} else
			holder = (ViewHolder) v.getTag();

		final ArrayList<String> custom = entries.get(position);
		final String id = custom.get(7);
		final String whole = custom.get(8);
		String[] subject_tags = fragment.getActivity().getResources()
				.getStringArray(R.array.subject_forAdapter);
		if (custom != null) {
			holder.id = id;

			holder.boton.setText(Html.fromHtml("<b>" + custom.get(0) + "</b>"));

			holder.course
					.setText(Html.fromHtml(subject_tags[0] + custom.get(1)));
			holder.semester.setText(Html.fromHtml(subject_tags[1]
					+ custom.get(2)));
			Log.d("Starts: ", custom.get(3));
			Log.d("Ends: ", custom.get(4));
			if (custom.get(3).equals("") || custom.get(3) == null) {
				holder.starts.setText(Html.fromHtml(subject_tags[2]
						+ fragment.getActivity().getResources()
								.getString(R.string.unspecified)));
			} else {
				holder.starts.setText(Html.fromHtml(subject_tags[2]
						+ custom.get(3)));
			}
			if (custom.get(4).equals("") || custom.get(4) == null) {
				holder.ends.setText(Html.fromHtml(subject_tags[3]
						+ fragment.getActivity().getResources()
								.getString(R.string.unspecified)));
			} else {
				holder.ends.setText(Html.fromHtml(subject_tags[3]
						+ custom.get(4)));
			}

			String prefix = new String(subject_tags[5]);
			String[] subject_types = fragment.getActivity().getResources()
					.getStringArray(R.array.subject_types);

			if (custom.get(5).equals("1"))
				holder.kind.setText(Html.fromHtml(prefix + subject_types[0]));
			else if (custom.get(5).equals("2"))
				holder.kind.setText(Html.fromHtml(prefix + subject_types[1]));
			else if (custom.get(5).equals("3"))
				holder.kind.setText(Html.fromHtml(prefix + subject_types[2]));
			else if (custom.get(5).equals("4"))
				holder.kind.setText(Html.fromHtml(prefix + subject_types[3]));
			else if (custom.get(5).equals("5"))
				holder.kind.setText(Html.fromHtml(prefix + subject_types[4]));

			holder.ECTSCredits.setText(Html.fromHtml(subject_tags[4]
					+ custom.get(6).toString()));

			holder.boton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// mCallback.OnSubjectClicked(custom.get(8));
					((OnSubjectClickedListener) fragment)
							.OnSubjectClicked(whole);

				}

			});
		}
		return v;
	}
}