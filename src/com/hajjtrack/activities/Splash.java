package com.hajjtrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.maxxsol.hajjtrack.R;

public class Splash extends Activity {

	protected int _splashTime = 3000; // time to display the splash screen in ms
	SharedPreferences _prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_splash);
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		Handler x = new Handler();
		x.postDelayed(new SplashHandler(), _splashTime);

	}

	class SplashHandler implements Runnable {

		@Override
		public void run() {
			Splash.this.startActivity(new Intent(Splash.this, MainActivity.class));
			Splash.this.finish();
		}

	}

}