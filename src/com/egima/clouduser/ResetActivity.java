package com.egima.clouduser;

import io.cloudboost.CloudException;
import io.cloudboost.CloudStringCallback;
import io.cloudboost.CloudUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ResetActivity extends Activity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    public ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        pdialog=new ProgressDialog(this);

        final TextInputLayout emailWrapper = (TextInputLayout) findViewById(R.id.reset_email_wrapper);
        final Button btn = (Button) findViewById(R.id.reset_button);
        final TextView go_login=(TextView) findViewById(R.id.return_login);
        go_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(ResetActivity.this, LoginActivity.class);
				startActivity(i);
				
			}
		});

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                String email = emailWrapper.getEditText().getText().toString();
                if (!validateEmail(email)) {
                	emailWrapper.setError("Not a valid email address!");
                } else {
                	emailWrapper.setErrorEnabled(false);
                    doReset(email);
                }
            }
        });
    }

    public void doReset(String email) {
        new reset().execute(email);
    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    class reset extends AsyncTask<String,String,String>{
    	

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			pdialog.setMessage("Authenticating...");
			pdialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				CloudUser.resetPassword(params[0], new CloudStringCallback() {
					
					@Override
					public void done(final String x, CloudException e) throws CloudException {
						if(e!=null)
							throw e;
						else{
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(ResetActivity.this, x, Toast.LENGTH_SHORT).show();

								}
							});
							Intent i=new Intent(ResetActivity.this, LoginActivity.class);
							startActivity(i);
						}
						
					}
				});
			} catch (final CloudException e) {
				Log.e("ResetActivity", e.getMessage());
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(ResetActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

					}
				});
			}

			
			return null;
		}
    	
    }

}
