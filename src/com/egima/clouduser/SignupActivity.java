package com.egima.clouduser;

import io.cloudboost.CloudException;
import io.cloudboost.CloudUser;
import io.cloudboost.CloudUserCallback;

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


public class SignupActivity extends Activity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    public ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pdialog=new ProgressDialog(this);

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.signup_username_wrapper);
        final TextInputLayout emailWrapper = (TextInputLayout) findViewById(R.id.email_wrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.signup_password_wrapper);
        final Button signUp = (Button) findViewById(R.id.signup_button);
        TextView login=(TextView) findViewById(R.id.go_login);
        login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(SignupActivity.this, LoginActivity.class);
				startActivity(i);
				
			}
		});

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
                String email=emailWrapper.getEditText().getText().toString();
                if (!validateEmail(email)) {
                	emailWrapper.setError("Not a valid email address!");
                } else if (!validatePassword(password)) {
                    passwordWrapper.setError("Not a valid password!");
                } else {
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    emailWrapper.setErrorEnabled(false);
                    doSignup(username,password,email);
                }
            }
        });
    }

    public void doSignup(String uname,String pass,String email) {
        new signup().execute(uname,pass,email);
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
    class signup extends AsyncTask<String,String,String>{
    	

		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			pdialog.setMessage("Creating Account...");
			pdialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pdialog.dismiss();
		}

		@Override
		protected String doInBackground(final String... params) {
			CloudUser user=new CloudUser();
			user.setUserName(params[0]);
			user.setPassword(params[1]);
			user.setEmail(params[2]);
			try {
				user.signUp(new CloudUserCallback() {
					
					@Override
					public void done(CloudUser user, CloudException e) throws CloudException {
						if(e!=null)
							throw e;
						else{
							runOnUiThread(new Runnable() {
								public void run() {
									Toast.makeText(SignupActivity.this, "New user "+params[0]+" created", Toast.LENGTH_SHORT).show();;
									
								}
							});
							CloudUser.setCurrentUser(user);
							App.CURRENT_USER=user;
							Intent i=new Intent(SignupActivity.this, DashboardActivity.class);
							startActivity(i);
						}
					}
				});
			} catch (final CloudException e) {
				Log.e("SignupActivity", e.getMessage());
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;

					}
				});
			}
			
			return null;
		}
    	
    }

}
