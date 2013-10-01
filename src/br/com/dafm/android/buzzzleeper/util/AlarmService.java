package br.com.dafm.android.buzzzleeper.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PowerManager;
import android.widget.Toast;
import br.com.dafm.android.buzzzleeper.listener.MyLocationListener;

public class AlarmService extends BroadcastReceiver {
	
	LocationManager locationManager;
	
	LocationListener locationListener;
	
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Put here YOUR code.
		Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For

		wl.release();
	}

	public void setAlarm(Context context) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmService.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),1000 * 10, pi);
		
		
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener(context);  
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(bestProvider, 5000, 10, locationListener);
	}

	public void cancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmService.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		
		if(locationManager != null){
			locationManager.removeUpdates(locationListener);
			locationManager = null;
		}
	}
}
