package br.com.dafm.android.buzzzleeper.service;

import java.io.IOException;
import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.entity.BlrAddress;

public class TrackerService extends Service {

	private Context context;

	private LocationManager locationManager;

	private LocationListener locationListener;

	private MediaPlayer mediaPlayer;

	private BlrAddress blrAddress;

	private Boolean arrived;

	private View view;

	public TrackerService(Context context, BlrAddress blrAddress, View view) {
		super();
		this.context = context;
		this.blrAddress = blrAddress;
		this.view = view;
		mediaPlayer = new MediaPlayer();
		arrived = false;
	}

	public void startTracking() {

		getAlarmUri();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {

			TextView textView = (TextView) view.findViewById(R.id.txtDistance);

			@Override
			public void onProviderEnabled(String provider) {
				// TODO [diego] informar que o GPS foi ativado
			}

			@Override
			public void onProviderDisabled(String provider) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						context);

				// Setting Dialog Title
				alertDialog.setTitle("GPS is settings");

				// Setting Dialog Message
				alertDialog
						.setMessage("GPS is not enabled. Do you want to go to settings menu?");

				// On pressing Settings button
				alertDialog.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								context.startActivity(intent);
							}
						});

				// on pressing cancel button
				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				// Showing Alert Message
				alertDialog.show();
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				if (location == null)
					return;

				Double distance = getCurrentDistance(location);
				DecimalFormat df = new DecimalFormat("#.##");
				textView.setText(df.format(distance) + " Km");
				if (distance < (blrAddress.getBuffer().doubleValue() / 1000)) {
					if (!arrived) {
						startAlarm();
						openBtnStop();
					}
				}
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO [diego] metodo chamado a cada 1000mts
			}

		};

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String bestProvider = locationManager.getBestProvider(criteria, true);

		locationManager.requestLocationUpdates(bestProvider, 1000, 0,
				locationListener);
	}

	private void openBtnStop() {
		Button button = (Button) view.findViewById(R.id.btnStopAlarm);
		button.setVisibility(1);
	}

	public void stopTracking() {
		TextView textView = (TextView) view.findViewById(R.id.txtDistance);
		textView.setText("");
		locationManager.removeUpdates(locationListener);
		locationManager = null;
		stopAlarm();

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
		return distance / 1000;
	}

	public void startAlarm() {
		playSound(getAlarmUri());
	}
	
	public void stopAlarm() {
		mediaPlayer.stop();
		
	}

	private void playSound(Uri alert) {
		try {
			mediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.prepare();
				mediaPlayer.start();
				arrived = true;
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

		RingtoneManager ringtoneManager = new RingtoneManager(context);
		ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
		Cursor cursor = ringtoneManager.getCursor();

		while (!cursor.isAfterLast() && cursor.moveToNext()) {
			Ringtone ringtone = ringtoneManager.getRingtone(cursor
					.getPosition());

			if (ringtone.getTitle(context).equalsIgnoreCase(
					blrAddress.getRingtone())) {
				alarmUri = ringtoneManager.getRingtoneUri(cursor.getPosition());
				break;
			}
		}
		cursor.close();

		return alarmUri;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
