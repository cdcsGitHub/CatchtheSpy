package com.example.spy;

import android.app.Activity;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

public class SettingsActivity extends Activity 
{

	public SharedPreferences sharedPrefs = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ActionBar actionbar = getActionBar();
		actionbar.show();
		getFragmentManager().beginTransaction().replace(android.R.id.content, 
				new SettingsFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
