package connections.server;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONObject;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Client implements Runnable 
{
	public JSONObject serverMessage = new JSONObject();

//	public Client() 
//	{
//	}
	
	public Client(JSONObject serverMessage)
	{
		this.serverMessage = serverMessage;
	}

	public static final int SERVERPORT = 9999;

	private String serverIpAddress = "192.168.1.116";

	private boolean connected = false;

	public void connect()
	{
		if(!connected)
		{
			Log.d("ClientActivity", "Creating Thread");
			Thread cThread = new Thread(new Client(serverMessage));
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
				out.println(serverMessage.toString());

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
}

