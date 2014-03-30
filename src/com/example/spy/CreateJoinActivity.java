package com.example.spy;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class CreateJoinActivity extends Activity 
{
	private JSONObject serverMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_join);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_join, menu);
		return true;
	}
	
	public void onResume()
	{
		super.onResume();
		
	}

	public void create(View v)
	{
		startActivity(new Intent(getApplicationContext(), CreateGameActivity.class));
	}
	
	public void join(View v)
	{
		startActivity(new Intent(getApplicationContext(), JoinGameActivity.class));
	}
}
