package br.com.dafm.android.buzzzleeper.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.util.AlarmService;
import br.com.dafm.android.buzzzleeper.views.PctgDistanceView;

public class TrackingActivity extends Activity {
	
	private Typeface signikaSemibold;

	private AddressDAO addressDAO;

	private BlrAddress blrAddress;

	private AlarmService alarm;
	
	private View pctgView;
	
	private Double firstDistanceDetected;
	
	private MediaPlayer mediaPlayer;

	private Boolean arrived = Boolean.FALSE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracking);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			addressDAO = new AddressDAO(getApplicationContext());
			String value = extras.get("BLR_ADDRESS_ID").toString();
			blrAddress = addressDAO.findById(Integer.parseInt(value));
			mediaPlayer = new MediaPlayer();			
			displayData();
			setupStopAlarm();
			setupBtnBackHome();
			setupCirclePctg(0f,0d);
			startTracking();			
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
        if(mediaPlayer.isPlaying()){
        	mediaPlayer.stop();
        }
        alarm.cancelAlarm(getApplicationContext());
        this.finish();
    }
	
	private void startTracking(){
		alarm = new AlarmService();
		IntentFilter tracking = new IntentFilter("trackingInfo");
		IntentFilter stopTracking = new IntentFilter("stopTrackingInfo");
		alarm.setAlarm(getApplicationContext(),tracking);
		BroadcastReceiver receiver = new BroadcastReceiver() {
		  public void onReceive(Context context, Intent intent) {
			  
		    if(intent.getAction().equals("trackingInfo")) {
		    	Location location = (Location) intent.getExtras().get("location");
		    	displayDistance(location);
		    }
		    
		    if(intent.getAction().equals("stopTrackingInfo")) {
		    	stopTracking();
		    }
		  }
		};
		registerReceiver(receiver, tracking);
		registerReceiver(receiver, stopTracking);
	}
	
	private void setupBtnBackHome() {
		RelativeLayout btnAddAddress = (RelativeLayout) findViewById(R.id.btnArrowBack);
		btnAddAddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
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

	private void setupStopAlarm() {
		Button button = (Button) findViewById(R.id.btnStopAlarm);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alarm.cancelAlarm(getApplicationContext());
				mediaPlayer.stop();
			}
		});
	}
	
	private void displayDistance(Location location){
		Double distance = getCurrentDistance(location);
		if(firstDistanceDetected == null || firstDistanceDetected < distance){
			firstDistanceDetected = distance;
		}
		Double pctg = (100*distance/firstDistanceDetected);
		setupCirclePctg(100-pctg.floatValue(),distance);
		if (distance < (blrAddress.getBuffer().doubleValue())) {
			if (!arrived) {
				arrived = true;
				playSound(getAlarmUri());
				openBtnStop();
			}
		}
	}

	private void openBtnStop() {
		Button button = (Button) findViewById(R.id.btnStopAlarm);
		button.setVisibility(1);
	}

	public void stopTracking() {
		if(mediaPlayer.isPlaying()){
        	mediaPlayer.stop();
        }
        alarm.cancelAlarm(getApplicationContext());
        this.finish();
	}
	
	public void stopTracking(Boolean stopAll) {
		stopTracking();
		alarm.cancelAlarm(getApplicationContext());
		Intent data = new Intent("stopTrackingMap");
		sendBroadcast(data);
	}

	public Double getCurrentDistance(Location location) {
		Double distance;

		Location locationA = new Location("Posicao Atual");

		locationA.setLatitude(location.getLatitude());
		locationA.setLongitude(location.getLongitude());

		Location locationB = new Location(blrAddress.getName());

		locationB.setLatitude(blrAddress.getLat());
		locationB.setLongitude(blrAddress.getLng());

		distance = (double) locationA.distanceTo(locationB);
		return distance;
	}
	
	private void setupCirclePctg(Float percent, Double distance) {
		LinearLayout circle = (LinearLayout) findViewById(R.id.canvasPctgDistance);
		circle.removeAllViews();
		pctgView = new PctgDistanceView(getApplicationContext(), percent,distance,blrAddress);
		circle.addView(pctgView);
	}

	private void playSound(Uri alert) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(getApplicationContext(), alert);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (IOException e) {
			new RuntimeException(e);
			System.out.println(e);
		}
	}

	// Get an alarm sound. Try for an alarm. If none set, try notification,
	// Otherwise, ringtone.
	private Uri getAlarmUri() {
		Uri alarmUri = null;

		RingtoneManager ringtoneManager = new RingtoneManager(getApplicationContext());
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		Cursor cursor = ringtoneManager.getCursor();

		while (!cursor.isAfterLast() && cursor.moveToNext()) {
			Ringtone ringtone = ringtoneManager.getRingtone(cursor
					.getPosition());

			if (ringtone.getTitle(getApplicationContext()).equalsIgnoreCase(
					blrAddress.getRingtone())) {
				alarmUri = ringtoneManager.getRingtoneUri(cursor.getPosition());
				break;
			}
		}
		cursor.close();

		return alarmUri;
	}

	

}
