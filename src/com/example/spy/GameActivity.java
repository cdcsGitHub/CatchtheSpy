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

public class GameActivity extends Fragment implements LocationListener 
{
	private JSONObject serverMessage;
	private Client client;
	
	public SharedPreferences sharedPrefs = null;
	
	GoogleMap googleMap;
	MapView mapView;

	AlertDialog alertDialog;
	AlertDialog.Builder alertDialogBuilder;
	private LocationManager locationManager;
	private String provider;
	Criteria criteria;
	protected Location location;
	CameraPosition camera;
	CameraUpdate cameraUpdate;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{

		View rootView = inflater.inflate(R.layout.activity_game, container, false);
		//sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);

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
		googleMap.setMyLocationEnabled(true);
		locationManager.requestLocationUpdates(provider, 100, 1, this);
	}

	public void onPause()
	{
		super.onPause();
		locationManager.removeUpdates(this);
	}

	public void onLocationChanged(Location location)
	{
		
		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
		googleMap.animateCamera(cameraUpdate);
		locationManager.removeUpdates(this);
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
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
//				googleMap.addMarker(new MarkerOptions()
//				.position(new LatLng(location.getLatitude(), location.getLongitude()))
//				.title("Current Location"));

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
				.center(new LatLng(38.381284, -78.967803))
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
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	
//	public class createGame extends AsyncTask<Void, Void, String> 
//	{
//		@SuppressWarnings("deprecation")
//		@Override
//		protected String doInBackground(Void... params) 
//		{
//			username = sharedPrefs.getString("username", null);
//			try 
//			{
//				toServerMessage.put("ActionNum", "2");
//				toServerMessage.put("Username", username);
//				toServerMessage.put("Latitude", lat);
//				toServerMessage.put("Longitude", lon);
//			} 
//			catch (JSONException e) 
//			{
//				e.printStackTrace();
//			}
//			try {
//				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
//				Log.d("ClientActivity", "C: Connecting...");
//				Socket socket = new Socket(serverAddr,SERVERPORT);
//				connected = true;
//				try {
//					Log.d("ClientActivity", "C: Sending command.");
//					PrintWriter out = new PrintWriter(new BufferedWriter(
//							new OutputStreamWriter(socket.getOutputStream())),
//							true);
//					out.println(toServerMessage.toString());
//					is = new DataInputStream(socket.getInputStream());
//					String line = is.readLine();
//					fromServerMessage = new JSONObject(line);
//					serverResponse = fromServerMessage.getString("Response");
//					sharedPrefs.edit().putString("createResponse", serverResponse).commit();
//					serverResponse = fromServerMessage.getString("Response");
//					Log.d("Server", fromServerMessage.toString());
//					Log.d("ClientActivity", "C: Sent.");
//				} catch (Exception e) {
//					Log.e("ClientActivity", "S: Error", e);
//				}
//				socket.close();
//				Log.d("ClientActivity", "C: Closed.");
//				connected = false;
//			} catch (Exception e) {
//				Log.e("ClientActivity", "C: Error", e);
//				connected = false;
//			}
//			return serverResponse;
//		}
//
//		@Override
//		protected void onPostExecute(String result) 
//		{
//			
//			if (result.equals("SUCCESS")) 
//			{
//				startActivity(new Intent(getApplicationContext(), TabActivity.class));
//			}
//			else 
//			{
//				Toast.makeText(getApplicationContext(), 
//						"Error, could not create game", Toast.LENGTH_LONG).show();
//			}
//		}
//	}
//	
//	public class joinGame extends AsyncTask<Void, Void, String> 
//	{
//		@SuppressWarnings("deprecation")
//		@Override
//		protected String doInBackground(Void... params) 
//		{
//			username = sharedPrefs.getString("username", null);
//			try 
//			{
//				toServerMessage.put("ActionNum", "3");
//				toServerMessage.put("Username", username);
//				toServerMessage.put("Latitude", lat);
//				toServerMessage.put("Longitude", lon);
//			} 
//			catch (JSONException e) 
//			{
//				e.printStackTrace();
//			}
//			try {
//				InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
//				Log.d("ClientActivity", "C: Connecting...");
//				Socket socket = new Socket(serverAddr,SERVERPORT);
//				connected = true;
//				try {
//					Log.d("ClientActivity", "C: Sending command.");
//					PrintWriter out = new PrintWriter(new BufferedWriter(
//							new OutputStreamWriter(socket.getOutputStream())),
//							true);
//					out.println(toServerMessage.toString());
//					is = new DataInputStream(socket.getInputStream());
//					String line = is.readLine();
//					fromServerMessage = new JSONObject(line);
//					serverResponse = fromServerMessage.getString("Response");
//					sharedPrefs.edit().putString("joinResponse", serverResponse).commit();
//					serverResponse = fromServerMessage.getString("Response");
//					Log.d("Server", fromServerMessage.toString());
//					Log.d("ClientActivity", "C: Sent.");
//				} catch (Exception e) {
//					Log.e("ClientActivity", "S: Error", e);
//				}
//				socket.close();
//				Log.d("ClientActivity", "C: Closed.");
//				connected = false;
//			} catch (Exception e) {
//				Log.e("ClientActivity", "C: Error", e);
//				connected = false;
//			}
//			return serverResponse;
//		}
//
//		@Override
//		protected void onPostExecute(String result) 
//		{
//			
//			if (result.equals("SUCCESS")) 
//			{
//				startActivity(new Intent(getApplicationContext(), JoinGameActivity.class));
//			}
//			else 
//			{
//				Toast.makeText(getApplicationContext(), 
//						"Error, could not find games", Toast.LENGTH_LONG).show();
//			}
//		}
//	}

}
