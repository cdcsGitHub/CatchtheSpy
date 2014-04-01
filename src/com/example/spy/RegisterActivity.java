package com.example.spy;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import connections.server.Client;
import database.handlers.DatabaseHandler;
import database.handlers.User;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.provider.MediaStore;
import android.net.Uri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;

/**
 * This example shows how to create and handle image picker in Android.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class RegisterActivity extends Activity 
{
	private Uri mImageCaptureUri;
	private ImageView mImageView;	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private JSONObject serverMessage = new JSONObject();
	private Client client;
	private EditText userName;
	private EditText password;
	private EditText retypePassword;
	private DatabaseHandler db = new DatabaseHandler(this);

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);

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
		userName = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		retypePassword = (EditText) findViewById(R.id.EditText01);
		
		String name = userName.getText().toString();
		String pass = password.getText().toString();
		
		try 
		{
			serverMessage.put("ActionNum", 0);
			serverMessage.put("Username", name);
			serverMessage.put("Password", pass);
		
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		client = new Client(serverMessage);
		client.connect();
		db.addUser(new User(name, pass));
		startActivity(new Intent(getApplicationContext(), CreateJoinActivity.class));
	}
}
