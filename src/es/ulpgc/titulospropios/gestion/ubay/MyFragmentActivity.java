package es.ulpgc.titulospropios.gestion.ubay;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;

public class MyFragmentActivity extends FragmentActivity {
	protected Context mActivity;
	protected ProgressDialog progressDialog;

	protected void AlertDialogGoingBack(int id) {

		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		mActivity = this;
		alertDialogBuilder.setTitle(R.string.id);

		alertDialogBuilder
				.setMessage(id)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		alertDialogBuilder.create().show();
	}

	protected void setUpProgressDialog(int id) {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}

		progressDialog = new ProgressDialog(this, R.style.MyTheme);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getResources().getString(id));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgress(0);
		progressDialog.show();
	}

	protected void dismissWindow() {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}

	protected void setUpLoginAlertDialogError() {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(R.string.errorLogin);
		alertDialogBuilder
				.setMessage(R.string.message_back_start)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do nothing
							}
						});
		alertDialogBuilder.create().show();
	}

	protected void launchErrorWindow(int error_id) {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
		AlertDialog.Builder alertDialogBuilder = null;

		if (error_id != 0)
			alertDialogBuilder = IOError();
		else
			alertDialogBuilder = IOErrorWithRetry();

		alertDialogBuilder.create().show();

	}

	private Builder IOErrorWithRetry() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		mActivity = this;
		alertDialogBuilder.setTitle(R.string.error_io);

		alertDialogBuilder
				.setMessage(R.string.error_io_message)
				.setCancelable(false)
				.setPositiveButton(R.string.retry,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = getIntent();
								finish();
								startActivity(intent);
							}
						});
		return alertDialogBuilder;
	}

	private Builder IOError() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		mActivity = this;
		alertDialogBuilder.setTitle(R.string.error_io);

		alertDialogBuilder
				.setMessage(R.string.error_io_message)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Do nothing
							}
						});
		return alertDialogBuilder;
	}

	protected void updateProgressDialog(String message) {
		progressDialog.setMessage(Html.fromHtml(message));
	}
}
