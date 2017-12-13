package org.gcs;

import org.gcs.R;
import org.gcs.activitys.RCActivity;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class Splash extends Activity {
	int splashWaitingTime = 2 * 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent intent = new Intent(Splash.this,RCActivity.class);
				finish();
				startActivity(intent);

				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		}, splashWaitingTime);

		new Handler().postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				//Do nothing
			} 
		}, splashWaitingTime);

	}


	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
}
