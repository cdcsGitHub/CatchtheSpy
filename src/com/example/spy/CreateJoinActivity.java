package com.example.spy;

import org.json.JSONException;
import org.json.JSONObject;

import connections.server.Client;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CreateJoinActivity extends Activity 
{
	public SharedPreferences sharedPrefs = null;
	private JSONObject serverMessage;
	private Client client;
	private String username;
	private Location currentLoc;
	private double lat;
	private double lon;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_join);

		serverMessage = new JSONObject();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		//		lat = currentLoc.getLatitude();
		//		lon = currentLoc.getLongitude();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_join, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onResume()
	{
		super.onResume();

	}

	public void create(View v)
	{
		username = sharedPrefs.getString("username", null);
		try 
		{
			serverMessage.put("ActionNum", 2);
			serverMessage.put("Username", username);
			//			serverMessage.put("Latitude", lat);
			//			serverMessage.put("Longitude", lon);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		client = new Client(serverMessage);
		client.connect();
		startActivity(new Intent(getApplicationContext(), TabActivity.class));
	}

	public void join(View v)
	{
		startActivity(new Intent(getApplicationContext(), JoinGameActivity.class));
	}
}
