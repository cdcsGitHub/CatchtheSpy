package com.example.spy;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import connections.server.Client;
import android.os.Vibrator;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class GameActivity extends Fragment implements LocationListener, LocationSource
{
	public JSONObject toServerMessage;
	public JSONObject fromServerMessage;
	public static final int SERVERPORT = 9999;
	private String serverIpAddress = "68.57.74.253";
	//private String serverIpAddress = "147.138.43.128";
	private boolean connected = false;
	private DataInputStream is = null;
	private double lat;
	private double lon;
	
	private updateUserLocation update;
	private attemptCapture attempt;
	private escapeCapture escape;

	public SharedPreferences sharedPrefs = null;
	private String username;
	public String serverResponse;

	private final int minTime = 10000;
	private final int minDistance = 1;   

	private GoogleMap googleMap;
	private MapView mapView;

	private AlertDialog alertDialog;
	private AlertDialog.Builder alertDialogBuilder;

	private LocationManager locationManager;
	private OnLocationChangedListener mListener;
	private String provider;
	private Criteria criteria;
	protected Location location;
	private CameraPosition camera;
	private CameraUpdate cameraUpdate;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

		View rootView = inflater.inflate(R.layout.activity_game, container, false);

		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setBuildingsEnabled(true);
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		getBestAvailableProvider();
		location = locationManager.getLastKnownLocation(provider);
		
		update = null;
		attempt = null;
		escape = null;

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		toServerMessage = new JSONObject();
		fromServerMessage = new JSONObject();
		openingMapSequence();

		return rootView;
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	public void onResume()
	{
		super.onResume();
//		Toast.makeText(getActivity().getApplicationContext(), 
//				"Test for resume", Toast.LENGTH_LONG).show();
		getBestAvailableProvider();
		googleMap.setMyLocationEnabled(true);
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}

	public void onPause()
	{
		super.onPause();
		googleMap.setMyLocationEnabled(false);
		locationManager.removeUpdates(this);
	}

	public void onLocationChanged(Location location)
	{
//		Toast.makeText(getActivity().getApplicationContext(), 
//				"Test for onLocationChanged", Toast.LENGTH_LONG).show();
		lat = location.getLatitude();
		lon = location.getLongitude();
		LatLng latLng = new LatLng(lat, lon);
		cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
		googleMap.animateCamera(cameraUpdate);
		//locationManager.removeUpdates(this);
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
		update = new updateUserLocation();
		update.execute();
	}

	@Override
	public void activate(OnLocationChangedListener listener) 
	{
//		Toast.makeText(getActivity().getApplicationContext(), 
//				"Test for activate", Toast.LENGTH_LONG).show();
		mListener = listener;
		locationManager.requestLocationUpdates(provider, minTime, minDistance, this);
	}

	@Override
	public void deactivate() 
	{
		locationManager.removeUpdates(this);
		mListener = null;

	}

	private void getBestAvailableProvider() 
	{
		/* The preffered way of specifying the location provider (e.g. GPS, NETWORK) to use 
		 * is to ask the Location Manager for the one that best satisfies our criteria.
		 * By passing the 'true' boolean we ask for the best available (enabled) provider. */
		provider = locationManager.getBestProvider(criteria, true);
	}
	
	public void openingMapSequence()
	{
		camera = new CameraPosition.Builder()
		.target(new LatLng(33.922132, -30.026016))      // Sets the center of the map to Mountain View
		.zoom(0)                   // Sets the zoom
		.build();                   // Creates a CameraPosition from the builder
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
		CountDownTimer waitTimer = new CountDownTimer(5000, 1000) 
		{

			public void onTick(long millisUntilFinished) 
			{

			}

			public void onFinish() 
			{
				googleMap.setBuildingsEnabled(true);

				Circle circle = googleMap.addCircle(new CircleOptions()
				.center(new LatLng(location.getLatitude(), location.getLongitude()))
				.radius(1609)
				.strokeColor(Color.BLUE)
				.fillColor(Color.TRANSPARENT)); 

				camera = CameraPosition.builder()
						.target(new LatLng(location.getLatitude(), location.getLongitude()))     
						.zoom(14)                   
						//.bearing(90)                
						//.tilt(30)                  
						.build();      
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
			}
		}.start();

		CountDownTimer targetTimer = new CountDownTimer(10000, 1000) 
		{

			public void onTick(long millisUntilFinished) 
			{
				//called every 300 milliseconds, which could be used to
				//display some crude animation
			}

			public void onFinish() 
			{

				Circle target = googleMap.addCircle(new CircleOptions()
				.center(new LatLng(38.341163, -78.797684))
				.radius(30.48)
				.strokeWidth(5)
				.strokeColor(Color.RED)
				.fillColor(0x40ff0000));
			}
		}.start();
	}

	public void updateTargetRadius(LatLng latlng)
	{
		Circle target = googleMap.addCircle(new CircleOptions()
		.center(latlng)
		.radius(30.48)
		.strokeWidth(5)
		.strokeColor(Color.RED)
		.fillColor(0x40ff0000));
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		getBestAvailableProvider();
	}

	@Override
	public void onProviderEnabled(String provider) 
	{

	}

	@Override
	public void onProviderDisabled(String provider) 
	{

	}

	public class updateUserLocation extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			username = sharedPrefs.getString("username", null);
			try 
			{
				toServerMessage.put("ActionNum", "4");
				toServerMessage.put("Username", username);
				toServerMessage.put("Latitude", Double.toString(lat));
				toServerMessage.put("Longitude", Double.toString(lon));
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
				Toast.makeText(getActivity().getApplicationContext(), 
						"Location Update", Toast.LENGTH_LONG).show();
			}
			else if(result.equals("ATTEMPTCAPTURE"))
			{
				alertDialog = new AlertDialog.Builder(getActivity()).create();  
				alertDialog.setTitle("CAPTURE TARGET");  
				alertDialog.setMessage("00:10");
				alertDialog.show();   

				new CountDownTimer(5000, 1000) 
				{
					@Override
					public void onTick(long millisUntilFinished) 
					{
						alertDialog.setMessage("00:"+ (millisUntilFinished/1000));
					}

					@Override
					public void onFinish() 
					{
						attempt = new attemptCapture();
						attempt.execute();
						alertDialog.dismiss();
						//info.setVisibility(View.GONE);
					}
				}.start();
			}
			else if(result.equals("CAPTURETHREAT"))
			{
				Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				
				escape = new escapeCapture();
				escape.execute();
			}
			else 
			{
				Toast.makeText(getActivity().getApplicationContext(), 
						"Server Error", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class attemptCapture extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			username = sharedPrefs.getString("username", null);
			try 
			{
				toServerMessage.put("ActionNum", "5");
				toServerMessage.put("Username", username);
				toServerMessage.put("Latitude", Double.toString(lat));
				toServerMessage.put("Longitude", Double.toString(lon));
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

			if (result.equals("CAPTURED")) 
			{
				Toast.makeText(getActivity().getApplicationContext(), 
						"Target Captured!", Toast.LENGTH_LONG).show();
			}
			else 
			{
				Toast.makeText(getActivity().getApplicationContext(), 
						"Target Escaped!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class escapeCapture extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			username = sharedPrefs.getString("username", null);
			try 
			{
				toServerMessage.put("ActionNum", "6");
				toServerMessage.put("Username", username);
				toServerMessage.put("Latitude", Double.toString(lat));
				toServerMessage.put("Longitude", Double.toString(lon));
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

			if (result.equals("FAILED")) 
			{
				Toast.makeText(getActivity().getApplicationContext(), 
						"You escaped!", Toast.LENGTH_LONG).show();
			}
			else 
			{
				Toast.makeText(getActivity().getApplicationContext(), 
						"You were captured!", Toast.LENGTH_LONG).show();
			}
		}
	}
}
