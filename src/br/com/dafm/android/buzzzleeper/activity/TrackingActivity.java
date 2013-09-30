package br.com.dafm.android.buzzzleeper.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.TrackerService;

public class TrackingActivity extends Activity {
	
	private Typeface signikaSemibold;

	private AddressDAO addressDAO;

	private BlrAddress blrAddress;

	private TrackerService trackerService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracking);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			addressDAO = new AddressDAO(getApplicationContext());
			String value = extras.get("BLR_ADDRESS_ID").toString();
			blrAddress = addressDAO.findById(Integer.parseInt(value));
			
			displayData();
			startTracking();
			setupStopAlarm();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            trackerService.stopTracking();
            
            this.finish();
    }
	
	private void displayData(){

		signikaSemibold = Typeface.createFromAsset(getAssets(),
				"fonts/Signika-Semibold.ttf");

		TextView name = (TextView) findViewById(R.id.txtTitle);
		name.setText(blrAddress.getName());
		name.setTypeface(signikaSemibold);

		TextView address = (TextView) findViewById(R.id.trackingTxtAddress);
		address.setText(blrAddress.getAddress());
		address.setTypeface(signikaSemibold);

		TextView buffer = (TextView) findViewById(R.id.trackingTxtBuffer);
		buffer.setText(Integer.toString(blrAddress.getBuffer()) + " "
				+ getString(R.string.meters));
		buffer.setTypeface(signikaSemibold);

		TextView ringtone = (TextView) findViewById(R.id.trackingTxtRingtone);
		ringtone.setText(blrAddress.getRingtone());
		ringtone.setTypeface(signikaSemibold);

		TextView coordinates = (TextView) findViewById(R.id.trackingTxtCoordinates);
		coordinates.setText(String.format("%.7f", blrAddress.getLat())
				+ ", " + String.format("%.7f", blrAddress.getLng()));
		coordinates.setTypeface(signikaSemibold);
		
	}

	private void startTracking() {
		trackerService = new TrackerService(getApplicationContext(),
				blrAddress, (View) findViewById(R.id.llTracking));
		trackerService.startTracking();
	}

	private void setupStopAlarm() {
		Button button = (Button) findViewById(R.id.btnStopAlarm);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trackerService.stopTracking();
			}
		});
	}

	

}
