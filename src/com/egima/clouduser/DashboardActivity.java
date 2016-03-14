package com.egima.clouduser;

import io.cloudboost.CloudException;
import io.cloudboost.CloudUser;
import io.cloudboost.CloudUserCallback;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DashboardActivity extends ActionBarActivity {
    public ProgressDialog pdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
        pdialog=new ProgressDialog(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    class logout extends AsyncTask<String,String,String>{
    	

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			pdialog.setMessage("Logging out...");
			pdialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params) {
			CloudUser user=new CloudUser();
			user.setUserName(params[0]);
			user.setPassword(params[1]);
			user.setEmail(params[2]);
			try {
				user.signUp(new CloudUserCallback() {
					
					@Override
					public void done(CloudUser user, CloudException e) throws CloudException {
						
					}
				});
			} catch (CloudException e) {
				Log.e("SignupActivity", e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
    	
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.action_change_password) {
			Intent i=new Intent(this, ChangePasswordActivity.class);
			startActivity(i);
			return true;
		}
		if (id == R.id.action_logout) {
			App.logout();
			Intent i=new Intent(this, LoginActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
