package com.egima.clouduser;

import io.cloudboost.CloudException;
import io.cloudboost.CloudUser;
import io.cloudboost.CloudUserCallback;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {

	public ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		pdialog = new ProgressDialog(this);

		final TextInputLayout oldPasswordWrapper = (TextInputLayout) findViewById(R.id.old_password_wrapper);
		final TextInputLayout newPasswordWrapper = (TextInputLayout) findViewById(R.id.new_password_wrapper);

		final Button btn = (Button) findViewById(R.id.change_password_button);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();

				String oldPass = oldPasswordWrapper.getEditText().getText()
						.toString();
				String newPass = newPasswordWrapper.getEditText().getText()
						.toString();
				oldPasswordWrapper.setErrorEnabled(false);
				newPasswordWrapper.setErrorEnabled(false);

				doChange(oldPass, newPass);

			}
		});
	}

	public void doChange(String oldPass, String newPass) {
		new change().execute(oldPass, newPass);
	}

	public boolean validatePassword(String password) {
		return password.length() > 5;
	}

	private void hideKeyboard() {
		View view = getCurrentFocus();
		if (view != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	class change extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			pdialog.setMessage("changing password...");
			pdialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params) {
			CloudUser user = App.CURRENT_USER;
			try {
				user.changePassword(params[0], params[1],
						new CloudUserCallback() {

							@Override
							public void done(CloudUser user,
									final CloudException e)
									throws CloudException {
								if (e != null)
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(
													ChangePasswordActivity.this,
													e.getMessage(),
													Toast.LENGTH_SHORT).show();
											;

										}
									});
								else {
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(
													ChangePasswordActivity.this,
													"Password changed successfully",
													Toast.LENGTH_SHORT).show();
											;

										}
									});
									CloudUser.setCurrentUser(user);
									App.CURRENT_USER = user;
									Intent i = new Intent(
											ChangePasswordActivity.this,
											DashboardActivity.class);
									startActivity(i);
								}

							}
						});
			} catch (final CloudException e) {
				Log.e("ChangePassword", e.getMessage());
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(ChangePasswordActivity.this,
								e.getMessage(), Toast.LENGTH_SHORT).show();
						;

					}
				});
			}

			return null;
		}

	}

}
