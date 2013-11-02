package br.com.dafm.android.buzzzleeper.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.dao.AddressDAO;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;
import br.com.dafm.android.buzzzleeper.service.TrackingService;
import br.com.dafm.android.buzzzleeper.views.PctgDistanceView;

public class TrackingActivity extends Activity {
	
	private Typeface signikaSemibold;

	private AddressDAO addressDAO;

	private BlrAddress blrAddress;

	private View pctgView;
	
	private Double greaterDistance;
	
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
			setupBtnBackHome();
			registerReceiver();
			
			if(statusAlarm == Boolean.FALSE){
				startTracking();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
        stopActivity();
    }
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("statusAlarm", statusAlarm);  
		if(greaterDistance != null){
			savedInstanceState.putDouble("greaterDistance", greaterDistance);
		}
  		super.onSaveInstanceState(savedInstanceState);  
	}  

	@Override  
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		  statusAlarm = savedInstanceState.getBoolean("statusAlarm");
		  greaterDistance = savedInstanceState.getDouble("greaterDistance");
	}
	
	private void startTracking(){
		statusAlarm = Boolean.TRUE;
		setupCirclePctg(0f,0d, getString(R.string.starting));
		
		Intent intent=new Intent(this, TrackingService.class);
		intent.putExtra("BLR_ADDRESS_ID", blrAddress.getId());
		intent.putExtra("BLR_ADDRESS_NAME", blrAddress.getName());
	    startService(intent);
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
		    
		    if(intent.getAction().equals("stopServiceBackToMainInfo")){
		    	stopTracking(true);
		    	onBackPressed();	    	
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

	private void displayDistance(Location location){
		Double distance = getCurrentDistance(location);
		if(greaterDistance == null ){
			greaterDistance = distance;
		}
		Double pctg = (100 - (100*distance/greaterDistance));
				
		if(arrived){
			setupCirclePctg(pctg.floatValue(),distance,getString(R.string.stop));
		}else{
			setupCirclePctg(pctg.floatValue(),distance, null);			
		}
				
		if (distance < (blrAddress.getBuffer().doubleValue())) {
			if (!arrived) {
				arrived = true;
				playSound();
				
				prepareIntent(getApplicationContext());
			}
		}
	}
	
	private static PendingIntent prepareIntent(Context context) {
	    Intent intent = new Intent(context, TabTrackingActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	
	
	public void stopActivity() {
		stopTracking();		
		this.finish();
	}
	
	public void stopTracking(Boolean stopAll) {
		stopTracking();
		Intent data = new Intent("stopTrackingMap");
		sendBroadcast(data);
	}

	public void stopTracking() {
		statusAlarm = Boolean.FALSE;
		stopService(new Intent(this, TrackingService.class));
		
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			mediaPlayer.stop();
		}
		
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
	
	
	
	private void setupCirclePctg(Float percent, Double distance, String textCirlce) {
		LinearLayout circle = (LinearLayout) findViewById(R.id.canvasPctgDistance);
		circle.removeAllViews();
		pctgView = new PctgDistanceView(getApplicationContext(), percent,distance,blrAddress,textCirlce);
		pctgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data;
				if(statusAlarm){
					data = new Intent("stopTrackingInfo");
					sendBroadcast(data);
					
					data = new Intent("stopTrackingMap");
					sendBroadcast(data);
				}else{
					data = new Intent("startTrackingInfo");
					sendBroadcast(data);
					
					data = new Intent("startTrackingMap");
					sendBroadcast(data);
				}				
			}
		});
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
