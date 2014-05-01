package com.example.spy;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.spy.CreateJoinActivity.createGame;

import connections.server.Client;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.net.Uri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

/**
 * This example shows how to create and handle image picker in Android.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class RegisterActivity extends Activity 
{
	public JSONObject toServerMessage;
	public JSONObject fromServerMessage;
	public static final int SERVERPORT = 9999;
	private String serverIpAddress = "68.57.74.253";
	//private String serverIpAddress = "147.138.43.128";
	private String serverResponse;
	private String name;
	private String pass;
	@SuppressWarnings("unused")
	private boolean connected = false;
	private DataInputStream is = null;
	
	public SharedPreferences sharedPrefs = null;
	private Uri mImageCaptureUri;
	private ImageView mImageView;	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private JSONObject serverMessage = new JSONObject();
	private Client client;
	private EditText userName;
	private EditText password;
	private EditText retypePassword;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		toServerMessage = new JSONObject();
		fromServerMessage = new JSONObject();
		final String [] items			= new String [] {"From Camera", "From Device"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() 
		{
			public void onClick( DialogInterface dialog, int item ) 
			{
				if (item == 0) 
				{
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File file		 = new File(Environment.getExternalStorageDirectory(),
							"tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
					mImageCaptureUri = Uri.fromFile(file);

					try 
					{			
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}			

					dialog.cancel();
				} 
				else 
				{
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );

		final AlertDialog dialog = builder.create();

		mImageView = (ImageView) findViewById(R.id.imageView1);

		((Button) findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				dialog.show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (resultCode != RESULT_OK) return;

		Bitmap bitmap 	= null;
		String path		= "";

		if (requestCode == PICK_FROM_FILE) 
		{
			mImageCaptureUri = data.getData(); 
			path = getRealPathFromURI(mImageCaptureUri); //from Gallery 

			if (path == null)
				path = mImageCaptureUri.getPath(); //from File Manager

			if (path != null) 
				bitmap 	= BitmapFactory.decodeFile(path);
		}
		else 
		{
			path	= mImageCaptureUri.getPath();
			bitmap  = BitmapFactory.decodeFile(path);
		}

		mImageView.setImageBitmap(bitmap);		
	}

	public String getRealPathFromURI(Uri contentUri) 
	{
		String [] proj 		= {MediaStore.Images.Media.DATA};
		Cursor cursor 		= managedQuery( contentUri, proj, null, null,null);

		if (cursor == null) return null;

		int column_index 	= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		cursor.moveToFirst();

		return cursor.getString(column_index);
	}
	
	public void registerUser(View v)
	{
		registerUser newUser = new registerUser();
		newUser.execute();
	}
	
	public class registerUser extends AsyncTask<Void, Void, String> 
	{
		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Void... params) 
		{
			userName = (EditText) findViewById(R.id.editText1);
			password = (EditText) findViewById(R.id.editText2);
			retypePassword = (EditText) findViewById(R.id.EditText01);
			
			name = userName.getText().toString();
			pass = password.getText().toString();
			
			try 
			{
				toServerMessage.put("ActionNum", "1");
				toServerMessage.put("Username", name);
				toServerMessage.put("Password", pass);
			
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
			
			if (result.equals("REGISTERED")) 
			{
				sharedPrefs.edit().putString("username", name).commit();
				sharedPrefs.edit().putString("password", pass).commit();
				startActivity(new Intent(getApplicationContext(), CreateJoinActivity.class));
			}
			else 
			{
				Toast.makeText(getApplicationContext(), 
						"Registration Error", Toast.LENGTH_LONG).show();
			}
		}
	}
}
