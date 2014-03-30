package com.example.spy;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class GameActivity extends Fragment 
{
	private JSONObject serverMessage;
	GoogleMap googleMap;
	MapView mapView;
	AlertDialog alertDialog;
	AlertDialog.Builder alertDialogBuilder;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.activity_game, container, false);

		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(35.746512, -39.462891))      // Sets the center of the map to Mountain View
		.zoom(0)                   // Sets the zoom
		.build();                   // Creates a CameraPosition from the builder
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		CountDownTimer waitTimer = new CountDownTimer(5000, 1000) 
		{

			public void onTick(long millisUntilFinished) 
			{

			}

			public void onFinish() 
			{
				googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(38.378756, -78.971553))
				.title("Hello world"));

				googleMap.setBuildingsEnabled(true);

				Circle circle = googleMap.addCircle(new CircleOptions()
				.center(new LatLng(38.378756, -78.971553))
				.radius(1609)
				.strokeColor(Color.BLUE)
				.fillColor(Color.TRANSPARENT)); 

				CameraPosition cameraPosition = CameraPosition.builder()
						.target(new LatLng(38.378756, -78.971553))     
						.zoom(14)                   
						//.bearing(90)                
						//.tilt(30)                  
						.build();      
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

		return rootView;
	}

	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
}
