package com.shea.location_aware;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener 
{
	/*
	 * Check for Google Play Services
	 * Check that Google Play services is installed before you attempt to connect to Location Services. To check if it is installed, 
	 * call GooglePlayServicesUtil.isGooglePlayServicesAvailable(), which returns one of the integer result codes listed in the reference documentation for ConenctionResult.
	 * If an error occurs, call GooglePlayServicesUtil.getErrorDialog() to retrieve localized dialog that prompts users to take the correct action, then display the dialog in a
	 * DialogFragment.   
	 */
	
	//Define a request code to send to Google Play services
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	LocationClient mLocationClient;
	Location mCurrentLcoation;
	
	/*
	 * To get the current location, call getLastLocation()
	 * mCurrentLocation = mLocationClient.getLastLocation();
	 */
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationClient = new LocationClient (this, this, this);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop()
	{
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// A DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment
	{
		// Global field to contain the error dialog
		private Dialog mDialog;
		public ErrorDialogFragment ()
		{
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog (Dialog dialog)
		{
			mDialog = dialog;
		}
		// Return a dialog to the DialogFragment
		@Override
		public Dialog onCreateDialog (Bundle savedInstanceState)
		{
			return mDialog;
		}
	}

	/*
	 * The dialog created above can be used to the correct the problem with the APK. We'll used an overridden onAvtivityResult() in order to
	 * handle the result. 
	 */
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		switch (requestCode) 
		{
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
				switch (resultCode)
				{
					case Activity.RESULT_OK:
						/**
						 * Try the request again
						 */
					break;
				}
		}
	}
	
	private boolean servicesConnected()
	{
		// Hold the result of whether there is Google Play services available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		//If the result of Google Play services matches the success of ConnectionResult
		if (ConnectionResult.SUCCESS == resultCode)
		{
			Log.d ("Location Updates","Google Play services is available.");
			return true;
		}
		// For some reason, Google Play services just isn't there or compatible
		else
		{
			// Get the error code
			int errorCode = ConnectionResult.B.getErrorCode();
			// Get the error dialog, using the error code from ConnectionResult, from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			// If an error dialog is provided by Google Play services
			if (errorDialog != null)
			{
				// Using the class ErrorDialogFragment written above we will create a new dialog using the errorDialog from Google Play services, then show it.
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				errorFragment.show(getFragmentManager(), "Location Updates");
			}
		}
		return false;
	}
	
	/*
	 * we need to create a locationc lient, connect it to Location Services, thenc all getLastLocation()
	 * In order for Location Services to communicate with your app, two interfaces need to be implemented
	 * 		ConnectionCallbacks: Specifies methods that Lcation services calls when a lcoation client is (dis)connected
	 * 		OnConnectionFailedListener: Uses showErrorDialog in order to generate an error message
	 */
	
	@Override
	public void onConnectionFailed(ConnectionResult result) 
	{
		if (ConnectionResult.B.hasResolution())
		{
			try
			{
				ConnectionResult.B.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			}
			catch(IntentSender.SendIntentException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			//showErrorDialog(ConnectionResult.B.getErrorCode());
			showDialog(ConnectionResult.B.getErrorCode());
		}
	}

	@Override
	public void onConnected(Bundle dataBundle)
	{
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() 
	{
		Toast.makeText(this, "Disconnected. Please re-connect", Toast.LENGTH_SHORT).show();
	}
}
