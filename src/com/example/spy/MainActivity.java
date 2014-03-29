package com.example.spy;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionbar = getActionBar();
		actionbar.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onResume()
	{
		super.onResume();
		CountDownTimer waitTimer = new CountDownTimer(2000, 1000) {

			public void onTick(long millisUntilFinished) {
				//called every 300 milliseconds, which could be used to
				//display some crude animation
			}

			public void onFinish() {
				startActivity(new Intent(getApplicationContext(), LoginActivity.class));
				//After 60000 milliseconds (60 sec) finish current 
				//activity and open next activity           
			}
		}.start();
	}
}
