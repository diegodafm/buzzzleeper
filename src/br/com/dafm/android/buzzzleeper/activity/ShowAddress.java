package br.com.dafm.android.buzzzleeper.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
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

	private Typeface signikaSemibold;

	private BlrAddress blrAddress;

	private AddressDAO addressDAO;

	private GoogleMap googleMap;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new LoadViewTask().execute();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void setupFontTypeFace() {
		signikaSemibold = Typeface.createFromAsset(getAssets(),
				"fonts/Signika-Semibold.ttf");
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

			setupFontTypeFace();

			if (extras != null) {
				addressDAO = new AddressDAO(getApplicationContext());
				String value = extras.get("BLR_ADDRESS_ID").toString();
				blrAddress = addressDAO.findById(Integer.parseInt(value));

				TextView name = (TextView) findViewById(R.id.txtName);
				name.setText(blrAddress.getName());
				name.setTypeface(signikaSemibold);

				TextView address = (TextView) findViewById(R.id.txtAddress);
				address.setText(blrAddress.getAddress());
				address.setTypeface(signikaSemibold);

				TextView buffer = (TextView) findViewById(R.id.txtBuffer);
				buffer.setText(Integer.toString(blrAddress.getBuffer()) + " "
						+ getString(R.string.meters));
				buffer.setTypeface(signikaSemibold);

				TextView ringtone = (TextView) findViewById(R.id.txtRingtone);
				ringtone.setText(blrAddress.getRingtone());
				ringtone.setTypeface(signikaSemibold);

				TextView coordinates = (TextView) findViewById(R.id.txtCoordinates);
				coordinates.setText(String.format("%.7f", blrAddress.getLat())
						+ ", " + String.format("%.7f", blrAddress.getLng()));
				coordinates.setTypeface(signikaSemibold);

				LinearLayout btnRemove = (LinearLayout) findViewById(R.id.btnRemoveBlrAddress);
				((TextView) btnRemove.findViewById(R.id.txtRemove)).setTypeface(signikaSemibold);
				btnRemove.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
				
				LinearLayout btnEdit = (LinearLayout) findViewById(R.id.btnEditBlrAddress);
				((TextView) btnEdit.findViewById(R.id.txtEdit)).setTypeface(signikaSemibold);
				btnEdit.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
				
				
				setupMap();
			}
		}
	}

	private void setupMap() {
		googleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap)).getMap();
		addMarker(new LatLng(blrAddress.getLat(), blrAddress.getLng()));
	}

	private void addMarker(LatLng point) {
		googleMap.clear();
		CircleOptions circleOptions = new CircleOptions().center(point)
				.radius(blrAddress.getBuffer()).fillColor(0x40ff0000)
				.strokeColor(Color.BLUE).strokeWidth(5);
		googleMap.addCircle(circleOptions);
		googleMap.addMarker(new MarkerOptions().position(point));

		CameraUpdate center = CameraUpdateFactory.newLatLng(point);
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
		googleMap.moveCamera(center);
		googleMap.animateCamera(zoom);
	}

}
