package connections.server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import 	android.database.sqlite.SQLiteOpenHelper;

public class Client implements Runnable 
{
	public JSONObject toServerMessage = new JSONObject();
	public JSONObject fromServerMessage = new JSONObject();
	public static SQLiteDatabase responseDatabase;
	public Context context;
	
	//public SQLiteOpenHelper helper = new SQLiteOpenHelper();
	public SharedPreferences sharedPrefs;
	
	public Client(JSONObject serverMessage)
	{
		this.toServerMessage = serverMessage;
	}

	public static final int SERVERPORT = 9999;

	private String serverIpAddress = "10.0.2.2";

	private boolean connected = false;

	private DataInputStream is = null;

	public volatile String serverResponse;

	public void connect()
	{
		//sharedPrefs = context.getSharedPreferences("com.example.Spy", Context.MODE_PRIVATE);
		if(!connected)
		{
			Log.d("ClientActivity", "Creating Thread");
			Thread cThread = new Thread(new Client(toServerMessage));
			cThread.start();
		}
	}
	public void run() 
	{
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
				//serverResponse = fromServerMessage.getString("Response");
				//sharedPrefs.edit().putString("createResponse", serverResponse).commit();
				setServerResponse(fromServerMessage);
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
	}

	public void setServerResponse(JSONObject object) throws JSONException
	{
		serverResponse = object.getString("Response");
		Log.d("setServerResponse", serverResponse);
	}
	
	public String getServerResponse()
	{
		Log.d("getServerResponse", serverResponse);
		return serverResponse;
	}
}

