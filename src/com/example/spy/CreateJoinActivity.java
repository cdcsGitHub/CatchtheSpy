package com.example.spy;

import org.json.JSONException;
import org.json.JSONObject;

import connections.server.Client;
import database.handlers.DatabaseHandler;
import database.handlers.User;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class CreateJoinActivity extends Activity 
{
	private JSONObject serverMessage;
	private Client client;
	private DatabaseHandler db = new DatabaseHandler(this);
	private User user;
	private String name;
	private Location currentLoc;
	private double lat;
	private double lon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_join);
		
		serverMessage = new JSONObject();
		user = db.getUser(0);
		name = user.getUserName();
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
	
	public void onResume()
	{
		super.onResume();
		
	}

	public void create(View v)
	{
		try 
		{
			serverMessage.put("ActionNum", 2);
			serverMessage.put("Username", name);
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
