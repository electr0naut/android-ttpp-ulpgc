package es.ulpgc.titulospropios.gestion.ubay;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class DegreeMainMenuFragment extends Fragment {
	OnAcademicInfoClickedListener mAcademicCallback;
	OnEconomicInfoClickedListener mEconomicCallback;
	OnTeachersClickedListener mTeachersCallback;
	OnSubjectsClickedListener mSubjectsCallback;
	OnReportClickedListener mReportCallback;

	public interface OnAcademicInfoClickedListener {
		public void OnAcademicInfoClicked();
	}

	public interface OnEconomicInfoClickedListener {
		public void OnEconomicInfoClicked();
	}

	public interface OnTeachersClickedListener {
		public void OnTeachersClicked();
	}

	public interface OnSubjectsClickedListener {
		public void OnSubjectsClicked();
	}

	public interface OnReportClickedListener {
		public void OnReportClicked();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mAcademicCallback = (OnAcademicInfoClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnAcademicInfoClickedListener");
		}

		try {
			mEconomicCallback = (OnEconomicInfoClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnEconomicInfoClickedListener");
		}

		try {
			mTeachersCallback = (OnTeachersClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTeachersClickedListener");
		}
		try {
			mSubjectsCallback = (OnSubjectsClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSubjectsClickedListener");
		}
		try {
			mReportCallback = (OnReportClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnReportClickedListener");
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View myFragmentView = inflater.inflate(
				R.layout.degree_main_menu_fragment, container, false);

		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return myFragmentView;
	}
}
