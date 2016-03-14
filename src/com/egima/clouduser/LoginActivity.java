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


public class LoginActivity extends Activity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;
    public ProgressDialog pdialog;
    TextInputLayout usernameWrapper ;
    TextInputLayout passwordWrapper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pdialog=new ProgressDialog(this);

        usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final Button btn = (Button) findViewById(R.id.btn);
        final TextView signupText=(TextView) findViewById(R.id.signup);
        final TextView resetPass=(TextView) findViewById(R.id.reset_password);
        resetPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(LoginActivity.this, ResetActivity.class);
				startActivity(i);
				
			}
		});
        signupText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
				
			}
		});




        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                String username = usernameWrapper.getEditText().getText().toString();
                String password = passwordWrapper.getEditText().getText().toString();
               if (!validatePassword(password)) {
                    passwordWrapper.setError("Not a valid password!");
                } else {
                    usernameWrapper.setErrorEnabled(false);
                    passwordWrapper.setErrorEnabled(false);
                    doLogin(username,password);
                }
            }
        });
    }

    public void doLogin(String uname,String pass) {
        new login().execute(uname,pass);
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
    class login extends AsyncTask<String,String,String>{
    	

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
			CloudUser user=new CloudUser();
			user.setUserName(params[0]);
			user.setPassword(params[1]);
			try {
				user.logIn(new CloudUserCallback() {
					
					@Override
					public void done(CloudUser user, CloudException e) throws CloudException {
						if(e!=null)
							throw e;
						else{
							App.CURRENT_USER=user;
							Intent i=new Intent(LoginActivity.this, DashboardActivity.class);
							startActivity(i);
						}
					}
				});
			} catch (final CloudException e) {
				Log.e("LoginActivity", e.getMessage());
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						passwordWrapper.getEditText().setText("");
						usernameWrapper.getEditText().setText("");
					}
				});
			}
			
			return null;
		}
    	
    }

}
