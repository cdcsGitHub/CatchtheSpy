package com.example.spy;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;

public class JoinGameActivity extends Activity 
{
	private JSONObject serverMessage;
	protected ArrayAdapter adapter;
	final ArrayList<String> list = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_game, menu);
		return true;
	}
	
	public void onResume()
	{
		super.onResume();
	}
	
	public void launchGame(View v)
	{
		startActivity(new Intent(getApplicationContext(), GameActivity.class));
	}

}
