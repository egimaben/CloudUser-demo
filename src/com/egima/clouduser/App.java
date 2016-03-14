package com.egima.clouduser;

import io.cloudboost.CloudApp;
import io.cloudboost.CloudUser;
import android.app.Application;
import android.util.Log;

public class App extends Application {
public static CloudUser CURRENT_USER=null;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("App", "onCreate(");
		CloudApp.init("cpnbzclvxjts", "67d3f4e4-97e6-4f2b-91af-d553b9160e20");
	}
	public static void logout(){
		CURRENT_USER=null;
		CloudUser.setCurrentUser(null);
	}

}
