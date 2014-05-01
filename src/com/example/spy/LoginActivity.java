package com.example.spy;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import connections.server.*;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity 
{
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	public JSONObject serverMessage;
	public Client client;
	public CheckBox checkBox;
	public SharedPreferences sharedPrefs = null;
	private String username;
	private String password;

	public JSONObject toServerMessage;
	public JSONObject fromServerMessage;
	public static final int SERVERPORT = 9999;
	private String serverIpAddress = "68.57.74.253";
	//private String serverIpAddress = "147.138.43.128";
	private String serverResponse;
	private String userName;
	@SuppressWarnings("unused")
	private boolean connected = false;
	private DataInputStream is = null;

	private UserLoginTask mAuthTask = null;
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		serverMessage = new JSONObject();
		checkBox = (CheckBox) findViewById(R.id.checkBox1);
		toServerMessage = new JSONObject();
		fromServerMessage = new JSONObject();

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) 
			{
				if (id == R.id.login || id == EditorInfo.IME_NULL) 
				{
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		if(checkBox.isChecked())
		{
			username = sharedPrefs.getString("username", null);
			password = sharedPrefs.getString("password", null);
			try 
			{
				serverMessage.put("Username", username);
				serverMessage.put("Password", password);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void register(View v)
	{
		startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() 
	{
		if (mAuthTask != null) 
		{
			return;
		}
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} 
		else if (!mEmail.contains("@")) {
			//				mEmailView.setError(getString(R.string.error_invalid_email));
			//				focusView = mEmailView;
			//				cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}


	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) 
		{
			userName = mEmailView.getText().toString();
			password = mPassword.toString();
			//retypePassword = (EditText) findViewById(R.id.EditText01);

			try 
			{
				toServerMessage.put("ActionNum", "0");
				toServerMessage.put("Username", userName);
				toServerMessage.put("Password", password);
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
		protected void onPostExecute(final String success) {
			mAuthTask = null;
			showProgress(false);

			if (success.equals("SUCCESS")) {
				startActivity(new Intent(getApplicationContext(), CreateJoinActivity.class));
			} 
			else 
			{
				mPasswordView
				.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() 
		{
			mAuthTask = null;
			showProgress(false);
		}
	}
}
