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
	
	private Boolean statusAlarm = Boolean.FALSE;
	
	private BroadcastReceiver receiver;

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
			registerReceiver();
			startTracking();			
		}
	}

	@Override
    public void onBackPressed() {
        super.onBackPressed();
        stopActivity();
    }
	
	private void startTracking(){
		statusAlarm = Boolean.TRUE;
		setupCirclePctg(0f,0d, getString(R.string.starting));
		IntentFilter tracking = new IntentFilter("trackingInfo");
		alarm = new AlarmService();
		alarm.setAlarm(getApplicationContext(),tracking);
	}
	
	private void registerReceiver(){
		receiver = new BroadcastReceiver() {
		  public void onReceive(Context context, Intent intent) {
			  
		    if(intent.getAction().equals("trackingInfo")) {
		    	Location location = (Location) intent.getExtras().get("location");
		    	displayDistance(location);
		    }
		    
		    if(intent.getAction().equals("stopTrackingInfo")) {
		    	stopTracking();
		    }
		    
		    if(intent.getAction().equals("stopActivityInfo")) {
		    	stopActivity();
		    }
		    
		    if(intent.getAction().equals("startTrackingInfo")) {
		    	startTracking();
		    }
		  }
		};

		registerReceiver(receiver, new IntentFilter("trackingInfo"));
		registerReceiver(receiver, new IntentFilter("stopTrackingInfo"));
		registerReceiver(receiver, new IntentFilter("startTrackingInfo"));
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

		signikaSemibold = Typeface.createFromAsset(getAssets(),"fonts/Signika-Semibold.ttf");

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
		coordinates.setText(String.format("%.7f", blrAddress.getLat()) + ", " + String.format("%.7f", blrAddress.getLng()));
		coordinates.setTypeface(signikaSemibold);
		
	}

	private void setupStopAlarm() {
		Button button = (Button) findViewById(R.id.btnStopAlarm);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopTracking();
			}
		});
	}
	
	private void displayDistance(Location location){
		Double distance = getCurrentDistance(location);
		if(firstDistanceDetected == null || firstDistanceDetected < distance){
			firstDistanceDetected = distance;
		}
		Double pctg = (100 - (100*distance/firstDistanceDetected));
				
		if(arrived){
			setupCirclePctg(pctg.floatValue(),distance,getString(R.string.stop));
		}else{
			setupCirclePctg(pctg.floatValue(),distance);			
		}
				
		if (distance < (blrAddress.getBuffer().doubleValue())) {
			if (!arrived) {
				arrived = true;
				playSound();
			}
		}
	}
	
	
	public void stopActivity() {
		stopTracking();		
		this.finish();
	}
	
	public void stopActivity(Boolean stopAll) {
		stopTracking();
		alarm.cancelAlarm(getApplicationContext());
		Intent data = new Intent("stopTrackingMap");
		sendBroadcast(data);
	}

	public void stopTracking() {
		statusAlarm = Boolean.FALSE;
		
		if(mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
		alarm.cancelAlarm(getApplicationContext());
		
		setupCirclePctg(0f,0d, getString(R.string.start));
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
		pctgView = new PctgDistanceView(getApplicationContext(), percent,distance,statusAlarm,blrAddress);
		circle.addView(pctgView);
	}
	
	private void setupCirclePctg(Float percent, Double distance, String textCirlce) {
		LinearLayout circle = (LinearLayout) findViewById(R.id.canvasPctgDistance);
		circle.removeAllViews();
		pctgView = new PctgDistanceView(getApplicationContext(), percent,distance,statusAlarm,blrAddress,textCirlce);
		circle.addView(pctgView);
	}

	private void playSound() {
		Uri alert = getAlarmUri();
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
		if(alarmUri == null){
			alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}
		return alarmUri;
	}
}
