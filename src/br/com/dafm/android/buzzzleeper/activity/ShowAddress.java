package br.com.dafm.android.buzzzleeper.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowAddress extends FragmentActivity {

	private BlrAddress blrAddress;

	private AddressDAO addressDAO;
	
	private GoogleMap googleMap;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new LoadViewTask().execute();

	}

	// To use the AsyncTask, it must be subclassed
	private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
		// Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ShowAddress.this,
					"Loading...", "Loading application View, please wait...",
					false, false);
			progressDialog.show();
		}

		// The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			/*
			 * This is just a code that delays the thread execution 4 times,
			 * during 850 milliseconds and updates the current progress. This is
			 * where the code that is going to be executed on a background
			 * thread must be placed.
			 */
			try {
				// Get the current thread's token
				synchronized (this) {
					// Initialize an integer (that will act as a counter) to
					// zero
					int counter = 0;
					// While the counter is smaller than four
					while (counter <= 4) {
						// Wait 850 milliseconds
						this.wait(850);
						// Increment the counter
						counter++;
						// Set the current progress.
						// This value is going to be passed to the
						// onProgressUpdate() method.
						publishProgress(counter * 25);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		// Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
			// set the current progress of the progress dialog
			progressDialog.setProgress(values[0]);
		}

		// after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			// close the progress dialog
			progressDialog.dismiss();
			// initialize the View
			setContentView(R.layout.show_address);

			Bundle extras = getIntent().getExtras();

			if (extras != null) {
				addressDAO = new AddressDAO(getApplicationContext());
				String value = extras.get("BLR_ADDRESS_ID").toString();
				blrAddress = addressDAO.findById(Integer.parseInt(value));
				
				TextView name = (TextView) findViewById(R.id.txtBlrAddressName);
				name.setText(blrAddress.getName());
				
				TextView address = (TextView) findViewById(R.id.txtBlrAddress);
				address.setText(blrAddress.getAddress());
				
				TextView buffer = (TextView) findViewById(R.id.txtBlrAddressBuffer);
				//buffer.setText(getString(R.string.bufferDistance) + ": "+ Integer.toString(blrAddress.getBuffer()) + " "+ getString(R.string.meters));
				
				TextView ringtone = (TextView) findViewById(R.id.txtBlrAddressRingtone);
				ringtone.setText(blrAddress.getRingtone());
				
				setupMap();
			}
		}
	}
	
	private void setupMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();
		addMarker(new LatLng(blrAddress.getLat(), blrAddress.getLng()));
	}

	private void addMarker(LatLng point) {
		googleMap.clear();
		CircleOptions circleOptions = new CircleOptions().center(point).radius(blrAddress.getBuffer()).fillColor(0x40ff0000).strokeColor(Color.BLUE).strokeWidth(5);
		googleMap.addCircle(circleOptions);
		googleMap.addMarker(new MarkerOptions().position(point));
		
		CameraUpdate center = CameraUpdateFactory.newLatLng(point);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);

		LinearLayout rlInfoMap = (LinearLayout) this.findViewById(R.id.rlInfoMap);
		rlInfoMap.setVisibility(0);

		TextView coordinates = (TextView) this.findViewById(R.id.txtCoordinates);
		coordinates.setText("Latitude: "+ String.format("%.7f", point.latitude) + ", Longitude: "+ String.format("%.7f", point.longitude));

		TextView addressLocation = (TextView) this.findViewById(R.id.txtAddressLocation);
		addressLocation.setText(blrAddress.getAddress());
		
		
	}

}
