package com.example.spy;


import org.json.JSONException;
import org.json.JSONObject;

import connections.server.Client;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CreateJoinActivity extends Activity implements LocationListener 
{
	public SharedPreferences sharedPrefs = null;
	private JSONObject serverMessage;
	private Client client;
	private String username;
	private String provider;
	private double lat;
	private double lon;
	protected LocationManager locationManager;
	protected LocationListener locationLisener;
	protected Location location;
	protected Context context;
	protected Criteria criteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_join);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setCostAllowed(true);
	    
		serverMessage = new JSONObject();
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);
		onLocationChanged(location);
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

	public void onLocationChanged(Location location)
	{
		lat = location.getLatitude();
		lon = location.getLongitude();
	}
	@Override
	public void onProviderDisabled(String provider) 
	{
		Log.d("Latitude","disable");
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		Log.d("Latitude","enable");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		Log.d("Latitude","status");
	}

	public void create(View v)
	{
		username = sharedPrefs.getString("username", null);
		try 
		{
			serverMessage.put("ActionNum", 2);
			serverMessage.put("Username", username);
			serverMessage.put("Latitude", lat);
			serverMessage.put("Longitude", lon);
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
		try 
		{
			serverMessage.put("ActionNum", 3);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		client = new Client(serverMessage);
		client.connect();
		startActivity(new Intent(getApplicationContext(), JoinGameActivity.class));
	}
}
