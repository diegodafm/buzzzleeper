package br.com.dafm.android.buzzzleeper.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import br.com.dafm.android.buzzzleeper.R;
import br.com.dafm.android.buzzzleeper.activity.TabTrackingActivity;
import br.com.dafm.android.buzzzleeper.receiver.AlarmReceiver;

public class TrackingService extends Service {
	
	public AlarmReceiver alarmReceiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String blrAddressId = intent.getStringExtra("BLR_ADDRESS_ID");
		
		String blrAddressName = intent.getStringExtra("BLR_ADDRESS_NAME");
		
		play(blrAddressName,blrAddressId);
		
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(new IntentFilter("trackingInfo"));
		filters.add(new IntentFilter("trackingMap"));
		
		alarmReceiver = new AlarmReceiver();
		alarmReceiver.setAlarm(getApplicationContext(),filters);

		return (START_NOT_STICKY);
	}

	@Override
	public void onDestroy() {
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (null);
	}

	private void play(String blrAddressName, String blrAddressId) {

		Notification note = new Notification(R.drawable.ic_zzz_notification,"Started to tracking", System.currentTimeMillis());
		Intent intent = new Intent(this, TabTrackingActivity .class);
		intent.putExtra("BLR_ADDRESS_ID", blrAddressId);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
		note.setLatestEventInfo(this, "Buzzzleeper", blrAddressName, pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(8888, note);
	}

	private void stop() {
		alarmReceiver.cancelAlarm(getApplicationContext());
		stopForeground(true);
	}
}