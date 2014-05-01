package com.example.spy;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.content.SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class CreateJoinActivity extends Activity implements LocationListener 
{
	public JSONObject toServerMessage;
	public JSONObject fromServerMessage;
	public static final int SERVERPORT = 9999;
	private String serverIpAddress = "68.57.74.253";
	//private String serverIpAddress = "147.138.43.128";
	@SuppressWarnings("unused")
	private boolean connected = false;
	private DataInputStream is = null;
	
	public SharedPreferences sharedPrefs = null;
	private String username;
	private String provider;
	private String serverResponse;
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

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		fromServerMessage = new JSONObject();
		toServerMessage = new JSONObject();
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
		createGame newGame = new createGame();
		newGame.execute();
	}

	public void join(View v)
	{
		joinGame joinGame = new joinGame();
		joinGame.execute();
	}

	/**
	 * 
	 */
	public class createGame extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			username = sharedPrefs.getString("username", null);
			try 
			{
				toServerMessage.put("ActionNum", "2");
				toServerMessage.put("Username", username);
				toServerMessage.put("Latitude", String.valueOf(lat));
				toServerMessage.put("Longitude", String.valueOf(lon));
				toServerMessage.put("userLat", String.valueOf(lat));
				toServerMessage.put("userLon", String.valueOf(lon));
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				Log.d("ClientActivity", "C: Connecting...");
				Socket socket = new Socket(serverAddr,SERVERPORT);
				connected = true;
				try {
					Log.d("ClientActivity", "C: Sending command.");
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					out.println(toServerMessage.toString());
					is = new DataInputStream(socket.getInputStream());
					String line = is.readLine();
					fromServerMessage = new JSONObject(line);
					serverResponse = fromServerMessage.getString("Response");
					sharedPrefs.edit().putString("createResponse", serverResponse).commit();
					serverResponse = fromServerMessage.getString("Response");
					Log.d("Server", fromServerMessage.toString());
					Log.d("ClientActivity", "C: Sent.");
				} catch (Exception e) {
					Log.e("ClientActivity", "S: Error", e);
				}
				socket.close();
				Log.d("ClientActivity", "C: Closed.");
				connected = false;
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
				connected = false;
			}
			return serverResponse;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			
			if (result.equals("SUCCESS")) 
			{
				startActivity(new Intent(getApplicationContext(), TabActivity.class));
			}
			else 
			{
				Toast.makeText(getApplicationContext(), 
						"Error, could not create game", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class joinGame extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			username = sharedPrefs.getString("username", null);
			try 
			{
				toServerMessage.put("ActionNum", "3");
				toServerMessage.put("Username", username);
				toServerMessage.put("Latitude", lat);
				toServerMessage.put("Longitude", lon);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			try {
				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
				Log.d("ClientActivity", "C: Connecting...");
				Socket socket = new Socket(serverAddr,SERVERPORT);
				connected = true;
				try {
					Log.d("ClientActivity", "C: Sending command.");
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())),
							true);
					out.println(toServerMessage.toString());
					is = new DataInputStream(socket.getInputStream());
					String line = is.readLine();
					fromServerMessage = new JSONObject(line);
					serverResponse = fromServerMessage.getString("Response");
					sharedPrefs.edit().putString("joinResponse", serverResponse).commit();
					serverResponse = fromServerMessage.getString("Response");
					Log.d("Server", fromServerMessage.toString());
					Log.d("ClientActivity", "C: Sent.");
				} catch (Exception e) {
					Log.e("ClientActivity", "S: Error", e);
				}
				socket.close();
				Log.d("ClientActivity", "C: Closed.");
				connected = false;
			} catch (Exception e) {
				Log.e("ClientActivity", "C: Error", e);
				connected = false;
			}
			return serverResponse;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			
			if (result.equals("SUCCESS")) 
			{
				startActivity(new Intent(getApplicationContext(), JoinGameActivity.class));
			}
			else 
			{
				Toast.makeText(getApplicationContext(), 
						"Error, could not find games", Toast.LENGTH_LONG).show();
			}
		}
	}
}
