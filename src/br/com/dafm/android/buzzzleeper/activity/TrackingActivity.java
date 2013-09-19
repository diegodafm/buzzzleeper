package br.com.dafm.android.buzzzleeper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.TrackerService;

public class TrackingActivity extends Activity {

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

			
			startTracking();
			setupStopAlarm();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
